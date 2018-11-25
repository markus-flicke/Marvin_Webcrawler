import unittest
from Analysis.Konfliktplan import Konfliktplan

class KonfliktplanTest(unittest.TestCase):
    def test(self):
        Konfliktplan.send('Holzmann', to='MarvinWebcrawler@gmail.com')