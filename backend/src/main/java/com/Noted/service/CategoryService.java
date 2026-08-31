package com.Noted.service;

import com.Noted.exception.CategoryAlreadyExistsException;
import com.Noted.exception.NoteNotFoundException;
import com.Noted.model.Category;
import com.Noted.model.User;
import com.Noted.repository.CategoryRepository;
import com.Noted.repository.NoteRepository;
import com.Noted.response.CategoryNoteCount;
import com.Noted.response.CategoryResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final NoteRepository noteRepository;

    public CategoryService(CategoryRepository categoryRepository, UserService userService, NoteRepository noteRepository) {
        this.categoryRepository = categoryRepository;
        this.userService = userService;
        this.noteRepository = noteRepository;
    }

    public Category addCategory(String token, String name){
        User user = userService.getUserFromToken(token);

        Category newCategory = new Category();
        newCategory.setUser(user);
        newCategory.setName(name);

        if(categoryRepository.existsByUserAndName(user, name)){
            throw new CategoryAlreadyExistsException("You already have a category named: " + name);
        }

        return categoryRepository.save(newCategory);

    }

    public List<CategoryResponse> getAllCategories(String token) {
        User user = userService.getUserFromToken(token);
        List<Category> categories = categoryRepository.findAllByUser(user);

        return categories.stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public Category getCategoryById(String token, Long categoryId) {
        User user = userService.getUserFromToken(token);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoteNotFoundException("Category with that id cannot be found."));

        if(!category.getUser().getId().equals(user.getId())){
            throw new NoteNotFoundException("Category with that id cannot be found.");
        }

        return category;
    }

    public Category updateCategory(String newName, String token, Long id){
        User user = userService.getUserFromToken(token);
        Category category = getCategoryById(token, id);

        if(categoryRepository.existsByUserAndNameAndIdNot(user, newName, id)){
            throw new CategoryAlreadyExistsException("You already have a category named: " + newName);
        }
        category.setName(newName);

        return categoryRepository.save(category);
    }

    public void deleteCategory(String token, Long id){
        userService.getUserFromToken(token);
        Category category = getCategoryById(token, id);

        categoryRepository.deleteById(category.getId());

    }

    public List<CategoryNoteCount> getNoteCountByCategory(String token) {
        User user = userService.getUserFromToken(token);
        List<Category> categories = categoryRepository.findAllByUser(user);

        return categories.stream()
                .map(category -> new CategoryNoteCount(
                        category.getId(),
                        category.getName(),
                        noteRepository.countByCategoryIdAndDeletedFalse(category.getId())
                ))
                .toList();

    }
}
