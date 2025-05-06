package com.classpathio.order.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name="line_items")

@Data
@EqualsAndHashCode(exclude = "order")
@ToString(exclude = "order")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LineItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String name;
	private int qty;
	private double price;
	
	@ManyToOne
	@JoinColumn(name="order_id", nullable = false)
	@JsonBackReference
	private Order order;
}
