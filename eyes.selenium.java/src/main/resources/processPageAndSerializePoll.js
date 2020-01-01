/* @applitools/dom-snapshot@3.2.2 */

function __processPageAndSerializePoll() {
  var processPageAndSerializePoll = (function () {
  'use strict';

  const EYES_NAME_SPACE = '__EYES__APPLITOOLS__';

  function pullify(script, win = window) {
    return function() {
      const scriptName = script.name;
      if (!win[EYES_NAME_SPACE]) {
        win[EYES_NAME_SPACE] = {};
      }
      if (!win[EYES_NAME_SPACE][scriptName]) {
        win[EYES_NAME_SPACE][scriptName] = {
          status: 'WIP',
          value: null,
          error: null,
        };
        script
          .apply(null, arguments)
          .then(r => ((resultObject.status = 'SUCCESS'), (resultObject.value = r)))
          .catch(e => ((resultObject.status = 'ERROR'), (resultObject.error = e.message)));
      }

      const resultObject = win[EYES_NAME_SPACE][scriptName];
      if (resultObject.status === 'SUCCESS') {
        win[EYES_NAME_SPACE][scriptName] = null;
      }

      return JSON.stringify(resultObject);
    };
  }

  var pollify = pullify;

  // This code was copied and modified from https://github.com/beatgammit/base64-js/blob/bf68aaa277/index.js
  // License: https://github.com/beatgammit/base64-js/blob/bf68aaa277d9de7007cc0c58279c411bb10670ac/LICENSE

  function arrayBufferToBase64(ab) {
    const lookup = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/'.split('');

    const uint8 = new Uint8Array(ab);
    const len = uint8.length;
    const extraBytes = len % 3; // if we have 1 byte left, pad 2 bytes
    const parts = [];
    const maxChunkLength = 16383; // must be multiple of 3

    let tmp;

    // go through the array every three bytes, we'll deal with trailing stuff later
    for (let i = 0, len2 = len - extraBytes; i < len2; i += maxChunkLength) {
      parts.push(encodeChunk(i, i + maxChunkLength > len2 ? len2 : i + maxChunkLength));
    }

    // pad the end with zeros, but make sure to not forget the extra bytes
    if (extraBytes === 1) {
      tmp = uint8[len - 1];
      parts.push(lookup[tmp >> 2] + lookup[(tmp << 4) & 0x3f] + '==');
    } else if (extraBytes === 2) {
      tmp = (uint8[len - 2] << 8) + uint8[len - 1];
      parts.push(lookup[tmp >> 10] + lookup[(tmp >> 4) & 0x3f] + lookup[(tmp << 2) & 0x3f] + '=');
    }

    return parts.join('');

    function tripletToBase64(num) {
      return (
        lookup[(num >> 18) & 0x3f] +
        lookup[(num >> 12) & 0x3f] +
        lookup[(num >> 6) & 0x3f] +
        lookup[num & 0x3f]
      );
    }

    function encodeChunk(start, end) {
      let tmp;
      const output = [];
      for (let i = start; i < end; i += 3) {
        tmp = ((uint8[i] << 16) & 0xff0000) + ((uint8[i + 1] << 8) & 0xff00) + (uint8[i + 2] & 0xff);
        output.push(tripletToBase64(tmp));
      }
      return output.join('');
    }
  }

  var arrayBufferToBase64_1 = arrayBufferToBase64;

  function extractLinks(doc = document) {
    const srcsetUrls = Array.from(doc.querySelectorAll('img[srcset],source[srcset]'))
      .map(srcsetEl =>
        srcsetEl
          .getAttribute('srcset')
          .split(', ')
          .map(str => str.trim().split(/\s+/)[0]),
      )
      .reduce((acc, urls) => acc.concat(urls), []);

    const srcUrls = Array.from(
      doc.querySelectorAll('img[src],source[src],input[type="image"][src]'),
    ).map(srcEl => srcEl.getAttribute('src'));

    const imageUrls = Array.from(doc.querySelectorAll('image,use'))
      .map(hrefEl => hrefEl.getAttribute('href') || hrefEl.getAttribute('xlink:href'))
      .filter(u => u && u[0] !== '#');

    const objectUrls = Array.from(doc.querySelectorAll('object'))
      .map(el => el.getAttribute('data'))
      .filter(Boolean);

    const cssUrls = Array.from(doc.querySelectorAll('link[rel="stylesheet"]')).map(link =>
      link.getAttribute('href'),
    );

    const videoPosterUrls = Array.from(doc.querySelectorAll('video[poster]')).map(videoEl =>
      videoEl.getAttribute('poster'),
    );

    return Array.from(srcsetUrls)
      .concat(Array.from(srcUrls))
      .concat(Array.from(imageUrls))
      .concat(Array.from(cssUrls))
      .concat(Array.from(videoPosterUrls))
      .concat(Array.from(objectUrls));
  }

  var extractLinks_1 = extractLinks;

  function uuid() {
    return window.crypto.getRandomValues(new Uint32Array(1))[0];
  }

  var uuid_1 = uuid;

  function isInlineFrame(frame) {
    return (
      !/^https?:.+/.test(frame.src) ||
      (frame.contentDocument &&
        frame.contentDocument.location &&
        frame.contentDocument.location.href === 'about:blank')
    );
  }

  var isInlineFrame_1 = isInlineFrame;

  function isAccessibleFrame(frame) {
    try {
      const doc = frame.contentDocument;
      return !!(doc && doc.defaultView && doc.defaultView.frameElement);
    } catch (err) {
      // for CORS frames
    }
  }

  var isAccessibleFrame_1 = isAccessibleFrame;

  function absolutizeUrl(url, absoluteUrl) {
    return new URL(url, absoluteUrl).href;
  }

  var absolutizeUrl_1 = absolutizeUrl;

  const NEED_MAP_INPUT_TYPES = new Set([
    'date',
    'datetime-local',
    'email',
    'month',
    'number',
    'password',
    'search',
    'tel',
    'text',
    'time',
    'url',
    'week',
  ]);

  function domNodesToCdt(docNode, baseUrl) {
    const cdt = [{nodeType: Node.DOCUMENT_NODE}];
    const docRoots = [docNode];
    const canvasElements = [];
    const inlineFrames = [];

    cdt[0].childNodeIndexes = childrenFactory(cdt, docNode.childNodes);
    return {cdt, docRoots, canvasElements, inlineFrames};

    function childrenFactory(cdt, elementNodes) {
      if (!elementNodes || elementNodes.length === 0) return null;

      const childIndexes = [];
      Array.prototype.forEach.call(elementNodes, elementNode => {
        const index = elementNodeFactory(cdt, elementNode);
        if (index !== null) {
          childIndexes.push(index);
        }
      });
      return childIndexes;
    }

    function elementNodeFactory(cdt, elementNode) {
      let node, manualChildNodeIndexes, dummyUrl;
      const {nodeType} = elementNode;

      if ([Node.ELEMENT_NODE, Node.DOCUMENT_FRAGMENT_NODE].includes(nodeType)) {
        if (elementNode.nodeName !== 'SCRIPT') {
          if (
            elementNode.nodeName === 'STYLE' &&
            elementNode.sheet &&
            elementNode.sheet.cssRules.length
          ) {
            cdt.push(getCssRulesNode(elementNode));
            manualChildNodeIndexes = [cdt.length - 1];
          }

          if (elementNode.tagName === 'TEXTAREA' && elementNode.value !== elementNode.textContent) {
            cdt.push(getTextContentNode(elementNode));
            manualChildNodeIndexes = [cdt.length - 1];
          }

          node = getBasicNode(elementNode);
          node.childNodeIndexes =
            manualChildNodeIndexes ||
            (elementNode.childNodes.length ? childrenFactory(cdt, elementNode.childNodes) : []);

          if (elementNode.shadowRoot) {
            node.shadowRootIndex = elementNodeFactory(cdt, elementNode.shadowRoot);
            docRoots.push(elementNode.shadowRoot);
          }

          if (elementNode.nodeName === 'CANVAS') {
            dummyUrl = absolutizeUrl_1(`applitools-canvas-${uuid_1()}.png`, baseUrl);
            node.attributes.push({name: 'data-applitools-src', value: dummyUrl});
            canvasElements.push({element: elementNode, url: dummyUrl});
          }

          if (
            elementNode.nodeName === 'IFRAME' &&
            isAccessibleFrame_1(elementNode) &&
            isInlineFrame_1(elementNode)
          ) {
            dummyUrl = absolutizeUrl_1(`?applitools-iframe=${uuid_1()}`, baseUrl);
            node.attributes.push({name: 'data-applitools-src', value: dummyUrl});
            inlineFrames.push({element: elementNode, url: dummyUrl});
          }
        } else {
          node = getScriptNode(elementNode);
        }
      } else if (nodeType === Node.TEXT_NODE) {
        node = getTextNode(elementNode);
      } else if (nodeType === Node.DOCUMENT_TYPE_NODE) {
        node = getDocNode(elementNode);
      }

      if (node) {
        cdt.push(node);
        return cdt.length - 1;
      } else {
        return null;
      }
    }

    function nodeAttributes({attributes = {}}) {
      return Object.keys(attributes).filter(k => attributes[k] && attributes[k].name);
    }

    function getCssRulesNode(elementNode) {
      return {
        nodeType: Node.TEXT_NODE,
        nodeValue: Array.from(elementNode.sheet.cssRules)
          .map(rule => rule.cssText)
          .join(''),
      };
    }

    function getTextContentNode(elementNode) {
      return {
        nodeType: Node.TEXT_NODE,
        nodeValue: elementNode.value,
      };
    }

    function getBasicNode(elementNode) {
      const node = {
        nodeType: elementNode.nodeType,
        nodeName: elementNode.nodeName,
        attributes: nodeAttributes(elementNode).map(key => {
          let value = elementNode.attributes[key].value;
          const name = elementNode.attributes[key].name;
          if (/^blob:/.test(value)) {
            value = value.replace(/^blob:/, '');
          }
          return {
            name,
            value,
          };
        }),
      };

      if (elementNode.tagName === 'INPUT' && ['checkbox', 'radio'].includes(elementNode.type)) {
        if (elementNode.attributes.checked && !elementNode.checked) {
          const idx = node.attributes.findIndex(a => a.name === 'checked');
          node.attributes.splice(idx, 1);
        }
        if (!elementNode.attributes.checked && elementNode.checked) {
          node.attributes.push({name: 'checked'});
        }
      }

      if (
        elementNode.tagName === 'INPUT' &&
        NEED_MAP_INPUT_TYPES.has(elementNode.type) &&
        (elementNode.attributes.value && elementNode.attributes.value.value) !== elementNode.value
      ) {
        const nodeAttr = node.attributes.find(a => a.name === 'value');
        if (nodeAttr) {
          nodeAttr.value = elementNode.value;
        } else {
          node.attributes.push({name: 'value', value: elementNode.value});
        }
      }

      if (elementNode.tagName === 'OPTION' && elementNode.parentElement.value === elementNode.value) {
        const nodeAttr = node.attributes.find(a => a.name === 'selected');
        if (!nodeAttr) {
          node.attributes.push({name: 'selected', value: ''});
        }
      }
      return node;
    }

    function getScriptNode(elementNode) {
      return {
        nodeType: Node.ELEMENT_NODE,
        nodeName: 'SCRIPT',
        attributes: nodeAttributes(elementNode)
          .map(key => ({
            name: elementNode.attributes[key].name,
            value: elementNode.attributes[key].value,
          }))
          .filter(attr => attr.name !== 'src'),
        childNodeIndexes: [],
      };
    }

    function getTextNode(elementNode) {
      return {
        nodeType: Node.TEXT_NODE,
        nodeValue: elementNode.nodeValue,
      };
    }

    function getDocNode(elementNode) {
      return {
        nodeType: Node.DOCUMENT_TYPE_NODE,
        nodeName: elementNode.nodeName,
      };
    }
  }

  var domNodesToCdt_1 = domNodesToCdt;

  function uniq(arr) {
    const result = [];
    new Set(arr).forEach(v => v && result.push(v));
    return result;
  }

  var uniq_1 = uniq;

  function aggregateResourceUrlsAndBlobs(resourceUrlsAndBlobsArr) {
    return resourceUrlsAndBlobsArr.reduce(
      ({resourceUrls: allResourceUrls, blobsObj: allBlobsObj}, {resourceUrls, blobsObj}) => ({
        resourceUrls: uniq_1(allResourceUrls.concat(resourceUrls)),
        blobsObj: Object.assign(allBlobsObj, blobsObj),
      }),
      {resourceUrls: [], blobsObj: {}},
    );
  }

  var aggregateResourceUrlsAndBlobs_1 = aggregateResourceUrlsAndBlobs;

  function makeGetResourceUrlsAndBlobs({processResource, aggregateResourceUrlsAndBlobs}) {
    return function getResourceUrlsAndBlobs({documents, urls, forceCreateStyle = false}) {
      return Promise.all(
        urls.map(url => processResource({url, documents, getResourceUrlsAndBlobs, forceCreateStyle})),
      ).then(resourceUrlsAndBlobsArr => aggregateResourceUrlsAndBlobs(resourceUrlsAndBlobsArr));
    };
  }

  var getResourceUrlsAndBlobs = makeGetResourceUrlsAndBlobs;

  function filterInlineUrl(absoluteUrl) {
    return /^(blob|https?):/.test(absoluteUrl);
  }

  var filterInlineUrl_1 = filterInlineUrl;

  function toUnAnchoredUri(url) {
    const m = url && url.match(/(^[^#]*)/);
    const res = (m && m[1]) || url;
    return (res && res.replace(/\?\s*$/, '')) || url;
  }

  var toUnAnchoredUri_1 = toUnAnchoredUri;

  var noop = () => {};

  function flat(arr) {
    return arr.reduce((flatArr, item) => flatArr.concat(item), []);
  }

  var flat_1 = flat;

  function makeProcessResource({
    fetchUrl,
    findStyleSheetByUrl,
    getCorsFreeStyleSheet,
    extractResourcesFromStyleSheet,
    extractResourcesFromSvg,
    sessionCache,
    cache = {},
    log = noop,
  }) {
    return function processResource({
      url,
      documents,
      getResourceUrlsAndBlobs,
      forceCreateStyle = false,
    }) {
      if (!cache[url]) {
        if (sessionCache && sessionCache.getItem(url)) {
          const resourceUrls = getDependencies(url);
          log('doProcessResource from sessionStorage', url, 'deps:', resourceUrls.slice(1));
          cache[url] = Promise.resolve({resourceUrls});
        } else {
          const now = Date.now();
          cache[url] = doProcessResource(url).then(result => {
            log('doProcessResource', `[${Date.now() - now}ms]`, url);
            return result;
          });
        }
      }
      return cache[url];

      function doProcessResource(url) {
        log('fetching', url);
        const now = Date.now();
        return fetchUrl(url)
          .catch(e => {
            if (probablyCORS(e)) {
              return {probablyCORS: true, url};
            } else {
              throw e;
            }
          })
          .then(({url, type, value, probablyCORS}) => {
            if (probablyCORS) {
              sessionCache && sessionCache.setItem(url, []);
              return {resourceUrls: [url]};
            }

            log('fetched', `[${Date.now() - now}ms]`, url);

            const thisBlob = {[url]: {type, value}};
            let dependentUrls;
            if (/text\/css/.test(type)) {
              let styleSheet = findStyleSheetByUrl(url, documents);
              if (styleSheet || forceCreateStyle) {
                const {corsFreeStyleSheet, cleanStyleSheet} = getCorsFreeStyleSheet(
                  value,
                  styleSheet,
                );
                dependentUrls = extractResourcesFromStyleSheet(corsFreeStyleSheet);
                cleanStyleSheet();
              }
            } else if (/image\/svg/.test(type)) {
              try {
                dependentUrls = extractResourcesFromSvg(value);
                forceCreateStyle = !!dependentUrls;
              } catch (e) {
                console.log('could not parse svg content', e);
              }
            }

            if (dependentUrls) {
              const absoluteDependentUrls = dependentUrls
                .map(resourceUrl => absolutizeUrl_1(resourceUrl, url.replace(/^blob:/, '')))
                .map(toUnAnchoredUri_1)
                .filter(filterInlineUrl_1);

              sessionCache && sessionCache.setItem(url, absoluteDependentUrls);

              return getResourceUrlsAndBlobs({
                documents,
                urls: absoluteDependentUrls,
                forceCreateStyle,
              }).then(({resourceUrls, blobsObj}) => ({
                resourceUrls,
                blobsObj: Object.assign(blobsObj, thisBlob),
              }));
            } else {
              sessionCache && sessionCache.setItem(url, []);
              return {blobsObj: thisBlob};
            }
          })
          .catch(err => {
            log('error while fetching', url, err);
            sessionCache && clearFromSessionStorage();
            return {};
          });
      }

      function probablyCORS(err) {
        const msg =
          err.message &&
          (err.message.includes('Failed to fetch') || err.message.includes('Network request failed'));
        const name = err.name && err.name.includes('TypeError');
        return msg && name;
      }

      function getDependencies(url) {
        const dependentUrls = sessionCache.getItem(url);
        return [url].concat(dependentUrls ? uniq_1(flat_1(dependentUrls.map(getDependencies))) : []);
      }

      function clearFromSessionStorage() {
        log('clearing from sessionStorage:', url);
        sessionCache.keys().forEach(key => {
          const dependentUrls = sessionCache.getItem(key);
          sessionCache.setItem(
            key,
            dependentUrls.filter(dep => dep !== url),
          );
        });
        log('cleared from sessionStorage:', url);
      }
    };
  }

  var processResource = makeProcessResource;

  function getUrlFromCssText(cssText) {
    const re = /url\((?!['"]?:)['"]?([^'")]*)['"]?\)/g;
    const ret = [];
    let result;
    while ((result = re.exec(cssText)) !== null) {
      ret.push(result[1]);
    }
    return ret;
  }

  var getUrlFromCssText_1 = getUrlFromCssText;

  function makeExtractResourcesFromSvg({parser, decoder, extractResourceUrlsFromStyleTags}) {
    return function(svgArrayBuffer) {
      const decooder = decoder || new TextDecoder('utf-8');
      const svgStr = decooder.decode(svgArrayBuffer);
      const domparser = parser || new DOMParser();
      const doc = domparser.parseFromString(svgStr, 'image/svg+xml');

      const srcsetUrls = Array.from(doc.querySelectorAll('img[srcset]'))
        .map(srcsetEl =>
          srcsetEl
            .getAttribute('srcset')
            .split(', ')
            .map(str => str.trim().split(/\s+/)[0]),
        )
        .reduce((acc, urls) => acc.concat(urls), []);

      const srcUrls = Array.from(doc.querySelectorAll('img[src]')).map(srcEl =>
        srcEl.getAttribute('src'),
      );

      const fromHref = Array.from(doc.querySelectorAll('image,use,link[rel="stylesheet"]')).map(
        e => e.getAttribute('href') || e.getAttribute('xlink:href'),
      );
      const fromObjects = Array.from(doc.getElementsByTagName('object')).map(e =>
        e.getAttribute('data'),
      );
      const fromStyleTags = extractResourceUrlsFromStyleTags(doc, false);
      const fromStyleAttrs = urlsFromStyleAttrOfDoc(doc);

      return srcsetUrls
        .concat(srcUrls)
        .concat(fromHref)
        .concat(fromObjects)
        .concat(fromStyleTags)
        .concat(fromStyleAttrs)
        .filter(u => u[0] !== '#');
    };
  }

  function urlsFromStyleAttrOfDoc(doc) {
    return flat_1(
      Array.from(doc.querySelectorAll('*[style]'))
        .map(e => e.style.cssText)
        .map(getUrlFromCssText_1)
        .filter(Boolean),
    );
  }

  var makeExtractResourcesFromSvg_1 = makeExtractResourcesFromSvg;

  /* global window */

  function fetchUrl(url, fetch = window.fetch) {
    // Why return a `new Promise` like this? Because people like Atlassian do horrible things.
    // They monkey patched window.fetch, and made it so it throws a synchronous exception if the route is not well known.
    // Returning a new Promise guarantees that `fetchUrl` is the async function that it declares to be.
    return new Promise((resolve, reject) => {
      return fetch(url, {cache: 'force-cache', credentials: 'same-origin'})
        .then(resp =>
          resp.status === 200
            ? resp.arrayBuffer().then(buff => ({
                url,
                type: resp.headers.get('Content-Type'),
                value: buff,
              }))
            : Promise.reject(`bad status code ${resp.status}`),
        )
        .then(resolve)
        .catch(err => reject(err));
    });
  }

  var fetchUrl_1 = fetchUrl;

  function sanitizeAuthUrl(urlStr) {
    const url = new URL(urlStr);
    if (url.username) {
      url.username = '';
    }
    if (url.password) {
      url.password = '';
    }
    return url.href;
  }

  var sanitizeAuthUrl_1 = sanitizeAuthUrl;

  function makeFindStyleSheetByUrl({styleSheetCache}) {
    return function findStyleSheetByUrl(url, documents) {
      const allStylesheets = flat_1(documents.map(d => Array.from(d.styleSheets)));
      return (
        styleSheetCache[url] ||
        allStylesheets.find(styleSheet => {
          const styleUrl = styleSheet.href && toUnAnchoredUri_1(styleSheet.href);
          return styleUrl && sanitizeAuthUrl_1(styleUrl) === url;
        })
      );
    };
  }

  var findStyleSheetByUrl = makeFindStyleSheetByUrl;

  function makeExtractResourcesFromStyleSheet({styleSheetCache, CSSRule = window.CSSRule}) {
    return function extractResourcesFromStyleSheet(styleSheet) {
      const urls = uniq_1(
        Array.from(styleSheet.cssRules || []).reduce((acc, rule) => {
          const getRuleUrls = {
            [CSSRule.IMPORT_RULE]: () => {
              if (rule.styleSheet) {
                styleSheetCache[rule.styleSheet.href] = rule.styleSheet;
                return rule.href;
              }
            },
            [CSSRule.FONT_FACE_RULE]: () => getUrlFromCssText_1(rule.cssText),
            [CSSRule.SUPPORTS_RULE]: () => extractResourcesFromStyleSheet(rule),
            [CSSRule.MEDIA_RULE]: () => extractResourcesFromStyleSheet(rule),
            [CSSRule.STYLE_RULE]: () => {
              let rv = [];
              for (let i = 0, ii = rule.style.length; i < ii; i++) {
                const urls = getUrlFromCssText_1(rule.style.getPropertyValue(rule.style[i]));
                rv = rv.concat(urls);
              }
              return rv;
            },
          }[rule.type];

          const urls = (getRuleUrls && getRuleUrls()) || [];
          return acc.concat(urls);
        }, []),
      );
      return urls.filter(u => u[0] !== '#');
    };
  }

  var extractResourcesFromStyleSheet = makeExtractResourcesFromStyleSheet;

  function extractResourceUrlsFromStyleAttrs(cdt) {
    return cdt.reduce((acc, node) => {
      if (node.nodeType === 1) {
        const styleAttr =
          node.attributes && node.attributes.find(attr => attr.name.toUpperCase() === 'STYLE');

        if (styleAttr) acc = acc.concat(getUrlFromCssText_1(styleAttr.value));
      }
      return acc;
    }, []);
  }

  var extractResourceUrlsFromStyleAttrs_1 = extractResourceUrlsFromStyleAttrs;

  function makeExtractResourceUrlsFromStyleTags(extractResourcesFromStyleSheet) {
    return function extractResourceUrlsFromStyleTags(doc, onlyDocStylesheet = true) {
      return uniq_1(
        Array.from(doc.querySelectorAll('style')).reduce((resourceUrls, styleEl) => {
          const styleSheet = onlyDocStylesheet
            ? Array.from(doc.styleSheets).find(styleSheet => styleSheet.ownerNode === styleEl)
            : styleEl.sheet;
          return styleSheet
            ? resourceUrls.concat(extractResourcesFromStyleSheet(styleSheet))
            : resourceUrls;
        }, []),
      );
    };
  }

  var extractResourceUrlsFromStyleTags = makeExtractResourceUrlsFromStyleTags;

  function createTempStylsheet(cssArrayBuffer) {
    const cssText = new TextDecoder('utf-8').decode(cssArrayBuffer);
    const head = document.head || document.querySelectorAll('head')[0];
    const style = document.createElement('style');
    style.type = 'text/css';
    style.setAttribute('data-desc', 'Applitools tmp variable created by DOM SNAPSHOT');
    head.appendChild(style);

    // This is required for IE8 and below.
    if (style.styleSheet) {
      style.styleSheet.cssText = cssText;
    } else {
      style.appendChild(document.createTextNode(cssText));
    }
    return style.sheet;
  }

  var createTempStyleSheet = createTempStylsheet;

  function getCorsFreeStyleSheet(cssArrayBuffer, styleSheet) {
    let corsFreeStyleSheet;
    if (styleSheet) {
      try {
        styleSheet.cssRules;
        corsFreeStyleSheet = styleSheet;
      } catch (e) {
        console.log(
          `[dom-snapshot] could not access cssRules for ${styleSheet.href} ${e}\ncreating temp style for access.`,
        );
        corsFreeStyleSheet = createTempStyleSheet(cssArrayBuffer);
      }
    } else {
      corsFreeStyleSheet = createTempStyleSheet(cssArrayBuffer);
    }

    return {corsFreeStyleSheet, cleanStyleSheet};

    function cleanStyleSheet() {
      if (corsFreeStyleSheet !== styleSheet) {
        corsFreeStyleSheet.ownerNode.parentNode.removeChild(corsFreeStyleSheet.ownerNode);
      }
    }
  }

  var getCorsFreeStyleSheet_1 = getCorsFreeStyleSheet;

  function base64ToArrayBuffer(base64) {
    var binary_string = window.atob(base64);
    var len = binary_string.length;
    var bytes = new Uint8Array(len);
    for (var i = 0; i < len; i++) {
      bytes[i] = binary_string.charCodeAt(i);
    }
    return bytes.buffer;
  }

  var base64ToArrayBuffer_1 = base64ToArrayBuffer;

  function buildCanvasBlobs(canvasElements) {
    return canvasElements.map(({url, element}) => {
      const data = element.toDataURL('image/png');
      const value = base64ToArrayBuffer_1(data.split(',')[1]);
      return {url, type: 'image/png', value};
    });
  }

  var buildCanvasBlobs_1 = buildCanvasBlobs;

  function extractFrames(documents = [document]) {
    const iframes = flat_1(
      documents.map(d => Array.from(d.querySelectorAll('iframe[src]:not([src=""])'))),
    );

    return iframes.filter(f => isAccessibleFrame_1(f) && !isInlineFrame_1(f)).map(f => f.contentDocument);
  }

  var extractFrames_1 = extractFrames;

  const getBaesUrl = function(doc) {
    const baseUrl = doc.querySelectorAll('base')[0] && doc.querySelectorAll('base')[0].href;
    if (baseUrl && isUrl(baseUrl)) {
      return baseUrl;
    }
  };

  function isUrl(url) {
    return url && !/^(about:blank|javascript:void|blob:)/.test(url);
  }

  var getBaseUrl = getBaesUrl;

  function toUriEncoding(url) {
    const result =
      (url &&
        url.replace(/(\\[0-9a-fA-F]{1,6}\s?)/g, s => {
          const int = parseInt(s.substr(1).trim(), 16);
          return String.fromCodePoint(int);
        })) ||
      url;
    return result;
  }

  var toUriEncoding_1 = toUriEncoding;

  function makeLog(referenceTime) {
    return function log() {
      const args = ['[dom-snapshot]', `[+${Date.now() - referenceTime}ms]`].concat(
        Array.from(arguments),
      );
      console.log.apply(console, args);
    };
  }

  var log = makeLog;

  const RESOURCE_STORAGE_KEY = '__process_resource';

  function makeSessionCache({log, sessionStorage}) {
    let sessionStorageCache;
    try {
      sessionStorage = sessionStorage || window.sessionStorage;
      const sessionStorageCacheStr = sessionStorage.getItem(RESOURCE_STORAGE_KEY);
      sessionStorageCache = sessionStorageCacheStr ? JSON.parse(sessionStorageCacheStr) : {};
    } catch (ex) {
      log('error creating session cache', ex);
    }

    return {
      getItem,
      setItem,
      keys,
      persist,
    };

    function getItem(key) {
      if (sessionStorageCache) {
        return sessionStorageCache[key];
      }
    }

    function setItem(key, value) {
      if (sessionStorageCache) {
        log('saving to in-memory sessionStorage, key:', key, 'value:', value);
        sessionStorageCache[key] = value;
      }
    }

    function keys() {
      if (sessionStorageCache) {
        return Object.keys(sessionStorageCache);
      } else {
        return [];
      }
    }

    function persist() {
      if (sessionStorageCache) {
        sessionStorage.setItem(RESOURCE_STORAGE_KEY, JSON.stringify(sessionStorageCache));
      }
    }
  }

  var sessionCache = makeSessionCache;

  function processPage(doc = document, {showLogs, useSessionCache, dontFetchResources} = {}) {
    const log$$1 = showLogs ? log(Date.now()) : noop;
    log$$1('processPage start');
    const sessionCache$$1 = useSessionCache && sessionCache({log: log$$1});
    const styleSheetCache = {};
    const extractResourcesFromStyleSheet$$1 = extractResourcesFromStyleSheet({styleSheetCache});
    const findStyleSheetByUrl$$1 = findStyleSheetByUrl({styleSheetCache});
    const extractResourceUrlsFromStyleTags$$1 = extractResourceUrlsFromStyleTags(
      extractResourcesFromStyleSheet$$1,
    );

    const extractResourcesFromSvg = makeExtractResourcesFromSvg_1({extractResourceUrlsFromStyleTags: extractResourceUrlsFromStyleTags$$1});
    const processResource$$1 = processResource({
      fetchUrl: fetchUrl_1,
      findStyleSheetByUrl: findStyleSheetByUrl$$1,
      getCorsFreeStyleSheet: getCorsFreeStyleSheet_1,
      extractResourcesFromStyleSheet: extractResourcesFromStyleSheet$$1,
      extractResourcesFromSvg,
      absolutizeUrl: absolutizeUrl_1,
      log: log$$1,
      sessionCache: sessionCache$$1,
    });

    const getResourceUrlsAndBlobs$$1 = getResourceUrlsAndBlobs({
      processResource: processResource$$1,
      aggregateResourceUrlsAndBlobs: aggregateResourceUrlsAndBlobs_1,
    });

    return doProcessPage(doc).then(result => {
      log$$1('processPage end');
      result.scriptVersion = '3.2.2';
      return result;
    });

    function doProcessPage(doc, pageUrl = doc.location.href) {
      const baseUrl = getBaseUrl(doc) || pageUrl;
      const {cdt, docRoots, canvasElements, inlineFrames} = domNodesToCdt_1(doc, baseUrl);

      const linkUrls = flat_1(docRoots.map(extractLinks_1));
      const styleTagUrls = flat_1(docRoots.map(extractResourceUrlsFromStyleTags$$1));
      const absolutizeThisUrl = getAbsolutizeByUrl(baseUrl);
      const urls = uniq_1(
        Array.from(linkUrls)
          .concat(Array.from(styleTagUrls))
          .concat(extractResourceUrlsFromStyleAttrs_1(cdt)),
      )
        .map(toUriEncoding_1)
        .map(absolutizeThisUrl)
        .map(toUnAnchoredUri_1)
        .filter(filterInlineUrlsIfExisting);

      const resourceUrlsAndBlobsPromise = dontFetchResources
        ? Promise.resolve({resourceUrls: urls, blobsObj: {}})
        : getResourceUrlsAndBlobs$$1({documents: docRoots, urls}).then(result => {
            sessionCache$$1 && sessionCache$$1.persist();
            return result;
          });
      const canvasBlobs = buildCanvasBlobs_1(canvasElements);
      const frameDocs = extractFrames_1(docRoots);

      const processFramesPromise = frameDocs.map(f =>
        doProcessPage(f, f.defaultView.frameElement.src),
      );
      const processInlineFramesPromise = inlineFrames.map(({element, url}) =>
        doProcessPage(element.contentDocument, url),
      );

      const srcAttr =
        doc.defaultView &&
        doc.defaultView.frameElement &&
        doc.defaultView.frameElement.getAttribute('src');

      return Promise.all(
        [resourceUrlsAndBlobsPromise].concat(processFramesPromise).concat(processInlineFramesPromise),
      ).then(function(resultsWithFrameResults) {
        const {resourceUrls, blobsObj} = resultsWithFrameResults[0];
        const framesResults = resultsWithFrameResults.slice(1);
        return {
          cdt,
          url: pageUrl,
          srcAttr,
          resourceUrls: resourceUrls.map(url => url.replace(/^blob:/, '')),
          blobs: blobsObjToArray(blobsObj).concat(canvasBlobs),
          frames: framesResults,
        };
      });
    }
  }

  function getAbsolutizeByUrl(url) {
    return function(someUrl) {
      try {
        return absolutizeUrl_1(someUrl, url);
      } catch (err) {
        // can't do anything with a non-absolute url
      }
    };
  }

  function blobsObjToArray(blobsObj) {
    return Object.keys(blobsObj).map(blobUrl =>
      Object.assign(
        {
          url: blobUrl.replace(/^blob:/, ''),
        },
        blobsObj[blobUrl],
      ),
    );
  }

  function filterInlineUrlsIfExisting(absoluteUrl) {
    return absoluteUrl && filterInlineUrl_1(absoluteUrl);
  }

  var processPage_1 = processPage;

  function processPageAndSerialize() {
    return processPage_1.apply(this, arguments).then(serializeFrame);
  }

  function serializeFrame(frame) {
    frame.blobs = frame.blobs.map(({url, type, value}) => ({
      url,
      type,
      value: arrayBufferToBase64_1(value),
    }));
    frame.frames.forEach(serializeFrame);
    return frame;
  }

  var processPageAndSerialize_1 = processPageAndSerialize;

  var processPageAndSerializePoll = pollify(processPageAndSerialize_1);

  return processPageAndSerializePoll;

}());

  return processPageAndSerializePoll.apply(this, arguments);
}