package com.stocksstats.stocksstats.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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