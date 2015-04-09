#!/bin/bash

sed s/REPLACE_WITH_DBCS_IP/$1/ < config-cloud > mydbssh
