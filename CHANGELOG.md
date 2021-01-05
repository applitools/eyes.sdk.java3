## [vNext]
### Fixed
- A bug in setting server configuration in UFG tests. [Trello 2404](https://trello.com/c/4AuvRh4U)

## [3.190.2]
### Updated
- Dom snapshot 4.4.3. [Trello 2405](https://trello.com/c/wBy9nGQi)
- Added debug screenshot before calling dom snapshot in js layout.
### Fixed
- Setting server configuration now works properly in UFG tests. [Trello 2404](https://trello.com/c/4AuvRh4U)

## [3.190.1] - 2021-01-01
### Fixed
- reverted rename of "getIsOpen". 

## [3.190.0] - 2020-12-31
### Added
- New JS layout feature and `setLayoutBreakpoints' API. [Trello 2387](https://trello.com/c/8caokJ19) [Trello 2258](https://trello.com/c/3Ty3HRCG)
### Updated
- Updated logger structure. [Trello 2395](https://trello.com/c/NuhnOCD6)
- Updated `isMobileDevice()` method to make `isBrowser()` accessible. [Trello 2307](https://trello.com/c/8VCtSmfN)
- Now sending screenshot location in match window data and setting correct scrolled element for dom capture. [Trello 2379](https://trello.com/c/Wn9Fp4XN)
- A massive update to eyes runners and services. [Trello 2092](https://trello.com/c/gulak9SJ)
### Fixed 
- Initialize content size from element bounds if IOException is thrown. [Trello 2250](https://trello.com/c/mVAzVx0X)

## [3.189.0] - 2020-12-17
### Fixed
- DOM Capture won't be stuck in an infinite recursion because of bidirectional frame dependency anymore. [Trello 2292](https://trello.com/c/HmNKvpk4)
### Updated
- Dom snapshot 4.4.1. [Trello 2360](https://trello.com/c/Nkq5X4dJ)
- Updated method for searching scrollable view. Ignore horizontal scrollable views on searching for the scrollable element. [Trello 2347](https://trello.com/c/FDZ1oWF9) 
- Adjusted scroll coordinates on a stitchOverlap value. [Trello 2138](https://trello.com/c/EI2mxmTD)
- Added new browser for UFG tests: SAFARI_EARLY_ACCESS. [Trello 2385](https://trello.com/c/5PncFGDO)

## [3.188.0] - 2020-12-09
### Fixed
- Visual locators user-defined scale provider. [Trello 2300](https://trello.com/c/RdB0I4G2)
### Updated
- Concurrency version 2. [Trello 2368](https://trello.com/c/0qi2c0jW)

## [3.187.0] - 2020-12-07
### Fixed
- Added setApiKey and setProxy to BatchClose. [Trello 2189](https://trello.com/c/SlHH9Ssb)
### Updated
- Use default viewport height value if we get an exception after `driver.getSystemBars()` call. [Trello 2307](https://trello.com/c/8VCtSmfN)
- Disabled skip list feature temporarily. [Trello 2349](https://trello.com/c/V8ldfbZu)

## [3.186.0] - 2020-11-23
### Updated
- Agent ID property name (Internal). [Trello 2344](https://trello.com/c/hNfSbXNe)

## [3.185.1] - 2020-11-23
### Fixed
- Fixed a bug in the resource collection task. [Trello 2336](https://trello.com/c/zzgVhf4a)

## [3.185.0] - 2020-11-18
## Fixed
- URLs sanitizing for skip list. [Trello 1974](https://trello.com/c/44xq8dze)
- All configuration set methods support fluent api. [Trello 2334](https://trello.com/c/oMalocSn)
## Updated
- DOM Snapshot 4.2.7. [Trello 2260](https://trello.com/c/IKvMS37R)

## [3.184.0] - 2020-11-18
### Added
- New DOM scripts features. [Trello 2268](https://trello.com/c/x6ImzMue)
- UFG skip list functionality. [Trello 1974](https://trello.com/c/44xq8dze)
### Updated
- Updated concurrency model. [Trello 2152](https://trello.com/c/yNzhBkBh)
### Fixed
- Fixed `DeviceSize` class to use `landscape` field. [Trello 2150](https://trello.com/c/8xXBu5Wk)
- Debug resource writer bugfix. [Trello 2310](https://trello.com/c/EOSqNaoG)

## [3.183.0] - 2020-11-09
### Added
- Stitch overlap functionality for iOS. [Trello 2138](https://trello.com/c/EI2mxmTD)
- Implemented full-coverage report event. [Trello 2019](https://trello.com/c/3y4UcfXd)
### Updated
- Supporting iPhone 12 in `IosDeviceName` class. [Trello 2269](https://trello.com/c/yWFy2pRE)
- Assembling test results. [Trello 2286](https://trello.com/c/pVwVbFr1)
### Fixed
- Calling check more than once is now possible on images SDK. [Trello 2288](https://trello.com/c/hnnPOkVf)
- Unifying urls which are similar (`#` or `?` in the end of the url). [Trello 2299](https://trello.com/c/r13tsIrG)
- Fixed a bug in the css tokenizer. [Trello 2299](https://trello.com/c/r13tsIrG)
- Now sanitizing relative urls. [Trello 2299](https://trello.com/c/r13tsIrG)

## [3.182.0] - 2020-10-28 
### Added
- New logs for printing dom snapshot result. [Trello 2252](https://trello.com/c/7aalHb28)
- New API `runner.setDontCloseBatched` in case the user doesn't want to close the batches in the end of the test. [Trello 1908](https://trello.com/c/8BGfvXKU)
### Updated
- Dom Snapshot script version 4.2.2. [Trello 2226](https://trello.com/c/yH8WYHgt)
- Dom Snapshot script version 4.2.3 and DOM Capture script version 8.0.1. [Trello 2260](https://trello.com/c/IKvMS37R)
- Added retry mechanism for requests for the server. [Trello 2265](https://trello.com/c/fv6gC31H) 
- Getting platform version with previous implementation to avoid NoSuchMethodError with old Appium client versions. [Trello 2204](https://trello.com/c/LfbGdoxz)
### Fixed
- Now checking if the element is scrollable before choosing default root element. [Trello 2198](https://trello.com/c/DTvpdAj4), [Trello 2207](https://trello.com/c/v5s4lv8u), [Trello 2215](https://trello.com/c/nUzTl0KB)
- CSS stitching is now working when checking a frame overlapping with elements from its outer frame. [Trello 1846](https://trello.com/c/grlCdwMs)
- Calling close without open now behaves properly. [Trello 2241](https://trello.com/c/h7tW49Nz)
- Calling session delete session will work without setting a new server connector. [Trello 2246](https://trello.com/c/aANiFwRX)
- Fixed crashing app with no helper library and recyclerView, listView etc inside. [Trello 2202](https://trello.com/c/qEH2mQgP)

## [3.181.0] - 2020-10-16
### Added
- A new API for closing batch explicitly: `BatchClose`. [Trello 2189](https://trello.com/c/SlHH9Ssb)
- A new log handler for sending logs to the eyes server. [Trello 2206](https://trello.com/c/EX8JfK7W)
- Supporting check full element with ufg. [Trello 2145](https://trello.com/c/8tPAnz66)
### Fixed
- Fixed ignoring statusBar height on cropping when invisible. [Trello 2202](https://trello.com/c/qEH2mQgP)
### Updated
- Removed appium dependency from eyes-selenium-common module. [Trello 2188](https://trello.com/c/uTbDNRdf)

## [3.180.0] - 2020-10-09
### Updated
- DeviceName now includes new mobile devices. [Trello 1751](https://trello.com/c/JOyUqzEM)
- IosDeviceInfo includes the version property. [Trello 2187](https://trello.com/c/25AjSV6V)
- Cleaned the `DomCapture` class to be more clear and less buggy. [Trello 2173](https://trello.com/c/ccXQpdKy)
### Fixed
- DOM capture now parses CSS correctly. [Trello 2173](https://trello.com/c/ccXQpdKy)

## [3.179.0] - 2020-10-01
### Added
- New API for setting `deviceInfo`, `hostingAppInfo` and `osInfo` in the configuration. [Trello 2140](https://trello.com/c/vGSi2NFz)
### Updated
- Use touch action to reach top left corner in Appium iOS. [Trello 2083](https://trello.com/c/bz4C8PMw)
### Fixed
- Calculation viewport size for Android devices. [Trello 2132](https://trello.com/c/CDfbKUV6)
- Agent ID contains the real version of the SDK. [Trello 2165](https://trello.com/c/mYiH2zvw)
- Platform version wasn't retrieved properly in browserstack. [Trello 2181](https://trello.com/c/OlOrmyJ0)

## [3.178.0] - 2020-09-10
### Updated
- The `USE_PREDEFINED_DEVICE_INFO` feature now compares device name with all aliases returned from the server. [Trello 301](https://trello.com/c/vGSi2NFz)
### Fixed
- When we take a viewport screenshot, and the client size of the html element is 0 the sdk won't fail anymore. [Trello 2130](https://trello.com/c/U6fhdMOO)

## [3.177.1] - 2020-09-10
### Fixed
- Setting viewport 0x0 now won't do anything. [Trello 2115](https://trello.com/c/KYKyXSr6)
- Now parsing cached resources when collecting resources before rendering. [Trello 2135](https://trello.com/c/hwkbp14g)
- Checking non scrollable elements after scrolling the page works now. [Trello 2100](https://trello.com/c/7TDFAWUn)

## [3.177.0] - 2020-09-07 
### Added
- Use predefined device info for pixel ratio. [Trello 301](https://trello.com/c/vGSi2NFz)
### Fixed
- Supporting `getBoundingClientRect()` for web elements in IE Browser. [Trello 2130](https://trello.com/c/U6fhdMOO)
- Supporting pages without a `body` element. [Trello 2130](https://trello.com/c/U6fhdMOO)

## [3.176.0] - 2020-09-02
### Added
- Implemented test on getting status bar height. [Trello 1478](https://trello.com/c/RuPL3v4v)
- Implemented test on setting deviceName to appEnvironment data. [Trello 125](https://trello.com/c/ekZqajRU)
- Supporting `VisualGridOptions` in configuration and in fluent API. [Trello 2089](https://trello.com/c/d4zggQes)
- Added a new feature for getting mobile device pixel ratios from the server. [Trello 301](https://trello.com/c/vGSi2NFz)
- If setting proxy without url, the url will be take from the env var `APPLITOOLS_HTTP_PROXY`. [Trello 1070](https://trello.com/c/9DKb46YO)
- Added set/get ignoreDisplacements api to eyes. [Trello 688](https://trello.com/c/zdscWBlG)
### Updated
- Checking for browserStack specific caps for appEnvironment data. [Trello 2017](https://trello.com/c/3q1wrnYG)
- Scroll mechanism for Android. Added possibility to scroll with helper library. [Trello 1673](https://trello.com/c/CYbkzXia)
- Connection with the server is now asynchronous. [Trello 2094](https://trello.com/c/O74dUDxG)
- Calling `eyes.getViewportSize` returns `null` instead of throwing an exception. [Trello 1449](https://trello.com/c/twHGaW1X)
- Updated DOM Snapshot script to 4.0.6. [Trello 2133](https://trello.com/c/8DEaODgE)
### Fixed
- When Y coordinate is smaller than 0 it will be set as 0 to avoid IllegalArgumentException. [Trello 2121](https://trello.com/c/3atHV3Ee)

## [3.175.0] - 2020-08-26
### Fixed
- UFG tests now finish properly even when being closed after `eyes.check` has finished. [Trello 2000](https://trello.com/c/0EWqP5to)
- Now using cut provider and scale provider added by the user in the visual locators. [Trello 1955](https://trello.com/c/rhfcRXLV)
- The viewport size in eyes-images is now the size of the checked image. [Trello 1783](https://trello.com/c/CHwLLCl1)
- `eyes.getHostApp()` returned invalid value. [Trello 2108](https://trello.com/c/1hMHwHZf)
### Updated
- DOM snapshot script to 4.0.5. [Trello 2006](https://trello.com/c/a6l6gTf9)
- Extracted connectivity and ufg dom analyzing to remote repositories. [Trello 2074](https://trello.com/c/pP9VbmKF)
### Added
- When render fails, the correct useragent will be sent in the test results. [Trello 2086](https://trello.com/c/RLOmjJLT)

## [3.174.0] - 2020-08-12
### Fixed
- Agent ID was null in some cases. [Trello 2060](https://trello.com/c/zELwZYma)
- Connectivity modules now get the correct log handler. [Trello 1803](https://trello.com/c/1lavL4Mg)
### Updated
- Moved `AccessibilityStatus` enum to its own file. [Trello 2040](https://trello.com/c/ujY0T84R)
- Refactored algorithm. [Trello 2072](https://trello.com/c/0r3gS3Ew/)
### Added
- Supporting feature flags in the configuration. [Trello 2032](https://trello.com/c/tOKrAbIk)
- New feature flag NO_SWITCH_WITHOUT_FRAME_CHAIN. [Trello 2032](https://trello.com/c/tOKrAbIk)

## [3.173.0] - 2020-08-05
### Fixed
- Try to send correct iOS device size when rendering fails. [Trello 2006](https://trello.com/c/a6l6gTf9)
- EyesWebDriver namespace fix [Trello 1980](https://trello.com/c/RYAOPRpc)
### Updated
- Updated DOM Snapshot script to 4.0.1. [Trello 2049](https://trello.com/c/8GP2pfLr)
### Added
- Added some missing logs for investigating clients' issues. [Trello 2000](https://trello.com/c/0EWqP5to)

## [3.172.1] - 2020-08-04
### Added
- Appium module from java4 sdk. [Trello 1980](https://trello.com/c/RYAOPRpc)
- Additional logging in `getViewportRect`. [Trello 644](https://trello.com/c/FnOtYN6J)
### Fixed
- Artifact names (internal)

## [3.172.0] - 2020-08-03
### Added
- Appium module from java4 sdk. [Trello 1980](https://trello.com/c/RYAOPRpc)
- Additional logging in `getViewportRect`. [Trello 644](https://trello.com/c/FnOtYN6J)

## [3.171.1] - 2020-07-27
### Fixed
- Check region inside a frame with scroll stitching is now working. [Trello 2033](https://trello.com/c/ndMDaRQB)
- Resource caching now work properly. [Trello 1989](https://trello.com/c/uyVUx6kL)

## [3.171.0] - 2020-07-22
### Fixed
- Calling `switchTo` with `EyesWebDriver` now works properly. [Trello 1818](https://trello.com/c/488BZ24S)
- "Check many" for limited  screenshot size. [Trello 1991](https://trello.com/c/2iCNfoI7)
### Updated
- Screenshot retry mechanism is now more efficient. [Trello 1866](https://trello.com/c/KyxkI6Bu)
- Supporting visual viewports in UFG. [Trello 1957](https://trello.com/c/jWvdBwex)
- Failed downloading of resources won't fail the UFG tests anymore. [Trello 1989](https://trello.com/c/uyVUx6kL)

## [3.170.0] - 2020-07-13
### Fixed
- Fixed a bug when new tests weren't defined as new in old versions of the server. [Trello 1993](https://trello.com/c/JSnJauTu)
### Updated
- Update all `GetRegion` classes, so they don't depend on `Eyes` or `EyesBase` objects. [Trello 1980](https://trello.com/c/RYAOPRpc)
- Updated the render request to match the protocol (internal). [Trello 1988](https://trello.com/c/Yr6EsUlL)
- Limited screenshot size. [Trello 1991](https://trello.com/c/2iCNfoI7)

## [3.169.0] - 2020-07-05
### Fixed
- Correct size calculation for elements that change when hiding scrollbars. [Trello 1881](https://trello.com/c/9pVjoVwC) 
### Updated
- Updated DOM Snapshot to 3.6.0 and supported scripts for internet explorer. [Trello 1962](https://trello.com/c/MlHqSdXv)
- The default scroll root element is now the bigger one between "body" and "html" instead of only "html". [Trello 1972](https://trello.com/c/YfJRReVo)

## [3.168.1] - 2020-06-28
### Fixed
- `eyes.getConfiguration` now returns `com.applitools.eyes.selenium.Configuration`. [Trello 1950](https://trello.com/c/7vPwXLqG)
### Updated
- Updated DOM Snapshot to 3.5.4. [Trello 1961](https://trello.com/c/iDf2x25p)

## [3.168.0] - 2020-06-25
### Fixed
- Calling `eyes.check` with `fully(false)` now doesn't take full page screenshot even if `forceFullPageScreenshot` is set to true in the configuration. [Trello 1926](https://trello.com/c/4vcerUTm)
- `saveDebugScreenshot` now works when there is no full page screenshot. [Trello 1138](https://trello.com/c/DUjJxuMH)
- Removed unused `Target` class in the java.sdk.core. [Trello 1098](https://trello.com/c/Oi36yIro)
- Added missing constructors to `com.applitools.eyes.selenium.Configuration`. [Trello 1954](https://trello.com/c/al9nZGPD)
### Updated
- Updated the API of the IOS Simulation. [Trello 1944](https://trello.com/c/EzyG7525)

## [3.167.0] - 2020-06-23
### Fixed
- Users can now use fluent API to update configuration in any order. [Trello 1689](https://trello.com/c/UDYmDZnw)
- `eyes.setApiKey` now Updates configuration properly. [Trello 1918](https://trello.com/c/J3FVMkCK)
### Updated
- Configuration structure is refactored to be less complicated for developers. [Trello 1888](https://trello.com/c/cSqePDVh)
- Moved the content of eyes.common.java to eyes.sdk.core. [Trello 1889](https://trello.com/c/2j9Owbw3)

## [3.166.0] - 2020-06-22
### Fixed
- Tests now report their status properly. [Trello 1902](https://trello.com/c/Y8SZwm6m)
- An endless loop on failed renders. [Trello 1907](https://trello.com/c/n80nncwf)
### Updated
- DOM Capture and Snapshot scripts [Trello 1865](https://trello.com/c/haTeCXzq)
- Updated browser info api as required. [Trello 1872](https://trello.com/c/bykk2rzB)
### Added
- Supporting tests page factory object. [Trello 1503](https://trello.com/c/pjmn2N7H)

## [3.165.0] - 2020-06-09
### Added
- Supported rendering on ios simulators. [Trello 1872](https://trello.com/c/bykk2rzB)
- Added support for Visual Locators. [Trello 1788](https://trello.com/c/dEeEDiIY)

## [3.164.1] - 2020-06-03
### Fixed
- Fixed the reporting of the TestResultsSummary. [Trello 1860](https://trello.com/c/X9xtbgXr)
- When render fails twice the test fails instead of running endlessly. Fixed downloaded resource caching. [Trello 1850](https://trello.com/c/R6MYtCX6)
- Updated the logic of putting resources to fix buggy behaviour. [Trello 1858](https://trello.com/c/rExcJAQy)
- Fixed resource management problems that caused an out-of-memory error. [Trello 1805](https://trello.com/c/PmxMgn4W)

## [3.164.0] - 2020-05-24
### Added
- Edge Chromium support. [Trello 1757](https://trello.com/c/LUe43aee)
### Fixed
- Updated the logic of collecting resources to fix buggy behaviour. [Trello 1822](https://trello.com/c/NE50kV8P)
- Added the `Referer` header to every request for downloading resources. [Trello 1801](https://trello.com/c/oexcxdyL)

## [3.163.0] - 2020-05-18
### Fixed
- Updating configuration now updates the default match settings. [Trello 1810](https://trello.com/c/dAdD9AkN)
### Updated
- Accessibility guidelines version support [Trello 1767](https://trello.com/c/gq69woeK)

## [3.162.0] - 2020-05-13
### Fixed
- Fixed a bug where the Jeresy1 and Jboss connectivity modules didn't work with visual grid runner when running multiple tests simultaneously.
- Fixed a bug where calling abortAsync when running tests with Visual Grid would cancel all previous `check` calls. [Trello 1762](https://trello.com/c/UrYlQavt)
### Updated
- Moved the logic from the connectivity modules and merged it into the common module to save a lot of code duplication. [Trello 1732](https://trello.com/c/mug8ARUc)
### Added
- Disabled SSL verification. Accept all certificates. [Trello 1777](https://trello.com/c/ZNSJZ1cf)
- Added a script for running basic eyes tests for connectivity packages Jersey1 and Jsboss. [Trello 1782](https://trello.com/c/TA7v4Y4t)

## [3.161.0] - 2020-05-05
### Fixed
- Default versions reported in `AgentId` now automatically generated and match `pom.xml`.
- Method `setEnablePatterns` now works properly. [Trello 1714](https://trello.com/c/jQgW5dpz)
- Fixed steps missing in certain cases in UltraFast Grid. [Trello 1717](https://trello.com/c/U1TTels2)
- Now all requests include the Eyes-Date header when sending a long request
### Updated
- The `startSession` method now uses long request. [Trello 1715](https://trello.com/c/DcVzWbeR)
- Adding agent id to all requests headers. [Trello 1697](https://trello.com/c/CzhUxOqE)

## [3.160.3] - 2020-03-18
### Fixed
- Fixed UserAgent parsing + tests. (Problem found in [Trello 1589](https://trello.com/c/3C2UTw5P))
- Fixed Viewport metatag parsing. [Trello 1629](https://trello.com/c/a0AgWIWj)
### Updated
- DOM Snapshot script to version 3.3.3. [Trello 1588](https://trello.com/c/ZS0Wb1FN)
- Upload DOM directly to storage service on MatchWindow. [Trello 1592](https://trello.com/c/MXixwLnj)

## [3.160.2] - 2020-02-24
### Fixed
- Fixed UFG coded regions placements. [Trello 1542](https://trello.com/c/2Awzru1V)
- Check at least one render request exists before extracting id. [Trello 1489](https://trello.com/c/KSfk2LhY)

## [3.160.1] - 2020-02-23
### Fixed
- Update sizes and SizeAdjuster on every check due to possible URL change. [Trello 1353](https://trello.com/c/rhTs54Kb)
- Images configuration set/get. [Trello 1560](https://trello.com/c/hSTcBcvJ)
- Query param is null in Jersey1x for UFG [Trello 1490](https://trello.com/c/p0TypdOe)

## [3.160.0] - 2020-01-30
### Fixed
- Misaligned coded regions. [Trello 1504](https://trello.com/c/ob3kzcDR)
- Fixed long-request implementation in all 3 connectivity packages. [Trello 1516](https://trello.com/c/GThsXbIL)
### Updated
- All Eyes related commands now enable long-request. [Trello 1516](https://trello.com/c/GThsXbIL)

## [3.159.0] - 2020-01-21
### Updated
- Capture scripts. [Trello 151](https://trello.com/c/owyVIQG9)
- Upload images directly to storage service on MatchWindow. [Trello 1461](https://trello.com/c/1V5X9O37)
- Visual Grid: Added older versions support for Chrome, Firefox and Safari browsers. [Trello 1479](https://trello.com/c/kwsR1zql)

### Fixed
- Added missing method: abortAsync. [Trello 1420](https://trello.com/c/3NOKuLLj)
- Fixed viewport computation on edge cases.
- Some special characters are not rendering on Visual Grid. [Trello 1443](https://trello.com/c/GWzVCY7W)
- Fixed default match settings being incorrectly overriden by image match settings. [Trello 1495](https://trello.com/c/KEbWXavV)

## [3.158.9] - 2019-12-12
### Fixed
- Fixed ensuring eyes open before check. [Trello 722](https://trello.com/c/JgXaAhPo)
- Fixed creation of new Rest client when closing batches. [Trello 1327](https://trello.com/c/Jdoj8AQ9) 
- Disabled ImageDeltaCompressor. [Trello 1361](https://trello.com/c/AZiEB8bP) 
- Added new stitchingServiceUrl field to RenderingInfo and RenderRequest [Trello 1368](https://trello.com/c/RkBRBJCu) 
- Unrecognized fields in server response JSON are ignored. [Trello 1375](https://trello.com/c/RqYEUoIq) 

## [3.158.8] - 2019-11-22
### Fixed
- Calling updated methods in DOM Snapshot. [Trello 375](https://trello.com/c/elkaV9Dm)

## [3.158.7] - 2019-11-22
### Fixed
- Updated DOM snapshot script. [Trello 375](https://trello.com/c/elkaV9Dm)

## [3.158.6] - 2019-11-13
### Fixed
- Fixed connection pool hanging with DomCapture. [Trello 1144](https://trello.com/c/Aex0NkjK) 

## [3.158.5] - 2019-11-08
### Fixed
- CSS scrolling in chrome 78. [Trello 1206](https://trello.com/c/euVqe1Sv) 

## [3.158.4] - 2019-10-23
### Fixed
- Jersey1x proxy.

## [3.158.3] - 2019-10-19
### Fixed
- DOM-Snapshot correct close of resource download response.

## [3.158.2] - 2019-10-10
### Fixed
- Updated accessibility enums (experimental).

## [3.158.1] - 2019-10-10
### Added 
- Batch close & notification support for JBoss and Jersey1x.
### Fixed
- Batch close now checks for bamboo_ prefix for env var.

## [3.158.0] - 2019-10-10
### Added 
- Accessibility support [experimental].
- Batch close & notification support.

## [3.157.12] - 2019-07-27
### Added 
- This CHANGELOG file.
### Fixed
- Bugfix: Creating screenshots correctly when IEDriver provides a full page without being requested to do so.
