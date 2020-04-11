package io.notoh.elobot;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Util {

    public static String TOKEN;
    public static String DB_USER;
    public static String DB_PASS;
    public static String DB_URL;

    public static final String PREFIX = "-";
    public static final int MESSAGE_CACHE_MAX = 200;
    public static final String UPDATE_ROLE = "696841987927834655";
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#####");

    static {
        DECIMAL_FORMAT.setRoundingMode(RoundingMode.CEILING);
    }
}
