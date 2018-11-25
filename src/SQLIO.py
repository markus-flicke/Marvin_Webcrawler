import psycopg2
from psycopg2.extensions import ISOLATION_LEVEL_REPEATABLE_READ, ISOLATION_LEVEL_SERIALIZABLE


class SQLIO:
    def __init__(self):
        self.conn = psycopg2.connect("host=localhost dbname=Vorlesungsverzeichnis user=postgres password=something")
        # self.conn.set_isolation_level(ISOLATION_LEVEL_SERIALIZABLE)
        self.cur = self.conn.cursor()

    def insert(self, table, values, headers=False):
        """

        :param table: SQL table to insert into
        :param values: values in SQL query string format. i.e. String(x) -> 'x'
        :param headers: table attributes to insert into. If None -> insert into all attributes
        :return:
        """
        if headers:
            return self.execute("Insert into {} ({}) Select {}".format(table, ','.join(headers), ",".join(values)))
        return self.execute("Insert into {} values({})".format(table, ",".join(values)))

    def select(self, table, attribute = False, where = False):
        if attribute:
            query = "Select {} from {}".format(attribute, table)
        else:
            query = "Select * from {}".format(table)
        if where:
            query += ' where {}'.format(where)
        self.execute(query)
        return self.cur.fetchall()

    def commit(self):
        self.conn.commit()

    def reset(self):
        with open('../reset.sql', 'r') as file:
            commands = file.read().split(';')
            file.close()

        for command in commands:
            self.execute(command)
        self.commit()

    def execute(self, command):
        try:
            return self.cur.execute(command)
        except:
            print(command)
            raise