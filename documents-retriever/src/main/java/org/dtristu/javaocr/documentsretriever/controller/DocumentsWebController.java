package org.dtristu.javaocr.documentsretriever.controller;

import org.dtristu.javaocr.commons.dto.OCRTaskDTO;
import org.dtristu.javaocr.documentsretriever.service.DocumentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

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
            OCRTaskDTO ocrTaskDTO = documentsService.getTaskById(fileId,token);
            file = documentsService.getResource(ocrTaskDTO);
            String fileName = ocrTaskDTO.getDocumentName();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @PostMapping("/web/documents/delete/{fileId}")
    public RedirectView deleteFile(@PathVariable("fileId") String fileId, @CookieValue(value = "Authorization") String token,
                                   RedirectAttributes redirectAttributes){
        try {
            documentsService.deleteById(fileId,token);
            redirectAttributes.addFlashAttribute("message", "File deleted successfully: ");
        } catch (Exception e){
            redirectAttributes.addFlashAttribute("message", "Failed to delete file: ");
        }
        return new RedirectView("/web/documents");
    }
}
