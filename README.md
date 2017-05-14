# Server-client
## Инфо о сервере
*`ContributionType.java` (до востребования (0), срочный (1), расчетный (2), накопительный (3), сберегательный(4), металлический(5)) представлен как перечисление*

*В ресурсах содержится файл с стартовым списком вкладов (`list.json`)*

Есть возможность собрать jar с зависимостями с помощью Maven.

Запуск:
```
server-1.0-SNAPSHOT-jar-with-dependencies.jar <port>
или 
server-1.0-SNAPSHOT-jar-with-dependencies.jar
(будут использован параметры по-умолчанию)
```

Сервер имеет консольный интерфейс и поддерживает следующие комманды:
```
stop – остановить сервер
help – показать запросы, которые поддерживает сервер
```
## Инфо о клиенте
*По-умолчанию:* host = 127.0.0.1, port = 9000

*`ContributionType.java` (до востребования (0), срочный (1), расчетный (2), накопительный (3), сберегательный(4), металлический(5)) представлен как перечисление*

Есть возможность собрать jar с зависимостями с помощью Maven.

Запуск:
```
client-1.0-SNAPSHOT-jar-with-dependencies.jar <host> <port>
или 
client-1.0-SNAPSHOT-jar-with-dependencies.jar
(будут использован параметры по-умолчанию)
```

Клиент имеет консольный интерфейс и поддерживает следующие комманды:
```
list – выдать список всех вкладов
sum – выдать общую сумму вкладов
count – выдать количество вкладов
info account <account id> - информация по счету
info depositor <depositor> - информация по имени вкладчика
show type <type> - список всех вкладов указанного типа
show bank <name> - список всех вкладов в указанном банке
add <deposit info> - добавить информацию о вкладе
Usage: add name <name> country <country> type <type> depositor <depositor> accountId <accountId> amountOnDeposit <amountOnDeposit> profitability <profitability> timeConstraints <timeConstraints>
delete <account id>"
```
