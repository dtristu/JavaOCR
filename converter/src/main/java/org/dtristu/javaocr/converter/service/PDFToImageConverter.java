package org.dtristu.javaocr.converter.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.bson.types.ObjectId;
import org.dtristu.javaocr.commons.dto.OCRTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

@Service
public class PDFToImageConverter {
    @Autowired
    GridFsTemplate gridFsTemplate;
    @Autowired
    GridFsOperations gridFsOperations;
    @Autowired
    OutgoingTaskService outgoingTaskService;
    protected static final Logger logger = LogManager.getLogger(PDFToImageConverter.class);

    public void documentHandler(OCRTask ocrTask) throws Exception{
        PDDocument pdDocument=null;
        try {

            byte[] bytes = readFileToByteArray(ocrTask);
            pdDocument = Loader.loadPDF(bytes);
            bytes = null;
            logger.trace("Loaded pdf document");
            PDFRenderer pdfRenderer = new PDFRenderer(pdDocument);
            int nrOfPages = pdDocument.getNumberOfPages();
            Set<String> rawImages = new HashSet<>(nrOfPages);
            for (int i = 0; i < nrOfPages; i++) {
                BufferedImage image = pdfRenderer.renderImageWithDPI(i,300);
                logger.trace("rendered image");
                DBObject metaData = new BasicDBObject();
                metaData.put("type", "bufferedImage");
                metaData.put("userName", ocrTask.getUserName());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "bmp", baos);
                InputStream is = new ByteArrayInputStream(baos.toByteArray());

                ObjectId id = gridFsTemplate.store(is, ocrTask.getDocumentId() + i, metaData);
                logger.trace("stored image");

                rawImages.add(id.toString());
                ocrTask.setRawImagesId(rawImages);
                ocrTask.addToLog("added raw image with id=" + id.toString());
            }
        } finally {
            if (pdDocument!=null){
                try {
                    pdDocument.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
            }}
        }
        outgoingTaskService.publishTask(ocrTask);
    }

    public byte[] readFileToByteArray(OCRTask ocrTask) throws Exception {
        String documentId = ocrTask.getDocumentId();
        GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(documentId)));
        if (gridFSFile != null && gridFSFile.getMetadata() != null) {
            return IOUtils.toByteArray(gridFsOperations.getResource(gridFSFile).getInputStream());
        }
        throw new Exception("Error reading file!");
    }
}
