package com.roland.whitehall.core;

import com.roland.whitehall.enums.LocationType;
import com.roland.whitehall.game.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class Utils {
    //public static SimpleGraph<Node, DefaultEdge> graph;
    public static HashSet<Node> set;
    // Hashmap
    public static HashMap<Node, ArrayList<Node>> map;
    public static Crossing[] cs;
    public static Location[] ls;
    public static int[] waterLocs = {66, 80, 95, 96, 98, 110, 111, 113, 131, 138, 143, 144, 154, 157, 158, 160, 166, 167, 174, 184};

    public static int[][] crossingCoords = {{0,0},{336,149},{416,136},{575,156},{669,176},{703,76},{1108,168},{1362,89},{1450,172},{1753,177},{1889,124},{137,364},{189,222},{227,300},{408,280},{458,374},{482,291},{628,292},{763,196},{816,317},{931,227},{956,309},{1098,329},{1186,251},{1334,294},{1422,320},{1564,235},
            {1686,291},{1897,263},{2049,282},{2147,341},{247,549},{355,456},{540,527},{556,411},{641,553},{683,416},{783,524},{845,424},{982,525},{1094,467},{1223,562},{1235,392},{1298,537},{1482,398},{1488,527},{1575,397},{1634,489},{1710,423},{1775,571},{1890,493},{2240,575},{148,737},
            {155,648},{315,647},{463,596},{516,690},{680,673},{830,601},{974,718} //<- 59
            ,{1100,603},{1388,694},{1634,671},{1934,723},{1955,767},{2108,693},{127,902},{284,821},{375,775},{424,862},{593,833},{667,960},{758,838},{801,923},{888,803},{1110,823} // <- 75
            ,{1111,953},{1226,827},{1322,913},
            {1460,793},{1546,841},{1720,968},{1748,805},{1850,838},{216,977},{280,1104},{385,1121},{528,1047},{593,1162},{730,1077},{911,984},{933,1143},{1144,1071},{1228,994},{1341,1102},{1380,988},{1493,1021},{1620,1120},{2189,1166},{164,1264},{287,1260},{325,1329},{455,1246},{678,1284},{850,1304},
            {1081,1181},{1109,1343},{1241,1364},{1281,1216},{1364,1323},{1534,1310},{1570,1226},{2015,1310},{2107,1223},{156,1431},{491,1400},{707,1410},{711,1488},{902,1483},{1034,1476},{1120,1417},{1357,1477},{1481,1506},{1920,1511},{1956,1429},{2104,1504},{2157,1383},{2229,1554},{242,1590},{307,1737},{449,1586},
            {460,1634},{597,1700},{741,1637},{862,1750},{895,1575},{907,1642},{1067,1629} // <- 137
            ,{1168,1604},{1443,1767},{1474,1566},{1873,1731},{1892,1587},{2029,1764},{2066,1640},{2174,1682},{130,1854},{311,1892},{501,1806},{833,1895},{924,1768},{935,1935},{1216,1794},{1857,1950},{1998,1842},{2123,1901},{105,1996},
            {115,2115},{298,2140},{339,1994},{465,1976},{616,2074},{715,1950},{799,2097},{925,2113},{1202,2134},{1206,1965},{1414,2141},{1426,2022},{1809,2154},{1911,2156},{1943,1982},{2058,2025},{134,2224},{459,2166}};
    public static int[][] locationCoords = {{0,0},{210,147},{647,112},{879,130},{1235,130},{1411,130},{1657,132},{2037,187},{115,271},{268,184},{303,295},{373,213},{448,208},{519,345},{594,214},{659,357},{696,253},{789,255},{878,271},{1027,309},{1250,204},{1282,349},{1382,238},{1518,208},{1650,339},{1830,219},{2076,372},{2216,255},{165,444},{285,388},{298,506},{406,528},{497,453},{600,479},{722,489},{834,486},{911,563},{983,421},
            {1097,390},{1099,537},{1199,504},{1295,457},{1431,441},{1566,569},{1603,438},{1824,394},{1834,537},{2198,459},{180,579},{237,647},{238,735},{409,699},{566,771},{599,681},{657,614},{725,761},{980,617},{1103,710},{1265,759},{1348,621},{1545,734},{1684,740},{1708,620},{1855,722},{2033,734},{2090,636},{2159,773},{2199,654},{204,869},{337,915},{477,956},{633,891},{825,815},{1001,803},{1112,891},{1222,923},
            {1391,840},{1438,938},{1599,926},{1774,906},{1913,912},{133,1022},{351,1058},{561,1106},{597,1000},{663,1120},{821,1120},{831,1016},{924,1060},{1087,1126},{1186,1039},{1267,1154},{1276,1043},{1473,1082},{1660,1058},{1794,1034},{2051,1168},{2078,982},{2240,1078},{246,1199},{408,1292},{418,1190},{585,1349},{639,1223},{782,1358},{912,1222},{1105,1274},{1210,1298},{1449,1279},{1453,1190},{1633,1363},{1708,1172},{1775,1333},{1961,1259},
            {1983,1369},{2085,1345},{2246,1283},{192,1516},{241,1378},{396,1480},{590,1552},{621,1464},{837,1495},{878,1396},{970,1482},{1054,1551},{1168,1530},{1180,1393},{1360,1400},{1425,1492},{1510,1403},{1845,1494},{2033,1467},{2129,1444},{301,1665},{441,1726},{543,1762},{671,1663},{753,1698},{833,1611},{986,1633},{1192,1692},{1462,1668},{1573,1579},{1797,1726},{1882,1654},{1949,1750},{1976,1611},{2049,1705},{2085,1573},{2117,1663},{2201,1620},
            {124,1928},{217,1801},{214,1941},{306,1813},{393,1825},{427,1902},{519,1875},{588,1959},{846,1821},{866,1947},{1067,1777},{1070,1947},{1213,1881},{1437,1895},{1539,1797},{1782,1944},{1869,1842},{1971,1913},{2052,1870},{2091,1964},{2181,1792},{145,2166},{222,2055},{447,2094},{630,2135},{765,2031},{924,2022},{1055,2123},{1208,2054},{1312,2018},{1310,2142},{1420,2079},{1534,2009},{1630,2148},{1839,2051},{1928,2070},{2010,2139},{294,2196}};
    public static int[][] locationAlleys = {{1,9},{9,10,11},{11,12,13},{12,14},{2,14,16},{2,16,17},{3,17,18},{3,19},{4,20},{5,20,22},{22,23},{6,24},{25,45},{7,25}, // Counted = c
            {28,29,30},{10,29},{13,14},{13,32,33},{13,15,33},{15,16,17},{18,37},{19,37,38},{21,38},{20,21},{21,22,41},{40,41,42},{23,24},{42,44},{24,44},{24,45,46},{45,26},{26,27}, // c
            {48,49},{30,31,51},{31,32},{33,53,54},{33,34},{34,35,54},{35,36,37},{36,56,73},{39,56},{39,40},{42,59},{43,44},{43,59},{43,46,62},{46,60,61,63,77,78,79,93,94},{26,46,47,65}, //
            {49,50},{50,68},{50,51},{51,52},{52,53,55},{55,71},{55,72},{56,57,73},{57,58},{58,59},{58,76},{43,60},{61,62,63},{64,65,67},
            {68,69},{69,70,82},{52,70,71,84},{71,87},{72,73,74,87},{74,75},{75,76},{88,89},{75,90},{76,77},{77,78},
            {81,99},{82,99,101},{83,101},{83,84,85},{85,86,102,103,104,105,119,121},{86,87,88},{89,105},{89,91,108,109},{90,91,92},{92,93,109},
            {99,118},{100,101},{100,117,118,119,134},{122,124,125,140},{126,129,141,142,164,165,181},{132,147,149},{133,149,150,151},
            {120,135,137,139},{152,153,154,155},{135,136,155,156},{136,137,139,156,159},{139,161,177},{140,141,162},{145,146,147,148},{148,150,170,172},{146,168,169,186,187},
            {161,162,163,164},{169,170,171},
            {173,174,175},{159,175,177},{161,178},{163,178,179,180},{180,181,182,183},{187,188},
            {173,175,189},{175,176,177}
    };
    public static int[][] locationWaterBlocks = {{66,98},{80,95,96,111,113},{110,131,143,144,166,167,184},{138,157,158,160},{154,174}};
//    public static ArrayList<Alley> alleys = new ArrayList<>();
//    public static ArrayList<WaterBlock> waterBlocks = new ArrayList<>();

    public static String[] turnStrings = {"0th","1st","2nd","3rd","4th","5th","6th","7th","8th","9th","10th","11th","12th","13th","14th","15th"};

    // Gets locations with in 1 move
    // Not being used currently TODO
    public static ArrayList<Location> getAdjLoc(Location loc) {
        ArrayList<Location> adjLocs = new ArrayList<Location>();

        Queue<Node> nodesQ = new LinkedList<Node>();
        nodesQ.add(loc);

        HashSet<Node> visited = new HashSet<Node>();

        while (!nodesQ.isEmpty()) {
            Node current = nodesQ.remove();
            for (Node adjCurrent : map.get(current)) {
                if (visited.contains(adjCurrent)) continue;
                visited.add(adjCurrent);

                if (adjCurrent instanceof Location) {
                    adjLocs.add((Location) adjCurrent);
                } else {
                    nodesQ.add(adjCurrent);
                }
            }
        }
        adjLocs.remove(loc); // Remove itself
        return adjLocs;
    }

    // Gets crossings within 1 move
    public static ArrayList<Crossing> getAdjCro(Crossing cro) {
        System.out.println("Checking adj crossings of crossing: "+cro.getTempID());
        ArrayList<Crossing> adjCros = new ArrayList<Crossing>();

        Queue<Node> nodesQ = new LinkedList<Node>();
        nodesQ.add(cro);

        HashSet<Node> visited = new HashSet<Node>();

        while (!nodesQ.isEmpty()) {
            Node current = nodesQ.remove();

            for (Node adjCurrent : map.get(current)) {
                if (visited.contains(adjCurrent)) continue;
                visited.add(adjCurrent);

                if (adjCurrent instanceof Crossing) {
                    adjCros.add((Crossing) adjCurrent);
                    System.out.println("Queue: Adding Crossing to list"+((Crossing) adjCurrent).getTempID());
                } else {
                    nodesQ.add(adjCurrent);
                    System.out.println("Queue: Adding Location"+((Location) adjCurrent).id);
                }
            }
        }
        adjCros.remove(cro); // Remove itself
        return adjCros;
    }

    public static HashSet<Crossing> getAllMoves(Crossing cro) {
        // TODO bug?
        System.out.println("Running getAllMoves for "+cro.getTempID()+" in Utils");
        ArrayList<Crossing> adjCros = getAdjCro(cro);
        HashSet<Crossing> moves = new HashSet<Crossing>();
        // get adj of adj
        for (Crossing adjCro : adjCros) {
            moves.add(adjCro);
            String debug = "Adjacents of "+adjCro.getTempID();
            for (Crossing crossing : getAdjCro(adjCro)) {
                moves.add(crossing); // no duplicates
                debug += " "+crossing.getTempID();
            }
            System.out.println(debug);

        }
        System.out.println("Returning from getAllMoves in Utils");
        //moves.remove(cro); // removes itself ?
        return moves;
    }

    // Returns adj nodes if they are locations
    public static ArrayList<Location> getAdjLoc(Crossing crossing) {
        ArrayList<Location> adjLocs = new ArrayList<Location>();

        for (Node n : map.get(crossing)) {
            if (n instanceof Location) {
                adjLocs.add((Location) n);
            }
        }
        return adjLocs;
    }

    public static void createGraph() {
        //graph = new SimpleGraph<Node, DefaultEdge>(DefaultEdge.class);
        //graph.addVertex(new Location())

        cs = new Crossing[175];
        for (int i = 1; i < cs.length; i++) {
            cs[i] = new Crossing();
        }

        ls = new Location[190];
        for (int i = 1; i < ls.length; i++) {
            ls[i] = new Location(i);
            // CHECK FOR WATER FIRST
            if (has(waterLocs,i)) {
                ls[i].type = LocationType.WATER;
                ls[i].waterBlocks = new ArrayList<WaterBlock>();
            } else if (range(i,1,3) || range(i,8,18) || range(i,28,36) || range(i,48,55) || range(i,68,72)) {
                ls[i].type = LocationType.ONE;
            } else if (range(i,5,7) || range(i,23,27) || range(i,42,47) || range(i,60,67) || range(i,77,79)) {
                ls[i].type = LocationType.TWO;
            } else if (range(i,117,123) || range(i,134,139) || range(i,152,161) || range(i,173,178)) {
                ls[i].type = LocationType.THREE;
            } else if (range(i,129,133) || range(i,142,151) || range(i,165,172) || range(i,183,188)) {
                ls[i].type = LocationType.FOUR;
            }

            // ONE: 1-3, 8-18, 28-36, 48-55, 68-72
            // TWO: 5-7, 23-27, 42-47, 60-67, 77-79
            // THREE: 117-123, 134-139, 152-161, 173-178
            // FOUR: 129-133, 142-151, 165-172, 183-188
        }

        // Alleys
        setupAlleys();
        setupWaterBlocks();

        map = new HashMap<Node, ArrayList<Node>>();
        map.put(ls[1],cAdj(cs[1],cs[12]));
        map.put(cs[1],cAdj(ls[1],cs[2],ls[9],ls[11]));
        map.put(cs[2],cAdj(cs[1],ls[12],cs[3]));
        map.put(cs[3],cAdj(cs[2],ls[2],ls[14]));
        map.put(ls[2],cAdj(cs[3],cs[5],cs[4]));
        map.put(cs[4],cAdj(ls[2],ls[16]));
        map.put(cs[5],cAdj(ls[2],cs[18]));
        map.put(ls[3],cAdj(cs[18],cs[6],cs[20]));
        map.put(cs[6],cAdj(ls[4],ls[3],cs[22],cs[23]));
        map.put(ls[4],cAdj(cs[6],cs[7]));
        map.put(cs[7],cAdj(ls[4],ls[20],ls[5]));
        map.put(ls[5],cAdj(cs[7],cs[8]));
        map.put(cs[8],cAdj(ls[5],ls[22],ls[23]));
        map.put(ls[6],cAdj(cs[9],cs[26]));
        map.put(cs[9],cAdj(ls[6],ls[25],cs[27]));

        map.put(cs[10],cAdj(ls[25],ls[7]));
        map.put(ls[7],cAdj(cs[10],cs[29]));
        map.put(ls[8],cAdj(cs[11],cs[12]));
        map.put(cs[11],cAdj(cs[13],ls[8],ls[28]));
        map.put(cs[12],cAdj(cs[13],ls[1],ls[8],ls[9]));
        map.put(cs[13],cAdj(cs[11],cs[12],ls[10],ls[29]));
        map.put(ls[9],cAdj(cs[1],cs[12]));
        map.put(ls[10],cAdj(cs[13],cs[14]));
        map.put(ls[11],cAdj(cs[1],cs[14]));
        map.put(cs[14],cAdj(cs[15],ls[10],ls[11]));
        map.put(cs[15],cAdj(cs[14],cs[32],ls[32],ls[13]));
        map.put(ls[12],cAdj(cs[2],cs[16]));
        map.put(cs[16],cAdj(ls[13],ls[12],ls[14]));
        map.put(ls[13],cAdj(cs[15],cs[16],cs[17],cs[34]));
        map.put(ls[14],cAdj(cs[3],cs[16],cs[17]));
        map.put(cs[17],cAdj(ls[13],ls[14],ls[15],ls[16]));
        map.put(ls[15],cAdj(cs[17],cs[36]));
        map.put(ls[16],cAdj(cs[4],cs[17],cs[18]));
        map.put(cs[18],cAdj(cs[5],ls[3],ls[16],ls[17]));
        map.put(ls[17],cAdj(cs[18],cs[19]));
        map.put(cs[19],cAdj(cs[36],cs[38],ls[17],ls[18]));
        map.put(ls[18],cAdj(cs[19],cs[20]));
        map.put(cs[20],cAdj(cs[21],ls[3],ls[18]));
        map.put(cs[21],cAdj(cs[20],ls[19],ls[37]));
        map.put(ls[19],cAdj(cs[21],cs[22]));
        map.put(cs[22],cAdj(cs[6],ls[19],ls[38],cs[23]));
        map.put(cs[23],cAdj(cs[6],cs[22],ls[20],ls[21]));
        map.put(ls[20],cAdj(cs[7],cs[23],cs[24]));
        map.put(cs[24],cAdj(ls[20],ls[21],ls[22]));
        map.put(ls[21],cAdj(cs[23],cs[24],cs[42]));
        map.put(ls[22],cAdj(cs[24],cs[25],cs[8]));
        map.put(cs[25],cAdj(ls[22],ls[23],ls[41],cs[44]));
        map.put(ls[23],cAdj(cs[8],cs[26],cs[25])); //
        map.put(cs[26],cAdj(ls[23],ls[6],ls[24]));
        map.put(ls[24],cAdj(cs[26],cs[27],cs[46],cs[48]));
        map.put(cs[27],cAdj(cs[9],ls[24],ls[45]));
        map.put(ls[25],cAdj(cs[9],cs[10],cs[28]));
        map.put(cs[28],cAdj(ls[45],ls[25],cs[29]));
        map.put(cs[29],cAdj(cs[28],ls[7],ls[26],ls[27]));
        map.put(ls[26],cAdj(cs[29],cs[30],cs[50]));
        map.put(cs[30],cAdj(ls[26],ls[27],ls[47]));
        map.put(ls[27],cAdj(cs[29],cs[30]));

        map.put(ls[28],cAdj(cs[11],cs[31]));
        map.put(cs[31],cAdj(ls[28],ls[30],ls[48],cs[54]));
        map.put(ls[29],cAdj(cs[13],cs[32]));
        map.put(cs[32],cAdj(ls[29],ls[30],ls[31],cs[33]));
        map.put(ls[30],cAdj(cs[31],cs[32]));
        map.put(ls[31],cAdj(cs[32],cs[55]));
        map.put(ls[32],cAdj(cs[15],cs[33]));
        map.put(cs[33],cAdj(cs[55],ls[32],ls[33],ls[53]));
        map.put(ls[33],cAdj(cs[33],cs[34],cs[35],cs[36]));
        map.put(ls[34],cAdj(cs[35],cs[36],cs[38]));
        map.put(cs[34],cAdj(ls[13],ls[33]));
        map.put(cs[35],cAdj(ls[33],ls[34],ls[54]));
        map.put(cs[36],cAdj(ls[33],ls[15],ls[34],cs[19]));
        map.put(cs[37],cAdj(ls[54],ls[35],cs[58]));
        map.put(cs[38],cAdj(ls[34],ls[35],ls[37],cs[19]));
        map.put(ls[35],cAdj(cs[37],cs[38]));
        map.put(ls[36],cAdj(cs[58],cs[39]));
        map.put(ls[37],cAdj(cs[21],cs[38],cs[39],cs[40]));
        map.put(cs[39],cAdj(ls[36],ls[37],ls[56],cs[40]));
        map.put(cs[40],cAdj(ls[37],ls[38],ls[39],ls[40],cs[39],cs[42]));
        map.put(ls[38],cAdj(cs[22],cs[40]));
        map.put(ls[39],cAdj(cs[40],cs[60]));
        map.put(ls[40],cAdj(cs[40],cs[43]));
        map.put(cs[41],cAdj(cs[60],cs[43],ls[58]));
        map.put(cs[42],cAdj(ls[21],ls[41],cs[40]));
        map.put(cs[43],cAdj(cs[41],ls[40],ls[42],ls[59]));
        map.put(ls[41],cAdj(cs[42],cs[25]));
        map.put(ls[42],cAdj(cs[43],cs[44],cs[45]));
        map.put(cs[44],cAdj(cs[25],ls[42],cs[46]));
        map.put(cs[45],cAdj(ls[42],ls[43],ls[44],ls[59]));
        map.put(cs[46],cAdj(ls[44],ls[24],cs[44]));
        map.put(ls[43],cAdj(cs[45],cs[47],cs[61],cs[62]));
        map.put(ls[44],cAdj(cs[45],cs[46],cs[47]));
        map.put(cs[47],cAdj(ls[43],ls[44],cs[48]));
        map.put(cs[48],cAdj(cs[47],ls[24],ls[46]));
        map.put(cs[49],cAdj(ls[46],ls[62],ls[63]));
        map.put(ls[45],cAdj(cs[27],cs[28],cs[50]));
        map.put(ls[46],cAdj(cs[48],cs[49],cs[50],cs[63]));
        map.put(cs[50],cAdj(ls[45],ls[46],ls[26]));
        map.put(ls[47],cAdj(cs[30],cs[51]));
        map.put(cs[51],cAdj(ls[47],ls[65],ls[67]));

        map.put(ls[48],cAdj(cs[31],cs[53]));
        map.put(cs[52],cAdj(cs[53],ls[50],cs[66]));
        map.put(cs[53],cAdj(cs[52],ls[48],ls[49]));
        map.put(ls[49],cAdj(cs[53],cs[54]));
        map.put(ls[50],cAdj(cs[52],cs[67],cs[54]));
        map.put(cs[54],cAdj(ls[49],ls[50],ls[51],cs[31]));
        map.put(ls[51],cAdj(cs[54],cs[55],cs[56],cs[68]));
        map.put(cs[55],cAdj(cs[56],ls[31],ls[51],cs[33]));
        map.put(cs[56],cAdj(cs[55],ls[51],ls[52],ls[53]));
        map.put(ls[52],cAdj(cs[56],cs[69],cs[70]));
        map.put(ls[53],cAdj(cs[56],cs[33],cs[57]));
        map.put(ls[54],cAdj(cs[35],cs[57],cs[37]));
        map.put(ls[55],cAdj(cs[57],cs[70],cs[72]));
        map.put(cs[57],cAdj(ls[53],ls[54],ls[55],cs[58]));
        map.put(cs[58],cAdj(cs[57],cs[37],ls[36],cs[74]));
        map.put(cs[59],cAdj(ls[56],ls[73])); //
        map.put(ls[56],cAdj(cs[39],cs[59],cs[60]));
        map.put(cs[60],cAdj(ls[39],ls[56],ls[57],cs[41]));
        map.put(ls[57],cAdj(cs[60],cs[75]));
        map.put(ls[58],cAdj(cs[41],cs[77],cs[61])); //
        map.put(ls[59],cAdj(cs[43],cs[45],cs[61]));
        map.put(cs[61],cAdj(ls[43],ls[58],ls[59],cs[79])); //
        map.put(ls[60],cAdj(cs[79],cs[62])); //
        map.put(cs[62],cAdj(ls[43],ls[60],ls[61],ls[62])); //
        map.put(ls[61],cAdj(cs[62],cs[82]));
        map.put(ls[62],cAdj(cs[62],cs[49]));
        map.put(ls[63],cAdj(cs[49],cs[82]));
        map.put(cs[63],cAdj(cs[64],ls[46]));
        map.put(cs[64],cAdj(cs[63],ls[64],cs[83],ls[97]));
        map.put(ls[64],cAdj(cs[64],cs[65]));
        map.put(cs[65],cAdj(ls[64],ls[67],ls[66]));
        map.put(ls[65],cAdj(cs[63],cs[51]));
        map.put(ls[66],cAdj(cs[65]));
        map.put(ls[67],cAdj(cs[51],cs[65]));

        map.put(cs[66],cAdj(cs[52],ls[68]));
        map.put(ls[68],cAdj(cs[66],cs[67],cs[84]));
        map.put(cs[67],cAdj(ls[50],ls[68],ls[69],cs[68]));
        map.put(ls[69],cAdj(cs[67],cs[69],cs[84]));
        map.put(cs[68],cAdj(cs[67],ls[51],cs[69]));
        map.put(cs[69],cAdj(cs[68],ls[69],ls[70],ls[52]));
        map.put(ls[70],cAdj(cs[69],cs[87]));
        map.put(cs[70],cAdj(ls[52],ls[55],ls[71]));
        map.put(ls[71],cAdj(cs[70],cs[71],cs[72]));
        map.put(cs[71],cAdj(ls[71],ls[84],cs[89]));
        map.put(cs[72],cAdj(ls[55],ls[71],ls[72],cs[73])); //
        map.put(cs[73],cAdj(cs[72],ls[72],ls[87])); //
        map.put(ls[72],cAdj(cs[72],cs[73],cs[74])); //
        map.put(cs[74],cAdj(ls[72],ls[73],cs[58])); //
        map.put(ls[73],cAdj(cs[74],cs[75],cs[59])); //
        map.put(cs[75],cAdj(ls[73],ls[57],ls[74],cs[77])); //
        map.put(cs[76],cAdj(ls[74],ls[75],cs[92],cs[90])); //
        map.put(cs[77],cAdj(cs[75],ls[58],ls[75],ls[76])); //
        map.put(ls[74],cAdj(cs[75],cs[76])); //
        map.put(ls[75],cAdj(cs[76],cs[77],cs[93])); //
        map.put(cs[78],cAdj(cs[93],ls[76],cs[95])); //
        map.put(ls[76],cAdj(cs[77],cs[78],cs[79])); //
        map.put(cs[79],cAdj(ls[76],cs[61],cs[80],ls[60])); //
        map.put(cs[80],cAdj(ls[77],ls[78],cs[79])); //
        map.put(ls[77],cAdj(cs[80],cs[95],cs[96])); //
        map.put(ls[78],cAdj(cs[80],cs[96])); //
        map.put(cs[81],cAdj(ls[94],ls[95],ls[79])); //
        map.put(ls[79],cAdj(cs[81],cs[83])); //
        map.put(cs[82],cAdj(ls[61],ls[63])); //
        map.put(cs[83],cAdj(cs[64],ls[79],ls[80])); //
        map.put(ls[80],cAdj(cs[83])); //

        map.put(ls[81],cAdj(cs[99],cs[84]));
        map.put(cs[84],cAdj(ls[68],ls[81],ls[69],cs[85]));
        map.put(cs[85],cAdj(cs[84],ls[82],ls[99]));
        map.put(ls[82],cAdj(cs[85],cs[86]));
        map.put(cs[86],cAdj(ls[82],ls[101],cs[87]));
        map.put(cs[87],cAdj(cs[86],ls[70],ls[83],ls[84])); //
        map.put(ls[83],cAdj(cs[87],cs[88]));
        map.put(ls[84],cAdj(cs[87],cs[71]));
        map.put(cs[88],cAdj(ls[83],ls[85],ls[103],cs[102])); //
        map.put(ls[85],cAdj(cs[88],cs[89]));
        map.put(cs[89],cAdj(ls[85],ls[86],ls[87],cs[71])); //
        map.put(ls[86],cAdj(cs[89],cs[91])); //
        map.put(ls[87],cAdj(cs[73],cs[89],cs[90])); //
        map.put(ls[88],cAdj(cs[90],cs[91])); //
        map.put(cs[90],cAdj(ls[87],ls[88],cs[76])); //
        map.put(cs[91],cAdj(ls[86],ls[88],ls[89],ls[105])); //
        map.put(ls[89],cAdj(cs[91],cs[92],cs[108],cs[105])); //
        map.put(cs[92],cAdj(cs[76],ls[89],ls[90],ls[91])); //
        map.put(ls[90],cAdj(cs[92],cs[93])); //
        map.put(cs[93],cAdj(ls[75],ls[90],ls[92],cs[78])); //
        map.put(ls[91],cAdj(cs[92],cs[94])); //
        map.put(ls[92],cAdj(cs[93],cs[94])); //
        map.put(cs[94],cAdj(ls[91],ls[92],ls[109])); //
        map.put(ls[93],cAdj(cs[95],cs[111])); //
        map.put(cs[95],cAdj(ls[77],cs[78],ls[93])); //
        map.put(cs[96],cAdj(ls[77],ls[78])); //
        map.put(cs[97],cAdj(ls[94],ls[111],cs[111])); //
        map.put(ls[94],cAdj(cs[97],cs[81])); //
        map.put(ls[95],cAdj(cs[81])); //
        map.put(ls[96],cAdj(cs[113])); //
        map.put(ls[97],cAdj(cs[64],cs[98])); //
        map.put(cs[98],cAdj(ls[98],ls[97],ls[116],cs[113])); //
        map.put(ls[98],cAdj(cs[98])); //

        map.put(cs[99],cAdj(ls[81],ls[99],cs[114]));
        map.put(ls[99],cAdj(cs[99],ls[85],cs[100]));
        map.put(cs[100],cAdj(ls[99],ls[101],cs[101]));
        map.put(cs[101],cAdj(cs[100],ls[100],ls[118]));
        map.put(ls[100],cAdj(cs[101],cs[102],cs[115]));
        map.put(ls[101],cAdj(cs[100],cs[102],cs[86]));
        map.put(ls[102],cAdj(cs[103],cs[115]));
        map.put(cs[102],cAdj(ls[100],ls[101],cs[88])); //
        map.put(ls[103],cAdj(cs[103],cs[88])); //
        map.put(cs[103],cAdj(ls[103],ls[102])); //
        map.put(ls[104],cAdj(cs[104],cs[116]));
        map.put(cs[104],cAdj(ls[104],ls[123],ls[105]));
        map.put(ls[105],cAdj(cs[104],cs[91],cs[105]));
        map.put(cs[105],cAdj(ls[105],ls[89],ls[106]));
        map.put(ls[106],cAdj(cs[105],cs[106]));
        map.put(cs[106],cAdj(ls[106],ls[107],cs[120]));
        map.put(ls[107],cAdj(cs[106],cs[107],cs[108]));
        map.put(cs[107],cAdj(ls[107],ls[127],cs[109]));
        map.put(cs[108],cAdj(ls[89],ls[107],ls[108])); //
        map.put(cs[109],cAdj(cs[107],ls[128],ls[108]));
        map.put(ls[108],cAdj(cs[108],cs[109],cs[110]));
        map.put(cs[110],cAdj(ls[108],ls[110],ls[130],cs[111]));
        map.put(cs[111],cAdj(cs[110],cs[97],ls[93],ls[109],ls[112]));
        map.put(ls[109],cAdj(cs[94],cs[111]));
        map.put(ls[110],cAdj(cs[110]));
        map.put(ls[111],cAdj(cs[97]));
        map.put(ls[112],cAdj(cs[111],cs[124]));
        map.put(ls[113],cAdj(cs[112]));
        map.put(ls[114],cAdj(cs[112],cs[124])); //
        map.put(cs[112],cAdj(ls[113],ls[114],ls[115],cs[113])); //
        map.put(ls[115],cAdj(cs[112],cs[126])); //
        map.put(cs[113],cAdj(cs[112],ls[96],cs[98])); //
        map.put(ls[116],cAdj(cs[98],cs[126])); //

        map.put(cs[114],cAdj(cs[99],ls[117],ls[118]));
        map.put(ls[117],cAdj(cs[114],cs[128]));
        map.put(ls[118],cAdj(cs[114],cs[101]));
        map.put(ls[119],cAdj(cs[115],cs[130]));
        map.put(cs[115],cAdj(ls[102],ls[100],ls[119]));
        map.put(ls[120],cAdj(cs[131],cs[117]));
        map.put(cs[116],cAdj(ls[121],ls[122],ls[104])); //
        map.put(ls[121],cAdj(cs[130],cs[116])); //
        map.put(ls[122],cAdj(cs[116],cs[135])); //
        map.put(cs[117],cAdj(ls[120],ls[139])); //
        map.put(cs[118],cAdj(ls[122],ls[123],ls[124])); //
        map.put(ls[123],cAdj(cs[104],cs[118])); //
        map.put(ls[124],cAdj(cs[118],cs[119])); //
        map.put(cs[119],cAdj(ls[124],ls[125]));
        map.put(ls[125],cAdj(cs[119],cs[137]));
        map.put(ls[126],cAdj(cs[120],cs[138],cs[121]));
        map.put(cs[120],cAdj(cs[106],ls[127],ls[126]));
        map.put(ls[127],cAdj(cs[120],cs[107]));
        map.put(cs[121],cAdj(ls[126],ls[128],ls[129]));
        map.put(ls[128],cAdj(cs[109],cs[121]));
        map.put(ls[129],cAdj(cs[121],cs[122]));
        map.put(ls[130],cAdj(cs[110],cs[122]));
        map.put(cs[122],cAdj(ls[129],ls[130],cs[140]));
        map.put(ls[131],cAdj(cs[123]));
        map.put(cs[123],cAdj(cs[142],ls[131],cs[124]));
        map.put(cs[124],cAdj(cs[123],ls[112],ls[114],ls[132]));
        map.put(ls[132],cAdj(cs[124],cs[125]));
        map.put(cs[125],cAdj(ls[132],ls[133],ls[149]));
        //map.put(ls[132],cAdj(cs[125],cs[126],cs[127]));
        map.put(cs[126],cAdj(ls[115],ls[116],ls[133]));
        map.put(cs[127],cAdj(ls[133],ls[151]));
        map.put(ls[133],cAdj(cs[125],cs[126],cs[127]));

        map.put(cs[128],cAdj(ls[117],ls[134]));
        map.put(ls[134],cAdj(cs[128],cs[130],cs[129]));
        map.put(cs[129],cAdj(ls[134],ls[135],ls[153],ls[155]));
        map.put(cs[130],cAdj(ls[119],ls[121],ls[134]));
        map.put(ls[135],cAdj(cs[129],cs[132]));
        map.put(ls[136],cAdj(cs[132],cs[148]));
        map.put(cs[131],cAdj(cs[129],ls[120]));
        map.put(cs[132],cAdj(ls[136],ls[135],ls[137]));
        map.put(ls[137],cAdj(cs[132],cs[133]));
        map.put(cs[133],cAdj(ls[137],ls[138],ls[139]));
        map.put(ls[138],cAdj(cs[133])); //
        map.put(ls[139],cAdj(cs[117],cs[133],cs[134],cs[135])); //
        map.put(cs[134],cAdj(ls[139],ls[160])); //
        map.put(cs[135],cAdj(cs[136],ls[122]));
        map.put(cs[136],cAdj(cs[135],ls[140],cs[150]));
        map.put(ls[140],cAdj(cs[136],cs[137]));
        map.put(cs[137],cAdj(ls[125],ls[140],cs[138]));
        map.put(cs[138],cAdj(cs[137],ls[126],ls[141]));
        map.put(ls[141],cAdj(cs[138],cs[152]));
        map.put(cs[139],cAdj(ls[142],ls[165],ls[166]));
        map.put(cs[140],cAdj(cs[122],ls[142],ls[143]));
        map.put(ls[142],cAdj(cs[139],cs[140]));
        map.put(ls[143],cAdj(cs[140]));
        map.put(ls[144],cAdj(cs[141]));
        map.put(cs[141],cAdj(ls[144],ls[145],ls[146],ls[168]));
        map.put(ls[145],cAdj(cs[142],cs[141]));
        map.put(cs[142],cAdj(ls[145],ls[147],cs[123]));
        map.put(ls[146],cAdj(cs[141],cs[143]));
        map.put(ls[147],cAdj(cs[142],cs[144]));
        map.put(cs[143],cAdj(ls[146],ls[148],cs[154]));
        map.put(cs[144],cAdj(ls[147],ls[148],ls[149],ls[150]));
        map.put(cs[145],cAdj(ls[150],ls[151],ls[172]));
        map.put(ls[148],cAdj(cs[143],cs[144]));
        map.put(ls[149],cAdj(cs[144],cs[125]));
        map.put(ls[150],cAdj(cs[144],cs[145]));
        map.put(ls[151],cAdj(cs[127],cs[145]));

        map.put(cs[146],cAdj(ls[152],ls[153]));
        map.put(ls[152],cAdj(cs[146],cs[156]));
        map.put(ls[153],cAdj(cs[146],cs[129]));
        map.put(ls[154],cAdj(cs[156],cs[147]));
        map.put(ls[155],cAdj(cs[147],cs[129]));
        map.put(cs[147],cAdj(ls[154],ls[155],ls[156],cs[159]));
        map.put(ls[156],cAdj(cs[147],cs[148]));
        map.put(cs[148],cAdj(ls[136],ls[156],ls[158]));
        map.put(ls[157],cAdj(cs[160]));
        map.put(ls[158],cAdj(cs[148]));
        map.put(ls[159],cAdj(cs[160],cs[162]));
        map.put(cs[149],cAdj(cs[162],ls[177],ls[160]));
        map.put(ls[160],cAdj(cs[149],cs[134]));
        map.put(cs[150],cAdj(cs[136],ls[161],ls[162]));
        map.put(cs[151],cAdj(ls[161],ls[163],ls[178]));
        map.put(ls[161],cAdj(cs[150],cs[151],cs[163]));
        map.put(ls[162],cAdj(cs[150],cs[152]));
        map.put(cs[152],cAdj(ls[162],ls[141],ls[164]));
        map.put(ls[163],cAdj(cs[151],cs[166]));
        map.put(ls[164],cAdj(cs[166],cs[152]));
        map.put(ls[165],cAdj(cs[139],cs[168]));
        map.put(ls[166],cAdj(cs[139]));
        map.put(ls[167],cAdj(cs[153]));
        map.put(cs[153],cAdj(ls[167],ls[168],ls[186]));
        map.put(ls[168],cAdj(cs[141],cs[153]));
        map.put(ls[169],cAdj(cs[171],cs[154]));
        map.put(cs[154],cAdj(cs[143],ls[169],ls[170]));
        map.put(ls[170],cAdj(cs[154],cs[155]));
        map.put(cs[155],cAdj(ls[170],ls[171],ls[172]));
        map.put(ls[171],cAdj(cs[155],cs[172]));
        map.put(ls[172],cAdj(cs[145],cs[155]));

        map.put(cs[156],cAdj(ls[152],ls[154]));
        map.put(cs[157],cAdj(ls[173],ls[174]));
        map.put(ls[173],cAdj(cs[157],cs[173],cs[158]));
        map.put(ls[174],cAdj(cs[157],cs[159]));
        map.put(cs[158],cAdj(ls[173],ls[175]));
        map.put(cs[159],cAdj(cs[147],ls[174],ls[175],cs[160]));
        map.put(cs[160],cAdj(ls[157],ls[159],ls[175],cs[159]));
        map.put(ls[175],cAdj(cs[158],cs[159],cs[160],cs[161],cs[174]));
        map.put(cs[161],cAdj(ls[175],ls[177]));
        map.put(ls[176],cAdj(cs[174],cs[163]));
        map.put(ls[177],cAdj(cs[149],cs[162],cs[161],cs[163]));
        map.put(cs[162],cAdj(cs[149],ls[159],ls[177]));
        map.put(cs[163],cAdj(ls[161],ls[176],ls[177],cs[164]));
        map.put(cs[164],cAdj(cs[163],ls[178],ls[179]));
        map.put(ls[178],cAdj(cs[151],cs[164]));
        map.put(ls[179],cAdj(cs[164],cs[165]));
        map.put(cs[165],cAdj(ls[179],ls[180],ls[182]));
        map.put(ls[180],cAdj(cs[165],cs[166]));
        map.put(cs[166],cAdj(ls[163],ls[164],ls[180],ls[181]));
        map.put(ls[181],cAdj(cs[166],cs[168]));
        map.put(ls[182],cAdj(cs[165],cs[167]));
        map.put(ls[183],cAdj(cs[167],cs[168]));
        map.put(cs[167],cAdj(ls[182],ls[183],ls[185])); //
        map.put(cs[168],cAdj(ls[165],ls[181],ls[183],ls[184])); //
        map.put(ls[184],cAdj(cs[168])); //
        map.put(ls[185],cAdj(cs[167],cs[169])); //
        map.put(ls[186],cAdj(cs[153],cs[169])); //
        map.put(cs[169],cAdj(ls[185],ls[186],cs[170]));
        map.put(cs[170],cAdj(cs[169],ls[187],ls[188]));
        map.put(ls[187],cAdj(cs[170],cs[171]));
        map.put(cs[171],cAdj(ls[169],ls[187],cs[172]));
        map.put(cs[172],cAdj(cs[171],ls[171],ls[188]));
        map.put(cs[173],cAdj(ls[173],ls[189]));
        map.put(ls[188],cAdj(cs[172],cs[170]));
        map.put(ls[189],cAdj(cs[173],cs[174]));
        map.put(cs[174],cAdj(ls[189],ls[176]));
        System.out.println("Created graph");
    }

    public static ArrayList<Node> cAdj(Node n1) {
        ArrayList<Node> adj = new ArrayList<Node>();
        adj.add(n1);
        return adj;
    }

    public static ArrayList<Node> cAdj(Node n1, Node n2) {
        ArrayList<Node> adj = new ArrayList<Node>();
        adj.add(n1);
        adj.add(n2);
        return adj;
    }

    public static ArrayList<Node> cAdj(Node n1, Node n2, Node n3) {
        ArrayList<Node> adj = new ArrayList<Node>();
        adj.add(n1);
        adj.add(n2);
        adj.add(n3);
        return adj;
    }

    public static ArrayList<Node> cAdj(Node n1, Node n2, Node n3, Node n4) {
        ArrayList<Node> adj = new ArrayList<Node>();
        adj.add(n1);
        adj.add(n2);
        adj.add(n3);
        adj.add(n4);
        return adj;
    }

    public static ArrayList<Node> cAdj(Node n1, Node n2, Node n3, Node n4, Node n5) {
        ArrayList<Node> adj = new ArrayList<Node>();
        adj.add(n1);
        adj.add(n2);
        adj.add(n3);
        adj.add(n4);
        adj.add(n5);
        return adj;
    }

    public static ArrayList<Node> cAdj(Node n1, Node n2, Node n3, Node n4, Node n5, Node n6) {
        ArrayList<Node> adj = new ArrayList<Node>();
        adj.add(n1);
        adj.add(n2);
        adj.add(n3);
        adj.add(n4);
        adj.add(n5);
        adj.add(n6);
        return adj;
    }

    public static int indexOfCrossing(Crossing crossing) {
        int i = 0;
        for (Crossing c : cs) {
            if (c == crossing) return i;
            i++;
        }
        return -1;
    }

    public static boolean has(int[] array, int n) {
        for (int num : array) {
            if (num == n) return true;
        }
        return false;
    }

    public static boolean range(int i, int low, int high) {
        return (i >= low && i <= high);
    }

    public static boolean areValidBodyPartLocs(String[] ids) {
        // 4 numbers, 1 of each corner, (no water)
        return ids.length == 4 && count(ids, LocationType.ONE) == 1 && count(ids, LocationType.TWO) == 1
                && count(ids, LocationType.THREE) == 1 && count(ids, LocationType.FOUR) == 1;
    }

    public static int count(String[] ids, int locType) {
        int count = 0;
        for (String id : ids) {
            if (ls[Integer.parseInt(id)].type == locType) count++;
        }
        return count;
    }

    public static BufferedImage scaleImage(BufferedImage img, int width, int height,
                                           Color background) {
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        if (imgWidth*height < imgHeight*width) {
            width = imgWidth*height/imgHeight;
        } else {
            height = imgHeight*width/imgWidth;
        }
        BufferedImage newImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = newImage.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setBackground(background);
            g.clearRect(0, 0, width, height);
            g.drawImage(img, 0, 0, width, height, null);
        } finally {
            g.dispose();
        }
        return newImage;
    }

    public static void setupAlleys() {
        for (int[] alleyIDs : locationAlleys) {
            // Create alley object and add to locations
            Alley alley = new Alley(alleyIDs);
            for (int alleyID : alleyIDs) {
                ls[alleyID].alleys.add(alley);
            }
        }
    }

    public static void setupWaterBlocks() {
        for (int[] waterBlockIDs : locationWaterBlocks) {
            // Create alley object and add to locations
            WaterBlock waterBlock = new WaterBlock(waterBlockIDs);
            for (int waterBlockID : waterBlockIDs) {
                ls[waterBlockID].waterBlocks.add(waterBlock);
            }
        }
    }

    public static boolean isCommand(String message) {
        try {
            String copy = message.replace(" ","");
            Integer.parseInt(copy);
            return true;
        } catch (NumberFormatException e) {
            return GlobalVars.letterToInt(message) != -1;
        }
    }
}
