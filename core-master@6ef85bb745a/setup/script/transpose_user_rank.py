#!/usr/bin/python

# local imports

# python modules
import MySQLdb
import string
import time
import datetime

DB_HOST = "localhost"
DB_USER = "root"
DB_PASS = ""
DB_NAME = ""
# number of queries per interval
MAX_QUERY_TICK = 10000000
# how long in seconds we sleep for
TICK_SLEEP = 1

QUERY_GET_USER_RANKS = ''' SELECT uuid, userrank, trialRank, trialRankExpiry FROM users; '''

# open MySQL connection
db = MySQLdb.connect(host=DB_HOST, user=DB_USER, passwd=DB_PASS, db=DB_NAME)
# grab cursor
cur = db.cursor()

# print out variables
print("MAX_QUERY_TICK = " + str(MAX_QUERY_TICK))
print("TICK_SLEEP = " + str(TICK_SLEEP))
print("QUERY_GET_USER_RANKS = " + str(QUERY_GET_USER_RANKS))

def get_user_ranks():
	'''
	Get all the user ranks known on the network.

	Return:
		A list of pairs in the form of (uuid, rank) for all the users.
	'''

	# grab cursor
	#cur = db.cursor()
	cur.execute(QUERY_GET_USER_RANKS)

	ranks = []
	for tup in cur:

		uuid = string.replace(str(tup[0]), "-", "")
		rank = str(tup[1])
		trial_rank = tup[2]
		trial_rank_expire = long(tup[3])

		ranks.append((uuid, rank, trial_rank, trial_rank_expire))

	# commit query
	# db.commit()
	# cur.close()
	
	return ranks

# CREATE TABLE IF NOT EXISTS user_profile(
# uuid BINARY(16) NOT NULL, 
# server_key VARCHAR(10) NOT NULL, 
# rank VARCHAR(10) NOT NULL, 
# PRIMARY KEY (uuid, server_key), 
# FOREIGN KEY (uuid) REFERENCES user(uuid) ON DELETE CASCADE
# );

def create_user_profile(uuid, server_key, rank):
	'''
	Create user profile information for the respective player.

	Args:
		uuid: The uuid of the user
		server_key: The key of the server
		rank: The user's rank for that server
	'''
	# grab cursor
	# cur = db.cursor()
	query = '''INSERT IGNORE INTO user_profile VALUES (UNHEX(%s), %s, %s);'''
	data = (str(uuid), str(server_key), str(rank))
	cur.execute(query, data)

	# commit query
	# db.commit()
	# cur.close()

# CREATE TABLE IF NOT EXISTS user_trial_rank(
# uuid BINARY(16) NOT NULL, 
# server_key VARCHAR(10) NOT NULL, 
# rank VARCHAR(10) NOT NULL, 
# creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
# expire_at TIMESTAMP NOT NULL, 
# PRIMARY KEY (uuid, server_key), 
# FOREIGN KEY (uuid) REFERENCES user(uuid) ON DELETE CASCADE
# );

def create_user_trial_rank(uuid, server_key, rank, expire_at):
	'''
	Create user trial rank information for the respective player.

	Args:
		uuid: The uuid of the user
		server_key: The key of the server
		rank: The user's rank for that server
		expire_at: When this rank expires
	'''
	#print('expire_at', expire_at)
	# convert from epoch time "1516038023138" (in msecs) to secs
	expire_secs = expire_at // 1000
	#print('expire_secs', expire_secs)
	# convert to time object (takes secs)
	expire = datetime.datetime.fromtimestamp(expire_secs)
	#print('expire', expire)

	# grab cursor
	# cur = db.cursor()
	query = '''INSERT IGNORE INTO user_trial_rank (uuid, server_key, rank, expire_at) VALUES (UNHEX(%s), %s, %s, %s);'''
	data = (str(uuid), str(server_key), str(rank), expire)
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

ranks = get_user_ranks()
ranks_created = 0
trial_ranks_created = 0
for uuid, rank, trial_rank, trial_rank_expire in ranks:
	tick += 1

	# create the user rank
	create_user_profile(uuid=uuid, server_key="GLOBAL", rank=rank)
	ranks_created += 1

	if trial_rank is not None:
		create_user_trial_rank(uuid=uuid, server_key="GLOBAL", rank=trial_rank, expire_at=trial_rank_expire)
		trial_ranks_created += 1

	if tick > MAX_QUERY_TICK:
		tick = 0
		time.sleep(TICK_SLEEP)

db.commit()
cur.close()

run_time = time.time() - start_time	
print('Script is now complete! (' + str(run_time) + ' secs)')
print('Number of users ranks found: ' + str(len(ranks)))
print('Number of users ranks transposed: ' + str(ranks_created))
print('Number of users trial ranks transposed: ' + str(trial_ranks_created))

# close the connection
db.close()