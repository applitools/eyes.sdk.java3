### Integration of the generic tests into the java3 sdk
#### To run locally need to be installed:
- nodejs: https://nodejs.org/en/
- yarn: https://yarnpkg.com/getting-started/install
- docker: https://www.docker.com/get-started
- java
 
#### Inside the eyes.selenium.java module, could be used JS generic tool, there are available cli commands:
##### All the commands should be executed from the ${projectRoot}/eyes.selenium.java/
-   yarn java (used on Travis) - install nodejs dependencies => generate generic tests with execution grid flags => run testng suite with max concurrency => report the result to the QA sandbox dashboard
-   yarn release (used on Travis) - Same as yarn java but send report results to the QA production dashboard(HTML report is generated from the data accumulated on the QA production dashboard).
-   yarn local - install nodejs dependencies (without creating lockfile, so it will always install latest version of the tool) => start docker container for the selenium =>generate generic tests => run testng suite => report results to the QA sandbox dashboard => stop and remove docker container for the selenium
-   yarn generate - command generate generic tests
-   yarn generate:eg - command generate generic tests for execution grid 
-   yarn test - command generate generic tests for execution grid and run the generic test suite with higher concurrency
-   yarn test:local - command generate generic tests without execution grid and run the generic test suite with lower concurrency
-   yarn report - command send the report to QA sandbox dashboard
-   yarn report:prod - command send the report to QA production dashboard
-   yarn move:report - move the report generated from the testng run, from the target directory to the current working directory (DIR_PATH/eyes.selenium.java/). During the reporting JS tool will look for the 2 files, coverage-test-report.xml coverage-tests-metadata.json which should be at cwd.
-   mvn test - is command which run the generic tests testng suite. 
-   mvn test -DsuiteFile=EGSuite.xml - is command which run the generic tests testng suite with higher concurrency.
 