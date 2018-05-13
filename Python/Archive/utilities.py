import pandas as pd
import requests

CATALOGUE_URL = 'https://marvin.uni-marburg.de/qisserver/pages/cm/exa/coursecatalog/showCourseCatalog.xhtml?_flowId=showCourseCatalog-flow&_flowExecutionKey='
home = 'e1s1'

def get_dataframes(url):
    """
    :param url:
    :return: A list of dataframes found on site.
    """
    resp = requests.get(url)
    dfs = pd.read_html(resp.text)
    return dfs

