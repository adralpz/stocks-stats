package com.stocksstats.stocksstats.dto;

import jakarta.persistence.*;
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

}