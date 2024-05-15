package org.example.repo;

import org.example.domain.Document;
import org.example.domain.Step;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StepRepository extends JpaRepository<Step, Long> {
    List<Step> findByDocument(Document document);

    Page<Step> findAllByOrderByDateAsc(Pageable pageable);
}
