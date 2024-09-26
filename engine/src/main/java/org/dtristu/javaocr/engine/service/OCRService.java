package org.dtristu.javaocr.engine.service;

import org.dtristu.javaocr.commons.dto.OCRTask;
import org.dtristu.javaocr.engine.config.TesseractConfig;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.OCRResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
public class OCRService {
    @Autowired
    OutgoingTaskService outgoingTaskService;
    @Autowired
    GridFsTemplate gridFsTemplate;
    @Autowired
    GridFsOperations gridFsOperations;
    @Autowired
    TesseractConfig tesseractConfig;
    protected static final Logger logger = LogManager.getLogger(OCRService.class);

    public void doTask(OCRTask ocrTask) throws Exception {
        Path tempDir = null;
        List<String> fileNamesForDel = new ArrayList<>();
        try {
            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath(tesseractConfig.getDataPath());
            int ocrMode = getOCRMode(ocrTask);

            tempDir = Files.createTempDirectory("ocrEngine");

            PDFMergerUtility merger = new PDFMergerUtility();

            BufferedImage img;
            for (String imgId : ocrTask.getRawImagesId()) {
                img = readFileToByteArray(imgId);
                Path imgPathWOSuffix = tempDir.resolve(imgId);

                fileNamesForDel.add(imgPathWOSuffix + ".pdf");
                OCRResult ocrResult = tesseract.createDocumentsWithResults(img, "fileName1", imgPathWOSuffix.toString(), new ArrayList<>(Collections.singleton(ITesseract.RenderedFormat.PDF)), ocrMode);
                merger.addSource(imgPathWOSuffix + ".pdf");
            }

            Path resultPath = tempDir.resolve(ocrTask.getDocumentId() + ".pdf");
            fileNamesForDel.add(String.valueOf(resultPath));
            merger.setDestinationFileName(String.valueOf(resultPath));

            merger.mergeDocuments(null);
            ObjectId id = storeFile(resultPath, ocrTask.getUserName(), "filename2");
            ocrTask.setMergedResult(id.toString());
            outgoingTaskService.publishTask(ocrTask);

        }
        finally {
            cleanFiles(tempDir, fileNamesForDel);
        }
    }

    private void cleanFiles(Path tempDir, List<String> fileNamesForDel) {
        if (tempDir == null) {
            return;
        }
        for (String path : fileNamesForDel) {
            File file = new File(path);
            boolean b = file.delete();
            if (!b) {
                throw new RuntimeException();
            }
        }
        File file = tempDir.toFile();
        boolean b = file.delete();
        if (!b) {
            throw new RuntimeException();
        }
    }

    public BufferedImage readFileToByteArray(String imgId) throws Exception {
        GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(imgId)));
        if (gridFSFile != null && gridFSFile.getMetadata() != null) {
            InputStream inputStream = gridFsOperations.getResource(gridFSFile).getInputStream();
            return ImageIO.read(inputStream);
        }
        throw new Exception("Error reading file");
    }

    public ObjectId storeFile(Path path, String userName, String fileName) throws Exception {
        DBObject metaData = new BasicDBObject();
        metaData.put("type", "pdf");
        metaData.put("userName", userName);
        try (InputStream streamToUploadFrom = Files.newInputStream(path)) {
            ObjectId id = gridFsTemplate.store(streamToUploadFrom, fileName, "pdf", metaData);
            logger.trace("File stored!");
            logger.trace("file id= {}", id.toString());
            return id;
        } catch (Exception e) {
            System.out.println("e = " + e);
            throw new Exception("Error writing file");
        }
    }

    protected int getOCRMode(OCRTask ocrTask){
        if(ocrTask.getPreferredOCRMode()!=null){
            return ocrTask.getPreferredOCRMode();
        }
        return 3;
    }
}
