package com.stocksstats.stocksstats.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "stock")
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stock_id_gen")
    @SequenceGenerator(name = "stock_id_gen", sequenceName = "stocks_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Column(name = "name")
    private String name;

    @Column(name = "reputation")
    private Short reputation;

    @OneToMany(mappedBy = "symbol")
    @JsonIgnore
    private Set<Mention> mentions = new LinkedHashSet<>();

}