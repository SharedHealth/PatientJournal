#!/bin/sh
nohup java -Dserver.port=$PATIENT_JOURNAL_PORT -jar /opt/patient-journal/lib/PatientJournal.war >/var/log/patient-journal/patient-journal.log &
echo $! > /var/run/patient-journal/patient-journal.pid