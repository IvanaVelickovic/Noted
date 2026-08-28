package com.Noted.service;

import com.Noted.dto.CreateNote;
import com.Noted.exception.NoteNotFoundException;
import com.Noted.model.Note;
import com.Noted.model.User;
import com.Noted.repository.NoteRepository;
import com.Noted.repository.UserRepository;
import com.Noted.response.NoteBasicInfo;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserService userService;

    public NoteService(NoteRepository noteRepository, UserService userService){
        this.noteRepository = noteRepository;
        this.userService = userService;
    }

    public Note createNote(CreateNote createNote, String token){

        User user = userService.getUserFromToken(token);

        Note newNote = new Note();
        newNote.setTitle(createNote.title());
        newNote.setBody(createNote.body());
        newNote.setCreatedAt(LocalDateTime.now());
        newNote.setLastEdited(LocalDateTime.now());
        newNote.setUser(user);
        noteRepository.save(newNote);

        return newNote;
    }

    public List<NoteBasicInfo> getAllNotes(String token){
        User user = userService.getUserFromToken(token);
        List<Note> notes = noteRepository.findAllByUserId(user.getId());

        return notes.stream()
                .filter(note -> !note.isDeleted())
                .map(NoteBasicInfo::fromEntity)
                .collect(Collectors.toList());
    }

    public Note getNoteById(String token, Long noteId) {
        User user = userService.getUserFromToken(token);

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Note with that id cannot be found."));

        if(note.getUser().getId() != user.getId()){
            throw new BadCredentialsException("You are not authorized to access this note.");
        }

        if(note.isDeleted()){
            throw new NoteNotFoundException("Note has been deleted.");
        }

        return note;

    }

    public Note updateNote(CreateNote updatedNote, String token, Long noteId) {
        Note note = getNoteById(token, noteId);

        note.setTitle(updatedNote.title());
        note.setBody(updatedNote.body());
        note.setLastEdited(LocalDateTime.now());

        noteRepository.save(note);

        return note;
    }

    public void deleteNote(String token, Long noteId) {
        Note note = getNoteById(token, noteId);

        note.setDeleted(true);
        note.setLastEdited(LocalDateTime.now());

        noteRepository.save(note);
    }
}
