package io.notoh.elobot.rank;

import de.gesundkrank.jskills.*;
import de.gesundkrank.jskills.trueskill.TwoTeamTrueSkillCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Calculator {

    private Calculator() {
        //limit scope
    }

    public static void playGame(List<PlayerWrapper> winners, List<PlayerWrapper> losers) {
        GameInfo info = GameInfo.getDefaultGameInfo();
        Team win = new Team();
        winners.forEach(playerWrapper -> win.addPlayer(playerWrapper.getPlayer(), playerWrapper.getRating()));
        Team lose = new Team();
        losers.forEach(playerWrapper -> lose.addPlayer(playerWrapper.getPlayer(), playerWrapper.getRating()));
        List<ITeam> teams = new ArrayList<>();
        teams.add(win);
        teams.add(lose);
        Map<IPlayer, Rating> results = new TwoTeamTrueSkillCalculator().calculateNewRatings(info, teams, 1, 2);

        for(PlayerWrapper wrapper : winners) {
            wrapper.setRating(results.get(wrapper.getPlayer()));
        }

        for(PlayerWrapper wrapper : losers) {
            wrapper.setRating(results.get(wrapper.getPlayer()));
        }
    }


}
