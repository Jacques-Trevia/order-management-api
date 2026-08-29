package ecommerce_order_api.dto;

import java.time.Instant;

import ecommerce_order_api.entities.Payment;
import ecommerce_order_api.entities.enums.PaymentMethod;
import ecommerce_order_api.entities.enums.PaymentStatus;

public class PaymentDTO {

	private Long id;
	private PaymentMethod method;
	private PaymentStatus status;
	private Instant paidAt;
	
	public PaymentDTO() {
	}

	public PaymentDTO(Long id, PaymentMethod method, PaymentStatus status, Instant paidAt) {
		this.id = id;
		this.method = method;
		this.status = status;
		this.paidAt = paidAt;
	}
	
	public PaymentDTO(Payment entity) {
		id = entity.getId();
		method = entity.getMethod();
		status = entity.getStatus();
		paidAt = entity.getPaidAt();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PaymentMethod getMethod() {
		return method;
	}

	public void setMethod(PaymentMethod method) {
		this.method = method;
	}

	public PaymentStatus getStatus() {
		return status;
	}

	public void setStatus(PaymentStatus status) {
		this.status = status;
	}

	public Instant getPaidAt() {
		return paidAt;
	}

	public void setPaidAt(Instant paidAt) {
		this.paidAt = paidAt;
	}
}
