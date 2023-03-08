#!/usr/bin/env bash
chromeVersion=`google-chrome --product-version | sed 's/\..*//'`
latestChromeDriverURL=$(wget http://chromedriver.storage.googleapis.com/LATEST_RELEASE_$chromeVersion -q -O -)
#latestChromeDriverURL=$(wget http://chromedriver.storage.googleapis.com/LATEST_RELEASE_99 -q -O -)
latestChromeDriverURL="110.0.5481.77"
echo $latestChromeDriverURL
wget "http://chromedriver.storage.googleapis.com/${latestChromeDriverURL}/chromedriver_linux64.zip"
unzip chromedriver_linux64.zip -d /home/travis/build/
chmod +x /home/travis/build/chromedriver
/home/travis/build/chromedriver --version