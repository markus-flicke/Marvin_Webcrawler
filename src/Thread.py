import threading

from src.EventWriter import EventWriter
from src.EventReader import EventReader
from src.Navigator import Navigator


class Thread(threading.Thread):
    def __init__(self, name, page_nr, event_nr):
        super().__init__()
        self.page_nr = page_nr
        self.event_nr = event_nr
        self.name = name

    def run(self):
        print("{} started!".format(self.name) )
        try:
            self.crawl_event(self.page_nr, self.event_nr)
        except:
            print("{} failed!".format(self.name))
            raise
        print("{} finished!".format(self.name))

    def crawl_event(self, page_nr, event_nr):
        n = Navigator(True)
        try:
            n.open_search()
            n.go_to_page(page_nr)
            n.open_event(event_nr)
            e = EventReader(n)
            event = e.read()
            EventWriter.write(event)
        finally:
            n.quit()