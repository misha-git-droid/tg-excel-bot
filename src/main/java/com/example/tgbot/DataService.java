package com.example.tgbot;

import lombok.Data;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@Data
@Service
public class DataService {

    Logger logger = Logger.getLogger(DataService.class.getName());
    @Autowired
    DataExcel dataExcel;
    @Autowired
    ConnectionService connectionService;

    File newTempFile;
    File tempFile;

    public void saveDataTest(DataExcel dataExcel) {

        this.dataExcel = dataExcel;

    }

    public void saveData(SuccessfulPayment successfulPayment) {

        String userName = successfulPayment.getInvoicePayload();
        dataExcel.setUserName(userName);
        String idProvider = successfulPayment.getProviderPaymentChargeId();
        dataExcel.setIdProvider(idProvider);
        int amount = successfulPayment.getTotalAmount() / 100;
        dataExcel.setAmount(amount);
        LocalDateTime localDateTime = LocalDateTime.now();
        dataExcel.setLocalDateTime(localDateTime);

//        logger.info("Данные сохранились в объекте: " + dataExcel + "\n"
//        + "UserName: " + dataExcel.getUserName() + "\n"
//                + "IdProvider: " + dataExcel.getIdProvider() + "\n"
//        + "Amount: " + dataExcel.getAmount() + "\n"
//        + "LocalDateTime: " + dataExcel.getLocalDateTime());

    }

    public void writeToExcel() {
        // чтобы записать данные в эксель надо получить ссылку для загрузки с диска и потом ссылку для загрузки на диск

        String downloadHref = connectionService.getDownloadHref();

        // загрузка файла по ссылке
        try (InputStream inputStream = new URL(downloadHref).openStream()) {

            tempFile = File.createTempFile("temp_excel", ".xlsx");
            // будем записывать во временный файл то, что прочитаем по ссылке
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            //

            // редактирование этого файла
            File file = editExcelFile(tempFile);

            outputStream.close();

            String uploadHref = connectionService.getUploadHref();

            connectionService.uploadEditFileToDisk(uploadHref, file);

            Files.deleteIfExists(tempFile.toPath());
            Files.deleteIfExists(newTempFile.toPath());

        } catch(IOException e) {
            e.printStackTrace();
        }

    }

    private File editExcelFile(File file) {

        try (Workbook workbook = new XSSFWorkbook(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            row.createCell(0).setCellValue(dataExcel.getUserName());
            row.createCell(1).setCellValue(dataExcel.getIdProvider());
            row.createCell(2).setCellValue(dataExcel.getAmount());
            row.createCell(3).setCellValue(dataExcel.getLocalDateTime());
            logger.info("Внесли необходимые изменения в таблицу. Данные взяты из объекта: " + dataExcel +
                    "\nС данными :\n" +
                    "UserName: " + dataExcel.getUserName() + "\n"
                    + "IdProvider: " + dataExcel.getIdProvider() + "\n"
                    + "Amount: " + dataExcel.getAmount() + "\n"
                    + "LocalDateTime: " + dataExcel.getLocalDateTime());

            logger.info("Создан новый временный файл для хранения обновленной информации");
            newTempFile = File.createTempFile("newTempFile", ".xlsx");
            FileOutputStream fileOut = new FileOutputStream(newTempFile);

            workbook.write(fileOut);
            logger.info("загрузили обновленные данные в новый временный файл");

            fileOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            throw new RuntimeException(e);
        }
        return newTempFile;
    }

}
