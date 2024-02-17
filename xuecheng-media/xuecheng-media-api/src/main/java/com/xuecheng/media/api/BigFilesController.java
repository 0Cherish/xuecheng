package com.xuecheng.media.api;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.UploadFileParamsDTO;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 大文件上传接口
 *
 * @author Lin
 * @date 2024/2/17 14:00
 */
@Api(value = "大文件上传接口", tags = "大文件上传接口")
@RestController
public class BigFilesController {

    @Autowired
    private MediaFileService mediaFileService;

    @ApiOperation("文件上传前检查文件")
    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkFile(@RequestParam("fileMd5") String fileMd5) {
        return mediaFileService.checkFile(fileMd5);
    }

    @ApiOperation("分块文件上传前的检测")
    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkChunk(@RequestParam("fileMd5") String fileMd5,
                                            @RequestParam("chunk") int chunk) {
        return mediaFileService.checkChunk(fileMd5, chunk);
    }

    @ApiOperation("上传分块文件")
    @PostMapping("/upload/uploadchunk")
    public RestResponse<Boolean> uploadChunk(@RequestPart("file") MultipartFile file,
                                             @RequestParam("fileMd5") String fileMd5,
                                             @RequestParam("chunk") int chunk) throws Exception {
        File tempFile = File.createTempFile("minio", ".temp");
        file.transferTo(tempFile);
        String localFilePath = tempFile.getAbsolutePath();
        return mediaFileService.uploadChunk(fileMd5, chunk, localFilePath);
    }

    @ApiOperation("合并文件")
    @PostMapping("/upload/mergechunks")
    public RestResponse<Boolean> mergeChunks(@RequestParam("fileMd5") String fileMd5,
                                             @RequestParam("fileName") String fileName,
                                             @RequestParam("chunkTotal") int chunkTotal) {
        // TODO 获取用户所属机构id
        Long companyId = 1232141425L;
        UploadFileParamsDTO uploadFileParamsDTO = new UploadFileParamsDTO();
        uploadFileParamsDTO.setFileType("001002");
        uploadFileParamsDTO.setTags("课程视频");
        uploadFileParamsDTO.setFilename(fileName);
        return mediaFileService.mergeChunks(companyId, fileMd5, chunkTotal, uploadFileParamsDTO);
    }
}
