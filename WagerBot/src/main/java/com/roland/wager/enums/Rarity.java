package com.roland.wager.enums;

public class Rarity {
    public static final int common = 0;
    public static final int rare = 1;
    public static final int epic = 2;
    public static final int legendary = 3;
    public static final int mythic = 4;

    public static final double common_chance = 0.6; // not needed in current code because common is 'else'
    public static final double rare_chance = 0.25;
    public static final double epic_chance = 0.1;
    public static final double legendary_chance = 0.045;
    public static final double mythic_chance = 0.005;
}
