package io.notoh.elobot.rank;

import java.util.List;
import java.util.function.Function;

import static java.lang.Math.*;


/*
This entire class is me transcribing scala, very ugly and i should just use my scala implementation tbh
 */
public class Glicko2 {

    public static double gamePct(int won, int lost) {
        if(won > lost) {
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
            } else {
                return 0.65;
            }
        }
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
        } else {
            return 0.35;
        }
    }

    private static final double conversion = 173.7178;
    private static final double tau = 0.5;

    public static final double newPlayerVolatility = 0.06;
    public static final double newPlayerRating = 1500;
    public static final double newPlayerDeviation = 300;

    public static double g(double deviation) {
        return 1.0 / sqrt(1.0 + 3 * pow(deviation, 2) / pow(PI, 2));
    }

    public static double E(double rating, double opponentRating, double opponentDeviation) {
        return 1.0 / (1.0 + exp(-g(opponentDeviation) * (rating - opponentRating)));
    }

    public static double[] glicko1ToGlicko2(double rating, double deviation, double volatility) {
        return new double[]{(rating - newPlayerRating) / conversion, deviation / conversion, volatility};
    }

    public static double[] glicko1ToGlicko2(double[] glicko1) {
        return glicko1ToGlicko2(glicko1[0], glicko1[1], glicko1[2]);
    }

    public static double[] glicko2ToGlicko1(double[] glicko2) {
        return new double[]{glicko2[0] * conversion + newPlayerRating, glicko2[1] * conversion, glicko2[2]};
    }

    public static double estimatedVariance(double rating, double[] opponentGlicko2) {
        return 1.0 / (pow(g(opponentGlicko2[1]), 2) * E(rating, opponentGlicko2[0], opponentGlicko2[1]) * (1-E(rating, opponentGlicko2[0], opponentGlicko2[1])));
    }

    public static double estimatedImprovement(double rating, double[] opponentGlicko2, double result) {
        return estimatedVariance(rating, opponentGlicko2) * (g(opponentGlicko2[1]) * (result - E(rating, opponentGlicko2[0], opponentGlicko2[1])));
    }

    public static double[] calculateNewRating(double[] glicko2, double[] opponentGlicko2, double result) {
        final double convergence = 0.000001;
        final double v = estimatedVariance(glicko2[0], opponentGlicko2);
        final double i = estimatedImprovement(glicko2[0], opponentGlicko2, result);
        final double deviation = glicko2[1];
        final double volatility = glicko2[2];

        final double a = log(pow(volatility, 2));
        Function<Double, Double> f = x -> (exp(x) * (pow(i, 2) - pow(deviation, 2) - v - exp(x))) /
                (2.0 * pow(pow(deviation, 2) + v + exp(x), 2)) -
                (x - a) / pow(tau, 2);

        double A = a;
        double B;
        if(pow(i, 2) > pow(deviation, 2) + v) {
            B = log(pow(i, 2) - pow(deviation, 2) - v);
        } else {
            int k = 1;
            while(f.apply(a - k * tau) < 0) {
                k++;
            }
            B =  a - k * tau;
        }

        double fA = f.apply(A);
        double fB = f.apply(B);

        while(abs(B - A) > convergence) {
            double C = A + (A - B) * fA / (fB - fA);
            double fC = f.apply(C);
            if(fC * fB < 0) {
                A = B;
                fA = fB;
            } else {
                fA /= 2;
            }
            B = C;
            fB = fC;
        }

        double newVolatility = exp(A / 2);
        double newDeviation = 1.0 / sqrt(1.0 / pow(sqrt(pow(deviation, 2) + pow(newVolatility, 2)), 2) + 1.0 / v);
        double newRating = glicko2[0] + pow(newDeviation, 2) * (g(opponentGlicko2[1]) * (result - E(glicko2[0], opponentGlicko2[0], opponentGlicko2[1])));

        return new double[]{newRating, newDeviation, newVolatility};
    }

    public static double[] teamGlicko2(List<List<Double>> glicko2s) {
        //I know averaging RDs / volatilities of a team is stupid when they are logarithmic but if its a problem ill
        // change it
        double avgRating = 0;
        double avgRd = 0;
        double avgVolatility = 0;
        for(List<Double> glicko2 : glicko2s) {
           avgRating += glicko2.get(0);
           avgRd += glicko2.get(1);
           avgVolatility += glicko2.get(2);
        }
        avgRating /= 5;
        avgRd /= 5;
        avgVolatility /= 5;
        return new double[]{avgRating, avgRd, avgVolatility};
    }

    public static double updateDeviation(double deviation, double volatility) {
        deviation = deviation / conversion;
        for(int i = 0; i < 5; i++) {
            deviation = sqrt(pow(deviation, 2) + pow(volatility, 2));
        }
        return Math.min(newPlayerDeviation, deviation);
    }

    
    public static double[] getComparisonRatings(double[] playerGlicko1, double teamGlicko1Ratings,
                                                double[] equivPlayerGlicko1, double opponentGlicko1Ratings) {
        double selfRating = 1500 + playerGlicko1[0] - opponentGlicko1Ratings;
        double opponentRating = 1500 + equivPlayerGlicko1[0] - teamGlicko1Ratings;
        return new double[]{selfRating, playerGlicko1[1], opponentRating, equivPlayerGlicko1[1]};
    }
}
