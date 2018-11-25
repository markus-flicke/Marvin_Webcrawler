import threading
import time

from src.Navigator import Navigator
from src.SQLIO import SQLIO
from src.Thread import Thread

if __name__ == '__main__':
    SQLIO().reset()
    base_number_threads = threading.active_count()
    n = Navigator(True)
    n.open_search()
    number_of_pages = n.max_page_nr()
    print('Max page nr.: {}'. format(number_of_pages))
    n.quit()
    threads = []
    for page_nr in range(1, number_of_pages):
        for event_nr in range(10):
            while not (threading.active_count() < 10 + base_number_threads):
                time.sleep(0.1)

            thread = Thread(name="Thread-(p={},e={})".format(page_nr, event_nr), page_nr=page_nr, event_nr=event_nr)
            thread.start()
            threads.append(thread)

    for thread in threads:
        thread.join()
    print('done')