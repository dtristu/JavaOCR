package org.dtristu.javaocr.ocrengine.service;

import org.dtristu.javaocr.commons.dto.OCRTask;
import org.dtristu.javaocr.ocrengine.config.Kafka.KafkaConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class OutgoingTaskService {
    @Autowired
    KafkaTemplate<String, OCRTask> kafkaTemplate;
    @Autowired
    KafkaConfig kafkaConfig;

    protected static final Logger logger = LogManager.getLogger(OutgoingTaskService.class);
    public void publishTask(OCRTask ocrTask) throws Exception{
        ocrTask.addToLog("trying to send task from ocrService id=" + ocrTask.getDocumentId());
        CompletableFuture<SendResult<String, OCRTask>> future = kafkaTemplate.send(kafkaConfig.getTopicOut(), ocrTask);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.trace("Sent message=[{}] with offset=[{}]", ocrTask, result.getRecordMetadata().offset());

            } else {
                logger.trace("Unable to send message=[{}] due to : {}", ocrTask, ex.getMessage());

            }
        });
    }

    public void warn(OCRTask ocrTask) {

    }
}
