import unittest

from src.EventReader import EventReader
from src.EventWriter import EventWriter
from src.Navigator import Navigator
from src.SQLIO import SQLIO


class EventWriterTests(unittest.TestCase):
    def test_writer(self):
        url = "https://marvin.uni-marburg.de:443/qisserver/pages/startFlow.xhtml?_flowId=detailView-flow&unitId=10583&periodId=76"
        SQLIO().reset()
        n = Navigator(True)
        n.get(url)
        e = EventReader(n)
        res = e.read()
        w = EventWriter()
        w.write(res)
        self.assertEqual(1, SQLIO().select('veranstaltungen', 'veranstaltungsID', "titel = 'Objektorientierte Programmierung';")[0][0])