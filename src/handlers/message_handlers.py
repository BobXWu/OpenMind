#!/usr/bin/python
# -*- coding:utf-8 -*-

# Author 		: 	Lv Yang
# Created 		: 	30 August 2016
# Modified 		: 	30 August 2016
# Version 		: 	1.0

"""
This script used to deal with requests about different kinds of messages

each function accepts four parameters :
	'post_data' is the post data from client
	'post_files' is the uploads files from client
	'usr_sessions' is the current user's sessions
	'server_conf' is the global shared configuration
each function returns a dict
"""

import time
from bson import ObjectId

import mongo_conn
import active_manager
import xss_filter

# deal with requests of sending a comment
def send_comment(post_data,post_files,usr_sessions,server_conf):
    # when not login
    if  'id' not in usr_sessions:
        return {'result':False,'reason':1}

    # check ""
    if len(post_data['content']) is 0:
        return {'result':False,'reason':3}

    # prepare timestamp and timestr
    time_stamp = int(time.time())
    time_array = time.localtime(time_stamp)
    time_str = time.strftime("%Y%m%d",time_array)
    this_month = int(time.strftime("%Y%m",time_array))
    this_day = int(time.strftime("%d",time_array))

    # filter xss
    th_parent_id = xss_filter.valid_filter(post_data['parent_id'])
    th_recv_usr = xss_filter.valid_filter(post_data['recv_usr'])
    th_recv_name = xss_filter.valid_filter(post_data['recv_name'])
    th_content = xss_filter.valid_filter(post_data['content'])
    th_own_usr = xss_filter.valid_filter(post_data['own_usr'])
    th_proj_id = xss_filter.valid_filter(post_data['proj_id'])
    th_proj_name = xss_filter.valid_filter(post_data['proj_name'])

    # construct a comment
    one_comment = {}
    one_comment['id'] = "%d%s"%(time_stamp,usr_sessions['id'])
    one_comment['parent_id'] = th_parent_id
    one_comment['send_usr'] = usr_sessions['id']
    one_comment['send_name'] = usr_sessions['name']
    one_comment['send_head'] = usr_sessions['head']
    one_comment['recv_usr'] = th_recv_usr
    one_comment['recv_name'] = th_recv_name
    one_comment['time'] = time_stamp
    one_comment['content'] = th_content

    # construct a message to inform this project's owner
    msg = {}
    msg['username'] = th_own_usr
    msg['who_usr'] = usr_sessions['id']
    msg['who_name'] = usr_sessions['name']
    msg['who_head'] = usr_sessions['head']
    msg['time'] = time_stamp
    msg['proj_id'] = th_proj_id
    msg['proj_name'] = th_proj_name
    msg['action_id'] = 0
    msg['content'] = th_content

    # connect to mongo
    db_name = server_conf['mongo']['db_name']
    mongo_client = mongo_conn.get_conn(server_conf['mongo']['host'],db_name,\
        server_conf['mongo']['db_user'],server_conf['mongo']['db_pwd'])

    # declare response
    response = {'result':False,'reason':2}

    # push a comment to mongo
    update_factor_1 = {'_id':ObjectId(th_proj_id),'own_usr':th_own_usr}
    update_factor_2 = {'$push':{'comments':one_comment}}
    update_res = mongo_client[db_name]['project_info'].update_one(update_factor_1,update_factor_2)
    if update_res.modified_count > 0:
        # update this commentor's active data
        active_manager.increase_active(mongo_client,db_name,usr_sessions['id'],\
            this_month,this_day,\
            server_conf['active']['comment_inc'])

        # insert a message to inform this project's owner
        mongo_client[db_name]['associate_info'].insert_one(msg)

        # insert a message to inform the user who I reply
        if th_parent_id != "0" and th_own_usr!=th_recv_usr:
            msg['username'] = th_recv_usr
            msg.pop('_id',None)
            mongo_client[db_name]['associate_info'].insert_one(msg)

        # set response
        response = {'result':True,'comment':one_comment}

    # delete some objects
    mongo_client.close()
    del mongo_client
    del time_stamp
    del time_array
    del time_str
    del this_month
    del this_day
    del one_comment
    del msg
    del db_name
    del update_factor_1
    del update_factor_2
    del update_res
    del th_parent_id
    del th_recv_usr
    del th_recv_name
    del th_content
    del th_own_usr
    del th_proj_id
    del th_proj_name

    # return result
    return response


# deal with requests of receiving messages about me
def receive_messages(post_data,post_files,usr_sessions,server_conf):
    # declare response
    response = []

    # when not login
    if  'id' not in usr_sessions:
        return response

    # accept parameters from client
    page_size = int(post_data['page_size'])
    time_max = int(post_data['time_max'])

    # connect to mongo
    db_name = server_conf['mongo']['db_name']
    mongo_client = mongo_conn.get_conn(server_conf['mongo']['host'],db_name,\
        server_conf['mongo']['db_user'],server_conf['mongo']['db_pwd'])

    # get messages
    query_factor_1 = {'username':usr_sessions['id'],'time':{'$lt':time_max}}
    query_factor_2 = {'_id':0,'username':0}
    data = mongo_client[db_name]['associate_info'].find(query_factor_1,query_factor_2).limit(page_size)
    for msg in data:
        response.append(msg)
        del msg

    # delete some objects
    mongo_client.close()
    del mongo_client
    del db_name
    del query_factor_1
    del query_factor_2
    del data

    # return response
    return response


# deal with requests of get comments of one project
def get_project_comments(post_data,post_files,usr_sessions,server_conf):
    # declare response
    response = []

    # connect to mongo
    db_name = server_conf['mongo']['db_name']
    mongo_client = mongo_conn.get_conn(server_conf['mongo']['host'],db_name,\
        server_conf['mongo']['db_user'],server_conf['mongo']['db_pwd'])

    # do query
    query_factor_1 = {'_id':ObjectId(post_data['proj_id'])}
    query_factor_2 = {'_id':0,'comments':1}
    data = mongo_client[db_name]['project_info'].find_one(query_factor_1,query_factor_2)
    if data is not None:
        response = data['comments']

    # delete some objects
    mongo_client.close()
    del mongo_client
    del db_name
    del query_factor_1
    del query_factor_2
    del data

    # return result
    return response