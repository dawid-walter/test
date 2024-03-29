package com.dwalter.bookaro.upload.domian;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class Upload {
    String id;
    byte[] file;
    String contentType;
    String filename;
    LocalDateTime createdAt;
}
