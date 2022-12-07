#!/usr/bin/env bash
chromeVersion=`google-chrome --product-version | sed 's/\..*//'`
echo $chromeVersion
latestChromeDriverURL=$(wget http://chromedriver.storage.googleapis.com/LATEST_RELEASE_$chromeVersion -q -O -)
#latestChromeDriverURL=$(wget http://chromedriver.storage.googleapis.com/LATEST_RELEASE_99 -q -O -)
echo $latestChromeDriverURL
wget "http://chromedriver.storage.googleapis.com/107.0.5304.62/chromedriver_linux64.zip"
unzip chromedriver_linux64.zip -d /usr/local/bin/
chmod +x /usr/local/bin/chromedriver
/usr/local/bin/chromedriver --version