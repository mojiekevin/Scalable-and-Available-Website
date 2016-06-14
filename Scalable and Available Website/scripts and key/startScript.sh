#!/bin/bash
# install tomcat

# return private ip address

N=3
S3_BUCKET=edu-cornell-cs-cs5300s16-dj327

echo +++++++++++++++++++++++++
echo +++++++++++++++++++++++++
echo +++STARTCUSTOMER+++++++++
echo +++++++++++++++++++++++++
echo +++++++++++++++++++++++++

aws configure set aws_access_key_id AKIAIFDH3YINRI7FXUOQ
aws configure set aws_secret_access_key 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
aws configure set default.region us-east-1
aws configure set preview.sdb true

yum -y remove java-1.7.0-openjdk 
yum -y install java-1.8.0
yum -y install tomcat8-webapps tomcat8-docs-webapp tomcat8-admin-webapps

aws s3 cp  s3://${S3_BUCKET}/ssm.war /usr/share/tomcat8/webapps/ssm.war
aws s3 cp  s3://${S3_BUCKET}/reboot.sh /home/ec2-user/reboot.sh


echo ------------------------
echo ------------------------
localIp=$(curl http://169.254.169.254/latest/meta-data/local-ipv4)
echo localIp$localIp

amiIndex=$(curl http://169.254.169.254/latest/meta-data/ami-launch-index)
echo amiIndex$amiIndex
echo -------------------------
echo -------------------------


#aws sdb create-domain --domain-name serverInfo
#aws sdb put-attributes --domain-name serverInfo --item-name instance0 --attributes Name=string,Value=string,Replace=true





#	[
#	{ "Name": "ip", "Value":"8"},
#	{ "Name": "port", "Value":"8080"}
#	]


addQuery="[{ \"Name\": \"ip\", \"Value\":\"$localIp\"},{ \"Name\": \"index\", \"Value\":\"$amiIndex\"}]"
aws sdb put-attributes --domain-name serversData --item-name "instance$amiIndex" --attributes "$addQuery"


#aws sdb put-attributes --domain-name serverInfo --item-name instance0 --attributes "[{ \"Name\": \"ip\", \"Value\":\"8\"},{ \"Name\": \"port\", \"Value\":\"8080\"}]"



echo +++++++++++++++++++++++++
echo +++++++++++++++++++++++++
echo +++ENDCUSTOMER+++++++++++
echo +++++++++++++++++++++++++
echo +++++++++++++++++++++++++

while :
do
	#https://linuxconfig.org/how-to-extract-a-number-from-a-string-using-bash-example
	number=$(aws sdb select --select-expression "select count(*)  from serversData " --output text | grep -o -E '[0-9]+')
	echo $number
	if [ $number -eq $N ]
		then
		echo "all servers are online"
		aws sdb select --select-expression "select *  from serversData" > /usr/share/tomcat8/webapps/allServerInfo.txt
		echo ${amiIndex} > /usr/share/tomcat8/webapps/amiIndex.txt
		echo 0 > /usr/share/tomcat8/webapps/reboot.txt
		#chmod 777 /home
		#chmod 777 /home/ec2-user
		#chmod 777 /var/log/tomcat8/catalina.out
		#chmod 444 /usr/share/tomcat8/webapps/allServerInfo.txt
		#chmod 444 /usr/share/tomcat8/webapps/amiIndex.txt
		#chmod 444 /usr/share/tomcat8/webapps/reboot.txt
		break
	fi
	sleep 10
done

service tomcat8 start






