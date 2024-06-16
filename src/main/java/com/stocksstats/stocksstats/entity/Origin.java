package com.stocksstats.stocksstats.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "origin", schema = "stock_stats")
public class Origin {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "origin_id_gen")
    @SequenceGenerator(name = "origin_id_gen", sequenceName = "origin_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "mention_id", nullable = false)
    private Mention mention;

    @Column(name = "url", length = Integer.MAX_VALUE)
    private String url;

    @Column(name = "text_fragment", length = Integer.MAX_VALUE)
    private String textFragment;

}