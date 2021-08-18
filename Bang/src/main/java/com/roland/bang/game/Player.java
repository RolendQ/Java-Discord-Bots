package com.roland.bang.game;

import java.awt.*;

public class Player {
    public String playerID;
    public Player nextPlayer;
    public String displayName;

    public Player prevPlayer;
    public int position;
    public int health;
    public int role;
    public String character;
    public int characterID;
    public boolean isAlive = true;
    public int arrows;

    public Player(String playerID, String displayName) {
        this.playerID = playerID;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Player getNextPlayer() {
        return nextPlayer;
    }

    public void setNextPlayer(Player nextPlayer) {
        this.nextPlayer = nextPlayer;
    }

    public Player getPrevPlayer() {
        return prevPlayer;
    }

    public void setPrevPlayer(Player prevPlayer) {
        this.prevPlayer = prevPlayer;
    }

    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getHealth() {
        return health;
    }

    public void updateHealth(int health) {
        this.health += health;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public int getArrows() {
        return arrows;
    }

    public void updateArrows(int arrows) {
        this.arrows += arrows;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setArrows(int arrows) {
        this.arrows = arrows;
    }

    public int getCharacterID() { return characterID; }

    public void setCharacterID(int characterID) { this.characterID = characterID; }
}
