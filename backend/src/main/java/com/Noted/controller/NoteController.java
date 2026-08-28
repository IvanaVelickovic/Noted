package com.Noted.controller;

import com.Noted.dto.CreateNote;
import com.Noted.model.Note;
import com.Noted.model.User;
import com.Noted.repository.UserRepository;
import com.Noted.response.NoteBasicInfo;
import com.Noted.service.JWTService;
import com.Noted.service.NoteService;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping("/create-note")
    public ResponseEntity<NoteBasicInfo> createNote(@Valid @RequestBody CreateNote newNote,
                                             @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader){

        Note savedNote = noteService.createNote(newNote, authHeader);

        return ResponseEntity.status(HttpStatus.CREATED).body(NoteBasicInfo.fromEntity(savedNote));
    }
}
