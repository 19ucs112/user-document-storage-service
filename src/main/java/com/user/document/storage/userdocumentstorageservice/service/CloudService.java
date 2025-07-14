package com.user.document.storage.userdocumentstorageservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudService {

    byte[] downloadFile(String fileName, String userName);

    String uploadFile(MultipartFile file, String userName);

}
