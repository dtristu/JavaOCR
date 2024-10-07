package org.dtristu.javaocr.dispatcher.service;

import org.dtristu.javaocr.commons.dto.OCRTask;
import org.dtristu.javaocr.dispatcher.config.Kafka.KafkaConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;


@Service
public class OutgoingTaskService {
    protected static final Logger logger = LogManager.getLogger(OutgoingTaskService.class);
    @Autowired
    KafkaTemplate<String, OCRTask> kafkaTemplate;
    @Autowired
    KafkaConfig kafkaConfig;
    @Autowired
    FailedTasksService failedTasksService;
    public void publishTaskToConverter(OCRTask ocrTask){
        CompletableFuture<SendResult<String, OCRTask>> future = kafkaTemplate.send(kafkaConfig.getTopicOutConverter(), ocrTask);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.trace("Sent message=[{}] with offset=[{}]", ocrTask, result.getRecordMetadata().offset());

            } else {
                logger.trace("Unable to send message=[{}] due to : {}", ocrTask, ex.getMessage());
                failedTasksService.addTaskToFailedTasks(ocrTask);
            }
        });
    }
    public void publishTaskToUser(OCRTask ocrTask){
        CompletableFuture<SendResult<String, OCRTask>> future = kafkaTemplate.send(kafkaConfig.getTopicOutUser(), ocrTask);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.trace("Sent message=[{}] with offset=[{}]", ocrTask, result.getRecordMetadata().offset());

            } else {
                logger.trace("Unable to send message=[{}] due to : {}", ocrTask, ex.getMessage());
                failedTasksService.addTaskToFailedTasks(ocrTask);
            }
        });
    }
}
