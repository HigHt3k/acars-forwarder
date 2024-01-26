package com.acars.acarsforwarder.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ACARSDataReceiver {

    private long sentMessages = 0;

    private final String pathToFile = "/home/pi/acarsserv/acarsserv.sqb";

    private final RestTemplate restTemplate;

    private final Logger logger = LoggerFactory.getLogger(ACARSDataReceiver.class);

    public ACARSDataReceiver(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedRate = 5000)
    public void retrieveACARSData() {
        sendData();
    }

    private void sendData() {
        String endpointUrl = "https://34.28.80.116:1880/acars";

        String requestData = "data";

        try(BufferedReader br = new BufferedReader(new FileReader(pathToFile))) {
            String line;
            while((line = br.readLine()) != null) {
                logger.info(line);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }


        logger.info("SENDING DATA: {}", requestData);
        sentMessages++;
        logger.info("Total amount of sent messages: {}", sentMessages);

        restTemplate.put(endpointUrl, requestData);
    }
}
