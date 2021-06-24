module.exports = {
  name: 'eyes_selenium_java',
  emitter: 'https://raw.githubusercontent.com/applitools/sdk.coverage.tests/execution_grid/java/emitter.js',
  overrides: 'https://raw.githubusercontent.com/applitools/sdk.coverage.tests/execution_grid/java/overrides.js',
  template: 'https://raw.githubusercontent.com/applitools/sdk.coverage.tests/execution_grid/java/template.hbs',
  tests: 'https://raw.githubusercontent.com/applitools/sdk.coverage.tests/execution_grid/coverage-tests.js',
  ext: '.java',
  outPath: './src/test/java/coverage/generic',
}
