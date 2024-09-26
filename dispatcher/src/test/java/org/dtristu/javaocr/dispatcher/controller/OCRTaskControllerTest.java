package org.dtristu.javaocr.dispatcher.controller;

import org.dtristu.javaocr.dispatcher.config.Kafka.KafkaConfig;
import org.dtristu.javaocr.dispatcher.config.Kafka.KafkaConsumerConfig;
import org.dtristu.javaocr.dispatcher.config.Kafka.KafkaProducerConfig;
import org.dtristu.javaocr.dispatcher.config.RestConfig;
import org.dtristu.javaocr.dispatcher.controller.OCRTaskController;
import org.dtristu.javaocr.dispatcher.controller.TokenController;
import org.dtristu.javaocr.dispatcher.repository.Account.AccountRepository;
import org.dtristu.javaocr.dispatcher.service.OCRTaskService;
import org.dtristu.javaocr.dispatcher.service.OutgoingTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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