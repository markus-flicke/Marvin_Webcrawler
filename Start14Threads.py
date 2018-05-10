import subprocess

handles = []
for i in range(3):
    handles.append(subprocess.Popen(['python3', 'ReadOnePage.py', str(i)]))