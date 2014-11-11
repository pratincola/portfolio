import json
import yahoo_options_data

computedJson = yahoo_options_data.contractAsJson("aapl.dat")
expectedJson = open("aapl.json").read()
expectedJson_change = open("aapl_change.json").read()

if json.loads(computedJson) != json.loads(expectedJson) and json.loads(computedJson) != json.loads(expectedJson_change):
  print "Test failed!"
  # print "Expected output:", expectedJson
  # print "Your output:", computedJson
  # assert False
else:
  print "Test passed"

#
# expectedJson_test = open("aapl_baseTest.json").read()
#
# expec_json = json.loads(expectedJson_test)
# compu_json = json.loads(computedJson)
# print "expec json", expec_json
# print "comp  json", compu_json
#
# if json.loads(expectedJson_test) == json.loads(computedJson) :
#     print "Test Passed"
# else:
#     print "lame"
