package com.roland.bang.game;

import com.roland.bang.core.GlobalVars;
import com.roland.bang.core.Utils;
import com.roland.bang.enums.Character;
import com.roland.bang.enums.GameStatusType;
import com.roland.bang.enums.Role;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Game {
    public Game(MessageChannel channel, Guild guild, String authorID) {
        this.channel = channel;
        this.guild = guild;

        status = GameStatusType.PREGAME;
        dice = new Die[]{new Die(0), new Die(1), new Die(2), new Die(3), new Die(4)};
        arrowsLeft = 9;
        confirmedDice = false;

        String name = guild.getMemberById(authorID).getEffectiveName();
        Player p1 = new Player(authorID,name);
        players.add(p1);
        p1.setNextPlayer(p1);

        channel.sendMessage(name + " is starting a game of Bang!").queue();
    }

    public void addPlayer(String playerID) {
        String name = guild.getMemberById(playerID).getEffectiveName();
        Player p = new Player(playerID,name);
        players.add(p);

        channel.sendMessage("Added Player: "+name).queue();
    }

    public void start() {
        // Shuffle players
        Collections.shuffle(players);

        // Set next/prev players
        if (players.size() != 1) {
            for (int i = 0; i < players.size(); i++) {
                Player next = players.get((i + 1) % players.size());
                players.get(i).setNextPlayer(next);
                next.setPrevPlayer(players.get(i));
            }
        }

        // Assign unique characters (and their respective health)
        HashSet<Integer> existingCharacters = new HashSet<Integer>();
        Random r = new Random();
        int characterID;
        for (int i = 0; i < players.size(); i++) {
            // Replace with Collections.shuffle?
            do {
                characterID = r.nextInt(GlobalVars.characters.length);
            } while (existingCharacters.contains(characterID));
            existingCharacters.add(characterID);
            players.get(i).setCharacterID(characterID);
            players.get(i).setCharacter(GlobalVars.characters[characterID]);
            players.get(i).setHealth(GlobalVars.healths[characterID]);
        }

        // Assign roles S, D, O, R
        ArrayList<Integer> roles = new ArrayList<Integer>();
        for (int i = 0; i < players.size(); i++) {
            roles.add(GlobalVars.availableRoles.get(i));
        }
        Collections.shuffle(roles);
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setRole(roles.get(i));
            if (roles.get(i) == Role.SHERIFF) {
                sheriff = players.get(i);
                sheriff.updateHealth(2); // Sheriff starts with 2 more health
            }
        }

        for (Player p : players) {
            User user = guild.getMemberById(p.getPlayerID()).getUser();
            System.out.println(user.getName() + " got a dm");
//            user.openPrivateChannel().queue(new Consumer<PrivateChannel>() {
//                public void accept(PrivateChannel channel) {
//                    String msg = "1 You are playing as **"+p.getCharacter()+"**\nYour role is "+GlobalVars.roleIndicators[p.getRole()];
//                    System.out.println(msg);
//                    channel.sendMessage(msg).queue();
//                }
//            });

            user.openPrivateChannel().queueAfter(1000, TimeUnit.MILLISECONDS, (new Consumer<PrivateChannel>() {
                public void accept(PrivateChannel channel) {
                    String msg = "2 You are playing as **"+p.getCharacter()+"**\nYour role is "+GlobalVars.roleIndicators[p.getRole()];
                    System.out.println(msg);
                    channel.sendMessage(msg).queue();
                }
            }));
        }

        drawStartingBoard();

        updateAlivePlayers();

        System.out.println("Starting...");
        status = GameStatusType.IN_PROGRESS;
        currentPlayer = sheriff;
        newTurn();
    }

    public void newTurn() {
        if (status == GameStatusType.IN_PROGRESS) {
            activatedAbilityPlayer = null;
            drawBoard();
            System.out.println("Rolling 5 dice for " + currentPlayer.getDisplayName());
            rerollsLeft = 3;
            if (currentPlayer.getCharacterID() == Character.LUKE) {
                rerollsLeft++;
            }
            currentInput = -1; // Don't take inputs
            confirmedDice = false;
            if (currentPlayer.getCharacterID() == Character.SID) {
                activatedAbilityPlayer = currentPlayer;
                promptAbility(currentPlayer);
                return;
            }
            roll(null,false);
        }
    }

    public void roll(int[] ids, boolean isReroll) {
        if (rerollsLeft == 0 || confirmedDice) return;

        // -1 is all
        if (ids == null) {
            for (Die d : dice) {
                if (isReroll && currentPlayer.getCharacterID() == Character.JACK && !hasThreeDynamite()) {
                    System.out.println("Rerolled dynamite");
                    d.roll(false); // Can reroll dynamite as Jack
                } else {
                    d.roll(isReroll); // Dynamite is ignored
                }
                if (d.currentState == Die.ARROW) {
                    resolveArrow(currentPlayer,1);
                }
                if (alivePlayers.size() <= 3 && d.currentState == Die.SHOOT_2) {
                    d.setCurrentState(Die.SHOOT_1);
                }
            }
        } else {
            for (int id : ids) {
                dice[id].roll(isReroll); // Dynamite is ignored
                if (dice[id].currentState == Die.ARROW) {
                    resolveArrow(currentPlayer,1);
                }
                if (alivePlayers.size() <= 3 && dice[id].currentState == Die.SHOOT_2) {
                    dice[id].setCurrentState(Die.SHOOT_1);
                }
            }
        }

        rerollsLeft--;

        // Update dice display TODO
        displayDice();

        if (hasThreeDynamite()) {
            updateHealth(null, currentPlayer, -1);
            channel.sendMessage("Three dynamite :boom:! **"+currentPlayer.getHealth()+"** :heart: left, confirmed rolls").queue();
            confirmRolls();
        } else if (rerollsLeft == 0) {
            confirmRolls();
            //channel.sendMessage("Ran out of rolls, confirmed rolls").queue();
        }
    }

    // Player confirms rolls or runs out of rerolls
    public boolean confirmRolls() {
        if (!confirmedDice) {
            System.out.println("Confirmed rolls");
            confirmedDice = true;
            // Refresh board and dice
            drawBoard();
            displayDice();
            clearResults();
            for (Die d : dice) {
                results.put(d.currentState,results.get(d.currentState)+1);
            }
            if (currentPlayer.getCharacterID() == Character.SUZY && results.get(Die.SHOOT_1) == 0 && results.get(Die.SHOOT_2) == 0) {
                updateHealth(null, currentPlayer, 2);
                channel.sendMessage("**Suzy** :star: Healed **2**").queue();
            }
//            if (!hasActionsLeft()) endTurn(); // automatically end turn if no actions
//            else {
                // Prompt inputs to shoot1, shoot2, and heal
                currentInput = Die.SHOOT_1;
                promptInput();
//            }
            return true;
        }
        return false;
    }

    public void promptInput() {
        if (status != GameStatusType.IN_PROGRESS) return;

        if (currentInput > Die.BEER) {
            endTurn(); // Automatically ends turn
            return;
        }

        if (currentInput == -2) { // From gatling gun (Bart)
            nextPlayer();
            return;
        }

        if (currentInput == -1) { // From new turn (Sid)
            roll(null,false);
            return;
        }

        if (results.get(currentInput) > 0) {
            if (currentInput == Die.SHOOT_1) {
                channel.sendMessage(GlobalVars.diceEmoji[Die.SHOOT_1] + " Choose someone to shoot exactly **one** space away **("+results.get(Die.SHOOT_1)+"x)**").queue();
            } else if (currentInput == Die.SHOOT_2) {
                channel.sendMessage(GlobalVars.diceEmoji[Die.SHOOT_2] + " Choose someone to shoot exactly **two** spaces away **("+results.get(Die.SHOOT_2)+"x)**").queue();
            } else if (currentInput == Die.BEER) {
                channel.sendMessage(GlobalVars.diceEmoji[Die.BEER] + " Choose someone to heal **("+results.get(Die.BEER)+"x)**").queue();
            } else if (currentPlayer.getCharacterID() == Character.CARL && currentInput == Die.GATLING) {
                promptAbility(currentPlayer);
            } else {
                // 1, 3, or 4
                currentInput++;
                promptInput();
            }
        } else { // Count is 0
            currentInput++;
            promptInput();
        }
    }

    public void resolveArrow(Player p, int amount) {
        while (amount > 0 && p.isAlive()) {
            p.updateArrows(1);
            arrowsLeft -= 1;
            channel.sendMessage("**"+p.getCharacter()+"** ("+p.getDisplayName()+") picked up an arrow, **"+p.getArrows()+"x** "+GlobalVars.diceEmoji[Die.ARROW]).queue();
            // Check for INDIAN ATTACK
            if (arrowsLeft == 0) {
                channel.sendMessage("Indian attack! Everyone loses :heart: equal to :bow_and_arrow: they have").queue();
                for (Player player : players) {
                    if (player.isAlive()) {
                        if (player.getCharacterID() == Character.JORDAN && player.getArrows() > 1) {
                            channel.sendMessage(":star: **" + player.getCharacter() + "** (" + player.getDisplayName() + ") lost **1** :heart:").queue();
                            updateHealth(p, player, -1);
                            arrowsLeft += player.getArrows();
                            player.setArrows(0);
                        } else {
                            channel.sendMessage("**" + player.getCharacter() + "** (" + player.getDisplayName() + ") lost **" + player.getArrows() + "** :heart:").queue();
                            while (player.getArrows() > 0) { // Have to discard one by one in the case of Pedro
                                updateHealth(p, player, -1);
                                player.updateArrows(-1);
                                arrowsLeft += 1;
                            }
                        }
                    }
                    if (status != GameStatusType.IN_PROGRESS) return;
                }

                // simple fix
                //if (arrowsLeft > 9) arrowsLeft = 9;
            }
            amount--;
        }
    }

    public void promptAbility(Player p) {
        int c = p.getCharacterID();
        activatedAbilityPlayer = p;
        if (c == Character.BART) {
            channel.sendMessage("**Bart** :star: Take an arrow instead of being shot? (Y/N)").queue();
        } else if (c == Character.CARL) {
            channel.sendMessage("**Carl** :star: Choose a player to discard an arrow").queue();
        } else if (c == Character.SID) {
            channel.sendMessage("**Sid** :star: Choose a player to heal").queue();
        } else if (c == Character.KAIN) {
            channel.sendMessage("**Kain** :star: Use a heal to deal double damage? (Y/N)").queue();
        }
    }

    public void processAbility(String input) {
        int c = activatedAbilityPlayer.getCharacterID();
        if (c == Character.BART) {
            if (input.equalsIgnoreCase("y")) {
                resolveArrow(activatedAbilityPlayer, 1); // can't be last arrow
                activatedAbilityPlayer = null;
                promptInput();
            } else if (input.equalsIgnoreCase("n")) {
                Player previousPlayer = currentPlayer;
                updateHealth(null, activatedAbilityPlayer, -1);
                activatedAbilityPlayer = null;
                if (currentPlayer == previousPlayer) { // still their turn
                    promptInput();
                }
            }
        } else if (c == Character.CARL) {
            Player player = getPlayerByChar(input);
            if (player != null) {
                if (player.getArrows() > 0) {
                    player.updateArrows(-1);
                    arrowsLeft++;
                    channel.sendMessage("Discarded an arrow from **" + player.getCharacter() + "** ("+player.getDisplayName()+"). They have **"+player.getArrows()+"** :bow_and_arrow:").queue();
                }
                results.put(Die.GATLING,results.get(Die.GATLING)-1);
                activatedAbilityPlayer = null;
                promptInput(); // choosing someone with no arrows skips
            }
        } else if (c == Character.SID) {
            Player player = getPlayerByChar(input);
            if (player != null) {
                updateHealth(null, player, 1);
                channel.sendMessage("Healed **" + player.getCharacter() + "** ("+player.getDisplayName()+"). They have **"+player.getHealth()+"** :heart:").queue();
                activatedAbilityPlayer = null;
                promptInput();
            }
        } else if (c == Character.KAIN) {
            if (input.equalsIgnoreCase("y")) {
                // Shoot them twice (no chance to block) TODO
                updateHealth(null, target, -2);
                results.put(Die.BEER, results.get(Die.BEER) - 1);
                activatedAbilityPlayer = null;
                promptInput();
            } else if (input.equalsIgnoreCase("n")) {
                // Shoot them once, must check for Bart
                if (target.getCharacterID() == Character.BART && arrowsLeft > 1) {
                    promptAbility(target);
                } else {
                    updateHealth(null, target, -1);
                    activatedAbilityPlayer = null;
                }
            }
        }
    }

    public void processInput(String name) {
        System.out.println("Processing name: "+name+" for input type: "+currentInput);
        if (currentInput == Die.SHOOT_1) {
            shoot(name, 1);
        } else if (currentInput == Die.SHOOT_2) {
            shoot(name, 2);
        } else if (currentInput == Die.BEER) {
            heal(name);
        } else if (currentInput == Die.GATLING) { // Carl
            processAbility(name);
        }
    }

    public void shoot(String name, int distance) {
        shoot(getPlayerByChar(name), distance);
    }

    public void shoot(Player p, int distance) {
        if (p == null) return;
        int resultNumber = distance+1;
        if (results.get(resultNumber) > 0) { // Checks Shoot1 and Shoot2
            // Check choice
            if (p.isAlive()) {
                // Check validity for Janet, Rose, and everyone else
                if ((currentPlayer.getCharacterID() == Character.JANET && (isAdjX(currentPlayer, p, 1) || isAdjX(currentPlayer, p, 2)))
                        || (currentPlayer.getCharacterID() == Character.ROSE && isAdjX(currentPlayer, p, distance+1))
                        || (isAdjX(currentPlayer, p, distance))) {
                    results.put(resultNumber, results.get(resultNumber) - 1);
                    if (currentPlayer.getCharacterID() == Character.KAIN && results.get(Die.BEER) > 0) {
                        promptAbility(currentPlayer);
                        target = p;
                    } else if (p.getCharacterID() == Character.BART && arrowsLeft > 1) {
                        promptAbility(p);
                    } else {
                        Player previousPlayer = currentPlayer;
                        updateHealth(currentPlayer,p, -1);
                        channel.sendMessage("Shot **" + p.getCharacter() + "** (" + p.getDisplayName() + "). They have **" + p.getHealth() + "** :heart:").queue();
                        //if (!hasActionsLeft()) endTurn(); // automatically end turn if no actions
                        if (currentPlayer == previousPlayer) { // still their turn
                            promptInput();
                        }
                    }
                } else channel.sendMessage("That player is not exactly "+distance+" space(s) away").queue();
            } else {
                System.out.println("Player is dead");
            }
        } else {
            channel.sendMessage("No shoot "+distance).queue();
        }
    }

    public void heal(String name) {
        heal(getPlayerByChar(name));
    }

    public void heal(Player p) {
        if (p == null) return;
        if (results.get(Die.BEER) > 0) {
            // Check choice
            if (p.isAlive()) {
                results.put(Die.BEER, results.get(Die.BEER) - 1);
                Player previousPlayer = currentPlayer;
                if (p.getCharacterID() == Character.JESSIE && currentPlayer == p && p.getHealth() <= 4) {
                    updateHealth(null,p, 1);
                    channel.sendMessage("**Jessie** :star: Healed one extra").queue();
                }
                updateHealth(null,p, 1);
                channel.sendMessage("Healed **" + p.getCharacter() + "** ("+p.getDisplayName()+"). They have **"+p.getHealth()+"** :heart:").queue();
                //if (!hasActionsLeft()) endTurn(); // automatically end turn if no actions
                if (currentPlayer == previousPlayer) { // still their turn
                    promptInput();
                }
            }
        } else {
            channel.sendMessage("No beer").queue();
        }
    }

    public void endTurn() {
        if (status != GameStatusType.IN_PROGRESS) return;

        currentInput = -2; // for dealng with Bart's ability

        if (!confirmedDice) {
            channel.sendMessage("You have not confirmed your dice yet").queue();
            return;
        }

        // Can remove this after promptInput is complete
        if (hasActionsLeft()) {
            channel.sendMessage("You still have actions left to perform").queue();
            return;
        }

        // Check for GATLING
        if ((currentPlayer.getCharacterID() == Character.WILL && results.get(Die.GATLING) >= 2) || results.get(Die.GATLING) >= 3) {
            // Discard arrows
            channel.sendMessage("**"+currentPlayer.getCharacter()+"** ("+currentPlayer.getDisplayName()+") discarded **"+currentPlayer.getArrows()+"** :bow_and_arrow:").queue();
            arrowsLeft += currentPlayer.getArrows();
            currentPlayer.setArrows(0);
            // Shoot others
            channel.sendMessage("Gatling Gun! :gear: Everyone else loses **1** :heart:").queue();
            for (Player p : players) {
                if (p != currentPlayer) { // Hurt everyone else
                    if (p.getCharacterID() != Character.PAUL) {
                        if (p.getCharacterID() == Character.BART) {
                            if (arrowsLeft > 1) {
                                promptAbility(p);
                                return; // wait for promptInput to move to next turn
                            }
                        } else {
                            updateHealth(currentPlayer, p, -1);
                        }
                    } else {
                        channel.sendMessage("**Paul** :star: Took no damage from Gatling Gun").queue();
                    }
                }
            }
        }

        if (status != GameStatusType.IN_PROGRESS) return; // can probably remove this

        nextPlayer();
    }

    public void nextPlayer() {
        // Check for infinite loop
        do {
            System.out.println("Next player...");
            currentPlayer = currentPlayer.getNextPlayer();
        } while (!currentPlayer.isAlive());
        newTurn();
    }

    public void drawBoard() { // isFirst
        // Take starting board
        BufferedImage combined = new BufferedImage(GlobalVars.images.get("table").getWidth(), GlobalVars.images.get("table").getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics g = combined.getGraphics();
        // Draws table first
        try {
            g.drawImage(ImageIO.read(new File("startingboard.png")), 0, 0, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int[] coordsX = Utils.avatarCoordsX[players.size()];
        int[] coordsY = Utils.avatarCoordsY[players.size()];

        // Add revealed roles
        g.setColor(Color.MAGENTA);
        g.setFont(new Font("Arial Black", Font.BOLD, 24));
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i) == sheriff || !players.get(i).isAlive()) {
                g.drawString(GlobalVars.roleIndicators[players.get(i).getRole()], coordsX[i] + 108, coordsY[i] + 20);
            }
        }

        // Add health, arrows, arrows in middle, turn indicator
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial Black", Font.BOLD, 28));
        for (int i = 0; i < players.size(); i++) {
            g.setColor(Color.YELLOW);
            Player p = players.get(i);
            int maxHealth = GlobalVars.healths[p.getCharacterID()];
            if (p == sheriff) maxHealth += 2;

            if (p.getHealth() == maxHealth) { // Green number
                g.setColor(Color.GREEN);
            }
            if (p.getHealth() >= 10) { // offset for larger numbers
                g.drawString(p.getHealth()+"", coordsX[i] - 6, coordsY[i] + 120);
            } else g.drawString(p.getHealth()+"", coordsX[i] + 6, coordsY[i] + 120);
            g.setColor(Color.YELLOW);
            if (p.getArrows() == 0) { // Green number
                g.setColor(Color.GREEN);
            }
            g.drawString(p.getArrows()+"", coordsX[i] + 102, coordsY[i] + 120);

            if (p == currentPlayer) {
                // frame
                Graphics2D g2 = combined.createGraphics();
                float thickness = 6;
                //Stroke oldStroke = g2.getStroke();
                g2.setColor(Color.RED);
                g2.setStroke(new BasicStroke(thickness));
                g2.drawRect((int) (coordsX[i]-thickness),(int) (coordsY[i]-thickness),(int) (128+2*thickness),(int) (128+2*thickness));
                //g2.setStroke(oldStroke);
            }
        }
        g.setColor(Color.YELLOW);
        g.drawString(arrowsLeft+"", Utils.arrowCoords[0] + 6, Utils.arrowCoords[1] + 24);


        System.out.println("Finished drawing new board");

        // Writes the combined image into a file
        try {
            ImageIO.write(combined, "PNG", new File("newboard.png"));
        } catch (IOException e) {
            System.out.println("Fail to write file");
            e.printStackTrace();
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Table");
        embed.setColor(Color.GRAY);
        InputStream test = null;
        try {
            test = new FileInputStream("newboard.png");
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
        embed.setImage("attachment://newboard.png");
        MessageBuilder m = new MessageBuilder();
        m.setEmbed(embed.build());
        channel.sendFile(test, "newboard.png", m.build()).queue();
    }

    public void updateHealth(Player attacker, Player p, int amount) {
        if (p.isAlive()) {
            p.updateHealth(amount);
            if (p.getHealth() <= 0) {
                p.setHealth(0);
                p.setAlive(false);
                // Discard arrows
                arrowsLeft += p.getArrows();
                p.setArrows(0);
                channel.sendMessage(p.getDisplayName() + " died :skull:").queue();

                checkGameOver();
            } else {
                fixMaxHealth(p); // Going over max is not allowed
            }

            if (status == GameStatusType.IN_PROGRESS) {

                if (amount >= 0) return;

                if (attacker != null && attacker != p && p.getCharacterID() == Character.RINGO) {
                    resolveArrow(attacker, 1); // Ringo's thorns
                    channel.sendMessage("**Ringo** :star: Gave an arrow to " + attacker.getCharacter()).queue();
                }

                if (p.getCharacterID() == Character.PEDRO) {
                    if (p.getArrows() > 0) {
                        p.updateArrows(-1);
                        arrowsLeft++;
                        channel.sendMessage("**Pedro** :star: Discarded an arrow, **" + p.getArrows() + "x** :bow_and_arrow:").queue();
                    }
                }

            }

            // issues with interaction?

            if (!currentPlayer.isAlive()) skip();
        }
    }

    public void checkGameOver() {
        updateAlivePlayers();

        // Someone died so if Sam is alive, heal Sam
        for (Player player : alivePlayers.keySet()) {
            if (player.getCharacterID() == Character.SAM) {
                channel.sendMessage("**Sam** :star: Healed **2**").queue();
                updateHealth(null,player,2);
            }
        }

        // If sheriff is dead -or- outlaws and renegades are dead
        if (!sheriff.isAlive()) {
            if (alivePlayers.keySet().size() == 1) {
                for (Player onePlayer : alivePlayers.keySet()) {
                    if (onePlayer.getRole() == Role.RENEGADE) {
                        channel.sendMessage(onePlayer.getDisplayName() + " won as RENEGADE!").queue();
                        end();
                        return;
                    }
                }
            }

            // Outlaws win
            channel.sendMessage("Outlaws won!").queue();
            end();
        } else {
            // Check for no outlaws or renegades
            for (Player player : alivePlayers.keySet()) {
                if (player.getRole() == Role.OUTLAW || player.getRole() == Role.RENEGADE) {
                    System.out.println("Game continues");
                    return;
                }
            }

            channel.sendMessage("The Sheriff and Deputies won!").queue();
            end();
        }
    }


    public boolean hasThreeDynamite() {
        int ct = 0;
        for (Die d : dice) {
            if (d.currentState == Die.DYNAMITE) {
                ct++;
            }
        }
        return ct >= 3;
    }

    public void clearResults() {
        results.clear();
        results.put(Die.ARROW,0);
        results.put(Die.DYNAMITE,0);
        results.put(Die.SHOOT_1,0);
        results.put(Die.SHOOT_2,0);
        results.put(Die.GATLING,0);
        results.put(Die.BEER,0);
    }

    public void drawStartingBoard() {
        String[] ids = new String[players.size()];
        for (int i = 0; i < players.size(); i++) {
            ids[i] = players.get(i).getPlayerID();
        }

        BufferedImage combined = new BufferedImage(GlobalVars.images.get("table").getWidth(), GlobalVars.images.get("table").getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics g = combined.getGraphics();
        // Draws table first
        g.drawImage(GlobalVars.images.get("table"), 0, 0, null);

        int[] coordsX = Utils.avatarCoordsX[ids.length];
        int[] coordsY = Utils.avatarCoordsY[ids.length];

        // Draw arrows in middle
        g.drawImage(GlobalVars.images.get("arrows"),Utils.arrowCoords[0],Utils.arrowCoords[1],null);

        // Draw avatars
        for (int i = 0; i < ids.length; i++) {
            File avatar = new File(GlobalVars.ppath+ids[i]+".png");
            if (!avatar.exists()) {
                String avatarUrl = guild.getMemberById(ids[i]).getUser().getAvatarUrl();
                System.out.println("Avatar url: "+avatarUrl);
                try {
                    Utils.saveImage(avatarUrl, ids[i]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                avatar = new File(GlobalVars.ppath+ids[i]+".png");
            }
            try {
                g.drawImage(ImageIO.read(avatar),coordsX[i],coordsY[i], null);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // name
            Random r = new Random();
            g.setColor(Color.WHITE);
            g.fillRect(coordsX[i], coordsY[i], 128, 22);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial Black", Font.BOLD, 20));
            g.drawString(players.get(i).getCharacter(), coordsX[i]+4, coordsY[i]+18);

            // icons
            g.drawImage(GlobalVars.images.get("heart"),coordsX[i],coordsY[i]+96,null);
            g.drawImage(GlobalVars.images.get("arrows"),coordsX[i]+96,coordsY[i]+96,null);

            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial Black", Font.BOLD, 28));
        }

        // Writes the combined image into a file
        try {
            ImageIO.write(combined, "PNG", new File("startingboard.png"));
        } catch (IOException e) {
            System.out.println("Fail to write file");
            e.printStackTrace();
        }

//        EmbedBuilder embed = new EmbedBuilder();
//        embed.setTitle("Starting Table");
//        embed.setColor(Color.GRAY);
//        InputStream test = null;
//        try {
//            test = new FileInputStream("startingboard.png");
//        } catch (FileNotFoundException e) {
//            System.out.println("File not found");
//        }
//        embed.setImage("attachment://startingboard.png");
//        MessageBuilder m = new MessageBuilder();
//        m.setEmbed(embed.build());
//        channel.sendFile(test, "startingboard.png", m.build()).queue();
    }

    public boolean hasActionsLeft() {
        return !(results.get(Die.SHOOT_1) == 0 && results.get(Die.SHOOT_2) == 0 && results.get(Die.BEER) == 0);
    }

    public void updateAlivePlayers() {
        // First number all ALIVE players 0 through n-1
        alivePlayers = new HashMap<Player,Integer>();
        int count = 0;
        for (Player p : players) {
            if (p.isAlive()) {
                alivePlayers.put(p, count);
                count++;
            }
        }
    }

    public Player getPlayerByChar(String charName) {
        for (Player p : players) {
            if (p.getCharacter().equalsIgnoreCase(charName)) {
                return p;
            }
        }
        System.out.println("Did not find player for: "+charName);
        return null;
    }

    public boolean isAdjX(Player p1, Player p2, int distance) {
        //updateAlivePlayers(); // TODO remove later

        System.out.println("Checking adj for "+p1.getDisplayName()+" and "+p2.getDisplayName()+" distance: "+distance);

        // Need to check smaller and larger
        int smaller = convertToPlayerIndex(alivePlayers.get(p1) - distance);
        int larger = convertToPlayerIndex(alivePlayers.get(p1) + distance);

        return smaller == alivePlayers.get(p2) || larger == alivePlayers.get(p2);
    }

    public int convertToPlayerIndex(int n) {
        if (n >= alivePlayers.size()) return n - alivePlayers.size();
        if (n < 0) return n + alivePlayers.size();
        return n;
    }

    public void fixMaxHealth(Player p) {
        int maxHealth = GlobalVars.healths[p.getCharacterID()];
        if (p == sheriff) maxHealth += 2;
        if (p.getHealth() > maxHealth) {
            p.setHealth(maxHealth);
        }
    }

    // TODO temp
    public void displayDice() {
        String txt = "";
        for (Die d : dice) {
            txt +=  GlobalVars.diceEmoji[d.currentState] + " ";
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Dice");
        if (confirmedDice) embed.setColor(Color.RED);
        else embed.setColor(Color.GRAY);
        embed.setDescription(txt);
        embed.setFooter(rerollsLeft+" rerolls left",null);
        channel.sendMessage(embed.build()).queue();
    }

    public void displayStats() {
        String txt = "Stats";
        for (Player p : players) {
            txt += "\n[" + p.getDisplayName() + "] Char: " + p.getCharacter() + " Health: " + p.getHealth() + " Arrows: " + p.getArrows();
        }
        channel.sendMessage(txt).queue();
    }

    // debug
    public void skip() {
        currentPlayer = currentPlayer.getNextPlayer();
        newTurn();
    }


    public void end() {
        status = GameStatusType.DONE;
        channel.sendMessage("Game is ending").queue();
        GlobalVars.remove(this);
    }

    public Player getAAPlayer() {
        return activatedAbilityPlayer;
    }

    public Die[] dice;
    public int arrowsLeft;
    public int rerollsLeft;
    public boolean confirmedDice;
    public HashMap<Integer,Integer> results = new HashMap<Integer,Integer>();
    public HashMap<Player,Integer> alivePlayers;
    public int currentInput;
    public Player activatedAbilityPlayer;

    public Player target;

    public int status;
    public Player currentPlayer;
    public Player sheriff;
    public ArrayList<Player> players = new ArrayList<Player>();
    public Guild guild;
    public MessageChannel channel;
}
