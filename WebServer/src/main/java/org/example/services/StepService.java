package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.domain.Document;
import org.example.domain.Step;
import org.example.dtos.AddStepRequestDto;
import org.example.repo.StepRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StepService {
    private final StepRepository stepRepository;

    public void createNewStep(AddStepRequestDto stepRequest, String publisher, Document document){
        Step newStep = new Step();
        Date currentDate = new Date();

        newStep.setTitle(stepRequest.getTitle());
        newStep.setText(stepRequest.getText());
        newStep.setDate(currentDate);
        newStep.setPublisher(publisher);
        newStep.setDocument(document);
        newStep.setDoc_attached(false);

        stepRepository.save(newStep);
    }

    public void createFirstStep(Document document){
        Step firstStep = new Step();
        firstStep.setTitle("Опубликован новый проект");
        firstStep.setText(document.getDescription());
        firstStep.setDate(document.getDate());
        firstStep.setPublisher(document.getPublisher());
        firstStep.setDocument(document);
        firstStep.setDoc_attached(true);
        stepRepository.save(firstStep);
    }

    public void createLastStep(Document document){
        Step lastStep = new Step();
        lastStep.setTitle("Проект стал решением");
        lastStep.setText("Проект " + document.getName() + " стал решением");
        lastStep.setDate(document.getDate());
        lastStep.setPublisher(document.getPublisher());
        lastStep.setDocument(document);
        lastStep.setDoc_attached(true);
        stepRepository.save(lastStep);
    }

    public List<Step> findStepsByDocument(Document document){
        return stepRepository.findByDocument(document);
    }

    public Page<Step> getPageNews(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return stepRepository.findAllByOrderByDateAsc(pageable);
    }

    public void deleteAllStepsForDocument(Document document){
        List<Step> deleteSteps = stepRepository.findByDocument(document);
        stepRepository.deleteAll(deleteSteps);
    }

}
