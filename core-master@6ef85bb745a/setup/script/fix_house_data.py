#!/usr/bin/python

# local imports

# python modules
import MySQLdb
import time
import json

DB_HOST = "localhost"
DB_USER = "root"
DB_PASS = ""
DB_NAME = ""

# open MySQL connection
db = MySQLdb.connect(host=DB_HOST, user=DB_USER, passwd=DB_PASS, db=DB_NAME)

# CREATE TABLE IF NOT EXISTS gtm_house (
# id INT NOT NULL AUTO_INCREMENT, 
# house_num INT NOT NULL, 
# server_key VARCHAR(10) NOT NULL, 
# premium TINYINT(1) NOT NULL DEFAULT 0, 
# currency VARCHAR(10) NOT NULL, 
# price INT NOT NULL, 
# PRIMARY KEY (id), 
# UNIQUE INDEX (house_num, server_key, premium)
# );

def get_premium_houses():
	'''
	Get all the premium houses that exist on the network.

	Returns:
		A list of id where each id is the unique house id.
	'''

	# grab cursor
	cur = db.cursor()

	query = ''' SELECT id FROM gtm_house WHERE premium=1 ORDER BY id DESC LIMIT 1000; '''
	cur.execute(query)

	# commit query
	db.commit()

	result = []
	for tup in cur:
		result.append(int(tup[0]))

	cur.close()

	return result

# CREATE TABLE IF NOT EXISTS gtm_house_user (
# house_id INT NOT NULL, 
# uuid BINARY(16) NOT NULL, 
# is_owner TINYINT(1) NOT NULL, 
# PRIMARY KEY (house_id, uuid), 
# INDEX(uuid), 
# FOREIGN KEY (house_id) REFERENCES gtm_house(id) ON DELETE CASCADE
# );

def is_house_owned(house_id):
	'''
	Get whether or not the house is owned by a player.

	Args:
		house_id: The id of the house

	Return:
		True of the house is owned by a player, False otherwise.
	'''
	# grab cursor
	cur = db.cursor()

	query = ''' SELECT COUNT(*) FROM gtm_house_user WHERE house_id=%s; '''
	data = (house_id)
	cur.execute(query, [data])

	# commit query
	db.commit()

	num_users = 0
	for tup in cur:
		num_users = int(tup[0])

	cur.close()

	result = False
	if num_users > 0:
		result = True

	return result

# CREATE TABLE IF NOT EXISTS gtm_house_data (
# house_id INT NOT NULL, 
# hotspot_id INT NOT NULL AUTO_INCREMENT, 
# hotspot_type VARCHAR(5) NOT NULL, 
# data BLOB DEFAULT NULL, 
# PRIMARY KEY (hotspot_id), 
# INDEX (house_id), 
# FOREIGN KEY (house_id) REFERENCES gtm_house(id) ON DELETE CASCADE
# );

def get_house_data(house_id, hotspot_type):
	'''
	Get the house data for the specified house id and the hotspot_type.

	Args:
		house_id: The unique id for the house
		hotspot_type: The type of hotspot data

	Returns:
		A list of tuples in the form of (house_id, hotspot_id, hotspot_type, data).
	'''
	# grab cursor
	cur = db.cursor()

	query = ''' SELECT house_id, hotspot_id, hotspot_type, data FROM gtm_house_data WHERE house_id=%s AND hotspot_type=%s; '''
	data = (house_id, hotspot_type)
	cur.execute(query, data)

	# commit query
	db.commit()

	result = []
	for tup in cur:
		result.append((int(tup[0]), int(tup[1]), str(tup[2]), str(tup[3])))

	cur.close()
	return result

def update_house_data(house_id, hotspot_id, hotspot_type, data):
	'''
	Updates the house data in the database.

	Args:
		house_id: The id of the house
		hotspot_id: The id of the hotspot
		hotspot_type: The type of the hotspot
		data: The data json object
	'''
	# grab cursor
	cur = db.cursor()

	query = ''' UPDATE gtm_house_data SET data=%s WHERE house_id=%s AND hotspot_id=%s AND hotspot_type=%s '''
	data = (str(data), int(house_id), int(hotspot_id), str(hotspot_type))
	cur.execute(query, data)

	# commit query
	db.commit()
	cur.close()


#########
# BELOW IS THE ACTUAL SCRIPT LOGIC
#########

print('Running script fix_house_data.py...' )
print()
start_time = time.time()

# get all premium houses
premium_house_ids = get_premium_houses()
houses_changed = 0
trash_data_changed = 0
for house_id in premium_house_ids:

	# TODO re-enable
	print('Running house_id #' + str(house_id))

	# is the house owned
	owned = is_house_owned(house_id=house_id)

	# has this data for this house been changed
	changed = False

	if not owned:

		# get all TRASH data
		trash_data_list = get_house_data(house_id=house_id, hotspot_type='TRASH')
		if trash_data_list is not None and len(trash_data_list) > 0:

			# for each data
			for hid, hotspot_id, hotspot_type, data in trash_data_list:

				# convert to json
				json_data = json.loads(data)
				if 'owned' in json_data and json_data['owned'] is True:
					json_data['owned'] = False

					# convert back to string
					new_data = json.dumps(json_data)

					# update new house data
					update_house_data(house_id=hid, hotspot_id=hotspot_id, hotspot_type='TRASH', data=new_data)
					
					print('Changed owned trash for house_id #' + str(house_id))
					
					# stats
					trash_data_changed += 1
					if not changed:
						changed = True
						houses_changed += 1

run_time = time.time() - start_time
print('Number of initial premium houses = ' + str(len(premium_house_ids)))
print('Number of houses that were changed = ' + str(houses_changed))
print('Number of data that were changed = ' + str(trash_data_changed))
print('Finished script in ' + str(run_time) + ' secs.')

# close the connection
db.close()