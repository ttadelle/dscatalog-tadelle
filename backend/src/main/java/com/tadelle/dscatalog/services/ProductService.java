package com.tadelle.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tadelle.dscatalog.dto.CategoryDTO;
import com.tadelle.dscatalog.dto.ProductDTO;
import com.tadelle.dscatalog.entities.Category;
import com.tadelle.dscatalog.entities.Product;
import com.tadelle.dscatalog.repositories.CategoryRepository;
import com.tadelle.dscatalog.repositories.ProductRepository;
import com.tadelle.dscatalog.services.exceptions.DatabaseException;
import com.tadelle.dscatalog.services.exceptions.ResourceNotFoundException;



@Service
public class ProductService {
	
	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Transactional(readOnly=true)
	public Page<ProductDTO> findAllPaged(Long categoryId, String name, Pageable pageable) {
		Category category = (categoryId == 0) ? null : categoryRepository.getOne(categoryId);
		Page<Product> pageList = repository.find(category, name.trim(), pageable);
		return pageList.map(x -> (new ProductDTO(x)));
	}
	
	@Transactional(readOnly=true)
	public ProductDTO findById(Long id) {
		Optional<Product> obj = repository.findById(id);
		Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Product not found"));	
		return new ProductDTO(entity, entity.getCategories());
	}
	
	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product entity = new Product();
		
		copyDtoToEntity(dto,entity);
		//entity.setName(dto.getName());
		entity = repository.save(entity);
		return new ProductDTO(entity);
	}
	
	@Transactional
	public ProductDTO update(Long id,ProductDTO dto) {
		try {
			Product entity = repository.getOne(id);
			
			copyDtoToEntity(dto,entity);
			//entity.setName(dto.getName());
			repository.save(entity);
			return new ProductDTO(entity);
		}
		catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
	}

	public void delete(Long id) {
		try {
			repository.deleteById(id);		
		} 
		catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
		catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
	}
	
	private void copyDtoToEntity(ProductDTO dto, Product entity) {
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setDate(dto.getDate());
		entity.setImgUrl(dto.getImgUrl());
		entity.setPrice(dto.getPrice());
		entity.getCategories().clear();
		for(CategoryDTO catDto : dto.getCategories()) {
			Category category = categoryRepository.getOne(catDto.getId());
			entity.getCategories().add(category);
		}
	}
}
