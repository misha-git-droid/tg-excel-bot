package com.example.tgbot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
public class BotConfiguration {

    @Bean
    public TelegramBotsLongPollingApplication telegramBotsLongPollingApplication(ExcelBot excelBot) throws TelegramApiException, Exception{
        TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
        botsApplication.registerBot(System.getenv("BOT_TOKEN_EXCEL"), excelBot);
        return botsApplication;
    }

    @Bean
    public TelegramClient telegramClient() {
        return new OkHttpTelegramClient(System.getenv("BOT_TOKEN_EXCEL"));
    }

}
