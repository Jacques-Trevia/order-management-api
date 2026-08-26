package ecommerce_order_api.dto;

import java.math.BigDecimal;

import ecommerce_order_api.entities.OrderItem;

public class OrderItemDTO {

	private Long productId;
	private Integer quantity;
	private BigDecimal unitPrice;
	private BigDecimal subTotal;
	
	public OrderItemDTO() {
	}

	public OrderItemDTO(Long productId, Integer quantity, BigDecimal unitPrice, BigDecimal subTotal) {
		this.productId = productId;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.subTotal = subTotal;
	}
	
	public OrderItemDTO(OrderItem entity) {
		productId = entity.getProduct().getId();
		quantity = entity.getQuantity();
		unitPrice = entity.getUnitPrice();
		subTotal = entity.getSubTotal();
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	public BigDecimal getSubTotal() {
		return subTotal;
	}
}
