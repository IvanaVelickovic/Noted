package com.Noted.repository;

import com.Noted.model.Note;
import com.Noted.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findAllByUserId(Long userId);
    List<Note> findAllByCategoryIdAndUserAndDeletedFalse(Long categoryId, User user);
}
