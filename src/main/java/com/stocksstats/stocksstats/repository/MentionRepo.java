package com.stocksstats.stocksstats.repository;

import com.stocksstats.stocksstats.entity.Mention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MentionRepo extends JpaRepository<Mention, Integer> {

    List<Mention> findAllByLastMentionOrderByAmountAsc(LocalDate createdAt);

}
