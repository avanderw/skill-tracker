package net.avdw.skilltracker.match;

import com.google.inject.Inject;

import java.util.ResourceBundle;

public class GroupQualityResolver {
    private final ResourceBundle resourceBundle;

    @Inject
    GroupQualityResolver(@MatchScope final ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    String resolve(final int qualityMetric) {
        int lowestThreshold = Integer.parseInt(resourceBundle.getString(MatchBundleKey.QUALITY_LOWEST_THRESHOLD));
        int lowThreshold = Integer.parseInt(resourceBundle.getString(MatchBundleKey.QUALITY_LOW_THRESHOLD));
        int medThreshold = Integer.parseInt(resourceBundle.getString(MatchBundleKey.QUALITY_MED_THRESHOLD));
        String summary = resourceBundle.getString(MatchBundleKey.QUALITY_HI_SUMMARY);
        if (qualityMetric < medThreshold) {
            summary = resourceBundle.getString(MatchBundleKey.QUALITY_MED_SUMMARY);
        }
        if (qualityMetric < lowThreshold) {
            summary = resourceBundle.getString(MatchBundleKey.QUALITY_LOW_SUMMARY);
        }
        if (qualityMetric < lowestThreshold) {
            summary = resourceBundle.getString(MatchBundleKey.QUALITY_LOWEST_SUMMARY);
        }
        return summary;
    }
}
