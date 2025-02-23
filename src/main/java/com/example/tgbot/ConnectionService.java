package com.example.tgbot;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

@Service
@Data
public class ConnectionService {

    Logger logger = Logger.getLogger(ConnectionService.class.getName());
    URL url;
    HttpURLConnection connection;
    String href;
    String downloadHref;
    String uploadHref;

    private StringBuilder getResponse(HttpURLConnection connection) {

        StringBuilder response = new StringBuilder();

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();
        } catch (IOException e) {
            logger.info(ConnectionService.class.getName());
            e.printStackTrace();
        }

        logger.info("Получили ответ");
        return response;
    }

    private String getHrefFromResponse(StringBuilder response) {

        try (JsonReader reader = Json.createReader(new StringReader(response.toString()))) {
            JsonObject jsonObject = reader.readObject();

            href = jsonObject.getString("href");

        }

        logger.info("Отделили ссылку из json");

        return href;
    }

    public String getDownloadHref() {

        String param = URLEncoder.encode("Application/telegramBotTest/Book.xlsx" , StandardCharsets.UTF_8);

        try {
            url = new URL("https://cloud-api.yandex.net/v1/disk/resources/download?path=" + param);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            // токен доступ к яндекс диску. скрыть данные в переменные среды
            connection.setRequestProperty("Authorization", System.getenv("TOKEN_YANDEX_DISK"));

            StringBuilder response = getResponse(connection);

            downloadHref = getHrefFromResponse(response);

        } catch (IOException e) {
            logger.info(ConnectionService.class.getName());
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        logger.info("Получили ссылку для загрузки во временный файл");
        return downloadHref;
    }

    public String getUploadHref() {

        logger.info("Формируем запрос для получения ссылки для загрузки обновленного файла на Яндекс диск");
        String overwrite = "&overwrite=true";
        String path = URLEncoder.encode("Application/telegramBotTest/Book.xlsx", StandardCharsets.UTF_8);
        try {
            url = new URL("https://cloud-api.yandex.net/v1/disk/resources/upload?path=" + path + overwrite);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type" , "application/json");
            connection.setRequestProperty("Authorization", System.getenv("TOKEN_YANDEX_DISK"));

            StringBuilder response = getResponse(connection);

            uploadHref = getHrefFromResponse(response);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        logger.info("Получили ссылку для загрузки на яндекс диск");
        return uploadHref;
    }

    public void uploadEditFileToDisk(String uploadHref, File file) {
        logger.info("Формируем запрос для отправки файла на Яндекс диск");
        try {
            URL url = new URL(uploadHref);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/octet-stream");

            OutputStream outputStream = connection.getOutputStream();
            FileInputStream fileInputStream = new FileInputStream(file);
            logger.info("Записываем данные из файла в поток запроса");
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            int responseCode = connection.getResponseCode();

            logger.info("Получаем ответ на этот запрос: " + Integer.toString(responseCode));

            outputStream.close();
            fileInputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        logger.info("Загрузка данных на диск завершена");

    }

}
