package com.examly.springapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.examly.springapp.model.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    List<Vote> findByPollIdAndUsername(Long pollId, String username);

    List<Vote> findByPollId(Long pollId);

    // Vote History
    List<Vote> findByUsername(String username);
    boolean existsByPollId(Long pollId);
}