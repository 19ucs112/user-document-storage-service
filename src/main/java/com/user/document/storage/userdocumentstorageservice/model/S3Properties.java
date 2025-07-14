package com.user.document.storage.userdocumentstorageservice.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "com.user.data.storage.aws.s3")
public class S3Properties {
    private String s3bucketName;
    private String s3StoragePrefix;
}
