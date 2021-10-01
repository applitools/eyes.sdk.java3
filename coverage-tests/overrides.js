module.exports = {
    // JS specific, no need to implement in java
    'should return test results from close with passed classic test': {skipEmit: true}, // skipped
    'should return test results from close with passed vg test': {skipEmit: true}, //   cause
    'should return test results from close with failed classic test': {skipEmit: true}, // tests
    'should return test results from close with failed vg test': {skipEmit: true}, // JS specific
    'should handle check of stale element in frame if selector is preserved': {skipEmit: true}, // Not implemented yet
    // General
    'should hide and restore scrollbars with vg': {skip: true}, // java.lang.IllegalArgumentException: checkTasks == 0
    'should send floating region by coordinates in frame with vg': {skip: true}, // stale element reference
    'should send dom on edge legacy': {skip: true}, // java.lang.IllegalArgumentException: width < 0 , in Eyes.open
    'should set viewport size on edge legacy': {skip: true}, // java.lang.IllegalArgumentException: width < 0
    'should extract text regions from image': {skip: true},
    // window
    'check window fully on android chrome emulator on desktop page': {skip: true}, // have diffs
    'check window fully on android chrome emulator on mobile page': {skip: true}, // have diffs
    'check window fully on android chrome emulator on mobile page with horizontal scroll': {skip: true}, // have diffs
    'check window fully with fixed scroll root element': {config: {branchName: 'current1'}}, // diffs if compare to common baseline
    'check window fully and frame in frame fully with vg': {skip: true}, // Unable to locate element: {"method":"css selector","selector":"[name="frame1"]"}
    'check window after manual scroll with vg': {skip: true}, // diffs
    'check window after manual scroll on safari 11': {skip: true}, //diffs
    'check window on page with sticky header with vg': {skip: true}, // diffs
    // region
    'check region by selector in frame fully with vg': {skip: true}, // stale element reference
    'check region by selector in frame fully with vg classic': {skip: true}, // stale element reference
    'check region by native selector': {skip: true}, // There is a difference with the viewport size of the baseline created on JS (related to the viewport issue cross SDKs)
    'check region by coordinates in frame fully with vg': {skip: true}, // stale element reference VG
    'check region by coordinates in frame with vg': {skip: true}, // Unable to locate element: {"method":"css selector","selector":"[name="frame1"]"}
    'check fixed region by selector with css stitching': {skip: true}, // diffs
    'check hovered region by element with scroll stitching': {skip: true}, // Diffs
    'check hovered region by element with css stitching': {config: {branchName: 'current1'}}, // diffs if compare to common baseline
    'check region by selector in overflowed frame fully with css stitching': {skip: true}, // diffs
    'check region by selector in overflowed frame fully with scroll stitching': {skip: true},// diffs
    'check region by selector in overflowed frame with vg': {skip: true}, // Unable to locate element: {"method":"css selector","selector":"[name="frame1"]"}
    'check regions by coordinates in frame with css stitching': {skip: true}, // com.applitools.eyes.OutOfBoundsException: Region [(0, 15000) 385x5000, SCREENSHOT_AS_IS] is out of screenshot bounds [(0, 0) 385x15000, SCREENSHOT_AS_IS]
    'check regions by coordinates in frame with scroll stitching': {skip: true}, // com.applitools.eyes.OutOfBoundsException: Region [(0, 15000) 385x5000, SCREENSHOT_AS_IS] is out of screenshot bounds [(0, 0) 385x15000, SCREENSHOT_AS_IS]
    'check regions by coordinates in frame with vg': {skip: true}, // Unable to locate element: {"method":"css selector","selector":"#modal2 iframe"}
    'check regions by coordinates in overflowed frame with css stitching': {skip: true}, // com.applitools.eyes.OutOfBoundsException: Region [(0, 15000) 385x5000, SCREENSHOT_AS_IS] is out of screenshot bounds [(0, 0) 385x15000, SCREENSHOT_AS_IS]
    'check regions by coordinates in overflowed frame with scroll stitching': {skip: true}, // com.applitools.eyes.OutOfBoundsException: Region [(0, 15000) 385x5000, SCREENSHOT_AS_IS] is out of screenshot bounds [(0, 0) 385x15000, SCREENSHOT_AS_IS]
    'check regions by coordinates in overflowed frame with vg': {skip: true}, // Unable to locate element: {"method":"css selector","selector":"#modal3 iframe"}
    // frame
    'check frame with vg': {skip: true}, // Unable to locate element: {"method":"css selector","selector":"[name="frame1"]"}
    'check frame with vg classic': {skip: true}, // stale element reference:
    'check frame with scroll stitching classic': {skip: true}, // diffs
    'check frame with css stitching classic': {skip: true}, // diffs
    'check frame in frame fully with vg': {skip: true}, //  Unable to locate element: {"method":"css selector","selector":"[name="frame1"]"}
    'check frame fully with vg': {skip: true}, //  stale element reference
    'check frame fully with css stitching': {config: {branchName: 'current_ruby'}}, // diffs
    'check frame after manual switch to frame with vg classic': {skip: true}, // java.lang.IllegalArgumentException: checkTasks == 0
    'appium android check window': {skip: true}, //wrong ignore region ticket 2396
    'appium android check region with ignore region': {skip: true}, //wrong ignore region region ticket 2396
    'appium iOS check region with ignore region': {skip: true}, //wrong ignore region region ticket 2396

    'should send dom and location when check window': {skipEmit: true},
    'should send dom and location when check window with vg': {skipEmit: true},
    'should send dom and location when check window fully': {skipEmit: true},
    'should send dom and location when check window fully with vg': {skipEmit: true},
    'should send dom and location when check frame': {skipEmit: true},
    'should send dom and location when check frame with vg': {skipEmit: true},
    'should send dom and location when check frame fully': {skipEmit: true},
    'should send dom and location when check frame fully with vg': {skip: true}, // not supported by ufg
    'should send dom and location when check region by selector': {skipEmit: true},
    'should send dom and location when check region by selector with vg': {skipEmit: true},
    'should send dom and location when check region by selector fully': {skipEmit: true},
    'should send dom and location when check region by selector fully with vg': {skipEmit: true},
    'should send dom and location when check region by selector in frame': {skipEmit: true},
    'should send dom and location when check region by selector in frame with vg': {skip: true}, // not supported by ufg
    'should send dom and location when check region by selector with custom scroll root': {skipEmit: true},
    'should send dom and location when check region by selector with custom scroll root with vg': {skipEmit: true},
    'should send dom and location when check region by selector fully with custom scroll root': {skipEmit: true},
    'should send dom and location when check region by selector fully with custom scroll root with vg': {skipEmit: true},
    'should send dom of version 11': {skipEmit: true},
    'should not fail if scroll root is stale on android': {skipEmit: true},
    'check region by selector in frame fully on firefox legacy': { skipEmit: true },
    'adopted styleSheets on chrome': {skipEmit: true},
	'adopted styleSheets on firefox': {skipEmit: true},
	'check region by selector within shadow dom with vg': {skipEmit: true},
	'check region by element within shadow dom with vg': {skipEmit: true},
}
