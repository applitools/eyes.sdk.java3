function getVisibleElementRect(el) {
    var intersected = el.getBoundingClientRect()
    el = el.parentElement;
    if (el != null && el.tagName === 'HTML'){
        el = el.ownerDocument.defaultView.frameElement
    }
    while (el != null) {
        var bcr = el.getBoundingClientRect()
        var cr = {
            left: bcr.left + el.clientLeft,
            top: bcr.top + el.clientTop,
            width: el.clientWidth,
            height: el.clientHeight,
        }
        if (el.tagName === 'IFRAME'){
            intersected.left += cr.left
            intersected.top += cr.top
        }
        var tempIntersected = intersect(cr, intersected)
        if (tempIntersected.height<intersected.height) {
            if (window.getComputedStyle(el)['overflowY'] == 'visible'){
                tempIntersected.height = intersected.height
            }
        }
        if (tempIntersected.width < intersected.width)
        {
            if (window.getComputedStyle(el)['overflowX'] == 'visible')
            {
                tempIntersected.width = intersected.width
            }
        }
        intersected = tempIntersected
        el = el.parentElement;
        if (el != null && el.tagName === 'HTML'){
            el = el.ownerDocument.defaultView.frameElement
        }
    }
    return intersected.left+';'+intersected.top+';'+intersected.width+';'+intersected.height;
    function intersect(rect1, rect2) {
        var intersectionLeft = rect1.left >= rect2.left ? rect1.left : rect2.left
        var intersectionTop = rect1.top >= rect2.top ? rect1.top : rect2.top
        var rect1Right = rect1.left + rect1.width
        var rect2Right = rect2.left + rect2.width
        var intersectionRight = rect1Right <= rect2Right ? rect1Right : rect2Right
        var intersectionWidth = intersectionRight - intersectionLeft
        var rect1Bottom = rect1.top + rect1.height
        var rect2Bottom = rect2.top + rect2.height
        var intersectionBottom = rect1Bottom <= rect2Bottom ? rect1Bottom : rect2Bottom
        var intersectionHeight = intersectionBottom - intersectionTop
        return {
            left: intersectionLeft,
            top: intersectionTop,
            width: intersectionWidth,
            height: intersectionHeight,
        }
    }
}
return getVisibleElementRect(arguments[0]);