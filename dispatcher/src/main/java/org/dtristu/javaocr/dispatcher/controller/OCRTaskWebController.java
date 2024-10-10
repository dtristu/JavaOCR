package org.dtristu.javaocr.dispatcher.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.http.parser.Authorization;
import org.dtristu.javaocr.dispatcher.service.FileValidationService;
import org.dtristu.javaocr.dispatcher.service.OCRTaskService;
import org.dtristu.javaocr.dispatcher.service.OutgoingTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

@Controller
public class OCRTaskWebController {
    protected static final Logger logger = LogManager.getLogger(OutgoingTaskService.class);
    @Autowired
    OCRTaskService ocrTaskService;
    @Autowired
    FileValidationService fileValidationService;
    @GetMapping("/web/upload")
    public String getUploadPage(){
        return "uploadPage";
    }
    @PostMapping ("/web/upload")
    public RedirectView uploadFile(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes, @CookieValue(value = "Authorization") String token){
        try {
            fileValidationService.validateUploadedFile(file);
        } catch (Exception e){
            logger.trace(e.toString());
            redirectAttributes.addFlashAttribute("message",e.toString());
            return new RedirectView("http://localhost:8080/web/upload");
        }
        try {
            ocrTaskService.processFile(file,token);
        } catch (IOException e) {
            logger.trace(e.toString());
            redirectAttributes.addFlashAttribute("message",e.toString());
            return new RedirectView("http://localhost:8080/web/upload");
        }
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");
        return new RedirectView("http://localhost:8080/web/documents");
    }
    @GetMapping("/web/upload/redirect-to-documents")
    public RedirectView redirectToDocuments(){
    return new RedirectView("http://localhost:8080/web/documents");
    }
}
