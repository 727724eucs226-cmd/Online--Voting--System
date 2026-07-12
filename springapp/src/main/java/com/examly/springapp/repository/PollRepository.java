package com.examly.springapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.examly.springapp.model.Poll;
import com.examly.springapp.model.PollStatus;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {

    Page<Poll> findByIsPublicTrue(Pageable pageable);

    List<Poll> findByCreatedBy(String createdBy);

    List<Poll> findByTitleContainingIgnoreCase(String title);
    List<Poll> findByStatus(PollStatus status);
    Optional<Poll> findByPrivateLink(String privateLink);

}