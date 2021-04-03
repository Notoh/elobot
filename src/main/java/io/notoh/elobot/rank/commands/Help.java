package io.notoh.elobot.rank.commands;

import io.notoh.elobot.Command;
import net.dv8tion.jda.api.entities.Message;

public class Help extends Command {

    public Help() {
        super("help");
    }

    @Override
    public void run(Message msg) {
        msg.getChannel().sendMessage("Commands are as follows:```\n-initiateban initiates banning.\n-ban <map> bans a" +
                " " +
                "map when banning is enabled.\n-notbanned lists the maps not banned yet.\n-captains <ten players> " +
                "to find the top 2 players based on rating from that list.\n-leaderboard [page] gives the full leaderboard.\n-rank <player> gives the player's rank " +
                "and" +
                " " +
                        "rating.\n-register <player> adds a player to the rating list.\n-changename <old> <new> " +
                "changes a player's name in the system.\n" +
                "-help is " +
                "this command" +
                ".\nFor " +
                "moderators:\n-deleteplayer <player> removes a player from the rating list.\n-addgameexport " +
                "<exact_podcrash_export> adds a game from a podcrash export.\n-punish <player> punishes a " +
                "player's rating.\n-pardon <player> undos a punish.\n-removegame <exact export format> undoes a " +
                "game inputted.\n-forcerating <player> <rating> sets a player's rating.\n-carry <player> gives a " +
                "player their carry bonus for the game.```").queue();

    }
}
