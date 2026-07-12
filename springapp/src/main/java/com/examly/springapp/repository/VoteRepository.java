package com.examly.springapp.repository;
//this
import com.examly.springapp.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    List<Vote> findByPollIdAndUsername(Long pollId, String username);

    List<Vote> findByPollId(Long pollId);
}