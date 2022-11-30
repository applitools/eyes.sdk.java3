#!/usr/bin/env bash
chromeVersion=`google-chrome --product-version | sed 's/\..*//'`
latestChromeDriverURL=$(wget http://chromedriver.storage.googleapis.com/LATEST_RELEASE_$chromeVersion -q -O -)
#latestChromeDriverURL=$(wget http://chromedriver.storage.googleapis.com/LATEST_RELEASE_99 -q -O -)
echo $latestChromeDriverURL
wget "http://chromedriver.storage.googleapis.com/108.0.5359.71/chromedriver_linux64.zip"
unzip chromedriver_linux64.zip -d /home/travis/build/
chmod +x /home/travis/build/chromedriver
/home/travis/build/chromedriver --version