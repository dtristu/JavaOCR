package org.dtristu.javaocr.dispatcher.service;

import org.dtristu.javaocr.commons.DocumentType;
import org.dtristu.javaocr.commons.dto.AccountDTO;
import org.dtristu.javaocr.commons.dto.OCRTask;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

@Service
public class OCRTaskService {
    protected static final Logger logger = LogManager.getLogger(OCRTaskService.class);
    @Autowired
    OutgoingTaskService outgoingTaskService;
    @Autowired
    GridFsTemplate gridFsTemplate;
    @Autowired
    GridFsOperations gridFsOperations;
    @Autowired
    RestClient restClient;

    /**
     * saves the file and creates an ocrTask
     * @param multipartFile the file to process
     * @param authorization to get the username from
     * @throws IOException if there is an error writing the file or getting the userName
     */
    public void processFile(MultipartFile multipartFile, String authorization) throws IOException {
        String fileName=multipartFile.getOriginalFilename();
        String extension = FilenameUtils.getExtension(fileName);
        String userName=getAccount(authorization).getUserName();
        ObjectId id = storeFile(multipartFile, userName,extension);
        OCRTask ocrTask = createTask(userName, id, DocumentType.documentTypeMapper(extension),fileName);
        outgoingTaskService.publishTaskToConverter(ocrTask);
    }

    /**
     * storea a file in the database
     * @param multipartFile the file to store
     * @param userName the name of the account
     * @param extension the file extension
     * @return an ID corresponding to the file
     * @throws IOException if there is an exception writing the file
     */
    public ObjectId storeFile(MultipartFile multipartFile, String userName, String extension) throws IOException {
        DBObject metaData = new BasicDBObject();
        metaData.put("type", extension);
        metaData.put("userName", userName);
        metaData.put("locked",true);
        ObjectId id = gridFsTemplate.store(
                multipartFile.getInputStream(), multipartFile.getName(), multipartFile.getContentType(), metaData);
        logger.trace("File stored!");
        logger.trace("file id= {}", id.toString());
        return id;
    }

    /**
     *  creates an ocrTask
     * @param userName of the Account
     * @param objectId of the initial document
     * @param documentType the type of the initial document
     * @param fileName the original filename
     * @return an ocrTask
     */
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

    /**
     * reads file from database to byte[]
     * @param id of the file
     * @return a byte[] of the file
     * @throws Exception if there is a problem reading the file
     */
    public byte[] readFileToByteArray(String id) throws Exception {
        GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
        if (gridFSFile != null && gridFSFile.getMetadata() != null) {
            InputStream inputStream= gridFsOperations.getResource(gridFSFile).getInputStream();
            return IOUtils.toByteArray(inputStream);
        }
        throw new Exception("Error reading file");
    }

    /**
     * gets an Account from the user service
     * @param authorization to get the jwt from
     * @return an AccountDTO object from the user service
     * @throws IOException if it cannot get the account
     */
    public AccountDTO getAccount(String authorization) throws IOException{
        try {
            String token = authorization.substring(7);
            ResponseEntity<AccountDTO> response = restClient.get()
                .uri("/getAccount?token="+token)
                .retrieve()
                .toEntity(AccountDTO.class);
            return response.getBody();
        } catch (Exception e){
            throw new IOException("Not able to get AccountDTO");
        }
    }
}
