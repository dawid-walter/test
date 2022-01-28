package com.dwalter.bookaro.upload.app.port;

import com.dwalter.bookaro.upload.domian.Upload;

import java.util.Optional;

public interface UploadUseCase {
    Upload save(SaveUploadCommand command);

    Optional<Upload> getById(String id);

    void deleteById(String coverId);

    record SaveUploadCommand(String filename, byte[] file, String contentType) {
    }
}
