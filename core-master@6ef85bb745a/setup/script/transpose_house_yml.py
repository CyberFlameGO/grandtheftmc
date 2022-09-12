#!/usr/bin/python

# local imports

# python modules
import yaml
import MySQLdb
import string
import time
import json

DB_HOST = "localhost"
DB_USER = "root"
DB_PASS = ""
DB_NAME = ""
# number of queries per interval
MAX_QUERY_TICK = 10000000
# how long in seconds we sleep for
TICK_SLEEP = 1

SERVER_KEY = "GTM0"
# mutated by script
PREMIUM = True

# print out variables
print("MAX_QUERY_TICK = " + str(MAX_QUERY_TICK))
print("TICK_SLEEP = " + str(TICK_SLEEP))
print("SERVER_KEY = " + str(SERVER_KEY))

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

def create_house(house_num, server_key, premium, currency, price):
	'''
	Create the new representation of the house in the new table.

	Args:
		house_num: The number id of the house
		server_key: The server key, i.e. gtm1
		premium: Whether or not this is a premium house
		currency: The currency for purchases
		price: The cost of the purchase

	Return:
		The id of the house that was just created.
	'''

	# grab cursor
	cur = db.cursor()

	query = ''' INSERT INTO gtm_house (house_num, server_key, premium, currency, price) VALUES (%s, %s, %s, %s, %s); '''
	data = (int(house_num), str(server_key), bool(premium), str(currency), int(price))
	cur.execute(query, data)

	# commit query
	db.commit()

	generated_house_id = cur.lastrowid
	cur.close()

	return generated_house_id

# CREATE TABLE IF NOT EXISTS gtm_house_data (
# house_id INT NOT NULL, 
# hotspot_id INT NOT NULL AUTO_INCREMENT, 
# hotspot_type VARCHAR(5) NOT NULL, 
# data BLOB DEFAULT NULL, 
# PRIMARY KEY (hotspot_id), 
# INDEX (house_id), 
# FOREIGN KEY (house_id) REFERENCES gtm_house(id) ON DELETE CASCADE
# );

def create_house_data(house_id, hotspot_type, data):
	'''
	Create a new house data record in the database for the given house.

	Args:
		house_id: The id of the house
		hotspot_type: The type of the hotspot
		data: The data representation for the hotspot
	'''

	# grab cursor
	cur = db.cursor()

	query = ''' INSERT INTO gtm_house_data (house_id, hotspot_type, data) VALUES (%s, %s, %s); '''
	data = (int(house_id), str(hotspot_type), str(data))
	cur.execute(query, data)

	# commit query
	db.commit()
	cur.close()

# CREATE TABLE IF NOT EXISTS gtm_house_user (
# house_id INT NOT NULL, 
# uuid BINARY(16) NOT NULL, 
# is_owner TINYINT(1) NOT NULL, 
# PRIMARY KEY (house_id, uuid), 
# INDEX(uuid), 
# FOREIGN KEY (house_id) REFERENCES gtm_house(id) ON DELETE CASCADE
# );

def create_house_user(house_id, uuid, is_owner):
	'''
	Create a new house user record in the database for the specified house.

	Args:
		house_id: The id of the house
		uuid: The uuid of the user for the house
		is_owner: Whether the user owns the house
	'''

	# grab cursor
	cur = db.cursor()

	query = ''' INSERT INTO gtm_house_user (house_id, uuid, is_owner) VALUES (%s, UNHEX(%s), %s); '''
	data = (int(house_id), str(uuid), bool(is_owner))
	cur.execute(query, data)

	# commit query
	db.commit()
	cur.close()

