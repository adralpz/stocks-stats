package com.stocksstats.stocksstats.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "mention")
public class Mention {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mention_id_gen")
    @SequenceGenerator(name = "mention_id_gen", sequenceName = "mention_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "amount")
    private Short amount;

    @Column(name = "last_mention")
    private LocalDate lastMention;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "symbol_id")
    private Stock symbol;

    @Transient
    @OneToMany(mappedBy = "mention")
    private Set<Origin> origins = new LinkedHashSet<>();

}