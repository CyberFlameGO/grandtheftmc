#!/usr/bin/python

# local imports

# python modules
import MySQLdb
import time
import json
import urllib2
import base64

DB_HOST = "localhost"
DB_USER = "root"
DB_PASS = ""
DB_NAME = ""

# open MySQL connection
db = MySQLdb.connect(host=DB_HOST, user=DB_USER, passwd=DB_PASS, db=DB_NAME)

def request_api(username):
	'''
	This will request the API server.

	Args:
		username: The username to use to lookup the uuid.

	Returns:
		The uuid for the user with the name username, otherwise None.
	'''

	api_url = "https://api.mojang.com/users/profiles/minecraft/{}".format(username)  # This server can be pinged as many times as needed.
	req = urllib2.Request(api_url)
	try:
		resp = urllib2.urlopen(req)
	except urllib2.HTTPError as e:
		if e.code == 404:
			pass
			# do something...
		else:
			pass
			# ...
	except urllib2.URLError as e:
		pass
		# Not an HTTP-specific error (e.g. connection refused)
		# ...
	else:
		# 200
		body = resp.read()

		if body is not None and body is not '':
			decoded_body = body.decode("utf-8")
			api_load = json.loads(decoded_body)

			if 'id' in api_load:
				return str(api_load['id'])

	return None

def request_name(uuid):
	'''
	This will request the names server.

	Args:
		uuid: The uuid to use to get the name.

	Returns:
		The last known name for the given uuid, if one exists, other None.
	'''

	name_url = "https://api.mojang.com/user/profiles/{}/names".format(uuid)  # This server can be pinged as many times as needed.
	
	req = urllib2.Request(name_url)
	try:
		resp = urllib2.urlopen(req)
	except urllib2.HTTPError as e:
		if e.code == 404:
			pass
			# do something...
		else:
			pass
			# ...
	except urllib2.URLError as e:
		pass
		# Not an HTTP-specific error (e.g. connection refused)
		# ...
	else:
		# 200
		body = resp.read()

		if body is not None and body is not '':
			decoded_body = body.decode("utf-8")
			name_load = json.loads(decoded_body)

			if len(name_load) > 0:
				last_known_name = name_load[-1]

				if last_known_name is not None and 'name' in last_known_name:
					return str(last_known_name['name'])

	return None

# CREATE TABLE `user` (
#   `uuid` binary(16) NOT NULL,
#   `name` varchar(16) NOT NULL,
#   PRIMARY KEY (`uuid`),
#   KEY `name` (`name`);

def get_duplicate_users():
	'''
	Get a list of names where they have duplicate entries in the database.

	Returns:
		A list of names where each name is a duplicate uuid, name pair in the database.
	'''

	# grab cursor
	cur = db.cursor()

	query = ''' SELECT name, COUNT(*) FROM user GROUP BY name ORDER BY COUNT(*) DESC LIMIT 500; '''
	cur.execute(query)

	# commit query
	db.commit()

	result = []
	for tup in cur:
		name = str(tup[0])
		num = int(tup[1])

		# only add to list those that have duplicates
		if num > 1:
			result.append(name)

	cur.close()

	return result

def get_uuids(name):
	'''
	Get a list of uuids for the given name.

	Returns:
		A list of uuids for the given name.
	'''

	# grab cursor
	cur = db.cursor()

	query = ''' SELECT HEX(uuid) FROM user WHERE name=%s; '''
	data = (name)
	cur.execute(query, [data])

	# commit query
	db.commit()

	result = []
	for tup in cur:
		uuid = str(tup[0])
		result.append(uuid)

	cur.close()

	return result

def get_last_login(name):
	'''
	Get last login information for this user.

	Args:
		The name of the user to lookup.

	Returns:
		TODO
	'''

	# grab cursor
	cur = db.cursor()

	query = ''' SELECT uuid, lastname, last_login FROM users WHERE lastname=%s ORDER BY last_login DESC; '''
	data = (name)
	cur.execute(query, [data])

	# commit query
	db.commit()

	result = []
	for tup in cur:
		uuid = str(tup[0])
		lastname = str(tup[1])
		last_login = str(tup[2])

		print(last_login)

		result.append((uuid, lastname, last_login))

	cur.close()

	return result

def update_name_for_user(uuid, name):
	'''
	Updates the name record for the uuid.

	Args:
		uuid: The uuid primary key that never changes
		name: The name of the user to change for
	'''

	# grab cursor
	cur = db.cursor()

	query = ''' UPDATE user SET name=%s WHERE uuid=UNHEX(%s); '''
	data = (str(name), str(uuid))
	cur.execute(query, data)

	# commit query
	db.commit()
	cur.close()

def delete_user_record(uuid):
	'''
	Deletes the user record from the database.

	Args:
		uuid: The uuid primary key to delete.
	'''

	# grab cursor
	cur = db.cursor()

	query = ''' DELETE FROM user WHERE uuid=UNHEX(%s); '''
	data = (str(uuid))
	cur.execute(query, [data])

	# commit query
	db.commit()
	cur.close()

#########
# BELOW IS THE ACTUAL SCRIPT LOGIC
#########

print('Running script fix_user_data.py...' )
print()
start_time = time.time()

# get duplicate users
dup_users = get_duplicate_users()

print('Found ' + str(len(dup_users)) + ' uuid/name pairs with duplicates.')

# how many names were resolved by the mojang api
names_found = 0
# how many names were not resolved by the mojang api
names_not_found = 0
# how many user names were changed in the user table
user_names_changed = 0

# TODO remove
count = 0
if dup_users is not None and len(dup_users) > 0:
	for du in dup_users:
		
		# grab the uuids with this username
		uuids = get_uuids(name=du)
		if uuids is not None and len(uuids) > 0:

			for uid in uuids:

				# mojang api request for last known name
				last_known_name = request_name(uuid=uid)

				# if we have a name for them
				if last_known_name is not None:
					print('Last known name for uuid=' + str(uid) + ' is ' + str(last_known_name))
					names_found = names_found + 1

					update_name_for_user(uuid=uid, name=last_known_name)
					user_names_changed = user_names_changed + 1

				# else its an offline uuid
				else:
					print('Could not find last known name for uuid=' + str(uid))
					names_not_found = names_not_found + 1

					delete_user_record(uuid=uid)

		count = count + 1
		if count > 50:
			break

# TEST CASES
#print(request_api(username='BlameStephen'))
#print(request_name(uuid='0bfd8917b6bf48088b12c59ec43e4260'))
#print(request_name(uuid='0FF3234F08D33344887FDB721202F9BB'))
#print(request_name(uuid='0BFD8917B6BF48088B12C59EC43E4260'))

run_time = time.time() - start_time
print('Number of duplicate entries found = ' + str(len(dup_users)))
print('Number of names found = ' + str(names_found))
print('Number of names NOT found = ' + str(names_not_found))
print('Number of usernames changed = ' + str(user_names_changed))
print('Finished script in ' + str(run_time) + ' secs.')

# close the connection
db.close()