package org.dtristu.javaocr.documentsretriever.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dtristu.javaocr.commons.dto.OCRTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class CleanupService {
    protected static final Logger logger = LogManager.getLogger(CleanupService.class);
    @Autowired
    GridFsTemplate gridFsTemplate;

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
    public void deleteFiles(OCRTask ocrTask){
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
}
