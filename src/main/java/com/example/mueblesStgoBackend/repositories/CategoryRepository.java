package com.example.mueblesStgoBackend.repositories;

import com.example.mueblesStgoBackend.entities.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    @Query(value = "SELECT * FROM category WHERE category = :category", nativeQuery = true)
    CategoryEntity findByCategory(@Param("category") String category);
}
