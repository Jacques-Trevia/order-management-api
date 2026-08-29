package ecommerce_order_api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ecommerce_order_api.dto.PaymentDTO;
import ecommerce_order_api.dto.PaymentRequestDTO;
import ecommerce_order_api.services.PaymentService;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/orders")
public class PaymentController {

	@Autowired
	private PaymentService service;
	
	@PostMapping(value = "/{id}/payment")
	public ResponseEntity<PaymentDTO> processPayment(@PathVariable Long id, @Valid @RequestBody PaymentRequestDTO dto) {
		PaymentDTO result = service.processPayment(id, dto);
		return ResponseEntity.ok().body(result);
	}
}
