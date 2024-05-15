package org.example.services;

import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.example.config.SecurityConfig;
import org.example.domain.Document;
import org.example.dtos.DocumentDownloadDto;
import org.example.repo.DocRepository;
import org.example.utils.ConnectionWithSearcherUtils;
import org.example.utils.JwtTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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

        // сохраняю документ в поисковик, но нужно будет обработчики ошибок добавить
        // (вдруг док сохраниться в системе, а при сохранении в поисковик проблема произойдет)
        ResponseEntity<HttpResponse> responce = connectionWithSearcherUtils.addDocumentInSearcher(newDocument.getId(), file);
    }

    public Optional<Document> findDocumentById(Long id){
        return documentsRepository.findById(id);
    }

    public Page<Document> getPageDocuments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return documentsRepository.findAllByOrderByDateAsc(pageable);
    }

    public Page<Document> getPageProjects(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return documentsRepository.findByProvidedFalseOrderByDateAsc(pageable);
    }

    public Page<Document> getPageSolution(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return documentsRepository.findByProvidedTrueOrderByDateAsc(pageable);
    }

    public Boolean provideProjectIntoSolution(Long id, String publisher){
        Optional<Document> optionalDocument = documentsRepository.findById(id);
        if (optionalDocument.isPresent()) {
            Date currentDate = new Date();

            Document document = optionalDocument.get();
            document.setPublisher(publisher);
            document.setProvided(true);
            document.setDate(currentDate);
            documentsRepository.save(document);

            stepService.createLastStep(document);

            return true;
        }
        else {
            logger.info("Документ с id: " + id + " не найден");
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

    public List<Document> getDocsByIds(List<Long> ids, int page, int size){
        int start = page * size;
        int end = Math.min(start + size, ids.size());
        List<Long> idsToFetch = ids.subList(start, end);

        return documentsRepository.findAllById(idsToFetch);
    }
}
