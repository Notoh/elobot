package io.notoh.elobot.rank;

public final class Calculator {

    private Calculator() {
        //limit scope
    }

    public static double periodDeviation(double oldDeviation) {
        return Math.min(350.0, Math.sqrt(oldDeviation*oldDeviation + 34.6*34.6));
    }

    public static double calcNewDeviation(double oldDeviation, double d) {
        return Math.max(1/(Math.sqrt((1/(oldDeviation*oldDeviation)) + 1/d)), 30.0);
    }

    public static double gamePct(int won, int lost) {
        return ((double) won / (double) (won+lost));
    }

    /*
    As described by Glicko, with a ln curve to adjust for individual performance
     */
    public static double[] newRating(int oldRating, double deviation, int opposedRating, double outcome,
                                double performance) {
        double q = 0.00575646273;
        double g = 1/(Math.sqrt(1 + (3*q*q*deviation*deviation)/(Math.PI*Math.PI)));
        double e = 1/(1 + Math.pow(10, (g*(oldRating-opposedRating))/-400));
        double d = 1/(q*q*g*g*e*(1-e));
        double perf = 0.5 + Math.pow(0.5, (outcome > 0.5 ? 1/Math.min(performance, 1.65) : Math.min(performance,
                1.65)));
        double r = oldRating + ((((q/(1/(deviation*deviation) + 1/(d)))*g*(outcome-e))) * perf);
        return new double[]{r, calcNewDeviation(deviation, d)};
    }

    public static int calcAvg(int[] ratings) {
        int ret = 0;
        for(int i : ratings) {
            ret += i;
        }
        return ret/ratings.length;
    }



}
