package io.notoh.elobot.rank.commands.veto;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import net.dv8tion.jda.core.entities.Message;

public class Ban extends Command {

    public Ban(Database database) {
        super("ban");
    }

    @Override
    public void run(Message msg) {
        if(!InitiateBan.isBanning) {
            msg.getChannel().sendMessage("You must enable map banning with -initiateban first!").queue();
            return;
        }
        String[] args = getArguments(msg);
        if(args.length == 0) {
            msg.getChannel().sendMessage("Correct usage: -ban <map>").queue();
            return;
        }
        String mapName = args[0].toLowerCase();
        for(String string : InitiateBan.maps.keySet()) {
            if(string.equals(mapName)) {
                boolean result = InitiateBan.maps.get(string);
                if(result) {
                    msg.getChannel().sendMessage("Map already banned!").queue();
                    return;
                } else {
                    InitiateBan.maps.put(string, true);
                    msg.getChannel().sendMessage("Map " + mapName + " banned.").queue();
                    int left = 0;
                    for(String remaining : InitiateBan.maps.keySet()) {
                        if(!InitiateBan.maps.get(remaining)) {
                            if(++left >= 2) {
                                break;
                            }
                        }
                    }
                    if(left == 1) {
                        for(String remaining : InitiateBan.maps.keySet()) {
                            if(!InitiateBan.maps.get(remaining)) {
                                msg.getChannel().sendMessage(remaining + " is chosen.").queue();
                                InitiateBan.maps.replaceAll((m, v) -> false);
                                InitiateBan.isBanning = false;
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}
