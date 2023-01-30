module.exports = {
  name: "eyes_playwright_java",
  emitter: "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/java_playwright/java/playwright/emitter.js",
  overrides: [
    "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/java_playwright/js/overrides.js",
    "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/java_playwright/java/playwright/overrides.js"
  ],
  template: "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/java_playwright/java/playwright/template.hbs",
  tests: "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/java_playwright/coverage-tests.js",
  ext: ".java",
  outPath: "./src/test/java/coverage/generic/playwright",
  fixtures: "./fixtures"
};