package com.lerei.store.controllers;

import com.lerei.store.entities.Category;
import com.lerei.store.repositories.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {
    @Autowired
    private CategoryRepo categoryRepo;

    @GetMapping("/get-categories")
    public List<Category> getAllCategories(){
        return  categoryRepo.findAll();
    }
}
