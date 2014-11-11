import json
import sys
import re
import urllib
import pprint
from BeautifulSoup import BeautifulSoup

def contractAsJson(filename):
    jsonQuoteData = "[]"
    inFile = open(filename,'r').read()
    soup = BeautifulSoup(inFile)

    time_rtq_ticker = soup.find("span", {"class": "time_rtq_ticker"}).contents
    get_rt_quote = re.findall('\d*\.\d*',str(time_rtq_ticker[0]))[0]
    

    date_expire_table = soup.find("table", {"id": "yfncsumtab"})
    # print date_expire_table
    # print date_expire_table.tbody.tr.td.contents

    j = date_expire_table.findAll('tr')[2]
    k = j.findAll('td')[0]
    l = k.findAll('a')

    get_dateUrls = []
    for item in l:
        my_str = re.findall('\/\S+[\d](?=\")', str(item))[0]
        if '&amp;m' in str(my_str):
            get_dateUrls.append('http://finance.yahoo.com' + str(my_str))



    # data_tables = soup.find("table", {"class" : "yfnc_datamodoutline1"}).findAll('tr')
    data_tables = soup.findAll("table", {"class" : "yfnc_datamodoutline1"})
    optionQuotes = []
    get_strike  = re.compile(ur'(?<=>)(N\/A|[\d]+.[\d]+)(?=<)')   # find 50.00 in <td class="" nowrap=""><a href="/q/op?s=AAPL&amp;k=50.000000"><strong>50.00</strong></a></td>
    get_symbol  = re.compile(ur'(?<=>)(N\/A|\w|\w\D+7?)(?=\d)')   # (?<=>)(N\/A|\w\D+7|\w\D+)(?=\d)') # (?<=>)(N\/A|\w\D+)(?=\d)') find AAPL in <td class="yfnc_h"><a href="/q?s=AAPL140920C00050000">AAPL140920C00050000</a></td>
    get_type    = re.compile(ur'(?<=\d)(N\/A|\D)(?=\d)')
    get_date    = re.compile(ur'(N\/A|\d{6})(?=\D)')
    get_last    = re.compile(ur'(?<=>)(N\/A|[\d]+.[\d]+)(?=<)')
    get_chg     = re.compile(ur'(?<=>)[\d]+.[\d]+(?=<)')                     #[+-]?\d+.\d+(?=<)
    get_bid     = re.compile(ur'(?<=>)(N\/A|[\d]+.[\d]+)(?=<)')
    get_ask     = re.compile(ur'(?<=>)(N\/A|[\d]+.[\d]+)(?=<)')
    get_vol     = re.compile(ur'(?<=>)(N\/A|[\d]*.[\d]*)(?=<)')
    get_open_int= re.compile(ur'(?<=>)(N\/A|[\d]*.[\d]*)(?=<)')

    l = []
    for table in xrange(len(data_tables)):
        tr_data = data_tables[table].findAll('tr')
        for items in xrange(2, len(tr_data)):
            optionQuotes_dict = {}
            a = tr_data[items].findAll('td')
            optionQuotes_dict['Type'] = re.findall(get_type, str(a[1]))[0]
            optionQuotes_dict['Strike'] = re.findall(get_strike, str(a[0]))[0]
            optionQuotes_dict['Symbol'] = re.findall(get_symbol, str(a[1]))[0]
            optionQuotes_dict['Date'] = re.findall(get_date, str(a[1]))[0]
            optionQuotes_dict['Last'] = re.findall(get_last, str(a[2]))[0]
            optionQuotes_dict['Change'] = " " + re.findall(get_chg, str(a[3]))[0]
            optionQuotes_dict['Bid'] = re.findall(get_bid, str(a[4]))[0]
            optionQuotes_dict['Ask'] = re.findall(get_ask, str(a[5]))[0]
            optionQuotes_dict['Vol'] = re.findall(get_vol, str(a[6]))[0]
            optionQuotes_dict['Open'] = (re.findall(get_open_int, str(a[7]))[0]).replace(',','')    # Need to remove ',' from 234,234 and convert to int so that we can sort based on it.
            optionQuotes.append(optionQuotes_dict)
            # b = sorted(optionQuotes_dict,key=lambda s:s[0])
            # for i in b:
            #     print optionQuotes_dict[b]
            # print "dict", optionQuotes_dict
            # print type(b), b




    sorted_optionQuotes = sorted(optionQuotes, key=lambda t:int(t['Open']), reverse=True)
                           # sorted(testDict, key=lambda cha: testDict[cha].gpa)

    for i in sorted_optionQuotes:
        i['Open'] = "{:,}".format(int(i['Open']))



    python_object = {'currPrice' : float(get_rt_quote),
                    "dateUrls"  : get_dateUrls,
                    "optionQuotes": sorted_optionQuotes}
    jsonQuoteData = json.dumps(python_object)

    # pprint.pprint(jsonQuoteData, indent=4)
    return jsonQuoteData
