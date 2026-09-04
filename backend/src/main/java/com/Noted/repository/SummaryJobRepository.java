package com.Noted.repository;

import com.Noted.model.Note;
import com.Noted.model.SummaryJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SummaryJobRepository extends JpaRepository<SummaryJob, Long> {
    List<SummaryJob> findAllByNoteOrderByCreatedAtDesc(Note note);
    List<SummaryJob> findAllByNote_User_Id(Long userId);
}
