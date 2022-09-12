#!/usr/bin/python

# local imports

# python modules
import MySQLdb
import string
import time
import logging, logging.handlers
import os
import sys
import csv
import json
import slack_api

# MySQL database creds
DB_HOST = "localhost"
DB_USER = "root"
DB_PASS = ""
DB_NAME = "bat"
# slack credentials
SLACK_API_URL = 'https://hooks.slack.com/services/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'
# The name of the network
NETWORK_NAME = 'Grand Theft Minecraft'
# the icon's url used in the bot that sends the response
ICON_URL = 'https://s3-us-west-2.amazonaws.com/slack-files2/avatars/2017-09-07/237267836001_e1d81f50852486fa452c_132.png'

# slack hookup
SLACK_HEADERS = {'content-type': 'application/json'}
SLACK_SERVER = slack_api.SlackAPI(api_url=SLACK_API_URL, headers=SLACK_HEADERS)

# set up the logger
LOG = logging.getLogger('stat_log')
LOG.setLevel(logging.DEBUG)
handler = logging.handlers.RotatingFileHandler("stats.log", backupCount=5)
handler.setFormatter(logging.Formatter('[%(asctime)s][%(levelname)s]: %(message)s'))
LOG.addHandler(handler)

# open MySQL connection
db = MySQLdb.connect(host=DB_HOST, user=DB_USER, passwd=DB_PASS, db=DB_NAME)
gtmdb = MySQLdb.connect(host=DB_HOST, user=DB_USER, passwd=DB_PASS, db='users')
# grab cursor
cur = db.cursor()

