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
                                   RedirectAttributes redirectAttributes, @RequestHeader("Authorization") String authorization) {
        try {
            fileValidationService.validateUploadedFile(file);
        } catch (Exception e){
           logger.trace(e.toString());
            return e.getMessage();
        }
        try {
            ocrTaskService.processFile(file,authorization);
        } catch (IOException e) {
            logger.trace(e.toString());
            return e.getMessage();
        }
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");
        return "redirect:/docs/latest";
    }

}
