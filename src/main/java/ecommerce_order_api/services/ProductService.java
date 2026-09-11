package ecommerce_order_api.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ecommerce_order_api.dto.CategoryDTO;
import ecommerce_order_api.dto.ProductDTO;
import ecommerce_order_api.entities.Category;
import ecommerce_order_api.entities.Product;
import ecommerce_order_api.projections.ProductProjection;
import ecommerce_order_api.repositories.CategoryRepository;
import ecommerce_order_api.repositories.ProductRepository;
import ecommerce_order_api.services.exceptions.DataBaseException;
import ecommerce_order_api.services.exceptions.ResourceNotFoundException;
import ecommerce_order_api.utils.Util;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(String name,  String categoryId, Pageable pageable){
		
		List<Long> categoryIds = new ArrayList<>();
		if (!"0".equals(categoryId)) {
			categoryIds = Arrays.asList(categoryId.split(",")).stream().map(Long::parseLong).toList();
		}
		
		Page<ProductProjection> page = repository.searchProducts(categoryIds, name, pageable);
		List<Long> productIds = page.map(x -> x.getId()).toList();
		
		List<Product> entities = repository.searchProductsWithCategories(productIds);
		
		entities = (List<Product>) Util.replace(page.getContent(), entities);
		
		List<ProductDTO> dtos = entities.stream().map(p -> new ProductDTO(p, p.getCategories())).toList();
		
		return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
	}
	
	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Optional<Product> obj = repository.findById(id);
		Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new ProductDTO(entity, entity.getCategories());
	}
	
	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product entity = new Product();
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);
		return new ProductDTO(entity, entity.getCategories());
	}
	
	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {
		try {
			Product entity = repository.getReferenceById(id);
			copyDtoToEntity(dto, entity);
			entity = repository.save(entity);
			return new ProductDTO(entity, entity.getCategories());
		}
		catch(EntityNotFoundException e) {
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
		}
		catch (DataIntegrityViolationException e) {
			throw new DataBaseException("Integrity violation");
		}
	}
	
	private void copyDtoToEntity(ProductDTO dto, Product entity) {
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setPrice(dto.getPrice());
		entity.setStockQuantity(dto.getStockQuantity());

		entity.getCategories().clear();
		for (CategoryDTO catDto : dto.getCategories()) {
			try {
				Category category = categoryRepository.getReferenceById(catDto.getId());
				entity.getCategories().add(category);
			}
			catch (EntityNotFoundException e) {
				throw new ResourceNotFoundException("Category not found: " + catDto.getId());
			}
		}
	}
}
