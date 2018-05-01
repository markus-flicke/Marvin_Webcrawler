# Marvin_Webcrawler

requirements: 
Python3, https://www.python.org/
Selenium, pip3 install selenium
Pandas, pip3 install pandas
SQLalchemy, pip3 install sqlalchemy

Zum öffnen von *.ipynb verwendest du terminal command: jupyter notebook
(jupyter sollte in deiner Python installation enthalten sein)

Für Ergebnisse in SQL, brauchst du eine Datenbank:
host: localhost
username: postgres
password: something
table: events


Create Table Events (Titel varchar(150), bemerkung varchar(100), verantwortlicher varchar(100), raum varchar(200),  wochentag varchar(30), von varchar(30)
					 , bis varchar(30), rhythmus varchar(30), startdatum varchar(30), enddatum varchar(30), organisationseinheit varchar(150)
					 , permalink varchar(200));

select * from events;

select distinct titel from events order by titel;

truncate table events;