package ecommerce_order_api.dto;

import java.time.Instant;

import ecommerce_order_api.entities.Customer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CustomerDTO {

	private Long id;
	
	@NotBlank(message = "Required field")
	private String name;
	
	@NotBlank(message = "Required field")
	@Email(message = "Please enter a valid email address.")
	private String email;
	
	@NotBlank(message = "Required field")
	private String phone;
	private Instant moment;
	
	public CustomerDTO() {
	}

	public CustomerDTO(Long id, String name, String email, String phone, Instant moment) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.moment = moment;
	}
	
	public CustomerDTO(Customer entity) {
		id = entity.getId();
		name = entity.getName();
		email = entity.getEmail();
		phone = entity.getPhone();
		moment = entity.getMoment();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Instant getMoment() {
		return moment;
	}

	public void setMoment(Instant moment) {
		this.moment = moment;
	}
}
