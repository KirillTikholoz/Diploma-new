package org.example.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DocumentDownloadDto {
    private String name;
    private String description;
    //private MultipartFile file;
}
