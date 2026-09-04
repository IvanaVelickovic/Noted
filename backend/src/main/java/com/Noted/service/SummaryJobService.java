package com.Noted.service;

import com.Noted.exception.JobNotFoundException;
import com.Noted.messaging.RabbitMQProducer;
import com.Noted.model.Note;
import com.Noted.model.SummaryJob;
import com.Noted.model.User;
import com.Noted.model.enums.SummaryJobStatus;
import com.Noted.repository.SummaryJobRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SummaryJobService {
    private final SummaryJobRepository summaryJobRepository;
    private final RabbitMQProducer rabbitMQProducer;
    private final UserService userService;

    public SummaryJobService(SummaryJobRepository summaryJobRepository, RabbitMQProducer rabbitMQProducer, UserService userService) {
        this.summaryJobRepository = summaryJobRepository;
        this.rabbitMQProducer = rabbitMQProducer;
        this.userService = userService;
    }

    public SummaryJob createAndDispatch(User user, Note note){
        SummaryJob job = new SummaryJob();

        job.setUser(user);
        job.setNote(note);
        job.setStatus(SummaryJobStatus.PENDING);
        job = summaryJobRepository.save(job);

        rabbitMQProducer.sendMessage(job.getId(), note.getBody());

        return job;
    }

    public SummaryJob getSummaryJobById(String token, Long jobId){
        User user = userService.getUserFromToken(token);

        SummaryJob job = summaryJobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Couldn't find the job with id: " + jobId));

        if(!user.getId().equals(job.getUser().getId())){
            throw new JobNotFoundException("Couldn't find the job with id: " + jobId);
        }

        return job;
    }

    public void retryJob(SummaryJob job, String causeMessage){
        int retryCount = job.getRetryCount() + 1;
        job.setRetryCount(retryCount);

        if(retryCount <= 3){
            job.setStatus(SummaryJobStatus.PROCESSING);
            job.setErrorMessage(String.format("Attempt %d of 3 failed (%s). Retrying shortly...", retryCount, causeMessage));
            summaryJobRepository.save(job);

            rabbitMQProducer.sendMessage(job.getId(), job.getNote().getBody());
        } else {
            job.setStatus(SummaryJobStatus.FAILED);
            job.setErrorMessage("Failed to summarize note after 3 attempts due to AI service unavailability. Please try again later.");
            summaryJobRepository.save(job);
        }
    }

    public List<SummaryJob> getAllJobsByNote(Note note){
        return summaryJobRepository.findAllByNoteOrderByCreatedAtDesc(note);
    }
}
