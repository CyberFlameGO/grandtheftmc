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

SERVER_KEY = "GTM0"
QUERY_GET_GANGS = '''SELECT name, leader, leaderName, description, maxMembers FROM gtm0_gangs;'''
QUERY_GET_GANG_RELATIONS = '''SELECT gang1, gang2, relation FROM gtm0_gangs_relations;'''
QUERY_GET_USER_GANG = '''SELECT uuid, gang, gangRank FROM gtm0 WHERE gang IS NOT NULL;'''


# open MySQL connection
db = MySQLdb.connect(host=DB_HOST, user=DB_USER, passwd=DB_PASS, db=DB_NAME)
# grab cursor
cur = db.cursor()

# print out variables
print("MAX_QUERY_TICK = " + str(MAX_QUERY_TICK))
print("TICK_SLEEP = " + str(TICK_SLEEP))
print("SERVER_KEY = " + str(SERVER_KEY))
print("QUERY_GET_GANGS = " + str(QUERY_GET_GANGS))
print("QUERY_GET_GANG_RELATIONS = " + str(QUERY_GET_GANG_RELATIONS))
print("QUERY_GET_USER_GANG = " + str(QUERY_GET_USER_GANG))

# CREATE TABLE IF NOT EXISTS gtm_gang(
# id INT NOT NULL AUTO_INCREMENT, 
# server_key VARCHAR(10) NOT NULL, 
# name VARCHAR(36) NOT NULL, 
# owner BINARY(16) NOT NULL, 
# description VARCHAR(255) NOT NULL, 
# max_members INT(11) NOT NULL, 
# PRIMARY KEY (id), 
# INDEX (server_key), 
# INDEX (name)
# );

# represents a gang
class Gang(object):
	def __init__(self):
		# id of the gang
		self.id = -1
		# the server that this gang is for
		self.server_key = None
		# name of the gang
		self.name = None
		# uuid of the owner of the gang
		self.owner = None
		# description of the gang
		self.description = None
		# max members this gang can hold
		self.max_members = 0

	def __str__(self):
		return 'id=' + str(self.id) + ", server_key=" + str(self.server_key) + ", name=" + str(self.name) + ", owner=" + str(self.owner) + ", description=" + str(self.description) + ", max_members=" + str(self.max_members)

# CREATE TABLE IF NOT EXISTS gtm_gang_member(
# gang_id INT NOT NULL, 
# uuid BINARY(16) NOT NULL, 
# role SMALLINT(6) DEFAULT 1, 
# PRIMARY KEY (gang_id, uuid), 
# FOREIGN KEY (gang_id) REFERENCES gtm_gang(id) ON DELETE CASCADE
# );

# represents a gang member
class GangMember(object):
	def __init__(self):
		# the id of the gang
		self.gang_id = -1
		# the name of the gang
		self.gang_name = None
		# uuid of the member
		self.uuid = None
		# role of the member
		self.role = None

	def __str__(self):
		return "gang_id=" + str(self.gang_id) + ", gang_name=" + str(self.gang_name) + ",uuid=" + str(self.uuid) + ", role=" + str(self.role)

# CREATE TABLE IF NOT EXISTS gtm_gang_relation(
# gang_id INT NOT NULL, 
# other_id INT NOT NULL, 
# relation VARCHAR(10), 
# PRIMARY KEY (gang_id, other_id), 
# FOREIGN KEY (gang_id) REFERENCES gtm_gang(id) ON DELETE CASCADE, 
# FOREIGN KEY (other_id) REFERENCES gtm_gang(id) ON DELETE CASCADE
# );

def get_gangs():
	'''
	Get all the gangs that belong in the specified table.

	Returns:
		A list of dictionary objects that represent a gang.
	'''

	# # grab cursor
	# cur = db.cursor()
	cur.execute(QUERY_GET_GANGS)

	gangs = []
	for tup in cur:
		g = Gang()
		g.server_key = str(SERVER_KEY)
		g.name = str(tup[0])
		g.owner = string.replace(str(tup[1]), "-", "")
		g.description = str(tup[3])
		g.max_members = int(tup[4])
		gangs.append(g)

	# # commit query
	# db.commit()
	# cur.close()
	
	return gangs

def create_gang(gang):
	'''
	Create the new representation of the gang in the new table.

	Args:
		gang: The gang object to create.
	'''
	# # grab cursor
	# cur = db.cursor()
	query = '''INSERT INTO gtm_gang (server_key, name, owner, description, max_members) VALUES (%s, %s, UNHEX(%s), %s, %s);'''
	data = (str(gang.server_key), str(gang.name), str(gang.owner), str(gang.description), int(gang.max_members))
	cur.execute(query, data)

	# # commit query
	# db.commit()
	# cur.close()

def create_gang_member(gang_member):
	'''
	Create the gang member representation in the database.

	Args:
		gang_member: The member object to create
	'''

	# get the role of the member
	rank = gang_member.role
	# determine the role to change it to
	role = 1

	if rank is not None:
		rank = str(rank).lower()

		if rank == 'member':
			role = 1
		elif rank == 'trusted':
			role = 2
		elif rank == 'coleader':
			role = 5
		elif rank == 'leader':
			role = 6

	# # grab cursor
	# cur = db.cursor()
	query = '''INSERT INTO gtm_gang_member (gang_id, uuid, role) VALUES (%s, UNHEX(%s), %s);'''
	data = (int(gang_member.gang_id), str(gang_member.uuid), int(role))
	cur.execute(query, data)

	# # commit query
	# db.commit()
	# cur.close()

