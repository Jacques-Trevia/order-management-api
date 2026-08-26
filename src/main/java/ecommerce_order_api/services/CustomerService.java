package ecommerce_order_api.services;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ecommerce_order_api.dto.CustomerDTO;
import ecommerce_order_api.dto.CustomerInsertDTO;
import ecommerce_order_api.dto.CustomerUpdateDTO;
import ecommerce_order_api.entities.Customer;
import ecommerce_order_api.repositories.CustomerRepository;
import ecommerce_order_api.services.exceptions.DataBaseException;
import ecommerce_order_api.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;

@Service
public class CustomerService {

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private CustomerRepository repository;
	
	@Transactional(readOnly = true)
	public Page<CustomerDTO> findAllPaged(Pageable pageable) {
		Page<Customer> list = repository.findAll(pageable);
		return list.map(x -> new CustomerDTO(x));
	}
	
	@Transactional(readOnly = true)
	public CustomerDTO findById(Long id) {
		Optional<Customer> obj = repository.findById(id);
		Customer entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new CustomerDTO(entity);
	}
	
	@Transactional
	public CustomerDTO insert(CustomerInsertDTO dto) {
		Customer entity = new Customer();
		copyDtoToEntity(dto, entity);
		
		entity.setMoment(Instant.now());
		entity.setPassword(passwordEncoder.encode(dto.getPassword()));
		entity = repository.save(entity);
		return new CustomerDTO(entity);
	}
	
	@Transactional
	public CustomerDTO update(Long id, CustomerUpdateDTO dto) {
		try {
			Customer entity = repository.getReferenceById(id);
			copyDtoToEntity(dto, entity);
			entity = repository.save(entity);
			return new CustomerDTO(entity);
		} catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
	}
	
	@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
		try {
			repository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataBaseException("Integrity violation");
		}
	}
	
	private void copyDtoToEntity(CustomerDTO dto, Customer entity) {
		entity.setName(dto.getName());
		entity.setEmail(dto.getEmail());
		entity.setPhone(dto.getPhone());
	}
}
