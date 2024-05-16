package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.domain.Document;
import org.example.dtos.DocumentIdResponseDto;
import org.example.services.DocService;
import org.example.utils.ConnectionWithSearcherUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping()
public class SearchController {
    private final ConnectionWithSearcherUtils connectionWithSearcherUtils;
    private final DocService documentsServices;

    @GetMapping("/search/projects")
    public ResponseEntity<?> handleSearchProjects(@RequestParam String query,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "5") int size) {
        ResponseEntity<DocumentIdResponseDto> responseEntity = connectionWithSearcherUtils.searchProjectsRequest(query);
        DocumentIdResponseDto ids = responseEntity.getBody();

        List<Document> docs = documentsServices.getDocsByIds(ids, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(docs);
    }

    @GetMapping("/search/solutions")
    public ResponseEntity<?> handleSearchSolutions(@RequestParam String query,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "5") int size) {
        ResponseEntity<DocumentIdResponseDto> responseEntity = connectionWithSearcherUtils.searchSolutionsRequest(query);
        DocumentIdResponseDto ids = responseEntity.getBody();

        List<Document> docs = documentsServices.getDocsByIds(ids, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(docs);
    }

    @GetMapping("/search/documents")
    public ResponseEntity<?> handleSearchDocuments(@RequestParam String query,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "5") int size) {
        ResponseEntity<DocumentIdResponseDto> responseEntity = connectionWithSearcherUtils.searchDocumentsRequest(query);
        DocumentIdResponseDto ids = responseEntity.getBody();

        List<Document> docs = documentsServices.getDocsByIds(ids, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(docs);
    }




}
