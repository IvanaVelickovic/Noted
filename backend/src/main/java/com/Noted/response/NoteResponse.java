package com.Noted.response;

import com.Noted.model.Note;
import com.Noted.model.User;

import java.time.LocalDateTime;

public record NoteResponse (
        Long id,
        String title,
        String body,
        LocalDateTime createdAt,
        LocalDateTime lastEdited
){
    public static NoteResponse fromEntity(Note note){
        return new NoteResponse(note.getId(),
                note.getTitle(),
                note.getBody(),
                note.getCreatedAt(),
                note.getLastEdited()
                );
    }
}
