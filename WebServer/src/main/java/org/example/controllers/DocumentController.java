package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.dtos.DocumentIdRequestDto;
import org.example.services.DocService;
import org.example.utils.JwtTokenUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/documents")
public class DocumentController {
    private final DocService documentsServices;
    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping("/download")
    public ResponseEntity<?> handleFileUpload(@RequestParam("name") String name,
                                              @RequestParam("description") String description,
                                              @RequestParam("pdfFile") MultipartFile file,
                                              @CookieValue(name = "accessToken", required = false) String accessTokenArg
    ) {
        String fileName = file.getOriginalFilename();

        if (fileName != null) {
            String publisher = jwtTokenUtils.getNameFromAccessToken(accessTokenArg);
            documentsServices.createNewDocument(name, description, publisher, file);
        } else {
            return ResponseEntity.badRequest().body("Документ не прикреплен к запросу");
        }
        return ResponseEntity.ok("ok");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> handleDocumentDelete(@RequestBody DocumentIdRequestDto documentIdRequest) {
        try {
            Long id = documentIdRequest.getDocumentId();
            Boolean DeletFlag = documentsServices.deleteDocument(id);
            if (DeletFlag) {
                return ResponseEntity.ok("Проект удален");
            } else {
                return ResponseEntity.badRequest().body("Данного проекта не существует");
            }
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Произошла ошибка при обработке запроса");
        }
    }

    @PostMapping("/provide")
    public ResponseEntity<?> handleFileProvide(@RequestBody DocumentIdRequestDto documentIdRequest,
                                               @CookieValue(name = "accessToken", required = false) String accessTokenArg
    ) {
        try {
            Long id = documentIdRequest.getDocumentId();
            String publisher = jwtTokenUtils.getNameFromAccessToken(accessTokenArg);
            Boolean provideFlag = documentsServices.provideProjectIntoSolution(id, publisher);
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
