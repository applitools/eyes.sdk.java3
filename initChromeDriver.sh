#!/usr/bin/env bash
chromeVersion=`google-chrome --product-version | sed 's/\..*//'`
echo $chromeVersion
latestChromeDriverURL=$(wget http://chromedriver.storage.googleapis.com/LATEST_RELEASE_107.0.5304.62 -q -O -)
#latestChromeDriverURL=$(wget http://chromedriver.storage.googleapis.com/LATEST_RELEASE_99 -q -O -)
echo $latestChromeDriverURL
wget "http://chromedriver.storage.googleapis.com/${latestChromeDriverURL}/chromedriver_linux64.zip"
unzip chromedriver_linux64.zip -d /home/travis/build/
chmod +x /home/travis/build/chromedriver
/home/travis/build/chromedriver --version