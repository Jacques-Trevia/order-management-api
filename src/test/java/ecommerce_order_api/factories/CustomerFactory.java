package ecommerce_order_api.factories;

import java.time.Instant;

import ecommerce_order_api.dto.CustomerInsertDTO;
import ecommerce_order_api.dto.CustomerUpdateDTO;
import ecommerce_order_api.entities.Customer;

public class CustomerFactory {

	public static Customer createCustomer() {
		return new Customer(1L, "João Silva", "joao@email.com", "$2a$10$hashfake", "(11) 99999-8888", Instant.now());
	}
	
	public static CustomerInsertDTO createCustomerInsertDTO() {
		CustomerInsertDTO dto = new CustomerInsertDTO();
		dto.setName("Maria Silva");
		dto.setEmail("maria@email.com");
		dto.setPhone("(11) 98888-7777");
		dto.setPassword("senha123");
		return dto;
	}
	
	public static CustomerUpdateDTO createCustomerUpdateDTO() {
		CustomerUpdateDTO dto = new CustomerUpdateDTO();
		dto.setName("Maria Silva Santos");
		dto.setEmail("maria@email.com");
		dto.setPhone("(11) 91234-5678");
		return dto;
	}
}