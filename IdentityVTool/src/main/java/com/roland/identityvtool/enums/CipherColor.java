package com.roland.identityvtool.enums;

import java.awt.*;
import java.util.HashMap;

public class CipherColor {
    public static final int RED = 0;
    public static final int ORANGE = 1;
    public static final int YELLOW = 2;

    public static Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW};

    public static int getCipherColor(String str) {
        if (str.contentEquals("R")) return 0;
        if (str.contentEquals("O")) return 1;
        if (str.contentEquals("Y")) return 2;
        return -1;
    }
}
