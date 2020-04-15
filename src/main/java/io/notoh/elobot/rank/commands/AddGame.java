package io.notoh.elobot.rank.commands;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import io.notoh.elobot.Util;
import io.notoh.elobot.rank.Calculator;
import io.notoh.elobot.rank.Player;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

public class AddGame extends Command {

    private Database database;

    public AddGame(Database database) {
        super("addgame");
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
        if(args.length < 22) {
            msg.getChannel().sendMessage("Correct usage: -addgame <names> <roundswinner> <roundsloser> <corresponding_performances>").queue();
            return;
        }

        String[] names = new String[10];
        for(int i = 0; i < 10; i++) {
            names[i] = args[i].toLowerCase();
        }
        for(String name : names) {
            if(database.getPlayers().get(name) == null) {
                msg.getChannel().sendMessage("Player " + name + " does not exist! Cancelling.").queue();
                return;
            }
        }
        int won = Integer.parseInt(args[10]);
        int lost = Integer.parseInt(args[11]);
        double outcomeWinner = Calculator.gamePct(won, lost);
        double outcomeLoser = Calculator.gamePct(lost, won);
        int winnerAvg = 0;
        for(int i = 0; i < 5; i++) {
            winnerAvg += database.getPlayers().get(names[i]).getRating();
        }
        winnerAvg /= 5;
        int loserAvg = 0;
        for(int i = 5; i < 10; i++) {
            loserAvg += database.getPlayers().get(names[i]).getRating();
        }
        loserAvg /= 5;
        double[] performances = new double[10];
        for(int i = 0; i < 10; i++) {
            performances[i] = Double.parseDouble(args[12+i]);
        }
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < 5; i++) {
            String name = names[i];
            Player player = database.getPlayers().get(name);
            player.playGame(loserAvg, outcomeWinner, performances[i]);
            database.updateRating(name, String.valueOf(player.getRating()),
                    Util.DECIMAL_FORMAT.format(player.getDeviation()));
            builder.append("Updated player ").append(name).append(". New rating: ").append(player.getRating()).append(". New ").append("deviation: ").append(Util.DECIMAL_FORMAT.format(player.getDeviation())).append(".\n");

        }
        for(int i = 5; i < 10; i++) {
            String name = names[i];
            Player player = database.getPlayers().get(name);
            player.playGame(winnerAvg, outcomeLoser, performances[i]);
            database.updateRating(name, String.valueOf(player.getRating()),
                    Util.DECIMAL_FORMAT.format(player.getDeviation()));
            builder.append("Updated player ").append(name).append(". New rating: ").append(player.getRating()).append(". New ").append("deviation: ").append(Util.DECIMAL_FORMAT.format(player.getDeviation())).append(".\n");

        }
        msg.getChannel().sendMessage(builder).queue();
    }
}
