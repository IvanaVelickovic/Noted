package com.Noted.response;

import com.Noted.model.Note;

import java.time.LocalDateTime;

public record NoteBasicInfo(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime lastEdited,
        String title,
        Long categoryId
) {
    public static NoteBasicInfo fromEntity(Note note){
        Long categoryId = (note.getCategory() != null) ? note.getCategory().getId() : null;
        return new NoteBasicInfo(note.getId(), note.getCreatedAt(), note.getLastEdited(), note.getTitle(), categoryId);
    }
}
