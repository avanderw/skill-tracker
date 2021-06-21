package net.avdw.skilltracker;

public class StringFormat {
    public static String camelCaseToTitleCase(String text) {
        String replace = text
                .replaceAll("_", " ")
                .replaceAll("([a-z])([A-Z][a-z])", "$1 $2")
                .replaceAll("([A-Z][a-z])([A-Z])", "$1 $2")
                .replaceAll("([a-z])([A-Z]+[a-z])", "$1 $2")
                .replaceAll("([A-Z]+)([A-Z][a-z][a-z])", "$1 $2")
                .replaceAll("([a-z]+)([A-Z0-9]+)", "$1 $2")

                // Note: the next regex includes a special case to exclude plurals of acronyms, e.g. "ABCs"
                .replaceAll("([A-Z]+)([A-Z][a-rt-z][a-z]*)", "$1 $2")
                .replaceAll("([0-9])([A-Z][a-z]+)", "$1 $2")

                // Note: the next two regexes use {2,} instead of + to add space on phrases like Room26A and 26ABCs but not on phrases like R2D2 and C3PO"
                .replaceAll("([A-Z]{2,})([0-9]{2,})", "$1 $2")
                .replaceAll("([0-9]{2,})([A-Z]{2,})", "$1 $2")
                .trim();

        return Character.toUpperCase(replace.charAt(0)) + replace.substring(1);
    }
}
