package com.lerei.store.repositories;

import com.lerei.store.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepo extends JpaRepository<Category,Integer> {
    List<Category> findAllByOrderByViewIdAsc();
}
