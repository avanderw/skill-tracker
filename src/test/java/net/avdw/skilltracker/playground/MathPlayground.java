package net.avdw.skilltracker.playground;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.Random;

public class MathPlayground {

    public static void main(String[] args) {
        Random random = new Random();
        DescriptiveStatistics stats = new DescriptiveStatistics();

        // Add the data from the array
        for (int i = 0; i < 100_000; i++) {
            stats.addValue(random.nextGaussian());
        }

        // Compute some statistics
        double mean = stats.getMean();
        double median = stats.getPercentile(50);
        double oneStddev = stats.getPercentile(84.1);
        double twoStddev = stats.getPercentile(97.7);
        double threeStddev = stats.getPercentile(99.8);

        System.out.println(String.format("Stddev = %ss", stats.getStandardDeviation()));
        System.out.println();
        System.out.println(String.format("50.0%% => %ss", median));
        System.out.println(String.format("84.1%% => %ss", oneStddev));
        System.out.println(String.format("97.7%% => %ss", twoStddev));
        System.out.println(String.format("99.8%% => %ss", threeStddev));
        System.out.println(stats.getSkewness());
        System.out.println(stats.getKurtosis());
        System.out.println();
        System.out.println(stats);
    }
}
