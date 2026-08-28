package com.Noted.service;

import com.Noted.dto.CreateNote;
import com.Noted.model.Note;
import com.Noted.model.User;
import com.Noted.repository.NoteRepository;
import com.Noted.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final JWTService jwtService;
    private final UserRepository userRepository;

    public NoteService(NoteRepository noteRepository, JWTService jwtService, UserRepository userRepository){
        this.noteRepository = noteRepository;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    private User getUserFromToken(String token){
        String email;
        try{
            email = jwtService.extractEmail(token);
        } catch(JwtException ex){
            throw new BadCredentialsException("Couldn't extract email from the token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Couldn't find the user with email: " + email));

        return user;
    }

    public Note createNote(CreateNote createNote, String authHeader){
        String token = authHeader.substring(7);

        User user = getUserFromToken(token);

        Note newNote = new Note();
        newNote.setTitle(createNote.title());
        newNote.setBody(createNote.body());
        newNote.setLastEdited(LocalDateTime.now());
        newNote.setUser(user);
        noteRepository.save(newNote);

        return newNote;
    }
}
