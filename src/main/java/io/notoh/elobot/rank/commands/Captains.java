package io.notoh.elobot.rank.commands;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import io.notoh.elobot.Util;
import io.notoh.elobot.rank.PlayerWrapper;
import net.dv8tion.jda.core.entities.Message;


public class Captains extends Command {
    private Database database;

    public Captains(Database database) {
        super("captains");
        this.database = database;
    }

    @Override
    public void run(Message msg) {
        String[] args = getArguments(msg);
        if(args.length < 10) {
            msg.getChannel().sendMessage("Usage: -captains <ten players>").queue();
            return;
        }
        double bestRating = Double.MIN_VALUE;
        PlayerWrapper best = null;
        double secondBest = Double.MIN_VALUE;
        PlayerWrapper second = null;
        for(String name : args) {
            PlayerWrapper player = database.getPlayers().get(name.toLowerCase());
            boolean isValid =
                    Util.validCaptains.contains(name.toLowerCase()) || player.getWins()+player.getLosses()>=10;
            if(player == null) {
                msg.getChannel().sendMessage("Player " + name + " does not exist! Cancelling.").queue();
                return;
            }
            if(!isValid)
                continue;
            double rating = player.getRating().getConservativeRating();
            if(rating >= bestRating) {
                second = best;
                secondBest = bestRating;
                bestRating = rating;
                best = player;
            } else if(rating >= secondBest) {
                secondBest = rating;
                second = player;
            }
        }
        if(best == null || second == null) {
            for(String name : args) {
                PlayerWrapper player = database.getPlayers().get(name.toLowerCase());
                if(player == null) {
                    msg.getChannel().sendMessage("Player " + name + " does not exist! Cancelling.").queue();
                    return;
                }
                double rating = player.getRating().getConservativeRating();
                if(rating >= bestRating) {
                    second = best;
                    secondBest = bestRating;
                    bestRating = rating;
                    best = player;
                } else if(rating >= secondBest) {
                    secondBest = rating;
                    second = player;
                }
            }
        }
        if(best == null || second == null) {
            msg.getChannel().sendMessage("An error occurred.").queue();
            return;
        }
        msg.getChannel().sendMessage("Captains are " + best.getPlayer().getId() + " and " + second.getPlayer().getId() + ".").queue();
    }
}
