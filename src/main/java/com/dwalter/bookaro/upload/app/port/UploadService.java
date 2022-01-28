package com.dwalter.bookaro.upload.app.port;

import com.dwalter.bookaro.upload.domian.Upload;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
class UploadService implements UploadUseCase {
    private final Map<String, Upload> storage = new HashMap<>();

    @Override
    public Upload save(SaveUploadCommand command) {
        String randomId = RandomStringUtils.randomAlphanumeric(8).toLowerCase();
        Upload upload = new Upload(randomId, command.file(), command.contentType(), command.filename(), LocalDateTime.now());
        storage.put(upload.getId(), upload);
        return upload;
    }

    @Override
    public Optional<Upload> getById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public void deleteById(String coverId) {
        storage.remove(coverId);
    }
}
