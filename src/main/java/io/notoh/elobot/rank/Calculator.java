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
        if(won == 9 && lost < 9) {
            if(lost <= 3) {
                return 1.0;
            } else if(lost == 4) {
                return 0.9;
            } else if(lost == 5) {
                return 0.8;
            } else if(lost == 6) {
                return 0.75;
            } else if(lost == 7) {
                return 0.7;
            }
        }
        if(lost == 9 && won < 9) {
            if(won <= 3) {
                return 0;
            } else if(won == 4) {
                return 0.1;
            } else if(won == 5) {
                return 0.2;
            } else if(won == 6) {
                return 0.25;
            } else if(won == 7) {
                return 0.3;
            }
        }
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
        double perf = 0.5 + Math.pow(0.5, (outcome > 0.5 ? 1/performance : performance));
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

    public static int calcAvg(Player[] players) {
        int[] ratings = new int[5];
        for(int i = 0; i < 5; i++) {
            ratings[i] = players[i].getRating();
        }
        return calcAvg(ratings);
    }


}
