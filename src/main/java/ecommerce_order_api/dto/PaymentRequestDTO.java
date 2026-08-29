package ecommerce_order_api.dto;

import ecommerce_order_api.entities.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public class PaymentRequestDTO {

	@NotNull(message = "Payment method is required")
	private PaymentMethod method;

	public PaymentMethod getMethod() {
		return method;
	}

	public void setMethod(PaymentMethod method) {
		this.method = method;
	}
}
