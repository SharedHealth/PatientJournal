#!/bin/sh

rm -f /etc/init.d/patient-journal
rm -f /etc/default/patient-journal
rm -f /var/run/patient-journal

#Remove patient-journal from chkconfig
chkconfig --del patient-journal || true
