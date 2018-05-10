import argparse
from Classes.Search import *
import traceback

def argparser():
    parser = argparse.ArgumentParser()
    parser.add_argument('pageN', metavar='pageN', type=int, nargs=1,
                        help='Page number to be read')
    return parser.parse_args()

if __name__ == "__main__":
    args = argparser()
    page_n = args.pageN[0]
    try:
        search = Search()
        search.empty_search()
        search.set_entries_count(300)
        search.read_search_page(page_n)
    except:
        with open("Errors/error_page_{}.log".format(page_n), "w") as logf:
            logf.write(traceback.format_exc())