
CarRace is the game of car racing with server and multiple clients based on UDP and multithreading in Java.
Server open a new thread while a new player is connecting to game. Server is connecting through port 11331. Client uses one thread to parse packets, and second to execute the game. Car has to pass checkpoints on the track to count the lap, checkpoints are written in the .txt files. After all the players finish the laps (the number of laps is specified during starting the server) completely, a preview pops up with the results of the race including usernames and time taken for each player. A player can take extra laps as much as he wants till the rest of players finish their main number of laps. 
Also, there is a chatting feature where players can chat together while playing, it is built on TCP. The chatting is connecting through port 22222.

Keys :-
|----------------|--------------------------------|
|    KEY UP		 |			accelerate			  |
|----------------|--------------------------------|
|   KEY DOWN	 | 		release/go back			  |
|----------------|--------------------------------|
| KEY LEFT/Right | 		turn left/right			  |
|----------------|--------------------------------|
|	  R			 | reset to the starting position |
|----------------|--------------------------------|
|	  S			 | 		 start the game			  |
|----------------|--------------------------------|
|	  ESC		 | 		  exit the game			  |
|----------------|--------------------------------|

The required environment :-
The project is built on Windows 10, intellij, JDK 1.8, and you would only need to download and import these libraries:
•	import java.util
•	import java.awt
•	import java.net
•	import javax.swing

How to run the project :-
1- Run a clone of the project as a server; you should know this device IP.
2- Make as many clones as the players to play, run each one with your server IP and unique username.
3- Once all players are ready to start the game, one of them shall press ‘S’ from keyboard to start racing.
4- Use the game with the given keys below and chat using the chatting console.
5- The results will be shown when all users finish the number of laps specified by the server.

Youtube Link :-
