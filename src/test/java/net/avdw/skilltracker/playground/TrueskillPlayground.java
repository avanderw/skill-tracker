package net.avdw.skilltracker.playground;

import de.gesundkrank.jskills.*;
import de.gesundkrank.jskills.trueskill.FactorGraphTrueSkillCalculator;
import de.gesundkrank.jskills.trueskill.TwoPlayerTrueSkillCalculator;
import de.gesundkrank.jskills.trueskill.TwoTeamTrueSkillCalculator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class TrueskillPlayground {

    public static void main(String[] args) {
        SkillCalculator twoPlayerSkillCalculator = new TwoPlayerTrueSkillCalculator();
        SkillCalculator twoTeamSkillCalculator = new TwoTeamTrueSkillCalculator();
        SkillCalculator factorGraphSkillCalculator = new FactorGraphTrueSkillCalculator();

        play(twoPlayerSkillCalculator);
        play(twoTeamSkillCalculator);
        play(factorGraphSkillCalculator);

        GameInfo gameInfo = GameInfo.getDefaultGameInfo();

        Player<Integer> andrew = new Player<>(1);
        Player<Integer> karl = new Player<>(2);
        Player<Integer> jaco = new Player<>(3);
        Player<Integer> etienne = new Player<>(4);
        Player<Integer> marius = new Player<>(5);
        Player<Integer> raoul = new Player<>(6);

        Team team1 = new Team()
                .addPlayer(andrew, gameInfo.getDefaultRating())
                .addPlayer(karl, gameInfo.getDefaultRating());
        Team team2 = new Team()
                .addPlayer(jaco, gameInfo.getDefaultRating())
                .addPlayer(etienne, gameInfo.getDefaultRating());
        Team team3 = new Team()
                .addPlayer(marius, gameInfo.getDefaultRating())
                .addPlayer(raoul, gameInfo.getDefaultRating());

        Collection<ITeam> teams = Team.concat(team1, team2, team3);
        print("Northgard test", gameInfo, teams, factorGraphSkillCalculator, 1, 2, 2);
    }

    private static void play(SkillCalculator skillCalculator) {
        playTwoPlayer(skillCalculator);
        playTwoTeam(skillCalculator);
        playMultiTeam(skillCalculator);
    }

    private static void playMultiTeam(SkillCalculator skillCalculator) {
        twoOnFourOnTwoWinDraw(skillCalculator);
        threeTeamsOfOne(skillCalculator);
        threeTeamsOfOneDraw(skillCalculator);
    }

    private static void playTwoTeam(SkillCalculator skillCalculator) {
        twoOnTwoSimple(skillCalculator);
        twoOnTwoDraw(skillCalculator);
        twoOnTwoUnbalanced(skillCalculator);
        twoOnTwoUpset(skillCalculator);
        fourOnFourSimple(skillCalculator);
        oneOnTwoSimple(skillCalculator);
        oneOnTwoBalanced(skillCalculator);
        oneOnThreeSimple(skillCalculator);
        oneOnTwoDraw(skillCalculator);
        oneOnThreeDraw(skillCalculator);
        oneOnSevenSimple(skillCalculator);
        threeOnTwo(skillCalculator);
    }

    private static void threeTeamsOfOneDraw(SkillCalculator skillCalculator) {
        Player<Integer> player1 = new Player<Integer>(1);
        Player<Integer> player2 = new Player<Integer>(2);
        Player<Integer> player3 = new Player<Integer>(3);
        GameInfo gameInfo = GameInfo.getDefaultGameInfo();

        Team team1 = new Team(player1, gameInfo.getDefaultRating());
        Team team2 = new Team(player2, gameInfo.getDefaultRating());
        Team team3 = new Team(player3, gameInfo.getDefaultRating());

        Collection<ITeam> teams = Team.concat(team1, team2, team3);
        print("1(draw) v 1(draw) v 1(draw)", gameInfo, teams, skillCalculator, 1, 1, 1);
    }

    private static void threeTeamsOfOne(SkillCalculator skillCalculator) {
        Player<Integer> player1 = new Player<Integer>(1);
        Player<Integer> player2 = new Player<Integer>(2);
        Player<Integer> player3 = new Player<Integer>(3);
        GameInfo gameInfo = GameInfo.getDefaultGameInfo();

        Team team1 = new Team(player1, gameInfo.getDefaultRating());
        Team team2 = new Team(player2, gameInfo.getDefaultRating());
        Team team3 = new Team(player3, gameInfo.getDefaultRating());

        Collection<ITeam> teams = Team.concat(team1, team2, team3);
        print("1(win) v 1(second) v 1(third)", gameInfo, teams, skillCalculator, 1, 2, 3);
    }

    private static void twoOnFourOnTwoWinDraw(SkillCalculator skillCalculator) {
        Player<Integer> player1 = new Player<Integer>(1);
        Player<Integer> player2 = new Player<Integer>(2);

        GameInfo gameInfo = GameInfo.getDefaultGameInfo();

        Team team1 = new Team()
                .addPlayer(player1, new Rating(40,4))
                .addPlayer(player2, new Rating(45,3));

        Player<Integer> player3 = new Player<Integer>(3);
        Player<Integer> player4 = new Player<Integer>(4);
        Player<Integer> player5 = new Player<Integer>(5);
        Player<Integer> player6 = new Player<Integer>(6);

        Team team2 = new Team()
                .addPlayer(player3, new Rating(20, 7))
                .addPlayer(player4, new Rating(19, 6))
                .addPlayer(player5, new Rating(30, 9))
                .addPlayer(player6, new Rating(10, 4));

        Player<Integer> player7 = new Player<Integer>(7);
        Player<Integer> player8 = new Player<Integer>(8);

        Team team3 = new Team()
                .addPlayer(player7, new Rating(50,5))
                .addPlayer(player8, new Rating(30,2));

        Collection<ITeam> teams = Team.concat(team1, team2, team3);
        print("2(win) v 4(draw) v 2(draw)", gameInfo, teams, skillCalculator, 1, 2, 2);
    }

    private static void threeOnTwo(SkillCalculator skillCalculator) {
        Player<Integer> player1 = new Player<Integer>(1);
        Player<Integer> player2 = new Player<Integer>(2);
        Player<Integer> player3 = new Player<Integer>(3);

        Team team1 = new Team()
                .addPlayer(player1, new Rating(28, 7))
                .addPlayer(player2, new Rating(27, 6))
                .addPlayer(player3, new Rating(26, 5));


        Player<Integer> player4 = new Player<Integer>(4);
        Player<Integer> player5 = new Player<Integer>(5);

        Team team2 = new Team()
                .addPlayer(player4, new Rating(30, 4))
                .addPlayer(player5, new Rating(31, 3));

        GameInfo gameInfo = GameInfo.getDefaultGameInfo();

        Collection<ITeam> teams = Team.concat(team1, team2);
        print("3(win) v 2(lose)", gameInfo, teams, skillCalculator, 1, 2);
    }

    private static void oneOnSevenSimple(SkillCalculator skillCalculator) {
        Player<Integer> player1 = new Player<Integer>(1);

        GameInfo gameInfo = GameInfo.getDefaultGameInfo();

        Team team1 = new Team()
                .addPlayer(player1, gameInfo.getDefaultRating());

        Player<Integer> player2 = new Player<Integer>(2);
        Player<Integer> player3 = new Player<Integer>(3);
        Player<Integer> player4 = new Player<Integer>(4);
        Player<Integer> player5 = new Player<Integer>(5);
        Player<Integer> player6 = new Player<Integer>(6);
        Player<Integer> player7 = new Player<Integer>(7);
        Player<Integer> player8 = new Player<Integer>(8);

        Team team2 = new Team()
                .addPlayer(player2, gameInfo.getDefaultRating())
                .addPlayer(player3, gameInfo.getDefaultRating())
                .addPlayer(player4, gameInfo.getDefaultRating())
                .addPlayer(player5, gameInfo.getDefaultRating())
                .addPlayer(player6, gameInfo.getDefaultRating())
                .addPlayer(player7, gameInfo.getDefaultRating())
                .addPlayer(player8, gameInfo.getDefaultRating());

        Collection<ITeam> teams = Team.concat(team1, team2);
        print("1(win) v 7(lose)", gameInfo, teams, skillCalculator, 1, 2);
    }

    private static void oneOnThreeDraw(SkillCalculator skillCalculator) {
        Player<Integer> player1 = new Player<Integer>(1);

        GameInfo gameInfo = GameInfo.getDefaultGameInfo();

        Team team1 = new Team()
                .addPlayer(player1, gameInfo.getDefaultRating());

        Player<Integer> player2 = new Player<Integer>(2);
        Player<Integer> player3 = new Player<Integer>(3);
        Player<Integer> player4 = new Player<Integer>(4);

        Team team2 = new Team()
                .addPlayer(player2, gameInfo.getDefaultRating())
                .addPlayer(player3, gameInfo.getDefaultRating())
                .addPlayer(player4, gameInfo.getDefaultRating());

        Collection<ITeam> teams = Team.concat(team1, team2);
        print("1(draw) v 3(draw)", gameInfo, teams, skillCalculator, 1, 1);
    }

    private static void oneOnTwoDraw(SkillCalculator skillCalculator) {
        Player<Integer> player1 = new Player<Integer>(1);

        GameInfo gameInfo = GameInfo.getDefaultGameInfo();

        Team team1 = new Team()
                .addPlayer(player1, gameInfo.getDefaultRating());

        Player<Integer> player2 = new Player<Integer>(2);
        Player<Integer> player3 = new Player<Integer>(3);

        Team team2 = new Team()
                .addPlayer(player2, gameInfo.getDefaultRating())
                .addPlayer(player3, gameInfo.getDefaultRating());

        Collection<ITeam> teams = Team.concat(team1, team2);
        print("1(draw) v 2(draw)", gameInfo, teams, skillCalculator, 1, 1);
    }

    private static void oneOnThreeSimple(SkillCalculator skillCalculator) {
        Player<Integer> player1 = new Player<Integer>(1);

        GameInfo gameInfo = GameInfo.getDefaultGameInfo();

        Team team1 = new Team()
                .addPlayer(player1, gameInfo.getDefaultRating());

        Player<Integer> player2 = new Player<Integer>(2);
        Player<Integer> player3 = new Player<Integer>(3);
        Player<Integer> player4 = new Player<Integer>(4);

        Team team2 = new Team()
                .addPlayer(player2, gameInfo.getDefaultRating())
                .addPlayer(player3, gameInfo.getDefaultRating())
                .addPlayer(player4, gameInfo.getDefaultRating());

        Collection<ITeam> teams = Team.concat(team1, team2);
        print("1(win) v 3(lose)", gameInfo, teams, skillCalculator, 1, 2);
    }

    private static void oneOnTwoBalanced(SkillCalculator skillCalculator) {
        Player<Integer> player1 = new Player<Integer>(1);

        GameInfo gameInfo = GameInfo.getDefaultGameInfo();

        Team team1 = new Team()
                .addPlayer(player1, new Rating(40, 6));

        Player<Integer> player2 = new Player<Integer>(2);
        Player<Integer> player3 = new Player<Integer>(3);

        Team team2 = new Team()
                .addPlayer(player2, new Rating(20, 7))
                .addPlayer(player3, new Rating(25, 8));

        Collection<ITeam> teams = Team.concat(team1, team2);
        print("1(win) v 2(lose)", gameInfo, teams, skillCalculator, 1, 2);
    }

    private static void oneOnTwoSimple(SkillCalculator skillCalculator) {
        Player<Integer> player1 = new Player<Integer>(1);

        GameInfo gameInfo = GameInfo.getDefaultGameInfo();

        Team team1 = new Team()
                .addPlayer(player1, gameInfo.getDefaultRating());

        Player<Integer> player2 = new Player<Integer>(2);
        Player<Integer> player3 = new Player<Integer>(3);

        Team team2 = new Team()
                .addPlayer(player2, gameInfo.getDefaultRating())
                .addPlayer(player3, gameInfo.getDefaultRating());

        Collection<ITeam> teams = Team.concat(team1, team2);
        print("1(win) v 2(lose)", gameInfo, teams, skillCalculator, 1, 2);
    }

    private static void fourOnFourSimple(SkillCalculator skillCalculator) {
        Player<Integer> player1 = new Player<Integer>(1);
        Player<Integer> player2 = new Player<Integer>(2);
        Player<Integer> player3 = new Player<Integer>(3);
        Player<Integer> player4 = new Player<Integer>(4);

        GameInfo gameInfo = GameInfo.getDefaultGameInfo();

        Team team1 = new Team()
                .addPlayer(player1, gameInfo.getDefaultRating())
                .addPlayer(player2, gameInfo.getDefaultRating())
                .addPlayer(player3, gameInfo.getDefaultRating())
                .addPlayer(player4, gameInfo.getDefaultRating());

        Player<Integer> player5 = new Player<Integer>(5);
        Player<Integer> player6 = new Player<Integer>(6);
        Player<Integer> player7 = new Player<Integer>(7);
        Player<Integer> player8 = new Player<Integer>(8);

        Team team2 = new Team()
                .addPlayer(player5, gameInfo.getDefaultRating())
                .addPlayer(player6, gameInfo.getDefaultRating())
                .addPlayer(player7, gameInfo.getDefaultRating())
                .addPlayer(player8, gameInfo.getDefaultRating());


        Collection<ITeam> teams = Team.concat(team1, team2);
        print("4(win) v 4(lose)", gameInfo, teams, skillCalculator, 1, 2);
    }

    private static void twoOnTwoUpset(SkillCalculator skillCalculator) {
        Player<Integer> player1 = new Player<Integer>(1);
        Player<Integer> player2 = new Player<Integer>(2);

        GameInfo gameInfo = GameInfo.getDefaultGameInfo();

        Team team1 = new Team()
                .addPlayer(player1, new Rating(20, 8))
                .addPlayer(player2, new Rating(25, 6));

        Player<Integer> player3 = new Player<Integer>(3);
        Player<Integer> player4 = new Player<Integer>(4);

        Team team2 = new Team()
                .addPlayer(player3, new Rating(35, 7))
                .addPlayer(player4, new Rating(40, 5));

        Collection<ITeam> teams = Team.concat(team1, team2);
        print("2(win) v 2(lose)", gameInfo, teams, skillCalculator, 1, 2);
    }

    private static void twoOnTwoUnbalanced(SkillCalculator skillCalculator) {
        Player<Integer> player1 = new Player<>(1);
        Player<Integer> player2 = new Player<>(2);

        GameInfo gameInfo = GameInfo.getDefaultGameInfo();

        Team team1 = new Team()
                .addPlayer(player1, new Rating(15, 8))
                .addPlayer(player2, new Rating(20, 6));

        Player<Integer> player3 = new Player<>(3);
        Player<Integer> player4 = new Player<>(4);

        Team team2 = new Team()
                .addPlayer(player3, new Rating(25, 4))
                .addPlayer(player4, new Rating(30, 3));

        Collection<ITeam> teams = Team.concat(team1, team2);
        print("2(draw) v 2(draw)", gameInfo, teams, skillCalculator, 1, 1);
    }

    private static void twoOnTwoDraw(SkillCalculator skillCalculator) {
        Player<Integer> player1 = new Player<>(1);
        Player<Integer> player2 = new Player<>(2);

        GameInfo gameInfo = GameInfo.getDefaultGameInfo();

        Team team1 = new Team()
                .addPlayer(player1, gameInfo.getDefaultRating())
                .addPlayer(player2, gameInfo.getDefaultRating());

        Player<Integer> player3 = new Player<>(3);
        Player<Integer> player4 = new Player<>(4);

        Team team2 = new Team()
                .addPlayer(player3, gameInfo.getDefaultRating())
                .addPlayer(player4, gameInfo.getDefaultRating());

        Collection<ITeam> teams = Team.concat(team1, team2);
        print("2(draw) v 2(draw)", gameInfo, teams, skillCalculator, 1, 1);
    }

    private static void twoOnTwoSimple(SkillCalculator skillCalculator) {
        GameInfo gameInfo = GameInfo.getDefaultGameInfo();

        Player<Integer> player1 = new Player<>(1);
        Player<Integer> player2 = new Player<>(2);
        Team team1 = new Team()
                .addPlayer(player1, gameInfo.getDefaultRating())
                .addPlayer(player2, gameInfo.getDefaultRating());

        Player<Integer> player3 = new Player<>(3);
        Player<Integer> player4 = new Player<>(4);
        Team team2 = new Team()
                .addPlayer(player3, gameInfo.getDefaultRating())
                .addPlayer(player4, gameInfo.getDefaultRating());

        Collection<ITeam> teams = Team.concat(team1, team2);
        print("2(win) v 2(lose)", gameInfo, teams, skillCalculator, 1, 2);
    }

    private static void print(String title, GameInfo gameInfo, Collection<ITeam> teams, SkillCalculator skillCalculator, int... teamRanks) {
        System.out.println(String.format("[Game] %s [Calculator] %s", title, skillCalculator.getClass().getSimpleName()));
        System.out.println(String.format("[GameInfo] %s", gameInfo));
        teams.forEach(team-> System.out.println(String.format("[Team] %s", team)));
        System.out.println(String.format("[TeamRanks] %s", Arrays.toString(teamRanks)));
        try {
            Map<IPlayer, Rating> newRatings = skillCalculator.calculateNewRatings(gameInfo, teams, teamRanks);
            newRatings.forEach((p, r) -> System.out.println(String.format("[Player%s] %s", p, r)));
        } catch (RuntimeException e) {
            System.out.println(String.format("[%s] Unsupported", skillCalculator.getClass().getSimpleName()));
        }
        System.out.println();
    }

    private static void playTwoPlayer(SkillCalculator skillCalculator) {
        twoPlayer1(skillCalculator);
        twoPlayerDrawn(skillCalculator);
        twoPlayerChessBug(skillCalculator);
        twoPlayerMassiveUpsetDraw(skillCalculator);
    }

    private static void twoPlayerMassiveUpsetDraw(SkillCalculator skillCalculator) {
        Player<Integer> player1 = new Player<>(1);
        Player<Integer> player2 = new Player<>(2);
        GameInfo gameInfo = GameInfo.getDefaultGameInfo();

        Team team1 = new Team().addPlayer(player1, gameInfo.getDefaultRating());
        Team team2 = new Team().addPlayer(player2, new Rating(50, 12.5));
        Collection<ITeam> teams = Team.concat(team1, team2);

        print("1(draw) v 1(draw)", gameInfo, teams, skillCalculator, 1, 1);
    }

    private static void twoPlayerChessBug(SkillCalculator skillCalculator) {
        Player<Integer> player1 = new Player<>(1);
        Player<Integer> player2 = new Player<>(2);
        GameInfo gameInfo = new GameInfo(1200.0, 1200.0 / 3.0, 200.0, 1200.0 / 300.0, 0.03);

        Team team1 = new Team(player1, new Rating(1301.0007, 42.9232));
        Team team2 = new Team(player2, new Rating(1188.7560, 42.5570));
        Collection<ITeam> teams = Team.concat(team1, team2);

        print("1(win) v 1(lose)", gameInfo, teams, skillCalculator, 1, 2);
    }

    private static void twoPlayerDrawn(SkillCalculator skillCalculator) {
        Player<Integer> player1 = new Player<>(1);
        Player<Integer> player2 = new Player<>(2);
        GameInfo gameInfo = GameInfo.getDefaultGameInfo();

        Team team1 = new Team(player1, gameInfo.getDefaultRating());
        Team team2 = new Team(player2, gameInfo.getDefaultRating());
        Collection<ITeam> teams = Team.concat(team1, team2);

        print("1(draw) v 1(draw)", gameInfo, teams, skillCalculator, 1, 1);
    }

    private static void twoPlayer1(SkillCalculator skillCalculator) {
        Player<Integer> player1 = new Player<>(1);
        Player<Integer> player2 = new Player<>(2);
        GameInfo gameInfo = GameInfo.getDefaultGameInfo();

        Team team1 = new Team(player1, gameInfo.getDefaultRating());
        Team team2 = new Team(player2, gameInfo.getDefaultRating());
        Collection<ITeam> teams = Team.concat(team1, team2);

        print("1(win) v 1(lose)", gameInfo, teams, skillCalculator, 1, 2);
    }

}
