package com.applitools.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    /**
     * Stringify the string for json format
     */
    public static String stringify(String s) {
        if (s == null || s.isEmpty())
        {
            return "";
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String cleaned;
        try {
            cleaned = objectMapper.writeValueAsString(s);
            // Remove quotes
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        } catch (JsonProcessingException e) {
            cleaned = "";
        }

        return cleaned;
    }
}
