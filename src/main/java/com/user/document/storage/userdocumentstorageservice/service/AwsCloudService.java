package com.user.document.storage.userdocumentstorageservice.service;

import com.user.document.storage.userdocumentstorageservice.model.PathBuilder;
import com.user.document.storage.userdocumentstorageservice.model.S3Properties;
import com.user.document.storage.userdocumentstorageservice.model.exceptions.UserDocumentStorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@RequiredArgsConstructor
public class AwsCloudService implements CloudService {

    private static final String FILE_UPLOAD_SUCCESS_MESSAGE = "File uploaded successfully. File key -";
    private final S3Client s3Client;
    private final S3Properties s3Properties;

    @Override
    @Cacheable(value = "fileCache", key = "{#fileName, #userName}")
    public byte[] downloadFile(String fileName, String userName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .key(buildStaticFileKey(fileName, userName))
                .bucket(s3Properties.getS3bucketName())
                .build();

        try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest)) {
            return s3Object.readAllBytes();
        } catch (Exception e) {
            throw new UserDocumentStorageException("Error reading file from S3");
        }
    }

    @Override
    public String uploadFile(MultipartFile file, String userName) {
        try {
            String fileKey = buildStaticFileKey(file.getOriginalFilename(), userName);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Properties.getS3bucketName())
                    .key(buildStaticFileKey(file.getOriginalFilename(), userName))
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            return FILE_UPLOAD_SUCCESS_MESSAGE + fileKey;
        } catch (Exception e) {
            throw new UserDocumentStorageException("Error uploading file to S3");
        }

    }

    private String buildStaticFileKey(String fileName, String userName) {
        return PathBuilder.builder()
                .join(s3Properties.getS3StoragePrefix())
                .join(userName)
                .join(fileName)
                .build();
    }
}
