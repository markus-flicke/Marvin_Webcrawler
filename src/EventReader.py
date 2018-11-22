import pandas as pd

class EventReader():
    def __init__(self, driver):
        self.driver = driver

    def read(self):
        res = {}
        try:
            res['grunddaten'] = self.read_grunddaten()
        except:
            print("Error: No Grunddaten")

        try:
            self.driver.open_termine_tab()
            res['termine'] = self.read_termine()
        except:
            print("Error: No Termine")

        try:
            self.driver.open_module_tab()
            res['module'] = self.read_module()
        except:
            print("Error: No Module")
        return res

    def read_grunddaten(self):
        headers_webelements = self.driver.find_elements_by_class_name("labelWithBG")
        headers = list(map(lambda e: e.text, headers_webelements))
        values_webelements = self.driver.find_elements_by_class_name("answer")
        values = list(map(lambda e: e.text, values_webelements))
        return dict(zip(headers, values))

    def read_termine(self):
        basics = self.read_grunddaten()
        df = pd.read_html(self.driver.page_source(), match='Rhythmus')[1]
        df = df.apply(lambda x: x.apply(lambda y: y.replace(x.name, '')))
        return basics, df

    def read_module(self):
        res = pd.read_html(self.driver.page_source(), match='Modulnummer')[1]
        res.columns = list(map(lambda x: x.replace('\n', ''), res.columns))
        return res.apply(lambda x: x.apply(lambda y: y.replace(x.name, '')))