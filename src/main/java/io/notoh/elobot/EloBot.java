package io.notoh.elobot;


import io.notoh.elobot.rank.commands.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public final class EloBot {

    public void start() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("./credentials.txt"));
            Util.TOKEN = br.readLine();
            Util.DB_USER = br.readLine();
            Util.DB_PASS = br.readLine();
            Util.DB_URL = br.readLine();
            br.close();
            System.out.println("Read credentials successfully, building JDA.");
            JDA bot = JDABuilder.createDefault(Util.TOKEN).build().awaitReady();
            System.out.println("JDA built, starting DB");
            Database database = new Database(bot);
            MainEventHandler handler = new MainEventHandler();
            bot.addEventListener(handler);
            bot.getPresence().setActivity(Activity.playing("CvC Ranked"));

            handler.addCommand(new Rank(database));
            handler.addCommand(new Register(database));
            handler.addCommand(new AddGameExport(database));
            handler.addCommand(new DeletePlayer(database));
            handler.addCommand(new Leaderboard(database));
            handler.addCommand(new Help());
            handler.addCommand(new Captains(database));
            handler.addCommand(new ChangeName(database));
            handler.addCommand(new Punish(database));
            handler.addCommand(new Pardon(database));
            handler.addCommand(new Carry(database));
            handler.addCommand(new ForceRating(database));
            handler.addCommand(new ActualMath(database));
        } catch (LoginException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
