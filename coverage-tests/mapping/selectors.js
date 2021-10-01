module.exports = {
    "css": (selector) => `By.cssSelector(${selector})`,
    "class name": (selector) => `By.className(${selector})`,
    "id": (selector) => `By.id(${selector})`,
    "accessibility id": (selector) => `MobileBy.AccessibilityId(${selector})`,
    "-android uiautomator": (selector) => `MobileBy.AndroidUIAutomator(${selector})`,
    "androidViewTag": (selector) => `MobileBy.AndroidViewTag(${selector})`,
    "-ios predicate string": (selector) => `MobileBy.iOSNsPredicateString(${selector})`,
    "-ios class chain": (selector) => `MobileBy.iOSClassChain(${selector})`,
}