reboot=$(</usr/share/tomcat8/webapps/reboot.txt)
reboot=$((reboot+1))
echo $reboot >  /usr/share/tomcat8/webapps/reboot.txt

service tomcat8 start