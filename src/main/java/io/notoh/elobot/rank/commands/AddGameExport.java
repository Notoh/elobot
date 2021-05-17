package io.notoh.elobot.rank.commands;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import io.notoh.elobot.rank.PlayerWrapper;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static io.notoh.elobot.Util.DECIMAL_FORMAT;
import static io.notoh.elobot.rank.Glicko2.gamePct;
import static io.notoh.elobot.rank.Glicko2.getComparisonRatings;

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
        execute(getArguments(msg), msg.getChannel());
    }

    public boolean execute(String[] args, MessageChannel toSend) {
        if(args.length != 59) {
            toSend.sendMessage("Invalid format!").queue();
            return false;
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
                toSend.sendMessage("Player " + name + " does not exist! Cancelling.").queue();
                return false;
            }
        }
        for(String name : namesLost) {
            if(database.getPlayers().get(name) == null) {
                toSend.sendMessage("Player " + name + " does not exist! Cancelling.").queue();
                return false;
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
            winners.get(i).addKillsAndSetTemp(killsWon[i]);
            winners.get(i).addDeathsAndSetTemp(deathsWon[i]);
            winners.get(i).addWin();
        }
        for(int i = 0; i < 5; i++) {
            losers.add(database.getPlayers().get(namesLost[i]));
            losers.get(i).addKillsAndSetTemp(killsLost[i]);
            losers.get(i).addDeathsAndSetTemp(deathsLost[i]);
            losers.get(i).addLoss();
        }
        StringBuilder builder = new StringBuilder();

        Comparator<? super PlayerWrapper> comparator = (player1, player2) -> (int) (player2.getRating() - player1.getRating());

        winners.sort(comparator);
        losers.sort(comparator);

        double avgWinners = winners.stream().mapToDouble(PlayerWrapper::getRating).average().orElseThrow(AssertionError::new);
        double avgLosers = losers.stream().mapToDouble(PlayerWrapper::getRating).average().orElseThrow(AssertionError::new);

        double winPct = gamePct(won, lost);
        for(int i = 0; i < 5; i++) {
            PlayerWrapper player = winners.get(i);

            double avgW = ((avgWinners * 5) - player.getRating()) / 4;
            double avgL = ((avgLosers * 5) - losers.get(i).getRating()) / 4;

            player.playGame(winPct, getComparisonRatings(player.getGlicko(), avgW, losers.get(i).getGlicko(), avgL));
            database.updateRating(player);
            builder.append("Updated player ").append(player.getName()).append(". New rating: ").append(player.isProvisional() ?
                    "Provisional" : DECIMAL_FORMAT.format(player.getRating())).append(" RD: ").append(DECIMAL_FORMAT.format(player.getDeviation())).append(".").append(".\n");
        }

        for(int i = 0; i < 5; i++) {
            PlayerWrapper player = losers.get(i);

            double avgL = ((avgLosers * 5) - player.getRating()) / 4;
            double avgW = ((avgWinners * 5) - winners.get(i).getRating()) / 4;

            player.playGame(1.0-winPct, getComparisonRatings(player.getGlicko(), avgL, winners.get(i).getGlicko(), avgW));
            database.updateRating(player);
            builder.append("Updated player ").append(player.getName()).append(". New rating: ").append(player.isProvisional() ?
                    "Provisional" : DECIMAL_FORMAT.format(player.getRating())).append(" RD: ").append(DECIMAL_FORMAT.format(player.getDeviation())).append(".").append(".\n");
        }
        toSend.sendMessage(builder).queue();
        return true;
    }
}
