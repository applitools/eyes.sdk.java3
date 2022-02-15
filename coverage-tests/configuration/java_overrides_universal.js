module.exports = {
    // JS specific, no need to implement in java
    'should return test results from close with passed classic test': {skipEmit: true}, // skipped
    'should return test results from close with passed vg test': {skipEmit: true}, //   cause
    'should return test results from close with failed classic test': {skipEmit: true}, // tests
    'should return test results from close with failed vg test': {skipEmit: true}, // JS specific
    // 'should handle check of stale element in frame if selector is preserved': {skipEmit: true}, // Not implemented yet

    // fails in selenium4 only due to legacy driver being used
    'check window after manual scroll on safari 11': {skip: true},

    // Shadow emitter not implemented
    // 'check region by element within shadow dom with vg': {skip: true},
    // 'check region by selector within shadow dom with vg': {skip: true},

    // Failed to generate
    // 'should send dom and location when check window': {skipEmit: true},
    // 'should send dom and location when check window with vg': {skipEmit: true},
    // 'should send dom and location when check window fully': {skipEmit: true},
    // 'should send dom and location when check window fully with vg': {skipEmit: true},
    // 'should send dom and location when check frame': {skipEmit: true},
    // 'should send dom and location when check frame with vg': {skipEmit: true},
    // 'should send dom and location when check frame fully': {skipEmit: true},
    // 'should send dom and location when check frame fully with vg': {skip: true}, // not supported by ufg
    // 'should send dom and location when check region by selector': {skipEmit: true},
    // 'should send dom and location when check region by selector with vg': {skipEmit: true},
    // 'should send dom and location when check region by selector fully': {skipEmit: true},
    // 'should send dom and location when check region by selector fully with vg': {skipEmit: true},
    // 'should send dom and location when check region by selector in frame': {skipEmit: true},
    // 'should send dom and location when check region by selector in frame with vg': {skip: true}, // not supported by ufg
    // 'should send dom and location when check region by selector with custom scroll root': {skipEmit: true},
    // 'should send dom and location when check region by selector with custom scroll root with vg': {skipEmit: true},
    // 'should send dom and location when check region by selector fully with custom scroll root': {skipEmit: true},
    // 'should send dom and location when check region by selector fully with custom scroll root with vg': {skipEmit: true},
    // 'should send dom of version 11': {skipEmit: true},
    // 'should not fail if scroll root is stale on android': {skipEmit: true},
    // 'check region by selector in frame fully on firefox legacy': { skipEmit: true },
    'check window fully on android chrome emulator on mobile page': {skip: true},
    'check window fully on android chrome emulator on mobile page with horizontal scroll': {skip: true},
    'check window fully on android chrome emulator on desktop page': {skip: true},
    'adopted styleSheets on chrome': {skipEmit: true},
    'adopted styleSheets on firefox': {skipEmit: true},
    'check region by selector within shadow dom with vg': {skipEmit: true},
    'check region by element within shadow dom with vg': {skipEmit: true},
}