class StatApp(object):
	
	def __init__(self, file_name='network_stats.csv'):

		# internal data, generated by collect()
		self.data = []
		self.dict_data = {}
		self.file_name = file_name

		# the internal data folder for stats
		self.internal_folder = '/minecraft/stats'
		# the file directory + name, ex: /home/cache.txt used for caching data
		self.cache_dir_and_file = self.internal_folder + '/cache.txt'

		# make sure the folder exists if it doesn't
		if not os.path.exists(self.internal_folder):
			os.makedirs(self.internal_folder)

	def collect(self):
		'''
		Collects the stats that this app should handle.
		'''
		
		# collect the stats
		default = self.collect_default()
		spec = self.collect_specific()
		if spec is None:
			spec = []

		self.data = default + spec

		# create the dict representation of this data
		for k,v in self.data:
			self.dict_data[k] = v

		# try:
		# 	# optional call-back for after the data is collected
		# 	self.on_collect()
		# except Exception as e:
		# 	err_msg = 'Error occurred in on_collect(). ' + str(e)
		# 	print(err_msg)
		# 	LOG.error(err_msg)


	def collect_default(self):
		'''
		Collects the default stats, like number of unique players.

		Returns:
			A list of tuples, in the form of (category, value), as the default stats
			we are collecting.
		'''
		
		# we'll store results in here
		result = []

		# grab default stats and add to result
		result.append(("Total Unique Players", get_total_unique_players()))
		result.append(("Active Users (1 day)", get_num_players_last_x_days(1)))
		result.append(("Active Users (7 days)", get_num_players_last_x_days(7)))
		result.append(("Active Users (30 days)", get_num_players_last_x_days(30)))
		result.append(("New Users (1 day)", get_num_players_joined_x_days(1)))
		result.append(("New Users (7 days)", get_num_players_joined_x_days(7)))
		result.append(("New Users (30 days)", get_num_players_joined_x_days(30)))

		return result

	def collect_specific(self):
		'''
		Collects the specific stats, different for each network.

		Returns:
			A list of tuples, in the form of (category, value), as the stats
			we are collecting.
		'''
		
		# we'll store results in here
		result = []

		# NOTE: this should be implemented for specific projects.
		result.append(("Votes (1 day)", get_num_votes_x_days(1)))
		result.append(("Votes (7 days)", get_num_votes_x_days(7)))
		result.append(("Votes (30 days)", get_num_votes_x_days(30)))

		return result

	def save_to_csv(self):
		'''
		Saves this object to the csv file.
		'''

		# if there is data to save
		if self.data is not None and len(self.data) > 0:

			# sort into columns / values
			columns = []
			values = []

			columns.append('Time')
			#values.append(str(time.ctime(time.time())))
			values.append(str(time.strftime('%Y-%b-%d')))
			columns.append('Day')
			#values.append(str(time.ctime(time.time())))
			values.append(str(time.strftime('%A')))

			for k,v in self.data:
				columns.append(k)
				values.append(v)

			# write to csv, generates headers if file doesn't exist
			write_row_csv(self.internal_folder + '/' + str(self.file_name), columns, values)

	def save_to_file(self):
		'''
		Writes the specified internal data of this factory to a file in the form of JSON.
		'''

		# if there is data to save
		if self.data is not None and len(self.data) > 0:
			
			# build a dictionary of key/value
			cache = {}
			for k,v in self.data:
				cache[k] = v

			# add the timestamp
			cache['Time'] = str(time.ctime(time.time()))

			with open(self.internal_folder + '/cache.txt', 'wb') as outfile:
				json.dump(cache, outfile)

	def notify_slack(self):
		'''
		Send a message to slack about this object.
		'''

		# create a message
		message = slack_api.Message()

		# add attachments
		attachments = self.get_attachments()
		if attachments is not None and len(attachments) > 0:
			for a in attachments:
				message.add_attachment(a)

		message_data = message.get_contents()
		message_data['channel'] = '#important'
		message_data['username'] = NETWORK_NAME
		message_data['icon_emoji'] = ':bar_chart:'

		# send message to Slack
		if SLACK_SERVER is not None:
			SLACK_SERVER.send_json(message_data)

	def get_attachments(self):
		'''
		Get the attachments for a message that are being sent to Slack.

		Returns:
			A list of attachments that are to be added to the slack message.
		'''

		result = []

		# get the default attachments and add to result
		default = self.get_def_attachments()
		if default is not None and len(default) > 0:
			result = result + default

		# get specific attachments and add to result
		specific = self.get_spec_attachments()
		if specific is not None and len(specific) > 0:
			result = result + specific

		return result

	def get_def_attachments(self):
		'''
		Get the default attachments that are to be sent to Slack in the message.

		Returns:
			A list of attachments, in the form of a dict, to be added the Slack message.
		'''

		total_unique_players = self.dict_data["Total Unique Players"]
		active_user_1 = self.dict_data["Active Users (1 day)"]
		active_user_7 = self.dict_data["Active Users (7 days)"]
		active_user_30 = self.dict_data["Active Users (30 days)"]
		new_user_1 = self.dict_data["New Users (1 day)"]
		new_user_7 = self.dict_data["New Users (7 days)"]
		new_user_30 = self.dict_data["New Users (30 days)"]
		vote_1 = self.dict_data["Votes (1 day)"]
		vote_7 = self.dict_data["Votes (7 days)"]
		vote_30 = self.dict_data["Votes (30 days)"]

		old_data = None
		# if cached stats file exists
		if os.path.isfile(self.cache_dir_and_file):
			with open(self.cache_dir_and_file) as data_file:    
				old_data = json.load(data_file)

		# these will be mutated if we have cached results from previous running of script
		active_user_line1 = str(active_user_1)
		new_user_line1 = str(new_user_1)
		active_user_line7 = str(active_user_7)
		new_user_line7 = str(new_user_7)
		active_user_line30 = str(active_user_30)
		new_user_line30 = str(new_user_30)
		vote_line1 = str(vote_1)
		vote_line7 = str(vote_7)
		vote_line30 = str(vote_30)

		# if cached old_data, change the line data
		if old_data is not None:
			prev_active1 = int(old_data['Active Users (1 day)'])
			prev_new1 = int(old_data['New Users (1 day)'])
			prev_active7 = int(old_data['Active Users (7 days)'])
			prev_new7 = int(old_data['New Users (7 days)'])
			prev_active30 = int(old_data['Active Users (30 days)'])
			prev_new30 = int(old_data['New Users (30 days)'])
			prev_vote1 = int(old_data['Votes (1 day)'])
			prev_vote7 = int(old_data['Votes (7 days)'])
			prev_vote30 = int(old_data['Votes (30 days)'])

			# calculate change, expressed in %
			active_change1 = "{0:.2f}".format(get_state_of_change(prev_active1, active_user_1))
			new_change1 = "{0:.2f}".format(get_state_of_change(prev_new1, new_user_1))
			active_change7 = "{0:.2f}".format(get_state_of_change(prev_active7, active_user_7))
			new_change7 = "{0:.2f}".format(get_state_of_change(prev_new7, new_user_7))
			active_change30 = "{0:.2f}".format(get_state_of_change(prev_active30, active_user_30))
			new_change30 = "{0:.2f}".format(get_state_of_change(prev_new30, new_user_30))
			vote_change1 = "{0:.2f}".format(get_state_of_change(prev_vote1, vote_1))
			vote_change7 = "{0:.2f}".format(get_state_of_change(prev_vote7, vote_7))
			vote_change30 = "{0:.2f}".format(get_state_of_change(prev_vote30, vote_30))

			# active_change1 = int(round(get_state_of_change(prev_active1, active_user_1)))
			# new_change1 = int(round(get_state_of_change(prev_new1, new_user_1)))
			# active_change7 = int(round(get_state_of_change(prev_active7, active_user_7)))
			# new_change7 = int(round(get_state_of_change(prev_new7, new_user_7)))
			# active_change30 = int(round(get_state_of_change(prev_active30, active_user_30)))
			# new_change30 = int(round(get_state_of_change(prev_new30, new_user_30)))

			active_user_line1 = str(active_user_1) + ' (' + str(active_change1) + '%)'
			new_user_line1 = str(new_user_1) + ' (' + str(new_change1) + '%)'
			active_user_line7 = str(active_user_7) + ' (' + str(active_change7) + '%)'
			new_user_line7 = str(new_user_7) + ' (' + str(new_change7) + '%)'
			active_user_line30 = str(active_user_30) + ' (' + str(active_change30) + '%)'
			new_user_line30 = str(new_user_30) + ' (' + str(new_change30) + '%)'
			vote_line1 = str(vote_1) + ' (' + str(vote_change1) + '%)'
			vote_line7 = str(vote_7) + ' (' + str(vote_change7) + '%)'
			vote_line30 = str(vote_30) + ' (' + str(vote_change30) + '%)'

		contents = {}
		contents['title'] = 'User Report'
		contents['color'] = 'good'

		# build the fields
		fields = []
		f1 = {}
		f1['title'] = 'Active (Last 7 days)'
		f1['value'] = active_user_line7
		f1['short'] = True
		f2 = {}
		f2['title'] = 'Active (Last 30 days)'
		f2['value'] = active_user_line30
		f2['short'] = True
		f3 = {}
		f3['title'] = 'New (Last 7 days)'
		f3['value'] = new_user_line7
		f3['short'] = True
		f4 = {}
		f4['title'] = 'New (Last 30 days)'
		f4['value'] = new_user_line30
		f4['short'] = True
		f5 = {}
		f5['title'] = 'Votes (Last 7 days)'
		f5['value'] = vote_line7
		f5['short'] = True
		f6 = {}
		f6['title'] = 'Votes (Last 30 days)'
		f6['value'] = vote_line30
		f6['short'] = True
		f7 = {}
		f7['title'] = 'Total Users'
		f7['value'] = int(total_unique_players)
		f7['short'] = True

		fields.append(f1)
		fields.append(f2)
		fields.append(f3)
		fields.append(f4)
		fields.append(f5)
		fields.append(f6)
		fields.append(f7)
		contents['fields'] = fields

		if type(contents) is dict:
			# general footer
			contents['footer'] = str(NETWORK_NAME)
			contents['footer_icon'] = str(ICON_URL)
			# attach the timestamp
			contents['ts'] = int(time.time())

		result = []
		result.append(contents)

		return result

	def get_spec_attachments(self):
		'''
		Optional implementation to get specific stats attachments to be sent to Slack.

		Returns:
			A list of attachments, in the form of a dict, to add to the message to be sent to Slack.
		'''
		pass

