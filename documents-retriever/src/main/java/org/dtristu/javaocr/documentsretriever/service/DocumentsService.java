package org.dtristu.javaocr.documentsretriever.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.dtristu.javaocr.commons.dto.AccountDTO;
import org.dtristu.javaocr.commons.dto.OCRTask;
import org.dtristu.javaocr.commons.dto.OCRTaskDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentsService {
    @Autowired
    RestClient restClient;
    @Autowired
    GridFsTemplate gridFsTemplate;
    @Autowired
    GridFsOperations gridFsOperations;
    @Autowired
    CleanupService cleanupService;

    /**
     * gets an Account from the user service
     * @param authorization of the user
     * @return an Account DTO
     * @throws IOException if the method is unable to get the AccountDTO
     */
    public AccountDTO getAccount(String authorization) throws IOException {
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

    /**
     * makes a list of OCRTaskDTO from an AccountDTO
     * @param accountDTO from the users service
     * @return a list of OCRTaskDTO
     */
    public List<OCRTaskDTO> getTaskList(AccountDTO accountDTO) {
        List<OCRTask> ocrTaskList = accountDTO.getOcrTaskList();
        List<OCRTaskDTO> ocrTaskDTOList = new ArrayList<>(ocrTaskList.size());
        for (OCRTask ocrTask:ocrTaskList){
            OCRTaskDTO ocrTaskDTO= new OCRTaskDTO(ocrTask);
            ocrTaskDTOList.add(ocrTaskDTO);
        }
        return ocrTaskDTOList;
    }

    /**
     * gets a resource from the database
     * @param ocrTaskDTO a ocrTaskDTO
     * @return a resource from the database
     * @throws IOException if it is unable to read the resource
     */
    public Resource getResource(OCRTaskDTO ocrTaskDTO) throws IOException {
        String resultId = ocrTaskDTO.getMergedResult();
        GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(resultId)));
        if (gridFSFile != null && gridFSFile.getMetadata() != null) {
           InputStream fileIS = gridFsOperations.getResource(gridFSFile).getInputStream();
           return new InputStreamResource(fileIS);
        }
        throw new IOException("Error reading file!");
    }
    public OCRTaskDTO getTaskById(String fileId, String token) throws IOException {
        AccountDTO accountDTO= getAccount(token);
        List<OCRTaskDTO> ocrTaskDTOList =getTaskList(accountDTO);
        for (OCRTaskDTO ocrTaskDTO:ocrTaskDTOList){
            if (fileId.equals(ocrTaskDTO.getMergedResult())){
                return ocrTaskDTO;
            }
        }
        throw new IOException("Didn't find the task for the current user!");
    }

    /**
     * checks that the respective ocrTask belongs to the respective user
     * @param ocrTaskUnsafe that is not checked
     * @param authorization of the user
     * @throws IOException if the task does not belong to the user
     */
    public void validateOCRTask(OCRTaskDTO ocrTaskUnsafe, String authorization) throws IOException {
        AccountDTO accountDTO =getAccount(authorization);
        List<OCRTaskDTO> ocrTaskDTOList = getTaskList(accountDTO);
        if (!ocrTaskDTOList.contains(ocrTaskUnsafe)){
            throw new IOException("Task is not in user account");
        }
    }

    public Resource getLatestResource(String authorization) throws IOException {
        List<OCRTask> ocrTaskDTOList = getAccount(authorization).getOcrTaskList();
        String resultId = ocrTaskDTOList.getLast().getMergedResult();
        GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(resultId)));
        if (gridFSFile != null && gridFSFile.getMetadata() != null) {
            InputStream fileIS = gridFsOperations.getResource(gridFSFile).getInputStream();
            return new InputStreamResource(fileIS);
        }
        throw new IOException("Error reading file!");
    }

    public PageImpl<OCRTaskDTO> listAllFiles(Pageable pageable, String token) throws IOException {
        AccountDTO accountDTO = getAccount(token);
        List<OCRTaskDTO> ocrTaskDTOList = getTaskList(accountDTO);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), ocrTaskDTOList.size());
        return new PageImpl<>(ocrTaskDTOList.subList(start, end), pageable, ocrTaskDTOList.size());

    }

    public void deleteById(String fileId, String token) throws Exception {
        List<OCRTask> ocrTaskList = getAccount(token).getOcrTaskList();
        boolean doesFileIdExist = false;
        for (OCRTask ocrTask:ocrTaskList){
            if(fileId.equals(ocrTask.getMergedResult())){
                cleanupService.deleteFiles(ocrTask);
                doesFileIdExist=true;
                break;
            }
        }
        if (!doesFileIdExist){
            throw new Exception("File not found in user's files!");
        }
    }
}
