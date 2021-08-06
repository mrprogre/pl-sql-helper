# PL/SQL helper

Программа сделана для удобства разработки в PL/SQL developer для просмотра лога 
и отлавливания нужных переменных в режиме онлайн. Также добавлен функционал для работы со всеми таблицами в базе данных.

Возможности программы:
1) можно добавить 4 среды разработки нажав на знак "+" (быстрое переключение между средами)
2) возможно автоматическое обновление данных лога в основной таблице при включённом чекбоксе "auto"
3) возможен экспорт лога в Excel, остановка поиска, очистка результатов
4) контекстное меню при нажатии правой кнопки мыши на строке: копировать, удалять выделенные строки
5) кнопка открытия/закрытия дополнительного функционала (пункты 6-13)
6) добавлен combobox со списком таблиц пользователя в текущей среде разработки
7) анализ выбранной таблицы в combobox по кнопке "Go" (имя столбца, тип столбца, 
количество уникальных значений в столбце (на момент запроса), селективность)
8) возможность добавления/удаления избранных таблиц (для каждой среды свои избранные таблицы)
9) возможность просмотра только избранных таблиц, активировав чекбокс "Favorite tables"
10) возможен экспорт файлов blob/clob с автоопределением формата файла. Если blob/clob пустой, то отображается знак минус
11) выборка данных из таблицы выбранной в combobox по кнопке "Select"
12) список избранных таблиц можно просмотреть, нажав на черный квадрат
13) ведется лог работы программы (коричневый квадрат)
14) имеется 8 тем интерфейса, включая тему DOS и контраст. Также любой цвет фона приложения можно выбрать по кнопке с палитрой
15) в поле iata можно ввести код аэропорта из 3 букв (к примеру lax) и получить информацию о нём, нажав enter. 
Также подключен конвертер дфч в lax, если забыли сменить раскладку клавиатуры.

![Image alt](https://github.com/mrprogre/log/blob/master/gui.png)
