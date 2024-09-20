package com.example.DispacherOCRJava.Service;

import com.example.DispacherOCRJava.Config.Kafka.KafkaConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.example.LibraryOCRJava.OCRTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;


@Service
public class OutgoingTaskService {
    protected static final Logger logger = LogManager.getLogger(OutgoingTaskService.class);
    @Autowired
    KafkaTemplate<String,OCRTask> kafkaTemplate;
    @Autowired
    KafkaConfig kafkaConfig;
    public void publishTask(OCRTask ocrTask) throws Exception {
        CompletableFuture<SendResult<String, OCRTask>> future = kafkaTemplate.send(kafkaConfig.getTopicOut(), ocrTask);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.trace("Sent message=[{}] with offset=[{}]", ocrTask, result.getRecordMetadata().offset());

            } else {
                logger.trace("Unable to send message=[{}] due to : {}", ocrTask, ex.getMessage());
            }
        });
    }
}
