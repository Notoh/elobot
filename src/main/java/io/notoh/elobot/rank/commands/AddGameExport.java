package io.notoh.elobot.rank.commands;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import io.notoh.elobot.rank.Glicko2;
import io.notoh.elobot.rank.PlayerWrapper;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.notoh.elobot.rank.Glicko2.gamePct;
import static io.notoh.elobot.rank.Glicko2.teamGlicko2;

public class AddGameExport extends Command {

    private final Database database;

    public AddGameExport(Database database) {
        super("addgameexport");
        this.database = database;
    }

    @Override
    public void run(Message msg) {
        if(!checkPermission(msg.getMember())) {
            msg.getChannel().sendMessage("No permission!").queue();
            return;
        }
        String[] args = getArguments(msg);
        if(args.length != 59) {
            msg.getChannel().sendMessage("Invalid format!").queue();
            return;
        }
        int roundsA = Integer.parseInt(args[6]);
        int roundsB = Integer.parseInt(args[33]);
        int won = Math.max(roundsA, roundsB);
        int lost = Math.min(roundsA, roundsB);
        String[] namesWon = new String[5];
        String[] namesLost = new String[5];
        int startIndexWin = won == roundsA ? 7 : 34;
        int startIndexLoss = won == roundsA ? 34 : 7;
        int count = 0;
        for(int i = startIndexWin; i < startIndexWin+25; i += 5) {
            namesWon[count] = args[i].toLowerCase();
            count++;
        }
        count = 0;
        for(int i = startIndexLoss; i < startIndexLoss+25; i+=5) {
            namesLost[count] = args[i].toLowerCase();
            count++;
        }
        String[] killsWinners = new String[5];
        String[] killsLosers = new String[5];
        startIndexWin = won == roundsA ? 8 : 35;
        startIndexLoss = won == roundsA ? 35 : 8;
        count = 0;
        for(int i = startIndexWin; i < startIndexWin+24; i+=5) {
            killsWinners[count] = args[i];
            count++;
        }
        count = 0;
        for(int i = startIndexLoss; i < startIndexLoss+24; i+=5) {
            killsLosers[count] = args[i];
            count++;
        }
        for(String name : namesWon) {
            if(database.getPlayers().get(name) == null) {
                msg.getChannel().sendMessage("Player " + name + " does not exist! Cancelling.").queue();
                return;
            }
        }
        for(String name : namesLost) {
            if(database.getPlayers().get(name) == null) {
                msg.getChannel().sendMessage("Player " + name + " does not exist! Cancelling.").queue();
                return;
            }
        }
        int[] killsWon = new int[5];
        int[] deathsWon = new int[5];
        int[] killsLost = new int[5];
        int[] deathsLost = new int[5];

        for(int i = 0; i < 5; i++) {
            killsWon[i] = Integer.parseInt(killsWinners[i].split("-")[0]);
            deathsWon[i] = Integer.parseInt(killsWinners[i].split("-")[1]);
        }
        for(int i = 0; i < 5; i++) {
            killsLost[i] = Integer.parseInt(killsLosers[i].split("-")[0]);
            deathsLost[i] = Integer.parseInt(killsLosers[i].split("-")[1]);
        }
        List<PlayerWrapper> winners = new ArrayList<>();
        List<PlayerWrapper> losers = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            winners.add(database.getPlayers().get(namesWon[i]));
            winners.get(i).addKills(killsWon[i]);
            winners.get(i).addDeaths(deathsWon[i]);
            winners.get(i).addWin();
        }
        for(int i = 0; i < 5; i++) {
            losers.add(database.getPlayers().get(namesLost[i]));
            losers.get(i).addKills(killsLost[i]);
            losers.get(i).addDeaths(deathsLost[i]);
            losers.get(i).addLoss();
        }
        StringBuilder builder = new StringBuilder();
        double[] losersGlicko2 = teamGlicko2(losers.stream().map(PlayerWrapper::getGlicko).map(Glicko2::glicko1ToGlicko2).collect(ArrayList::new, (list, array) -> {
                    List<Double> list1 = new ArrayList<>();
                    Arrays.stream(array).forEach(list1::add);
                    list.add(list1);
                }, ArrayList::addAll));
        double winPct = gamePct(won, lost);
        for(int i = 0; i < 5; i++) {
            PlayerWrapper player = winners.get(i);
            player.playGame(killsWon[i], deathsWon[i], winPct, losersGlicko2);
            database.updateRating(player);
            builder.append("Updated player ").append(namesWon[i]).append(". New rating: ").append(player.isProvisional() ? "Provisional" : player.getRating()).append("RD/σ: ").append(player.getDeviation()).append("/").append(player.getVolatility()).append(".").append(".\n");
        }
        double[] winnersGlicko2 = teamGlicko2(winners.stream().map(PlayerWrapper::getGlicko).map(Glicko2::glicko1ToGlicko2).collect(ArrayList::new, (list, array) -> {
            List<Double> list1 = new ArrayList<>();
            Arrays.stream(array).forEach(list1::add);
            list.add(list1);
        }, ArrayList::addAll));
        for(int i = 0; i < 5; i++) {
            PlayerWrapper player = losers.get(i);
            player.playGame(killsLost[i], deathsLost[i], 1.0-winPct, winnersGlicko2);
            database.updateRating(player);
            builder.append("Updated player ").append(namesLost[i]).append(". New rating: ").append(player.isProvisional() ? "Provisional" : player.getRating()).append("RD/σ: ").append(player.getDeviation()).append("/").append(player.getVolatility()).append(".").append(".\n");
        }
        msg.getChannel().sendMessage(builder).queue();
    }
}
