### EloBot

A discord bot using [JDA](https://github.com/DV8FromTheWorld/JDA/) to calculate competitive 5v5 ratings for usage
 with CSGO and CvC, using a self-modified [glicko](https://wikipedia.org/wiki/Glicko) system with rating periods set
  to 1 week. Many thanks to Qata for showing me how discord bots work, most of the bot-specific code is based upon his.
  
  Connects to a local sql server (I use mysql) using a table called "ratings" with columns "handle" "rating" "deviation"