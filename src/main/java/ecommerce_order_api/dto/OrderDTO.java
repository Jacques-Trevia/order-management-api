package ecommerce_order_api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import ecommerce_order_api.entities.Order;
import ecommerce_order_api.entities.OrderItem;
import ecommerce_order_api.entities.enums.OrderStatus;
import jakarta.validation.constraints.NotEmpty;

public class OrderDTO {

	private Long id;
	private Instant moment;
	private String deliveryAddress;
	private OrderStatus status;
	private CustomerDTO customer;
	private BigDecimal total;
	
	@NotEmpty(message = "There must be at least one item")
	private List<OrderItemDTO> items = new ArrayList<>();
	
	public OrderDTO() {
	}

	public OrderDTO(Long id, Instant moment, String deliveryAddress, OrderStatus status, CustomerDTO customer, BigDecimal total) {
		this.id = id;
		this.moment = moment;
		this.deliveryAddress = deliveryAddress;
		this.status = status;
		this.customer = customer;
		this.total = total;
	}
	
	public OrderDTO(Order entity) {
		id = entity.getId();
		moment = entity.getMoment();
		deliveryAddress = entity.getDeliveryAddress();
		status = entity.getStatus();
		customer = new CustomerDTO(entity.getCustomer());
		total = entity.getTotal();
		
		for (OrderItem item : entity.getItems()) {
			OrderItemDTO itemDto = new OrderItemDTO(item);
			items.add(itemDto);
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Instant getMoment() {
		return moment;
	}

	public void setMoment(Instant moment) {
		this.moment = moment;
	}

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public CustomerDTO getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerDTO customer) {
		this.customer = customer;
	}

	public List<OrderItemDTO> getItems() {
		return items;
	}
	
	public BigDecimal getTotal() {
		return total;
	}
}
