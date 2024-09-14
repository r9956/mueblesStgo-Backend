package com.example.mueblesStgoBackend.services;

import com.example.mueblesStgoBackend.entities.CategoryEntity;
import com.example.mueblesStgoBackend.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    public CategoryEntity findByCategory(String category){
        return categoryRepository.findByCategory(category);
    }
}
