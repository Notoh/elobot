package io.notoh.elobot.rank.commands;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import io.notoh.elobot.rank.Calculator;
import net.dv8tion.jda.core.entities.Message;

public class CalcAvg extends Command {

    public CalcAvg(Database database) {
        super("calcavg");
    }

    @Override
    public void run(Message msg) {
        String[] args = getArguments(msg);
        if(args.length != 5) {
            msg.getChannel().sendMessage("Correct usage: -calcavg <rating1> <rating2> <rating3> <rating4> <rating5>").queue();
            return;
        }
        int[] ratings = new int[5];
        for(int i = 0; i < 5; i++) {
            ratings[i] = Integer.parseInt(args[i]);
        }
        int avg = Calculator.calcAvg(ratings);
        msg.getChannel().sendMessage("Average rating is " + avg + ".").queue();
    }
}
