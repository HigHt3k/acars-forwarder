package com.acars.acarsforwarder.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.sql.*;

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
        logger.info("Scheduled data send job");
        sendData();
    }

    private void sendData() {
        String endpointUrl = "https://34.28.80.116:1880/acars";

        String requestData = "";

        selectCount();

        logger.info("SENDING DATA: {}", requestData);
        sentMessages++;
        logger.info("Total amount of sent messages: {}", sentMessages);

        restTemplate.put(endpointUrl, requestData);
    }

    private Connection connect() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:" + pathToFile;
            conn = DriverManager.getConnection(url);

            logger.info("Connection established");

        } catch(SQLException e) {
            logger.error(e.getMessage());
        }
        return conn;
    }

    private void selectCount() {
        String sql = "SELECT count(*) FROM Messages";

        try(Connection conn = this.connect()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()) {
                logger.info(String.valueOf(rs.getInt(0)));
            }
        } catch(SQLException e) {
            logger.error(e.getMessage());
        }
    }
}
