package com.Noted.controller;

import com.Noted.dto.CreateNote;
import com.Noted.model.Note;
import com.Noted.response.NoteBasicInfo;
import com.Noted.response.NoteResponse;
import com.Noted.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/notes")
@RestController
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping("/create")
    public ResponseEntity<NoteBasicInfo> createNote(@Valid @RequestBody CreateNote newNote,
                                             @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader){

        Note savedNote = noteService.createNote(newNote, authHeader.substring(7));

        return ResponseEntity.status(HttpStatus.CREATED).body(NoteBasicInfo.fromEntity(savedNote));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<NoteBasicInfo>> getAllNotes(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader){

        List<NoteBasicInfo> notes = noteService.getAllNotes(authHeader.substring(7));

        return ResponseEntity.ok(notes);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<NoteResponse> getNoteById(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
                                                    @PathVariable Long id){
        Note note = noteService.getNoteById(authHeader.substring(7), id);

        return ResponseEntity.ok(NoteResponse.fromEntity(note));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<NoteBasicInfo> updateNote(@Valid @RequestBody CreateNote note,
                                                    @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
                                                    @PathVariable Long id){
        Note updatedNote = noteService.updateNote(note, authHeader.substring(7), id);

        return ResponseEntity.ok(NoteBasicInfo.fromEntity(updatedNote));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteNote(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
                                                    @PathVariable Long id){
        noteService.deleteNote(authHeader.substring(7), id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{categoryId}/category")
    public ResponseEntity<List<NoteBasicInfo>> listNotesByCategory(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
                                                                   @PathVariable Long categoryId){
        List<NoteBasicInfo> notes = noteService.getAllNotesByCategory(authHeader.substring(7), categoryId);

        return ResponseEntity.ok(notes);
    }
}
