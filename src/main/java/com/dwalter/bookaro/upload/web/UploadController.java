package com.dwalter.bookaro.upload.web;

import com.dwalter.bookaro.upload.app.port.UploadUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/uploads")
@RequiredArgsConstructor
class UploadController {
    private final UploadUseCase upload;

    @GetMapping("/{id}")
    public ResponseEntity<UploadResponse> get(@PathVariable String id) {
        return upload.getById(id).map(file -> {
                    UploadResponse response = new UploadResponse(file.getId(), file.getContentType(), file.getFilename(), file.getCreatedAt(), file.getFile().length);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> getFile(@PathVariable String id) {
        return upload.getById(id).map(file -> {
            String contentDisposition = "attachment; filename=\"" + file.getFilename() + "\"";
                    Resource resource = new ByteArrayResource(file.getFile());
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                            .contentType(MediaType.parseMediaType(file.getContentType()))
                            .body(resource);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    record UploadResponse(String id, String contentType, String filename, LocalDateTime createdAt, int fileSize) {
    }
}
