package net.avdw.skilltracker.playground;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.math3.util.Combinations;

import java.util.*;
import java.util.stream.Collectors;

public final class CombinatoricPlayground {
    private static void combination(final List<String> selectedNames, final List<String> names, final List<Integer> buckets) {
        if (buckets.isEmpty()) {
            System.out.println(String.join(",", selectedNames));
            return;
        }
        List<Integer> remainderBuckets = new ArrayList<>(buckets);
        int bucketSize = remainderBuckets.remove(0);

        Combinations combinations = new Combinations(names.size(), bucketSize);
        combinations.forEach(combination -> {
            List<String> localSelection = new ArrayList<>(selectedNames);
            List<String> remainderNames = new ArrayList<>(names);
            for (final int idx : combination) {
                String name = names.get(idx);
                localSelection.add(name);
                remainderNames.remove(name);
            }
            combination(new ArrayList<>(localSelection), remainderNames, remainderBuckets);
        });
    }

    public static Match formMatch(final List<String> nameList, final List<Integer> teamSizeList) {
        Match match = new Match();
        List<String> nameListCopy = new ArrayList<>(nameList);
        teamSizeList.forEach(count -> {
            Team team = new Team();
            for (int i = 0; i < count; i++) {
                team.add(nameListCopy.remove(0));
            }
            match.add(team);
        });
        return match;
    }

    public static void main(final String[] args) {
        List<String> names = Arrays.asList("Andrew", "JK", "Dean", "Jaco", "Etienne");
        List<Integer> counts = new ArrayList<>(Arrays.asList(2, 1, 1, 1));

        combination(new ArrayList<>(), names, counts);
        Set<Match> matchSet = CollectionUtils.permutations(names).stream().map(permutations -> formMatch(permutations, counts)).collect(Collectors.toSet());
        matchSet.forEach(System.out::println);
    }

    @Data
    static class Match {
        private final Set<Team> teamList = new HashSet<>();

        public void add(final Team team) {
            teamList.add(team);
        }
    }

    @Data
    static class Team {
        private final Set<String> nameList = new HashSet<>();

        public void add(final String name) {
            nameList.add(name);
        }
    }
}
