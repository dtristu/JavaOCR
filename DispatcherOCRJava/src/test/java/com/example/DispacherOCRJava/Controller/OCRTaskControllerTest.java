package com.example.DispacherOCRJava.Controller;

import com.example.DispacherOCRJava.Config.Kafka.KafkaConfig;
import com.example.DispacherOCRJava.Config.Kafka.KafkaConsumerConfig;
import com.example.DispacherOCRJava.Config.Kafka.KafkaProducerConfig;
import com.example.DispacherOCRJava.Config.RestConfig;
import com.example.DispacherOCRJava.Repository.Account.AccountRepository;
import com.example.DispacherOCRJava.Service.OCRTaskService;
import com.example.DispacherOCRJava.Service.OutgoingTaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ OCRTaskController.class, TokenController.class, OCRTaskService.class, AccountRepository.class, OutgoingTaskService.class, KafkaConsumerConfig.class, KafkaProducerConfig.class, KafkaConfig.class})
@Import(RestConfig.class)
public class OCRTaskControllerTest {

    @Autowired
    MockMvc mvc;
/**
    @Test
    void rootWhenAuthenticatedThenSaysHelloUser() throws Exception {
        // @formatter:off
        MvcResult result = this.mvc.perform(post("/token")
                        .with(httpBasic("johndoe123", "$2a$10$JyO71JaW7FRy2E8iwVL4bebqGa1oxd6PAye7UeRJVqONjxHe4uqe.")))
                .andExpect(status().isOk())
                .andReturn();

        String token = result.getResponse().getContentAsString();

        this.mvc.perform(get("/")
                        .header("Authorization", "Bearer " + token))
                .andExpect(content().string("ocrService"));
        // @formatter:on
    }

    @Test
    void rootWhenUnauthenticatedThen401() throws Exception {
        // @formatter:off
        this.mvc.perform(get("/"))
                .andExpect(status().isUnauthorized());
        // @formatter:on
    }

    @Test
    void tokenWhenBadCredentialsThen401() throws Exception {
        // @formatter:off
        this.mvc.perform(post("/token"))
                .andExpect(status().isUnauthorized());
        // @formatter:on
    }
**/
}