def create_gang_relation(gang1_id, gang2_id, relation):
	'''
	Create the gang relation in the database.

	Args:
		gang1_id: The id of the first gang
		gang2_id: The id of the second gang
		relation: The relationship they share
	'''

	# # grab cursor
	# cur = db.cursor()
	query = '''INSERT INTO gtm_gang_relation (gang_id, other_id, relation) VALUES (%s, %s, %s);'''
	data = (int(gang1_id), int(gang2_id), str(relation))
	cur.execute(query, data)

	# # commit query
	# db.commit()
	# cur.close()

def get_relations():
	'''
	Get a list of relation objects that need parsed.

	Returns:
		A relation objects that need parsed, in the form of a tuple (gang1, gang2, relation).
	'''

	# # grab cursor
	# cur = db.cursor()
	cur.execute(QUERY_GET_GANG_RELATIONS)

	relations = []
	for tup in cur:
		gang1 = str(tup[0])
		gang2 = str(tup[1])
		relation = str(tup[2])

		relations.append((gang1, gang2, relation))

	# # commit query
	# db.commit()
	# cur.close()

	return relations	

def get_users():
	'''
	Get a list of users and their respective gang member information.

	Returns:
		A list of GangMember objects where each one represents information about a member of a gang.
	'''

	# # grab cursor
	# cur = db.cursor()
	cur.execute(QUERY_GET_USER_GANG)

	users = []
	for tup in cur:
		gm = GangMember()
		gm.uuid = string.replace(str(tup[0]), "-", "")
		#print('gm.uuid', gm.uuid)
		gm.gang_name = str(tup[1])
		#print('gm.gang_name', gm.gang_name)
		gm.role = str(tup[2])
		#print('gm.role', gm.role)
		users.append(gm)

	# # commit query
	# db.commit()
	# cur.close()

	return users

def find_gang_id(server_key, gang_name):
	'''
	Find the id of a gang by their gang name.

	Args:
		server_key: The server key for the gang
		name: The name of the gang to find

	Returns:
		The id of the gang, if one exists, otherwise None.
	'''

	# # grab cursor
	# cur = db.cursor()
	query = ''' SELECT id FROM gtm_gang WHERE server_key=%s AND name=%s;'''
	data = (str(server_key), str(gang_name))
	cur.execute(query, data)

	gang_id = None
	for tup in cur:
		if len(tup) > 0:
			gang_id = int(tup[0])

	# # commit query
	# db.commit()
	# cur.close()

	return gang_id


#########
# BELOW IS THE ACTUAL SCRIPT LOGIC
#########

print('Running script...')
start_time = time.time()

# limit the amount of queries to the database per interval
tick = 0

# get all the gangs in old tables
gangs = get_gangs()
gangs_created = 0
for gang in gangs:
	tick += 1
	# create relation in new table
	try:
		create_gang(gang)
		gangs_created += 1
	except Exception as e:
		print(e)

	if tick > MAX_QUERY_TICK:
		tick = 0
		time.sleep(TICK_SLEEP)


# get all the old relations
relations = get_relations()
relations_created = 0
for rel in relations:
	tick += 1
	(gang1, gang2, relation) = rel

	# only do valid gangs
	gang1_id = find_gang_id(SERVER_KEY, gang1)
	if gang1_id is not None:

		gang2_id = find_gang_id(SERVER_KEY, gang2)
		if gang2_id is not None:
			try:
				print("#" + str(relations_created) + " - Creating gang relation " + str(rel))
				# create relation in new table
				create_gang_relation(gang1_id, gang2_id, relation)
				relations_created += 1
			except Exception as e:
				print(e)

	if tick > MAX_QUERY_TICK:
		tick = 0
		time.sleep(TICK_SLEEP)

# get all users in the old table
gang_members = get_users()
gang_members_created = 0

tick = 0
for gm in gang_members:
	tick += 1

	# find the gang_id with the given name
	gang_id = find_gang_id(SERVER_KEY, gm.gang_name)
	if gang_id is not None:
		gm.gang_id = gang_id

		try:
			print("#" + str(gang_members_created) + " - creating gang member=" + str(gm))

			# create new gang member
			create_gang_member(gm)
			gang_members_created += 1
		except Exception as e:
			print(e)

	if tick > MAX_QUERY_TICK:
		tick = 0
		time.sleep(TICK_SLEEP)

# for unt in# 	print("failed to " + str(unt))
# print(str(len(untranferred)) + " gang members were not found gang for") untranferred:

# commit query
db.commit()
cur.close()

run_time = time.time() - start_time	
print('Script is now complete! (' + str(run_time) + ' secs)')
print('Number of gangs found: ' + str(len(gangs)))
print('Number of gangs transposed: ' + str(gangs_created))
print()
print('Number of relations found: ' + str(len(relations)))
print('Number of relations transposed: ' + str(relations_created))
print()
print('Number of gang members found: ' + str(len(gang_members)))
print('Number of gang members transposed: ' + str(gang_members_created))

# close the connection
db.close()