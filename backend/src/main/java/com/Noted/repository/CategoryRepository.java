package com.Noted.repository;

import com.Noted.model.Category;
import com.Noted.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByUserAndName(User user, String name);
    List<Category> findAllByUser(User user);
    void deleteById(Long id);
}
