#!/usr/bin/python

# local imports

# python modules
import MySQLdb
import time
import sys

DB_HOST = "localhost"
DB_USER = "root"
DB_PASS = ""
DB_NAME = ""
QUERY = ''' SELECT backpackContents, name FROM gtm1; '''
SELECT_COL = 'backpackContents'

# open MySQL connection
db = MySQLdb.connect(host=DB_HOST, user=DB_USER, passwd=DB_PASS, db=DB_NAME)

def get_largest_data(query, select_col):
	'''
	Mine the data and determine the largest value for the given query.

	Args:
		query: The query to run
		select_col: The column to select

	Returns:
		The largest data entry for the given query.
	'''

	largest = 0
	name = None
	backpack_data = None

	# grab cursor
	cur = db.cursor()
	cur.execute(query)

	# commit query
	db.commit()

	result = []
	for tup in cur:
		x = tup[0]

		size_x = utf8len(str(x))
		if size_x >= largest:
			largest = size_x
			print(largest)
			name = str(tup[1])
			backpackContents = str(x)

			temp = {}
			temp[name] = backpackContents
			result.append(temp)

	cur.close()

	return result

def utf8len(s):
	'''
	Get the length of a given string.

	Args:
		s - the string in question

	Returns:
		The length of the string .
	'''
	return len(s.encode('utf-8'))

def get_len_by_sys(s):
	'''
	Get the length of a given string according to the sys module.

	Args:
		s - the string in question

	Returns:
		The length of the string.
	'''
	return sys.getsizeof(s)

def insert_data(name, backpack):
	'''
	Insert data into a test column to see if it fits.

	Args:
		name - the name of the user to insert fake data for
	'''

	largest = 0
	name = None

	# grab cursor
	cur = db.cursor()
	query = ''' SELECT uuid, backpackContents FROM gtm1 WHERE name=%s'''
	data = [str(name)]
	cur.execute(query, data)

	uuid = None
	for tup in cur:
		uuid = str(tup[0])

	uuid = "0xDD"

	# grab cursor
	cur = db.cursor()
	query = ''' INSERT IGNORE INTO backpack_test (uuid, backpack) VALUES (%s, %s)'''
	data = (uuid, backpack)
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

data_set = get_largest_data(query=QUERY, select_col=SELECT_COL)
#print(data_set)

# for element in data_set:
# 	for k in element:
# 		print("k", k)
# 		insert_data(k, element[k])

run_time = time.time() - start_time

print('Finished script in ' + str(run_time) + ' secs.')

# close the connection
db.close()