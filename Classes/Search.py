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

    def read_search_page(self, n = 0):
        """
        Reads the nth search page, starting from a search view.
        :param n:
        :return:
        """
        for i in range(n):
            self.next_search_page()
        for i in range(self.search_results * n, self.search_results * (n+1)):
            self.open_event(i)
            event = Event(self.driver.page_source)
            try:
                event.summarise()
            except:
                self.unhandled_urls.append(str(sys.exc_info()[1])) #Find a way to append the error message
                self.click_wait('showEvent:backButtonTop')
                continue
            event.sql_append()
            self.click_wait('showEvent:backButtonTop')
            # self.driver.back() TODO: Driver.back works most of the time, but fails randomly. idk why. Got a warning message from server. driver.back would be 500ms faster

    def unhandled_urls_to_file(self):
        pd.Series(self.unhandled_urls).to_csv('Unhandled/unhandled_urls', index=False)
        if self.unhandled_urls != []:
            print("WARNING: {} unhandled event pages. See unhandled_ids.csv".format(len(self.unhandled_urls)))

    def next_search_page(self):
        element_id = 'genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Navi2next'
        pn = self.page_number()
        self.click_wait(element_id)
        while self.page_number() == pn:
            sleep(0.01)

    def page_number(self):
        html = self.driver.page_source
        regex = '<span class="dataScrollerPageText">Seite ([0-9]+) von [0-9]+'
        return int(re.findall(regex, html)[0])

    def open_event(self, idx):
        element_id = 'genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Table:{}:tableRowAction'.format(idx)
        self.click_wait(element_id)


    def click_wait(self, element_id):
        # TODO: Use better waiting methods in the below.
        import time
        start = time.time()
        counter = 0
        element = None
        attempt_time = None
        while element == None and time.time() - start < 15:
            try:
                element = self.driver.find_element(By.ID, element_id)
            except:
                attempt_time = time.time()
                counter += 1
        if element == None:
            raise Exception("Element not found. Timeout \nAttempts:{} \nLast Attempt (secs before error): {} \nID: {}".format(counter, time.time() - attempt_time, element_id))

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
            raise Exception("Element found, but click Timeout: {}".format(element_id))

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
            raise Exception("Element not found. Timeout: {}".format(element_id))

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
            raise Exception("Element found, but send Timeout: {}".format(element_id))

    def set_entries_count(self, n):
        id = 'genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Navi2NumRowsInput'
        self.send_wait(id, 2 * Keys.BACKSPACE)
        self.send_wait(id, n)
        self.send_wait(id, Keys.RETURN)
        self.search_results = n

        # Wait until button "20" disappears. Pretty bullshit hack, but works
        time_elapsed = 0
        wait_per_cycle = 0.01

        btn_20_id = 'genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Navi2idx20'
        ele = self.driver.find_element(By.ID, btn_20_id)
        while time_elapsed < 15:
            try:
                self.driver.find_element(By.ID, btn_20_id)
                sleep(wait_per_cycle)
                time_elapsed += wait_per_cycle
            except:
                break

    def empty_search(self):
        self.start()
        suchen_id = 'genericSearchMask:search'
        self.click_wait(suchen_id)

    def start(self):
        self.driver.get(self.BASE_URL)