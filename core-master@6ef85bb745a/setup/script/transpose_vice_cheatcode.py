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

QUERY_GET_USER_DATA = ''' SELECT uuid, cheatcodes FROM vice1 WHERE cheatcodes IS NOT NULL; '''
#QUERY_GET_USER_DATA = ''' SELECT uuid, cheatcodes FROM vice1 WHERE cheatcodes IS NOT NULL AND name='Micatchu'; '''
QUERY_UPDATE_USER_DATA = ''' UPDATE vice2 SET cheatcodes = %s WHERE uuid = %s AND cheatcodes IS NULL; '''
#QUERY_UPDATE_USER_DATA = ''' UPDATE vice2 SET cheatcodes = %s WHERE uuid = %s; '''

# open MySQL connection
db = MySQLdb.connect(host=DB_HOST, user=DB_USER, passwd=DB_PASS, db=DB_NAME)
# grab cursor
cur = db.cursor()

# print out variables
print("MAX_QUERY_TICK = " + str(MAX_QUERY_TICK))
print("TICK_SLEEP = " + str(TICK_SLEEP))
print("QUERY_GET_USER_DATA = " + str(QUERY_GET_USER_DATA))
print("QUERY_UPDATE_USER_DATA = " + str(QUERY_UPDATE_USER_DATA))

def get_user_data():
	'''
	Get all the user data known on the network.

	Return:
		A list of pairs in the form of (uuid, cheatcode) for all the users.
	'''

	# execute the query
	cur.execute(QUERY_GET_USER_DATA)

	users = []
	for tup in cur:

		uuid = str(tup[0])
		cheatcodes = str(tup[1])

		users.append((uuid, cheatcodes))

	# commit query
	# db.commit()
	# cur.close()
	
	return users

def update_user_data(uuid, cheatcodes):
	'''
	Update user information for the respective user.

	Args:
		uuid: The uuid of the user
		cheatcodes: The cheatcodes for the user
	'''
	# grab cursor
	# cur = db.cursor()
	data = (str(cheatcodes), str(uuid))
	cur.execute(QUERY_UPDATE_USER_DATA, data)

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

users = get_user_data()
users_modified = 0
for uuid, cheatcodes in users:
	tick += 1

	print(uuid)
	print(cheatcodes)
	update_user_data(uuid=uuid, cheatcodes=cheatcodes)
	users_modified += 1

	if tick > MAX_QUERY_TICK:
		tick = 0
		time.sleep(TICK_SLEEP)

db.commit()
cur.close()

# convert ranks

run_time = time.time() - start_time	
print('Script is now complete! (' + str(run_time) + ' secs)')
print('Number of vice cheatcode user data found: ' + str(len(users)))
print('Number of vice cheatcode user data transposed: ' + str(users_modified))

# close the connection
db.close()