# PL/SQL helper

The program is made for ease of development in PL / SQL developer, namely to view the log
and catching the required variables online. Also added functionality for working with all tables in the database.

Features of the program:
1) you can add 4 development environments by clicking on the "+" sign (fast switching between environments)
2) it is possible to automatically update the log data in the main table when the "auto" checkbox is enabled
3) it is possible to export the log to Excel, stop the search, clear the results
4) context menu when right-clicking on a line: copy, delete selected lines
5) button for opening / closing additional functionality (paragraphs 6-13)
6) added combobox with a list of user tables in the current development environment
7) analysis of the selected table in the combobox by the "Go" button (column name, column type,
the number of unique values ​​in a column (at the time of the request), selectivity)
8) the ability to add / remove selected tables (each environment has its own selected tables)
9) the ability to view only selected tables by activating the "Favorite tables" checkbox
10) it is possible to export blob / clob files with autodetection of the file format. If blob / clob is empty, then minus sign is displayed
11) selection of data from the table selected in the combobox by the "Select" button
12) the list of favorite tables can be viewed by clicking on the black square
13) the program is running a log (brown square)
14) there are 8 interface themes including DOS theme and contrast. Also, any background color of the application can be selected by the button with the palette
15) in the iata field, you can enter the airport code of 3 letters (for example lax) and get information about it by pressing enter.
A dfch to lax converter is also connected, if you forgot to change the keyboard layout.

![Image alt](https://github.com/mrprogre/PL-SQL-Helper/blob/master/gui.png)

Программа сделана для удобства разработки в PL/SQL developer, а именно для просмотра лога 
и отлавливания нужных переменных в режиме онлайн. Также добавлен функционал для работы со всеми таблицами в базе данных.

---
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
