#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os
import sys
import zipfile

fpath = sys.argv[1]

# list all files
for dirpath, dirnames, filenames in os.walk(fpath):
    for filename in [f for f in filenames if f.endswith(".zip")]:
        fname = os.path.join(dirpath, filename)
        if os.path.isfile(fname):
            zf = zipfile.ZipFile(fname, 'r')
            print "###############################", fname
            for f in zf.namelist():
                data = zf.read(f)
                print data
    #print dirnames, filenames