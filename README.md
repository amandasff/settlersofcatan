# Settlers of Catan Simulator

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=amandasff_settlersofcatan&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=amandasff_settlersofcatan)

SFWRENG 2AA4 - Assignment 1

## What is this

A Java simulator for a simplified version of Settlers of Catan. 4 AI players play on a randomized hex board, rolling dice, collecting resources, and building roads/settlements/cities. There's a Swing GUI that pops up so you can watch the game visually, and console output for the log.

We left out harbours, trading, development cards, and the robber since those weren't in scope for this assignment.

## How to run

You need Java 17+ and Maven.

```
mvn compile
mvn exec:java -Dexec.mainClass="catan.Demonstrator" -q
```

Or without Maven:
```
javac -d out src/main/java/catan/*.java
java -cp out catan.Demonstrator
```

## Tests

```
mvn test
```

## Config

Set the number of rounds in `config.txt`:
```
turns: 100
```
Range is 1-8192. You can also type it in when the program starts.

## How it works

- Board is 19 hex tiles (3-4-5-4-3 layout) with randomized terrain and number tokens
- Each turn a player rolls 2 dice, resources get distributed, then they can build stuff
- Settlements cost 1 brick + 1 lumber + 1 wool + 1 grain (1 VP)
- Cities cost 3 ore + 2 grain (upgrades settlement to 2 VP)
- Roads cost 1 brick + 1 lumber
- Longest road (5+) gives 2 VP bonus
- If you roll a 7, anyone with more than 7 cards discards half
- First to 10 VP wins

The agents pick actions randomly (per R1.2). If they have more than 7 cards they try to spend them by building.
