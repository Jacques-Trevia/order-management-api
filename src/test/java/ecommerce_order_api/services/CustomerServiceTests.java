package ecommerce_order_api.services;

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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import ecommerce_order_api.dto.CustomerDTO;
import ecommerce_order_api.dto.CustomerInsertDTO;
import ecommerce_order_api.dto.CustomerUpdateDTO;
import ecommerce_order_api.entities.Customer;
import ecommerce_order_api.factories.CustomerFactory;
import ecommerce_order_api.repositories.CustomerRepository;
import ecommerce_order_api.services.exceptions.DataBaseException;
import ecommerce_order_api.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTests {

	@InjectMocks
	private CustomerService service;

	@Mock
	private CustomerRepository repository;

	@Mock
	private PasswordEncoder passwordEncoder;

	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;
	private Customer customer;
	private CustomerDTO customerDto;
	private CustomerInsertDTO customerInsertDto;
	private CustomerUpdateDTO customerUpdateDto;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 999L;
		dependentId = 2L;
		customer = CustomerFactory.createCustomer();
		customerDto = new CustomerDTO(customer);
		customerInsertDto = CustomerFactory.createCustomerInsertDTO();
		customerUpdateDto = CustomerFactory.createCustomerUpdateDTO();
	}
	
	@Test
	public void findAllPagedShouldReturnPage() {

		Pageable pageable = PageRequest.of(0, 10);
		Page<Customer> page = new PageImpl<>(List.of(customer));

		Mockito.when(repository.findAll(pageable)).thenReturn(page);

		Page<CustomerDTO> result = service.findAllPaged(pageable);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(1, result.getContent().size());
		Assertions.assertEquals(customer.getName(), result.getContent().get(0).getName());
	}
	
	@Test
	public void findByIdShouldReturnCustomerDTOWhenIdExists() {

		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(customer));

		CustomerDTO result = service.findById(existingId);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(existingId, result.getId());
		Assertions.assertEquals(customer.getName(), result.getName());
		Assertions.assertEquals(customer.getEmail(), result.getEmail());
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});
	}
	
	@Test
	public void insertShouldReturnCustomerDTO() {

		Mockito.when(passwordEncoder.encode(ArgumentMatchers.any())).thenReturn("hashed_password");
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(customer);

		CustomerDTO result = service.insert(customerInsertDto);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(customer.getId(), result.getId());
		Assertions.assertEquals(customer.getName(), result.getName());
		Assertions.assertEquals(customer.getEmail(), result.getEmail());
		Assertions.assertEquals(customer.getPhone(), result.getPhone());
		Mockito.verify(passwordEncoder, Mockito.times(1)).encode(customerInsertDto.getPassword());
	}
	
	@Test
	public void updateShouldReturnCustomerDTOWhenIdExists() {

		Mockito.when(repository.getReferenceById(existingId)).thenReturn(customer);
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(customer);

		CustomerDTO result = service.update(existingId, customerUpdateDto);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(existingId, result.getId());
		Assertions.assertEquals(customer.getName(), result.getName());
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		
		Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
	
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingId, customerUpdateDto);
		} );
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		
		Mockito.when(repository.existsById(existingId)).thenReturn(true);
		Mockito.doNothing().when(repository).deleteById(existingId);
		
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
	
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		
		Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
	}
	
	@Test
	public void deleteShouldThrowDataBaseExceptionWhenDependentId() {
		
		Mockito.when(repository.existsById(dependentId)).thenReturn(true);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
		
		Assertions.assertThrows(DataBaseException.class, () -> {
			service.delete(dependentId);
		});
	}
}
