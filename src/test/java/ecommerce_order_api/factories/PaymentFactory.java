package ecommerce_order_api.factories;

import java.time.Instant;

import ecommerce_order_api.dto.PaymentRequestDTO;
import ecommerce_order_api.entities.Order;
import ecommerce_order_api.entities.Payment;
import ecommerce_order_api.entities.enums.PaymentMethod;
import ecommerce_order_api.entities.enums.PaymentStatus;

public class PaymentFactory {

	public static Payment createPayment(Order order) {
		Payment payment = new Payment();
		payment.setOrder(order);
		payment.setMethod(PaymentMethod.PIX);
		payment.setStatus(PaymentStatus.APPROVED);
		payment.setPaidAt(Instant.now());
		return payment;
	}

	public static PaymentRequestDTO createPaymentRequestDTO() {
		PaymentRequestDTO dto = new PaymentRequestDTO();
		dto.setMethod(PaymentMethod.PIX);
		return dto;
	}
}