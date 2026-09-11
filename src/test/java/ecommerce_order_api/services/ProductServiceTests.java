package ecommerce_order_api.services;

import java.util.ArrayList;
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

import ecommerce_order_api.dto.CategoryDTO;
import ecommerce_order_api.dto.ProductDTO;
import ecommerce_order_api.entities.Category;
import ecommerce_order_api.entities.Product;
import ecommerce_order_api.factories.CategoryFactory;
import ecommerce_order_api.factories.ProductFactory;
import ecommerce_order_api.projections.ProductProjection;
import ecommerce_order_api.projections.ProductProjectionImpl;
import ecommerce_order_api.repositories.CategoryRepository;
import ecommerce_order_api.repositories.ProductRepository;
import ecommerce_order_api.services.exceptions.DataBaseException;
import ecommerce_order_api.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTests {

	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	@Mock
	private CategoryRepository categoryRepository;
	
	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;
	private Product product;
	private ProductDTO productDto;
	private Category category;
	
	@BeforeEach
	void setUp() throws Exception{
		existingId = 1L;
		nonExistingId = 999L;
		dependentId = 2L;
		product = ProductFactory.createProduct();
		category = CategoryFactory.createCategory();
		productDto = new ProductDTO(product, product.getCategories());
	}
	
	@Test
	public void findAllPagedShouldReturnPage() {

		List<Long> categoryIds = new ArrayList<>();
		Pageable pageable = PageRequest.of(0, 10);
		
		List<ProductProjection> projectionList = List.of(new ProductProjectionImpl(existingId, product.getName()));
		Page<ProductProjection> projectionPage = new PageImpl<>(projectionList, pageable, 1);
		
		Mockito.when(repository.searchProducts(categoryIds, "", pageable)).thenReturn(projectionPage);
		Mockito.when(repository.searchProductsWithCategories(List.of(existingId))).thenReturn(List.of(product));
		
		Page<ProductDTO> result = service.findAllPaged("", "0", pageable);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(1, result.getContent().size());
		Assertions.assertEquals(existingId, result.getContent().get(0).getId());
	}
	
	@Test
	public void findByIdShouldReturnProductDTOWhenIdExists() {
		
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
		
		ProductDTO result = service.findById(existingId);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(existingId, result.getId());
		Assertions.assertEquals(product.getName(), result.getName());
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});
	}
	
	@Test
	public void insertShouldReturnProductDTO() {
		
		Mockito.when(categoryRepository.getReferenceById(category.getId())).thenReturn(category);
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		
		ProductDTO result = service.insert(productDto);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(product.getName(), result.getName());
		Assertions.assertEquals(product.getDescription(), result.getDescription());
		Assertions.assertEquals(product.getPrice(), result.getPrice());
		Assertions.assertEquals(product.getStockQuantity(), result.getStockQuantity());
		Assertions.assertFalse(result.getCategories().isEmpty());
	}
	
	@Test
	public void insertShouldThrowResourceNotFoundExceptionWhenCategoryIdDoesNotExist() {

		Mockito.when(categoryRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

		CategoryDTO invalidCategoryDto = new CategoryDTO(nonExistingId, "Categoria Inexistente");
		ProductDTO dto = new ProductDTO(product);
		dto.getCategories().add(invalidCategoryDto);

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.insert(dto);
		});
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() {

		Mockito.when(categoryRepository.getReferenceById(category.getId())).thenReturn(category);
		Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

		ProductDTO result = service.update(existingId, productDto);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(existingId, result.getId());
		Assertions.assertEquals(product.getName(), result.getName());
		Assertions.assertEquals(product.getPrice(), result.getPrice());
		Assertions.assertEquals(product.getStockQuantity(), result.getStockQuantity());
		Assertions.assertFalse(result.getCategories().isEmpty());
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
				
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingId, productDto);
		});
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
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenCategoryIdDoesNotExist() {

		Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
		Mockito.when(categoryRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

		CategoryDTO invalidCategoryDto = new CategoryDTO(nonExistingId, "Categoria Inexistente");
		ProductDTO dto = new ProductDTO(product);
		dto.getCategories().add(invalidCategoryDto);

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(existingId, dto);
		});
	}
}
