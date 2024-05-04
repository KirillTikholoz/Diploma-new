package org.example.repo;

import org.example.domain.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface DocRepository extends JpaRepository<Document, Long> {
    Page<Document> findAllByOrderByDateAsc(Pageable pageable);
    Page<Document> findByProvidedFalseOrderByDateAsc(Pageable pageable);
    Page<Document> findByProvidedTrueOrderByDateAsc(Pageable pageable);

    //List<Optional<Document>> findAllById(List<Long> ids);

}
