package com.example.tgbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

@Component
public class ExcelBot {

    Logger logger = Logger.getLogger(ExcelBot.class.getName());

    @Autowired
    PaymentService paymentService;
    @Autowired
    MessageService messageService;
    @Autowired
    DataService dataService;

    @Override
    public void consume(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {

            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getFrom().getUserName();

            if (messageText.equals("/start")) {

                SendInvoice sendInvoice = SendInvoice
                        .builder()
                        .chatId(chatId)
                        .currency("RUB")
                        .title("Название товара")
                        .description("Описание")
                        .payload(userName)
                        .needPhoneNumber(true)
                        .sendPhoneNumberToProvider(true)
                        .providerToken(System.getenv("TOKEN_PROVIDER"))
                        .startParameter("test")
                        .prices(createPriceList(150 * 100))
//                        .providerData("Test test test\n" +
//                                "test@test.ru\n" +
//                                "+11111111111\n" +
//                                "123412341234")
                        .build();

                SendMessage message = messageService.createMessage(chatId,
                        "1. Тестовое описание алгоритма оплаты\n" +
                                "2. Тестовое описание алгоритма оплаты\n" +
                                "3. Тестовое описание алгоритма оплаты");

                messageService.sendMessage(message);
                messageService.sendMessage(sendInvoice);

            } else if (messageText.equals("/term")) {

                // Условия и положения

            } else if (messageText.equals("/help")) {

                // Справка по командам

            }
//            else if (messageText.equals("/write")) {
//
//                LocalDateTime localDateTime = LocalDateTime.now();
//
//                DataExcel dataExcel = new DataExcel();
//                dataExcel.setUserName("test");
//                dataExcel.setIdProvider("testID");
//                dataExcel.setAmount(111);
//                dataExcel.setLocalDateTime(localDateTime);
//
//                dataService.saveDataTest(dataExcel);
//
//                dataService.writeToExcel();
//
//            }
            else {

                SendMessage message = messageService.createMessage(chatId,
                        "Команда не найдена! Обратитесь в команде /help для уточнения возможностей бота!");
                messageService.sendMessage(message);
            }

        } else if (update.hasPreCheckoutQuery()) {

            PreCheckoutQuery preCheckoutQuery = paymentService.createPreCheckoutQuery(update);

            long chatId = preCheckoutQuery.getFrom().getId();

            AnswerPreCheckoutQuery answerPreCheckoutQuery = paymentService.createAnswerPreCheckoutQuery(preCheckoutQuery);

            messageService.sendMessage(answerPreCheckoutQuery);

            SendMessage message = messageService.createMessage(chatId,
                    "Бот проверил, все в наличии!" +
                            "\nОтправляем запрос платежному провайдеру для завершения транзакции, подождите, пожалуйста. " +
                            "\nКак оплата пройдет, я сразу вернусь!");

            messageService.sendMessage(message);


        } else if (update.getMessage().hasSuccessfulPayment()) {

            long chatId = update.getMessage().getChatId();

            SuccessfulPayment successfulPayment = update.getMessage().getSuccessfulPayment();

            dataService.saveData(successfulPayment);

            dataService.writeToExcel();

            SendMessage message = messageService.createMessage(chatId, "Я вернулся! Оплата прошла успешно!");
            messageService.sendMessage(message);
        }
    }

    private Collection<? extends LabeledPrice> createPriceList(int amount) {
        List<LabeledPrice> prices = new ArrayList<>();
        prices.add(new LabeledPrice("Цена товара", amount));
        return prices;
    }

}
