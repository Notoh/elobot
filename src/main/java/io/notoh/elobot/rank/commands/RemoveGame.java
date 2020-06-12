package io.notoh.elobot.rank.commands;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import io.notoh.elobot.Util;
import io.notoh.elobot.rank.PlayerWrapper;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

import java.util.ArrayList;
import java.util.List;

public class RemoveGame extends Command {

    private Database database;

    public RemoveGame(Database database) {
        super("removegame");
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
            winners.get(i).removeKills(killsWon[i]);
            winners.get(i).removeDeaths(deathsWon[i]);
            winners.get(i).removeWin();
        }
        for(int i = 0; i < 5; i++) {
            losers.add(database.getPlayers().get(namesLost[i]));
            losers.get(i).removeKills(killsLost[i]);
            losers.get(i).removeDeaths(deathsLost[i]);
            losers.get(i).removeLoss();
        }
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < 5; i++) {
            PlayerWrapper player = winners.get(i);
            player.invertGame(killsWon[i], deathsWon[i], 1.0);
            database.updateRating(player);
            builder.append("Updated player ").append(namesWon[i]).append(". New rating: ").append(player.getRating()).append(
                    ".").append(".\n");
        }
        for(int i = 0; i < 5; i++) {
            PlayerWrapper player = losers.get(i);
            player.invertGame(killsLost[i], deathsLost[i], 0);
            database.updateRating(player);
            builder.append("Updated player ").append(namesLost[i]).append(". New rating: ").append(player.getRating()).append(
                    ".").append(".\n");
        }
        msg.getChannel().sendMessage(builder).queue();
    }
}
