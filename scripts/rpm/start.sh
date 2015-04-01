#!/bin/sh
nohup java -Dserver.port=$PATIENT_JOURNAL_PORT -DPATIENT_JOURNAL_LOG_LEVEL=$PATIENT_JOURNAL_LOG_LEVEL -jar /opt/patient-journal/lib/PatientJournal.war > /dev/null 2>&1  &
echo $! > /var/run/patient-journal/patient-journal.pid