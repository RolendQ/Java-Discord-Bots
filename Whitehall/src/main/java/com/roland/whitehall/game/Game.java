package com.roland.whitehall.game;

import com.roland.whitehall.core.GlobalVars;
import com.roland.whitehall.core.Utils;
import com.roland.whitehall.enums.GameStatusType;
import com.roland.whitehall.enums.LocationType;
import com.roland.whitehall.enums.SpecialMoveType;
import com.roland.whitehall.enums.ZoomType;
import com.roland.whitehall.players.Detective;
import com.roland.whitehall.players.Jack;
import com.roland.whitehall.players.Player;
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
import java.util.function.Consumer;

public class Game {
    public Game(MessageChannel channel, Guild guild, String authorID, User jackUser) {
        this.channel = channel;
        this.guild = guild;
        this.jackUser = jackUser;

        String name = guild.getMemberById(authorID).getEffectiveName();

        Player p1 = new Jack(Color.GREEN,authorID, GlobalVars.pieces.get("green"), name);
        p1.getPiece().setX(300);
        p1.getPiece().setY(300);
        players.add(p1); // Players includes Jack
        p1.setNextPlayer(p1);

        currentPlayer = p1;

        channel.sendMessage(name + " is starting a game of Whitehall!").queue();
    }

    public void addPlayer(String playerID) {
        String name = guild.getMemberById(playerID).getEffectiveName();

        // TODO allow color selection later
        if (players.size() == 1) players.add(new Detective(Color.YELLOW, playerID, GlobalVars.pieces.get("yellow"), name));
        else if (players.size() == 2) players.add(new Detective(Color.BLUE, playerID, GlobalVars.pieces.get("blue"), name));
        else if (players.size() == 3) players.add(new Detective(Color.RED, playerID, GlobalVars.pieces.get("red"), name));

        channel.sendMessage("Added Detective: "+name).queue();
    }

    public void start() {
        detectives.add((Detective) players.get(1));
        detectives.add((Detective) players.get(2));
        detectives.add((Detective) players.get(3));

        // Set next players
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setNextPlayer(players.get((i+1) % players.size()));
        }
        Jack = (com.roland.whitehall.players.Jack) players.get(0);

        drawBoard(0, true, true);
        jackUser.openPrivateChannel().queue(new Consumer<PrivateChannel>() {
            public void accept(PrivateChannel channel) {
                channel.sendMessage("You are Jack. First, type your 4 body part locations like ``1 5 130 135``\n" +
                        "Each must be a white circle in a different quadrant of the map").queue();
            }
        });

