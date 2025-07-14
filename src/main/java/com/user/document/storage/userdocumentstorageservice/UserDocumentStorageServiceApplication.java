package com.user.document.storage.userdocumentstorageservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class UserDocumentStorageServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserDocumentStorageServiceApplication.class, args);
    }

}
