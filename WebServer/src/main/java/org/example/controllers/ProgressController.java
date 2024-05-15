package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.domain.Document;
import org.example.domain.Step;
import org.example.dtos.AddStepRequestDto;
import org.example.dtos.DocumentIdRequestDto;
import org.example.services.DocService;
import org.example.services.StepService;
import org.example.utils.JwtTokenUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ProgressController {
    private final StepService stepService;
    private final DocService documentsService;
    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping("/addProjectStep")
    public ResponseEntity<?> addProjectStep(@CookieValue(value = "accessToken", required = false) String accessTokenArg,
                                            @RequestBody AddStepRequestDto stepRequest){

        Optional<Document> existingDoc = documentsService.findDocumentById(stepRequest.getDocumentId());

        if (existingDoc.isPresent()){
            String publisher = jwtTokenUtils.getNameFromAccessToken(accessTokenArg);
            stepService.createNewStep(stepRequest, publisher, existingDoc.get());
            return ResponseEntity.ok("Шаг для документа создан");
        } else {
            return ResponseEntity.badRequest().body("Документа с таким id не существует");
        }
    }

    @GetMapping("/getProjectSteps")
    public ResponseEntity<?> addProjectStep(@RequestBody DocumentIdRequestDto getStepRequest){
        Optional<Document> existingDoc = documentsService.findDocumentById(getStepRequest.getDocumentId());

        if (existingDoc.isPresent()){
            List<Step> steps = stepService.findStepsByDocument(existingDoc.get());
            return ResponseEntity.ok(steps);
        } else {
            return ResponseEntity.badRequest().body("Документа с таким id не существует");
        }
    }
}
