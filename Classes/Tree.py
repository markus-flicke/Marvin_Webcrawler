from selenium import webdriver
from selenium.webdriver.common.by import By
import re
class Tree:
    def __init__(self, url):
        self.driver = webdriver.Chrome('./chromedriver_osx')
        self.driver.get(url)
        self.event_ids = set()

    def expand_all(self, idx):
        """
        Expands all subsections by index
        TODO: Method: find all subsection indices
        :return:
        """
        ea_id = 'hierarchy:courseCatalogFieldset:courseCatalog:0:8:{}:j_id_40_25_62_2_2_2_2_2_1:expandAll_button'.format(
            idx)
        eles = self.driver.find_element(By.ID, ea_id)
        eles.click()

    def get_button_elements(self):
        return self.driver.find_elements(By.XPATH, '//button')

    def find_all_detail_view_ids(self):
        html = self.driver.page_source
        id_pattern = "(hierarchy:courseCatalogFieldset:courseCatalog:[0-9]*?:[0-9]*?:[0-9]*?:[0-9]*?:[0-9]*?:showEventDateDetailVie\w+?)\""
        return set(re.findall(id_pattern, html))