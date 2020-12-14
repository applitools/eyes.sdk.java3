const iosDeviceName = require('./iosDeviceName')
const deviceName = require('./deviceName')
const {capitalizeFirstLetter} = require('../util')
const simpleGetter = (target, key) => `${target}.get${capitalizeFirstLetter(key)}()`;
const types = {
    "Map": {
        constructor: (value, generic) => {
            const mapKey = generic[0]
            const mapValue = generic[1]
            const keyType = types[mapKey.name]
            const valueType = types[mapValue.name]
            return `new HashMap<${keyType.name(mapKey)}, ${valueType.name(mapValue)}>()
    {{ ${Object.keys(value).map(key => `put(${keyType.constructor(key, mapKey.generic)}, ${valueType.constructor(value[key], mapValue.generic)});`).join(' ')} }}`
        },
        get: (target, key) => `${target}.get("${key}")`,
        isGeneric: true,
        name: (type) => {
            const key = type.generic[0].name
            const genericKey = type.generic[0]
            const value = type.generic[1].name
            const genericValue = type.generic[1]
            return `Map<${types[key].name(genericKey)},${types[value].name(genericValue)}>`
        },
    },
    "List": {
        constructor: (value, generic) => {
            const param = generic[0]
            const paramType = types[param.name]
            return `new ArrayList<${paramType.name(param)}>()
            {{ ${value.map(region => `add(${paramType.constructor(region)});`).join(' ')} }}`
        },
        name: (type) => `List<${type.generic[0].name}>`,
        get: (target, key) => Number.isInteger(Number(key)) ? `${target}.get(${key})` : `${target}.${key}()`
    },
    "RectangleSize": {
        constructor: (value) => `new RectangleSize(${value.width}, ${value.height})`,
        get: (target, key) => key.includes('get') ? `${target}.${key}` : simpleGetter(target, key),
        name: () => 'RectangleSize',
    },
    "TestInfo": {
        get: simpleGetter,
        name: () => 'SessionResults',
    },
    "JsonNode": {
        get: (target, key) => `${target}.get(${Number.isInteger(Number(key)) ? key : `"${key}"`})`,
        name: () => 'JsonNode'
    },
    "TestResults": {
        name: () => 'TestResults',
    },
    "Element": {
        name: () => 'WebElement',
        get: simpleGetter,
    },
    "Region": {
        name: () => 'Region',
        constructor: (value) => `new Region(${value.left}, ${value.top}, ${value.width}, ${value.height})`,
    },
    "FloatingRegion": {
        constructor: (value) => {
            let region;
            if(value.region) region = `${value.region.left},${value.region.top}, ${value.region.width}, ${value.region.height}`
            else region = `${value.left}, ${value.top}, ${value.width}, ${value.height}`
            return `new FloatingMatchSettings(${region}, ${value.maxUpOffset}, ${value.maxDownOffset}, ${value.maxLeftOffset}, ${value.maxRightOffset})`}
    },
    "Array": {
        get: (target, key) => `${target}[${key}]`,
    },
    "Boolean": {
        constructor: (value) => `${value}`,
        name: () => `Boolean`
    },
    "BooleanObject":{
        constructor: (value) => `Boolean.${value.toString().toUpperCase()}`
    },
    "String": {
        constructor: (value) => JSON.stringify(value),
        name: () => `String`,
    },
    "Number": {
        constructor: (value) => `${JSON.stringify(value)}L`,
        name: () => `Number`,
    },
    "Image": {
        get: simpleGetter,
    },
    "ImageMatchSettings": {
        get: simpleGetter,
    },
    "AppOutput": {
        get: simpleGetter,
    },
    "AccessibilitySettings":{
        constructor: function (value) {
            return `new AccessibilitySettings(${types.AccessibilityLevel.constructor(value.level)}, ${types.AccessibilityGuidelinesVersion.constructor(value.guidelinesVersion || value.version)})`
        } ,
        get: (target, key) => (key === 'version') ? `${target}.getGuidelinesVersion()` : simpleGetter(target, key)
    },
    "AccessibilityRegion":{
        constructor: (value) => `new AccessibilityRegionByRectangle(${value.left}, ${value.top}, ${value.width}, ${value.height}, AccessibilityRegionType.${capitalizeFirstLetter(value.type)})`
    },
    "AccessibilityLevel":{
        constructor: (value) => `AccessibilityLevel.${value}`
    },
    "AccessibilityGuidelinesVersion":{
        constructor: (value) => `AccessibilityGuidelinesVersion.${value}`
    },
    "Location": {
        constructor: (value) => `new Location(${value.x}, ${value.y})`
    },
    "BrowsersInfo": {
        constructor: (value) => {
            return value.map(render => {
                if(render.name) return `new DesktopBrowserInfo(${render.width}, ${render.height}, BrowserType.${render.name.toUpperCase()})`
                else if (render.iosDeviceInfo) return `new IosDeviceInfo(${iosDeviceName[render.iosDeviceInfo.deviceName]})`
                else if (render.chromeEmulationInfo) return `new ChromeEmulationInfo(${deviceName[render.chromeEmulationInfo.deviceName]}, ScreenOrientation.PORTRAIT)`
            }).join(', ')
        },
    },
}
module.exports = types