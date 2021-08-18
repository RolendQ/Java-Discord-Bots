package com.roland.wager.bot;

import net.dv8tion.jda.core.entities.User;

/**
 * Simple challenge object that stores the ids of the two participants
 */
public class Challenge {
    private String challengerID;
    private String challengedID = null;
    private User challengerUser; // used for dm later

    public Challenge(String challengerID, User challengerUser) {
        this.challengerID = challengerID;
        this.challengerUser = challengerUser;
    }
    public Challenge(String challengerID, User challengerUser, String challengedID) {
        this.challengerID = challengerID;
        this.challengerUser = challengerUser;
        this.challengedID = challengedID;
    }

    public String getChallengerID() {
        return challengerID;
    }

    public String getChallengedID() {
        return challengedID;
    }

    public User getChallengerUser() {
        return challengerUser;
    }
}