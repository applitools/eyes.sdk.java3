module.exports = {
  name: 'eyes_selenium_java',
  emitter: 'https://raw.githubusercontent.com/applitools/sdk.coverage.tests/master/java/emitter.js',
  overrides: 'https://raw.githubusercontent.com/applitools/sdk.coverage.tests/remove_skip_for_Java3_generic2/java/overrides.js',
  template: 'https://raw.githubusercontent.com/applitools/sdk.coverage.tests/master/java/template.hbs',
  ext: '.java',
  outPath: './src/test/java/coverage/generic',
}
