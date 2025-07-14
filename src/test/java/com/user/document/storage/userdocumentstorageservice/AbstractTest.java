package com.user.document.storage.userdocumentstorageservice;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.s3.S3Client;

@SpringBootTest(classes = {UserDocumentStorageServiceApplication.class, UserDocumentStorageServiceAutoConfiguration.class,
        AbstractTest.TestConfig.class})
@ActiveProfiles("test")
public class AbstractTest {

    protected static final String ASSERTION_MESSAGE = "Assertion must be true";

    @MockBean
    protected S3Client s3Client;

    @TestConfiguration
    public static class TestConfig {

    }
}
