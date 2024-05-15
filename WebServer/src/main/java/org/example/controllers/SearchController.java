package org.example.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.example.domain.Document;
import org.example.services.DocService;
import org.example.utils.ConnectionWithSearcherUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping()
public class SearchController {
    private final ConnectionWithSearcherUtils connectionWithSearcherUtils;
    private final DocService documentsServices;

    @GetMapping("/search")
    public ResponseEntity<?> handleFileUpload(@RequestParam String query,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "5") int size) {
        ResponseEntity<String> responseEntity = connectionWithSearcherUtils.searchRequest(query);

        String jsonResponse = responseEntity.getBody();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(jsonResponse);

            JsonNode idsArray = jsonNode.get("ids");
            List<Long> ids = new ArrayList<>();
            for (JsonNode idNode : idsArray) {
                ids.add(idNode.asLong());
            }

            List<Document> docs = documentsServices.getDocsByIds(ids, page, size);
            System.out.println(docs);

            return ResponseEntity.status(HttpStatus.OK).body(docs);
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body(null);
    }

}
