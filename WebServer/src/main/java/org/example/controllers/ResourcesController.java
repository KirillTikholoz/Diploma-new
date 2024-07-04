package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.example.domain.Document;
import org.example.domain.Step;
import org.example.services.DocService;
import org.example.services.StepService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resources")
public class ResourcesController {

    private final DocService documentsServices;
    private final StepService stepService;

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getResource(@PathVariable Long id){
        Optional<Document> document = documentsServices.findDocumentById(id);

        if (document.isPresent()){
            String pathFile = document.get().getPath();
            File file = new File(pathFile);

            try {
                InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
                if (resource.exists()) {
                    HttpHeaders headers = new HttpHeaders();

                    try {
                        Tika tika = new Tika();
                        String contentType = tika.detect(file);
                        headers.add(HttpHeaders.CONTENT_TYPE, contentType);
                        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(resource);
                    } catch (IOException e){
                        e.printStackTrace();
                    }

                } else {
                    return ResponseEntity.badRequest().body("Запись о файле найдена в базе данных, но такого файла не существует");
                }
            } catch (FileNotFoundException e){
                e.printStackTrace();
            }

        }

        return ResponseEntity.badRequest().body("Файла с таким id не существует");
    }

    @GetMapping("/documents")
    public ResponseEntity<?> getDocumentsForPage(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "5") int size){

        Page<Document> documents = documentsServices.getPageDocuments(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(documents);
    }

    @GetMapping("/documentsByAuthor")
    public ResponseEntity<?> getDocumentsByAuthorForPage(@RequestParam String publisher,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "5") int size){

        Page<Document> documents = documentsServices.getPageDocumentsByAuthor(publisher, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(documents);
    }

    @GetMapping("/projects")
    public ResponseEntity<?> getProjectsForPage(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "5") int size){

        Page<Document> projects = documentsServices.getPageProjects(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(projects);
    }

    @GetMapping("/solutions")
    public ResponseEntity<?> getSolutionsForPage(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "5") int size){

        Page<Document> solution = documentsServices.getPageSolution(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(solution);
    }

    @GetMapping("/news")
    public ResponseEntity<?> getNewsForPage(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "5") int size){

        Page<Step> steps = stepService.getPageNews(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(steps);
    }

}
