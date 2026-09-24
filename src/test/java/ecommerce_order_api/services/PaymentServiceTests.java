package ecommerce_order_api.services;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import ecommerce_order_api.dto.OrderDTO;
import ecommerce_order_api.dto.PaymentDTO;
import ecommerce_order_api.dto.PaymentRequestDTO;
import ecommerce_order_api.entities.Order;
import ecommerce_order_api.entities.Payment;
import ecommerce_order_api.entities.enums.OrderStatus;
import ecommerce_order_api.entities.enums.PaymentStatus;
import ecommerce_order_api.factories.OrderFactory;
import ecommerce_order_api.factories.PaymentFactory;
import ecommerce_order_api.repositories.OrderRepository;
import ecommerce_order_api.repositories.PaymentRepository;
import ecommerce_order_api.services.exceptions.BusinessRuleException;
import ecommerce_order_api.services.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTests {

	@InjectMocks
	private PaymentService service;

	@Mock
	private PaymentRepository repository;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private OrderService orderService;

	private Long existingOrderId;
	private Long nonExistingOrderId;
	private Order order;
	private Payment payment;
	private PaymentRequestDTO paymentRequestDto;

	@BeforeEach
	void setUp() throws Exception {
		existingOrderId = 1L;
		nonExistingOrderId = 999L;

		order = OrderFactory.createOrder();
		payment = PaymentFactory.createPayment(order);
		paymentRequestDto = PaymentFactory.createPaymentRequestDTO();
	}
	
	@Test
	public void processPaymentShouldReturnPaymentDTOWhenOrderExistsAndHasNoPayment() {

		Mockito.when(orderRepository.findById(existingOrderId)).thenReturn(Optional.of(order));
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(payment);
		Mockito.when(orderService.updateStatus(ArgumentMatchers.eq(existingOrderId), ArgumentMatchers.eq(OrderStatus.PAID)))
				.thenReturn(new OrderDTO(order));

		PaymentDTO result = service.processPayment(existingOrderId, paymentRequestDto);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(PaymentStatus.APPROVED, result.getStatus());
		Assertions.assertEquals(paymentRequestDto.getMethod(), result.getMethod());
		Assertions.assertNotNull(result.getPaidAt());
		Mockito.verify(orderService, Mockito.times(1)).updateStatus(existingOrderId, OrderStatus.PAID);
	}
	
	@Test
	public void processPaymentShouldThrowResourceNotFoundExceptionWhenOrderDoesNotExist() {

		Mockito.when(orderRepository.findById(nonExistingOrderId)).thenReturn(Optional.empty());

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.processPayment(nonExistingOrderId, paymentRequestDto);
		});
	}
	
	@Test
	public void processPaymentShouldThrowBusinessRuleExceptionWhenOrderAlreadyHasPayment() {

		order.setPayment(payment);

		Mockito.when(orderRepository.findById(existingOrderId)).thenReturn(Optional.of(order));

		Assertions.assertThrows(BusinessRuleException.class, () -> {
			service.processPayment(existingOrderId, paymentRequestDto);
		});
	}
}