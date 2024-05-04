package org.example.controllers;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.example.services.DocService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping
@CrossOrigin
public class ProvideController {
    private final DocService documentsServices;
    @PostMapping("/provide")
    @PermitAll
    public ResponseEntity<?> handleFileProvide(@RequestBody Map<String, Long> payload) {
        try {
            Long id = payload.get("id");
            Boolean provideFlag = documentsServices.provideProjectIntoSolution(id);
            if (provideFlag) {
                return ResponseEntity.ok("Проект успешно стал решением");
            } else {
                return ResponseEntity.badRequest().body("Данного проекта не существует");
            }
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Произошла ошибка при обработке запроса");
        }
    }

}
