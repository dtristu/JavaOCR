package org.dtristu.javaocr.dispatcher.service;

import org.dtristu.javaocr.commons.DocumentType;
import org.dtristu.javaocr.commons.dto.OCRTask;
import org.dtristu.javaocr.dispatcher.repository.Account.Account;
import org.dtristu.javaocr.dispatcher.repository.Account.AccountRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.*;

@Service
public class OCRTaskService {
    protected static final Logger logger = LogManager.getLogger(OCRTaskService.class);
    @Autowired
    OutgoingTaskService outgoingTaskService;
    @Autowired
    GridFsTemplate gridFsTemplate;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    GridFsOperations gridFsOperations;

    public void processFile(MultipartFile multipartFile, String userName) throws IOException {
        String fileName=multipartFile.getOriginalFilename();
        String extension = FilenameUtils.getExtension(fileName);

        ObjectId id = storeFile(multipartFile, userName,extension);
        OCRTask ocrTask = createTask(userName, id, DocumentType.documentTypeMapper(extension),fileName);
        outgoingTaskService.publishTask(ocrTask);
    }

    public ObjectId storeFile(MultipartFile multipartFile, String userName, String extension) throws IOException {
        DBObject metaData = new BasicDBObject();
        metaData.put("type", extension);
        metaData.put("userName", userName);
        ObjectId id = gridFsTemplate.store(
                multipartFile.getInputStream(), multipartFile.getName(), multipartFile.getContentType(), metaData);
        logger.trace("File stored!");
        logger.trace("file id= {}", id.toString());
        return id;
    }

    public OCRTask createTask(String userName, ObjectId objectId, DocumentType documentType, String fileName) {
        OCRTask ocrTask = new OCRTask();
        ocrTask.setDocumentId(objectId.toString());
        ocrTask.setUserName(userName);
        ocrTask.setDocumentType(documentType);
        ocrTask.setDocumentName(fileName);
        ocrTask.addToLog("File added and task created " + Instant.now());
        logger.trace("Task created");
        return ocrTask;
    }

    public List<OCRTask> getUserOCRTasks(String userName) {
        Optional<Account> optionalAccount = accountRepository.findByUserName(userName);
        if (optionalAccount.isEmpty()){
            return new ArrayList<>();
        }
        Account account=optionalAccount.get();
        return account.getOcrTaskList();
    }
    public byte[] getLatestDoc(String username) throws Exception{
        Optional<Account> optionalAccount = accountRepository.findByUserName(username);
        if (optionalAccount.isEmpty()){
            throw new Exception("Not found!");
        }
        Account account=optionalAccount.get();
        List<OCRTask> taskList= account.getOcrTaskList();
        OCRTask latestOCRTask= taskList.get(taskList.size()-1);
        return readFileToByteArray(latestOCRTask.getMergedResult());
    }

    public byte[] readFileToByteArray(String id) throws Exception {
        GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
        if (gridFSFile != null && gridFSFile.getMetadata() != null) {
            InputStream inputStream= gridFsOperations.getResource(gridFSFile).getInputStream();
            return IOUtils.toByteArray(inputStream);
        }
        throw new Exception("Error reading file");
    }
}
