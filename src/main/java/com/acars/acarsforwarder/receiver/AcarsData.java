package com.acars.acarsforwarder.receiver;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class AcarsData {
    int messageId;
    int flightId;
    String time;
    int stId;
    int channel;
    int error;
    int signalLvl;
    String mode;
    String ack;
    String label;
    String blockNo;
    String messNo;
    String text;
}
