__author__ = 'prateek'

#!/usr/bin/env python
#
import webapp2
import logging

#adjust library path before any other module gets imported...
import fix_path

from controllers import *
from config import app_config


log = logging.getLogger(__name__)


route_list = [(r'^/', DefaultHandler),
              (r'^/ManagementPage', ManagementHandler),
              (r'^/CreateStreamPage', CreateStreamHandler),
              (r'^/ViewStream', ViewSingleStreamHandler),
              (r'^/ViewStreams', ViewAllStreamsHandler),
              (r'^/SearchStreams', SearchStreamsHandler),
              (r'^/TrendingStreams', TrendingStreamsHandler),
              (r'^/Social', SocialMedia),
              (r'^/GeoView', GeoView),
              (r'^/GetGeoInfo', GetGeoInfoForImages),
              (r'^/Error', ErrorHandler),
              (r'^/Upload', UploadUrlHandler),
              (r'^/AutoComplete', AutoComplete),

              (r'^/Streams.json', AllJsonStreamHandler),
              (r'^/Stream.json', SingleJsonStreamHandler)]

app = webapp2.WSGIApplication(route_list,
                              config=app_config,
                              debug=app_config.get('debug', True))
