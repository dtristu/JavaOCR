package com.example.DispacherOCRJava.Controller;

import com.example.DispacherOCRJava.Service.FileValidationService;
import com.example.DispacherOCRJava.Service.OCRTaskService;

import com.example.DispacherOCRJava.Service.OutgoingTaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.example.LibraryOCRJava.OCRTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                                   RedirectAttributes redirectAttributes, Authentication authentication) {
        try {
            fileValidationService.validateUploadedFile(file);
        } catch (Exception e){
           logger.trace(e.toString());
            return e.getMessage();
        }
        try {
            ocrTaskService.processFile(file,authentication.getName());
        } catch (Exception e) {
            logger.trace(e.toString());
            return e.getMessage();
        }
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");
        return "redirect:/docs/latest";
    }
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
}
