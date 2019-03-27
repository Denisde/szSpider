#!/bin/bash
ps_count="`ps -ef | grep 'java -jar UN_Lsports_BookOdds.jar' | grep -v 'grep' | wc -l`"
pid="`ps -ef | grep 'java -jar UN_Lsports_BookOdds.jar' | grep -v 'grep' | awk '{print $2}'`"
if [ $ps_count -gt 1 ]
then
  echo 'more one, kill!'
  until [ $ps_count -eq 1 ]; do
  pid="`ps -ef | grep 'java -jar UN_Lsports_BookOdds.jar' | grep -v 'grep' | awk '{print $2}' | head -n 1`"
  kill $pid
  ps_count="`ps -ef | grep 'java -jar UN_Lsports_BookOdds.jar' | grep -v 'grep' | wc -l`"
  done
elif [ $ps_count -eq 0 ]
then
  echo 'no exist, start one'
  cd /home/szspider/za/UN_Lsports_BookOdds/Pre; sh run.sh&
else
  echo 'normal'
fi
