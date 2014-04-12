package com.sloth.drive.app;

/**
 * Created by arithehun on 4/12/14.
 */
public class Constants {
    public static enum Strings {
        PREF_PRICE_KEY("com.sloth.drive.app.pref_price"),
        PREF_TIME_KEY("com.sloth.drive.app.pref_time");

        /**
         * Construct a string constant
         * @param value The value of the string
         */
        private Strings(String value) {
            this.value = value;
        }

        /**
         * The string value
         */
        private String value;

        /**
         * Get the string value
         * @return The string value
         */
        public String getValue() {
            return value;
        }
    }

    public static enum Ints {
        ;

        /**
         * Construct a integer constant
         * @param value The value of the integer
         */
        private Ints(int value) {
            this.value = value;
        }

        /**
         * The integer value
         */
        private int value;

        /**
         * Get the integer value
         * @return The integer value
         */
        public int getValue() {
            return value;
        }
    }
}
