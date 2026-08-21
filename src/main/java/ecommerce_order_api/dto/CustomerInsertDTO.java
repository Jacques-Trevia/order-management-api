package ecommerce_order_api.dto;

import ecommerce_order_api.services.validations.CustomerInsertValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@CustomerInsertValid
public class CustomerInsertDTO extends CustomerDTO {

	@NotBlank(message = "Required field")
	@Size(min = 8, message = "It must be at least 8 characters long")
	private String password;
	
	public CustomerInsertDTO() {
		super();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
