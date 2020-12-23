'use strict'
const {checkSettingsParser, java, getter, variable, call, returnSyntax, wrapSelector} = require('./parser')
const {capitalizeFirstLetter} = require('./util')
const imageMatchSettings = {
  type: 'ImageMatchSettings',
  schema: {
    ignoreDisplacements: 'BooleanObject',
    ignore: {type: 'Array', items: 'Region'},
    floating: {type: 'Array', items: 'FloatingRegion'},
    accessibility: {type: 'Array', items: 'AccessibilityRegion'},
    accessibilitySettings: {
      type: 'AccessibilitySettings',
      schema: {level: 'AccessibilityLevel', version: 'AccessibilityGuidelinesVersion'},
    },
    layout: {type: 'Array', items: 'Region'}
  },
}
module.exports = function (tracker, test) {
  const {addSyntax, addCommand, addHook} = tracker
  function argumentCheck(actual, ifUndefined){
     return (typeof actual === 'undefined') ? ifUndefined : actual
  }

  function addType(obj, type, generic) {
    return type ? {value: obj, type:type, generic: generic} : obj
  }
  function getTypeName(obj) {
    return obj.type().name
  }
  function emptyValue() {
    return {
      isRef: true,
      ref: () => ''
    }
  }

  function insert(value) {
    return {
      isRef: true,
      ref: () => value
    }
  }

  function findFrame(frame){
    if(!Array.isArray(frame)) throw new Error(`Frame property for check wasn't a Array type`)
    frame = frame[0]
    if(typeof frame === 'string' && !(/[#\[\]]/.test(frame))) {
      return frame
    } else {
      return findElement(frame)
    }
  }
  function findElement(element) {
    if(element.isRef && element.type().name === 'Element') return element
    else return driver.findElement(element)
  }

  function extraParameter(param, addToEnd=true){
    return (typeof param === 'undefined') ? emptyValue() : (addToEnd) ? `, ${param}` : `${param}, `
  }

  function extraParameters(params) {
    let result = ''
    for (const param of params) {
      if (param === undefined) break
      result += java`, ${param}`
    }
    return insert(result)
  }

  addHook('deps', `package coverage.generic;`)
  addHook('deps', ``)
  // Selenium
  if( test.meta.native ) {
    addHook('deps', `import coverage.NativeSetup;`)
    addHook('deps', `import com.applitools.eyes.appium.*;`)
    addHook('deps', `import com.applitools.eyes.appium.Target;`)
    addHook('deps', `import io.appium.java_client.MobileBy;`)
  } else {
    addHook('deps', `import coverage.TestSetup;`)
    addHook('deps', `import com.applitools.eyes.selenium.*;`)
    addHook('deps', `import com.applitools.eyes.selenium.fluent.Target;`)
  }
  // Not specific
  addHook('deps', `import com.applitools.eyes.*;`)
  addHook('deps', `import com.applitools.eyes.metadata.SessionResults;`)
  addHook('deps', `import org.openqa.selenium.*;`)
  addHook('deps', `import org.testng.annotations.*;`)
  addHook('deps', `import org.testng.Assert;`)
  addHook('deps', `import java.util.*;`)
  addHook('deps', `import com.applitools.eyes.locators.VisualLocatorSettings;`)
  addHook('deps', 'import com.fasterxml.jackson.databind.JsonNode;');

  addSyntax('var', variable)
  addSyntax('getter', getter)
  addSyntax('call', call)
  addSyntax('return', returnSyntax)

  addHook('beforeEach', java`initEyes(${argumentCheck(test.vg, false)}, ${argumentCheck(test.config.stitchMode, 'Scroll')}, ${argumentCheck(test.branchName, "master")});`,)
  addHook('beforeEach', java`buildDriver(${JSON.stringify(test.env) || emptyValue()});`)
  addHook('beforeEach', java`System.out.println(getClass().getName());`)
  const specific = ['baselineName', 'browsersInfo', 'appName', 'defaultMatchSettings', 'layoutBreakpoints'];
  Object.keys(test.config).filter(property => !specific.includes(property))
      .forEach(property => addHook('beforeEach', java`set${insert(capitalizeFirstLetter(property))}(${test.config[property]});`))
  if(test.config.browsersInfo) {
    addHook('deps', 'import com.applitools.eyes.visualgrid.model.*;')
    addHook('deps', 'import com.applitools.eyes.visualgrid.model.ScreenOrientation;')
    addHook('beforeEach', java`setBrowsersInfo(${{value:test.config.browsersInfo, type: 'BrowsersInfo'}});`)
  }
  if(test.config.defaultMatchSettings) {
    const defaultMatchSettings = test.config.defaultMatchSettings
    Object.keys(defaultMatchSettings)
        .forEach(property => addHook('beforeEach',
            java`set${insert(capitalizeFirstLetter(property))}(${{value: defaultMatchSettings[property], ...imageMatchSettings.schema[property]}});` ))
  }
  if (test.config.layoutBreakpoints) {
    addHook('beforeEach', `setLayoutBreakpoints(${test.config.layoutBreakpoints});`)
  }

  addHook('afterEach', java`driver.quit();`)
  addHook('afterEach', java`eyes.abort();`)

  const driver = {
    constructor: {
      isStaleElementError: () => 'StaleElementReferenceException.class'
    },
    visit(url) {
      addCommand(java`getDriver().get(${url});`)
    },
    executeScript(script, ...args) {
      return addCommand(java`((JavascriptExecutor) getDriver()).executeScript(${script}${extraParameters(args)});`)
    },
    switchToFrame(selector) {
      addCommand(java`getDriver().switchTo().frame(${selector});`)
    },
    switchToParentFrame() {
      addCommand(java`getDriver().switchTo().parentFrame();`)
    },
    findElement(selector) {
      return addCommand(java`getDriver().findElement(${wrapSelector(selector)});`).type('Element')
    },
    findElements(selector) {
      return addCommand(java`getDriver().findElements(${wrapSelector(selector)}));`).type('Array<Element>')
    },
    click(element) {
      if(element.isRef) addCommand(java`${element}.click();`)
      else addCommand(java`getDriver().findElement(${wrapSelector(element)}).click();`)
    },
    type(element, keys) {
      addCommand(java`${element}.sendKeys(${keys});`)
    },
    scrollIntoView(element, align=false) {
      addCommand(java`((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(arguments[1])", ${findElement(element)}, ${align});`)
    },
    hover(element, offset) {
      addCommand(java`hover(${findElement(element)});`)
    }
  }

  const eyes = {
    constructor: {
      setViewportSize(viewportSize) {
        return addCommand(java`Eyes.setViewportSize(getDriver(), ${addType(viewportSize, 'RectangleSize')});`)
      }
    },
    runner: {
      getAllTestResults(throwEx) {
        return addCommand(java`getRunner().getAllTestResults(${throwEx});`)
      },
    },
    open({appName, testName, viewportSize}) {
      let command = []
      command.push('open(driver')
      command.push(java`, ${appName || test.config.appName}`)
      command.push(java`, ${testName || test.config.baselineName}`)
      if(viewportSize) command.push(java`, new RectangleSize(${viewportSize.width}, ${viewportSize.height})`)
      command.push(');')
      addCommand(command.join(''))
    },
    check(checkSettings) {
      if(test.api === 'classic') {
          if (checkSettings === undefined || (checkSettings.frames === undefined && checkSettings.region === undefined)) {
            eyes.checkWindow()
          } else if (checkSettings.frames && checkSettings.region) {
            eyes.checkRegionInFrame(checkSettings.frames, checkSettings.region, checkSettings.matchTimeout, checkSettings.tag, checkSettings.isFully)
          } else if (checkSettings.frames) {
            eyes.checkFrame(checkSettings.frames, checkSettings.matchTimeout, checkSettings.tag)
          } else if (checkSettings.region) {
            eyes.checkRegion(checkSettings.region, checkSettings.matchTimeout, checkSettings.tag)
          } else {
            throw new Error('Not implemented classic api method was tried to generate')
          }
      } else {
        addCommand(`eyes.check(${checkSettingsParser(checkSettings, test.meta.native)});`)
      }
    },
    checkWindow(tag, matchTimeout, stitchContent) {
      if(matchTimeout && stitchContent) throw new Error(`There is no signature in java SDK for usage both matchTimeout and stitchContent`)
      addCommand(java`eyes.checkWindow(${extraParameter(matchTimeout, false)}${argumentCheck(tag, '')}${extraParameter(stitchContent)});`)
    },
    checkFrame(element, matchTimeout, tag) {
      const commands = []
      commands.push(java`eyes.checkFrame(`)
      commands.push(java`${findFrame(element)}`)
      if(matchTimeout) commands.push(java`, ${matchTimeout}`)
      if(tag) commands.push(java`, ${tag}`)
      commands.push(java`);`)
      addCommand([commands.join('')])
    },
    checkRegion(region, matchTimeout, tag) {
      const commands = []
      commands.push(java`eyes.checkRegion(`)
      commands.push(java`${wrapSelector(region)}`)
      if(matchTimeout) commands.push(java`, ${matchTimeout}`)
      if(tag) commands.push(java`, ${tag}`)
      commands.push(java`);`)
      addCommand([commands.join('')])
    },
    checkRegionInFrame(frameReference, selector, matchTimeout, tag, stitchContent) {
      const commands = []
      commands.push(java`eyes.checkRegionInFrame(`)
      commands.push(java`${findFrame(frameReference)},`)
      commands.push(java` ${wrapSelector(selector)}`)
      if(matchTimeout) commands.push(java`, ${matchTimeout}`)
      if(tag) commands.push(java`, ${tag}`)
      if(stitchContent) commands.push(java`, ${stitchContent}`)
      commands.push(java`);`)
      addCommand([commands.join('')])
    },
    close(throwEx) {
      return addCommand(java`eyes.close(${argumentCheck(throwEx, true)});`).type({type:'TestResults'})
    },
    abort() {
      return addCommand(java`eyes.abort();`)
    },
    getViewportSize() {
      return addCommand(java`eyes.getViewportSize();`).type({
        type: 'RectangleSize',
        schema: {
          height: {
            type: 'int'
          },
          width: {
            type: 'int'
          }
        }
      })
    },
    locate(visualLocator) {
      return addCommand(java`eyes.locate(new VisualLocatorSettings().names(Arrays.asList(${visualLocator.locatorNames.join(', ')})));`)
    }
  }

  const assert = {
    equal(actual, expected, message) {
      if(expected.isRef) {
        const typeCasting = actual.type().name === 'Number' ? insert(` (long) `) : emptyValue()
        addCommand(java`Assert.assertEquals(${typeCasting}${actual}, ${expected}${extraParameter(message)});`)
      } else {
        const type = getTypeName(actual)
        if (type === 'JsonNode') {
          addCommand(java`Assert.assertEquals(${actual}.asText(""), ${expected}${extraParameter(message)});`)
        } else if (type !== 'Map') {
          addCommand(java`Assert.assertEquals(${actual}, ${addType(expected, type)}${extraParameter(message)});`)
        } else {
          addCommand(java`Assert.assertEqualsDeep(${actual}, ${addType(expected, type, actual.type().generic)}${extraParameter(message)});`)
        }
      }
    },
    notEqual(actual, expected, message){
      addCommand(java`Assert.assertNotEquals(${actual}, ${expected}${extraParameter(message)});`)
    },
    instanceOf(){
      // As for java there is no need to check class of the returned value, it class present in the method declaration
      throw Error('instanceOf not implemented in Java')
    },
    throws(func, check){
      let command
      if(check){
        command = java`Assert.assertThrows(${insert(check())} , new Assert.ThrowingRunnable(){
          public void run() {${func}}
        });`
      } else {
        command = java`Assert.assertThrows(new Assert.ThrowingRunnable(){ 
        public void run() {${func}}
        });`
      }
      addCommand(command)
    }
  }

  const helpers = {
    getTestInfo(result) {
      return addCommand(java`getTestInfo(${result});`).type({
        type: 'TestInfo',
        schema: {
          actualAppOutput: {
            type: 'Array',
            items: {
              type: 'AppOutput',
              schema: {
                image: {
                  type: 'Image',
                  schema: {hasDom: 'Boolean', location: "Location"},
                },
                imageMatchSettings: imageMatchSettings,
              }},
          },
        },
      })
    },
    getDom(result, domId) {
      return addCommand(java`getDom(${result},${domId});`).type({type: 'JsonNode', recursive: true}).methods({
        getNodesByAttribute: (dom, attr) => addCommand(java`getNodesByAttributes(${dom}, ${attr});`).type({
          type: 'List<JsonNode>',
          schema: {length: {rename: 'size'}},
          items: {type: 'JsonNode', recursive: true}
        })
      })
    }
  }

  return {driver, eyes, assert, helpers}
}
