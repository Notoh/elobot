### EloBot

A discord bot using [JDA](https://github.com/DV8FromTheWorld/JDA/) to calculate competitive 5v5 ratings for usage
 with CSGO and CvC, using a self-modified [glicko](https://wikipedia.org/wiki/Glicko_rating_system) system with rating
  periods set
  to 1 week. Many thanks to Qata for showing me how discord bots work, most of the bot-specific code is based upon his.
  
  Connects to a local sql server (I use mysql) using a table called "ratings" with columns "handle" "rating" "deviation"
  
  The code is incredibly shitty and just meant to be a bot used for 10mans, don't blame me when you read my code you
   get an aneurysm from me not caring about good practice.