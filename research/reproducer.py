#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os
import sys
import subprocess
import re
import logging
import queue
import threading
import pathlib
from json_parser import parse_json

APPLICATIONS_DIR_PATH = ''
CRASHES_DIR_PATH = ''

DIJ_REPRODUCTION_JAR_PATH = ''

CONFIG_JSON_PATH = ''

RESULTS_DIR_PATH = ''

APPLICATIONS = {}
CRASHES = {}

APPLICATION_WHITELIST = []
CRASH_WHITELIST = []

NUM_OF_THREADS = 1

T_TIMEOUT = 200


def run_cases():
    q = queue.Queue()
    for crash in CRASHES.values():
        if len(APPLICATION_WHITELIST) != 0 or len(CRASH_WHITELIST) != 0:
            if crash.application not in APPLICATION_WHITELIST and crash.id not in CRASH_WHITELIST:
                continue
        if crash.application not in APPLICATIONS or crash.version not in APPLICATIONS[
                crash.application]:
            raise Exception(
                'Application \'{}\' with version \'{}\' not found'.format(
                    crash.application, crash.version))
        q.put(crash)

    def work(q):
        while True:
            if q.empty():
                return
            else:
                crash = q.get()
                run_case(crash)

    threads = []
    for i in range(NUM_OF_THREADS):
        threads.append(threading.Thread(target=work, args=(q, )))
    for t in threads:
        t.start()
    for t in threads:
        t.join()


def run_case(crash):
    logging.debug('Running crash \'{}\''.format(crash.id))

    project_cp = os.path.join(APPLICATIONS_DIR_PATH, crash.application,
                              crash.version, 'bin')
    crash_log = os.path.join(CRASHES_DIR_PATH, crash.application, crash.id,
                             crash.id + '.log')
    result_dir_path = os.path.join(RESULTS_DIR_PATH, crash.application,
                                   crash.id)
    pathlib.Path(result_dir_path).mkdir(parents=True, exist_ok=True)

    with open(crash_log, 'r') as f:
        frame = 1
        indented = False
        for line in f:
            # Frames are indented.
            if line.startswith(' ') or line.startswith('\t'):
                indented = True
                # Check if the frame matches the regex.
                if re.match(crash.target_frames, line):
                    logging.debug('Frame matched: [{}]:{}'.format(
                        frame, line.strip()))
                    try:
                        subprocess.check_call([
                            'java', '-jar', DIJ_REPRODUCTION_JAR_PATH,
                            '--', crash_log, '-target_frame',
                            str(frame), '-project_cp', project_cp,
                            '-Dtest_dir=' + result_dir_path,
                            '-Dsearch_budget=' + str(T_TIMEOUT - 100)
                        ],
                                              timeout=T_TIMEOUT)
                        subprocess.check_call([
                            'java', '-jar', DIJ_REPRODUCTION_JAR_PATH,
                            '-cp', 'project_cp',
                            '-tf', str(frame),
                            'o', 'result_dir_path',
                            '-t', '100',
                            crash_log,

                            ])
                    except subprocess.TimeoutExpired:
                        logging.warning('Running crash \'{}\' timeout'.format(
                            crash.id))
                frame += 1
            # Only one indented block is allowed, so break if another occurs.
            elif indented:
                break


if __name__ == '__main__':
    logging.basicConfig(level=logging.DEBUG)
    script_dir_path = os.path.dirname(os.path.realpath(__file__))
    jar_dir_path = os.path.join(script_dir_path, '..', 'target')
    for filename in os.listdir(jar_dir_path):
        if filename.startswith('dij-'):
            DIJ_REPRODUCTION_JAR_PATH = os.path.join(
                jar_dir_path, filename)
            break

    APPLICATIONS_DIR_PATH = os.path.join(script_dir_path, '.', 'applications')
    CRASHES_DIR_PATH = os.path.join(script_dir_path, '.', 'crashes')

    CONFIG_JSON_PATH = os.path.join(script_dir_path, '.', 'jcrashpack.json')

    RESULTS_DIR_PATH = os.path.join(script_dir_path, '.', 'results')

    if len(sys.argv) != 0 and (sys.argv[1] == '-h' or sys.argv[1] == '--help'):
        print('Usage: ' + sys.argv[0] +
              ' [-h | --help] | [[-a application | -c crash] ...]',
              file=sys.stderr)
        exit(1)

    parse_json(CONFIG_JSON_PATH, APPLICATIONS, CRASHES)
    idx = 1
    while idx < len(sys.argv):
        if sys.argv[idx] == '-a':
            if idx + 1 == len(sys.argv):
                logging.error('Missing \'application\' in args',
                              file=sys.stderr)
                exit(1)
            application = sys.argv[idx + 1]
            if application not in APPLICATIONS:
                logging.error('Invalid application \'{}\''.format(application))
                exit(1)
            APPLICATION_WHITELIST.append(application)
        elif sys.argv[idx] == '-c':
            if idx + 1 == len(sys.argv):
                logging.error('Missing \'crash\' in args', file=sys.stderr)
                exit(1)
            crash = sys.argv[idx + 1]
            if crash not in CRASHES:
                logging.error('Invalid crash \'{}\''.format(crash))
            CRASH_WHITELIST.append(crash)
        else:
            logging.error('Unrecognized arg \'{}\''.format(sys.argv[idx]),
                          file=sys.stderr)
            exit(1)
        idx += 2

    run_cases()
