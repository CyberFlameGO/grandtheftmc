#!/usr/bin/python

# local imports

# python modules
import MySQLdb
import string
import time
import sys

DB_HOST = "localhost"
DB_USER = "root"
DB_PASS = ""
DB_NAME = ""
# number of queries per interval
MAX_QUERY_TICK = 10000000
# how long in seconds we sleep for
TICK_SLEEP = 1

QUERY_GET_OLD_UUID_GTM1 = ''' SELECT uuid FROM gtm1; '''
QUERY_GET_OLD_UUID_GTM2 = ''' SELECT uuid FROM gtm2; '''
QUERY_GET_OLD_UUID_GTM3 = ''' SELECT uuid FROM gtm3; '''
QUERY_GET_OLD_UUID_GTM4 = ''' SELECT uuid FROM gtm4; '''
QUERY_GET_OLD_UUID_GTM5 = ''' SELECT uuid FROM gtm5; '''
QUERY_GET_OLD_UUID_GTM6 = ''' SELECT uuid FROM gtm6; '''
GET_UUID_QUERIES = [("GTM1", QUERY_GET_OLD_UUID_GTM1), ("GTM2", QUERY_GET_OLD_UUID_GTM2), ("GTM3", QUERY_GET_OLD_UUID_GTM3), ("GTM4", QUERY_GET_OLD_UUID_GTM4), ("GTM5", QUERY_GET_OLD_UUID_GTM5), ("GTM6", QUERY_GET_OLD_UUID_GTM6)]

QUERY_UPDATE_UUID_GTM1 = ''' UPDATE gtm1 SET new_uuid=UNHEX(%s) WHERE uuid=%s; '''
QUERY_UPDATE_UUID_GTM2 = ''' UPDATE gtm2 SET new_uuid=UNHEX(%s) WHERE uuid=%s; '''
QUERY_UPDATE_UUID_GTM3 = ''' UPDATE gtm3 SET new_uuid=UNHEX(%s) WHERE uuid=%s; '''
QUERY_UPDATE_UUID_GTM4 = ''' UPDATE gtm4 SET new_uuid=UNHEX(%s) WHERE uuid=%s; '''
QUERY_UPDATE_UUID_GTM5 = ''' UPDATE gtm5 SET new_uuid=UNHEX(%s) WHERE uuid=%s; '''
QUERY_UPDATE_UUID_GTM6 = ''' UPDATE gtm6 SET new_uuid=UNHEX(%s) WHERE uuid=%s; '''
UPDATE_UUID_QUERIES = {}
UPDATE_UUID_QUERIES['GTM1'] = QUERY_UPDATE_UUID_GTM1
UPDATE_UUID_QUERIES['GTM2'] = QUERY_UPDATE_UUID_GTM2
UPDATE_UUID_QUERIES['GTM3'] = QUERY_UPDATE_UUID_GTM3
UPDATE_UUID_QUERIES['GTM4'] = QUERY_UPDATE_UUID_GTM4
UPDATE_UUID_QUERIES['GTM5'] = QUERY_UPDATE_UUID_GTM5
UPDATE_UUID_QUERIES['GTM6'] = QUERY_UPDATE_UUID_GTM6

# open MySQL connection
db = MySQLdb.connect(host=DB_HOST, user=DB_USER, passwd=DB_PASS, db=DB_NAME)
# grab cursor
cur = db.cursor()

# print out variables
print("MAX_QUERY_TICK = " + str(MAX_QUERY_TICK))
print("TICK_SLEEP = " + str(TICK_SLEEP))
print("QUERY_GET_OLD_UUID_GTM1 = " + str(QUERY_GET_OLD_UUID_GTM1))
print("QUERY_GET_OLD_UUID_GTM2 = " + str(QUERY_GET_OLD_UUID_GTM2))
print("QUERY_GET_OLD_UUID_GTM3 = " + str(QUERY_GET_OLD_UUID_GTM3))
print("QUERY_GET_OLD_UUID_GTM4 = " + str(QUERY_GET_OLD_UUID_GTM4))
print("QUERY_GET_OLD_UUID_GTM5 = " + str(QUERY_GET_OLD_UUID_GTM5))
print("QUERY_GET_OLD_UUID_GTM6 = " + str(QUERY_GET_OLD_UUID_GTM6))

def get_old_uuid(query):
	'''
	Get all the old uuids.

	Return:
		A list of tuples in the form of (old_uuid, new_uuid).
	'''

	# grab cursor
	#cur = db.cursor()
	cur.execute(query)

	uuid_list = []
	for tup in cur:

		old_uuid = str(tup[0])
		new_uuid = string.replace(str(tup[0]), "-", "")

		uuid_list.append((old_uuid, new_uuid))

	# commit query
	# db.commit()
	# cur.close()
	
	return uuid_list

def create_uuid(old_uuid, new_uuid, query):
	'''
	Create newly formatted uuid.

	Args:
		old_uuid: The old uuid, with hyphens.
		new_uuid: The new uuid, without hyphens.
		query: The query to use
	'''

	# grab cursor
	# cur = db.cursor()
	data = (str(new_uuid), str(old_uuid))
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

num_uuid_records = 0
num_uuid_records_created = 0
for get_uuid_query in GET_UUID_QUERIES:
	server_key, query = get_uuid_query

	uuid_list = get_old_uuid(query)
	num_uuid_records += len(uuid_list)
	for old_uuid, new_uuid in uuid_list:
		tick += 1

		# TODO test remove
		print("old_uuid", old_uuid)

		create_uuid(old_uuid=old_uuid, new_uuid=new_uuid, query=UPDATE_UUID_QUERIES[server_key])
		num_uuid_records_created += 1

		if tick > MAX_QUERY_TICK:
			tick = 0
			time.sleep(TICK_SLEEP)

db.commit()
cur.close()

run_time = time.time() - start_time	
print('Script is now complete! (' + str(run_time) + ' secs)')
print('Number of user uuids found: ' + str(num_uuid_records))
print('Number of user ammo records transposed: ' + str(num_uuid_records_created))

# close the connection
db.close()