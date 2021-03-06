package io.notoh.elobot;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Util {

    public static String TOKEN;
    public static String DB_USER;
    public static String DB_PASS;
    public static String DB_URL;

    public static final String PREFIX = "-";
    public static final int MESSAGE_CACHE_MAX = 200;
    public static final String UPDATE_ROLE = "827513306788266024";
    public static final String MOD_ROLE = "827513306788266024";
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.###");
    public static final String CHANNEL_ID = "813795820792971275";

    public static final List<String> validCaptains = new ArrayList<>();

    static {
        DECIMAL_FORMAT.setRoundingMode(RoundingMode.CEILING);
        validCaptains.addAll(Arrays.asList("n0toh","crutan","19in","plusoneben","zxmonster","mr_spider_rider","usleless","storytale","_j4m",
                "27hr","toolchests","narlugaa","subaki2k","frqk","wwap","imsadaf","pyroaura","uktarik","impqkt",
                "klammbud","canadianave","dillaware","_icethea_","zhal","xephyisrip","odinleader","ssupersaiyan",
                "kellyfornia","freealcohol", "frqk", "drahyrt"));
    }

    public static double average(double[] avg) {
        double i = 0;
        for(double d : avg) {
            i += d;
        }
        return i / avg.length;
    }
}
