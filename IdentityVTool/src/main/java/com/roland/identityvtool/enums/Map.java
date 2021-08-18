package com.roland.identityvtool.enums;

public class Map {
    public static final int ARMS_FACTORY = 0;
    public static final int RED_CHURCH = 1;
    public static final int SACRED_HEART_HOSPITAL = 2;
    public static final int LAKESIDE_VILLAGE = 3;
    public static final int MOONLIT_RIVER_PARK = 4;
    public static final int LEOS_MEMORY = 5;
    public static final int EVERSLEEPING = 6;

    public static int[][] cipherCoordsX = {{442,583,611,546,502,270,84,163,235,226,391,456},
                                            {266,460,544,564,335,72,76,121,64,0,278,408},
                                            {375,534,554,544,427,191,74,99,218,0,312,348},
                                            {361,512,586,502,368,151,197,185,193,235,330},
                                            {442,747,993,850,958,697,316,112,40,179,367,551},
                                            {341,509,654,551,649,456,346,71,117,118,194,337},
                                            {327,379,495,406,491,403,340,36,86,163,214,260,272},
    };

    public static int[][] cipherCoordsY = {{115,190,280,560,607,567,520,268,209,97,405,321},
                                            {156,178,281,518,597,567,421,275,199,0,366,317},
                                            {97,179,418,611,568,580,500,245,117,0,478,360},
                                            {208,229,400,490,562,543,471,382,164,218,413},
                                            {193,192,208,363,546,538,624,534,324,202,426,385},
                                            {206,134,224,351,499,559,570,576,374,147,341,312},
                                            {127,153,170,349,417,593,524,392,204,181,470,423,307},
    };

    public static int[][][] cipherGroups = {{{1,3,5,6,7,10,11},{3,5,6,7,9,10,12},{1,2,5,6,7,9,11},{2,4,6,8,10,11,12},{1,3,4,6,7,8,11}},
                                            {{1,4,5,6,7,9,12},{2,4,5,6,8,9,11},{1,2,4,6,9,11,12},{2,3,4,5,7,9,11},{1,3,4,5,6,8,11}},
                                            {{2,3,5,7,8,9,11},{1,3,4,6,8,9,11},{2,3,5,6,7,9,12},{1,2,4,5,6,8,12},{1,2,4,5,7,8,11}},
                                            {{1,2,3,5,6,8,9},{1,2,3,5,6,10,11},{1,2,3,4,7,9,11},{1,3,4,6,8,9,11},{2,3,4,5,7,10,11}},
                                            {{1,3,5,7,9,10,12},{1,3,5,6,7,8,10},{1,2,5,6,8,9,11},{1,2,5,6,8,10,11},{1,3,4,6,7,9,11}},
                                            {{2,3,5,7,8,10,12},{1,2,3,5,6,9,10},{1,2,4,5,7,10,11},{1,3,5,6,8,9,12},{1,3,4,7,8,10,11}},
                                            {{3,5,6,8,10,12,13},{1,3,4,7,8,9,13},{1,3,5,6,8,9,12},{2,4,6,8,10,11,13},{2,4,7,8,9,12,13}},
    };

    public static String[][] survivorSpawns = {{"Box [Shack Gate]","Locker","~Factory Dungeon","Container [Shack Gate]","Right of Factory","~Shack Gate"},
                                                {"Wedding","Left of Wedding","Shack Window","~Graveyard Box","Graveyard","Right of Wedding","Behind Shack"},
                                                {"Ruins Box","~Forest","Statue Window","~Small Statue","2nd Story Forest Side","Above Ruins [Shack]"},
                                                {"Left Crab Shack","Crab Shack [Border]","2nd Floor Boat","1st Floor Boat","Docks","Crab Shack Pallet","Crab Shack [Small Boat]"},
                                                {"3rd Stop","Tent Cipher","Broken Carousel","God Kiting Chair","2nd Stop Stairs","Tent Entrance","4th Stop"},
                                                {"Moon Gate [Factory]","Left of Shack [Border]","~Factory Stairs","Triple Pallet","Left of Factory","~Water Tank","~Below Factory"},
                                                {"~Under Dragon Gate [Corner House]","Corner House","Outside 2 Story [2 Story]","Shit Pallet [Corner House]","Triple Pallet [Dancing Geisha]","~Alleyway","Dancing Geisha"},
    };

    public static String[][] hunterSpawns = {{"Water Tower","Factory","Deep Sandbags","Shack Gate","Shack Window","Big Rock"},
                                            {"Graveyard","Graveyard","Church Window","Front Wedding","Wedding","Top Breakable","Mid Church"},
                                            {"Statue Window","Statue","Ruins Box","Near Shack","Shit Corner","Hospital Entrance"},
                                            {"Shore","Big Boat","Big Rock","Walls","Shack Window","Cornfield","Small Boat"},
                                            {"God Kiting","Right of Broken Carousel","2 Story","1st Stop","Chair Bridge","Working Carousel","Side of Tent"},
                                            {"Left of Factory","Container Cipher","Triple Pallet","Right of Shack","Propane Tank","Factory","God Kiting"},
                                            {"Shit Corner","Fake Gate","Corner House Gate","Corner House","Dancing Geisha","Graveyard","Graveyard Gate"},
    };

    public static int getMap(String str) {
        str = str.toUpperCase();
        if (str.startsWith("A") || str.startsWith("F")) return Map.ARMS_FACTORY;
        if (str.startsWith("R") || str.startsWith("C")) return Map.RED_CHURCH;
        if (str.startsWith("S") || str.startsWith("H")) return Map.SACRED_HEART_HOSPITAL;
        if (str.startsWith("LA") || str.startsWith("V")) return Map.LAKESIDE_VILLAGE;
        if (str.startsWith("M")) return Map.MOONLIT_RIVER_PARK;
        if (str.startsWith("L") || str.startsWith("O")) return Map.LEOS_MEMORY;
        if (str.startsWith("E")) return Map.EVERSLEEPING;
        return Map.ARMS_FACTORY;
    }

    //public static int[][][] keyCiphers =
}
