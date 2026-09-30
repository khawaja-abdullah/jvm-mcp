package dev.jvmmcp.core.spring;

import java.util.regex.Pattern;

/**
 * Utility for case-insensitive glob pattern matching (*, ?).
 */
public final class GlobMatcher {

    private GlobMatcher() {}

    public static boolean matches(String text, String globPattern) {
        if (globPattern == null || globPattern.isBlank() || "*".equals(globPattern)) {
            return true;
        }
        if (text == null) {
            return false;
        }
        Pattern pattern = compileGlob(globPattern);
        return pattern.matcher(text).matches();
    }

    public static Pattern compileGlob(String globPattern) {
        StringBuilder regex = new StringBuilder("^");
        for (int i = 0; i < globPattern.length(); i++) {
            char c = globPattern.charAt(i);
            switch (c) {
                case '*' -> regex.append(".*");
                case '?' -> regex.append(".");
                case '.', '(', ')', '[', ']', '{', '}', '^', '$', '+', '|', '\\' -> {
                    regex.append('\\').append(c);
                }
                default -> regex.append(c);
            }
        }
        regex.append("$");
        return Pattern.compile(regex.toString(), Pattern.CASE_INSENSITIVE);
    }
}
