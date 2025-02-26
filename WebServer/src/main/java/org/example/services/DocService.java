package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.domain.Document;
import org.example.dtos.DocumentIdRequestDto;
import org.example.dtos.DocumentIdResponseDto;
import org.example.repo.DocRepository;
import org.example.utils.ConnectionWithSearcherUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocService {
    private static final Logger logger = LoggerFactory.getLogger(DocService.class);
    private final DocRepository documentsRepository;
    private final StepService stepService;
    private final ConnectionWithSearcherUtils connectionWithSearcherUtils;

    @Transactional
    public void createNewDocument(String name, String description, String publisher, MultipartFile file){
        Date currentDate = new Date();

        Document newDocument = new Document();
        newDocument.setName(name);
        newDocument.setDate(currentDate);
        newDocument.setPublisher(publisher);
        newDocument.setDescription(description);
        newDocument.setProvided(false);

        try {
            String pdfFileName = file.getOriginalFilename();
            String projectDirectory = System.getProperty("user.dir");
            Path filePath = Paths.get(projectDirectory, "files");
            File directory = filePath.toFile();

            File destinationFile = new File(directory, pdfFileName);

            try (InputStream inputStream = file.getInputStream();
                 OutputStream outputStream = new FileOutputStream(destinationFile)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                System.out.println("Файл успешно сохранен в: " + destinationFile.getAbsolutePath());
                newDocument.setPath(destinationFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        documentsRepository.save(newDocument);
        stepService.createFirstStep(newDocument);

        ResponseEntity<String> response = connectionWithSearcherUtils.addDocumentInSearcher(newDocument.getId(), file);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Ошибка при сохранении документа в поисковике");
        }
    }

    public Optional<Document> findDocumentById(Long id){
        return documentsRepository.findById(id);
    }

    public Page<Document> getPageDocuments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return documentsRepository.findAllByOrderByDateAsc(pageable);
    }

    public Page<Document> getPageDocumentsByAuthor(String publisher, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return documentsRepository.findAllByPublisher(publisher, pageable);
    }

    public Page<Document> getPageProjects(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return documentsRepository.findByProvidedFalseOrderByDateAsc(pageable);
    }

    public Page<Document> getPageSolution(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return documentsRepository.findByProvidedTrueOrderByDateAsc(pageable);
    }

    @Transactional
    public Boolean provideProjectIntoSolution(DocumentIdRequestDto documentIdRequest, String publisher){
        Optional<Document> optionalDocument = documentsRepository.findById(documentIdRequest.getDocumentId());
        if (optionalDocument.isPresent()) {
            Date currentDate = new Date();

            Document document = optionalDocument.get();
            document.setPublisher(publisher);
            document.setProvided(true);
            document.setDate(currentDate);
            documentsRepository.save(document);

            stepService.createLastStep(document);

            ResponseEntity<String> response = connectionWithSearcherUtils.addSolution(documentIdRequest);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Ошибка при проведении проекта в решение в поисковике");
            }

            return true;
        }
        else {
            logger.info("Документ с id: " + documentIdRequest.getDocumentId() + " не найден");
            return false;
        }

    }

    public Boolean deleteDocument(Long id){
        Optional<Document> optionalDocument = documentsRepository.findById(id);
        if (optionalDocument.isPresent()) {
            Document document = optionalDocument.get();
            stepService.deleteAllStepsForDocument(document);
            documentsRepository.delete(document);
            return true;
        } else {
            logger.info("Документ с id: " + id + " не найден");
            return false;
        }

    }

    public List<Document> getDocsByIds(DocumentIdResponseDto documentIdResponseDto, int page, int size){
        int start = page * size;
        int end = Math.min(start + size, documentIdResponseDto.getIds().size());
        List<Long> idsToFetch = documentIdResponseDto.getIds().subList(start, end);

        return documentsRepository.findAllById(idsToFetch);
    }
}
