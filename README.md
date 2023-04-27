# Cracking_the_Enigma_Machine
Built as part of Aviad Cohen's Java course at The Academic Collage Of Tel-Aviv Yaffo.

In this project, there is an implementation of a platform for hosting and participating in "Enigma Competitons" - competitions between different automatic deciphering teams.
The goal of the teams is to be the first to decipher correctly a message, which was ciphered with an Enigma Machine.

This project was built over the fundamentals of [Enigma Decrypt Manager](https://github.com/itayf9/Enigma_Decrypt_Manager)

## Features:

- Automatic deciphering of messages which were ciphered using an Enigma machine. Done by a multithread brute force opertion.
- All clients (Uboat, Allies, and Agent) can log in / log out to the system at any time (even during an active competiton).
- Many competitons can by active concurrently.
- The clients get frequent data updates.
- The UI was designed using JavaFX + CSS.

## Project Structure:
There are three different clients and one server:
#### UBoat  - The Competiton's Manager
- Represents a German submarine.
- Responsible for determining an initial configuration for the machine and the battlefield. It uploads an XML file that defines the battlefield (and the Enigma machine).
- Responsible for distributing an ciphered message.
- Responsible for deciding which team is the winner of the competition (which is the quickest team to decipher the message accurately).
- It cannot participate in a competition that already has another Uboat in it (in other words, there is exactly one Uboat in each competition).

![Uboat Configure Battlefield and Machine](images/uboat%20configure.png)

#### Allies - The Competiton's Team
- A deciphering team, compete among other "Allies" to be the first to succeed in deciphering the ciphered message transmitted by the UBoat.
- An "Allies" entity can connect to the server and choose the competition it wants to register to (only one competition at any given time).
- Each Allies type player is actually a decrypt manager. Since there are several Allies in the competition - there are also several different decrypt managers on the server side (In parallel).
- Each Allies owns some Agents, which are the workforce the do the multithread brute force deciphering.

![Allies Manage A Team](images/allies%20info.png)

#### Agent - The Competition's Workforce
- A Member of one of the various Allies. They are responsible for the deciphering tasks themselves.
- Each agent is a member of exactly one team.
- An Agent can connect to the server and select the Allies team that it wants to be a member of. Each Agent is a member of exactly one team. This detail is determined during registration and cannot change throughout the life of the application (of the Agent).
          
![Agent Connected To A Team](images/agent%20before%20competition.png)
          
#### The server
- Contains the configurations and setting of the competitions which are held in the system. Each competiton is called a "Battlefield".
- A Battlefield defines the name, the number of teams, and the difficulty level of the competition. There is exactly one Uboat participates in each battle. Also, there is one or more Allies.
- Each client (Uboat, Allies, Agent) that connects to the server begins on the system registration page, where he has to choose its username.
- The whole communication between the clients is done through fetching data from the server.


#### A Competition:
First, all the clients need to log in to the server. They can log out at any time.
Second, Each client configures it's relevent settings.

The competition starts when all players (Allies, Uboat) announce that they are ready.

The ciphered message is transmitted and distributed to all of the Allies teams participating in the competition.
Each team assigns the various tasks to it's agents.
The agents start the decipher process and send possible candidates back to the Allies.
The Allies sends the candidates from all its Agents, back to the UBoat.
The UBoat receives a collection of candidates from the various Allies teams and determines which team owns a winning candidate is (since only the it knows which deciphered candidate matches the original ciphered message).

![Uboat Announces Winner Of Competition](images/uboat%20competition.png)

The information about the winner is transmitted to all the clients.

![Allies Status Competiton](images/allies%20after%20competition.png)



