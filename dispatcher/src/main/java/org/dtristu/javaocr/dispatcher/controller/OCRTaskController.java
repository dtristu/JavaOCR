package org.dtristu.javaocr.dispatcher.controller;

import org.dtristu.javaocr.dispatcher.service.FileValidationService;
import org.dtristu.javaocr.dispatcher.service.OCRTaskService;

import org.dtristu.javaocr.dispatcher.service.OutgoingTaskService;
import org.dtristu.javaocr.commons.dto.OCRTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@RestController
public class OCRTaskController {
    protected static final Logger logger = LogManager.getLogger(OutgoingTaskService.class);
    @Autowired
    OCRTaskService ocrTaskService;
    @Autowired
    FileValidationService fileValidationService;
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes , @RequestHeader("name") String userName) {
        try {
            fileValidationService.validateUploadedFile(file);
        } catch (Exception e){
           logger.trace(e.toString());
            return e.getMessage();
        }
        try {
            ocrTaskService.processFile(file,userName);
        } catch (IOException e) {
            logger.trace(e.toString());
            return e.getMessage();
        }
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");
        return "redirect:/docs/latest";
    }
    /**
    @GetMapping("/docs")
    public List<OCRTask> ocrService(Authentication authentication){
        return ocrTaskService.getUserOCRTasks(authentication.getName());
    }
    @GetMapping("docs/latest")
    public ResponseEntity<byte[]> latestDoc(Authentication authentication){
        try {
            byte[] b= ocrTaskService.getLatestDoc(authentication.getName());
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            return  new ResponseEntity<byte[]>(b, headers, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    **/
}
