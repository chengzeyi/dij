#!/usr/bin/env python3
# -*- coding: utf-8 -*-


class Version(object):

    def __init__(self, node, node_name):
        self.src_url = '' if 'src_url' not in node else node['src_url']
        self.version = node_name

    def __repr__(self):
        return """+----------
|src_url: {}'
|version: {}
+----------""".format(self.src_url, self.version)


class Crash(object):

    def __init__(self, node, node_name):
        self.application = node['application']
        self.buggy_frame = node['buggy_frame']
        self.fixed_commit = '' if 'fixed_commit' not in node else node['fixed_commit']
        self.id = node_name
        self.issue = '' if 'issue' not in node else node['issue']
        self.target_frames = '' if 'target_frames' not in node else node['target_frames']
        self.version = node['version']
        self.version_fixed = '' if 'version_fixed' not in node else node['version_fixed']

    def __repr__(self):
        return """+----------
|application: {}
|buggy_frame: {}
|fixed_commit: {}
|id: {}
|issue: {}
|target_frames: {}
|version: {}
|version_fixed: {}
+----------""".format(
        self.application,
        self.buggy_frame,
        self.fixed_commit,
        self.id,
        self.issue,
        self.target_frames,
        self.version,
        self.version_fixed
    )
