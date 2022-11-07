module.exports = {
  name: "eyes_selenium_java",
  emitter: "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/java-sdk-unckip-tests/java/emitter.js",
  overrides: [
    "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/java-sdk-unckip-tests/js/overrides.js",
    "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/java-sdk-unckip-tests/java/overrides.js"
  ],
  template: "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/java-sdk-unckip-tests/java/template.hbs",
  tests: "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/java-sdk-unckip-tests/coverage-tests.js",
  ext: ".java",
  outPath: "./src/test/java/coverage/generic",
};
