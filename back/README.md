Начало работы
---
Создать структуру БД (файл database/mysql.sql)

Работа
---
Запускать нужно класс ru.kalcho.tracker.Main
В переменных окружения должны быть заданы параметры:
- DB_HOST
- DB_NAME
- DB_USERNAME
- DB_PASSWORD

Дополнительные параметры
- CHECK_POINTS_CHANGE_INTERVAL (в минутах, по умолчанию 5)
- DESTINATION_USER_PIN

Развертывание
---
Сборка gradle: задача distTar
Запуск: bin/track-back

MySQL
===
docker run --name mysql --rm -e MYSQL_ROOT_PASSWORD=password -d -p 3306:3306 mysql
docker run -it --rm mysql mysql -hhost.docker.internal -uroot -p

insert into check_points (name, latitude, longitude, radius) values ('Arena', 51.711071, 39.160427, 50);
insert into check_points (name, latitude, longitude, radius) values ('Severnoye Siyanie', 51.706474, 39.162361, 50);
insert into check_points (name, latitude, longitude, radius) values ('Ulmart', 51.706093, 39.154053, 50);

DB_HOST=127.0.0.1 DB_NAME=tracker DB_USERNAME=tracker DB_PASSWORD=password ./gradlew run


Starting with MySQL 8.0.4, they have changed the default authentication plugin for MySQL server from mysql_native_password to caching_sha2_password.
You can run the below command to resolve the issue.

alter user tracker@* identified with mysql_native_password by 'password';

create user tracker@172.17.0.1 identified with mysql_native_password by 'password';
grant all privileges on tracker.* to tracker@172.17.0.1;
flush privileges;


Caused by: com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Incorrect string value: '\xAC\xED\x00\x05sr...' for column 'id' at row 1
ALTER DATABASE tracker CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;