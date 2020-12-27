package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.Logger;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.Type;
import com.applitools.eyes.visualgrid.model.CssTokenizer;
import com.applitools.utils.GeneralUtils;
import org.w3c.dom.css.CSSImportRule;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleRule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CssTreeNode {
    enum CssRuleType {
        IMPORT,
        STYLE
    }

    static class CssRule {
        private final CssRuleType type;
        private final String value;

        CssRule(CssRuleType type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    private final String css;
    private final Map<String, CssTreeNode> childNodes = new HashMap<>();
    private final List<CssRule> cssRules = new ArrayList<>();

    CssTreeNode(String css) {
        this.css = css;
    }

    List<String> getImportedUrls() {
        List<String> urls = new ArrayList<>();
        for (CssRule rule: cssRules) {
            if (rule.type.equals(CssRuleType.IMPORT)) {
                urls.add(rule.value);
            }
        }

        return urls;
    }

    void addChildNode(String uri, CssTreeNode node) {
        childNodes.put(uri, node);
    }

    void parse(Logger logger) {
        if (css == null || css.isEmpty()) {
            return;
        }

        CSSRuleList ruleList;
        try {
            ruleList = CssTokenizer.getCssRules(css);
        } catch (IOException e) {
            GeneralUtils.logExceptionStackTrace(logger, Stage.CHECK, Type.DOM_SCRIPT, e);
            return;
        }

        for (int i = 0; i < ruleList.getLength(); i++) {
            CSSRule rule = ruleList.item(i);
            if (rule instanceof CSSImportRule) {
                String url = ((CSSImportRule) rule).getHref();
                cssRules.add(new CssRule(CssRuleType.IMPORT, url));
            }
            if (rule instanceof CSSStyleRule) {
                cssRules.add(new CssRule(CssRuleType.STYLE, rule.toString()));
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (CssRule rule : cssRules) {
            switch (rule.type) {
                case IMPORT:
                    sb.append(childNodes.get(rule.value).toString());
                    break;
                case STYLE:
                    sb.append(rule.value);
                    break;
                default:
                    throw new UnsupportedOperationException(String.format("No such type %s", rule.type));
            }
        }

        return sb.toString();
    }
}
