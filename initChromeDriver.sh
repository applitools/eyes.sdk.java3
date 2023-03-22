#!/usr/bin/env bash
chromeVersion=`google-chrome --product-version | sed 's/\..*//'`
echo $chromeVersion
latestChromeDriverURL=$(wget http://chromedriver.storage.googleapis.com/LATEST_RELEASE_$chromeVersion -q -O -)
echo $latestChromeDriverURL
wget "http://chromedriver.storage.googleapis.com/${latestChromeDriverURL}/chromedriver_linux64.zip"
unzip chromedriver_linux64.zip -d /usr/local/bin/
chmod +x /usr/local/bin/chromedriver
/usr/local/bin/chromedriver --version
