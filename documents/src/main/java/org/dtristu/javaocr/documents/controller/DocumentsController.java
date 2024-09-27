package org.dtristu.javaocr.documents.controller;

import org.dtristu.javaocr.commons.dto.AccountDTO;
import org.dtristu.javaocr.commons.dto.OCRTaskDTO;
import org.dtristu.javaocr.documents.service.DocumentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController("/documents")
public class DocumentsController {
    @Autowired
    DocumentsService documentsService;
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
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadDocument(@RequestBody OCRTaskDTO ocrTaskDTO, @RequestHeader("Authorization") String authorization){
        try {
            documentsService.validateOCRTask(ocrTaskDTO,authorization);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
}
