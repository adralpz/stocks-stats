package com.stocksstats.stocksstats.dto;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "mention", schema = "stock_stats")
public class Mention {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mention_id_gen")
    @SequenceGenerator(name = "mention_id_gen", sequenceName = "mention_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "symbol", nullable = false, length = Integer.MAX_VALUE)
    private String symbol;

    @Column(name = "amount")
    private Short amount;

    @Column(name = "date")
    private Timestamp date;

}