##  NUMBER STATISTIC SERVICE

### 1. Требования
Java 11+, Gradle 5.0+

### 2. Сборка
`gradle build`

### 3. Запуск
Приложение после сборки будет находиться в папке `/number-statistic-service/api/build/libs`  
Для запуска необходимо выполнить команду `{путь до директории /java/bin}/java -jar api-{номер версии}.jar`  
Приложение будет запущено на порту 8000. Его можно поменять в файле `/number-statistic-service/api/src/main/resources/application.properties`  

### 4. API
`POST /number-statistic/service/api/v1/numbers body={передаваемое число}` - добавить число  
`GET /number-statistic/service/api/v1/numbers/max` - максимальное переданное число  
`GET /number-statistic/service/api/v1/numbers/min` - минимальное переданное число  
`GET /number-statistic/service/api/v1/numbers/average` - среднее значение среди переданных чисел