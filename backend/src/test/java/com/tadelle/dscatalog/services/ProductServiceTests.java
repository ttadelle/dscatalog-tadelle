package com.tadelle.dscatalog.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.tadelle.dscatalog.dto.ProductDTO;
import com.tadelle.dscatalog.entities.Category;
import com.tadelle.dscatalog.entities.Product;
import com.tadelle.dscatalog.repositories.CategoryRepository;
import com.tadelle.dscatalog.repositories.ProductRepository;
import com.tadelle.dscatalog.services.exceptions.DatabaseException;
import com.tadelle.dscatalog.services.exceptions.ResourceNotFoundException;
import com.tadelle.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	@Mock
	private CategoryRepository categoryRepository;
	
	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private Product product;
	private ProductDTO productDTO;
	private Category category;
	private PageImpl<Product> page;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 2L;
		dependentId = 3L;
		product = Factory.createProduct();
		productDTO = Factory.createProductDTO();
		category = Factory.createdCategory();
		
		page = new PageImpl<>(List.of(product));
		
		Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
		
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		
		Mockito.when(repository.find(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(page);

		Mockito.when(repository.getOne(existingId)).thenReturn(product);
		Mockito.when(repository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);
		
		Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);
		Mockito.when(categoryRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);
		
		Mockito.doNothing().when(repository).deleteById(existingId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
	}
	
	
	@Test
	public void findAllPagedShouldReturnPage() {
		
		Pageable pageable = PageRequest.of(0,10);
		
		Page<ProductDTO> result = service.findAllPaged(0L,"",pageable);
		
		Assertions.assertNotNull(result);

	}
	
	@Test
	public void findByIdShouldReturnIdWhenIdExists() {
		
		ProductDTO entity = service.findById(existingId);
		
		Assertions.assertNotNull(entity);
	}
	
	@Test
	public void findByIdShouldThrowExceptiondWhenDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingId));
	}
	
	@Test
	public void updateShouldReturnProductWhenIdExists() {
				
		ProductDTO entity = service.update(existingId, productDTO);
		Assertions.assertNotNull(entity);
	}
	
	@Test
	public void updateShouldThrowExceptiondWhenDoesNotExists() {
				
		Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(nonExistingId, productDTO));
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		
		Assertions.assertThrows(DatabaseException.class,() -> {
			service.delete(dependentId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class,() -> {
			service.delete(nonExistingId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
	}
}
