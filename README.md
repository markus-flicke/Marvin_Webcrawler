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


Create Table Events (Titel varchar(300), bemerkung varchar(200), verantwortlicher varchar(500), raum varchar(400),  wochentag varchar(60), von varchar(60)
					 , bis varchar(60), rhythmus varchar(60), startdatum varchar(60), enddatum varchar(60), organisationseinheit varchar(300)
					 , permalink varchar(400));

select * from events;

select distinct titel from events order by titel;

truncate table events;