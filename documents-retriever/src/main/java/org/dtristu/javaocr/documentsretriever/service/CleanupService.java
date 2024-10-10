package org.dtristu.javaocr.documentsretriever.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dtristu.javaocr.commons.dto.AccountDTO;
import org.dtristu.javaocr.commons.dto.OCRTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class CleanupService {
    protected static final Logger logger = LogManager.getLogger(CleanupService.class);
    @Autowired
    GridFsTemplate gridFsTemplate;
    @Autowired
    RestClient restClientAccount;
    /**
     * deletes unnecessary files from the database
     * @param ocrTask a finished ocrTask
     */
    public void deleteUnnecessaryFiles(OCRTask ocrTask) {
        List<String> toDelete = new LinkedList<>();
        toDelete.addAll(ocrTask.getRawImagesId());
        for (String fileId:toDelete){
            deleteFIleById(fileId);
        }
    }
    public void deleteFilesByTask(OCRTask ocrTask){
        List<String> toDelete = new LinkedList<>();
        toDelete.add(ocrTask.getMergedResult());
        toDelete.add(ocrTask.getDocumentId());
        for (String fileId:toDelete){
            deleteFIleById(fileId);
        }
    }
    private void deleteFIleById(String fileId){
        GridFSFile fileToDelete = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(fileId)));
        if (fileToDelete != null) {
            if (fileToDelete.getMetadata() != null && fileToDelete.getMetadata().get("locked").equals(false)) {
                gridFsTemplate.delete(new Query(Criteria.where("_id").is(fileId)));
            }
        } else {
            logger.warn("Wanted to delete, could not find file! id:{}", fileId);
        }
    }
    @Scheduled(fixedRate = 10,initialDelay = 1, timeUnit = TimeUnit.MINUTES)
    private void routineCleanup(){
        logger.trace("Starting routine cleanup method");
        ResponseEntity<Set<String>> response = restClientAccount.get()
                    .uri("/get-all-file-ids")
                    .retrieve()
                    .toEntity( new ParameterizedTypeReference<Set<String>>() {});
        logger.trace("Got file ids from user service");
            Set<String> fileIds = response.getBody();
            if(fileIds!=null) {
                fileIds.forEach(this::deleteFIleById);
                logger.trace("Deleted files!");
            }
    }
}
