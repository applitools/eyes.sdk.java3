module.exports = {
  name: "eyes_selenium_java",
  emitter: "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/qa/eg_update/java/emitter.js",
  overrides: [
    "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/universal-sdk/js/overrides.js",
    "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/universal-sdk/java/overrides.js"
  ],
  template: "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/master/java/template.hbs",
  tests: "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/universal-sdk/coverage-tests.js",
  ext: ".java",
  outPath: "./src/test/java/coverage/generic",
};
