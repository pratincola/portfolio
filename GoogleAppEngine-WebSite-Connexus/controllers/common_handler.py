from google.appengine.ext.deferred import deferred

__author__ = 'prateek'

import logging
import urllib
import json
import re
from models import *
from google.appengine.api.images import get_serving_url
from google.appengine.api import users
from base_handler import BaseHandler
from google.appengine.api import urlfetch
from google.appengine.datastore.datastore_query import Cursor
from google.appengine.ext import blobstore
from google.appengine.ext.webapp import blobstore_handlers
from google.appengine.api import images

from google.appengine.api import files


log = logging.getLogger(__name__)


class DefaultHandler(BaseHandler):
    def get(self):
        config = self.app.config
        super(DefaultHandler, self).render_response({})


class ManagementHandler(BaseHandler):
    def get(self):
        error = ''
        user = users.get_current_user()
        if user:
            account_user = self.request.get('accountUser')
            if not account_user:
                account_user = user.user_id()
            log.info(account_user)
            ''' Streams I own: '''
            streams = [d.to_dict() for d in StreamObject.my_stream(ndb.Key("UserObject", account_user), 5)]
            log.info("sd")
            ''' Streams I'm subscribed too'''
            # log.info(streams)
            user_entity = UserObject.get_by_id(account_user)
            # log.info(user_entity)
            # log.info(userEntity.subscribe_list)
            # subscribed_streams = [d.to_dict() for d in StreamObject.my_subscription(user_entity.subscribe_list, 2)]
            subscribed_streams = [d.to_dict() for d in StreamObject.my_subscription(account_user, 5)]
            self.render_response({'streams': streams, 'subscribedStreams': subscribed_streams}, 'manage.html')
        else:
            error += " Please log-in to view Management page"
            self.render_response({'error': error}, 'manage.html')

    def post(self):
        account_user = self.request.get('accountUser')
        if not account_user:
            user = users.get_current_user()
            account_user = user.user_id()
        log.info(account_user)

        delete_stream = self.request.get_all('delete')
        if delete_stream:
            for items in delete_stream:
                log.info(items)
                StreamObject.remove_stream(items)

        # Doesn't work at the moment...
        unsubs_stream = self.request.get_all('unsubscribe')
        log.info(unsubs_stream)
        if unsubs_stream:
            for items in unsubs_stream:
                log.info(items)
                UserObject.unsubscribe_stream(account_user, items)

        self.redirect('/ManagementPage')


class CreateStreamHandler(BaseHandler):
    def get(self):
        error = ''
        user = users.get_current_user()
        if user:
            self.render_response({}, 'create.html')
        else:
            error += " Please login to create a stream "
            self.render_response({'error': error}, 'create.html')


    def post(self):
        error = ''
        user = users.get_current_user()
        if user:
            streamName = self.request.get('streamName')
            subscribersList = (self.request.get('subscribersList')).split(',')
            optionalMessage = self.request.get('optionalMessage')
            streamTags = (self.request.get('streamTags')).split(',')
            coverURL = self.request.get('coverURL')

            updatedTags = [ i.replace('#', '') for i in streamTags]
            log.info(updatedTags)
            # user = users.get_current_user()
            # streamName = "pratincola"
            # subscribersList = ['4925812092436480','pratincola','patrick@gmail.com','as@as.com']
            # optionalMessage = "poop"
            # streamTags = ['#lucknow','#india','#stuff']  # TODO: remove hashes when adding hashtags...
            # coverURL = "http://www.w3schools.com/images/w3schools_green.jpg"

            log.info(user.user_id())
            # add a check to see if the user is logged-in ...else force login
            if True: #TODO: Need to add a method which parses the input & does not basic validation...
                u = UserObject(id=user.user_id(), nickname=user.nickname(), email=user.email(), subscribe_list=updatedTags)
                log.info(u)
                u_key = u.put() #perhaps user get_or_put function here...
                log.info(u_key)
                s = StreamObject(stream_owner=u_key,
                                 stream_name=streamName,
                                 stream_tag=streamTags,
                                 cover_url=coverURL
                                 )
                s.put()
                log.info(s)
                log.info(user)
                self.redirect("/ManagementPage?accountUser=%s" %user.user_id())

        else:
            error += " Please login to create a stream "
            self.render_response({'error': error}, 'create.html')


