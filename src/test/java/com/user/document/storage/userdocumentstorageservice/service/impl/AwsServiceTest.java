package com.user.document.storage.userdocumentstorageservice.service.impl;

import com.user.document.storage.userdocumentstorageservice.AbstractTest;
import com.user.document.storage.userdocumentstorageservice.model.PathBuilder;
import com.user.document.storage.userdocumentstorageservice.model.S3Properties;
import com.user.document.storage.userdocumentstorageservice.model.exceptions.UserDocumentStorageException;
import com.user.document.storage.userdocumentstorageservice.service.AwsCloudService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AwsServiceTest extends AbstractTest {

    @Autowired
    private AwsCloudService awsCloudService;

    @Autowired
    private S3Properties s3Properties;

    @Test
    void searchAndDownloadFileSuccessTest() {
        GetObjectResponse getObjectResponse = GetObjectResponse.builder()
                .build();
        byte[] fileBytes = "sample content".getBytes();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileBytes);
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(new ResponseInputStream<>(getObjectResponse,
                byteArrayInputStream));
        byte[] fileResponseBytes = awsCloudService.downloadFile("test.txt", "test");
        assertArrayEquals(fileBytes, fileResponseBytes, ASSERTION_MESSAGE);
    }

    @Test
    void searchAndDownloadFileFailureTest() {
        when(s3Client.getObject(any(GetObjectRequest.class))).thenThrow(new RuntimeException("Error"));
        Throwable exception = assertThrows(UserDocumentStorageException.class,
                () -> awsCloudService.downloadFile("test.txt", "test"));
        assertNotNull(exception);
        assertEquals("Error reading file from S3", exception.getMessage());
    }

    @Test
    void uploadFileSuccessTest() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        String fileName = "doc.pdf";
        String userName = "john_doe";
        byte[] fileBytes = "dummy content".getBytes();

        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getBytes()).thenReturn(fileBytes);
        String result = awsCloudService.uploadFile(file, userName);
        String expectedFileKey = PathBuilder.builder()
                .join(s3Properties.getS3StoragePrefix())
                .join(userName)
                .join("doc.pdf").build();
        assertEquals("File uploaded successfully. File key -" + expectedFileKey, result,
                ASSERTION_MESSAGE);
    }

    @Test
    void uploadFileFailureTest() throws IOException {
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(new RuntimeException("message"));
        String fileName = "doc.pdf";
        MultipartFile file = mock(MultipartFile.class);
        byte[] fileBytes = "dummy content".getBytes();

        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getBytes()).thenReturn(fileBytes);
        String userName = "john_doe";
        Throwable exception = assertThrows(UserDocumentStorageException.class,
                () -> awsCloudService.uploadFile(file, userName));
        assertNotNull(exception);
        assertEquals("Error uploading file to S3", exception.getMessage());
    }
}
