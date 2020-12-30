'use strict'
const types = require('./mapping/types')
const selectors = require('./mapping/selectors')
const  {capitalizeFirstLetter} = require('./util')
function checkSettings(cs, native) {
    let java = `Target`
    if (cs === undefined) {
        return java + '.window()'
    }
    let element = ''
    let options = ''
    if (cs.frames === undefined && cs.region === undefined) element = '.window()'
    else {
        if (cs.frames) element += frames(cs.frames)
        if (cs.region) element += region(cs.region)
    }
    if (cs.ignoreRegions) options += ignoreRegions(cs.ignoreRegions)
    if (cs.floatingRegions) options += floatingRegions(cs.floatingRegions)
    if (cs.accessibilityRegions) options += accessibilityRegions(cs.accessibilityRegions)
    if (cs.layoutRegions) options += layoutRegions(cs.layoutRegions)
    if (cs.scrollRootElement && !cs.frames) options += `.scrollRootElement(${printSelector(cs.scrollRootElement)})`
    if (cs.ignoreDisplacements !== undefined) options += `.ignoreDisplacements(${cs.ignoreDisplacements})`
    if (cs.sendDom !== undefined) options += `.sendDom(${serialize(cs.sendDom)})`
    if (cs.matchLevel) options += `.matchLevel(MatchLevel.${cs.matchLevel.toUpperCase()})`
    if (cs.name) options += `.withName(${cs.name})`
    if (cs.isFully === true) {
        options += '.fully()'
    } else if (cs.isFully === false) {
        options += '.fully(false)'
    }
    return java + element + options

    // check settings

    function frames(arr) {
        return arr.reduce((acc, val) => acc + `${frame(val)}`, '')
    }
    function frame(frame) {
        return  ( !frame.isRef && frame.frame) ? `.frame(${frameSelector(frame.frame)}).scrollRootElement(${printSelector(frame.scrollRootElement)})` : `.frame(${frameSelector(frame)})`
    }
    function frameSelector(selector) {
        if(typeof selector === 'string' && !checkCss(selector)) {
            return JSON.stringify(selector)
        } else {
            return printSelector(selector);
        }
        function checkCss(string) {
            return (string.includes('[') && string.includes(']')) || string.includes('#')
        }
    }

    function region(region) {
        return `.region(${regionParameter(region)})`
    }

    function ignoreRegions(arr) {
        return arr.reduce((acc, val) => `${acc}.ignore(${regionParameter(val)})`, '')
    }
    function layoutRegions(arr){
        return arr.reduce((acc, val) => `${acc}.layout(${regionParameter(val)})`, '')
    }
    function floatingRegions(arr) {
        return arr.reduce((acc, val) => `${acc}.floating(${floating(val)})`, ``)
    }

    function floating(floating) {
        let string
        string = regionParameter(floating.region)
        string += `, ${floating.maxUpOffset}, ${floating.maxDownOffset}, ${floating.maxLeftOffset}, ${floating.maxRightOffset}`
        return string
    }

    function accessibilityRegions(arr) {
        return arr.reduce((acc, val) => `${acc}.accessibility(${accessibility(val)})`, ``)
    }

    function accessibility(val) {
        return `${regionParameter(val.region)}, AccessibilityRegionType.${capitalizeFirstLetter(val.type)}`
    }

    function regionParameter(region) {
        let string
        switch (typeof region) {
            case 'string':
                string = `By.cssSelector(${JSON.stringify(region)})`
                break;
            case "object":
                if(region.type) {
                    string = native ? `getDriver().findElement(${parseObject(region)})` : parseObject(region)
                } else {
                    string = parseObject({value: region, type: 'Region'})
                }
                break;
            case "function":
                string = serialize(region)
                break;
            default:
                throw new Error(`Region parameter of the unimplemented type was used:  ${JSON.stringify(region)}`)
        }
        return string
    }
}

// General

function java(chunks, ...values) {
    const commands = []
    let code = ''
    values.forEach((value, index) => {
        if (typeof value === 'function' && !value.isRef) {
            code += chunks[index]
            commands.push(code, value)
            code = ''
        } else {
            code += chunks[index] + serialize(value)
        }
    })
    code += chunks[chunks.length - 1]
    commands.push(code)
    return commands
}

function serialize(value) {
    let stringified = ''
    if (value && value.isRef) {
        stringified = value.ref()
    } else if (value === null) {
        throw Error(`Null shouldn't be passed to the java code. \n ${value}`)
    } else if (typeof value === 'object') {
        stringified = parseObject(value)
    } else if (typeof value === 'function') {
        stringified = value.toString()
    } else if (typeof value === 'undefined') {
        throw Error(`Undefined shouldn't be passed to the java code. \n ${value}`)
    } else {
        stringified = JSON.stringify(value)
    }
    return stringified
}

function parseObject(object) {
    if (object.selector) {
        return selectors[object.type](JSON.stringify(object.selector))
    } else if (object.type) {
        const typeBuilder = types[object.type]
        if (typeBuilder) {
            if(typeBuilder.isGeneric) {
                return typeBuilder.constructor(object.value, object.generic)
            } else {
                return typeBuilder.constructor(object.value)
            }
        } else throw new Error(`Constructor wasn't implemented for the type: ${object.type}`)
    } else return JSON.stringify(object)
}

function getter({target, key, type}) {
    // console.log(`target: ${target} , key: ${key}, type: ${JSON.stringify(type, null, 3)}, typeOfKey: ${typeof key}, isArray: ${Array.isArray(key)}`)
    if (typeof type === 'undefined') return `${target}.${key}`
    else if (types[type.name]) return types[type.name].get(target, key)
    else throw new Error(`Haven't implement type ${JSON.stringify(type)}`)
}

function mapTypes(type) {
    let mapped
    try {
        mapped = type ? types[type.name].name(type) : types.Element.name()
    } catch (e) {
        throw Error(`SDK haven't implemented support for the ${JSON.stringify(type)} \nWith error: ${e.message} \nStack:${e.stack}`)
    }
    return mapped
}
function wrapSelector(val) {
    return val.selector ? val : {type: 'css', selector: val}
}
function printSelector(val) {
    return serialize((val && val.isRef) ? val : wrapSelector(val))
}
const variable = ({name, value, type}) => `final ${mapTypes(type)} ${name} = (${mapTypes(type)}) ${value}`
const call = ({target, args}) => {
    return args.length > 0 ? `${target}(${args.map(val => JSON.stringify(val)).join(", ")})` : `${target}()`
}
const returnSyntax = ({value}) => {
    return `return ${value};`
}
module.exports = {
    checkSettingsParser: checkSettings,
    java: java,
    getter: getter,
    variable: variable,
    call: call,
    returnSyntax: returnSyntax,
    wrapSelector: wrapSelector,
}