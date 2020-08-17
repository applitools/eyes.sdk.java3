## [vNext]
### Updated
- DOM snapshot script to 4.0.5. [Trello 2006](https://trello.com/c/a6l6gTf9)
- Extracted connectivity and ufg dom analyzing to remote repositories. [Trello 2074](https://trello.com/c/pP9VbmKF)
### Added
- When render fails, the correct useragent will be sent in the test results. [Trello 2086](https://trello.com/c/RLOmjJLT)
- When render fails, the correct device size will be sent in the test results. [Trello 2087](https://trello.com/c/AQ0upINc)

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
