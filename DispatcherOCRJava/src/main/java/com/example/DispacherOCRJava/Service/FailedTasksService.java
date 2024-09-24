package com.example.DispacherOCRJava.Service;

import com.example.LibraryOCRJava.DTO.OCRTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class FailedTasksService {
    protected static final Logger logger = LogManager.getLogger(FailedTasksService.class);
    private Queue<OCRTask> failedTasks;

    public FailedTasksService() {
        this.failedTasks = new ConcurrentLinkedQueue<>();
    }

    public void addTaskToFailedTasks(OCRTask ocrTask){
        failedTasks.add(ocrTask);
        logger.debug("Added task to failedTasks: {}", ocrTask);
    }


}
