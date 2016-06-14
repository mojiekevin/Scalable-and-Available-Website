AMI=ami-08111162
KEY_PAIR=dj327_key3

aws configure set aws_access_key_id AKIAIFDH3YINRI7FXUOQ
aws configure set aws_secret_access_key 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
aws configure set default.region us-east-1
aws configure set preview.sdb true

aws sdb delete-domain --domain-name serversData
aws sdb create-domain --domain-name serversData

aws ec2 run-instances --image-id ${AMI} --count 3 --key-name ${KEY_PAIR} --instance-type t2.micro --security-groups cs5300_1b  --user-data file://startScript.sh


#getRealPath("/../all.txt")

#webapps

