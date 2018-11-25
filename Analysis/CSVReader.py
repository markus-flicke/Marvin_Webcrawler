import pandas as pd
from sqlalchemy import create_engine

def main():
    df = pd.read_csv('./Analysis/regelstudienplaene.csv', delimiter = ',') #,encoding = "ISO-8859-1"
    df = df.drop('Lfd.', axis = 1)
    df.head()
    engine = create_engine('postgresql://postgres:something@localhost:5432/Vorlesungsverzeichnis')
    df.columns = df.columns.map(lambda x: x.lower())
    df.to_sql('studienplaene', engine, if_exists='append', index = False)

if __name__=='__main__':
    main()