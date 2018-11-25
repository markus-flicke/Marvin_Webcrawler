from selenium import webdriver


class Driver:
    CHROME_DRIVERPATH = "../webdriver/chromedriver"

    def __init__(self, headless = False):
        options = webdriver.ChromeOptions()
        if headless:
            options.add_argument("--headless")
        try:
            self.d = webdriver.Chrome(self.CHROME_DRIVERPATH, options=options)
        except:
            self.d = webdriver.Chrome(self.CHROME_DRIVERPATH[1:], options=options)

    def get(self, url):
        self.d.get(url)

    def find_element_by_id(self, id):
        return self.d.find_element_by_id(id)

    def find_elements_by_id(self, id):
        return self.d.find_elements_by_id(id)

    def find_element_by_class_name(self, name):
        return self.d.find_element_by_class_name(name)

    def find_elements_by_class_name(self, name):
        return self.d.find_elements_by_class_name(name)

    def find_element(self, by, value = None):
        return self.d.find_element(by, value)

    def get_screenshot_as_file(self, filenname):
        self.d.get_screenshot_as_file(filenname)

    def execute_script(self, script):
        return self.d.execute_script(script)

    def page_source(self):
        return self.d.page_source

    def quit(self):
        self.d.quit()