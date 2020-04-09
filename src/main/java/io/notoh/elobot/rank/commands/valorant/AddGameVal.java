package io.notoh.elobot.rank.commands.valorant;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import io.notoh.elobot.Util;
import io.notoh.elobot.rank.Calculator;
import io.notoh.elobot.rank.Player;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

public class AddGameVal extends Command {

    private Database database;

    public AddGameVal(Database database) {
        super("addgameval");
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
        if(args.length < 13) {
            msg.getChannel().sendMessage("Correct usage: -addgameval <names> <average_opponent_rating>" +
                    "<roundswon> <roundslost> <corresponding_performances>").queue();
            return;
        }

        String[] names = new String[5];
        System.arraycopy(args, 0, names, 0, 5);
        for(String name : names) {
            if(database.getValPlayers().get(name) == null) {
                msg.getChannel().sendMessage("Player " + name + " does not exist! Cancelling.").queue();
                return;
            }
        }
        int avgOpponentRating = Integer.parseInt(args[5]);
        int won = Integer.parseInt(args[6]);
        int lost = Integer.parseInt(args[7]);
        double outcome = Calculator.gamePct(won, lost);
        double[] performances = new double[5];
        for(int i = 0; i < 5; i++) {
            performances[i] = Double.parseDouble(args[8+i]);
        }
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < 5; i++) {
            String name = names[i];
            Player player = database.getValPlayers().get(name);
            player.playGame(avgOpponentRating, outcome, performances[i]);
            database.updateValRating(name, String.valueOf(player.getRating()),
                    Util.DECIMAL_FORMAT.format(player.getDeviation()));
            builder.append("Updated player ").append(name).append(". New rating: ").append(player.getRating()).append(". New ").append("deviation: ").append(Util.DECIMAL_FORMAT.format(player.getDeviation())).append(".\n");

        }
        msg.getChannel().sendMessage(builder).queue();
    }
}
