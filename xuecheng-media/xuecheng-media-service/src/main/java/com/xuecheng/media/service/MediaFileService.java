package com.xuecheng.media.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.QueryMediaParamsDTO;
import com.xuecheng.media.model.dto.UploadFileParamsDTO;
import com.xuecheng.media.model.dto.UploadFileResultDTO;
import com.xuecheng.media.model.po.MediaFiles;

import java.io.File;

/**
 * @author Lin
 * @date 2024/2/16 16:41
 */
public interface MediaFileService {

    /**
     * 媒资文件查询
     *
     * @param companyId           机构id
     * @param pageParams          分页参数
     * @param queryMediaParamsDTO 查询条件
     * @return 查询结果
     */
    PageResult<MediaFiles> queryMediaFiles(Long companyId, PageParams pageParams, QueryMediaParamsDTO queryMediaParamsDTO);

    /**
     * 上传文件
     *
     * @param companyId           机构id
     * @param uploadFileParamsDTO 上传文件信息
     * @param localFilePath       文件磁盘路径
     * @return 文件信息
     */
    UploadFileResultDTO uploadFile(Long companyId, UploadFileParamsDTO uploadFileParamsDTO, String localFilePath);

    /**
     * 将文件信息添加到文件表
     *
     * @param companyId           机构id
     * @param fileMd5             文件md5值
     * @param uploadFileParamsDTO 上传文件的信息
     * @param bucket              桶
     * @param objectName          对象名
     * @return 文件信息
     */
    MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDTO uploadFileParamsDTO, String bucket, String objectName);

    /**
     * 检查文件是否存在
     *
     * @param fileMd5 文件md5
     * @return 文件存在与否
     */
    RestResponse<Boolean> checkFile(String fileMd5);

    /**
     * 检查分块是否存在
     *
     * @param fileMd5    文件的md5
     * @param chunkIndex 分块序号
     * @return 分块存在与否
     */
    RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);

    /**
     * 上传分块
     *
     * @param fileMd5            文件md5
     * @param chunkIndex         分块序号
     * @param localChunkFilePath 分块文件本地路径
     * @return 响应
     */
    RestResponse<Boolean> uploadChunk(String fileMd5, int chunkIndex, String localChunkFilePath);

    /**
     * 合并分块
     *
     * @param companyId           机构id
     * @param fileMd5             文件md5
     * @param chunkTotal          分块总和
     * @param uploadFileParamsDTO 文件信息
     * @return 合并成功与否
     */
    RestResponse<Boolean> mergeChunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDTO uploadFileParamsDTO);

    /**
     * 从minio下载文件
     *
     * @param bucket     桶
     * @param objectName 对象名称
     * @return 下载后的文件
     */
    public File downloadFileFromMinio(String bucket, String objectName);

    /**
     * 上传文件到minio
     *
     * @param localFilePath 文件磁盘路径
     * @param mimeType      媒体类型
     * @param bucket        桶
     * @param objectName    对象名
     * @return 上传成功
     */
    public boolean addMediaFilesToMinio(String localFilePath, String mimeType, String bucket, String objectName);
}
