package io.notoh.elobot.rank.commands;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import net.dv8tion.jda.core.entities.Message;

public class Help extends Command {

    public Help(Database database) {
        super("help");
    }

    @Override
    public void run(Message msg) {
        msg.getChannel().sendMessage("Commands are as follows:```\n-initiateban initiates banning.\n-ban <map> bans a" +
                " " +
                "map when banning is enabled.\n-notbanned lists the maps not banned yet.\n-top2 <ten players> " +
                "to find the top 2 players based on rating from that list.\n-calcavg <5 ratings> calculates the " +
                "average of those ratings" +
                ".\n-leaderboard [page] gives the full leaderboard.\n-rank <player> gives the player's rank and " +
                "rating.\n-register <player> adds a player to the rating list.\n-changename <old> <new> changes " +
                "names.\n-help is this " +
                "command" +
                ".\nFor " +
                "moderators:\n-deleteplayer <player> removes a player from the rating list.\n-addgame <names> " +
                "<roundswinner> <roundsloser> <corresponding_performances> adds game data.```\nFor the game specific " +
                "commands (rank, leaderboard, deleteplayer, register, addgame, top2) append cs or val to specify the " +
                "game i.e. -rankcs n0toh or -addgameval <shit>").queue();
    }
}
