#!/bin/sh

ln -s /opt/patient-journal/bin/patient-journal /etc/init.d/patient-journal
ln -s /opt/patient-journal/etc/patient-journal /etc/default/patient-journal
ln -s /opt/patient-journal/var /var/run/patient-journal

if [ ! -e /var/log/patient-journal ]; then
    mkdir /var/log/patient-journal
fi

# Add patient-journal service to chkconfig
chkconfig --add patient-journal