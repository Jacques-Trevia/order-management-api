package ecommerce_order_api.services.validations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import ecommerce_order_api.controllers.exceptions.FieldMessage;
import ecommerce_order_api.dto.CustomerUpdateDTO;
import ecommerce_order_api.entities.Customer;
import ecommerce_order_api.repositories.CustomerRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CustomerUpdateValidator implements ConstraintValidator<CustomerUpdateValid, CustomerUpdateDTO> {

	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private CustomerRepository repository;
	
	@Override
	public void initialize(CustomerUpdateValid ann) {
	}
	
	@Override
	public boolean isValid(CustomerUpdateDTO dto, ConstraintValidatorContext context) {
		
		@SuppressWarnings("unchecked")
		var uriVars = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		long customerId = Long.parseLong(uriVars.get("id"));
		
		List<FieldMessage> list = new ArrayList<>();
		
		Customer customer = repository.findByEmail(dto.getEmail());
		
		if (customer != null && customerId != customer.getId()) {
			list.add(new FieldMessage("email", "Email already exists"));
		}
		
		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName()).addConstraintViolation();
		}
		return list.isEmpty();
	} 
}
