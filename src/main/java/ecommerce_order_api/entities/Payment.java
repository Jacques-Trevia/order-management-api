package ecommerce_order_api.entities;

import java.time.Instant;
import java.util.Objects;

import ecommerce_order_api.entities.enums.PaymentMethod;
import ecommerce_order_api.entities.enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_payment")
public class Payment {

	@Id
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private PaymentMethod method;
	
	@Enumerated(EnumType.STRING)
	private PaymentStatus status;

	@Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
	private Instant paidAt;

	@OneToOne
	@MapsId
	@JoinColumn(name = "id")
	private Order order;

	public Payment() {
	}

	public Payment(PaymentMethod method, PaymentStatus status, Instant paidAt, Order order) {
		this.method = method;
		this.status = status;
		this.paidAt = paidAt;
		this.order = order;
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

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Payment other = (Payment) obj;
		return Objects.equals(id, other.id);
	}
}
