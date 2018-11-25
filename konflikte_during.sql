CREATE OR REPLACE FUNCTION konflikte_during(s varchar, e varchar, weekday varchar)
RETURNS table(anzeigen varchar, titel varchar, 
			  verantwortlicher varchar, wochentag varchar, von varchar, 
			  bis varchar, raum varchar, startdatum varchar, enddatum varchar) AS $$
select distinct anzeigen, a.titel, a.verantwortlicher, a.wochentag, a.von, a.bis, a.raum, a.startdatum, a.enddatum
from (select * from termine_during(s,e,weekday) natural join termine where organisationseinheit like '%Fb12%'
) a
left join studienplaene using(titel) 
order by titel
$$ LANGUAGE SQL;
