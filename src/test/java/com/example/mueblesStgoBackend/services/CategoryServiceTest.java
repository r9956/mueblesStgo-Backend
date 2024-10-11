package com.example.mueblesStgoBackend.services;

import com.example.mueblesStgoBackend.entities.CategoryEntity;
import com.example.mueblesStgoBackend.repositories.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    CategoryService categoryServiceTest = new CategoryService();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenCategoryExists_thenReturnCategory() {
        String category = "A";
        CategoryEntity expectedCategoryEntity = new CategoryEntity();
        expectedCategoryEntity.setCategory(category);

        when(categoryRepository.findByCategory(category)).thenReturn(expectedCategoryEntity);

        // When
        CategoryEntity actualCategory = categoryServiceTest.findByCategory(category);

        // Then
        Assertions.assertEquals(expectedCategoryEntity, actualCategory);
    }

    @Test
    void whenCategoryDoesNotExist_thenReturnNull() {
        // Given
        String category = "Z";

        when(categoryRepository.findByCategory(category)).thenReturn(null);

        // When
        CategoryEntity actualCategoryEntity = categoryServiceTest.findByCategory(category);

        // Then
        Assertions.assertNull(actualCategoryEntity); // Retorna null
    }

}

