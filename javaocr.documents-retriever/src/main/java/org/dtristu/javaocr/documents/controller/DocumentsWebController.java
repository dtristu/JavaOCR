package org.dtristu.javaocr.documents.controller;

import org.dtristu.javaocr.commons.dto.OCRTaskDTO;
import org.dtristu.javaocr.documents.dto.FileDTO;
import org.dtristu.javaocr.documents.service.DocumentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class DocumentsWebController {
    @Autowired
    DocumentsService documentsService;
    @GetMapping("/web/documents")
    public String getDocuments(RedirectAttributes redirectAttributes, @CookieValue(value = "Authorization") String token,
                               Model model, Pageable pageable){
        Page<OCRTaskDTO> filePage = null;
        try {
            filePage = documentsService.listAllFiles(pageable,token);
            model.addAttribute("filePage", filePage);
            return "documents";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/web/documents/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("fileId") String fileId,@CookieValue(value = "Authorization") String token){
        Resource file = null;
        try {
            file = documentsService.getResourceById(fileId,token);
            return ResponseEntity.ok()
                    //name does not work
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                    .body(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
