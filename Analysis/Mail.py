from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
import smtplib

class Mail():
    """
    copied from: https://code.tutsplus.com/tutorials/sending-emails-in-python-with-smtp--cms-29975
    """
    @staticmethod
    def send(message, to = "MarvinWebcrawler@gmail.com", subject = ''):
        msg = MIMEMultipart()
        password = "MarvinWebcrawler1!"
        msg['From'] = "MarvinWebcrawler@gmail.com"
        msg['To'] = to
        msg['Subject'] = subject

        msg.attach(MIMEText(message, 'html'))
        server = smtplib.SMTP('smtp.gmail.com: 587')
        server.starttls()
        server.login(msg['From'], password)
        server.sendmail(msg['From'], msg['To'], msg.as_string())
        server.quit()