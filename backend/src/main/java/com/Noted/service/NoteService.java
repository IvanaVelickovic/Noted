package com.Noted.service;

import com.Noted.dto.CreateNote;
import com.Noted.exception.NoteNotFoundException;
import com.Noted.model.Note;
import com.Noted.model.SummaryJob;
import com.Noted.model.User;
import com.Noted.repository.NoteRepository;
import com.Noted.response.NoteBasicInfo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final SummaryJobService summaryJobService;

    public NoteService(NoteRepository noteRepository, UserService userService, CategoryService categoryService, SummaryJobService summaryJobService){
        this.noteRepository = noteRepository;
        this.userService = userService;
        this.categoryService = categoryService;
        this.summaryJobService = summaryJobService;
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

        if(!note.getUser().getId().equals(user.getId())){
            throw new NoteNotFoundException("Note with that id cannot be found.");
        }

        if(note.isDeleted()){
            throw new NoteNotFoundException("Note has been deleted.");
        }

        return note;
    }

    public Note updateNote(CreateNote updatedNote, String token, Long noteId) {
        Note note = getNoteById(token, noteId);

        if(updatedNote.title() != null){
            note.setTitle(updatedNote.title());
        }

        if(updatedNote.body() != null){
            note.setBody(updatedNote.body());
        }

        if(updatedNote.categoryId() != null){
            note.setCategory(categoryService.getCategoryById(token, updatedNote.categoryId()));
        }

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

    public List<NoteBasicInfo> getAllNotesByCategory(String token, Long categoryId){
        User user = userService.getUserFromToken(token);

        categoryService.getCategoryById(token, categoryId);

        List<Note> notes = noteRepository.findAllByCategoryIdAndUserAndDeletedFalse(categoryId, user);

        return notes.stream()
                .map(NoteBasicInfo::fromEntity)
                .collect(Collectors.toList());
    }

    public List<NoteBasicInfo> getAllNotesByQuery(String token, String query, Long categoryId) {
        User user = userService.getUserFromToken(token);

        List<Note> notes = noteRepository.searchNotes(user.getId(), query);

        return notes.stream()
                .filter(note -> categoryId == null ||
                        (note.getCategory() != null && note.getCategory().getId().equals(categoryId)))
                .map(NoteBasicInfo::fromEntity)
                .collect(Collectors.toList());
    }

    public SummaryJob summarizeNote(String token, Long noteId){
        User user = userService.getUserFromToken(token);
        Note note = getNoteById(token, noteId);

        return summaryJobService.createAndDispatch(user, note);
    }
}
