import argparse
from Classes.Search import *
import traceback


def argparser():
    """
    Allowing input from the command line like:
    python3 ReadOnePage 2
    Would make this script read page number 3.
    :return: dict of arguments
    """
    # Initialise an argument parser from the argparse library
    parser = argparse.ArgumentParser()
    # Name so we can read out this argument later
    parser.add_argument('pageN', metavar='pageN', type=int, nargs=1,
                        help='Page number to be read')
    # Produces a dict of all arguments. In this case we have only one
    return parser.parse_args()

if __name__ == "__main__":
    # Get input argument
    args = argparser()
    # Unpack input page number from dict
    page_n = args.pageN[0]
    try:
        # Start on the empty search page
        search = Search()
        search.empty_search()
        # Use 300 Events per page to avoid navigating too much
        search.set_entries_count(300)
        # Read out all events on the search page and post to sql.
        search.read_search_page(page_n)
    except:
        # If anything goes wrong, write to an error file that is specific to the page it occured on.
        with open("Errors/error_page_{}.log".format(page_n), "w") as logf:
            logf.write(traceback.format_exc())
        # TODO: Find a way to append to log files, but still make clear when the last run did not raise an error