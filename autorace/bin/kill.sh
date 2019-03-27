#!/bin/bash
ps_count="`ps -ef | grep 'java -jar projectname-1.0.jar' | grep -v 'grep' | wc -l`"
pid="`ps -ef | grep 'java -jar projectname-1.0.jar' | grep -v 'grep' | awk '{print $2}'`"
until [ $ps_count -eq 0 ]; do
 pid="`ps -ef | grep 'java -jar projectname-1.0.jar' | grep -v 'grep' | awk '{print $2}' | head -n 1`"
 kill -9 $pid
 echo "kill $pid succ"
 ps_count="`ps -ef | grep 'java -jar projectname-1.0.jar' | grep -v 'grep' | wc -l`"
done
