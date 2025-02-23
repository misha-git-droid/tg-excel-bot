package com.example.tgbot;

import lombok.Data;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;

@Data
@Service
public class PaymentService {

    public PreCheckoutQuery createPreCheckoutQuery(Update update) {
        return update.getPreCheckoutQuery();
    }

    public AnswerPreCheckoutQuery createAnswerPreCheckoutQuery(PreCheckoutQuery preCheckoutQuery) {

        String preCheckoutQueryId = preCheckoutQuery.getId();

        return AnswerPreCheckoutQuery
                .builder()
                .preCheckoutQueryId(preCheckoutQueryId)
                .ok(true)
                .build();
    }

}