class ViewSingleStreamHandler(blobstore_handlers.BlobstoreUploadHandler,
                              blobstore_handlers.BlobstoreDownloadHandler,
                              BaseHandler):
    ''' /ViewStream?stream={{stream['key']}}&cursor='''
    def get(self):

        upload_url = blobstore.create_upload_url('/ViewStream')
        log.info(upload_url)
        view_stream = self.request.get('stream')
        log.info(view_stream)
        ''' Add clicks... '''
        StreamObject.update_click_times(view_stream)

        curs = Cursor(urlsafe=self.request.get('cursor'))  # range
        log.info(curs)

        images, next_curs, more = ImageObject.my_imgs(view_stream, 3, curs)
        # images = Images.my_imgs(view_stream, 3, curs)
        if not next_curs:
            next_curs_pos = ''
        else:
            next_curs_pos = next_curs.urlsafe()

        log.info(next_curs)
        log.info(more)
        log.info(images)

        # streams =  [d.to_dict() for d in Images.my_imgs(ndb.Key("StreamObject", view_stream), 3, curs)]

        # blob_info = [blobstore.BlobInfo.get(img.image_blob) for img in images]
        blob_info = [get_serving_url(img.image_blob) for img in images]
        log.info(blob_info)
        # self.send_blob(blob_info)

        self.render_response({'images': blob_info,
                              'more': more,
                              'next_curs': next_curs_pos,
                              'uploadUrl': upload_url,
                              'stream_id': view_stream}, "view_single_stream.html")


    def post(self):
        error = ''
        stream_id = self.request.get('streamId')
        user_id = users.get_current_user().user_id()
        file_upload = self.get_uploads('fileField')
        subscribe_stream = self.request.get('subscribeStream')

        log.info(stream_id)
        log.info(user_id)
        log.info(file_upload)
        log.info(subscribe_stream)
        log.info(":(")

        if file_upload:
            blob_info = file_upload[0]
            log.info(blob_info)
            try:
                ''' Upload image...'''
                s = ImageObject(stream_id=stream_id,
                                image_blob=blob_info.key())
                log.info(s)
                s.put()
                StreamObject.update_image_count(stream_id)
            except:
                error += " Problems uplaoding the picture"

        elif subscribe_stream and user_id:
            UserObject.subscribe_stream(user_id, stream_id)
        else:
            error += " You are not logged-in, please log-in and then subscribe"

        self.redirect('/ViewStream?stream=%s&cursor=' % stream_id)


class ViewAllStreamsHandler(BaseHandler):
    def get(self):

        stream_info = [d.to_dict() for d in StreamObject.get_covers_to_display()]
        log.info(stream_info)
        self.render_response({'stream': stream_info}, "view_all.html")

    ''' No implementation required as there is no action on this page. '''
    def post(self):
        pass


class SearchStreamsHandler(BaseHandler):
    def get(self):
        self.render_response({}, "search.html")

    def post(self):
        result = ''
        searh_term = self.request.get('localSearch')
        log.info(searh_term)
        if searh_term:
            result = [d.to_dict() for d in StreamObject.search_hash(searh_term, 5)]
            log.info(result)

        self.render_response({'search': searh_term,
                              'searchResult': result}, "search.html")


class TrendingStreamsHandler(BaseHandler):
    def get(self):
        error = self.request.get('error')
        result = [d.to_dict() for d in StreamObject.trending_streams(3)]
        self.render_response({'searchResult': result,
                              'error': error}, "trending.html")

    def post(self):
        error = ''
        update_rate = self.request.get('rate')
        log.info(update_rate)
        user = users.get_current_user()
        if user:
            u_id = user.user_id()
            UserObject.update_rate(u_id, update_rate)
            error += "Preference updated"
        else:
            error += "Please log-in before updating preference"

        self.redirect('/TrendingStreams?error=%s' % error)


