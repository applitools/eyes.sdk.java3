module.exports = {
    // JS specific, no need to implement in java
    'should return test results from close with passed classic test': {skipEmit: true}, // skipped
    'should return test results from close with passed vg test': {skipEmit: true}, //   cause
    'should return test results from close with failed classic test': {skipEmit: true}, // tests
    'should return test results from close with failed vg test': {skipEmit: true}, // JS specific
    'should extract text from regions': {skipEmit: true}, // Not implemented yet
    'should handle check of stale element in frame if selector is preserved': {skipEmit: true}, // Not implemented yet
    // General
    'should hide and restore scrollbars with vg': {skip: true}, // java.lang.IllegalArgumentException: checkTasks == 0
    'should not check if disabled': {skip: true}, // throws com.applitools.eyes.EyesException: Status is null in the test results
    'should send accessibility regions by selector with vg': {skip: true}, // diffs
    'should send dom on edge legacy': {skip: true}, // java.lang.IllegalArgumentException: width < 0 , in Eyes.open
    'should send floating region by coordinates in frame with vg': {skip: true}, // stale element reference
    'should send floating region by coordinates with vg': {skip: true}, // diffs
    'should send floating region by selector with vg': {skip: true}, // diffs
    'should send ignore region by coordinates with vg': {skip: true}, // diffs
    'should send ignore region by selector with vg': {skip: true}, // diffs
    'should send ignore region by the same selector as target region with scroll stitching': {skip: true}, // Region has different position [(0, 0) 304x184, SCREENSHOT_AS_IS] but found [(0, 284) 304x184, SCREENSHOT_AS_IS]
    'should send ignore regions by selector with vg': {skip: true}, // diffs
    'should set viewport size on edge legacy': {skip: true}, // java.lang.IllegalArgumentException: width < 0
    'should send ignore displacements with vg': {skip: true}, // diffs
    // window
    'check window with layout breakpoints in config': {skipEmit: true}, // layout breakpoints are not implemented
    'check window with layout breakpoints': {skipEmit: true}, // layout breakpoints are not implemented
    'check window fully on android chrome emulator on desktop page': {skip: true}, // have diffs
    'check window fully on android chrome emulator on mobile page': {skip: true}, // have diffs
    'check window fully on android chrome emulator on mobile page with horizontal scroll': {skip: true}, // have diffs
    'check window fully with fixed scroll root element': {skip: true}, // Have differences
    'check window two times with vg classic': {skip: true}, // Diffs, baseline saved for fully() while check is without ( all good for classic)
    'check window with vg classic': {skip: true}, // baseline saved for fully() while check is without
    'check window with vg': {skip: true}, // baseline saved for fully() while check is without
    'check window fully and frame in frame fully with vg': {skip: true}, // Unable to locate element: {"method":"css selector","selector":"[name="frame1"]"}
    'check window fully with vg': {skip: true}, // diffs
    'check window after manual scroll on safari 11': {skip: true}, //diffs
    'check window after manual scroll on safari 12': {skip: true}, // diffs
    'check window fully with html scrollRootElement after scroll with css stitching': {skip: true}, // diffs
    'check window fully with html scrollRootElement after scroll with scroll stitching': {skip: true}, // diffs
    // region
    'check region by selector in frame fully with vg': {skip: true}, // stale element reference
    'check region by selector in frame fully with vg classic': {skip: true}, // stale element reference
    'check region by native selector': {skip: true}, // There is a difference with the viewport size of the baseline created on JS (related to the viewport issue cross SDKs)
    'check region by coordinates in frame fully with vg': {skip: true}, // stale element reference VG
    'check region by coordinates in frame with vg': {skip: true}, // Unable to locate element: {"method":"css selector","selector":"[name="frame1"]"}
    'check fixed region by selector with css stitching': {skip: true}, // diffs
    'check hovered region by element with scroll stitching': {skip: true}, // Diffs
    'check hovered region by element with css stitching': {skip: true}, // Diffs
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
    'check frame fully with css stitching': {skip: true}, // diffs
    'check frame after manual switch to frame with vg classic': {skip: true}, // java.lang.IllegalArgumentException: checkTasks == 0
    'should send dom and location when check window': {skipEmit: true},
    'should send dom and location when check window with vg': {skipEmit: true},
    'should send dom and location when check window fully': {skipEmit: true},
    'should send dom and location when check window fully with vg': {skipEmit: true},
    'should send dom and location when check frame': {skipEmit: true},
    'should send dom and location when check frame with vg': {skipEmit: true},
    'should send dom and location when check frame fully': {skipEmit: true},
    'should send dom and location when check frame fully with vg': {skipEmit: true},
    'should send dom and location when check region by selector': {skipEmit: true},
    'should send dom and location when check region by selector with vg': {skipEmit: true},
    'should send dom and location when check region by selector fully': {skipEmit: true},
    'should send dom and location when check region by selector fully with vg': {skipEmit: true},
    'should send dom and location when check region by selector in frame': {skipEmit: true},
    'should send dom and location when check region by selector in frame with vg': {skipEmit: true},
    'should send dom and location when check region by selector with custom scroll root': {skipEmit: true},
    'should send dom and location when check region by selector with custom scroll root with vg': {skipEmit: true},
    'should send dom and location when check region by selector fully with custom scroll root': {skipEmit: true},
    'should send dom and location when check region by selector fully with custom scroll root with vg': {skipEmit: true},
    'should send dom of version 10': {skipEmit: true},
    'appium android check window': {skip: true}, //wrong ignore region ticket 2396
    'appium android check region with ignore region': {skip: true}, //wrong ignore region region ticket 2396
    'appium iOS check region with ignore region': {skip: true}, //wrong ignore region region ticket 2396
 }
