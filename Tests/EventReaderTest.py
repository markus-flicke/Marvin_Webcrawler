import unittest

from src.EventReader import EventReader
from src.Navigator import Navigator


class EventReaderTest(unittest.TestCase):
    url = "https://marvin.uni-marburg.de:443/qisserver/pages/startFlow.xhtml?_flowId=detailView-flow&unitId=10583&periodId=76"

    def test_grunddaten(self):
        n = Navigator(True)
        n.get(self.url)
        e = EventReader(n)
        expected_dict = {'Titel': 'Objektorientierte Programmierung', 'Langtext': 'Objektorientierte Programmierung', 'Nummer': 'LV-12-079-094', 'Organisationseinheit': 'Fb12 Mathematik und Informatik (Verantwortlicher)', 'Veranstaltungsart': 'Vorlesung', 'Angebotshäufigkeit': 'Unregelmäßig', 'Semesterwochenstunden': '4.0'}
        self.assertEqual(e.read_grunddaten(), expected_dict)

    def test_termine(self):
        n = Navigator(True)
        n.get(self.url)
        n.open_termine_tab()
        e = EventReader(n)
        print(e.read_termine())

    def test_module(self):
        n = Navigator(True)
        n.get(self.url)
        n.open_module_tab()
        e = EventReader(n)
        print(e.read_module())

    def test_read(self):
        n = Navigator(True)
        n.get(self.url)
        e = EventReader(n)
        self.assertEqual(len(e.read()), 3)