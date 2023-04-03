package com.tadelle.dscatalog.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tadelle.dscatalog.entities.Category;
import com.tadelle.dscatalog.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	
	@Query("SELECT DISTINCT obj "
			+ "FROM Product obj "
			+ "INNER JOIN obj.categories cats "
			+ "WHERE (:category IS NULL OR cats IN :category) "
			+ "AND UPPER(obj.name) LIKE UPPER(CONCAT('%',:name,'%'))")
	Page<Product> find(Category category, String name, Pageable pagealbe);

}	
