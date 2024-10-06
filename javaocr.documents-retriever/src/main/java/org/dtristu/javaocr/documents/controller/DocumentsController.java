package org.dtristu.javaocr.documents.controller;

import org.dtristu.javaocr.commons.dto.AccountDTO;
import org.dtristu.javaocr.commons.dto.OCRTaskDTO;
import org.dtristu.javaocr.documents.service.DocumentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentsController {
    @Autowired
    DocumentsService documentsService;

    /**
     * gets a list of ocrTasks for a specific user
     * @param authorization to get the username
     * @return a list of ocrTasks
     */
    @GetMapping
    public ResponseEntity<List<OCRTaskDTO>> getDocuments(@RequestHeader("Authorization") String authorization) {
        try {
            AccountDTO accountDTO=documentsService.getAccount(authorization);
            List<OCRTaskDTO> taskDTOList = documentsService.getTaskList(accountDTO);
            if (taskDTOList.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(taskDTOList,HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     *  downloads a specific file
     * @param ocrTaskDTO of the respective file
     * @param authorization of the user
     * @return the file from the database
     */
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadDocument(@RequestBody OCRTaskDTO ocrTaskDTO, @RequestHeader("Authorization") String authorization){
        try {
            documentsService.validateOCRTask(ocrTaskDTO,authorization);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            Resource resource = documentsService.getResource(ocrTaskDTO);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/download/latest")
    public ResponseEntity<Resource> downloadLatest (@RequestHeader("Authorization") String authorization){
        try {
            Resource resource = documentsService.getLatestResource(authorization);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
