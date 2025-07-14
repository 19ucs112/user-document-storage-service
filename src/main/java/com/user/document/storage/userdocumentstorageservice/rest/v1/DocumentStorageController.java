package com.user.document.storage.userdocumentstorageservice.rest.v1;

import com.user.document.storage.userdocumentstorageservice.service.CloudService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/document-storage")
@RequiredArgsConstructor
public class DocumentStorageController {

    private final CloudService cloudService;

    @GetMapping("/search")
    public ResponseEntity<Resource> searchAndDownloadFile(@RequestParam String fileName, @RequestParam String userName) {
        byte[] fileBytes = cloudService.downloadFile(fileName, userName);
        ByteArrayResource resource = new ByteArrayResource(fileBytes);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentLength(fileBytes.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam MultipartFile file, @RequestParam String userName) {
        return ResponseEntity.ok(cloudService.uploadFile(file, userName));
    }

}
