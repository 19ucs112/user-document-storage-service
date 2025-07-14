package com.user.document.storage.userdocumentstorageservice.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "com.user.data.storage")
@Getter
@Setter
public class ApplicationProperties {
    private String baseUrl;
}
