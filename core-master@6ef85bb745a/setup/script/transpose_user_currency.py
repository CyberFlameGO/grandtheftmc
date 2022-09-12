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

QUERY_GET_GLOBAL_CURR = ''' SELECT uuid, crowbars, tokens FROM users; '''

QUERY_GET_CURR_GTM1 = ''' SELECT uuid, money, permits FROM gtm1; '''
QUERY_GET_CURR_GTM2 = ''' SELECT uuid, money, permits FROM gtm2; '''
QUERY_GET_CURR_GTM3 = ''' SELECT uuid, money, permits FROM gtm3; '''
QUERY_GET_CURR_GTM4 = ''' SELECT uuid, money, permits FROM gtm4; '''
QUERY_GET_CURR_GTM5 = ''' SELECT uuid, money, permits FROM gtm5; '''
QUERY_GET_CURR_GTM6 = ''' SELECT uuid, money, permits FROM gtm6; '''
CURR_QUERIES = [("GTM1", QUERY_GET_CURR_GTM1), ("GTM2", QUERY_GET_CURR_GTM2), ("GTM3", QUERY_GET_CURR_GTM3), ("GTM4", QUERY_GET_CURR_GTM4), ("GTM5", QUERY_GET_CURR_GTM5), ("GTM6", QUERY_GET_CURR_GTM6)]


# open MySQL connection
db = MySQLdb.connect(host=DB_HOST, user=DB_USER, passwd=DB_PASS, db=DB_NAME)
# grab cursor
cur = db.cursor()

# print out variables
print("MAX_QUERY_TICK = " + str(MAX_QUERY_TICK))
print("TICK_SLEEP = " + str(TICK_SLEEP))
print("QUERY_GET_GLOBAL_CURR = " + str(QUERY_GET_GLOBAL_CURR))

def get_global_currencies():
	'''
	Get all the user currencies known on the network.

	This will return multiple tuples per user.

	Return:
		A list of tuples in the form of (uuid, crowbars, tokens) for all the users.
	'''

	# grab cursor
	#cur = db.cursor()
	cur.execute(QUERY_GET_GLOBAL_CURR)

	user_currencies = []
	for tup in cur:

		uuid = string.replace(str(tup[0]), "-", "")
		crowbars = int(tup[1])
		tokens = int(tup[2])

		user_currencies.append((uuid, crowbars, tokens))

	# commit query
	# db.commit()
	# cur.close()
	
	return user_currencies

def get_local_currencies(query):
	'''
	Get all the local user currencies.

	Return:
		A list of tuples in the form of (uuid, money, permits) for all the users.
	'''

	# grab cursor
	#cur = db.cursor()
	cur.execute(query)

	user_currencies = []
	for tup in cur:

		uuid = string.replace(str(tup[0]), "-", "")
		money = int(tup[1])
		permits = int(tup[2])

		user_currencies.append((uuid, money, permits))

	# commit query
	# db.commit()
	# cur.close()
	
	return user_currencies

# CREATE TABLE IF NOT EXISTS user_currency(
# uuid BINARY(16) NOT NULL, 
# server_key VARCHAR(10) NOT NULL, 
# currency VARCHAR(16) NOT NULL, 
# amount INT NOT NULL, 
# PRIMARY KEY (uuid, server_key), 
# FOREIGN KEY (uuid) REFERENCES user(uuid) ON DELETE CASCADE
# );

def create_user_currency(uuid, server_key, currency, amount):
	'''
	Create user currency information for the respective player.

	Args:
		uuid: The uuid of the user
		server_key: The server that this currency is for, or GLOBAL
		currency: The currency to store
		amount: The amount of currency to store
	'''
	# grab cursor
	# cur = db.cursor()
	query = '''INSERT IGNORE INTO user_currency VALUES (UNHEX(%s), %s, %s, %s);'''
	data = (str(uuid), str(server_key), str(currency), int(amount))
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

global_currencies = get_global_currencies()
global_currencies_created = 0
for uuid, crowbars, tokens in global_currencies:
	tick += 1

	create_user_currency(uuid=uuid, server_key="GLOBAL", currency="CROWBAR", amount=crowbars)
	global_currencies_created += 1
	create_user_currency(uuid=uuid, server_key="GLOBAL", currency="TOKEN", amount=tokens)
	global_currencies_created += 1

	if tick > MAX_QUERY_TICK:
		tick = 0
		time.sleep(TICK_SLEEP)

num_local_currencies = 0
num_local_currencies_created = 0
for curr_query in CURR_QUERIES:
	server_key, query = curr_query

	local_currencies = get_local_currencies(query)
	num_local_currencies += len(local_currencies)
	for uuid, money, permits in local_currencies:
		tick += 1

		create_user_currency(uuid=uuid, server_key=str(server_key), currency="MONEY", amount=money)
		num_local_currencies_created += 1
		create_user_currency(uuid=uuid, server_key=str(server_key), currency="PERMIT", amount=permits)
		num_local_currencies_created += 1

		if tick > MAX_QUERY_TICK:
			tick = 0
			time.sleep(TICK_SLEEP)

db.commit()
cur.close()

run_time = time.time() - start_time	
print('Script is now complete! (' + str(run_time) + ' secs)')
print('Number of global user currencies found: ' + str(len(global_currencies)))
print('Number of global user currencies transposed: ' + str(global_currencies_created))
print()
print('Number of local user currencies found: ' + str(num_local_currencies))
print('Number of local user currencies transposed: ' + str(num_local_currencies_created))

# close the connection
db.close()