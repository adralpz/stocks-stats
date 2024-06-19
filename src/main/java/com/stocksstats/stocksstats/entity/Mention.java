package com.stocksstats.stocksstats.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "mention", schema = "stock_stats")
public class Mention {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mention_id_gen")
	@SequenceGenerator(name = "mention_id_gen", sequenceName = "stock_stats.mention_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Integer id;

	@NotNull
	@Column(name = "symbol", nullable = false, length = Integer.MAX_VALUE)
	private String symbol;

	@Column(name = "amount")
	private Short amount;

	@ColumnDefault("now()")
	@Column(name = "created_at")
	private LocalDate createdAt;

}
