import psycopg2

class SQLIO:
    def __init__(self):
        self.conn = psycopg2.connect("host=localhost dbname=Vorlesungsverzeichnis user=postgres password=something")
        self.cur = self.conn.cursor()

    def insert(self, table, values, headers=False):
        if headers:
            return self.cur.execute("Insert into {} ({}) Select {}".format(table, ','.join(headers), ",".join(values)))
        return self.cur.execute("Insert into {} values({})".format(table, ",".join(values)))

    def select(self, table, attribute = False, where = False):
        if attribute:
            query = "Select {} from {}".format(attribute, table)
        else:
            query = "Select * from {}".format(table)
        if where:
            query += ' where {}'.format(where)
        self.cur.execute(query)
        return self.cur.fetchall()

    def commit(self):
        self.conn.commit()

    def reset(self):
        with open('../reset.sql', 'r') as file:
            commands = file.read().split(';')
            file.close()

        for command in commands:
            self.cur.execute(command)
        self.commit()