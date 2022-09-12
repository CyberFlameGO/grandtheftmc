#!/usr/bin/python

# local imports

# python modules
import MySQLdb
import string
import time

DB_HOST = "localhost"
DB_USER = "root"
DB_PASS = ""
DB_NAME = ""
# number of queries per interval
MAX_QUERY_TICK = 10000000
# how long in seconds we sleep for
TICK_SLEEP = 1

QUERY_GET_USERS = ''' SELECT uuid, lastname FROM users; '''

# open MySQL connection
db = MySQLdb.connect(host=DB_HOST, user=DB_USER, passwd=DB_PASS, db=DB_NAME)
# grab cursor
cur = db.cursor()

# print out variables
print("MAX_QUERY_TICK = " + str(MAX_QUERY_TICK))
print("TICK_SLEEP = " + str(TICK_SLEEP))
print("QUERY_GET_USERS = " + str(QUERY_GET_USERS))

def get_users():
	'''
	Get all the users known on the network.

	Return:
		A list of pairs in the form of (uuid, name) for all the users.
	'''

	# grab cursor
	#cur = db.cursor()
	cur.execute(QUERY_GET_USERS)

	users = []
	for tup in cur:

		uuid = string.replace(str(tup[0]), "-", "")
		name = str(tup[1])

		users.append((uuid, name))

	# commit query
	# db.commit()
	# cur.close()
	
	return users

# CREATE TABLE IF NOT EXISTS user(
# uuid BINARY(16) NOT NULL, 
# name VARCHAR(16) NOT NULL, 
# PRIMARY KEY (uuid), 
# INDEX (name)
# );

def create_user(uuid, name):
	'''
	Create user information for the respective player.

	Args:
		uuid: The uuid of the user
		name: The name of the user
	'''
	# grab cursor
	# cur = db.cursor()
	query = '''INSERT IGNORE INTO user VALUES (UNHEX(%s), %s);'''
	data = (str(uuid), str(name))
	cur.execute(query, data)

	# commit query
	# db.commit()
	# cur.close()

#########
# BELOW IS THE ACTUAL SCRIPT LOGIC
#########

print('Running script...')
start_time = time.time()

# limit the amount of queries to the database per interval
tick = 0

users = get_users()
users_created = 0
for uuid, name in users:
	tick += 1

	create_user(uuid=uuid, name=name)
	users_created += 1

	if tick > MAX_QUERY_TICK:
		tick = 0
		time.sleep(TICK_SLEEP)

db.commit()
cur.close()

# convert ranks

run_time = time.time() - start_time	
print('Script is now complete! (' + str(run_time) + ' secs)')
print('Number of users found: ' + str(len(users)))
print('Number of users transposed: ' + str(users_created))

# close the connection
db.close()