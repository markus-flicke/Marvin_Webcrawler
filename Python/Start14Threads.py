import subprocess
import os
from time import sleep

handles = []
for i in range(2):
    # Start 14 subprocesses to search in pseudo paralell.
    # The bottleneck should be the server response time.
    # TODO: Think about why certain actions take incredibly long. e.g. Selecting 300 events per page.
    try:
        # Remove old error logs if they exist
        os.remove('Errors/error_page_{}.log'.format(i))
    except:
        pass
    # Start reading pages at slightly different times to avoid requiring too many system resources during start.
    handles.append(subprocess.Popen(['python3', 'ReadOnePage.py', str(i)]))
    sleep(3)