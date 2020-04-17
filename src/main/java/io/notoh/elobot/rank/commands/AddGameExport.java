package io.notoh.elobot.rank.commands;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import io.notoh.elobot.Util;
import io.notoh.elobot.rank.Calculator;
import io.notoh.elobot.rank.Player;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

public class AddGameExport extends Command {

    private Database database;

    public AddGameExport(Database database) {
        super("addgameexport");
        this.database = database;
    }

    @Override
    public void run(Message msg) {
        boolean hasPerms = false;
        for(Role role : msg.getGuild().getMemberById(msg.getAuthor().getId()).getRoles()) {
            if(role.getId().equals(Util.UPDATE_ROLE)) {
                hasPerms = true;
            }
        }
        if(!hasPerms) {
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
        double outcomeLosers = Calculator.gamePct(lost, won);
        double outcomeWinners = Calculator.gamePct(won, lost);
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
        double[] perfWinners = new double[5];
        double[] perfLosers = new double[5];
        startIndexWin = won == roundsA ? 11 : 38;
        startIndexLoss = won == roundsA ? 38 : 11;
        count = 0;
        for(int i = startIndexWin; i < startIndexWin+21; i+=5) {
            perfWinners[count] = Double.parseDouble(args[i]);
            count++;
        }
        count = 0;
        for(int i = startIndexLoss; i < startIndexLoss+21; i+=5) {
            perfLosers[count] = Double.parseDouble(args[i]);
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
        Player[] winners = new Player[5];
        Player[] losers = new Player[5];
        for(int i = 0; i < 5; i++) {
            winners[i] = database.getPlayers().get(namesWon[i]);
        }
        for(int i = 0; i < 5; i++) {
            losers[i] = database.getPlayers().get(namesLost[i]);
        }
        int avgWinner = Calculator.calcAvg(winners);
        int avgLoser = Calculator.calcAvg(losers);
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < 5; i++) {
            Player player = winners[i];
            player.playGame(avgLoser, outcomeWinners, perfWinners[i]);
            database.updateRating(namesWon[i], String.valueOf(player.getRating()),
                    Util.DECIMAL_FORMAT.format(player.getDeviation()));
            builder.append("Updated player ").append(namesWon[i]).append(". New rating: ").append(player.getRating()).append(
                    ". New ").append("deviation: ").append(Util.DECIMAL_FORMAT.format(player.getDeviation())).append(".\n");
        }
        for(int i = 0; i < 5; i++) {
            Player player = losers[i];
            player.playGame(avgWinner, outcomeLosers, perfLosers[i]);
            database.updateRating(namesLost[i], String.valueOf(player.getRating()),
                    Util.DECIMAL_FORMAT.format(player.getDeviation()));
            builder.append("Updated player ").append(namesLost[i]).append(". New rating: ").append(player.getRating()).append(
                    ". New ").append("deviation: ").append(Util.DECIMAL_FORMAT.format(player.getDeviation())).append(".\n");
        }
        msg.getChannel().sendMessage(builder).queue();
    }
}
