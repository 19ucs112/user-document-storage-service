package com.user.document.storage.userdocumentstorageservice.web;

import com.user.document.storage.userdocumentstorageservice.BaseControllerTest;
import com.user.document.storage.userdocumentstorageservice.model.exceptions.UserDocumentStorageException;
import com.user.document.storage.userdocumentstorageservice.rest.v1.DocumentStorageController;
import com.user.document.storage.userdocumentstorageservice.service.CloudService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentStorageController.class)
@ExtendWith(SpringExtension.class)
public class DocumentStorageControllerTest extends BaseControllerTest {

    private static final String BASE_URL = "/api/v1/document-storage";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CloudService cloudService;


    @Test
    void downloadFileSuccessTest() throws Exception {
        when(cloudService.downloadFile(anyString(), anyString())).thenReturn("test".getBytes());
        mockMvc.perform(
                get(BASE_URL + "/search")
                        .param("fileName", "test.txt")
                        .param("userName", "john_doe")
        ).andExpect(status().isOk());
    }

    @Test
    void downloadFileWIthDocumentStorageExceptionFailureTest() throws Exception {
        when(cloudService.downloadFile(anyString(), anyString())).thenThrow(new RuntimeException("Error"));
        mockMvc.perform(
                get(BASE_URL + "/search")
                        .param("fileName", "test.txt")
                        .param("userName", "john_doe")
        ).andExpect(status().isBadRequest());
    }

    @Test
    void uploadFileSuccessTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "dummy content".getBytes()
        );
        when(cloudService.uploadFile(any(), anyString())).thenReturn("file-key");
        mockMvc.perform(
                multipart(BASE_URL + "/upload")
                        .file(file)
                        .param("userName", "john_doe")
                        .param("file", "test.txt")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andExpect(status().isOk());
    }

    @Test
    void uploadFileWithWithExceptionFailureTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "dummy content".getBytes()
        );
        when(cloudService.uploadFile(any(), anyString())).thenThrow(new UserDocumentStorageException("Error"));
        mockMvc.perform(
                multipart(BASE_URL + "/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("userName", "john_doe")
        ).andExpect(status().isBadRequest());
    }

    @Test
    void uploadFileWithWithBadRequestFailureTest() throws Exception {
        when(cloudService.uploadFile(any(), anyString())).thenThrow(new UserDocumentStorageException("Error"));
        mockMvc.perform(
                post(BASE_URL + "/upload")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("userName", "john_doe")
        ).andExpect(status().isBadRequest());
    }

}
