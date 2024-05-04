package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.domain.Document;
import org.example.repo.DocRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocService {
    private final DocRepository documentsRepository;

    public void createNewDocument(Document document){
        documentsRepository.save(document);
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

    public Boolean provideProjectIntoSolution(Long id){
        Optional<Document> optionalDocument = documentsRepository.findById(id);
        if (optionalDocument.isPresent()) {
            Date currentDate = new Date();

            Document document = optionalDocument.get();
            document.setProvided(true);
            document.setDate(currentDate);
            documentsRepository.save(document);

            return true;
        }
        else {
            System.out.print("Документ с id: " + id + " не найден");
            return false;
        }

    }

    public List<Document> getDocsByIds(List<Long> ids){
        return documentsRepository.findAllById(ids);
    }
}
