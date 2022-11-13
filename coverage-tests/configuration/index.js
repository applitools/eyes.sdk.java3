module.exports = {
  name: "eyes_selenium_java",
  emitter: "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/java_jpmc_skip_lazyload/java/emitter.js",
  overrides: [
    "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/java_jpmc_skip_lazyload/js/overrides.js",
    "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/java_jpmc_skip_lazyload/java/overrides.js"
  ],
  template: "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/java_jpmc_skip_lazyload/java/template.hbs",
  tests: "https://raw.githubusercontent.com/applitools/sdk.coverage.tests/java_jpmc_skip_lazyload/coverage-tests.js",
  ext: ".java",
  outPath: "./src/test/java/coverage/generic",
};
