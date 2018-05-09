from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from Classes.Event import *
import sys
from time import sleep

class Search:
    BASE_URL = 'https://marvin.uni-marburg.de/qisserver/pages/cm/exa/coursemanagement/basicCourseData.xhtml?_flowId=searchCourseNonStaff-flow&_flowExecutionKey=e1s1'
    unhandled_urls = []
    search_results = 10
    driver = None

    def __init__(self, url= BASE_URL):
        self.BASE_URL = url
        self.event_ids = set()
        self.driver_start()

    def driver_start(self):
        try:
            self.driver = webdriver.Chrome('./chromedriver_osx')
        except:
            self.driver = webdriver.Chrome('./chromedriver_ubuntu')

    def read_n_search_pages(self, n):
        """
        Reads n pages from search. However we get a warning after about 22 pages. Solution: search in multiple windows for speed.
        :param n:
        :return:
        """
        self.empty_search()
        for i in range(n):
            self.read_search_page(i * self.search_results, self.search_results)
            self.next_search_page()
        self.unhandled_urls_to_file()

    def read_search_page_range(self, min, max):
        for i in range(min):
            self.next_search_page()
        for i in range(max- min):
            self.read_search_page((i+min) * self.search_results, self.search_results)
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
        pd.Series(self.unhandled_urls).to_csv('Unhandled/unhandled_urls', index=False)
        if self.unhandled_urls != []:
            print("WARNING: {} unhandled event pages. See unhandled_ids.csv".format(len(self.unhandled_urls)))

    def next_search_page(self):
        element_id = 'genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Navi2next'
        self.click_wait(element_id)

    def open_event(self, idx):
        element_id = 'genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Table:{}:tableRowAction'.format(idx)
        self.click_wait(element_id)

    def click_wait(self, element_id):
        # TODO: Use better waiting methods in the below.
        counter = 0
        element = None
        while element == None and counter < 10 ** 3:
            try:
                element = self.driver.find_element(By.ID, element_id)
            except:
                counter += 1
        if counter >= 10 ** 3:
            raise Exception("Error: Element not found. Timeout")

        counter = 0
        while counter < 1000:
            try:
                element = self.driver.find_element(By.ID, element_id)
                element.click()
                break
            except:
                counter += 1
                pass
        if counter >= 10 ** 3:
            raise Exception("Error: Element found, but click timeout")

    def send_wait(self, element_id, keys):
        # TODO: Use better waiting methods in the below.
        counter = 0
        element = None
        while element == None and counter < 10 ** 3:
            try:
                element = self.driver.find_element(By.ID, element_id)
            except:
                counter += 1
        if counter >= 10 ** 3:
            raise Exception("Error: Element not found. Timeout")

        counter = 0
        while counter < 1000:
            try:
                element = self.driver.find_element(By.ID, element_id)
                element.send_keys(keys)
                break
            except:
                counter += 1
                pass
        if counter >= 10 ** 3:
            raise Exception("Error: Element found, but send timeout")

    def set_entries_count(self, n):
        id = 'genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Navi2NumRowsInput'
        self.send_wait(id, 2 * Keys.BACKSPACE)
        self.send_wait(id, n)
        self.send_wait(id, Keys.RETURN)
        self.search_results = n

        # Wait until button "20" disappears. Pretty bullshit hack, but works
        ele = self.driver.find_element(By.ID, id)
        btn_20_id = 'genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Navi2idx20'
        while ele == self.driver.find_element(By.ID, id):
            try:
                sleep(0.01)
            except:
                print('element not found')
                break

    def empty_search(self):
        self.start()
        suchen_id = 'genericSearchMask:search'
        self.click_wait(suchen_id)

    def start(self):
        self.driver.get(self.BASE_URL)