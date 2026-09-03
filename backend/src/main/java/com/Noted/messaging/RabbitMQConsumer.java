package com.Noted.messaging;

import com.Noted.client.SummarizationClient;
import com.Noted.dto.SummaryJobMessage;
import com.Noted.exception.JobNotFoundException;
import com.Noted.model.SummaryJob;
import com.Noted.model.enums.SummaryJobStatus;
import com.Noted.repository.SummaryJobRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {

    private final SummaryJobRepository summaryJobRepository;
    private final SummarizationClient summarizationClient;

    public RabbitMQConsumer(SummaryJobRepository summaryJobRepository, SummarizationClient summarizationClient) {
        this.summaryJobRepository = summaryJobRepository;
        this.summarizationClient = summarizationClient;
    }

    @RabbitListener(queues = "summary-job-queue")
    public void receiveMessage(SummaryJobMessage message) {
        Long summaryJobId = message.jobId();
        String noteText = message.noteText();

        SummaryJob job = summaryJobRepository.findById(summaryJobId)
                .orElseThrow(() -> new JobNotFoundException("Couldn't find job with id: " + summaryJobId));
        job.setStatus(SummaryJobStatus.PROCESSING);
        summaryJobRepository.save(job);

        try{
            String result = summarizationClient.summarize(noteText);
            job.setResult(result);
            job.setStatus(SummaryJobStatus.COMPLETED);
        } catch (Exception e){
            job.setStatus(SummaryJobStatus.FAILED);
            job.setErrorMessage(e.getMessage());
        }

        summaryJobRepository.save(job);
    }
}
