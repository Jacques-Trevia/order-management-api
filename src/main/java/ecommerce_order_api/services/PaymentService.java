package ecommerce_order_api.services;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ecommerce_order_api.dto.PaymentDTO;
import ecommerce_order_api.dto.PaymentRequestDTO;
import ecommerce_order_api.entities.Order;
import ecommerce_order_api.entities.Payment;
import ecommerce_order_api.entities.enums.OrderStatus;
import ecommerce_order_api.entities.enums.PaymentStatus;
import ecommerce_order_api.repositories.OrderRepository;
import ecommerce_order_api.repositories.PaymentRepository;
import ecommerce_order_api.services.exceptions.BusinessRuleException;
import ecommerce_order_api.services.exceptions.ResourceNotFoundException;

@Service
public class PaymentService {

	@Autowired
	private PaymentRepository repository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private OrderService orderService;
	
	@Transactional
	public PaymentDTO processPayment(Long orderId, PaymentRequestDTO requestDto) {
		
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found"));
		
		if(order.getPayment() != null) {
			throw new BusinessRuleException("This order already has a payment");
		}
		
		Payment payment = new Payment();
		payment.setOrder(order);
		payment.setMethod(requestDto.getMethod());
		payment.setStatus(PaymentStatus.APPROVED);
		payment.setPaidAt(Instant.now());
		
		payment = repository.save(payment);
		
		orderService.updateStatus(orderId, OrderStatus.PAID);
		
		return new PaymentDTO(payment);
	}
	
}
