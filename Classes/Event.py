import pandas as pd
import re
from sqlalchemy import create_engine

class Event:
    SQL_CONNECTION_STRING = 'postgresql://postgres:something@localhost:5432/Vorlesungsverzeichnis'

    def __init__(self, html):
        self.html = html
        self.df = pd.DataFrame()

    def summarise(self):
        try:
            lecturer = self.get_lecturer()
            grunddaten_df = self.get_Grunddaten()
            events_df = self.get_Veranstaltungen()
            # TODO: Talk about how to incorporate Modules in SQL relations
            # modules_df = self.get_Module()
        except:
            raise Exception("html source unreadable")

        permalink = self.get_permalink()

        df = events_df[['Wochentag', 'Von', 'Bis', 'Rhythmus', 'Startdatum\n', 'Enddatum', 'Raum', 'Bemerkung']]
        df = df.rename(columns={'Startdatum\n': 'Startdatum'})
        df.columns = df.columns.map(lambda s: s.lower())
        # TODO: Choose appropriate column names here
        df['verantwortlicher'] = lecturer
        df['permalink'] = permalink
        df['titel'] = grunddaten_df['Titel']
        df['organisationseinheit'] = grunddaten_df['Organisationseinheit']
        # TODO: Maybe delete "(Verantwortlicher)" in ['Organisationseinheit']
        self.df = df
        return self

    def sql_append(self):
        engine = create_engine(self.SQL_CONNECTION_STRING)
        # engine.execute("TRUNCATE TABLE EVENTS")
        if not self.df.empty:
            self.df.to_sql('events', engine, if_exists='append', index=False)
        else:
            raise Exception("Event dataframe not set (event.summarise required)")

    def get_permalink(self):
        """
        Returns permalink from Event View html source
        """
        permalinks = re.findall('data-page-permalink="true">(.*)<', self.html)
        if permalinks == []:
            raise Exception("Permalink not found")
        return permalinks[0].replace('&amp;', '&')

    def get_lecturer(self):
        """
        Returns Name of Lecturer(s)
        TODO: Discuss case of multiple lecturers
        """
        all_lecturers = re.findall('class="linkTableTree">(.*?)<', self.html)
        res = '; '.join(all_lecturers)
        return res

    def get_Grunddaten(self):
        """
        Returns a dictionary of the Grunddaten pane
        """
        raw = re.findall('>(.*?)\n</label><div id=".*?" class="answer">(.*?)\n', self.html)
        cleaned = self._clean_values(raw)
        return dict(cleaned)

    def get_Veranstaltungen(self):
        """
        Returns a Pandas DataFrame of the Veranstaltungen pane
        TODO: Type conversions (Everything is String right now), could be datetime
        """
        raw_df = pd.read_html(self.html)[2]
        df = raw_df.apply(lambda col: col.apply(lambda val: val[len(col.name):]))
        df = df.applymap(self._clean_values)
        return df

    def get_Module(self):
        """
        Returns a Pandas DataFrame with Modulen
        """
        raw_df = pd.read_html(self.html)[5]
        df = raw_df.apply(lambda col: col.apply(lambda val: val[len(col.name):]))
        df = df.applymap(self._clean_values)
        return df

    def _clean_values(self, val):
        val = val.replace('&amp;', '&')
        val = val.strip()
        return val