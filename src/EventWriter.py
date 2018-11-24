from src.SQLIO import SQLIO
import pandas as pd

class EventWriter:
    @classmethod
    def write(self, event):
        """

        :param event: Dict with some or none of grunddaten, termine and module
        :return:
        """
        if not event:
            return
        self.sql_io = SQLIO()
        self.write_module(event)
        self.write_veranstaltungen(event)
        self.sql_io.commit()
        self.sql_io = SQLIO()
        self.write_termin(event)
        self.sql_io.commit()

    @classmethod
    def write_module(self, event):
        if type(None) == type(event.get('module')):
            return False
        for i in range(event.get('module').shape[0]):
            row = event.get('module').loc[i]
            try:
                self.sql_io.insert('module', ["'"+row['Modulnummer']+"'", "'"+row['Modulname (Kurztext)']+"'", "'"+row['Modulname']+"'"])
            except Exception as e:
                print(e)

    @classmethod
    def write_veranstaltungen(self, event):
        grunddaten = event.get('grunddaten')
        if not grunddaten.get('Titel'):
            raise Exception('No Title')
        termindaten = event.get('termine')[0] if event.get('termine') else {}
        verantwortlicher = "'"+termindaten.get('Verantwortliche/-r')+"'" if termindaten.get('Verantwortliche/-r') else 'NULL'
        organisationseinheit = "'"+grunddaten.get('Organisationseinheit')+"'" if grunddaten.get('Organisationseinheit') else 'NULL'
        titel = "'"+grunddaten.get('Titel')+"'" if grunddaten.get('Titel') else 'NULL'
        self.sql_io.insert('veranstaltungen',
                      [verantwortlicher, organisationseinheit,titel],
                      headers=['verantwortlicher', 'organisationseinheit', 'titel'])

    @classmethod
    def write_termin(self, event):
        if event.get('termine'):
            df = event.get('termine')[1]
        else:
            return
        upload_headers = set(['Titel','Verantwortliche/-r','Durchführende/-r','Wochentag','Von','Bis','Raum','Startdatum','Enddatum','Langtext','Nummer','Organisationseinheit','Veranstaltungsart','Angebotshäufigkeit','Semesterwochenstunden','Rhythmus','Ausfalltermin','Bemerkung'])
        df_headers = set(df.columns)
        for header in upload_headers - df_headers:
            df[header] = None
        
        grunddaten = event.get('grunddaten')
        termindaten = event.get('termine')[0]
        sql_io = SQLIO()
        try:
            veranstaltungsid = \
            sql_io.select('veranstaltungen', 'veranstaltungsID', "titel = '{}';".format(grunddaten.get('Titel')))[0][0]
        except:
            print('wahrscheinlich nicht in Datenbank: {}'.format(grunddaten.get('Titel')))
            raise

        for header in grunddaten:
            df[header] = grunddaten[header]
        for header in termindaten:
            df[header] = termindaten[header]

        df['Von'] = df['Von - Bis'].str.extract('([0-9]{2}:[0-9]{2})', expand=True)
        df['Bis'] = df['Von - Bis'].str.extract('.+([0-9]{2}:[0-9]{2})', expand=True)
        df['Startdatum'] = df['Startdatum - Enddatum'].str.extract('([0-9]{2}.[0-9]{2}.[0-9]{4})', expand=True)
        df['Enddatum'] = df['Startdatum - Enddatum'].str.extract('.+([0-9]{2}.[0-9]{2}.[0-9]{4})', expand=True)

        df = df.drop('Startdatum - Enddatum', axis=1)
        df = df.drop('Von - Bis', axis=1)
        df = df.applymap(lambda x: 'NULL' if pd.isnull(x) or x.strip() == '' else "'" + x + "'")

        sql_io = SQLIO()
        veranstaltungen_sql_headers = ['VeranstaltungsID', 'Titel', 'Verantwortlicher', 'Durchführender', 'Wochentag',
                                       'Von', 'Bis', 'Raum', 'Startdatum', 'Enddatum', 'Langtext', 'Nummer',
                                       'Organisationseinheit',
                                       'Veranstaltungsart', 'Angebotshäufigkeit', 'Semesterwochenstunden', 'Rhythmus',
                                       'Ausfalltermin', 'Bemerkung']
        for i in df.index:
            row = df.loc[i]
            values = [
                str(veranstaltungsid),
                row['Titel'],
                row['Verantwortliche/-r'],
                row['Durchführende/-r'],
                row['Wochentag'],
                row['Von'],
                row['Bis'],
                row['Raum'],
                row['Startdatum'],
                row['Enddatum'],
                row['Langtext'],
                row['Nummer'],
                row['Organisationseinheit'],
                row['Veranstaltungsart'],
                row['Angebotshäufigkeit'],
                row['Semesterwochenstunden'],
                row['Rhythmus'],
                row['Ausfalltermin'],
                row['Bemerkung']
            ]
            sql_io.insert('termine', values, veranstaltungen_sql_headers)
        sql_io.commit()