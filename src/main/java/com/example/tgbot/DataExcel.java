package com.example.tgbot;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@NoArgsConstructor
@Component
public class DataExcel {

    private String userName;
    private String idProvider;
    private int amount;
    private LocalDateTime localDateTime;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setIdProvider(String idProvider) {
        this.idProvider = idProvider;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getUserName() {
        return userName;
    }

    public String getIdProvider() {
        return idProvider;
    }

    public int getAmount() {
        return amount;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

}
