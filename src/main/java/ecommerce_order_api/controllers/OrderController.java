package ecommerce_order_api.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import ecommerce_order_api.dto.OrderDTO;
import ecommerce_order_api.entities.enums.OrderStatus;
import ecommerce_order_api.services.OrderService;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/orders")
public class OrderController {

	@Autowired
	private OrderService service;

	@GetMapping
	public ResponseEntity<Page<OrderDTO>> findAllPaged(Pageable pageable) {
		Page<OrderDTO> list = service.findAllPaged(pageable);
		return ResponseEntity.ok().body(list);
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<OrderDTO> findById(@PathVariable Long id) {
		OrderDTO dto =  service.findById(id);
		return ResponseEntity.ok().body(dto);
	}
	
	@PostMapping
	public ResponseEntity<OrderDTO> insert(@Valid @RequestBody OrderDTO dto) {
		dto = service.insert(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
		return ResponseEntity.created(uri).body(dto);
	}
	
	@PatchMapping(value = "/{id}/status")
	public ResponseEntity<OrderDTO> updateStatus(@PathVariable Long id, @RequestBody OrderStatus newStatus) {
		OrderDTO dto = service.updateStatus(id, newStatus);
		return ResponseEntity.ok().body(dto);
	}

	@PatchMapping(value = "/{id}/cancel")
	public ResponseEntity<OrderDTO> cancel(@PathVariable Long id) {
		OrderDTO dto = service.cancel(id);
		return ResponseEntity.ok().body(dto);
	}
}
