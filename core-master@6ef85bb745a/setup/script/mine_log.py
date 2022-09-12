#!/usr/bin/python

# python modules
import json
import subprocess
import socket
import time
import datetime

# pip modules
import requests

FILE_NAME = '/var/log/auth.log'
USE_CURRENT_DATE = True

SLACK_API_URL = 'https://hooks.slack.com/services/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'

# maps fingerprint to name
FINGER_DICT = {
	'e3:a8:ef:d2:61:b1:13:8c:b0:e6:bc:aa:9c:3f:b0:90': 'Stephen',
	'35:73:3c:fa:86:41:c2:28:e7:d2:4f:aa:38:62:6e:a5': 'Brad',
	# brad macbook pro
	'dc:28:e9:c3:31:4b:7d:1b:fb:fe:c1:bb:5b:d9:d6:ab': 'Brad',
	'1d:70:6b:a0:e4:89:ee:1f:63:4b:18:a4:92:07:e8:52': 'Mason',
	# mason backup
	'35:c9:a6:d4:06:e8:4a:fb:e4:a0:44:ec:36:bf:43:d6': 'Mason',
	'33:ed:70:4e:02:3f:6f:a0:ff:e5:3c:f6:8f:67:fd:fb': 'Tim',
	'8c:b8:40:b1:e9:ee:e8:7e:b2:d6:39:6f:24:5c:e3:f6': 'Jed',
	'8b:ba:38:ff:e2:b1:28:b5:e1:6a:1c:24:8b:98:89:77': 'Liam',
	'71:7f:fd:8b:cc:eb:db:98:98:62:3c:6a:06:8d:92:6f': 'Luke',
	'47:cb:84:d1:6a:6e:94:a8:f7:b6:11:48:b5:72:8e:cf': 'Jesse',
	# Jesse access
	'76:c4:be:23:43:71:b4:d8:c8:3d:a3:a2:1b:bf:db:57': 'Jesse',
	# Jesse backup
	'b7:1d:90:ac:25:20:c9:96:fb:3a:3c:9a:9e:8a:02:19': 'Jesse',
}

# maps name to ip
NAME_DICT = {
	'Stephen': ['24.101.17.184', '24.112.212.17'],
	'Luke': ['90.240.15.159', '90.255.162.177', '90.255.175.1', '2.124.198.224'],
	'Mason': ['198.24.182.226', '24.253.105.169'],
	'Jed': ['150.143.79.210', '150.143.79.195'],
	'Brad': ['98.149.243.111'],
	'Tim': ['70.68.9.211'],
	'Jesse': ['95.97.205.201', '144.217.180.29', '162.208.95.194', '84.241.199.136', '139.162.217.42', '84.241.197.240', '12.217.236.130', '85.159.210.213'],
}

class SlackAPI(object):
    def __init__(self, api_url, headers, debug=False):
        '''
        Args:
            api_url: The URL for the API request
            headers: The headers attached to this post request
            debug: Boolean on whether we should print out debugging info
        '''
        self.api_url = api_url
        self.headers = headers
        self.debug = debug

    def __str__(self):
        '''
        Returns:
            The string representation for this Message object.
        '''
        return 'SlackAPI [api_url=' + str(self.api_url) + ', headers=' + str(self.headers) + ', debug=' + str(self.debug)

    def send_json(self, json_contents):
        '''
        Send an arbitrary json object as a POST message to the specified URL.

        Args:
            json: The dictionary representation that is being sent.

        Returns:
            The response from the POST request, False if something happened.
        '''
        try:
            r = requests.post(self.api_url, data=json.dumps(json_contents), headers=self.headers)
            print(r)
            return r
        except Exception as e:
            print('Unknown exception occurred when trying to send ' + str(json) + ' for Message ' + str(self))
            print(e)
            return False

    def send_message(self, contents, channel, username, icon_emoji):
        '''
        Sends a POST request message with the given attributes.

        Args:
            contents: The contents of the message as the 'text' field
            channel: The channel to send the message
            username: The username that is posting
            icon_emoji: The emoji that the username has

        Returns:
            The response from the POST request, False if something happened.
        '''

        # construct the JSON object
        json_contents = {}
        json_contents['text'] = contents
        json_contents['channel'] = channel
        json_contents['username'] = username
        json_contents['icon_emoji'] = icon_emoji
        
        return self.send_json(json_contents)

class Message(object):
    def __init__(self):

        # initialize contents as a python dictionary
        self.contents = {}

        # initialize attachments as a list
        self.attachments = []

    def add_attachment(self, data):
        '''
        Adds the specified data as an attachment to the message.

        Args:
            data: The data to add as an attachment

        Returns:
            True if the attachment was added, False otherwise.
        '''
        if type(data) is dict:
            self.attachments.append(data)
            return True

        return False

    def get_contents(self):
        '''
        Get the contents of this message in terms of a Python dictionary.

        Returns:
            The contents of this message.
        '''
        c = self.contents
        c['attachments'] = self.attachments
        return c

