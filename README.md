# Settlers of Catan Simulator

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=amandasff_settlersofcatan&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=amandasff_settlersofcatan)

SFWRENG 2AA4 - Assignment 2

## Description

A Java simulator for a simplified version of Settlers of Catan. One human player and 3 AI agents play on a randomized hex board, rolling dice, collecting resources, and building roads/settlements/cities. The human player enters commands through the console, and a Python visualizer shows the board state after each turn.

## How to run

You need Java 17+ and Maven.

## Config

Set the number of rounds in `config.txt`:
```
turns: 100
```
Range is 1-8192. You can also type it in when the program starts.

## How it works


- Board is 19 hex tiles (3-4-5-4-3 layout) with randomized terrain and number tokens
- Each turn a player rolls 2 dice, resources get distributed, then they can build
- Rolling a 7 triggers the Robber, players with more than 7 cards discard half, the robber moves to a random tile, and a qualifying opponent loses a card
- Settlements cost 1 brick + 1 lumber + 1 wool + 1 grain (1 VP)
- Cities cost 3 ore + 2 grain (upgrades settlement to 2 VP)
- Roads cost 1 brick + 1 lumber
- Longest road (5+) gives 2 VP bonus
- First to 10 VP wins
