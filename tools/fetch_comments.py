# -*- coding: utf-8 -*-

# Sample Python code for youtube.comments.list
# See instructions for running these code samples locally:
# https://developers.google.com/explorer-help/code-samples#python


from bs4 import BeautifulSoup
import dotenv
import json
import os
import googleapiclient.discovery


def cleanse(text):
    return BeautifulSoup(text,features="html.parser").get_text()


def main():
    dotenv.load_dotenv()
    api_key = os.environ['API_KEY']
    channel_id = os.environ['CHANNEL_ID']
    api_service_name = "youtube"
    api_version = "v3"

    youtube = googleapiclient.discovery.build(
        api_service_name, api_version, developerKey=api_key)

    request = youtube.commentThreads().list(
        part=["id", "snippet", "replies"],
        allThreadsRelatedToChannelId=channel_id,
        maxResults=100,
    )
    response = request.execute()

    just_the_comments = [cleanse(item['snippet']['topLevelComment']['snippet']['textDisplay']) for item in response['items']]
    for comment in just_the_comments:
        print(comment)


if __name__ == "__main__":
    main()
