package com.Noted.response;

import com.Noted.model.Note;
import com.Noted.model.User;

import java.time.LocalDateTime;

public record NoteBasicInfo(
        Long noteId,
        LocalDateTime createdAt,
        LocalDateTime lastEdited,
        String title
) {
    public static NoteBasicInfo fromEntity(Note note){
        return new NoteBasicInfo(note.getId(), note.getCreatedAt(), note.getLastEdited(), note.getTitle());
    }
}
