import pandas as pd
from src.SQLIO import SQLIO
from Analysis.Mail import Mail

class Konfliktplan:
    @staticmethod
    def get_lectures(professor_name):
        sql_io = SQLIO()
        lectures = sql_io.select(attribute="von,bis,wochentag, titel", table='termine',
                                 where="verantwortlicher like '%{}%' and rhythmus = 'wöchentlich'".format(
                                     professor_name))
        return lectures

    @staticmethod
    def get_konflikte(lectures):
        headers = ['Anzeigen', 'Titel', 'Verantwortlicher', 'Wochentag', 'Von', 'Bis', 'Raum', 'Startdatum', 'Enddatum']
        res = {}
        for lecture in lectures:
            sql_io = SQLIO()
            konflikte = pd.DataFrame(sql_io.select(where='True order by anzeigen', attribute='distinct *',
                                                   table="konflikte_during('{}','{}','{}')".format(lecture[0],
                                                                                                   lecture[1],
                                                                                                   lecture[2])),
                                     columns=headers)
            res['{}: {}'.format(lecture[3], lecture[2])] = konflikte
        return res

    @classmethod
    def send(self, professor_name, to='MarvinWebcrawler@gmail.com'):
        body = """<html>
          <head><style  type="text/css" > th {border: 1px solid black;width: 65px;} td 
          {border: 1px solid black;} table {border-collapse: collapse;border: 1px solid black;}
          </style>
          </head>
          <body>"""

        lectures = self.get_lectures(professor_name)
        konflikte = self.get_konflikte(lectures)
        for key in konflikte:
            df = konflikte[key]
            body += "<h1>{}</h1>".format(key)
            body += df.to_html(index=False)

        body += "</body></html>"
        Mail.send(body, subject='Konfliktplan Vorlesungen für: {}'.format(professor_name), to=to)