package com.stocksstats.stocksstats.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stocksstats.stocksstats.entity.Mention;
import org.springframework.stereotype.Repository;

@Repository
public interface MentionRepo extends JpaRepository<Mention, Integer> {

}
