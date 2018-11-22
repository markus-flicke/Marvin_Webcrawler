import unittest
from src.Navigator import Navigator


class NavigatorTests(unittest.TestCase):
    def test_page_nr(self):
        n = Navigator(True)
        n.open_search()
        self.assertEqual(n.current_page_nr(), 1)
        self.assertLess(n.max_page_nr(), 1000)
        self.assertGreater(n.max_page_nr(), 10)

    def test_open_events(self):
        n = Navigator(True)
        n.open_search()
        n.open_event(1)

    def test_go_to_page(self):
        n = Navigator(True)
        n.open_search()
        n.go_to_page(101)
        n.get_screenshot_as_file('page101.png')

    def test_back_button(self):
        n = Navigator(True)
        n.open_search()
        n.open_event(0)
        n.back()

    def test_tabs(self):
        n = Navigator(True)
        url = "https://marvin.uni-marburg.de:443/qisserver/pages/startFlow.xhtml?_flowId=detailView-flow&unitId=10583&periodId=76"
        n.get(url)
        n.open_termine_tab()
        n.open_module_tab()
        n.get_screenshot_as_file('module.png')