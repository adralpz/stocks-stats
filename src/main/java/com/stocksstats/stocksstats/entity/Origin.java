package com.stocksstats.stocksstats.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

@Getter
@Setter
@Entity
@Table(name = "origin")
public class Origin {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "origin_id_gen")
    @SequenceGenerator(name = "origin_id_gen", sequenceName = "origin_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mention_id", nullable = false)
    private Mention mention;

    @Lob
    @Column(name = "url")
    private String url;

    @Lob
    @Column(name = "text_fragment")
    private String textFragment;

}