package com.stocksstats.stocksstats.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stocksstats.stocksstats.entity.Mention;

/**
 * Repository is an interface that provides access to data in a database
 */
public interface MentionRepo extends JpaRepository<Mention, Integer> {
}
