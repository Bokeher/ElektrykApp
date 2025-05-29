package com.example.planlekcji.utils;

import java.util.HashMap;
import java.util.Map;

public class BoyerMooreSearch {
    private final Map<Character, Integer> lastOccurrence = new HashMap<>();

    private void preprocessPattern(String pattern) {
        lastOccurrence.clear();
        for (int i = 0; i < pattern.length(); i++) {
            lastOccurrence.put(pattern.charAt(i), i);
        }
    }

    public boolean search(String text, String pattern) {
        if (pattern.isEmpty() || text.isEmpty() || pattern.length() > text.length()) {
            return false;
        }

        preprocessPattern(pattern);
        int n = text.length();
        int m = pattern.length();
        int i = 0;

        while (i <= n - m) {
            int j = m - 1;

            while (j >= 0 && pattern.charAt(j) == text.charAt(i + j)) {
                j--;
            }

            if (j < 0) {
                return true;
            }

            int lastOccur = lastOccurrence.getOrDefault(text.charAt(i + j), -1);
            i += Math.max(1, j - lastOccur);
        }

        return false;
    }
}
