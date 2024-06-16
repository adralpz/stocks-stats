package com.stocksstats.stocksstats.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "stocks", schema = "stock_stats")
public class Stock {
    @Id
    @SequenceGenerator(name = "stocks_id_gen", sequenceName = "origin_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false, length = Integer.MAX_VALUE)
    private String id;

    @Column(name = "name", length = Integer.MAX_VALUE)
    private String name;

    @Column(name = "reputation")
    private Integer reputation;

}