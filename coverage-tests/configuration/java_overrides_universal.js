module.exports = {

    // Failed to generate
    // 'should not fail if scroll root is stale on android': {skipEmit: true},
    // 'check region by selector in frame fully on firefox legacy': { skipEmit: true },


    // Python like
    // fails in selenium4 only due to legacy driver being used
    'check window after manual scroll on safari 11': {skip: true},

    // Shadow emitter not implemented
    'check region by selector within shadow dom with vg': {skipEmit: true},
    'check region by element within shadow dom with vg': {skipEmit: true},

    // Feature not present in Selenium
    'should handle check of stale element if selector is preserved': {skip: true}, // Not implemented yet
    'should handle check of stale element in frame if selector is preserved': {skip: true}, // Not implemented yet
}
