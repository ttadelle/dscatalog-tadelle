package com.tadelle.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.tadelle.dscatalog.entities.Product;
import com.tadelle.dscatalog.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {
	
	@Autowired
	private ProductRepository repository;
	
	private long existingId;
	private long nonExistsId;
	private long countTotalProducts;
	
	@BeforeEach
	void setup() throws Exception {
		existingId = 1L;
		nonExistsId = 1000L;
		countTotalProducts = 25L;
	}
	
	@Test
	public void findByIdShouldreturnOptionalProductWhenIdExists() {
		
		Optional<Product> obj = repository.findById(existingId);
		Assertions.assertTrue(obj.isPresent());
	}
	
	@Test
	public void findByIdShouldreturnEmptyOptionalWhenIdDoesNotExists() {
		
		Optional<Product> obj = repository.findById(nonExistsId);
		Assertions.assertTrue(obj.isEmpty());
	}
	
	@Test
	public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
		Product product = Factory.createProduct();
		product.setId(null);
		repository.save(product);
		
		Assertions.assertNotNull(product);
		Assertions.assertEquals(countTotalProducts +1, product.getId());
	}
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {
		
		repository.deleteById(existingId);
		
		Optional<Product> result =  repository.findById(existingId);
		Assertions.assertFalse(result.isPresent());
	}
	
	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(nonExistsId);
			});
	}
	
}
