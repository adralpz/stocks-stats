package com.stocksstats.stocksstats.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

@Getter
@Setter
@Entity
@Table(name = "stock", schema = "stock_stats")
public class Stock {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stock_id_gen")
	@SequenceGenerator(name = "stock_id_gen", sequenceName = "stock_stats.stocks_id_seq", allocationSize = 1)
	@Column(name = "id", nullable = false)
	private Integer id;

	@NotNull
	@Column(name = "symbol", nullable = false, length = Integer.MAX_VALUE)
	private String symbol;

	@Column(name = "name", length = Integer.MAX_VALUE)
	private String name;

	@Column(name = "reputation")
	private Short reputation;

}
