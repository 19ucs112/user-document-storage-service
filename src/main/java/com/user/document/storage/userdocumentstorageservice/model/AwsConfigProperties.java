package com.user.document.storage.userdocumentstorageservice.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "com.user.data.storage.aws")
public class AwsConfigProperties {
    private String awsAccessKey;
    private String awsSecretKey;
    private String awsRegion;
}
