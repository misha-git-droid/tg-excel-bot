Ключевые технологии: Spring Boot, Spring Framework, jakarta.json-api, java.net, telegrambots-longpolling, Apache Poi.
Прим. Использование некоторых технологий необосновано, но в рамках учебного проекта допустимо :) Spring Boot, Spring Framework, java.net, longpolling.
У бота 4 основные функции: 1) Взаимодействие с пользователем 2) Взаимодействие с api платёжного провайдера 3) Взаимодействие с api облачного хранилища 4) Редактирование файла при помощи Apache Poi 

Взаимодействие с пользователем реализовано в классе ExcelBot. 
Реализация довольно-таки простая, бот постоянно спрашивает сервер телеграмма по поводу update и если он есть, то реагирует на это действием, исходя из текстового сообщения или параметров в этом update.
(25.02 идея: расширить форматы сообщений, которые бот принимает, например, голосовые сообщения, картинки и т.п.)

