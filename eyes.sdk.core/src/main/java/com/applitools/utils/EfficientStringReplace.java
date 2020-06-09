package com.applitools.utils;

import java.util.Map;

public class EfficientStringReplace {
    
    public static String efficientStringReplace(String refIdOpenToken, String refIdCloseToken, String input, Map<String, String> replacements) {
        int resultBufferSize = input.length();
        for (String rep : replacements.values()) {
            resultBufferSize += rep.length();
        }
        StringBuilder result = new StringBuilder(resultBufferSize);
        StringBuilder refId = new StringBuilder();
        int inLen = input.length();
        char[] refIdOpenChars = refIdOpenToken.toCharArray();
        int refOpenTokenLen = refIdOpenChars.length;
        char[] refIdCloseChars = refIdCloseToken.toCharArray();
        int refCloseTokenLen = refIdCloseChars.length;
        START:
        for (int i = 0; i < inLen; ++i) {

            boolean refTokenEncountered = input.charAt(i) == refIdOpenChars[0];
            if (refTokenEncountered) {
                for (int j = 1; j < refOpenTokenLen; ++j) {
                    if (input.charAt(i + j) != refIdOpenChars[j]) {
                        refTokenEncountered = false;
                        break;
                    }
                }
                if (refTokenEncountered) {
                    refId.setLength(0);
                    for (i += refOpenTokenLen; i < inLen; ++i) {
                        boolean refEndTokenEncountered = input.charAt(i) == refIdCloseChars[0];
                        if (refEndTokenEncountered) {
                            for (int j = 1; j < refCloseTokenLen; ++j) {
                                if (input.charAt(i + j) != refIdCloseChars[j]) {
                                    refEndTokenEncountered = false;
                                    break;
                                }
                            }
                            if (refEndTokenEncountered) {
                                String rep = replacements.get(refId.toString());
                                if (rep != null) {
                                    result.append(rep);
                                    i += refCloseTokenLen-1;
                                    continue START;
                                }
                            }
                        }
                        if (!refEndTokenEncountered) {
                            refId.append(input.charAt(i));
                        }
                    }

                }
            }

            if (!refTokenEncountered) {
                result.append(input.charAt(i));
            }
        }
        return result.toString();
    }

    public static String CleanForJSON(String s)
    {

        if (s == null || s.length() == 0)
        {
            return "";
        }

        char c = '\0';
        int i;
        int len = s.length();
        StringBuilder sb = new StringBuilder(len + 4);
        String t;

        for (i = 0; i < len; i += 1)
        {
            c = s.charAt(i);
            switch (c)
            {
                case '\\':
                case '"':
                case '/':
                    sb.append('\\');
                    sb.append(c);
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                default:
                    if (c < ' ')
                    {
                        t = String.format("%04x", c);
                        sb.append("\\u").append(t.substring(t.length() - 4));
                    }
                    else
                    {
                        sb.append(c);
                    }
                    break;
            }
        }
        return sb.toString();
    }
}
