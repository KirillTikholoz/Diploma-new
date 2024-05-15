package org.example.dtos;

import lombok.Data;

@Data
public class AddStepRequest {
    private Long documentId;
    private String title;
    private String text;
}
