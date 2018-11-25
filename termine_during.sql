CREATE OR REPLACE FUNCTION termine_during(s varchar, e varchar, weekday varchar)
RETURNS table(terminid integer) AS $$
select
terminid
from termine 
where
TO_TIMESTAMP(s, 'HH24:MI')::TIME < TO_TIMESTAMP(bis, 'HH24:MI')::TIME and 
TO_TIMESTAMP(e, 'HH24:MI')::TIME > TO_TIMESTAMP(von, 'HH24:MI')::TIME and 
rhythmus = 'wöchentlich' and
wochentag = weekday;
$$ LANGUAGE SQL;
