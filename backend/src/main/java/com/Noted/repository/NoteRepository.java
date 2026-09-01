package com.Noted.repository;

import com.Noted.model.Note;
import com.Noted.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findAllByUserId(Long userId);
    List<Note> findAllByCategoryIdAndUserAndDeletedFalse(Long categoryId, User user);
    long countByCategoryIdAndDeletedFalse(Long categoryId);

    @Query(value = """
           SELECT notes.*, 
                ts_rank(notes.search_vector, plainto_tsquery('english', :query)) AS rank
           FROM notes
           WHERE notes.user_id = :userId
              AND notes.deleted = false
              AND notes.search_vector @@ plainto_tsquery('english', :query)
           ORDER BY rank DESC
            """,
            nativeQuery = true)
    List<Note> searchNotes(@Param("userId") Long userId,
                     @Param("query") String query);
}