class SocialMedia(BaseHandler):
    def get(self):
        self.render_response({}, "social.html")

class ErrorHandler(BaseHandler):
    def get(self):
        config = self.app.config
        super(ErrorHandler, self).render_response({'error': 'Unexpected Error'})

WEBSITE = '/ViewStreams'
MIN_FILE_SIZE = 1  # bytes
MAX_FILE_SIZE = 5000000  # bytes
IMAGE_TYPES = re.compile('image/(gif|p?jpeg|(x-)?png)')
ACCEPT_FILE_TYPES = IMAGE_TYPES
THUMBNAIL_MODIFICATOR = '=s80'  # max width / height
EXPIRATION_TIME = 300  # seconds


def cleanup(blob_keys):
    blobstore.delete(blob_keys)

    ''' /Upload '''


class UploadUrlHandler(BaseHandler):

    def initialize(self, request, response):
        super(UploadUrlHandler, self).initialize(request, response)
        self.response.headers['Access-Control-Allow-Origin'] = '*'
        self.response.headers[
            'Access-Control-Allow-Methods'
        ] = 'OPTIONS, HEAD, GET, POST, PUT, DELETE'
        self.response.headers[
            'Access-Control-Allow-Headers'
        ] = 'Content-Type, Content-Range, Content-Disposition'

    def validate(self, file):
        if file['size'] < MIN_FILE_SIZE:
            file['error'] = 'File is too small'
        elif file['size'] > MAX_FILE_SIZE:
            file['error'] = 'File is too big'
        elif not ACCEPT_FILE_TYPES.match(file['type']):
            file['error'] = 'Filetype not allowed'
        else:
            return True
        return False

    def get_file_size(self, file):
        file.seek(0, 2)  # Seek to the end of the file
        size = file.tell()  # Get the position of EOF
        file.seek(0)  # Reset the file position to the beginning
        return size

    def write_blob(self, data, info):
        blob = files.blobstore.create(
            mime_type=info['type'],
            _blobinfo_uploaded_filename=info['name']
        )
        with files.open(blob, 'a') as f:
            f.write(data)
        files.finalize(blob)
        return files.blobstore.get_blob_key(blob)

    def handle_upload(self):
        results = []
        blob_keys = []
        for name, fieldStorage in self.request.POST.items():
            log.info(name)
            log.info(fieldStorage)
            if name == 'stream_name':
                stream_name = str(fieldStorage)
            if type(fieldStorage) is unicode:
                continue
            result = {}
            result['name'] = re.sub(
                r'^.*\\',
                '',
                fieldStorage.filename
            )
            image_name = str(re.findall('\D+(?=\.)', str(fieldStorage.filename))[0])
            result['type'] = fieldStorage.type
            result['size'] = self.get_file_size(fieldStorage.file)
            if self.validate(result):
                blobKey = self.write_blob(fieldStorage.value, result)
                blob_key = str(blobKey)
                blob_keys.append(blob_key)
                result['deleteType'] = 'DELETE'
                result['deleteUrl'] = self.request.host_url +\
                    '/Upload/?key=' + urllib.quote(blob_key, '')
                if (IMAGE_TYPES.match(result['type'])):
                    try:
                        result['url'] = images.get_serving_url(
                            blob_key,
                            secure_url=self.request.host_url.startswith(
                                'https'
                            )
                        )
                        result['thumbnailUrl'] = result['url'] +\
                            THUMBNAIL_MODIFICATOR
                    except:  # Could not get an image serving url
                        pass
                if not 'url' in result:
                    result['url'] = self.request.host_url +\
                        '/' + blob_key + '/' + urllib.quote(
                            result['name'].encode('utf-8'), '')
            results.append(result)
            log.info(stream_name)
            log.info(blobKey)
            new_image_profile = ImageObject(stream_id=stream_name, image_blob=blobKey)
            new_image_profile.put()
            # StreamObject.update_image_count(stream_name)
        deferred.defer(cleanup, blob_keys, _countdown=EXPIRATION_TIME)
        return results

    def options(self):
        pass

    def head(self):
        pass

    def get(self):
        self.redirect(WEBSITE)

    def post(self):
        if (self.request.get('_method') == 'DELETE'):
            return self.delete()
        result = {'files': self.handle_upload()}
        s = json.dumps(result, separators=(',', ':'))
        redirect = self.request.get('redirect')
        if redirect:
            return self.redirect(str(
                redirect.replace('%s', urllib.quote(s, ''), 1)
            ))
        if 'application/json' in self.request.headers.get('Accept'):
            self.response.headers['Content-Type'] = 'application/json'
        self.response.write(s)

    def delete(self):
        key = self.request.get('key') or ''
        blobstore.delete(key)
        s = json.dumps({key: True}, separators=(',', ':'))
        if 'application/json' in self.request.headers.get('Accept'):
            self.response.headers['Content-Type'] = 'application/json'
        self.response.write(s)

