SQL Table creation Strings:
CREATE TABLE Events (Veranstaltungsnummer int, wochentag varchar(60), von varchar(60), bist varchar(60), akademischezeit varchar(60),
					rhythmus varchar(60), startdatum varchar(60), enddatum varchar(60), teilnehmerzahl varchar(60), raum varchar(200), 
					 durchführender varchar(1000), ausfalltermin varchar(200), bemerkung varchar(1000));
CREATE TABLE Module (modulnummer int primary key, modulkuerzel varchar(60), bezeichnung varchar(1000));
CREATE TABLE Veranstaltungen (veranstaltungsnummer SERIAL primary key, verantwortlicher varchar(1000), 
							  organisationseinheit varchar(1000), titel varchar(1000));
CREATE TABLE Modulzuteilung (modulnummer int REFERENCES Module(modulnummer) ON DELETE CASCADE, 
							 veranstaltungsnummer int REFERENCES Veranstaltungen(veranstaltungsnummer) ON DELETE CASCADE);