def write_row_csv(fname, headers, data_row):
	'''
	Writes the data to a csv file.

	Args:
		fname: absolute name of the file, with directory
		headers: list of strings representing the column headings
		data_row: the data entry to write, as a list
	'''

	# if no file, generate headings
	if not os.path.isfile(fname):
		cw = csv.writer(open(fname, 'wb'))
		cw.writerow(headers)

	# open csv writer with the append feature
	cw = csv.writer(open(fname, 'ab'))

	# write the row
	cw.writerow(data_row)

def get_total_unique_players():
	'''
	Gets the total amount of players that have ever logged in.
	'''
	query = '''SELECT COUNT(*) FROM BAT_players'''
	cur.execute(query)

	count = 0
	for tup in cur:
		count = int(tup[0])

	# commit query
	# db.commit()

	return count

def get_num_players_last_x_days(day):
	'''
	Args:
		day - the amount of days to look back

	Gets the number of players that last logged on in the past day days.
	'''
	query = '''SELECT COUNT(*) FROM BAT_players WHERE lastlogin >= (CURDATE() - INTERVAL %s DAY);'''
	data = (day)
	cur.execute(query, [data])

	count = 0
	for tup in cur:
		count = int(tup[0])

	# # commit query
	# db.commit()

	return count

def get_num_players_joined_x_days(day):
	'''
	Args:
		day - the amount of days to look back

	Gets the number of players that joined the server in the past day days.
	'''

	cur = db.cursor()
	query = '''SELECT COUNT(*) FROM BAT_players WHERE firstlogin >= (CURDATE() - INTERVAL %s DAY);'''
	data = (day)
	cur.execute(query, [data])

	count = 0
	for tup in cur:
		count = int(tup[0])

	# commit query
	# db.commit()

	return count

