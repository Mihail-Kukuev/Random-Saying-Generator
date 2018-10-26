#!/usr/bin/env python

from random import randint
from cassandra import ConsistencyLevel
from cassandra.cluster import Cluster

random_id_query = 'SELECT id FROM sayings_content WHERE token(id) > token(uuid()) LIMIT 1'
rate_request_format = 'POST||/sayings/%s/rate||rate||{"rate": %s1}\r\n'
random_request = 'GET||/sayings/random||random\r\n'


def prepare_random_id_stmt(session):
    stmt = session.prepare(random_id_query)
    stmt.consistency_level = ConsistencyLevel.ONE
    return stmt


def execute_random_id_stmt(session, stmt):
    while True:
        row = session.execute(stmt).one()
        if row is not None:
            return row.id


def build_rate_request(format, saying_id):
    sign = '+' if randint(0, 1) else '-'
    return format % (saying_id, sign)


def generate_rate_requests(session, filename, amount):
    stmt = prepare_random_id_stmt(session)
    with open(filename, 'w') as file:
        for i in range(amount):
            saying_id = execute_random_id_stmt(session, stmt)
            file.write(build_rate_request(rate_request_format, saying_id))


def generate_mixed_requests(session, filename, amount):
    stmt = prepare_random_id_stmt(session)
    with open(filename, 'w') as file:
        for i in range(amount):
            if i % 2 == 1 and randint(0, 1):
                saying_id = execute_random_id_stmt(session, stmt)
                line = build_rate_request(rate_request_format, saying_id)
            else:
                line = random_request
            file.write(line)


def generate_requests():
    cluster = Cluster(['localhost'], port=9042)
    session = cluster.connect('random_sayings_generator')
    generate_rate_requests(session, 'rate_requests.txt', 1000)
    generate_mixed_requests(session, 'mixed_requests.txt', 1000)
    cluster.shutdown()


if __name__ == "__main__":
    generate_requests()
