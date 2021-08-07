module.exports = {
  name: "eyes_selenium_java",
  emitter: "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/eg_java/java/emitter.js",
  overrides: [
    "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/eg_java/java/overrides.js",
    "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/eg_java/eg.overrides.js"
  ],
  template: "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/eg_java/java/template.hbs",
  tests: "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/eg_java/coverage-tests.js",
  ext: ".java",
  outPath: "./src/test/java/coverage/generic",
}
