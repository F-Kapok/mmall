package com.fans.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @InterfaceName IFileService
 * @Description:
 * @Author fan
 * @Date 2019-01-07 12:07
 * @Version 1.0
 **/
public interface IFileService {
    String upload(MultipartFile file, String path) throws IOException;
}
