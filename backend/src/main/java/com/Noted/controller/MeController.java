package com.Noted.controller;

import com.Noted.dto.RefreshRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MeController {

    @GetMapping("/me")
    public ResponseEntity<String> me(){

        return ResponseEntity.ok("We passed authorization!!!!");
    }
}
