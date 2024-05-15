package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.example.services.DocService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class testController {

    private final DocService documentsServices;

    @GetMapping("/hello")
    @PermitAll
    public String hello(){
        return "Hello Word";
    }

    @PostMapping("/test")
    @PermitAll
    public ResponseEntity<?> test(@RequestParam("id") Long id,
                       @RequestParam("file") MultipartFile file){

        System.out.println("я вошел в тест");
        String fileName = file.getOriginalFilename();
        System.out.println(fileName);


        return ResponseEntity.ok("ok");
    }

    @GetMapping("/testSearch")
    @PermitAll
    public ResponseEntity<?> testSearch(@RequestParam String query){
        System.out.println("Я ЗАШЕЛ В ПОИСКОВИК КАК В МИКРОСЕРВИС");
        System.out.println("Запрос на другой стороне: " + query);

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Map<String, Object> jsonData = new HashMap<>();
            List<Long> ids = new ArrayList<>();
            ids.add(5L);
            ids.add(6L);
            ids.add(7L);
            ids.add(8L);
            ids.add(9L);
            ids.add(10L);
            ids.add(12L);
            ids.add(13L);
            jsonData.put("ids", ids);

            String json = objectMapper.writeValueAsString(jsonData);

            return ResponseEntity.ok(json);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }

}