class DownloadHandler(blobstore_handlers.BlobstoreDownloadHandler):
    def get(self, key, filename):
        if not blobstore.get(key):
            self.error(404)
        else:
            # Prevent browsers from MIME-sniffing the content-type:
            self.response.headers['X-Content-Type-Options'] = 'nosniff'
            # Cache for the expiration time:
            self.response.headers['Cache-Control'] = 'public,max-age=%d' % EXPIRATION_TIME
            # Send the file forcing a download dialog:
            self.send_blob(key, save_as=filename, content_type='application/octet-stream')


class AutoComplete(BaseHandler):
    # def get(self):
    #     result = []
    #     search_term = self.request.get('term')
    #     log.info(search_term)
    #     for d in StreamObject.get_all():
    #         result.append(d.stream_name)
    #         result.append(d.stream_tag[0])
    #     log.info(result)
    #     self.render_json({'data':result})

    def get(self):
        result = []
        for d in StreamObject.get_all():
            result.append(d.stream_name)
            result.append(d.stream_tag[0])
        log.info(result)
        self.render_json({'data':result})


    ''' /GeoView?stream={{stream['key']}}&cursor='''
class GeoView(BaseHandler):
    def get(self):
        view_stream = self.request.get('stream')
        self.render_response({'stream_id': view_stream}, "geo_view.html")


class GetGeoInfoForImages(BaseHandler):
    def get(self):
        streamId = self.request.get('stream')
        log.info(streamId)
        image_key, next_curs, more = ImageObject.all_stream_imgs(streamId, 10)
        log.info(image_key)
        img_dict = [img.img_to_dict() for img in image_key]
        log.info(img_dict)
        self.render_json({'meta_images': img_dict})



### For Mobile View
class AllJsonStreamHandler(BaseHandler):
    def get(self):
        stream_info = [d.to_dict() for d in StreamObject.get_covers_to_display()]
        log.info(stream_info)
        self.render_json(stream_info)

    ''' No implementation required as there is no action on this page. '''
    def post(self):
        pass

class SingleJsonStreamHandler(blobstore_handlers.BlobstoreUploadHandler,
                              blobstore_handlers.BlobstoreDownloadHandler,
                              BaseHandler):
    ''' /ViewStream?stream={{stream['key']}}&cursor='''
    def get(self):

        upload_url = blobstore.create_upload_url('/ViewStream')
        log.info(upload_url)
        view_stream = self.request.get('stream')
        log.info(view_stream)
        ''' Add clicks... '''
        StreamObject.update_click_times(view_stream)

        images, next_curs, more = ImageObject.all_stream_imgs(view_stream, 16)
        log.info(images)

        blob_info = [get_serving_url(img.image_blob) for img in images]
        log.info(blob_info)

        self.render_json({'images': blob_info,
                          'uploadUrl': upload_url,
                          'stream_id': view_stream
        })