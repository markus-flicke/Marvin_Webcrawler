drop table termine;
drop table vmzuteilung;
drop table module;
drop table veranstaltungen;
drop table unhandled;
CREATE TABLE module (modulID varchar primary key,
					 modulkuerzel varchar,
					 modulname varchar
					);
create table veranstaltungen (
veranstaltungsID SERIAL primary key,
							  verantwortlicher varchar,
							  organisationseinheit varchar,
							  titel varchar
);
create table termine (
TerminID Serial primary key,
VeranstaltungsID int references Veranstaltungen(veranstaltungsID),
Titel varchar,
Verantwortlicher varchar,
Durchführender varchar,
Wochentag varchar,
Von varchar,
Bis varchar,
Raum varchar,
Startdatum varchar,
Enddatum varchar,
Langtext varchar,
Nummer varchar,
Organisationseinheit varchar,
Veranstaltungsart varchar,
Angebotshäufigkeit varchar,
Semesterwochenstunden varchar,
Rhythmus varchar,
Ausfalltermin varchar,
Bemerkung varchar
);

CREATE TABLE VMzuteilung (modulID varchar REFERENCES Module(modulID) ON DELETE CASCADE ON UPDATE CASCADE,
VeranstaltungsID int REFERENCES Veranstaltungen(VeranstaltungsID) ON DELETE CASCADE ON UPDATE CASCADE);
create table Unhandled (url varchar, exception varchar)
