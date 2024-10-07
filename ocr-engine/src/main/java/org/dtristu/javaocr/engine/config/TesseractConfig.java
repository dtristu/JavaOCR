package org.dtristu.javaocr.engine.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

@Configuration
public class TesseractConfig {
    public String getDataPath() {
        try {
            File folder = new ClassPathResource("tessdata").getFile();
            return folder.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
