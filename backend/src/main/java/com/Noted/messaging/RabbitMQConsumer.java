package com.Noted.messaging;

import com.Noted.model.SummaryJob;
import com.Noted.model.enums.SummaryJobStatus;
import com.Noted.repository.SummaryJobRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {

    private final SummaryJobRepository summaryJobRepository;

    public RabbitMQConsumer(SummaryJobRepository summaryJobRepository) {
        this.summaryJobRepository = summaryJobRepository;
    }

    @RabbitListener(queues = "summary-job-queue")
    public void receiveMessage(Long summaryJobId) throws InterruptedException {

        SummaryJob currSummaryJob = summaryJobRepository.findById(summaryJobId)
                .orElseThrow();
        currSummaryJob.setStatus(SummaryJobStatus.PROCESSING);
        summaryJobRepository.save(currSummaryJob);

        Thread.sleep(10000);
        SummaryJob finishedSummaryJob = summaryJobRepository.findById(summaryJobId)
                .orElseThrow();
        finishedSummaryJob.setStatus(SummaryJobStatus.COMPLETED);
        summaryJobRepository.save(finishedSummaryJob);


    }
}
