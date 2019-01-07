package com.fans.service.impl;

import com.fans.service.interfaces.IFileService;
import com.fans.utils.FtpUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

/**
 * @ClassName FileServiceImpl
 * @Description:
 * @Author fan
 * @Date 2019-01-07 12:08
 * @Version 1.0
 **/
@Service(value = "iFileService")
@Slf4j
public class FileServiceImpl implements IFileService {
    @Override
    public String upload(MultipartFile file, String path) throws IOException {
        String fileName = file.getOriginalFilename();
        String fileSuffix = fileName.substring(Objects.requireNonNull(fileName).lastIndexOf("."));
        String uploadFileName = UUID.randomUUID().toString() + fileSuffix;
        log.info("--> 【开始上传文件】 文件名 : {} , 上传路径 : {} , 新文件名 : {}", fileName, path, uploadFileName);

        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        // 文件上传成功
        File targetFile = new File(path, uploadFileName);
        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            log.error("--> 【文件上传异常】", e);
            return null;
        }
        // 将文件上传至FTP服务
        String remoteFilePath = FtpUtil.uploadFile(Lists.newArrayList(targetFile));
        // 删除本地文件
        targetFile.delete();
        return remoteFilePath;
    }
}
