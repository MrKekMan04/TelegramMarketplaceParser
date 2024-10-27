# Telegram Marketplace Parser

![Bot](https://github.com/MrKekMan04/TelegramMarketplaceParser/actions/workflows/bot.yml/badge.svg)
![Gateway](https://github.com/MrKekMan04/TelegramMarketplaceParser/actions/workflows/gateway.yml/badge.svg)
![Scrapper](https://github.com/MrKekMan04/TelegramMarketplaceParser/actions/workflows/scrapper.yml/badge.svg)

Сервис для отслеживания маркетплейсов по правилам при помощи телеграмм бота

Сервис состоит из трех микросервисов:

- **bot** - Взаимодействие с пользователем
- **gateway** - Аггрегирование пользовательских данных и предоставление информации о маркетплейсах и правилах их
  парсинга
- **scrapper** - Собирание информации по ссылкам, проверка триггеров (правил парсинга)

## Локальный запуск

Для того, чтобы локально запустить контекст проекта, необходимо запустить docker engine и выполнить слеующие команды:

1. **mvn clean package**
2. **docker-compose build**
3. **docker-compose up -d**

Для того, чтобы остановить контекст, необходимо выполнить команду:

1. **docker-compose down**

## Порты в локальном запуске

- localhost:8080 - gateway
- localhost:8081 - bot
- localhost:8082 - scrapper
- localhost:5432 - БД gateway
- localhost:5433 - БД scrapper
