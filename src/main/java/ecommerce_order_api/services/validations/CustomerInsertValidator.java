package ecommerce_order_api.services.validations;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ecommerce_order_api.controllers.exceptions.FieldMessage;
import ecommerce_order_api.dto.CustomerInsertDTO;
import ecommerce_order_api.entities.Customer;
import ecommerce_order_api.repositories.CustomerRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CustomerInsertValidator implements ConstraintValidator<CustomerInsertValid, CustomerInsertDTO> {

	@Autowired
	private CustomerRepository repository;
	
	@Override
	public void initialize(CustomerInsertValid ann) {
	}
	
	@Override
	public boolean isValid(CustomerInsertDTO dto, ConstraintValidatorContext context) {
		
		List<FieldMessage> list = new ArrayList<>();
		
		Customer customer = repository.findByEmail(dto.getEmail());
		
		if (customer != null) {
			list.add(new FieldMessage("email", "Email already exists"));
		}
		
		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName()).addConstraintViolation();
		}
		return list.isEmpty();
	}
}