        chooseStartPhase();
    }

    public void testStart() {
        addPlayer(players.get(0).playerID);
        addPlayer(players.get(0).playerID);
        addPlayer(players.get(0).playerID);

        detectives.add((Detective) players.get(1));
        detectives.add((Detective) players.get(2));
        detectives.add((Detective) players.get(3));

        for (int i = 0; i < players.size(); i++) {
            players.get(i).setNextPlayer(players.get((i+1) % players.size()));
        }
        Jack = (com.roland.whitehall.players.Jack) players.get(0);
        drawBoard(0, true, false);
        drawBoard(0, false, true);

        players.get(1).setCurrentNode(91);
        players.get(2).setCurrentNode(168);
        players.get(3).setCurrentNode(94);

        status = GameStatusType.startPhase;

        channel.sendMessage("Waiting for Jack to choose...").queue();
    }

    public void newRound() {

       // drawBoard(0, false, false); for some reason this displays Jack's body parts

        channel.sendMessage("A new round has started. Jack has 15 turns to get to a body part").queue();
        channel.sendMessage("Jack is starting at **"+Jack.getCurrentNode().id+"**").queue();
        turnsLeft = 15;

        turnsTaken = 0;

        visitedLocations.clear();
        missedLocations.clear();
        foundLocations.clear();
        visitedLocations.add(Jack.getCurrentNode());

        newTurn();
    }

    public void newTurn() { // Starts with Jack selecting a move
        status = GameStatusType.movePhase;

        for (Detective det : detectives) {
            det.setCanMove(true);
        }

        // Jack first
        currentPlayer = Jack;
        drawBoard(ZoomType.AUTO, true);
        if (turnsLeft == 15) drawBoard(0, false); // Display board if first round of turn

        turnsTaken++;
        channel.sendMessage("Waiting for Jack to make their **"+Utils.turnStrings[turnsTaken]+"** turn. ("+turnsLeft+" turns left)").queue();
    }

    public void beginSearchPhase() {
        status = GameStatusType.searchPhase;
        for (Detective det : detectives) {
            det.setCanSearch(true);
            det.setCanArrest(true);
        }
        channel.sendMessage("It is now time for the detectives to search or arrest").queue();
    }

    public void chooseStartPhase() {
        status = GameStatusType.startPhase;

        drawBoard(0, false, true);

        channel.sendMessage("Please choose your starting positions (A-F)").queue();
    }

    public void choose(String playerID, int choice) {
        if (choice >= 0 && choice < startingCrossings.length) {
            int crossingID = startingCrossings[choice];
            for (Detective det : detectives) {
                if (det.getCurrentNode() != null && det.getCurrentNode().getTempID() == crossingID) {
                    channel.sendMessage("This crossing is already occupied").queue();
                    return;
                }
            }
            Detective det = getDetective(playerID);
            while (det.getCurrentNode() != null && det.getNextPlayer() instanceof Detective && det.getNextPlayer().playerID.equals(playerID)) {
                // Allow for choosing with 2 or 3 detectives on one account
                det = (Detective) det.getNextPlayer();
            }
            det.setCurrentNode(crossingID);
            //System.out.println("After set node");
            channel.sendMessage(det.getDisplayName() + " "+det.getColorString()+" has locked in a starting position").queue();
            checkStartPhaseEnd();
        }
    }

    public void chooseJack(int locID) {
        if (bodyPartLocations.contains(Utils.ls[locID])) {
            Jack.setCurrentNode(locID);
            displayedBodyPartLocations.add(Utils.ls[locID]);
        }
        checkStartPhaseEnd();
    }

    public void checkStartPhaseEnd() {
        // Check if everyone is ready
        for (Player p : players) {
            if (p.getCurrentNode() == null) return;
        }

        newRound();
    }

    public void setupJack(String[] bodyPartLocs) {
        if (Utils.areValidBodyPartLocs(bodyPartLocs)) {
            for (String num : bodyPartLocs) {
                bodyPartLocations.add(Utils.ls[Integer.parseInt(num)]);
                remainingBodyPartLocations.add(Utils.ls[Integer.parseInt(num)]);
            }
            channel.sendMessage("Jack has chosen his body part locations").queue();

            jackUser.openPrivateChannel().queue(new Consumer<PrivateChannel>() {
                public void accept(PrivateChannel channel) {
                    channel.sendMessage("Now, please choose one of the numbers to start at").queue();
                }
            });
        }
    }

    public void endSearchPhase() {
        status = GameStatusType.movePhase;

        // Check if Jack got to a body part
        if (remainingBodyPartLocations.contains(Jack.getCurrentNode())) {
            channel.sendMessage("Jack has successfully reached a new body part location!").queue();
            remainingBodyPartLocations.remove(Jack.getCurrentNode());
            displayedBodyPartLocations.add(Jack.getCurrentNode());
            if (remainingBodyPartLocations.size() == 0) {
                channel.sendMessage("Jack found all body parts").queue();
                channel.sendMessage("Game Over!").queue();
            } else {
                newRound();
            }
        } else if (turnsLeft <= 0) {
            channel.sendMessage("Jack ran out of turns").queue(); // TODO
            channel.sendMessage("Game Over!").queue();
        } else {
            newTurn();
        }
    }

    public void search(String playerID, int locID) {
        Detective det = getDetective(playerID);
        Location loc = Utils.ls[locID];
        if (det.canSearch() && det.getCurrentNode().getAdjLoc().contains(loc)) {
            det.setCanArrest(false);
            if (visitedLocations.contains(loc)) {
                foundLocations.add(loc);
                channel.sendMessage(det.getDisplayName() + " "+det.getColorString()+" found a clue at " + locID).queue();
                det.setCanSearch(false);
            } else {
                missedLocations.add(loc);
                channel.sendMessage(det.getDisplayName() + " "+det.getColorString()+" found nothing at " + locID).queue();
            }
        }
    }

    // For YELLOW only (on time use)
    public void searchAll(Detective yellowP) {
        channel.sendMessage(yellowP.getDisplayName() + " used their Search Ability").queue();
        for (Location loc : yellowP.getCurrentNode().getAdjLoc()) {
            yellowP.setCanArrest(false);
            if (visitedLocations.contains(loc)) {
                foundLocations.add(loc);
                channel.sendMessage(yellowP.getDisplayName() + " "+yellowP.getColorString()+" found a clue at " + loc.id).queue();
                yellowP.setCanSearch(false);
            } else {
                if (!missedLocations.contains(loc)) missedLocations.add(loc);
                channel.sendMessage(yellowP.getDisplayName() + " "+yellowP.getColorString()+" found nothing at " + loc.id).queue();
            }
        }
    }

    public void arrest(String playerID, int locID) {
        Detective det = getDetective(playerID);
        Location loc = Utils.ls[locID];
        if (det.canArrest() && det.getCurrentNode().getAdjLoc().contains(loc)) {
            det.setCanArrest(false);
            det.setCanSearch(false);
            if (Jack.getCurrentNode() == Utils.ls[locID]) {
                channel.sendMessage(det.getDisplayName() + " "+det.getColorString()+" found Jack at "+locID).queue();
                channel.sendMessage("Game Over!").queue(); // TODO
                // gameOver
            } else {
                channel.sendMessage(det.getDisplayName() + " "+det.getColorString()+" found nobody to arrest at "+locID).queue();
            }
        }
    }

    public void nextPlayer() { // Each player moves
        currentPlayer = currentPlayer.getNextPlayer();

        if (currentPlayer == Jack) {
            drawBoard(ZoomType.FULL, false);
            beginSearchPhase();
            return;
        }
        Detective det = (Detective) currentPlayer;
        if (det.canMove()) {
            drawBoard(ZoomType.AUTO, false);
            channel.sendMessage(det.getDisplayName() + " "+det.getColorString()+", move by selecting a letter from the map").queue();
//            // TODO just for debugging
//            String s = "[DEBUG] ";
//            for (Crossing cro : ((Crossing) currentPlayer.getCurrentNode()).getAdjCro()) {
//                s += cro.getTempID() + " ";
//            }
//            System.out.println(s);
        } else {
            // player couldn't move because of yellow, so next turn
            nextPlayer();
        }
    }

    public void useSpecial(int specialType, int locID) {
        useSpecial(specialType, locID, -1);
    }

    public void useSpecial(int specialType, int locID, int middleLocID) {
        // Cannot use special onto a body part location
        if (bodyPartLocations.contains(Utils.ls[locID])) return;

        if (specialType == SpecialMoveType.ALLEY) {
            if (Jack.alleys < 1) return;
            if (!Utils.ls[locID].hasSameAlley((Location) Jack.getCurrentNode())) {
                System.out.println("No alley in common");
                return;
            }
            Jack.alleys--;
        } else if (specialType == SpecialMoveType.BOAT) {
            if (Jack.boats < 1) return;
            if (!Utils.ls[locID].hasSameWaterBlock((Location) Jack.getCurrentNode())) {
                System.out.println("No water block in common");
                return;
            }
            Jack.boats--;
        } else if (specialType == SpecialMoveType.COACH) {
            int startID = Jack.getCurrentNode().id;
            if (Jack.coaches < 1) return;
            // Must all be unique locations and not water
            if (locID == middleLocID || startID == locID || startID == middleLocID) return;
            if (Utils.ls[locID].type == LocationType.WATER || Utils.ls[middleLocID].type == LocationType.WATER) return;
            visitedLocations.add(Utils.ls[middleLocID]);
            Jack.coaches--;
        }
        moveJack(locID);
        channel.sendMessage("Jack used a special movement that costs another turn. ("+Jack.getTotalLeft()+" left)").queue();
        turnsLeft -= 2;
    }

    public void walk(int locID, boolean isDebug) {
        if (isDebug || getValidJackMoves().contains(Utils.ls[locID])) {
            turnsLeft--;
            moveJack(locID);
        }
    }

    public void moveJack(int locID) {
        Jack.setCurrentNode(locID);
        visitedLocations.add(Utils.ls[locID]);
        System.out.println("Jack moved to: "+locID);
        for (User user : spectators) {
            user.openPrivateChannel().queue(new Consumer<PrivateChannel>() {
                public void accept(PrivateChannel channel) {
                    channel.sendMessage("Jack moved to **"+locID+"**").queue();
                }
            });
        }
        nextPlayer();
    }

    // For YELLOW only, input player and choice
    public void teleport(String playerID, int choice) { // TODO could be a letter?
        Detective det = getDetective(playerID);
        if (det != players.get(1)) { // must not be yellow
            ArrayList<Crossing> moves = getAllMoves();
            det.setCurrentNode(moves.get(choice).getTempID());
            det.setCanMove(false);
            det.setCanArrest(false);
            det.setCanSearch(false);
            det.setHasAbility(false);
            channel.sendMessage("The Yellow Detective used their ability to teleport "+det.getDisplayName()+" to near them").queue();
        }
    }

    public ArrayList<Location> getValidJackMoves() {

        ArrayList<Location> adjLocs = new ArrayList<Location>();

        Queue<Node> nodesQ = new LinkedList<Node>();
        nodesQ.add(Jack.getCurrentNode());

        HashSet<Node> visited = new HashSet<Node>();

        while (!nodesQ.isEmpty()) {
            Node current = nodesQ.remove();
            for (Node adjCurrent : Utils.map.get(current)) {
                if (visited.contains(adjCurrent)) continue;
                visited.add(adjCurrent);

                if (adjCurrent instanceof Location) {
                    adjLocs.add((Location) adjCurrent);
                } else if (!isOccupied((Crossing) adjCurrent)) { // Make sure it's not occupied
                    //System.out.println("Jack was blocked from going through "+((Crossing) adjCurrent).getTempID());
                    nodesQ.add(adjCurrent);
                }
            }
        }
        if (adjLocs.size() > 0) {
            adjLocs.remove(Jack.getCurrentNode()); // Remove itself

            Collections.sort(adjLocs);
        }
        System.out.println("Returning getValidJackMoves: "+adjLocs.size());
        return adjLocs;
    }

    // For Detectives only
    public void move(int choice) {
        if (choice == 25) {
            // skip their turn TODO change
            nextPlayer();
            return;
        }
        ArrayList<Crossing> moves = getAllMoves(); // Is this within 2 not 1 crossing?
        //  HashSet<Crossing> moves = ((Detective) currentPlayer).getCurrentNode().getAllMoves(); ??
        if (choice < moves.size()) {
            move(moves.get(choice));
        }
    }

    public void move(Crossing finish) {
        if (finish != null) {
            System.out.println("Moved to: "+finish.getTempID());

            currentPlayer.setCurrentNode(finish.getTempID());
            nextPlayer();
        }
    }

    public ArrayList<Crossing> getAllMoves() {
        ArrayList<Crossing> allMoves = new ArrayList<Crossing>();
        for (Crossing crossing : ((Crossing) currentPlayer.getCurrentNode()).getAllMoves()) {
            if (!isOccupied(crossing)) allMoves.add(crossing);
        }
        Collections.sort(allMoves);
        return allMoves;
    }

    public boolean isOccupied(Crossing crossing) {
        for (Player p : players) {
            if (p.getCurrentNode() == crossing) {
                System.out.println("Found occupied crossing: "+crossing.getTempID());
                return true;
            }
        }
        return false;
    }

    public void drawBoard(int zoom, boolean showJack) {
        drawBoard(zoom, showJack, false);
    }

    public void drawBoard(int zoom, boolean showJack, boolean isFirst) {
        System.out.println("[DEBUG LOG/Game.java] Running drawBoard: "+showJack+ " and "+isFirst);
        BufferedImage combined = new BufferedImage(GlobalVars.images.get("board").getWidth(), GlobalVars.images.get("board").getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics g = combined.getGraphics();
        // Draws board template first
        g.drawImage(GlobalVars.images.get("board"), 0, 0, null);

        // Draw characters
        int offsetX = -31;
        int offsetY = -39;
        // Draw letters
        if (currentPlayer != Jack) {
            System.out.println("Drawing letters");
            int i = 0;
            for (Crossing crossing : getAllMoves()) {
                System.out.println("Crossing id: "+crossing.getTempID()+ " "+GlobalVars.letters[i]);
                g.drawImage(GlobalVars.images.get("letter_"+GlobalVars.letters[i]), Utils.crossingCoords[crossing.getTempID()][0]+offsetX, Utils.crossingCoords[crossing.getTempID()][1]+offsetY, null);
                i++;
            }
        }

        // Drawing starting position letters
        if (isFirst) {
            for (int i = 0; i < startingCrossings.length; i++) {
                int id = startingCrossings[i];
                g.drawImage(GlobalVars.images.get("letter_"+GlobalVars.letters[i]), Utils.crossingCoords[id][0]+offsetX, Utils.crossingCoords[id][1]+offsetY, null);
            }
        }

        System.out.println("Drawing chars");
        g.drawImage(GlobalVars.images.get("red_char"), players.get(3).getPiece().getX()+offsetX, players.get(3).getPiece().getY()+offsetY, null);
        g.drawImage(GlobalVars.images.get("blue_char"), players.get(2).getPiece().getX()+offsetX, players.get(2).getPiece().getY()+offsetY, null);
        g.drawImage(GlobalVars.images.get("yellow_char"), players.get(1).getPiece().getX()+offsetX, players.get(1).getPiece().getY()+offsetY, null);


        // Draw markers
        float opacity = 0.4f;
        ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        offsetX = -40;
        offsetY = -40;
        //g.drawImage(GlobalVars.images.get("red_marker"), 208+offsetX, 146+offsetY, null);

        // Missed Locations
        for (Location loc : missedLocations) {
            g.drawImage(GlobalVars.images.get("gold_marker"), Utils.locationCoords[loc.id][0]+offsetX, Utils.locationCoords[loc.id][1]+offsetY, null);
        }
        // Show Jack where he's been (cyan markers)
        if (showJack) {
            for (Location loc : visitedLocations) {
                g.drawImage(GlobalVars.images.get("cyan_marker"), Utils.locationCoords[loc.id][0]+offsetX, Utils.locationCoords[loc.id][1]+offsetY, null);
            }
        }
        // Found locations
        for (Location loc : foundLocations) {
            g.drawImage(GlobalVars.images.get("green_marker"), Utils.locationCoords[loc.id][0]+offsetX, Utils.locationCoords[loc.id][1]+offsetY, null);
        }
        // Body part locations
        if (showJack) {
            // All body parts
            for (Location loc : bodyPartLocations) {
                g.drawImage(GlobalVars.images.get("red_marker"), Utils.locationCoords[loc.id][0]+offsetX, Utils.locationCoords[loc.id][1]+offsetY, null);
            }
        } else {
            // Only displayed ones
            for (Location loc : displayedBodyPartLocations) {
                System.out.println("Drew displayed body part loc: "+loc.id);
                g.drawImage(GlobalVars.images.get("red_marker"), Utils.locationCoords[loc.id][0]+offsetX, Utils.locationCoords[loc.id][1]+offsetY, null);
            }
        }

        ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        // Jack
        if (showJack && !isFirst) {
            offsetX = -32;
            offsetY = -38;
            g.drawImage(GlobalVars.images.get("green_char"), players.get(0).getPiece().getX()+offsetX, players.get(0).getPiece().getY()+offsetY, null);
        }

        System.out.println("Doing zoom");
        // Zoom
        if (zoom == ZoomType.FULL) {
            combined = Utils.scaleImage(combined,1160,1160,Color.GREEN);
        } else if (zoom == ZoomType.AUTO) {
            int x = currentPlayer.getPiece().getX() + 31;
            int y = currentPlayer.getPiece().getY() + 39;
            try {
                if (x < 580) {
                    x = 580;
                } else if (x > 1740) {
                    x = 1740;
                }
                if (y < 580) {
                    y = 580;
                } else if (y > 1740) {
                    y = 1740;
                }

                combined = combined.getSubimage(x - 580, y - 580, 1160, 1160);
            } catch (Exception e) {
                // Could not center on the piece
                System.out.println("Something went wrong with auto zoom...");
                if (x < 580) {
                    if (y < 580) zoom = ZoomType.TOP_LEFT;
                    else if (y < 1160) zoom = ZoomType.MID_LEFT;
                    else zoom = ZoomType.BOTTOM_LEFT;
                } else if (x < 1160) {
                    if (y < 580) zoom = ZoomType.TOP_CENTER;
                    else if (y < 1160) zoom = ZoomType.MID_CENTER; // Should not occur
                    else zoom = ZoomType.BOTTOM_CENTER;
                } else {
                    if (y < 580) zoom = ZoomType.TOP_RIGHT;
                    else if (y < 1160) zoom = ZoomType.MID_RIGHT;
                    else zoom = ZoomType.BOTTOM_RIGHT;
                }
            }
        }
        if (zoom == ZoomType.TOP_LEFT) {
            combined = combined.getSubimage(0,0,1160,1160);
        } else if (zoom == ZoomType.TOP_CENTER) {
            combined = combined.getSubimage(580,0,1160,1160);
        } else if (zoom == ZoomType.TOP_RIGHT) {
            combined = combined.getSubimage(1160,0,1160,1160);
        } else if (zoom == ZoomType.MID_LEFT) {
            combined = combined.getSubimage(0,570,1160,1160);
        } else if (zoom == ZoomType.MID_CENTER) {
            combined = combined.getSubimage(570,570,1160,1160);
        } else if (zoom == ZoomType.MID_RIGHT) {
            combined = combined.getSubimage(1160,570,1160,1160);
        } else if (zoom == ZoomType.BOTTOM_LEFT) {
            combined = combined.getSubimage(0,1160,1160,1160);
        } else if (zoom == ZoomType.BOTTOM_CENTER) {
            combined = combined.getSubimage(570,1160,1160,1160);
        } else if (zoom == ZoomType.BOTTOM_RIGHT) {
            combined = combined.getSubimage(1160,1160,1160,1160);
        }

        // Writes the combined image into a file
        try {
            ImageIO.write(combined, "PNG", new File("newboard.png"));
        } catch (IOException e) {
            System.out.println("Fail to write file");
            e.printStackTrace();
        }

        // Builds an embed and sends it to the filesChannel
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Whitehall");
        if (showJack && Jack.getCurrentNode() != null) {
            String s = "Moves: ";
            ArrayList<Location> moves = getValidJackMoves();
            System.out.println("[Creating Jack Embed] Jack's number of moves: "+moves.size());
            for (Location loc : moves) {
                s += loc.id + " ";
            }
            embed.setTitle(s);
            if (moves.size() == 0) {
                // Jack is stuck
                channel.sendMessage("Jack is stuck").queue();
                channel.sendMessage("Game Over!").queue(); // TODO actual game over
            }
            String footerText = "[Special Moves] " + Jack.alleys + " Alleys, " + Jack.boats + " Boats, " + Jack.coaches + " Coaches";
            embed.setFooter(footerText,null);
        }
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
        //GlobalVars.filesChannel.sendFile(test, "newboard.png", m.build()).queue();


        System.out.println("Sending file");
        if (showJack) {
            InputStream finalTest = test;
            jackUser.openPrivateChannel().queue(new Consumer<PrivateChannel>() {
                public void accept(PrivateChannel channel) {
                    channel.sendFile(finalTest, "newboard.png", m.build()).complete(); // try complete
                }
            });
            // Send to spectators
            for (User user : spectators) {
                user.openPrivateChannel().queue(new Consumer<PrivateChannel>() {
                    public void accept(PrivateChannel channel) {
                        channel.sendFile(finalTest, "newboard.png", m.build()).complete();
                    }
                });
            }
            System.out.println("Sent board (Jack)");
        } else {
            channel.sendFile(test, "newboard.png", m.build()).complete();
            System.out.println("Sent board (not Jack)");
        }
    }

    public void toggleSpectate(User user) {
        if (spectators.contains(user)) {
            spectators.remove(user);
            channel.sendMessage(user.getName() + ", you are no longer spectating this game").queue();
        } else {
            spectators.add(user);
            channel.sendMessage(user.getName() + ", you are now spectating this game").queue();
        }
    }

    // Transfer account
    public void transfer(String oldPlayerID, String newPlayerID) {
        System.out.println("Transferring");
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            if (p.getPlayerID().contentEquals(oldPlayerID)) { // Transfers first that matches
                p.setPlayerID(newPlayerID);
                String oldName = p.getDisplayName();
                p.setDisplayName(guild.getMemberById(newPlayerID).getEffectiveName());
                if (p instanceof Detective) {
                    channel.sendMessage("**" + oldName + "** transferred their role " + ((Detective) p).getColorString() + " to " + p.getDisplayName()).queue();
                } else {
                    channel.sendMessage("**" + oldName + "** transferred Jack to " + p.getDisplayName()).queue();
                }
                return;
            }
        }
    }

    public Detective getDetective(String playerID) {
        for (Detective p : detectives) {
            if (p.playerID.equals(playerID)) return p;
        }
        return null;
    }

    // For debug
    public void addVisit(int locID) {
        visitedLocations.add(Utils.ls[locID]);
        channel.sendMessage("Added visited").queue();
    }


