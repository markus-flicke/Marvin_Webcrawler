import pandas as pd
import re
from sqlalchemy import create_engine

class Event:
    # Constant to enable dumping our data to a localhost postgres SQL database
    SQL_CONNECTION_STRING = 'postgresql://postgres:something@localhost:5432/Vorlesungsverzeichnis'

    def __init__(self, html):
        """
        To set central data fields
        :param html:
        """
        # HTML source which is analysed in the below
        self.html = html
        # Results are collected in this table
        self.df = pd.DataFrame()

    def summarise(self):
        """
        Analyses HTML source and stores findings in self.df
        """
        # Obtains a url to the current Event page
        permalink = self.get_permalink()
        try:
            # Uses a Regex to obtain the lecturers in a single string
            lecturer = self.get_lecturer()
            # To find a dict of the general information from the box at the top of page
            grunddaten_df = self.get_Grunddaten()
            # To produce a pd.DataFrame of the Events that are in the events table
            events_df = self.get_Veranstaltungen()
            # TODO: Incorporate Modules in SQL relations
        except:
            # In case of error, raise the permalink (and hope it's not NaN, but it usually isnt)
            # These Errors are found under Marvin_Webcrawler/Errors/...log
            raise Exception(permalink)

        # Restrict the events Dataframe to the Relation Schema
        try:
            df = events_df[['Wochentag', 'Von', 'Bis', 'Rhythmus', 'Startdatum\n', 'Enddatum', 'Raum', 'Bemerkung']]
        except:
            raise Exception('Unusual relation schema: {}'.format(permalink))

        # Format the column names
        df = df.rename(columns={'Startdatum\n': 'Startdatum'})
        df.columns = df.columns.map(lambda s: s.lower())
        # Assign columns to the information obtained earlier
        df['verantwortlicher'] = lecturer
        df['permalink'] = permalink
        df['titel'] = grunddaten_df['Titel']
        df['organisationseinheit'] = grunddaten_df['Organisationseinheit']
        self.df = df
        return self

    def sql_append(self):
        """
        After every pevent pages has been read successfully its information is appended to the SQL table.
        :return:
        """
        # An engine obect from SQLAlchemy using our connection string (DB, user, pw, table, host)
        engine = create_engine(self.SQL_CONNECTION_STRING)
        # Append if table has been summarised
        if not self.df.empty:
            self.df.to_sql('events', engine, if_exists='append', index=False)
        else:
            self.summarise()
            self.df.to_sql('events', engine, if_exists='append', index=False)

    def get_permalink(self):
        """
        Returns url to the Event View from html source
        If none are found, returns NaN
        """
        # Using a RegEx to match the code sourrunding our Event url
        permalinks = re.findall('data-page-permalink="true">(.*)<', self.html)
        if permalinks == []:
            return pd.np.nan
        # Replacing HTML fragments to produce the real URL.
        # I think I am actually trying to parse HTML here -> may fail if there are other replacements.
        return permalinks[0].replace('&amp;', '&')

    def get_lecturer(self):
        """
        Returns Name of Lecturer(s) separated by a semicolon ';'
        """
        # Using a RegEx to match the code sourrunding the Lecturer names
        all_lecturers = re.findall('class="linkTableTree">(.*?)<', self.html)
        # To put all lecturers into a single Attribute in our SQL relation, they are joined with a semicolon
        res = '; '.join(all_lecturers)
        return res

    def get_Grunddaten(self):
        """
        Returns a dictionary of the Grunddaten pane
        """
        # Using a RegEx to match the code sourrunding all Grunddaten elements
        raw = re.findall('>(.*?)\n</label><div id=".*?" class="answer">(.*?)\n', self.html)
        # TODO: Clean Data Properly here, Data may contain &amp or other weirdness
        return dict(raw)

    def get_Veranstaltungen(self):
        """
        Returns a Pandas DataFrame of the Veranstaltungen pane
        TODO: Type conversions (Everything is String right now), could be datetime
        """
        # Using purely this id to look for the events table
        veranstaltungen_id = 'showEvent:planelementsOfCurrentTerm:0:termineRauemeFieldset1:plannedDatesTable_:plannedDatesTable_Table'
        # To find the table using, pandas builtin read_html function according to the table's id.
        raw_df = pd.read_html(self.html, attrs = {'id':veranstaltungen_id})[0]
        # To remove the repetition of the table header in each table element
        df = raw_df.apply(lambda col: col.apply(lambda val: val[len(col.name):]))
        # To clean values from raw html format into readable text
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
        """
        Parsing HTMl into readable text.
        TODO: Use a prebuilt (and complete) function for parsing html
        :param val:
        :return:
        """
        val = val.replace('&amp;', '&')
        val = val.strip()
        return val