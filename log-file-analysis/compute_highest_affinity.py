# No need to process files and manipulate strings - we will
# pass in lists (of equal length) that correspond to 
# sites views. The first list is the site visited, the second is
# the user who visited the site.

# See the test cases for more details.
from sets import Set


def highest_affinity(site_list, user_list):
    # Returned string pair should be ordered by dictionary order
    # I.e., if the highest affinity pair is "foo" and "bar"
    # return ("bar", "foo").
    site_user_dict = dict()
    affinity_pairs = dict()
    site_user_list = []

    ''' Creating a dictionary of key='website' & value='set of users' '''
    for idx, val in enumerate(site_list):
        if val in site_user_dict:
            # add more users to the site
            site_user_dict[val].add(user_list[idx])
        else:
            # first user to the site, create a Set with one element
            a = Set()
            a.add(user_list[idx])
            site_user_dict[val] = a


    ''' Actual implementation of the logic to find which pair of websites are the most visited'''

    for key in site_user_dict.iterkeys():
        site_user_list.append(key)

    for idx1, val1 in enumerate(site_user_list):
        # print "idx1:", idx1, val1, len(site_user_list)
        for idx2 in xrange(idx1 + 1, len(site_user_list)):
            j = (site_user_dict[site_user_list[idx1]] & site_user_dict[site_user_list[idx2]])
            # print "\t pair", site_user_list[idx1], site_user_list[idx2], "has ", len(j), " users ", j, " in common "
            affinity_pairs[len(j)] = [site_user_list[idx1], site_user_list[idx2]]
    result = sorted(affinity_pairs[sorted(affinity_pairs, reverse=True)[0]])
    return result[0], result[1]
