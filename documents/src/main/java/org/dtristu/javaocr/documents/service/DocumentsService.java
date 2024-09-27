package org.dtristu.javaocr.documents.service;

import org.dtristu.javaocr.commons.dto.AccountDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;

@Service
public class DocumentsService {
    @Autowired
    RestClient restClient;

    public AccountDTO getAccount(String authorization) throws IOException {
        try {
            String token = authorization.substring(7);
            ResponseEntity<AccountDTO> response = restClient.get()
                    .uri("/getAccount?token="+token)
                    .retrieve()
                    .toEntity(AccountDTO.class);
            return response.getBody();
        } catch (Exception e){
            throw new IOException("Not able to get AccountDTO");
        }
    }
}
