package org.dtristu.javaocr.dispatcher.controller;

import org.dtristu.javaocr.dispatcher.service.FailedTasksService;
import org.dtristu.javaocr.dispatcher.service.FileValidationService;
import org.dtristu.javaocr.dispatcher.service.OCRTaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OCRTaskControllerTest {
    @Mock
    private FailedTasksService failedTasksService;
    @Mock
    private OCRTaskService ocrTaskService;
    @Mock
    private FileValidationService fileValidationService;
    @Mock
    private RedirectAttributes redirectAttributes;
    @InjectMocks
    private OCRTaskController ocrTaskController;

    @Test
    public void testHandleFileUploadSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "test content".getBytes());
        String authorization = "Bearer token";
        doNothing().when(fileValidationService).validateUploadedFile(file);
        doNothing().when(ocrTaskService).processFile(file, authorization);

        String result = ocrTaskController.handleFileUpload(file, redirectAttributes, authorization);

        assertEquals("redirect:/docs/latest", result);
        verify(fileValidationService).validateUploadedFile(file);
        verify(ocrTaskService).processFile(file, authorization);
        verify(redirectAttributes).addFlashAttribute("message", "You successfully uploaded test.txt!");
    }

    @Test
    public void testHandleFileUploadFileValidationException() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "test content".getBytes());
        String authorization = "Bearer token";
        doThrow(new Exception("File validation failed")).when(fileValidationService).validateUploadedFile(file);

        String result = ocrTaskController.handleFileUpload(file, redirectAttributes, authorization);

        assertEquals("File validation failed", result);
        verify(fileValidationService).validateUploadedFile(file);
        verify(ocrTaskService, never()).processFile(file, authorization);
    }

    @Test
    public void testHandleFileUploadIOException() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "test content".getBytes());
        String authorization = "Bearer token";
        doNothing().when(fileValidationService).validateUploadedFile(file);
        doThrow(new IOException("Processing error")).when(ocrTaskService).processFile(file, authorization);

        String result = ocrTaskController.handleFileUpload(file, redirectAttributes, authorization);

        assertEquals("Processing error", result);
        verify(fileValidationService).validateUploadedFile(file);
        verify(ocrTaskService).processFile(file, authorization);
    }
}