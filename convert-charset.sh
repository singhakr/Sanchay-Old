java -Xmx800m  -Djava.ext.dirs=lib -cp .:dist/Sanchay.jar:build/classes -Djava.security.policy=policy sanchay.text.enc.conv.EncodingConverter $@
