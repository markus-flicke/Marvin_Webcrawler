from selenium import webdriver
from selenium.webdriver.common.by import By
from Classes.Event import *
import sys

class Search:
    BASE_URL = 'https://marvin.uni-marburg.de/qisserver/pages/cm/exa/coursemanagement/basicCourseData.xhtml?_flowId=searchCourseNonStaff-flow&_flowExecutionKey=e1s1'
    unhandled_urls = []
    SEARCH_RESULTS = 10

    def __init__(self, url= BASE_URL):
        self.BASE_URL = url
        self.event_ids = set()

    def read_n_search_pages(self, n):
        self.empty_search()
        for i in range(n):
            self.read_search_page(i*10, self.SEARCH_RESULTS)
            self.next_search_page()
        self.unhandled_urls_to_file()

    def read_search_page(self, start_idx = 0, n = 10):
        for i in range(start_idx, start_idx + n):
            self.open_event(i)
            event = Event(self.driver.page_source)
            try:
                event.summarise()
            except:
                self.unhandled_urls.append(str(sys.exc_info()[1])) #Find a way to append the error message
                self.driver.back()
                continue
            event.sql_append()
            self.driver.back()

    def unhandled_urls_to_file(self):
        pd.Series(self.unhandled_urls).to_csv('unhandled_ids', index=False)
        if self.unhandled_urls != []:
            print("WARNING: {} unhandled event pages. See unhandled_ids.csv".format(len(self.unhandled_urls)))

    def next_search_page(self):
        element_id = 'genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Navi2next'
        element = self.driver.find_element(By.ID, element_id)
        element.click()

    def open_event(self, idx):
        element_id = 'genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Table:{}:tableRowAction'.format(idx)
        element = None
        counter = 0
        while element == None and counter < 10**3:
            try:
                element = self.driver.find_element(By.ID, element_id)
            except:
                counter += 1
        if counter == 10**3:
            raise Exception("Error: Element not found. Timeout")
        element.click()

    def empty_search(self):
        self.start()
        suchen_id = 'genericSearchMask:search'
        element = self.driver.find_element(By.ID, suchen_id)
        element.click()

    def start(self):
        self.driver.get(self.BASE_URL)

    def driver_start(self):
        try:
            self.driver = webdriver.Chrome('./chromedriver_osx')
        except:
            self.driver = webdriver.Chrome('./chromedriver_ubuntu')
