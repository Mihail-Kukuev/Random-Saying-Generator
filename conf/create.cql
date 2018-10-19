CREATE KEYSPACE random_sayings_generator WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1 };
USE random_sayings_generator;
CREATE TABLE sayings_content ( id uuid PRIMARY KEY, text text, author text );
CREATE TABLE sayings_rating ( id uuid PRIMARY KEY, likes counter, dislikes counter );
