package org.dtristu.javaocr.commons;

import java.util.HashMap;

import java.util.Map;

/**
 * Enum of supported formats
 */
public enum DocumentType {
    PDF("pdf"),
    DOCX("docx"),
    DOC("doc");

    private static final Map<String, DocumentType> documentTypeMap = new HashMap<>();
    static {
        for (final DocumentType documentType : DocumentType.values()) {
            documentTypeMap.put(documentType.extension, documentType);
        }
    }
    public static DocumentType documentTypeMapper(String value) {
        return documentTypeMap.get(value);
    }

    private final String extension;

    DocumentType(String extension) {
        this.extension=extension;
    }

    public String getExtension() {
        return extension;
    }


}
