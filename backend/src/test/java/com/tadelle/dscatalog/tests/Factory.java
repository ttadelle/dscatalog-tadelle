package com.tadelle.dscatalog.tests;

import java.time.Instant;

import com.tadelle.dscatalog.dto.ProductDTO;
import com.tadelle.dscatalog.entities.Category;
import com.tadelle.dscatalog.entities.Product;

public class Factory {

	public static Product createProduct() {
		Product product = new Product(1L,"Phone","Good Phone",800.0,"https://img.com/img.png",Instant.parse("2020-10-20T03:00:00Z"));
		product.getCategories().add(createdCategory());
		return product;
	}
	
	public static ProductDTO createProductDTO() {
		Product product = createProduct();
		return new ProductDTO(product, product.getCategories());
	}
	
	public static Category createdCategory() {
		return new Category(1L,"Electronics");
	}
}
