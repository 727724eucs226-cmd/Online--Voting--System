package com.examly.springapp.repository;

import com.examly.springapp.model.Poll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {
    Page<Poll> findByIsPublicTrue(Pageable pageable);
}