# Below is an example of how to use this module
# api_url = "https://hooks.slack.com/services/TOKEN"
# headers = {'content-type': 'application/json'}
# slack = SlackAPI(api_url=api_url, headers=headers, debug=True)
# slack.send_message(contents='Hello Slack!', channel='#general', username='TestAPIBot', icon_emoji=':smile:')

# Mar  3 20:43:36 GTM-OVH-4 sshd[20268]: Accepted publickey for root from 198.24.182.226 port 53327 ssh2: RSA 35:c9:a6:d4:06:e8:4a:fb:e4:a0:44:ec:36:bf:43:d6
# Mar  3 20:43:36 GTM-OVH-4 sshd[20268]: Accepted publickey for root from 198.24.182.226 port 53327 ssh2: RSA 35:c9:a6:d4:06:e8:4a:fb:e4:a0:44:ec:36:bf:43:d6
# Mar  3 20:43:36 GTM-OVH-4 sshd[20268]: Accepted publickey for root from 198.24.182.226 port 53327 ssh2: RSA 35:c9:a6:d4:06:e8:4a:fb:e4:a0:44:ec:36:bf:43:d6

# maps ip to list of tuple (date_tuple)
UNKNOWN_LOGIN = {}
# maps unknown fingerprints to ips
UNKNOWN_FINGERPRINT = {}

# grab the current time
current_time = datetime.date.today().strftime("%b %d")

print('Running mine_log.py for ' + current_time)

with open(FILE_NAME) as f:
	lines = f.readlines()

	for line in lines:

		# strip new line away
		#line = line.rstrip('\n')
		line = " ".join(line.split())

		if 'Accepted publickey for' in line:
			
			# split by space
			parts = line.split(" ")

			# for i in range(0, len(parts)):
			# 	print("index " + str(i) + " = " + str(parts[i]))
			if parts is not None and len(parts) > 0:
				date_str_full = str(parts[0]) + " " + str(parts[1]) + " " + str(parts[2])
				date_str_md = str(parts[0]) + " " + str(parts[1])
				user_str = str(parts[8])
				ip_str = str(parts[10])
				rsa_str = str(parts[15])

				# if we want to use current date
				if USE_CURRENT_DATE:

					# only parse lines from same date (month/day)
					if current_time != date_str_md:
						continue

				# have we seen this ip for this fingerprint
				if rsa_str in FINGER_DICT:
					name = FINGER_DICT[rsa_str]

					if name in NAME_DICT:
						ip_list = NAME_DICT[name]

						if ip_str in ip_list:
							pass
						else:
							# print(str(ip_str) + ' has logged in using ' + str(name) + '\'s RSA key!')

							result = None
							if ip_str in UNKNOWN_LOGIN:
								result = UNKNOWN_LOGIN[ip_str]
							else:
								result = []

							data_tuple = (date_str_full, user_str, rsa_str, name)
							result.append(data_tuple)

							UNKNOWN_LOGIN[ip_str] = result
					else:
						result = None
						if ip_str in UNKNOWN_LOGIN:
							result = UNKNOWN_LOGIN[ip_str]
						else:
							result = []

						data_tuple = (date_str_full, user_str, rsa_str, name)
						result.append(data_tuple)

						UNKNOWN_LOGIN[ip_str] = result
				else:
					# mark as UNKNOWN FINGERPRINT
					UNKNOWN_FINGERPRINT[rsa_str] = ip_str

local_ip = socket.gethostbyaddr(socket.gethostname())
date = str(time.strftime("%c"))
SEND_MESSAGE = False

contents = 'Box IP: ' + str(local_ip[0]) + ', Access Log for ' + str(date) + '\n'

if len(UNKNOWN_LOGIN) > 0:
	SEND_MESSAGE = True

	for key in UNKNOWN_LOGIN:
		num_accesses = len(UNKNOWN_LOGIN[key])
		contents += 'Foreign IP (' + str(key) + ') x' + str(num_accesses) + ' conns using: ' + str(UNKNOWN_LOGIN[key][0][2]) + ' (' + str(UNKNOWN_LOGIN[key][0][3]) + ')'
		contents += '\n'

if len(UNKNOWN_FINGERPRINT) > 0:
	SEND_MESSAGE = True

	contents += '\nThe following fingerprints are unknown: \n'
	for key in UNKNOWN_FINGERPRINT:
		contents += 'RSA (' + str(key) + ') with IP (' + str(UNKNOWN_FINGERPRINT[key]) + ')'
		contents += '\n'

if SEND_MESSAGE:
	headers = {'content-type': 'application/json'}
	slack = SlackAPI(api_url=SLACK_API_URL, headers=headers)
	slack.send_message(contents=contents, channel='#important', username='Mine Log', icon_emoji=':closed_lock_with_key:')

