package org.example.controllers;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.example.domain.Document;
import org.example.services.DocService;
import org.example.utils.ConnectionWithSearcherUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;


@RestController
@RequiredArgsConstructor
@RequestMapping
@CrossOrigin
public class DownloadController {

    private final DocService documentsServices;
    private final ConnectionWithSearcherUtils connectionWithSearcherUtils;

    @PostMapping("/download")
    @PermitAll
    public ResponseEntity<?> handleFileUpload(@RequestParam("name") String name,
                                              @RequestParam("pdfFile") MultipartFile file
                                              ) {
        String pdfFileName = file.getOriginalFilename();


//        Long testid = 13L;
//        ResponseEntity<HttpResponse> responce = connectionWithSearcherUtils.addDocumentInSearcher(testid, pdfFile);
//        if (responce.getStatusCode() == HttpStatus.OK){
//            System.out.println("Статус ответа OK");
//        }

        if (pdfFileName != null) {
            Date currentDate = new Date();

            Document newDocument = new Document();
            newDocument.setName(name);
            newDocument.setDate(currentDate);

            // пока по умолчанию, потом следует брать и тела запроса
            newDocument.setPublisher("Без публикатора");
            newDocument.setDescription("Без описания");

            // вот эта строчка под вопросом (если загружать можно только проекты то оставить так, или изменить)
            newDocument.setProvided(false);

            try {
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

            documentsServices.createNewDocument(newDocument);
            // сохраняю документ в поисковик, но нужно будет обработчики ошибок добавить
            // (вдруг док сохраниться в системе, а при сохранении в поисковик проблема произойдет)
            ResponseEntity<HttpResponse> responce = connectionWithSearcherUtils.addDocumentInSearcher(newDocument.getId(), file);

        } else {
            return ResponseEntity.badRequest().body("Документ не прикреплен к запросу");
        }

        return ResponseEntity.ok("ok");
    }

}
