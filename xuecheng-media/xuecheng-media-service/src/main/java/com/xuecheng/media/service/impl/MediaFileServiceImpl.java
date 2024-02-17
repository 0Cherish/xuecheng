package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.exception.SystemException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDTO;
import com.xuecheng.media.model.dto.UploadFileParamsDTO;
import com.xuecheng.media.model.dto.UploadFileResultDTO;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Lin
 * @date 2024/2/16 16:41
 */
@Slf4j
@Service
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    private MediaFilesMapper mediaFilesMapper;

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MediaFileService currentProxy;

    /**
     * 存储普通文件
     */
    @Value("${minio.bucket.files}")
    private String bucketMediafiles;

    /**
     * 存储视频
     */
    @Value("${minio.bucket.videofiles}")
    private String bucketVideo;

    @Override
    public PageResult<MediaFiles> queryMediaFiles(Long companyId, PageParams pageParams, QueryMediaParamsDTO queryMediaParamsDTO) {
        // 拼装查询条件
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(queryMediaParamsDTO.getFilename()), MediaFiles::getFilename, queryMediaParamsDTO.getFilename());
        queryWrapper.eq(StringUtils.isNotEmpty(queryMediaParamsDTO.getFileType()), MediaFiles::getFileType, queryMediaParamsDTO.getFileType());
        queryWrapper.eq(StringUtils.isNotEmpty(queryMediaParamsDTO.getAuditStatus()), MediaFiles::getAuditStatus, queryMediaParamsDTO.getAuditStatus());

        // 构建分页参数
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());

        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        List<MediaFiles> list = pageResult.getRecords();
        long total = pageResult.getTotal();

        return new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
    }

    @Override
    public UploadFileResultDTO uploadFile(Long companyId, UploadFileParamsDTO uploadFileParamsDTO, String localFilePath) {
        // 获取媒体类型
        String filename = uploadFileParamsDTO.getFilename();
        String extension = filename.substring(filename.lastIndexOf("."));
        String mimeType = getMimeType(extension);
        // 获取对象名
        String defaultFolderPath = getDefaultFolderPath();
        String fileMd5 = getFileMd5(new File(localFilePath));
        String objectName = defaultFolderPath + fileMd5 + extension;
        // 将文件上传到minio
        boolean result = addMediaFilesToMinio(localFilePath, mimeType, bucketMediafiles, objectName);
        if (!result) {
            SystemException.cast("上传文件失败");
        }

        // 将文件信息保存到数据库
        MediaFiles mediaFiles = currentProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDTO, bucketMediafiles, objectName);
        UploadFileResultDTO uploadFileResultDTO = new UploadFileResultDTO();
        BeanUtils.copyProperties(mediaFiles, uploadFileResultDTO);

        return uploadFileResultDTO;
    }

    @Override
    @Transactional
    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDTO uploadFileParamsDTO, String bucket, String objectName) {
        //从数据库查询文件
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            //拷贝基本信息
            BeanUtils.copyProperties(uploadFileParamsDTO, mediaFiles);
            mediaFiles.setId(fileMd5);
            mediaFiles.setFileId(fileMd5);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setUrl("/" + bucket + "/" + objectName);
            mediaFiles.setBucket(bucket);
            mediaFiles.setFilePath(objectName);
            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setAuditStatus("002003");
            mediaFiles.setStatus("1");
            //保存文件信息到文件表
            int insert = mediaFilesMapper.insert(mediaFiles);
            if (insert <= 0) {
                log.error("保存文件信息到数据库失败,{}", mediaFiles);
                SystemException.cast("保存文件信息失败");
            }
            log.debug("保存文件信息到数据库成功,{}", mediaFiles);

        }
        return mediaFiles;
    }

    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        // 查询文件信息
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles != null) {
            String bucket = mediaFiles.getBucket();
            String filePath = mediaFiles.getFilePath();

            InputStream stream;
            try {
                stream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucket)
                                .object(filePath)
                                .build());

                if (stream != null) {
                    // 文件已存在
                    return RestResponse.success(true);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        // 文件不存在
        return RestResponse.success(false);
    }

    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {
        // 得到分块文件目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        // 得到分块文件路径
        String chunkFilePath = chunkFileFolderPath + chunkIndex;

        InputStream fileInputStream;
        try {
            fileInputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketVideo)
                            .object(chunkFilePath)
                            .build());
            if (fileInputStream != null) {
                // 分块存在
                return RestResponse.success(true);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        // 分块不存在
        return RestResponse.success(false);
    }

    @Override
    public RestResponse<Boolean> uploadChunk(String fileMd5, int chunkIndex, String localChunkFilePath) {
        // 得到分块文件目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        // 得到分块文件路径
        String chunkFilePath = chunkFileFolderPath + chunkIndex;

        String mimeType = getMimeType(null);
        boolean result = addMediaFilesToMinio(localChunkFilePath, mimeType, bucketVideo, chunkFilePath);
        if (!result) {
            return RestResponse.success(false, "上传分块失败");
        }
        return RestResponse.success(true, "上传分块成功");
    }

    @Override
    public RestResponse<Boolean> mergeChunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDTO uploadFileParamsDTO) {
        // 获取分块路径
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        List<ComposeSource> sourceObjectList = Stream.iterate(0, i -> ++i)
                .limit(chunkTotal)
                .map(i -> ComposeSource.builder()
                        .bucket(bucketVideo)
                        .object(chunkFileFolderPath.concat(Integer.toString(i)))
                        .build())
                .collect(Collectors.toList());

        // 合并
        String filename = uploadFileParamsDTO.getFilename();
        String extension = filename.substring(filename.lastIndexOf("."));
        String mergeFilPath = getFilePathByMd5(fileMd5, extension);
        try {
            // 合并文件
            ObjectWriteResponse response = minioClient.composeObject(
                    ComposeObjectArgs.builder()
                            .bucket(bucketVideo)
                            .object(mergeFilPath)
                            .sources(sourceObjectList)
                            .build());
            log.debug("文件合并成功：{}", mergeFilPath);
        } catch (Exception e) {
            log.debug("合并文件失败，fileMd5：{}，异常：{}", fileMd5, e.getMessage(), e);
            return RestResponse.fail(false, "合并文件失败");
        }

        // 验证md5
        File minioFile = downloadFileFromMinio(bucketVideo, mergeFilPath);
        if (minioFile == null) {
            log.debug("下载合并后文件失败，mergeFilePath：{}", mergeFilPath);
            return RestResponse.fail(false, "下载合并后文件失败");
        }

        try (FileInputStream newFileInputStream = new FileInputStream(minioFile)) {
            String md5 = DigestUtils.md5DigestAsHex(newFileInputStream);
            if (!fileMd5.equals(md5)) {
                return RestResponse.fail(false, "文件合并校验失败，最终上传失败");
            }
            // 文件大小
            uploadFileParamsDTO.setFileSize(minioFile.length());
        } catch (Exception e) {
            log.debug("校验文件失败，fileMd5：{}，异常：{}", fileMd5, e.getMessage(), e);
            return RestResponse.fail(false, "文件合并校验失败，最终上传失败");
        } finally {
            minioFile.delete();
        }

        // 将文件信息保存到数据库
        MediaFiles mediaFiles = currentProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDTO, bucketVideo, mergeFilPath);
        if (mediaFiles == null) {
            return RestResponse.fail(false, "文件入库失败");
        }

        // 清除分块文件
        clearChunkFiles(chunkFileFolderPath, chunkTotal);
        return RestResponse.success(true);
    }

    /**
     * 从minio下载文件
     *
     * @param bucket     桶
     * @param objectName 对象名称
     * @return 下载后的文件
     */
    public File downloadFileFromMinio(String bucket, String objectName) {
        File minioFile = null;
        FileOutputStream outputStream = null;
        try {
            InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
            // 创建临时文件
            minioFile = File.createTempFile("minio", ".merge");
            outputStream = new FileOutputStream(minioFile);
            IOUtils.copy(stream, outputStream);
            return minioFile;
        } catch (Exception e) {
            log.debug(e.getMessage());
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.debug(e.getMessage());
                }
            }
        }
        return null;
    }

    /**
     * 清除分块文件
     *
     * @param chunkFileFolderPath 分块文件路径
     * @param chunkTotal          分块文件总数
     */
    private void clearChunkFiles(String chunkFileFolderPath, int chunkTotal) {
        try {
            List<DeleteObject> deleteObjects = Stream.iterate(0, i -> ++i)
                    .limit(chunkTotal)
                    .map(i -> new DeleteObject(chunkFileFolderPath.concat(Integer.toString(i))))
                    .collect(Collectors.toList());

            RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder()
                    .bucket(bucketVideo)
                    .objects(deleteObjects)
                    .build();
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
            results.forEach(r -> {
                try {
                    DeleteError deleteError = r.get();
                } catch (Exception e) {
                    log.error("清除分块文件失败，", e);
                }
            });
        } catch (Exception e) {
            log.error("清除分块文件失败，chunkFileFolderPath：{}", chunkFileFolderPath, e);
        }
    }

    /**
     * 获取合并后的文件路径
     *
     * @param fileMd5   文件md5
     * @param extension 文件拓展名
     */
    private String getFilePathByMd5(String fileMd5, String extension) {
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + extension;
    }

    /**
     * 获取分块文件目录
     *
     * @param fileMd5 文件md5
     */
    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/chunk/";
    }

    /**
     * 根据拓展名获取mimeType
     *
     * @param extension 拓展名
     * @return mimeType
     */
    private String getMimeType(String extension) {
        if (extension == null) {
            extension = "";
        }
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        String mineType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (extensionMatch != null) {
            mineType = extensionMatch.getMimeType();
        }
        return mineType;
    }

    /**
     * 上传文件到minio
     *
     * @param localFilePath 文件磁盘路径
     * @param mimeType      媒体类型
     * @param bucket        桶
     * @param objectName    对象名
     * @return 上传成功
     */
    public boolean addMediaFilesToMinio(String localFilePath, String mimeType, String bucket, String objectName) {
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .filename(localFilePath)
                    .object(objectName)
                    .contentType(mimeType)
                    .build();

            minioClient.uploadObject(uploadObjectArgs);
            log.debug("上传文件成功，bucket：{}，objectName：{}", bucket, objectName);
            return true;
        } catch (Exception e) {
            log.error("上传文件出错，bucket：{}，objectName：{}，错误信息：{}", bucket, objectName, e.getMessage());
        }
        return false;
    }

    /**
     * 获取文件默认存储路径：年/月/日
     */
    private String getDefaultFolderPath() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(new Date()).replace("-", "/") + "/";
    }

    /**
     * 获取文件MD5值
     */
    private String getFileMd5(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return DigestUtils.md5DigestAsHex(fileInputStream);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
