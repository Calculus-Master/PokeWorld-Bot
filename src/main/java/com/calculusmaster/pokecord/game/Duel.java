package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.game.enums.Stat;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Duel
{
    public static final List<Duel> DUELS = new ArrayList<>();
    private static final String DUEL_BACKGROUND = "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/33f87af1-69a0-4629-856b-bcd83431548e/d4o49yb-f6ce0e46-18c7-4b95-8604-dfc301eb506b.png/v1/fill/w_1192,h_670,q_70,strp/battle_bases_01bg_hd_by_xalien95_d4o49yb-pre.jpg?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOiIsImlzcyI6InVybjphcHA6Iiwib2JqIjpbW3siaGVpZ2h0IjoiPD0xMDgwIiwicGF0aCI6IlwvZlwvMzNmODdhZjEtNjlhMC00NjI5LTg1NmItYmNkODM0MzE1NDhlXC9kNG80OXliLWY2Y2UwZTQ2LTE4YzctNGI5NS04NjA0LWRmYzMwMWViNTA2Yi5wbmciLCJ3aWR0aCI6Ijw9MTkyMCJ9XV0sImF1ZCI6WyJ1cm46c2VydmljZTppbWFnZS5vcGVyYXRpb25zIl19.vplnuE1otbM1_I8d7PytMdOC8XyVO2_g3Ig4P06OExI";

    private DuelStatus status;

    private String[] playerIDs;
    private PlayerDataQuery[] playerData;
    private Pokemon[] playerPokemon;

    private int turn;
    private byte[] duelImageBytes;

    //Assumes p2 is registered
    public static void initiate(String p1ID, String p2ID) throws IOException
    {
        Duel d = new Duel();

        d.setPlayers(p1ID, p2ID);
        d.setPlayerQuery();
        d.setPlayerPokemon();
        d.setDuelStatus(DuelStatus.WAITING);

        DUELS.add(d);
    }

    //Static Methods
    public static boolean isInDuel(String playerID)
    {
        return DUELS.stream().anyMatch(d -> d.getPlayers().contains(playerID));
    }

    public static Duel getInstance(String pID)
    {
        return DUELS.stream().filter(d -> d.getPlayers().contains(pID)).collect(Collectors.toList()).get(0);
    }

    public static void remove(String id)
    {
        int index = -1;
        for(Duel d : DUELS) if(d.getPlayers().contains(id)) index = DUELS.indexOf(d);
        DUELS.remove(index);
    }

    //Non-Static Methods
    public void start()
    {
        int p1Speed = this.playerPokemon[0].getStat(Stat.SPD);
        int p2Speed = this.playerPokemon[1].getStat(Stat.SPD);
        this.turn = p1Speed == p2Speed ? new Random().nextInt(2) : (p1Speed > p2Speed ? 0 : 1);

        //System.out.println(this.turn);
        this.setDuelStatus(DuelStatus.DUELING);
    }

    public String doTurn(int moveIndex)
    {
        //System.out.println(this.turn);
        this.setDuelStatus(DuelStatus.DUELING);
        String moveString = this.playerPokemon[this.turn].getLearnedMoves().get(moveIndex - 1);
        Move move = Move.asMove(moveString);
        boolean accurate = move.getIsAccurate();
        return !accurate ? move.getMoveResultsFail(this.playerPokemon[this.turn]) : move.logic(this.playerPokemon[this.turn], this.playerPokemon[this.getOtherTurn()]);
    }

    public void onWin()
    {
        //System.out.println(this.turn);
        this.setDuelStatus(DuelStatus.COMPLETE);
        this.turn = this.playerPokemon[0].isFainted() ? 1 : 0;
        //System.out.println(this.turn);
    }

    public String getWinner()
    {
        return this.playerPokemon[0].isFainted() ? this.playerIDs[1] : (this.playerPokemon[1].isFainted() ? this.playerIDs[0] : "");
    }

    public boolean isComplete()
    {
        return this.playerPokemon[0].isFainted() || this.playerPokemon[1].isFainted();
    }

    //Constructors and Builders

    public void setDuelStatus(DuelStatus s)
    {
        this.status = s;
    }

    public void setPlayers(String p1ID, String p2ID)
    {
        this.playerIDs = new String[]{p1ID, p2ID};
    }

    public void setPlayerQuery()
    {
        this.playerData = new PlayerDataQuery[]{new PlayerDataQuery(this.playerIDs[0]), new PlayerDataQuery(this.playerIDs[1])};
    }

    public void setPlayerPokemon()
    {
        this.playerPokemon = new Pokemon[]{this.playerData[0].getSelectedPokemon(), this.playerData[1].getSelectedPokemon()};
    }

    //Getters
    public List<String> getPlayers()
    {
        return Arrays.asList(this.playerIDs);
    }

    public DuelStatus getStatus()
    {
        return this.status;
    }

    public String getTurnID()
    {
        return this.playerIDs[this.turn];
    }

    private int getOtherTurn()
    {
        return this.turn == 0 ? 1 : 0;
    }

    //Embeds
    public EmbedBuilder getRequestEmbed()
    {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("New Duel Request!");
        embed.setDescription(this.getNameFromID(this.playerIDs[0]) + " has challenged " + this.getNameFromID(this.playerIDs[1]) + " to duel!\nType p!duel accept to accept the challenge!");

        return embed;
    }

    public void sendInitialTurnEmbed(MessageReceivedEvent event) throws IOException
    {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle(this.getTurnTitle());
        embed.setDescription(this.getHealthBars() + "\n" + this.getTurnPlayer());
        embed.setImage("attachment://duel.png");
        embed.setColor(this.getTurnColor());

        event.getChannel().sendFile(this.getDuelImage(), "duel.png").embed(embed.build()).queue();
    }

    public void sendGenericTurnEmbed(MessageReceivedEvent event, String moveResults) throws IOException
    {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle(this.getTurnTitle());
        embed.setDescription(this.getHealthBars() + "\n" + moveResults + "\n" + this.getTurnPlayer());
        embed.setImage("attachment://duel.png");
        embed.setColor(this.getTurnColor());

        event.getChannel().sendFile(this.getDuelImage(), "duel.png").embed(embed.build()).queue();
    }

    public void sendWinEmbed(MessageReceivedEvent event)
    {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle(this.getTurnTitle());
        embed.setDescription((this.playerPokemon[this.turn]).getName() + " defeated " + (this.playerPokemon[this.getOtherTurn()]).getName() + "!\n" + this.getNameFromID(getWinner()) + " has won!");
        //TODO: Some Victory Image
        embed.setColor(this.getTurnColor());

        event.getChannel().sendMessage(embed.build()).queue();
    }

    public void giveWinExp()
    {
        Pokemon win = this.getWinner().equals(this.playerIDs[0]) ? this.playerPokemon[0] : this.playerPokemon[1];
        win.addExp(win.getDuelExp(this.getWinner().equals(this.playerIDs[0]) ? this.playerPokemon[0] : this.playerPokemon[1]));
        win.gainEVs(this.getWinner().equals(this.playerIDs[0]) ? this.playerPokemon[1] : this.playerPokemon[0]);

        Pokemon.updateExperience(win);
        Pokemon.updateEVs(win);
    }

    public void giveWinCredits()
    {
        new PlayerDataQuery(this.getWinner()).changeCredits(new Random().nextInt(501) + 500);
    }

    private String getHealthBars()
    {
        String healthBarP1 = this.getHB(0);
        String healthBarP2 = this.getHB(1);

        return this.isComplete() ? "" : this.turn == 1 ? healthBarP1 + "\n" + healthBarP2 : healthBarP2 + "\n" + healthBarP1;
    }

    private String getHB(int p)
    {
        return this.getNameFromID(this.playerIDs[p]) + "'s " + this.playerPokemon[p].getName() + ": " + this.playerPokemon[p].getHealth() + " / " + this.playerPokemon[p].getStat(Stat.HP) + " HP";
    }

    private String getTurnTitle()
    {
        return this.getNameFromID(this.playerIDs[this.turn]) + " VS " + this.getNameFromID(this.playerIDs[this.getOtherTurn()]);
    }

    private String getTurnPlayer()
    {
        return this.isComplete() ? "" : "\nIt's " + this.getNameFromID(this.playerIDs[this.turn]) + "'s turn!";
    }

    private Color getTurnColor()
    {
        return this.playerPokemon[this.turn].getType()[0].getColor();
    }

    private String getNameFromID(String id)
    {
       return new PlayerDataQuery(id).getUsername();
    }

    public void swapTurns()
    {
        this.turn = this.turn == 0 ? 1 : 0;
    }

    //Image
    public void setDuelImage() throws IOException
    {
        int size = 256 + 128;
        int hint = BufferedImage.TYPE_INT_ARGB;

        BufferedImage background = ImageIO.read(new URL(DUEL_BACKGROUND));
        Image p1 = ImageIO.read(this.playerPokemon[0].getURL()).getScaledInstance(size, size, hint);
        Image p2 = ImageIO.read(this.playerPokemon[1].getURL()).getScaledInstance(size, size, hint);

        BufferedImage combined = new BufferedImage(background.getWidth(), background.getHeight(), hint);
        combined.getGraphics().drawImage(background, 0, 0, null);
        combined.getGraphics().drawImage(p1, 100, 250, null);
        combined.getGraphics().drawImage(p2, 700, 50, null);
        combined.getGraphics().dispose();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(combined, "png", out);
        this.duelImageBytes = out.toByteArray(); //This is the slow line
    }

    public InputStream getDuelImage()
    {
        //this.setDuelImage();
        return new ByteArrayInputStream(this.duelImageBytes);
    }

    public enum DuelStatus
    {
        WAITING, //Waiting for the opponent to type p!accept
        DUELING, //Currently dueling
        COMPLETE; //Finished
    }

    public static void printAllDuels()
    {
        for(Duel d : DUELS) System.out.println(d.toString());
    }

    @Override
    public String toString() {
        return "Duel{" +
                "status=" + status +
                ", playerIDs=" + Arrays.toString(playerIDs) +
                ", playerData=" + Arrays.toString(playerData) +
                ", playerPokemon=" + Arrays.toString(playerPokemon) +
                ", turn=" + turn +
                ", duelImage=" + "NONONONO" +
                '}';
    }
}
