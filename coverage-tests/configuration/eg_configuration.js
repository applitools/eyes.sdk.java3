module.exports = {
  name: "eyes_selenium_java",
  emitter: "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/universal-sdk/java/emitter.js",
  overrides: [
    "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/universal-sdk/js/overrides.js",
    "./configuration/java_overrides_universal"
  ],
  template: "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/master/java/template.hbs",
  tests: "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/universal-sdk/coverage-tests.js",
  ext: ".java",
  outPath: "./src/test/java/coverage/generic",
};
