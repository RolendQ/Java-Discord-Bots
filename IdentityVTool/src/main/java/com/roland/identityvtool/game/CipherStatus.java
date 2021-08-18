package com.roland.identityvtool.game;

import java.awt.*;
import java.util.Arrays;

public class CipherStatus {
    public int map;
    public boolean[] keys;
    public boolean[] checkedKeys;

    public static boolean[][][] cipherKeys = {{{false,false,false},{false,true,true},{false,true,false},{true,false,true},{true,false,false}},
                                            {{true,false,false},{false,true,true},{true,false,true},{false,false,true},{true,true,true}},
                                            {{true,true,true},{false,true,true},{true,true,false},{true,false,false},{true,false,true}},
                                            {{false,false,false},{false,true,true},{true,false,true},{false,false,true},{true,true,true}},
                                            {{false,true,true},{true,true,true},{true,false,false},{true,false,true},{true,true,false}},
                                            {{true,true,true},{true,false,false},{true,true,false},{false,false,true},{false,true,false}},
                                            {{false,false,true},{false,true,true},{false,false,false},{true,false,true},{true,true,true}},

    };

    // 0 means neither of the two main ciphers
    public static int[][] decisiveCiphers = {{1,9,5,10,0},
                                            {11,8,5,6,9},
                                            {5,4,3,0,11},
                                            {9,0,7,8,4},
                                            {9,8,0,10,7},
                                            {3,2,4,0,7},
                                            {0,1,9,4,2},

    };

    public static Color[][] decisiveCiphersColor = {{Color.YELLOW, Color.YELLOW, Color.GREEN, Color.GREEN, Color.RED},
                                                    {Color.RED, Color.MAGENTA, Color.RED, Color.RED, Color.RED},
                                                    {Color.YELLOW, Color.YELLOW, Color.GREEN, Color.RED, Color.GREEN},
                                                    {Color.GREEN, Color.RED, Color.YELLOW, Color.YELLOW, Color.GREEN},
                                                    {Color.YELLOW, Color.YELLOW, Color.RED, Color.GREEN, Color.GREEN},
                                                    {Color.YELLOW, Color.GREEN, Color.YELLOW, Color.RED, Color.GREEN},
                                                    {Color.RED, Color.YELLOW, Color.GREEN, Color.GREEN, Color.YELLOW},
    };

    public static int[][] keyCipherIDs = {{4, 9, 12},
                                        {1, 8, 11},
                                        {2, 3, 11},
                                        {7, 10, 11},
                                        {6, 7, 10},
                                        {2, 7, 12},
                                        {2, 7, 13}
    };

    public CipherStatus(int map) {
        this.map = map;
        checkedKeys = new boolean[]{false,false,false};
        keys = new boolean[]{false,false,false};
    }

    public boolean[] getKeys() {
        return keys;
    }

    public int checkCipher(int cipher, boolean exists) {
        for (int i = 0; i < keyCipherIDs[map].length; i++) {
            int keyCipher = keyCipherIDs[map][i];
            if (keyCipher == cipher) {
                System.out.println("Checked key cipher: "+cipher+" with: "+exists);
                checkedKeys[i] = true;
                keys[i] = exists;
                return calcGroup();
            }
        }
        return -1;
    }

    public int calcGroup() {
        if (checkedKeys[0] == true && checkedKeys[1] == true && checkedKeys[2] == true) {
            // If all keys have been checked
            System.out.println("Comparing key booleans with library: "+ Arrays.toString(keys));
            for (int group = 0; group < cipherKeys[map].length; group++) {
                if (Arrays.equals(keys,cipherKeys[map][group])) {
                    return group;
                }
            }
            System.out.println("No match found. Something went wrong");
        }
        return -1;
    }

//    public boolean equals(CipherStatus other) {
//        return keys[0] == other.getKeys()[0] &&
//                keys[1] == other.getKeys()[1] &&
//                keys[2] == other.getKeys()[2];
//    }
}
