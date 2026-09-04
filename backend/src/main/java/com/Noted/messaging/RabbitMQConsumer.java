package com.Noted.messaging;

import com.Noted.client.SummarizationClient;
import com.Noted.dto.SummaryJobMessage;
import com.Noted.exception.JobNotFoundException;
import com.Noted.model.SummaryJob;
import com.Noted.model.enums.SummaryJobStatus;
import com.Noted.repository.SummaryJobRepository;
import com.Noted.service.SummaryJobService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

@Component
public class RabbitMQConsumer {

    private final SummaryJobRepository summaryJobRepository;
    private final SummarizationClient summarizationClient;
    private final SummaryJobService summaryJobService;

    public RabbitMQConsumer(SummaryJobRepository summaryJobRepository, SummarizationClient summarizationClient, SummaryJobService summaryJobService) {
        this.summaryJobRepository = summaryJobRepository;
        this.summarizationClient = summarizationClient;
        this.summaryJobService = summaryJobService;
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
            job.setErrorMessage(null);
            summaryJobRepository.save(job);
        } catch (ResourceAccessException e){
            summaryJobService.retryJob(job, "AI service timeout");
        } catch (HttpClientErrorException | HttpServerErrorException e){
            summaryJobService.retryJob(job, "API Error " + e.getStatusCode());
        }
        catch (Exception e){
            summaryJobService.retryJob(job, e.getMessage());
        }
    }
}
