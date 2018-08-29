drop table EMzuteilung;
drop table Events;
drop table Veranstaltungen;
drop table Module;
drop table Unhandled;

CREATE TABLE Module (modulID bigint primary key, 
					 modulkuerzel varchar(60), 
					 bezeichnung varchar(1000));
CREATE TABLE Veranstaltungen (veranstaltungsID SERIAL primary key, 
							  verantwortlicher varchar(1000), 
							  organisationseinheit varchar(1000), 
							  titel varchar(1000));
CREATE TABLE Events (EventID Serial primary key,
					VeranstaltungsID int references Veranstaltungen(veranstaltungsID),
					wochentag varchar(60), von varchar(60), bis varchar(60), akademischezeit varchar(60),
					rhythmus varchar(60), startdatum varchar(60), enddatum varchar(60), teilnehmerzahl varchar(60), raum varchar(200), 
					 durchführender varchar(1000), ausfalltermin varchar(200), bemerkung varchar(1000));
CREATE TABLE EMzuteilung (modulID int REFERENCES Module(modulID) ON DELETE CASCADE ON UPDATE CASCADE, 
							 EventID int REFERENCES Events(EventID) ON DELETE CASCADE ON UPDATE CASCADE);
create table Unhandled (url varchar(1000), exception varchar(1000));
