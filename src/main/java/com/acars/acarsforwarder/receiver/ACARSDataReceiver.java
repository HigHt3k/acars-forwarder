package com.acars.acarsforwarder.receiver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class ACARSDataReceiver {

    private long sentMessages = 0;
    private long lastMessageIdSent = 0;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final Logger logger = LoggerFactory.getLogger(ACARSDataReceiver.class);

    public ACARSDataReceiver(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedRate = 5000)
    public void retrieveACARSData() {
        logger.info("Scheduled data send job");
        sendData();
    }

    private void sendData() {
        selectCount();
        sendNextMessages();
    }

    private Connection connect() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:/home/pi/acarsserv.sqb";
            conn = DriverManager.getConnection(url);

            logger.info("Connection established");

        } catch(SQLException e) {
            logger.error("Connection to file couldn't be established: {}", e.getMessage());
        }
        return conn;
    }

    private void sendNextMessages() {
        String endpointUrl = "http://34.28.80.116:1880/acars";

        String sql = "SELECT * FROM Messages WHERE MessageID > " + lastMessageIdSent;

        try(Connection conn = this.connect()) {
            logger.info("Excecuting query to get data...");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            logger.info("Found result for messages with id > {}", lastMessageIdSent);
            while(rs.next()) {
                AcarsData acarsData = new AcarsData();
                acarsData.setMessageId(rs.getInt(1));
                lastMessageIdSent = acarsData.getMessageId();
                logger.info("Processing message with id {}", lastMessageIdSent);
                acarsData.setFlightId(rs.getInt(2));
                acarsData.setTime(rs.getString(3));
                acarsData.setStId(rs.getInt(4));
                acarsData.setChannel(rs.getInt(5));
                acarsData.setError(rs.getInt(6));
                acarsData.setSignalLvl(rs.getInt(7));
                acarsData.setMode(rs.getString(8));
                acarsData.setAck(rs.getString(9));
                acarsData.setLabel(rs.getString(10));
                acarsData.setBlockNo(rs.getString(11));
                acarsData.setMessNo(rs.getString(12));
                acarsData.setText(rs.getString(13));

                String requestData = convertToJson(acarsData);

                restTemplate.put(endpointUrl, requestData);
                logger.info("[Total MSG: {}] - SENDING DATA: {}", sentMessages, requestData);
            }
        } catch(SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private void selectCount() {
        String sql = "SELECT count(*) FROM Messages";

        try(Connection conn = this.connect()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()) {
                logger.info(String.valueOf(rs.getInt(1)));
            }
        } catch(SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private String convertToJson(AcarsData data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            // Handle or log the exception as needed
            e.printStackTrace();
            return null;
        }
    }
}
