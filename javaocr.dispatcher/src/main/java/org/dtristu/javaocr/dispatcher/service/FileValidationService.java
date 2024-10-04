package org.dtristu.javaocr.dispatcher.service;


import org.apache.commons.io.FilenameUtils;
import org.dtristu.javaocr.commons.DocumentType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
@Service
public class FileValidationService {
    private static final long MAXIMUM_FILE_SIZE_ALLOWED = 500000000L;

    /**
     * Quick validation of file
     * @param file a multipart file
     * @throws Exception if the file name or size is not valid
     */
    public void validateUploadedFile(MultipartFile file) throws Exception{
        validateExtension(file);
        validateFileSize(file);
    }

    /**
     * validates the extension and most common file extension exploits
     * @param file a multipart file
     * @throws Exception if the file type is not supported
     */
    private void validateExtension(MultipartFile file) throws Exception {
        String fileName=file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(fileName);

        boolean isExtensionValid = false;
        for(DocumentType documentType:DocumentType.values()){
            if (documentType.getExtension().equals(extension)) {
                isExtensionValid = true;
                break;
            }
        }
        if(!isExtensionValid){
            throw new Exception("File type not supported");
        }

        int dot = 0;
        int percent=0;
        for (int i=0; i<fileName.length(); i++)
        {
            if (fileName.charAt(i) == '.')
                dot++;
            if(fileName.charAt(i)=='%'){
                percent++;
            }
        }
        if(dot!=1 || percent!=0){
            throw new Exception("File type not supported");
        }
    }

    /**
     * validates file size
     * @param file a multipart file
     * @throws Exception if the file is too large
     */
    private void validateFileSize(MultipartFile file) throws Exception {
        if (file.getSize() >= MAXIMUM_FILE_SIZE_ALLOWED) {
            throw new Exception("File size cannot be greater than 5 Mb");
        }
    }

    public FileValidationService() {
    }
}
