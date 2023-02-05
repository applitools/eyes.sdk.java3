module.exports = {
  name: "eyes_playwright_java",
  emitter: "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/universal-sdk/java/playwright/emitter.js",
  overrides: [
    "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/universal-sdk/js/overrides.js",
    "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/universal-sdk/java/playwright/overrides.js"
  ],
  template: "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/universal-sdk/java/playwright/template.hbs",
  tests: "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/universal-sdk/coverage-tests.js",
  ext: ".java",
  outPath: "./src/test/java/coverage/generic/playwright",
  fixtures: "./fixtures"
};