package com.user.document.storage.userdocumentstorageservice;

import com.user.document.storage.userdocumentstorageservice.model.ApplicationProperties;
import com.user.document.storage.userdocumentstorageservice.model.AwsConfigProperties;
import com.user.document.storage.userdocumentstorageservice.model.S3Properties;
import com.user.document.storage.userdocumentstorageservice.service.AwsCloudService;
import com.user.document.storage.userdocumentstorageservice.service.CloudService;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties({AwsConfigProperties.class, S3Properties.class, ApplicationProperties.class})
public class UserDocumentStorageServiceAutoConfiguration {

    @Autowired
    private AwsConfigProperties awsConfigProperties;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(awsConfigProperties.getAwsAccessKey(),
                awsConfigProperties.getAwsSecretKey());
        return S3Client.builder()
                .region(Region.of(awsConfigProperties.getAwsRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("fileCache");
    }


    @Bean
    public CloudService cloudService(S3Client s3Client, S3Properties s3Properties) {
        return new AwsCloudService(s3Client, s3Properties);
    }

    @Bean
    public OpenAPI openAPI() {
        OpenAPI openAPI = new OpenAPI();
        openAPI.servers(List.of(new Server().url(applicationProperties.getBaseUrl())));
        return openAPI;
    }

    @EventListener(ServletWebServerInitializedEvent.class)
    public void applicationStart(ServletWebServerInitializedEvent event) {
        int port = event.getWebServer().getPort();
        ConfigurableEnvironment environment = event.getApplicationContext().getEnvironment();
        System.out.println("================================================================================="); //NOPMD
        System.out.println("                                                                               "); //NOPMD
        System.out.printf("     Server started on port: %d and active profile: %s                          %n", //NOPMD
                port, Arrays.toString(environment.getActiveProfiles()));
        System.out.printf("     Swagger URL - http://localhost:%d/swagger-ui.html                          %n", port); //NOPMD
        System.out.println("                                                                               "); //NOPMD
        System.out.println("================================================================================="); //NOPMD
    }
}
