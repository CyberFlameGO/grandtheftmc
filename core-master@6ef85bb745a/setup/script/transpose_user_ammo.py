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

QUERY_GET_AMMO_GTM1 = ''' SELECT uuid, pistol, smg, shotgun, assault_rifle, mg, sniper, rocket, minigun FROM gtm1; '''
QUERY_GET_AMMO_GTM2 = ''' SELECT uuid, pistol, smg, shotgun, assault_rifle, mg, sniper, rocket, minigun FROM gtm2; '''
QUERY_GET_AMMO_GTM3 = ''' SELECT uuid, pistol, smg, shotgun, assault_rifle, mg, sniper, rocket, minigun FROM gtm3; '''
QUERY_GET_AMMO_GTM4 = ''' SELECT uuid, pistol, smg, shotgun, assault_rifle, mg, sniper, rocket, minigun FROM gtm4; '''
QUERY_GET_AMMO_GTM5 = ''' SELECT uuid, pistol, smg, shotgun, assault_rifle, mg, sniper, rocket, minigun FROM gtm5; '''
QUERY_GET_AMMO_GTM6 = ''' SELECT uuid, pistol, smg, shotgun, assault_rifle, mg, sniper, rocket, minigun FROM gtm6; '''
AMMO_QUERIES = [("GTM1", QUERY_GET_AMMO_GTM1), ("GTM2", QUERY_GET_AMMO_GTM2), ("GTM3", QUERY_GET_AMMO_GTM3), ("GTM4", QUERY_GET_AMMO_GTM4), ("GTM5", QUERY_GET_AMMO_GTM5), ("GTM6", QUERY_GET_AMMO_GTM6)]


# open MySQL connection
db = MySQLdb.connect(host=DB_HOST, user=DB_USER, passwd=DB_PASS, db=DB_NAME)
# grab cursor
cur = db.cursor()

# print out variables
print("MAX_QUERY_TICK = " + str(MAX_QUERY_TICK))
print("TICK_SLEEP = " + str(TICK_SLEEP))
print("QUERY_GET_AMMO_GTM1 = " + str(QUERY_GET_AMMO_GTM1))
print("QUERY_GET_AMMO_GTM2 = " + str(QUERY_GET_AMMO_GTM2))
print("QUERY_GET_AMMO_GTM3 = " + str(QUERY_GET_AMMO_GTM3))
print("QUERY_GET_AMMO_GTM4 = " + str(QUERY_GET_AMMO_GTM4))
print("QUERY_GET_AMMO_GTM5 = " + str(QUERY_GET_AMMO_GTM5))
print("QUERY_GET_AMMO_GTM6 = " + str(QUERY_GET_AMMO_GTM6))

def get_ammo(query):
	'''
	Get all the ammo.

	Return:
		A list of tuples in the form of (uuid, pistol, smg, shotgun, assault_rifle, mg, sniper, rocket, minigun) for all the users.
	'''

	# grab cursor
	#cur = db.cursor()
	cur.execute(query)

	user_ammo = []
	for tup in cur:

		uuid = string.replace(str(tup[0]), "-", "")
		pistol = int(tup[1])
		smg = int(tup[2])
		shotgun = int(tup[3])
		assault_rifle = int(tup[4])
		mg = int(tup[5])
		sniper = int(tup[6])
		rocket = int(tup[7])
		minigun = int(tup[8])

		user_ammo.append((uuid, pistol, smg, shotgun, assault_rifle, mg, sniper, rocket, minigun))

	# commit query
	# db.commit()
	# cur.close()
	
	return user_ammo

# CREATE TABLE IF NOT EXISTS user_ammo(
# uuid BINARY(16) NOT NULL, 
# server_key VARCHAR(10) NOT NULL, 
# ammo VARCHAR(16) NOT NULL, 
# amount INT NOT NULL, 
# PRIMARY KEY (uuid, server_key, ammo), 
# FOREIGN KEY (uuid) REFERENCES user(uuid) ON DELETE CASCADE
# );

def create_user_ammo(uuid, server_key, ammo, amount):
	'''
	Create user ammo information for the user.

	Args:
		uuid: The uuid of the user
		server_key: The server that this ammo is for
		ammo: The ammo to store
		amount: The amount of ammo to store
	'''
	# grab cursor
	# cur = db.cursor()
	query = '''INSERT IGNORE INTO user_ammo VALUES (UNHEX(%s), %s, %s, %s);'''
	data = (str(uuid), str(server_key), str(ammo), int(amount))
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

num_ammo_records = 0
num_ammo_records_created = 0
for ammo_query in AMMO_QUERIES:
	server_key, query = ammo_query

	ammo_record = get_ammo(query)
	num_ammo_records += len(ammo_record)
	for uuid, pistol, smg, shotgun, assault_rifle, mg, sniper, rocket, minigun in ammo_record:
		tick += 1

		create_user_ammo(uuid=uuid, server_key=str(server_key), ammo="PISTOL", amount=pistol)
		num_ammo_records_created += 1
		create_user_ammo(uuid=uuid, server_key=str(server_key), ammo="SMG", amount=smg)
		num_ammo_records_created += 1
		create_user_ammo(uuid=uuid, server_key=str(server_key), ammo="SHOTGUN", amount=shotgun)
		num_ammo_records_created += 1
		create_user_ammo(uuid=uuid, server_key=str(server_key), ammo="ASSAULT_RIFLE", amount=assault_rifle)
		num_ammo_records_created += 1
		create_user_ammo(uuid=uuid, server_key=str(server_key), ammo="MG", amount=mg)
		num_ammo_records_created += 1
		create_user_ammo(uuid=uuid, server_key=str(server_key), ammo="SNIPER", amount=sniper)
		num_ammo_records_created += 1
		create_user_ammo(uuid=uuid, server_key=str(server_key), ammo="ROCKET", amount=rocket)
		num_ammo_records_created += 1
		create_user_ammo(uuid=uuid, server_key=str(server_key), ammo="MINIGUN", amount=minigun)
		num_ammo_records_created += 1

		if tick > MAX_QUERY_TICK:
			tick = 0
			time.sleep(TICK_SLEEP)

db.commit()
cur.close()

run_time = time.time() - start_time	
print('Script is now complete! (' + str(run_time) + ' secs)')
print('Number of user ammo records found: ' + str(num_ammo_records))
print('Number of user ammo records transposed: ' + str(num_ammo_records_created))

# close the connection
db.close()