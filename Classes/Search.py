from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from Classes.Event import *
import sys
from time import sleep, time

class Search:
    # URL to start on the first Search page
    BASE_URL = 'https://marvin.uni-marburg.de/qisserver/pages/cm/exa/coursemanagement/basicCourseData.xhtml?_flowId=searchCourseNonStaff-flow&_flowExecutionKey=e1s1'
    # Collection of Event pages that cannot be read
    unhandled_urls = []
    # Current number of results displayed per event page
    search_results = 10
    # Driver to navigate Websites via simulated user action, using the Selenium API
    driver = None
    # To keep a set of Identifiers of Events currently visible on the Search page
    event_ids = set()

    def __init__(self, url= BASE_URL):
        self.BASE_URL = url
        # To initialise the selenium driver
        self.driver_start()

    def driver_start(self):
        """
        Selenium driver is initialised by trial and error.
        :return:
        """
        # Attempt to run osx, ubuntu chromedrivers
        # TODO: Add more drivers, e.g. Windows such that e.g. the secretary can run this
        try:
            self.driver = webdriver.Chrome('./chromedriver_osx')
        except:
            self.driver = webdriver.Chrome('./chromedriver_ubuntu')

    def read_search_page(self, n = 0):
        """
        Expects to start on the search view.
        Reads the nth search page.
        :param n:
        :return:
        """
        # Navigate to the nth search page
        # TODO: Go to nth page directly via number btn at btm of page
        for i in range(n):
            self.next_search_page()
        # Iterate over all expected event IDs to read out all Event pages.
        # Event IDs depend on their rank in the search results. (for empy search from 0 to ~4046)
        for i in range(self.search_results * n, self.search_results * (n+1)):
            # Navigate to the event page
            self.open_event(i)
            # Obtain the event's html source and initialise the Event handler
            event = Event(self.driver.page_source)
            # Attempt to gather all information from html source
            try:
                event.summarise()
                # Read successfull, add found data to SQL
                event.sql_append()
            except:
                # If summarising fails, Unhnadled cases are collected (and written to Marvin_Webcrawler/Unhandled/unhandled.csv)
                self.unhandled_urls.append(str(sys.exc_info()[1]))
            finally:
                # Successful or not, navigate back to the search page via "Zuruück" button
                self.click_wait('showEvent:backButtonTop')

        # For unhandled case classification, write unhandled urls to Marvin_Webcrawler / Unhandled / unhandled.csv
        self.unhandled_urls_to_file()
        # TODO: Append to file after smaller buckets, instead of attempting to write everything at the end. Doesnt work if process breaks.

    def unhandled_urls_to_file(self):
        # use pandas to create a csv of unhandled urls
        pd.Series(self.unhandled_urls).to_csv('Unhandled/unhandled_urls', index=False)
        # Warn the User that some Event pages were not read successfully
        if self.unhandled_urls != []:
            print("WARNING: {} unhandled event pages. See unhandled_ids.csv".format(len(self.unhandled_urls)))

    def next_search_page(self):
        """
        Navigate to the following search page
        :return:
        """
        # Constant next button id
        element_id = 'genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Navi2next'
        # remember the current page number to compare and check if next page has loaded
        pn = self.page_number()[0]
        # Click next including some time buffer to find the element
        self.click_wait(element_id)
        # Note System time to compare time elapsed since going to the next page.
        start = time()
        # Wait loop to see a change in page number. Give up if takes longer than 60sec
        while self.page_number()[0] == pn and time() - start < 60:
            sleep(0.01)
        # Page number has changed now, else -> Error
        assert self.page_number()[0] != pn, "Next page not loaded. Timeout"

    def page_number(self):
        """
        A tuple of (current_page, max_page)
        :return:
        """
        html = self.driver.page_source
        # Check html source via RegEx for (current_page, max_page) [shown at btm of search page]
        regex = '<span class="dataScrollerPageText">Seite ([0-9]+) von ([0-9]+)'
        # results need to be converted to integer
        res = re.findall(regex, html)[0]
        res = [int(res[0]), int(res[1])]
        return res

    def open_event(self, idx):
        """
        Opening Event pages via their ID.
        Depends only on Event's rank in search.
        :param idx:
        :return:
        """
        element_id = 'genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Table:{}:tableRowAction'.format(idx)
        self.click_wait(element_id)

    def click_wait(self, element_id, keys = None):
        """
        Click on an element id after waiting for previous process to finish.
        :param element_id:
        :return:
        """
        # Note time to give up after some time
        start = time()
        # Carry forward the number of trials. To see how often selenium attempted to get the event.
        counter = 0
        # None Element object to see a change here, means some element has been found
        element = None
        # Keep track of the last attempt to find the element
        attempt_time = None
        # While no change in element and before the giveup time...
        while element == None and time() - start < 60:
            try:
                # Try to find the element until it is found
                element = self.driver.find_element(By.ID, element_id)
                if not keys:
                    # Once the element has been found click
                    element.click()
                else:
                    # Optionally send keys instead of clicking
                    # TODO: Think of a more fitting name than 'click_wait'
                    element.send_keys(keys)
            except:
                attempt_time = time()
                counter += 1
        # The element should have been found. Else -> Error Message
        assert element != None, "Element not found. Timeout \nAttempts:{} \nLast Attempt (secs before error): {} \nID: {}".format(counter, time() - attempt_time, element_id)

    def set_entries_count(self, n):
        """
        Set the number of entries on the search page.
        Takes ridiculously long to load for e.g. 300 elements, but may be worth it to not have to switch search pages too much.
        :param n:
        :return:
        """
        # Set the Input Box at btm of page to n
        id = 'genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Navi2NumRowsInput'
        # delete old number (10)
        self.click_wait(id, 2 * Keys.BACKSPACE)
        self.click_wait(id, n)
        self.click_wait(id, Keys.RETURN)
        # Search results are needed to determine event ids. Update this
        self.search_results = n

        # keep trying for at least 120 sec
        start = time()
        # Or until the number of max pages changes
        max_pages = self.page_number()[1]
        while max_pages == self.page_number()[1] and time() - start < 120:
            # slight delay - this may keep CPU less busy. idk ¯\_(ツ)_/¯
            sleep(0.01)
        # If miracolously the page number has not changed -> Error
        assert max_pages != self.page_number()[1], "Entries Count. Timeout"


    def empty_search(self):
        """
        Navigate to a Marvin search with all entries
        :return:
        """
        # Start from the Base url
        self.start()
        # Click "Suchen"
        suchen_id = 'genericSearchMask:search'
        self.click_wait(suchen_id)

    def start(self):
        # Start out on the base url
        self.driver.get(self.BASE_URL)