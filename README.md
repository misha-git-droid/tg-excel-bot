Telegram bot for adding data to excel spreadsheets

A telegram bot that interacts with yandex.Disk after successful payment and makes changes to the file there.

Project Purpose
- Telegram bot for paying for services is a great tool for business
- Explore working with the Yandex Api and repeat working with the Telegram Api
- Study the payment process
- Learn how to parse the received data

Tech Stack
- org.eclipse.parsson + jakarta.json
- telegrambots-springboot-longpolling-starter + telegrambots-client
- poi-ooxml
- lombok
- spring-boot-starter + spring-boot-starter-test

Installation & Run
1. Clone the repo https://github.com/misha-git-droid/tg-excel-bot.git
2. Build with Maven mvn clean package
3.  



Ключевые технологии: Spring Boot, Spring Framework, jakarta.json-api, java.net, telegrambots-longpolling, Apache Poi.
Прим. Использование некоторых технологий необосновано, но в рамках учебного проекта допустимо :) Spring Boot, Spring Framework, java.net, longpolling.
У бота 4 основные функции: 
1) Взаимодействие с пользователем 
2) Взаимодействие с api платёжного провайдера 
3) Взаимодействие с api облачного хранилища 
4) Редактирование файла при помощи Apache Poi 

Взаимодействие с пользователем реализовано в классе ExcelBot. 
Реализация довольно-таки простая, бот постоянно спрашивает сервер телеграмма по поводу update и если он есть, то реагирует на это действием, исходя из текстового сообщения или параметров в этом update.
(25.02 идея: расширить форматы сообщений, которые бот принимает, например, голосовые сообщения, картинки и т.п.)

Телеграм предлагает способ для взаимодействия с api платежного провайдера. На практике все завязано на двух классах: PreCheckoutQuery и AnswerPreCheckoutQuery. 
Алгоритм того, как я работаю с этими классами. Если мне приходит update с содержанием "/start", то я формирую объект SendInvoice, который представляет собой что-то вроде сформированного заказа с описание, суммой заказа и прочими данными, а также с прикрепленной кнопкой оплатить, после нажатия которой, нужно ввести обязательные поля: номер карты, номер телефона/почта (все это настраивается в SendInvoice). Далее пользователь может оплатить покупку/услугу, на сервер телеграма прилетает update c объектом PreCheckoutQuery, мы его успешно отлавливаем и теперь нам надо отправить обратно на сервер объект AnswerPreCheckoutQuery в течении 10 секунд. Если правильно понял, во время 10 секунд мы можем проверить, например, наличие товара на складе. Этот процесс подробно описан на сайте телеграма, из-за чего я сильно сомневаюсь в его надобности, поэтому буду значительно корректировать этот абзац. 
