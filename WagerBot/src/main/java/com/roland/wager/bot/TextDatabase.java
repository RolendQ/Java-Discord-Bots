package com.roland.wager.bot;

import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;

public class TextDatabase {
    public static EmbedBuilder[] getEmbeds(int page) {
        EmbedBuilder[] ebs = new EmbedBuilder[2];
        EmbedBuilder eb1 = new EmbedBuilder();
        eb1.setColor(Color.GRAY);
        eb1.setDescription(embedData[page][0]);
        ebs[0] = eb1;
        EmbedBuilder eb2 = new EmbedBuilder();
        eb2.setColor(Color.GRAY);
        eb2.setTitle(embedData[page][1]);
        if (page == 99) { // info specific ones
            //eb2.addField("**Shards** :diamond_shape_with_a_dot_inside:","``4``",true);
            //eb2.addField("**Power** :boom:","``1``",true);
        } else {
            eb2.setDescription(embedData[page][2]);
        }
        ebs[1] = eb2;
        return ebs;
    }

    public static String[][] embedData = {
            {"","",""},
            {"Wager is a number based card game where you take turns playing cards onto a board. At the end, whoever has the most points totaled from their board wins","Example of a Board",
                    "**Player (25 Total Points)**\n:red_square: **6**\n:blue_square: **4** **5**\n:green_square:\n:yellow_square: **10**\n:purple_square:"},
            {"Cards come in **5** different colors, :red_square::blue_square::green_square::yellow_square::purple_square: with the values **2** through **10**. There is exactly one :red_square: 2, :red_square: 3, :blue_square: 2, green_square: 3, etc","Example of Cards",
                    ":red_square: **2**, :red_square: **3**, :red_square: **4**, :red_square: **5**, :red_square: **6**, :red_square: **7**, :red_square: **8**, :red_square: **9**, :red_square: **10**, " +
                            "\n:blue_square: **2**, :blue_square: **3**, :blue_square: **4**, :blue_square: **5...**"},
            {"At the start of each turn, you can play a card in the pile of their respective color as long as its value is higher than the other cards of that pile. Your piles start empty so you can start with any card, but you must play in increasing order after that. Go ahead and play the :red_square: 5 by clicking on the :point_up_2: and then the corresponding " +
                    "letter :regional_indicator_a:","Your Hand",":red_square: :regional_indicator_a: **5** :regional_indicator_b: **8**\n" +
                    ":blue_square: :regional_indicator_c: **10**"},
            {"Great! Now the card is permanently on your board and will contribute to your score at the end","Play Piles",
                    "**Player**\n:red_square: **5**\n:blue_square:\n:green_square:\n:yellow_square:\n:purple_square:"},
            {"After you play a card, you must draw a card. Right now the Discard Piles are empty, so you can only draw from the Deck. Draw from the Deck and end your turn by clicking on the :card_box:","Discard Piles",
                    ":red_square:\n:blue_square:\n:green_square:\n:yellow_square:\n:purple_square:"},
            {"As your opponent takes his/her turn, the display for your Hand has updated. You just drew a :red_square: **4** as highlighted by the asterisk","Your Hand",
                    ":red_square: :regional_indicator_a: **4*** :regional_indicator_b: **8**\n:blue_square: :regional_indicator_c: **10**"},
            {"Unfortunately, you cannot play this card at all because its number is lower than the :red_square: **5** on your Board from last turn. Besides playing a card, you can also **discard** any card in your Hand. " +
                    "When a card is in a Discard Pile, it can be recovered later by either player instead of drawing from the Deck. Discard it now by clicking on the :fire: and the :regional_indicator_a:\n","Your Hand",
                    ":red_square: :regional_indicator_a: **4*** :regional_indicator_b: **8**\n:blue_square: :regional_indicator_c: **10**"},
            {"As with your previous turn, you must now draw a card. Because you cannot draw what you just discarded, your only option again is to draw from the Deck. Maybe you’ll get lucky and draw a useful card this time","Discard Piles",
                    ":red_square: **4**\n:blue_square:\n:green_square:\n:yellow_square:\n:purple_square:"},
            {"This time you drew a special card indicated by a :game_die: This is called a **Wager**. Its value is technically **zero** but when placed in its respective color pile it increases the Point Multiplier of that pile by 1. Meaning, the pile below without the " +
                    "Wager is worth **3+5+8 = 16x1 = 16** while the pile with one Wager is worth **16x2 = 32**. There are **3** Wagers per color, giving a maximum possible Pile Multiplier of 4x","Example of a Board",
                    ":red_square: **3** **5** **8** (1x)\n:blue_square: :game_die: **3** **5** **8** (2x)"},
            {"Because Wagers have a value of zero, they must be played before any number. Your blue pile is still empty, so you can go ahead and play the :blue_square: :game_die: by clicking on the :point_up_2: and the :regional_indicator_b:","Your Hand",
                    ":red_square: :regional_indicator_a: **8**\n:blue_square: :regional_indicator_b: :game_die:***** :regional_indicator_c: **10**"},
            {"It’s time to draw a card again. Looks like your opponent discarded a :blue_square: Wager last turn. Take it now, instead of drawing from the Deck, by clicking on the :blue_square:. Whenever you do this, you always " +
                    "recover the most recent and therefore rightmost card of that pile. This also ends your turn",
                    "Discard Piles",":red_square: **4**\n:blue_square: :game_die:\n:green_square:\n:yellow_square:\n:purple_square:"},
            {"Skipping ahead, the game ends as soon as the last card of the Deck is drawn. It is strategic for the player who is leading to draw cards from the Deck to end the game faster. " +
                    "Conversely, the person who needs more turns catching up should draw cards from a Discard Pile instead","Example of a Board",
                    "**Player**\n:red_square: :game_die: :game_die: **3 4 6 8 10**\n:blue_square: :game_die: **2 4 5 7 8**" +
                            "\n:green_square:\n:yellow_square: :game_die: **2 3 6 9 10**\n:purple_square:"},
            {"Besides adding up the base numbers and multiplying, there is another point mechanic to keep in mind. Every pile that has at least one card starts with **-20 points**" +
                    "This makes starting a new color, especially late in the game, risky because you would need at least a sum of 20 to make it profitable. Otherwise you may find yourself with negative points, which are still multiplied by the Pile Multiplier","Example of a Board",
                    "**Player**\n:red_square: :game_die: **6 8 10 (24-20=4, 4x2=8)**\n:blue_square: :game_die: :game_die: **2 3 10 (15-20=-5, -5x3=-15)**"},
            {"Finally, there is a substantial **20 point bonus** added to any pile with at least **8** cards in it, including Wagers. Remember that you must always add cards in increasing order, so low numbers are the best for getting these bonus points","Example of a Board",
                    "**Player**\n:red_square: :game_die: :game_die: :game_die: **6 8 9 (23-20=3, 3x4=12)**\n" +
                    ":blue_square: **2 3 4 5 6 7 8 (35-20=15, 15x1=15, 15+20=35)**"},
            {"Congratulations, you have finished the tutorial! Go ahead and click on :video_game: to play your first practice game","","Please send any feedback to **Rolend#1816**"}
    };
}
