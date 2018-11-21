import unittest

from src.Driver import Driver

class DriverTests(unittest.TestCase):
    def test(self):
        Driver().get("http://www.google.com")