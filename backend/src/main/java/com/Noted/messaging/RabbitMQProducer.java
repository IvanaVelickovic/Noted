package com.Noted.messaging;

import com.Noted.model.Note;
import com.Noted.model.SummaryJob;
import com.Noted.model.User;
import com.Noted.model.enums.SummaryJobStatus;
import com.Noted.repository.NoteRepository;
import com.Noted.repository.SummaryJobRepository;
import com.Noted.repository.UserRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;
    private final SummaryJobRepository summaryJobRepository;

    private final UserRepository userRepository;
    private final NoteRepository noteRepository;

    public RabbitMQProducer(RabbitTemplate rabbitTemplate, SummaryJobRepository summaryJobRepository, UserRepository userRepository, NoteRepository noteRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.summaryJobRepository = summaryJobRepository;
        this.userRepository = userRepository;
        this.noteRepository = noteRepository;
    }

    public void sendMessage(String message){
        SummaryJob newSummaryJob = new SummaryJob();

        User user = userRepository.findByEmail("myemail@gmail.com")
                .orElseThrow();

        Note note = noteRepository.findById(1L)
                .orElseThrow();

        newSummaryJob.setUser(user);
        newSummaryJob.setNote(note);
        newSummaryJob.setStatus(SummaryJobStatus.PENDING);

        summaryJobRepository.save(newSummaryJob);

        rabbitTemplate.convertAndSend("summary-exchange", "routing-key", newSummaryJob.getId());
    }
}
