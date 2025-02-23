package com.example.tgbot;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Data
@Service
public class MessageService {

    @Autowired
    TelegramClient telegramClient;

    public SendMessage createMessage(long chatId, String text) {

        return SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .build();
    }

    public void sendMessage(BotApiMethod<?> methodMessage) {

        try {
            telegramClient.execute(methodMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

}
