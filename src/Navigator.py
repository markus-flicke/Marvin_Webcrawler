from src.Driver import Driver
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By

class Navigator(Driver):
    SEARCH_URL = "https://marvin.uni-marburg.de/qisserver/pages/cm/exa/coursemanagement/basicCourse" +\
    "Data.xhtml?_flowId=searchCourseNonStaff-flow&_flowExecutionKey=e1s1"

    current_page = 1
    max_page = None

    def __init__(self, headless = False):
        super().__init__(headless)
        self.wait = WebDriverWait(self, 10, 0.1)
        self.wait_super_short = WebDriverWait(self, 0.5, 0.1)

    def open_search(self):
        self.get(self.SEARCH_URL)
        button_search_id = "genericSearchMask:search"
        self.wait.until(EC.presence_of_element_located((By.ID, button_search_id)))
        self.find_element_by_id(button_search_id).click()

    def current_page_nr(self):
        # self.wait.until(EC.presence_of_element_located((By.CLASS_NAME,"dataScrollerPageText")))
        try:
            s = self.find_element_by_class_name("dataScrollerPageText").text
        except:
            return self.current_page
        self.current_page = int(s.split(' ')[1])
        return self.current_page

    def max_page_nr(self):
        self.wait.until(EC.presence_of_element_located((By.CLASS_NAME, "dataScrollerPageText")))
        s = self.find_element_by_class_name("dataScrollerPageText").text
        self.max_page = int(s.split(' ')[3])
        return self.max_page

    def open_event(self, idx):
        """
        Navigiert zum Event an gegebenem Index
        :param idx: Gesamt idx des Events. In range(0, 9)
        :return:
        """
        eventNumber = (self.current_page - 1) * 10 + idx
        buttonEventLinkID = "genSearchRes:id3df798d58b4bacd9:id3df798d58b4bacd9Table:{}:actionsLeft:show".format(eventNumber)

        self.wait.until(EC.presence_of_element_located((By.ID, buttonEventLinkID)))
        event_webelement = self.find_element_by_id(buttonEventLinkID)
        event_webelement.click()

        back_button_id = "form:dialogHeader:backButtonTop"
        self.wait.until(EC.presence_of_element_located((By.ID, back_button_id)))

    def open_termine_tab(self):
        id = 'detailViewData:tabContainer:tabs:parallelGroupsTab'
        self.wait_super_short.until(EC.presence_of_element_located((By.ID, id)))
        webel = self.find_element_by_id(id)
        webel.click()

    def open_module_tab(self):
        id = 'detailViewData:tabContainer:tabs:modulesCourseOfStudiesTab'
        self.wait_super_short.until(EC.presence_of_element_located((By.ID, id)))
        webel = self.find_element_by_id(id)
        webel.click()

    def go_to_page(self, page_nr):
        assert page_nr > 0, 'search page must be > 0'
        js = "var event = new Event('onclick');jsf.util.chain(document.getElementById('genSearchRes:id3df798d58b4bacd9:" \
             "id3df798d58b4bacd9Navi2idx"+str(self.current_page)+"'),event,'jsf.ajax.request(\\'genSearchRes:id3df798d58b4bacd9:id3d" \
             "f798d58b4bacd9Navi2idx"+str(self.current_page)+"\\',event,{execute:\\'genSearchRes:id3df798d58b4bacd9 genSearchRes \\'" \
             ",render:\\'genSearchRes:id3df798d58b4bacd9 genSearchRes genSearchRes:messages-infobox \\',onerror:de.his." \
             "ajax.Refresher.onError,onevent:de.his.ajax.Refresher.onEvent,\\'genSearchRes:id3df798d58b4bacd9:id3df798d" \
             "58b4bacd9Navi2\\':\\'idx"+str(page_nr)+"\\',\\'javax.faces.behavior.event\\':\\'action\\'})');"
        self.execute_script(js)

        self.wait.until(lambda x: self.current_page_nr() == page_nr)
        self.current_page = page_nr

    def back(self):
        """
        Uses the Zurück button. Particularly on event pages
        :return:
        """
        #
        back_button_id = "form:dialogHeader:backButtonTop"
        self.wait.until(EC.presence_of_element_located((By.ID, back_button_id)))
        event_webelement = self.find_element_by_id(back_button_id)
        event_webelement.click()
        # TODO: Back button sometimes broken in Marvin :(
        # Especially broken when the event contains little to no information
        self.wait.until(lambda x: self.current_page_nr() == self.current_page)