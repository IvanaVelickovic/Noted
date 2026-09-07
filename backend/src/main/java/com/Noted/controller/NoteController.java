package com.Noted.controller;

import com.Noted.dto.CreateNote;
import com.Noted.model.Note;
import com.Noted.model.SummaryJob;
import com.Noted.response.NoteBasicInfo;
import com.Noted.response.NoteResponse;
import com.Noted.response.SummaryJobCreatedResponse;
import com.Noted.response.SummaryJobResponse;
import com.Noted.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/notes")
@RestController
@Tag(name = "Notes", description = "CRUD operations for notes, summarize notes, search notes")
@SecurityRequirement(name = "bearerAuth")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @Operation(summary = "Create a new note")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Note created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token"),
    })
    @PostMapping("/create")
    public ResponseEntity<NoteBasicInfo> createNote(@Valid @RequestBody CreateNote newNote,
                                             @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader){

        Note savedNote = noteService.createNote(newNote, authHeader.substring(7));

        return ResponseEntity.status(HttpStatus.CREATED).body(NoteBasicInfo.fromEntity(savedNote));
    }

    @Operation(summary = "Get all notes basic info")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "All notes (basic info) successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token"),
    })
    @GetMapping("/get-all")
    public ResponseEntity<List<NoteBasicInfo>> getAllNotes(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader){

        List<NoteBasicInfo> notes = noteService.getAllNotes(authHeader.substring(7));

        return ResponseEntity.ok(notes);
    }

    @Operation(summary = "Get a complete note by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Note successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token"),
            @ApiResponse(responseCode = "404", description = "Not found - Note with that id couldn't be found"),
    })
    @GetMapping("/get/{id}")
    public ResponseEntity<NoteResponse> getNoteById(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
                                                    @PathVariable Long id){
        Note note = noteService.getNoteById(authHeader.substring(7), id);

        return ResponseEntity.ok(NoteResponse.fromEntity(note));
    }

    @Operation(summary = "Update note")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Note successfully updated"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing title or body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token"),
            @ApiResponse(responseCode = "404", description = "Not found - Note with that id couldn't be found"),
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<NoteBasicInfo> updateNote(@Valid @RequestBody CreateNote note,
                                                    @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
                                                    @PathVariable Long id){
        Note updatedNote = noteService.updateNote(note, authHeader.substring(7), id);

        return ResponseEntity.ok(NoteBasicInfo.fromEntity(updatedNote));
    }

    @Operation(summary = "Delete note")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No content - Note successfully deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token"),
            @ApiResponse(responseCode = "404", description = "Not found - Note with that id couldn't be found"),
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteNote(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
                                                    @PathVariable Long id){
        noteService.deleteNote(authHeader.substring(7), id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all notes by a category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK - All notes by category successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token"),
    })
    @GetMapping("/{categoryId}/category")
    public ResponseEntity<List<NoteBasicInfo>> listNotesByCategory(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
                                                                   @PathVariable Long categoryId){
        List<NoteBasicInfo> notes = noteService.getAllNotesByCategory(authHeader.substring(7), categoryId);

        return ResponseEntity.ok(notes);
    }

    @Operation(summary = "Get all notes by a query (search)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK - All notes that satisfy the query successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token"),
    })
    @GetMapping("/search")
    public ResponseEntity<List<NoteBasicInfo>> searchNotes(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
                                                           @RequestParam String query,
                                                           @RequestParam(required = false) Long categoryId){

        List<NoteBasicInfo> notes = noteService.getAllNotesByQuery(authHeader.substring(7), query, categoryId);

        return ResponseEntity.ok(notes);
    }

    @Operation(summary = "Summarize a note")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Accepted - Summarization job for that note accepted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token"),
            @ApiResponse(responseCode = "429", description = "Too many requests - Daily summarization limit hit"),
    })
    @PostMapping("/{noteId}/summarize")
    public ResponseEntity<SummaryJobCreatedResponse> summarizeNote(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
                                                                   @PathVariable Long noteId){
        SummaryJob summaryJob = noteService.summarizeNote(authHeader.substring(7), noteId);

        return  ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(SummaryJobCreatedResponse.fromEntity(summaryJob));
    }

    @Operation(summary = "Get a note's summarization history")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK - Summarization history for the note retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token"),
            @ApiResponse(responseCode = "404", description = "Not found - Note with that id couldn't be found"),
    })
    @GetMapping("/{noteId}/summary-history")
    public ResponseEntity<List<SummaryJobResponse>> getNoteSummaryHistory(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
                                                                    @PathVariable Long noteId){
        List<SummaryJobResponse> responses = noteService.getNoteSummaryHistory(authHeader.substring(7), noteId);

        return ResponseEntity.ok(responses);
    }
}