//    public Crossing calcMove(int locID) {
//        // Check simple move
//        for (Node n : Utils.map.get(currentPlayer.getCurrentNode())) {
//            if (n instanceof Location) {
//                if (((Location) n).id == locID) {
//                    // Found matching id
//                    ArrayList<Node> adjCrossings = Utils.map.get(n);
//                    if (adjCrossings.size() == 2) { // Only two crossings
//                        if (adjCrossings.get(0) == currentPlayer.getCurrentNode()) return (Crossing) adjCrossings.get(1);
//                        return (Crossing) adjCrossings.get(0);
//                    }
//                    // Fail
//                }
//                // Fail
//            } else {
//                // Check adj for the Location id
//                for (Node adj : Utils.map.get(n)) {
//                    if (adj instanceof Location) {
//                        if (((Location) adj).id == locID) {
//                            // Found matching id
//                            return (Crossing) n;
//                        }
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//    public Crossing calcMove(int locID1, int locID2) {
//        // Check for first number
//        for (Node n : Utils.map.get(currentPlayer.getCurrentNode())) {
//            System.out.println("CHecking node. IDs: "+locID1 + " " +locID2);
//            if (n instanceof Location) {
//                if (((Location) n).id == locID1) {
//                    // Found matching id for first
//                    System.out.println("Found first: "+locID1);
//
//                    for (Node n2 : Utils.map.get(n)) {
//                        if (n2 == currentPlayer.getCurrentNode()) continue; // skip itself
//
//                        // Look for node with second
//                        for (Node n3 : Utils.map.get(n2)) {
//                            if (n3 == n) continue; // skip first
//
//                            if (n3 instanceof Location) {
//                                if (((Location) n3).id == locID2) {
//                                    // Found matching id for second
//                                    System.out.println("Found second: "+locID1);
//                                    return (Crossing) n2;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        System.out.println("return null");
//        return null;
//    }

    public com.roland.whitehall.players.Jack Jack;
    public User jackUser;
    public Player currentPlayer;
    public ArrayList<Player> players = new ArrayList<Player>();
    public MessageChannel channel;
    public Guild guild;
    public int status = GameStatusType.pregame;
    public int[] startingCrossings = {90,91,94,95,104,106};
    public int turnsLeft;
    public int turnsTaken;

    public ArrayList<User> spectators = new ArrayList<User>();

    public ArrayList<Location> visitedLocations = new ArrayList<Location>();
    public ArrayList<Location> foundLocations = new ArrayList<Location>();
    public ArrayList<Location> missedLocations = new ArrayList<Location>();
    public ArrayList<Location> bodyPartLocations = new ArrayList<Location>();
    public ArrayList<Location> remainingBodyPartLocations = new ArrayList<>();
    public ArrayList<Location> displayedBodyPartLocations = new ArrayList<Location>();

    public ArrayList<Detective> detectives = new ArrayList<>();

    // Player
}
