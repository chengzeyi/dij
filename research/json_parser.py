#!/usr/bin/env python3
# -*- coding: utf-8 -*-


import json
import os
from common import Version, Crash


def parse_json(json_path, applications, crashes):
    with open(json_path, 'r') as json_file:
        data = json.load(json_file)
        # Check if 'applications' and 'crashes' keys exist.
        for key in ['applications', 'crashes']:
            if key not in data:
                raise Exception('Missing \'{}\' entry in JSON'.format(key))
        # Parse applications.
        for application_key, application in data['applications'].items():
            applications[application_key] = {}
            if 'versions' not in application:
                raise('Missing \'versions\' entry in application \'{}\''.format(application_key))
            for version_key, version in application['versions'].items():
                applications[application_key][version_key] = Version(version, version_key)
        # Parse crashes.
        for crash_key, crash in data['crashes'].items():
            for key in ['application', 'buggy_frame', 'version']:
                if key not in crash:
                    raise Exception('Missing \'{}\' entry in crash \'{}\''.format(key, crash_key))
            crashes[crash_key] = Crash(crash, crash_key)


if __name__ == '__main__':
    script_dir_path = os.path.dirname(os.path.realpath(__file__))
    json_path = os.path.join(script_dir_path, '..', 'jcrashpack.json')
    applications = {}
    crashes = {}
    parse_json(json_path, applications, crashes)
    print('----------')
    print('Applications')
    for application_key, application in applications.items():
        print('----------')
        print('{}'.format(application_key))
        for version in application.values():
            print(version)
    print('----------')
    print('Crashes')
    for crash in crashes.values():
        print(crash)
