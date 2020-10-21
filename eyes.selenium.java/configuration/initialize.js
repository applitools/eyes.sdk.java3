'use strict'
const {makeEmitTracker} = require('@applitools/sdk-coverage-tests')
const {checkSettingsParser, java, getTypes} = require('./parser')

function initialize(options) {
  const tracker = makeEmitTracker()

  function argumentCheck(actual, ifUndefined){
     return (typeof actual === 'undefined') ? ifUndefined : actual
  }

  function emptyValue() {
    return {
      isRef: true,
      ref: () => ''
    }
  }

  function findFrame(frame){
    return frame.isRef ? frame : {
      isRef: true,
      ref: () => `driver.findElement(By.cssSelector(${JSON.stringify(frame)}))`
    }
  }

  function extraParameter(param){
    return (typeof param === 'undefined') ? emptyValue() : `, ${param}`
  }

  tracker.storeHook('deps', `package coverage.generic;`)
  tracker.storeHook('deps', ``)
  tracker.storeHook('deps', `import coverage.TestSetup;`)
  tracker.storeHook('deps', `import com.applitools.eyes.*;`)
  tracker.storeHook('deps', `import com.applitools.eyes.selenium.fluent.Target;`)
  tracker.storeHook('deps', `import org.openqa.selenium.*;`)
  tracker.storeHook('deps', `import org.testng.annotations.*;`)
  tracker.storeHook('deps', `import org.testng.Assert;`)
  tracker.storeHook('deps', `import java.util.Map;`)

  tracker.addSyntax('var', ({name, value, type='WebElement'}) => `${type} ${name} = (${type}) ${value}`)
  tracker.addSyntax('getter', getTypes)
  tracker.addSyntax('call', ({target, args}) => args.length > 0 ? `${target}(${args.map(val => JSON.stringify(val)).join(", ")})` : `${target}()`)

  tracker.storeHook(
      'beforeEach',
      java`initEyes(${argumentCheck(options.executionMode.isVisualGrid, false)}, ${argumentCheck(options.executionMode.isCssStitching, false)}, ${argumentCheck(options.branchName, "master")});`,
  )

  tracker.storeHook('beforeEach', java`buildDriver();`)
  tracker.storeHook('beforeEach', java`System.out.println(getClass().getName());`)

  tracker.storeHook('afterEach', java`driver.quit();`)
  tracker.storeHook('afterEach', java`eyes.abort();`)

  const driver = {
    build(options) {
      // TODO need implementation
      console.log('build Need to be implemented')
    },
    cleanup() {
      tracker.storeCommand(java`driver.quit();`)
    },
    visit(url) {
      tracker.storeCommand(java`driver.get(${url});`)
    },
    executeScript(script, ...args) {
      return tracker.storeCommand(java`driver.executeScript(${script});`)
    },
    sleep(ms) {
      // TODO need implementation
      console.log('sleep Need to be implemented')
    },
    switchToFrame(selector) {
      tracker.storeCommand(java`driver.switchTo().frame(${selector});`)
    },
    switchToParentFrame() {
      // TODO need implementation
      console.log('switchToParentFrame Need to be implemented')
    },
    findElement(selector) {
      return tracker.storeCommand(
          java`driver.findElement(By.cssSelector(${selector}));`,
      )
    },
    findElements(selector) {
      return tracker.storeCommand(
          java`driver.findElements(By.cssSelector(${selector}));`,
      )
    },
    getWindowLocation() {
      // TODO need implementation
      console.log('getWindowLocation Need to be implemented')
    },
    setWindowLocation(location) {
      // TODO need implementation
      console.log('setWindowLocation Need to be implemented')
    },
    getWindowSize() {
      // TODO need implementation
      console.log('getWindowSize Need to be implemented')
    },
    setWindowSize(size) {
      // TODO need implementation
      console.log('setWindowSize Need to be implemented')
    },
    click(element) {
      if(element.isRef) tracker.storeCommand(java`${element}.click();`)
      else tracker.storeCommand(java`driver.findElement(By.cssSelector(${element})).click();`)
    },
    type(element, keys) {
      tracker.storeCommand(java`${element}.sendKeys(${keys});`)
    },
    waitUntilDisplayed() {
      // TODO: implement if needed
    },
    getElementRect() {
      // TODO: implement if needed
    },
    getOrientation() {
      // TODO: implement if needed
    },
    isMobile() {
      // TODO: implement if needed
    },
    isAndroid() {
      // TODO: implement if needed
    },
    isIOS() {
      // TODO: implement if needed
    },
    isNative() {
      // TODO: implement if needed
    },
    getPlatformVersion() {
      // TODO: implement if needed
    },
    getBrowserName() {
      // TODO: implement if needed
    },
    getBrowserVersion() {
      // TODO: implement if needed
    },
    getSessionId() {
      // TODO: implement if needed
    },
    takeScreenshot() {
      // TODO: implement if needed
    },
    getTitle() {
      // TODO: implement if needed
    },
    getUrl() {
      // TODO: implement if needed
    },
  }

  const eyes = {
    open({appName, viewportSize}) {
      tracker.storeCommand(java`eyes.open(driver, ${appName}, ${options.baselineTestName}, new RectangleSize(${viewportSize.width}, ${viewportSize.height}));`)
    },
    check(checkSettings) {
      tracker.storeCommand(`eyes.check(${checkSettingsParser(checkSettings)});`)
    },
    checkWindow(tag, matchTimeout, stitchContent) {
      if(matchTimeout || stitchContent) throw new Error(`There is no signature in java SDK for usage both matchTimeout and stitchContent`)
      tracker.storeCommand(java`eyes.checkWindow(${argumentCheck(tag, '')});`)
    },
    checkFrame(element, matchTimeout, tag) {
      tracker.storeCommand(java`eyes.checkFrame(${findFrame(element)}${extraParameter(matchTimeout)}${extraParameter(tag)});`)
    },
    checkElement(element, matchTimeout, tag) {
      tracker.storeCommand(java`eyes.checkRegion(${element}${extraParameter(matchTimeout)}${extraParameter(tag)});`)
    },
    checkElementBy(selector, matchTimeout, tag) {
      tracker.storeCommand(java`eyes.checkRegion(By.cssSelector(${selector})${extraParameter(matchTimeout)}${extraParameter(tag)});`)
    },
    checkRegion(region, matchTimeout, tag) {
      tracker.storeCommand(java`eyes.checkRegion(${region},${matchTimeout}, ${tag});`)
    },
    checkRegionByElement(element, matchTimeout, tag) {
      // TODO need implementation
      console.log('checkRegionByElement Need to be implemented')
    },
    checkRegionBy(selector, tag, matchTimeout, stitchContent) {
      // TODO need implementation
      console.log('checkRegionBy Need to be implemented')
    },
    checkRegionInFrame(frameReference, selector, matchTimeout, tag, stitchContent) {
      if(matchTimeout === null) matchTimeout = -1
      tracker.storeCommand(java`eyes.checkRegionInFrame(${findFrame(frameReference)}, By.cssSelector(${selector}), ${matchTimeout}, ${tag}, ${stitchContent});`)
    },
    close(throwEx) {
      tracker.storeCommand(java`eyes.close(${argumentCheck(throwEx, true)});`)
    },
    abort() {
      tracker.storeCommand(java`eyes.abort();`)
    },
    getViewportSize() {
      return tracker.storeCommand(java`eyes.getViewportSize();`)
    },
  }

  const assert = {
    strictEqual(actual, expected, message){
      tracker.storeCommand(java`Assert.assertEquals(${actual}, ${expected}${extraParameter(message)});`)
    }
  }

  return {tracker, driver, eyes, assert}
}

module.exports = {initialize}