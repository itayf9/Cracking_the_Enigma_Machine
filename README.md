# Cracking_the_Enigma_Machine
Built as part of Aviad Cohen's Java course at The Academic Collage Of Tel-Aviv Yaffo.

In this project you will find an implementation of a competition between different automatic decoding teams.
On the one hand, we can load different settings and configurations of the machines and distribute encrypted messages.
On the other hand, there are a bunch of automatic decoding teams that differ among themselves in the various parameters (number of agents, task size, etc.) and compete among themselves to see who will succeed in deciphering the encrypted message first (and if at all...).

This project was built over the fundamentals of []

## Project Structure:
There are 3 different clients and one server:

- UBoat - A German submarine responsible for determining an initial configuration for the machine, distributing an encrypted message and deciding which team is the             winner of the competition (which is the quickest team to decrypt the message accurately).

- Allies - A decrypting team. Competing among themselves to be the first to succeed in deciphering the encrypted message transmitted by the UBoat.

- Agent - A Member of one of the various decrypting teams. They are responsible for the decrypting tasks themselves.
Each agent is a member of exactly one team.

- The server - contains the definitions of the various competitions which are held in the system. Each competiton is called a "Battlefield".
A Battlefield defines the name, the number of teams, and the difficulty level of the competition. There is exactly one German submarine participates in each battle. Also, there is one or more decrypting teams.
Each client (Uboat, Allies, Agent) that connects to the server begins on the system registration page, where he has to choose its username.

## Features:

#### Control The Competition:
A "Uboat" entity can upload an XML file that defines the battlefield (and the Enigma machine).
It cannot participate in a competition that already has another Uboat in it (in other words, there is exactly one Uboat in each competition).

#### Manage A Team:
An "Allies" entity can connect to the server and choose the competition it wants to register to (only one competition at any given time).
Each Allies type player is actually a decrypt manager. 
Since there are several Allies in the competition - there are also several different decrypt managers on the server side (In parallel).

#### Decipher Enigma Machine's Messages:
An Agent type entity can connect to the server and select the Allies team that it wants to be a member of.
Each Agent is a member of exactly one team. This detail is determined during registration and cannot change throughout the life of the application (of the Agent)

#### A Competition:
The competition starts when all players (Allies, Uboat) announce that they are ready.
The UBoat will choose a configuration and encrypt a certain message (a valid message, from words in the dictionary). 
The encrypted message is transmitted and distributed to all the Allies teams participating in the competition.
Each team assigns the various tasks to it's agents.
The agents start the decoding process and send possible candidates back to DM (Decrypt Manager) 
The Allies send the candidates from all its Agents, back to the UBoat.
The UBoat receives a collection of candidates from the various Allies teams and only it will be the one to determine who the winning candidate is (since only he knows which deciphered candidate matches the original ciphered message).
