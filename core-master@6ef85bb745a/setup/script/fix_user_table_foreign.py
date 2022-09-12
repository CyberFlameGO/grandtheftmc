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

QUERY_LARGE_SET = ''' SELECT HEX(uuid), name FROM gtm1; '''
QUERY_SMALL_SET = ''' SELECT HEX(uuid), name FROM user; '''
QUERY_FOREIGN_SET = ''' SELECT HEX(uuid) AS gu, G.name FROM gtm1 G WHERE G.uuid NOT IN (SELECT uuid FROM user);'''

# open MySQL connection
db = MySQLdb.connect(host=DB_HOST, user=DB_USER, passwd=DB_PASS, db=DB_NAME)
# grab cursor
cur = db.cursor()

# print out variables
print("MAX_QUERY_TICK = " + str(MAX_QUERY_TICK))
print("TICK_SLEEP = " + str(TICK_SLEEP))
print("QUERY_LARGE_SET = " + str(QUERY_LARGE_SET))
print("QUERY_SMALL_SET = " + str(QUERY_SMALL_SET))
print("QUERY_FOREIGN_SET = " + str(QUERY_FOREIGN_SET))

def get_foreign_users():
	'''
	Get all the users that exist in the gtm table but not in the user table.

	Return:
		A list of pairs in the form of (uuid, name) for all the users.
	'''

	# grab cursor
	#cur = db.cursor()
	cur.execute(QUERY_FOREIGN_SET)

	users = []
	for tup in cur:

		uuid = str(tup[0])
		name = str(tup[1])

		users.append((uuid, name))

	# commit query
	# db.commit()
	# cur.close()
	
	return users

def get_large_users():
	'''
	Get all the users known on the network for the specified gtm table.

	Return:
		A list of pairs in the form of (uuid, name) for all the users.
	'''

	# grab cursor
	#cur = db.cursor()
	cur.execute(QUERY_LARGE_SET)

	users = []
	for tup in cur:

		uuid = str(tup[0])
		name = str(tup[1])

		users.append((uuid, name))

	# commit query
	# db.commit()
	# cur.close()
	
	return users

def get_small_users():
	'''
	Get all the users known on the network for the user table.

	Return:
		A list of users uuid for all the users.
	'''

	# grab cursor
	#cur = db.cursor()
	cur.execute(QUERY_SMALL_SET)

	users = []
	for tup in cur:

		uuid = str(tup[0])

		users.append(uuid)

	# commit query
	# db.commit()
	# cur.close()
	
	return users


def create_user(uuid, name):
	'''
	Create user information for the respective player.

	Args:
		uuid: The uuid of the user
		name: The name of the user
	'''
	# grab cursor
	# cur = db.cursor()
	query = '''INSERT IGNORE INTO user VALUES (UNHEX(%s), %s) ON DUPLICATE KEY UPDATE name=VALUES(name);'''
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

# get the large set of users (a list of tuples)
#large_users = get_large_users()

# get the small set of users (a list of uuid)
#small_users = get_small_users()

#creation_set = []

users_created = 0

# # for each user in gtm1
# for uuid, name in large_users:
# 	tick += 1

# 	if uuid not in small_users:
# 		print('name', name)
# 		create_user(uuid=uuid, name=name)
# 		users_created += 1

# 	if tick > MAX_QUERY_TICK:
# 		tick = 0
# 		time.sleep(TICK_SLEEP)

foreign_users = get_foreign_users()

# for each user in gtm1
for uuid, name in foreign_users:
	tick += 1

	print('name', name)
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
print('Number of users found in foreign set: ' + str(len(foreign_users)))
print('Number of users created: ' + str(users_created))

# close the connection
db.close()