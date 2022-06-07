const base = require("./index.js")
module.exports = {
 ...base,
  emitOnly: [
    'check window with css stitching',
    'check window with default fully with css stitching',
    'check window with scroll stitching classic',
    'check window with vg',
    'check window with default fully with vg',
    'check frame with css stitching',
    'check frame with vg',
    'check region by coordinates with css stitching',
    'check region by coordinates with vg',
  ],
};
