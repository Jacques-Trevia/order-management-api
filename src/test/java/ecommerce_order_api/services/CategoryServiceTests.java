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

import ecommerce_order_api.dto.CategoryDTO;
import ecommerce_order_api.entities.Category;
import ecommerce_order_api.factories.CategoryFactory;
import ecommerce_order_api.repositories.CategoryRepository;
import ecommerce_order_api.services.exceptions.DataBaseException;
import ecommerce_order_api.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTests {

	@InjectMocks
	private CategoryService service;
	
	@Mock
	private CategoryRepository repository;
	
	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;
	private Category category;
	private CategoryDTO categoryDto;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 999L;
		dependentId = 2L;
		category = CategoryFactory.createCategory();
		categoryDto = new CategoryDTO(category);
	}
	
	
	@Test
	public void findAllShouldReturnListOfCategoryDTO() {
		
		List<Category> list = List.of(category);
		Mockito.when(repository.findAll()).thenReturn(list);
		
		List<CategoryDTO> result = service.findAll();
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(1, result.size());
		Assertions.assertEquals(category.getName(), result.get(0).getName());
	}
	
	@Test
	public void findAllShouldReturnEmptyListWhenNoCategoriesExist() {

		Mockito.when(repository.findAll()).thenReturn(List.of());

		List<CategoryDTO> result = service.findAll();

		Assertions.assertNotNull(result);
		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void findByIdShouldReturnCategoryDTOwhenIdExists() {
		
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(category));
		
		CategoryDTO result = service.findById(existingId);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(existingId, result.getId());
		Assertions.assertEquals(category.getName(), result.getName());
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});
	}
	
	@Test
	public void insertShouldReturnCategoryDTO() {
		
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(category);
		
		CategoryDTO result = service.insert(categoryDto);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(category.getName(), result.getName());
	}
	
	@Test
	public void insertShouldThrowDataBaseExceptionWhenNameAlreadyExists() {

		Mockito.when(repository.save(ArgumentMatchers.any())).thenThrow(DataIntegrityViolationException.class);

		CategoryDTO dto = new CategoryDTO(null, "Eletrônicos");

		Assertions.assertThrows(DataBaseException.class, () -> {
			service.insert(dto);
		});
	}
	
	@Test
	public void updateShouldReturnCategoryDTOWhenIdExists() {
		
		Mockito.when(repository.getReferenceById(existingId)).thenReturn(category);
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(category);
		
		CategoryDTO dto = new CategoryDTO(null, "New name");
		
		CategoryDTO result = service.update(existingId, dto);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(existingId, result.getId());
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		
		Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
		
		CategoryDTO dto = new CategoryDTO(null, "New name");
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingId, dto);
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
}
