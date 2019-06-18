Начало работы
---
Должны быть установлены nodejs (версии не ниже 4) и npm.
Проверка:
```
> node --version && npm --version && bower --version && gulp --version
```
Установка bower и gulp (остальные не помню):
```
> sudo npm install --global bower gulp
```
Для установки модулей, требующихся приложению, в каталоге front выполнить команды:
```
> npm install
> bower install
```
Работа
---
Сборка приложения и запуск локального сервера:
```
> gulp serve
```
Сборка для сервера (в каталог dist):
```
> gulp build
```

global.js - apiEndpoint
