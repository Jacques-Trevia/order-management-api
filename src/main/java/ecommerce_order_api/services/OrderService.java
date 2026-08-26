package ecommerce_order_api.services;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ecommerce_order_api.dto.OrderDTO;
import ecommerce_order_api.dto.OrderItemDTO;
import ecommerce_order_api.entities.Customer;
import ecommerce_order_api.entities.Order;
import ecommerce_order_api.entities.OrderItem;
import ecommerce_order_api.entities.Product;
import ecommerce_order_api.entities.enums.OrderStatus;
import ecommerce_order_api.repositories.CustomerRepository;
import ecommerce_order_api.repositories.OrderItemRepository;
import ecommerce_order_api.repositories.OrderRepository;
import ecommerce_order_api.repositories.ProductRepository;
import ecommerce_order_api.services.exceptions.BusinessRuleException;
import ecommerce_order_api.services.exceptions.ResourceNotFoundException;
import ecommerce_order_api.utils.Util;

@Service
public class OrderService {

	@Autowired
	private OrderRepository repository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private OrderItemRepository orderItemRepository;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Page<OrderDTO> findAllPaged(Pageable pageable) {

		Page<Order> page = repository.findAll(pageable);
		List<Long> orderIds = page.map(x -> x.getId()).toList();

		List<Order> entities = repository.searchOrdersWithItems(orderIds);
		entities = (List<Order>) Util.replace(page.getContent(), entities);

		List<OrderDTO> dtos = entities.stream().map(OrderDTO::new).toList();

		return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
	}
	
	@Transactional(readOnly = true)
	public OrderDTO findById(Long id) {
		Optional<Order> obj = repository.findById(id);
		Order entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new OrderDTO(entity);
	}
	
	@Transactional
	public OrderDTO insert(OrderDTO dto) {
		
		Order order = new Order();
		
		order.setMoment(Instant.now());
		order.setDeliveryAddress(dto.getDeliveryAddress());
		order.setStatus(OrderStatus.WAITING_PAYMENT);
		
		Customer customer = customerRepository.getReferenceById(dto.getCustomer().getId());
		order.setCustomer(customer);
		
		for (OrderItemDTO itemDto : dto.getItems()) {
			Product product = productRepository.getReferenceById(itemDto.getProductId());
			
			if (product.getStockQuantity() < itemDto.getQuantity()) {
				throw new BusinessRuleException("Insufficient stock for product: " + product.getName());
			}
			
			OrderItem item = new OrderItem(order, product, itemDto.getQuantity(), product.getPrice());
			order.getItems().add(item);
			
			product.setStockQuantity(product.getStockQuantity() - itemDto.getQuantity());
		}
		
		order = repository.save(order);
		orderItemRepository.saveAll(order.getItems());
		
		return new OrderDTO(order);
	}
	
	@Transactional
	public OrderDTO updateStatus(Long id, OrderStatus newStatus) {
		Order order = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found"));

		if (!order.getStatus().canTransitionTo(newStatus)) {
			throw new BusinessRuleException(
				"Cannot change order status from " + order.getStatus() + " to " + newStatus);
		}

		order.setStatus(newStatus);
		order = repository.save(order);
		return new OrderDTO(order);
	}
	
	@Transactional
	public OrderDTO cancel(Long id) {
		Order order = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found"));

		if (!order.getStatus().canTransitionTo(OrderStatus.CANCELED)) {
			throw new BusinessRuleException("Order cannot be canceled in current status: " + order.getStatus());
		}

		for (OrderItem item : order.getItems()) {
			Product product = item.getProduct();
			product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
		}

		order.setStatus(OrderStatus.CANCELED);
		order = repository.save(order);
		return new OrderDTO(order);
	}
}
