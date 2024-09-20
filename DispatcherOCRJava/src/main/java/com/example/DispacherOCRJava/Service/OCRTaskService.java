package com.example.DispacherOCRJava.Service;

import com.example.DispacherOCRJava.Repository.Account.Account;
import com.example.DispacherOCRJava.Repository.Account.AccountRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import com.example.LibraryOCRJava.OCRTask;
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
    private Set<OCRTask> failedMessages = new HashSet<>();

    public void processFile(MultipartFile multipartFile, String userName) throws Exception {
        ObjectId id = storeFile(multipartFile, userName);
        OCRTask ocrTask = createTask(userName, id);
        outgoingTaskService.publishTask(ocrTask);
    }

    public ObjectId storeFile(MultipartFile multipartFile, String userName) throws IOException {
        DBObject metaData = new BasicDBObject();
        metaData.put("type", "pdf");
        metaData.put("userName", userName);
        ObjectId id = gridFsTemplate.store(
                multipartFile.getInputStream(), multipartFile.getName(), multipartFile.getContentType(), metaData);
        logger.trace("File stored!");
        logger.trace("file id= {}", id.toString());
        return id;
    }

    public OCRTask createTask(String userName, ObjectId objectId) {
        OCRTask ocrTask = new OCRTask();
        ocrTask.setDocumentId(objectId.toString());
        ocrTask.setUserName(userName);
        ocrTask.setLog(new LinkedList<String>());
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
