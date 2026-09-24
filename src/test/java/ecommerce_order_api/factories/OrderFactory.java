package ecommerce_order_api.factories;

import java.math.BigDecimal;
import java.time.Instant;

import ecommerce_order_api.entities.Customer;
import ecommerce_order_api.entities.Order;
import ecommerce_order_api.entities.OrderItem;
import ecommerce_order_api.entities.Product;
import ecommerce_order_api.entities.enums.OrderStatus;

public class OrderFactory {

	public static Order createOrder() {
		Customer customer = CustomerFactory.createCustomer();
		Order order = new Order(1L, Instant.now(), "Rua das Flores, 123", OrderStatus.WAITING_PAYMENT, customer);

		Product product = ProductFactory.createProduct();
		OrderItem item = new OrderItem(order, product, 2, new BigDecimal("3000.00"));
		order.getItems().add(item);

		return order;
	}

	public static Order createOrder(OrderStatus status) {
		Order order = createOrder();
		order.setStatus(status);
		return order;
	}
}