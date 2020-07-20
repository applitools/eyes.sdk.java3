const supportedTests = require('./supported-tests')
const {initialize} = require('./initialize')
const testFrameworkTemplate = require('./template')

module.exports = {
  name: 'eyes_selenium_java',
  initialize: initialize,
  supportedTests,
  testFrameworkTemplate: testFrameworkTemplate,
  ext: '.java',
  out: './src/test/java/coverage/generic'
}
