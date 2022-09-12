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

QUERY_GET_THIS_MONTHS_VOTES = ''' SELECT HEX(LUV.uuid), COUNT(*) FROM log_user_vote LUV WHERE YEAR(LUV.creation) = YEAR(CURRENT_DATE()) AND MONTH(LUV.creation) = MONTH(CURRENT_DATE()) GROUP by LUV.uuid ORDER BY COUNT(*); '''
QUERY_RESET_USER_VOTE = ''' UPDATE votes SET monthlyVotes = 0; '''
QUERY_UPDATE_USER_VOTE = ''' UPDATE votes SET monthlyVotes = %s WHERE uuid = %s; '''

# open MySQL connection
db = MySQLdb.connect(host=DB_HOST, user=DB_USER, passwd=DB_PASS, db=DB_NAME)
# grab cursor
cur = db.cursor()

def get_this_months_votes():
	'''
	Get all the votes, and the respective amount for this month.

	Returns:
		A list of tuples in the form of (uuid, num_votes) for each voter.
		Note: uuid is in form of 795FA12992B943E8B408BADB830CEB0B
	'''
	cur.execute(QUERY_GET_THIS_MONTHS_VOTES)

	result = []
	for tup in cur:
		uuid = str(tup[0])
		num_votes = int(tup[1])
		result.append((uuid, num_votes))

	return result

def reset_user_votes():
	'''
	Resets all users votes for the votes table, in the monthlyVotes column.
	'''
	cur.execute(QUERY_RESET_USER_VOTE)

def update_vote(uuid, num_votes):
	'''
	Updates the vote tally for the specified user.

	Args:
		uuid: The uuid for the user in the form of 0ff3234f-08d3-3344-887f-db721202f9bb
		num_votes: The number of votes for that user
	'''
	data = (int(num_votes), str(uuid))
	cur.execute(QUERY_UPDATE_USER_VOTE, data)

#########
# BELOW IS THE ACTUAL SCRIPT LOGIC
#########

print('Running script vote_sync.py...' )
print()
start_time = time.time()

votes_month = get_this_months_votes()
num_users_updated = 0
vote_tallied = 0
if votes_month is not None and len(votes_month):

	# reset user votes
	reset_user_votes()

	# for each user
	for uuid, num_votes in votes_month:
		lower_uuid = str(uuid).lower()

		# 8 4 4 4 12
		formatted_uuid = str(lower_uuid[0:8]) + '-' + str(lower_uuid[8:12]) + '-' + str(lower_uuid[12:16]) + '-' + str(lower_uuid[16:20]) + '-' + str(lower_uuid[20:32])

		update_vote(uuid=formatted_uuid, num_votes=num_votes)
		vote_tallied += num_votes
		num_users_updated += 1

# commit query
db.commit()

run_time = time.time() - start_time
print('Number of users updated = ' + str(num_users_updated))
print('Number of votes tallied = ' + str(vote_tallied))
print('Finished script in ' + str(run_time) + ' secs.')

# close the connection
db.close()