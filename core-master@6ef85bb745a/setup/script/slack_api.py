#!/usr/bin/env python

# python modules
import json
import subprocess

# pip modules
import requests

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

