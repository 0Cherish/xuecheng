package com.xuecheng.media.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.model.dto.QueryMediaParamsDTO;
import com.xuecheng.media.model.dto.UploadFileParamsDTO;
import com.xuecheng.media.model.dto.UploadFileResultDTO;
import com.xuecheng.media.model.po.MediaFiles;

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
}
