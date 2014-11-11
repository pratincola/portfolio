__author__ = 'prateek'
import sys

from google.appengine.ext import ndb
from models.modelUtil import ModelUtils
from datetime import datetime
import logging


log = logging.getLogger(__name__)

class UserObject(ndb.Model):
    # user_id = ndb.StringProperty(required=True) #using id instead of user_id, helps with indexing...
    nickname = ndb.StringProperty()
    email = ndb.StringProperty()
    subscribe_list = ndb.StringProperty(repeated=True)
    email_rate = ndb.IntegerProperty(default=0)

    ''' Input: Takes a user_id & stream_id stream
        It will subscribe the user to the stream by adding the stream id to user's subscribe list'''
    @classmethod
    def subscribe_stream(cls, u_id, stream_id):
        log.info(u_id)
        # k = ndb.Key(UserObject, str(u_id)).id()
        # log.info(k)
        user_entity = cls.get_or_insert(u_id)
        log.info(user_entity)
        stream_list = user_entity.subscribe_list
        log.info(stream_list)
        stream_list.append(stream_id)
        user_entity.put()

    @classmethod
    def update_rate(cls, u_id, rate):
        log.info(u_id)
        user_entity = cls.get_by_id(u_id)
        user_entity.email_rate = int(rate)
        log.info(user_entity)
        user_entity.put()

    @classmethod
    def get_users_for_rate(cls, rate):
        return cls.query(cls.email_rate == rate).fetch()


    @classmethod # query doesnt work...
    def unsubscribe_stream(cls, user_id, stream_id):
        log.info(user_id)
        log.info(stream_id)
        user_entity = cls.query(cls.key==ndb.Key(cls, user_id)).get()
        log.info(user_entity)
        for i,subscription in enumerate(user_entity.subscribe_list):
            log.info(i)
            log.info(type(subscription))
            log.info(type(stream_id))
            if subscription == stream_id:
                log.info(stream_id)
                user_entity.subscribe_list.remove(user_entity.subscribe_list[i])
            user_entity.put()


class StreamObject(ModelUtils, ndb.Model):
    stream_owner = ndb.KeyProperty(kind=UserObject)
    stream_name = ndb.StringProperty(required=True)
    stream_tag = ndb.StringProperty(repeated=True)
    cover_url = ndb.StringProperty()
    lifetime_stream_view_count = ndb.IntegerProperty(default=0)
    img_count = ndb.IntegerProperty(default=0)
    trending_stream_count = ndb.IntegerProperty(default=0)
    click_times = ndb.DateTimeProperty(repeated=True)
    stream_create_time = ndb.DateTimeProperty(auto_now_add=True)
    stream_modified_Datetime = ndb.DateTimeProperty(auto_now=True) # this should get updated when we update img_count for the stream

    ''' Returns a list of stream for a given user'''
    @classmethod
    def my_stream(cls,accountUser_key, num):
        return cls.query(cls.stream_owner == accountUser_key)\
            .order(cls.stream_modified_Datetime)\
            .fetch(num)

    ''' Returns a list of stream a user is subscribed to'''
    @classmethod
    def my_subscription(cls, user_id, num):
        log.info("in here")
        user_subs = UserObject.get_by_id(user_id)
        log.info(user_subs)
        if user_subs:
            result = cls.query(cls.stream_name.IN(UserObject
                                            .get_by_id(user_id)
                                            .subscribe_list)).fetch(num)
        else:
            result = []
        return result

    ''' Removes the stream with the given 'id' from the datastore'''
    @classmethod
    def remove_stream(cls, stream_id):
        ndb.Key(cls, int(stream_id)).delete()

    ''' Return all streams with cover '''
    @classmethod
    def get_covers_to_display(cls):
        return cls.query().order(-cls.stream_modified_Datetime).fetch(projection=[cls.stream_name, cls.cover_url])

    ''' searches the hash-tags to find the result'''
    @classmethod
    def search_hash(cls, hash, num_results):
        return cls.query(cls.stream_tag == hash).fetch(num_results, projection=[cls.stream_name, cls.cover_url])

    @classmethod
    def update_click_times(cls, stream_id):
        log.info(stream_id)
        stream_entity = cls.get_by_id(int(stream_id))
        log.info(stream_entity)
        click_times = stream_entity.click_times
        log.info(click_times)
        dt = datetime.now()
        log.info(dt)
        click_times.append(dt)
        log.info(stream_entity)
        stream_entity.put()

    @classmethod
    def update_image_count(cls, stream_id):
        stream_entity = cls.get_by_id(int(stream_id))
        stream_entity.img_count += 1
        log.info(stream_entity)
        stream_entity.put()

    @classmethod
    def trending_streams(cls, num_results):
        return cls.query()\
            .order(cls.trending_stream_count)\
            .fetch(num_results, projection=[cls.stream_name, cls.cover_url, cls.trending_stream_count])

    @classmethod
    def get_click_times(cls):
        return cls.query().fetch()

    @classmethod
    def remove_old_clicks(cls):
        cls.query()

    @classmethod
    def get_all(cls):
        return cls.query().fetch(projection=[cls.stream_name, cls.stream_tag])


class ImageObject(ModelUtils, ndb.Model):
    stream_id = ndb.StringProperty()
    image_blob = ndb.BlobKeyProperty()
    date = ndb.DateTimeProperty(auto_now_add=True)
    geo_val = ndb.GeoPtProperty()

    ''' Returns a list of images for a given stream'''
    @classmethod
    def my_imgs(cls, stream_id, num, curs):
        log.info("querying my images...")
        return cls.query(cls.stream_id == stream_id)\
            .order(-cls.date).fetch_page(num, start_cursor=curs)

    @classmethod
    def all_stream_imgs(cls, stream_id, num):
        log.info("querying my images...")
        return cls.query(cls.stream_id == stream_id)\
            .order(-cls.date).fetch_page(num)
