package org.example.dtos;

import lombok.Data;

import java.util.List;

@Data
public class DocumentIdResponseDto {
    private List<Long> ids;
}
