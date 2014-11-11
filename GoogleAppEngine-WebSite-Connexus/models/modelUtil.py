from google.appengine.api.images import get_serving_url

__author__ = 'prateek'

class ModelUtils(object):
    def to_dict(self):
        result = super(ModelUtils, self).to_dict()
        result['key'] = self.key.id()   # get the key as a string
        return result

    def img_to_dict(self):

        result = {"image_url": get_serving_url(self.image_blob),
                  "image_date": self.date.strftime("%m,%d,%Y"),
                  "image_geo_val": self.geo_val
                }
        # return dict((item.name, item.number) for item in iter(self))
        return result

    def search_to_dict(self):
        result = {"stream_name": self.stream_name,
                  "stream_tag": self.stream_tag
                }
        # return dict((item.name, item.number) for item in iter(self))
        return result