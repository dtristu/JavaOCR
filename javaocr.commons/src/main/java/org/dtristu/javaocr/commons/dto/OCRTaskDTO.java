package org.dtristu.javaocr.commons.dto;

import org.dtristu.javaocr.commons.DocumentType;

/**
 * DTO for user facing methods
 */
public class OCRTaskDTO {
    private String documentId;
    private DocumentType documentType;
    private String documentName;
    private String mergedResult;

    public OCRTaskDTO(String documentId, DocumentType documentType, String documentName, String mergedResult) {
        this.documentId = documentId;
        this.documentType = documentType;
        this.documentName = documentName;
        this.mergedResult = mergedResult;
    }

    public OCRTaskDTO(OCRTask ocrTask) {
        this.documentId = ocrTask.getDocumentId();
        this.documentType = ocrTask.getDocumentType();
        this.documentName = ocrTask.getDocumentName();
        this.mergedResult = ocrTask.getMergedResult();
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getMergedResult() {
        return mergedResult;
    }

    public void setMergedResult(String mergedResult) {
        this.mergedResult = mergedResult;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
