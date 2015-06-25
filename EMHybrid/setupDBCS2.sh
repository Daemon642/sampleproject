#!/bin/bash
#
cat /etc/sudoers /home/opc/oracleSudo.txt > /tmp/oracleSudo
cp /tmp/oracleSudo /etc/sudoers
chown root:root /etc/sudoers
chmod 440 /etc/sudoers
