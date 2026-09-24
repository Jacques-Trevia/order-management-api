package ecommerce_order_api.services;

import java.util.ArrayList;
import java.util.List;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import ecommerce_order_api.dto.OrderDTO;
import ecommerce_order_api.entities.Customer;
import ecommerce_order_api.entities.Order;
import ecommerce_order_api.entities.Product;
import ecommerce_order_api.entities.enums.OrderStatus;
import ecommerce_order_api.factories.CustomerFactory;
import ecommerce_order_api.factories.OrderFactory;
import ecommerce_order_api.factories.ProductFactory;
import ecommerce_order_api.repositories.CustomerRepository;
import ecommerce_order_api.repositories.OrderItemRepository;
import ecommerce_order_api.repositories.OrderRepository;
import ecommerce_order_api.repositories.ProductRepository;
import ecommerce_order_api.services.exceptions.BusinessRuleException;
import ecommerce_order_api.services.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTests {

	@InjectMocks
	private OrderService service;

	@Mock
	private OrderRepository repository;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private OrderItemRepository orderItemRepository;

	private Long existingOrderId;
	private Long nonExistingOrderId;
	private Long existingCustomerId;
	private Long existingProductId;
	private Order order;
	private OrderDTO orderDto;
	private Customer customer;
	private Product product;

	@BeforeEach
	void setUp() throws Exception {
		existingOrderId = 1L;
		nonExistingOrderId = 999L;
		existingCustomerId = 1L;
		existingProductId = 1L;

		order = OrderFactory.createOrder();
		orderDto = new OrderDTO(order);
		customer = CustomerFactory.createCustomer();
		product = ProductFactory.createProduct();
	}
	
	@Test
	public void findAllPagedShouldReturnPage() {

		Pageable pageable = PageRequest.of(0, 10);
		Page<Order> page = new PageImpl<>(List.of(order), pageable, 1);

		Mockito.when(repository.findAll(pageable)).thenReturn(page);
		Mockito.when(repository.searchOrdersWithItems(List.of(existingOrderId))).thenReturn(List.of(order));

		Page<OrderDTO> result = service.findAllPaged(pageable);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(1, result.getContent().size());
		Assertions.assertEquals(existingOrderId, result.getContent().get(0).getId());
	}
	
	@Test
	public void findByIdShouldReturnOrderDTOWhenIdExists() {

		Mockito.when(repository.findById(existingOrderId)).thenReturn(Optional.of(order));

		OrderDTO result = service.findById(existingOrderId);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(existingOrderId, result.getId());
		Assertions.assertEquals(order.getStatus(), result.getStatus());
	}

	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

		Mockito.when(repository.findById(nonExistingOrderId)).thenReturn(Optional.empty());

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingOrderId);
		});
	}
	
	@Test
	public void insertShouldReturnOrderDTO() {

		Mockito.when(customerRepository.getReferenceById(existingCustomerId)).thenReturn(customer);
		Mockito.when(productRepository.getReferenceById(existingProductId)).thenReturn(product);
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(order);
		Mockito.when(orderItemRepository.saveAll(ArgumentMatchers.any())).thenReturn(new ArrayList<>(order.getItems()));

		OrderDTO result = service.insert(orderDto);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(OrderStatus.WAITING_PAYMENT, result.getStatus());
		Assertions.assertEquals(order.getDeliveryAddress(), result.getDeliveryAddress());
		Assertions.assertFalse(result.getItems().isEmpty());
		Assertions.assertEquals(1, result.getItems().size());
	}
	
	@Test
	public void insertShouldThrowBusinessRuleExceptionWhenInsufficientStock() {

		Product lowStockProduct = ProductFactory.createProduct();
		lowStockProduct.setStockQuantity(1);

		Mockito.when(customerRepository.getReferenceById(existingCustomerId)).thenReturn(customer);
		Mockito.when(productRepository.getReferenceById(existingProductId)).thenReturn(lowStockProduct);

		Assertions.assertThrows(BusinessRuleException.class, () -> {
			service.insert(orderDto);
		});
	}
	
	@Test
	public void updateStatusShouldReturnOrderDTOWhenTransitionIsValid() {

		Mockito.when(repository.findById(existingOrderId)).thenReturn(Optional.of(order));
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(order);

		OrderDTO result = service.updateStatus(existingOrderId, OrderStatus.PAID);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(OrderStatus.PAID, result.getStatus());
		Mockito.verify(repository, Mockito.times(1)).save(ArgumentMatchers.any());
	}
	
	@Test
	public void updateStatusShouldThrowBusinessRuleExceptionWhenTransitionIsInvalid() {

		Mockito.when(repository.findById(existingOrderId)).thenReturn(Optional.of(order));

		Assertions.assertThrows(BusinessRuleException.class, () -> {
			service.updateStatus(existingOrderId, OrderStatus.DELIVERED);
		});
	}
	
	@Test
	public void cancelShouldReturnOrderDTOAndRestoreStockWhenStatusAllowsCancel() {

		Mockito.when(repository.findById(existingOrderId)).thenReturn(Optional.of(order));
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(order);

		Product orderedProduct = order.getItems().iterator().next().getProduct();
		Integer stockBeforeCancel = orderedProduct.getStockQuantity();
		Integer orderedQuantity = order.getItems().iterator().next().getQuantity();

		OrderDTO result = service.cancel(existingOrderId);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(OrderStatus.CANCELED, result.getStatus());
		Assertions.assertEquals(stockBeforeCancel + orderedQuantity, orderedProduct.getStockQuantity());
	}
	
	@Test
	public void cancelShouldThrowBusinessRuleExceptionWhenStatusDoesNotAllowCancel() {

		Order deliveredOrder = OrderFactory.createOrder(OrderStatus.DELIVERED);
		Mockito.when(repository.findById(existingOrderId)).thenReturn(Optional.of(deliveredOrder));

		Assertions.assertThrows(BusinessRuleException.class, () -> {
			service.cancel(existingOrderId);
		});
	}
}