def run_script(file_name):
	'''
	Run the tranpose of the house script.

	Args:
		file_name: The name of the file to read from.
	'''

	num_houses = 0
	houses_created = 0

	# lets get a list of keys that we know about
	with open(file_name, 'r') as stream:
		try:

			# get the data
			data = yaml.load(stream)
			num_houses = len(data)

			for house_num in data:

				print('Running house #' + str(house_num))

				# get the dict for the house data
				house_data = data[house_num]
				currency = None
				price = None
				owner = None

				# if 'blocks' not in house_data:
				# 	continue

				if 'permits' in house_data:
					price = int(house_data['permits'])
					currency = "PERMIT"
				elif 'price' in house_data:
					price = int(house_data['price'])
					currency = "MONEY"

				if 'owner' in house_data:
					owner = string.replace(str(house_data['owner']), "-", "")

				# create the generic house data
				generated_house_id = int(create_house(house_num=int(house_num), server_key=str(SERVER_KEY), premium=bool(PREMIUM), currency=currency, price=price))

				# if valid generated house
				if generated_house_id is not None and generated_house_id > 0:

					houses_created += 1

					# if valid owner, add as a user
					if owner is not None:
						create_house_user(house_id=generated_house_id, uuid=owner, is_owner=True)

					# parse the guests of this house
					if 'guests' in house_data:
						for guest in house_data['guests']:
							guest_uuid = string.replace(str(guest), "-", "")
							create_house_user(house_id=generated_house_id, uuid=guest_uuid, is_owner=False)

					# parse the chests of this house
					if 'chests' in house_data:
						chest_data = house_data['chests']

						# for each chest
						for chest_id in chest_data:

							# a chest can have multiple locations
							chest_loc1 = None
							chest_loc2 = None
							if 'loc1' in chest_data[chest_id]:
								chest_loc1 = str(chest_data[chest_id]['loc1'])
							if 'loc2' in chest_data[chest_id]:
								chest_loc2 = str(chest_data[chest_id]['loc2'])

							# build the json for this object
							chest_json = {}
							chest_json['id'] = int(chest_id)
							if chest_loc1 is not None:
								chest_json['loc1'] = str(chest_loc1)
							if chest_loc2 is not None:
								chest_json['loc2'] = str(chest_loc2)

							# create chest entry
							create_house_data(house_id=generated_house_id, hotspot_type="CHEST", data=str(json.dumps(chest_json)))

					if 'doors' in house_data:
						door_data = house_data['doors']

						for door_id in door_data:

							loc = None
							insideLoc = None
							outsideLoc = None
							if 'location' in door_data[door_id]:
								loc = str(door_data[door_id]['location'])
							if 'insideLocation' in door_data[door_id]:
								insideLoc = str(door_data[door_id]['insideLocation'])
							if 'outsideLocation' in door_data[door_id]:
								outsideLoc = str(door_data[door_id]['outsideLocation'])

							# build the json for this object
							door_json = {}
							door_json['id'] = int(door_id)
							if loc is not None:
								door_json['location'] = str(loc)
							if insideLoc is not None:
								door_json['insideLocation'] = str(insideLoc)
							if outsideLoc is not None:
								door_json['outsideLocation'] = str(outsideLoc)

							# create chest entry
							create_house_data(house_id=generated_house_id, hotspot_type="DOOR", data=str(json.dumps(door_json)))

					if 'signs' in house_data:
						sign_data = house_data['signs']

						for sign_loc in sign_data:
							sign_json = {}
							sign_json['loc'] = str(sign_loc)

							# create sign entry
							create_house_data(house_id=generated_house_id, hotspot_type="SIGN", data=str(json.dumps(sign_json)))

					if 'blocks' in house_data:
						block_data = house_data['blocks']

						for block_loc in block_data:

							block_info = None
							if 'default' in block_data[block_loc]:
								block_info = str(block_data[block_loc]['default'])

							# build the json for this object
							block_json = {}
							block_json['loc'] = str(block_loc)
							if block_info is not None:
								block_json['block_data'] = str(block_info)

							# create trashcan entry
							create_house_data(house_id=generated_house_id, hotspot_type="BLOCK", data=str(json.dumps(block_json)))

					if 'trashcans' in house_data:
						trash_data = house_data['trashcans']

						for trashcan_id in trash_data:

							loc = None
							owned = None
							if 'loc' in trash_data[trashcan_id]:
								loc = str(trash_data[trashcan_id]['loc'])
							if 'owned' in trash_data[trashcan_id]:
								owned = bool(trash_data[trashcan_id]['owned'])

							# build the json for this object
							trash_json = {}
							trash_json['id'] = int(trashcan_id)
							if loc is not None:
								trash_json['loc'] = str(loc)
							if owned is not None:
								trash_json['owned'] = bool(owned)

							# create trashcan entry
							create_house_data(house_id=generated_house_id, hotspot_type="TRASH", data=str(json.dumps(trash_json)))

				else:
					print('Unknown error occurred when trying to transpose data for id=' + str(house_num) + " and data=" + str(house_data))
					break

		except yaml.YAMLError as exc:
			print(exc)

	print('Initial amount of houses: ' + str(num_houses))
	print('Number of houses transposed: ' + str(houses_created))



#########
# BELOW IS THE ACTUAL SCRIPT LOGIC
#########

print('Running script for both houses.yml and premiumHouses.yml.' )
print()
start_time = time.time()

print('Running houses.yml...')
PREMIUM = False
run_script('houses.yml')
run_time = time.time() - start_time
print('Finished script for houses.yml in ' + str(run_time) + ' secs.')

print('Running premiumHouses.yml...')
PREMIUM = True
run_script('premiumHouses.yml')
run_time = time.time() - start_time
print('Finished script for premiumHouses.yml in ' + str(run_time) + ' secs.')

# close the connection
db.close()