def get_num_votes_x_days(day):
	'''
	Args:
		day - the amount of days to look back

	Gets the number of players that joined the server in the past day days.
	'''

	# grab cursor
	cur = gtmdb.cursor()
	query = '''SELECT COUNT(*) FROM log_user_vote WHERE creation >= (CURDATE() - INTERVAL %s DAY);'''
	data = (day)
	cur.execute(query, [data])

	count = 0
	for tup in cur:
		count = int(tup[0])

	# commit query
	# db.commit()

	return count

def get_state_of_change(prev, new):
	'''
	Calculates the percent of change from the previous to the new.

	Ex: If prev=50, and new=100, the state of change is (100-50)/50 * 100 = 100%

	Returns:
		The percent state of change, so times 100.
	'''

	diff = int(new - prev)
	change = 0
	if prev > 0:
		change = float(diff) / float(prev)

	return change * 100.0

#########
# BELOW IS THE ACTUAL SCRIPT LOGIC
#########

def command_help():
	print('You run this script by using: ')
	print('- python stat_app.py run')
	print('\nYou run and publish results to slack by: ')
	print('- python stat_app.py publish')
	print('\nYou can display test information, without writing it to files: ')
	print('- python stat_app.py test')

def command_test():
	print('Running script in test mode...')
	LOG.debug('Running script in test mode...')

	# construct a new stats object
	stat_obj = StatApp()

	# collect the stats information, and cache it inside the object
	stat_obj.collect()

	if stat_obj.data is not None and len(stat_obj.data) > 0:
		for k,v in stat_obj.data:
			print(str(k) + ' -> ' + str(v))

def command_run(slack_post=False):
	print('Running script in execute mode, slack_post=' + str(slack_post))
	LOG.debug('Running script in execute mode, slack_post=' + str(slack_post))

	# construct a new stats object
	stat_obj = StatApp()

	# collect the stats information, and cache it inside the object
	stat_obj.collect()
	LOG.debug('Data collected: ' + str(stat_obj.dict_data))

	# write to csv
	stat_obj.save_to_csv()

	if slack_post == True:
		stat_obj.notify_slack()

		# save to file, we can use to retrieve previous day's information
		stat_obj.save_to_file()

	print('Script has finished running. Look in ' + str(stat_obj.internal_folder))
	LOG.debug('Script has finished running. Look in ' + str(stat_obj.internal_folder))

if __name__ == '__main__':

	# execute from command line
	if len(sys.argv) > 1:
		command = str(sys.argv[1]).lower()

		if command == 'help':
			command_help()
		elif command == 'test':
			command_test()
		elif command == 'run':
			command_run(slack_post=False)
		elif command == 'publish':
			command_run(slack_post=True)

# commit query
db.commit()
cur.close()

# close the connection
db.close()