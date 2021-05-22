package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.game.enums.elements.*;
import com.calculusmaster.pokecord.game.enums.items.XPBooster;
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
    //private static final String DUEL_BACKGROUND = "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/549a70f1-b9f4-4aa7-b76b-589288b03302/d6vkkr7-2c5b3fd6-ed46-4e66-a81e-cab4a30069a0.png/v1/fill/w_1032,h_774,strp/pokemon_x_and_y_vs_template_by_lil_riku_d6vkkr7-pre.png";

    private DuelStatus status;

    private String[] playerIDs;
    private MessageReceivedEvent event;
    private PlayerDataQuery[] playerData;
    private Pokemon[] playerPokemon;
    private int[] asleepTurn;

    private Weather duelWeather;
    private int weatherTurns;

    public int turn;
    private byte[] duelImageBytes;

    //Assumes p2 is registered
    public static Duel initiate(String p1ID, String p2ID, MessageReceivedEvent event)
    {
        Duel d = new Duel();

        d.setPlayers(p1ID, p2ID);
        d.setEvent(event);
        d.setPlayerQuery();
        d.setPlayerPokemon();
        d.setDuelStatus(DuelStatus.WAITING);
        d.setDuelWeather(Weather.CLEAR);
        d.setDefaultInstanceVariables();

        DUELS.add(d);
        return d;
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

        this.asleepTurn = new int[]{0, 0};

        this.setDuelStatus(DuelStatus.DUELING);
    }

    public void setDefaultInstanceVariables()
    {
        this.usedDefenseCurl = new boolean[]{false, false};
        this.iceBallTurns = new int[]{1, 1};
        this.rolloutTurns = new int[]{1, 1};
        this.lastDamage = 0;
        this.usedRage = new boolean[]{false, false};
        this.electricTerrainTurns = -1;
        this.electricTerrainActive = false;
        this.magnetRiseTurns = new int[]{-1, -1};
        this.usedMagnetRise = new boolean[]{false, false};
    }

    //Variable specific to certain moves
    private boolean[] usedDefenseCurl, usedRage, usedMagnetRise;
    private int[] iceBallTurns, rolloutTurns, magnetRiseTurns;
    public int lastDamage;

    public boolean electricTerrainActive;
    public int electricTerrainTurns;

    public String doTurn(int moveIndex)
    {
        this.setDuelStatus(DuelStatus.DUELING);
        String moveString = this.playerPokemon[this.turn].getLearnedMoves().get(moveIndex - 1);
        //Move move = Move.asMove(moveString);
        Move move = new Move(moveString);

        boolean accurate = move.isAccurate();

        //Weather effects
        String weatherEffects = "";
        if(this.duelWeather.equals(Weather.HAIL))
        {
            boolean isThisPokemonAffected = !this.playerPokemon[this.turn].getType()[0].equals(Type.ICE) || !this.playerPokemon[this.turn].getType()[1].equals(Type.ICE);
            boolean isOtherPokemonAffected = !this.playerPokemon[this.getOtherTurn()].getType()[0].equals(Type.ICE) || !this.playerPokemon[this.getOtherTurn()].getType()[1].equals(Type.ICE);

            if(isThisPokemonAffected) this.playerPokemon[this.turn].damage(this.playerPokemon[this.turn].getStat(Stat.HP) / 16, this);
            if(isOtherPokemonAffected) this.playerPokemon[this.getOtherTurn()].damage(this.playerPokemon[this.getOtherTurn()].getStat(Stat.HP) / 16, this);

            weatherEffects = (isThisPokemonAffected && isOtherPokemonAffected ? "Both pokemon" : (isThisPokemonAffected ? this.playerPokemon[this.turn].getName() : (isOtherPokemonAffected ? this.playerPokemon[this.getOtherTurn()].getName() : "Neither pokemon"))) + " took damage from the freezing hailstorm!";

            if(move.getName().equals("Solar Beam") || move.getName().equals("Solar Blade")) move.setPower(move.getPower() / 2);
        }
        else if(this.duelWeather.equals(Weather.HARSH_SUNLIGHT))
        {
            if(move.getType().equals(Type.FIRE)) move.setPower((int)(move.getPower() * 1.5));
            else if(move.getType().equals(Type.WATER)) move.setPower((int)(move.getPower() * 0.5));

            if(move.getName().equals("Thunder") || move.getName().equals("Hurricane")) move.setAccuracy(50);
        }

        List<String> status = new ArrayList<>();
        boolean immune = false;
        String pokeName = this.playerPokemon[this.turn].getName();

        //Status Condition Logic
        int statusDamage = 0;

        if(this.playerPokemon[this.turn].hasStatusCondition(StatusCondition.BURNED))
        {
            statusDamage = (int)(this.playerPokemon[this.turn].getStat(Stat.HP) / 16.);
            this.playerPokemon[this.turn].damage(statusDamage, this);

            status.add(pokeName + " is burned! The burn dealt " + statusDamage + " damage!");
        }

        if(this.playerPokemon[this.turn].hasStatusCondition(StatusCondition.POISONED))
        {
            statusDamage = (int)(this.playerPokemon[this.turn].getStat(Stat.HP) / 8.);
            this.playerPokemon[this.turn].damage(statusDamage, this);

            status.add(pokeName + " is poisoned! The poison dealt " + statusDamage + " damage!");
        }

        if(this.playerPokemon[this.turn].hasStatusCondition(StatusCondition.CONFUSED))
        {
            if(new Random().nextInt(100) < 33)
            {
                statusDamage = new Move("Tackle").getDamage(this.playerPokemon[this.turn], this.playerPokemon[this.turn]);
                this.playerPokemon[this.turn].damage(statusDamage, this);

                status.add(pokeName + " is confused! It hurt itself in its confusion for " + statusDamage + " damage!");

                return this.getStatusResults(status);
            }
            else if(new Random().nextInt(100) < 50) this.playerPokemon[this.turn].removeStatusCondition(StatusCondition.CONFUSED);
        }

        if(this.playerPokemon[this.turn].hasStatusCondition(StatusCondition.FROZEN))
        {
            List<String> unfreezeMoves = Arrays.asList("Fusion Flare", "Flame Wheel", "Sacred Fire", "Flare Blitz", "Scald", "Steam Eruption");
            boolean unfreeze = new Random().nextInt(100) < 20;

            if(unfreezeMoves.contains(move.getName()) || unfreeze || this.duelWeather.equals(Weather.HARSH_SUNLIGHT))
            {
                status.add(pokeName + " has thawed out" + (this.duelWeather.equals(Weather.HARSH_SUNLIGHT) ? " due to the harsh sunlight!" : "!"));
                this.playerPokemon[this.turn].removeStatusCondition(StatusCondition.FROZEN);
            }
            else
            {
                status.add(pokeName + " is frozen and can't use any moves!");
                return this.getStatusResults(status);
            }
        }

        if(this.playerPokemon[this.turn].hasStatusCondition(StatusCondition.ASLEEP))
        {
            if(this.asleepTurn[this.turn] == 2 || (this.electricTerrainActive && !this.playerPokemon[this.turn].isType(Type.FLYING)))
            {
                this.playerPokemon[this.turn].removeStatusCondition(StatusCondition.ASLEEP);
                status.add(this.playerPokemon[this.turn].getName() + " woke up" + (this.electricTerrainActive ? " due to the Electric Field!" : "!"));
                this.asleepTurn[this.turn] = 0;
            }
            else
            {
                this.asleepTurn[this.turn]++;
                status.add(pokeName + " is asleep!");
                return this.getStatusResults(status);
            }
        }

        if(this.playerPokemon[this.turn].hasStatusCondition(StatusCondition.PARALYZED))
        {
            if(new Random().nextInt(100) < 25)
            {
                status.add(pokeName + " is paralyzed and can't move!");
                return this.getStatusResults(status);
            }
        }

        if(this.playerPokemon[this.turn].hasStatusCondition(StatusCondition.FLINCHED))
        {
            this.playerPokemon[this.turn].removeStatusCondition(StatusCondition.FLINCHED);

            status.add(pokeName + " flinched and cannot move!");
            return this.getStatusResults(status);
        }

        //Add code here that needs to check things every turn

        //Unfreeze opponent if move is fire type
        if((move.getType().equals(Type.FIRE) || move.getName().equals("Scald") || move.getName().equals("Steam Eruption")) && this.playerPokemon[this.getOtherTurn()].hasStatusCondition(StatusCondition.FROZEN)) this.playerPokemon[this.getOtherTurn()].removeStatusCondition(StatusCondition.FROZEN);

        if(move.getName().equals("Rollout"))
        {
            if(accurate) this.rolloutTurns[this.turn]++;
            else this.rolloutTurns[this.turn] = 1;

            move.setPower((this.usedDefenseCurl[this.turn] ? 2 : 1) * 30 * (int) Math.pow(2, this.rolloutTurns[this.turn]));
        }
        else this.rolloutTurns[this.turn] = 1;

        if(move.getName().equals("Ice Ball"))
        {
            if(accurate) this.iceBallTurns[this.turn]++;
            else this.iceBallTurns[this.turn] = 1;

            move.setPower((this.usedDefenseCurl[this.turn] ? 2 : 1) * 30 * (int) Math.pow(2, this.iceBallTurns[this.turn]));
        }
        else this.iceBallTurns[this.turn] = 1;

        if(this.usedRage[this.turn] && this.lastDamage > 0) this.playerPokemon[this.turn].changeStatMultiplier(Stat.ATK, 1);

        if(this.electricTerrainTurns <= 0)
        {
            this.electricTerrainActive = false;
            this.electricTerrainTurns = -1;
        }

        if(this.electricTerrainActive)
        {
            if(move.getType().equals(Type.ELECTRIC)) move.setPower((int)(move.getPower() * 1.5));

            this.electricTerrainTurns--;
        }

        if(this.usedMagnetRise[this.getOtherTurn()])
        {
            this.magnetRiseTurns[this.getOtherTurn()]--;

            if(move.getType().equals(Type.GROUND)) immune = true;

            if(this.magnetRiseTurns[this.getOtherTurn()] <= 0)
            {
                this.magnetRiseTurns[this.getOtherTurn()] = -1;
                this.usedMagnetRise[this.getOtherTurn()] = false;
            }
        }

        //Main move results
        String results = "\n";

        if(!accurate) results += move.getMissedResult(this.playerPokemon[this.turn]);
        else if(immune) results += this.playerPokemon[this.getOtherTurn()].getName() + " is immune to the attack!";
        else
        {
            results += move.logic(this.playerPokemon[this.turn], this.playerPokemon[this.getOtherTurn()], this);

            if(move.getCategory().equals(Category.STATUS)) this.lastDamage = 0;

            if(move.getName().equals("Defense Curl")) this.usedDefenseCurl[this.turn] = true;
            if(move.getName().equals("Magnet Rise"))
            {
                this.usedMagnetRise[this.turn] = true;
                this.magnetRiseTurns[this.turn] = this.magnetRiseTurns[this.turn] <= 0 ? 5 : this.magnetRiseTurns[this.turn];
            }
        }

        this.usedRage[this.turn] = accurate && move.getName().equals("Rage");

        //Status condition
        results += (!status.isEmpty() ? "\n" + this.getStatusResults(status) : "");

        //Weather (TODO: Maybe change image depending on weather? probably not since performance but cool)
        String weatherUpdate = this.duelWeather.getStatus();

        if(accurate)
        {
            if(move.getName().equals("Hail"))
            {
                this.duelWeather = Weather.HAIL;
                this.weatherTurns = 5;
            }
            else if(move.getName().equals("Sunny Day"))
            {
                this.duelWeather = Weather.HARSH_SUNLIGHT;
                this.weatherTurns = 5;
            }
        }

        if(weatherTurns != -1 && weatherTurns != 0) weatherTurns--;
        else if(weatherTurns == 0)
        {
            duelWeather = Weather.CLEAR;
            weatherTurns = -1;
            weatherUpdate = "The weather is now clear again!";
        }

        return results + "\n" + weatherUpdate + "\n" + weatherEffects;
    }

    private String getStatusResults(List<String> status)
    {
        StringBuilder sb = new StringBuilder();

        for(String s : status) sb.append(s).append(status.indexOf(s) == status.size() - 1 ? "" : "\n");

        return sb.toString().trim();
    }

    public void onWin()
    {
        this.setDuelStatus(DuelStatus.COMPLETE);
        this.turn = this.playerPokemon[0].isFainted() ? 1 : 0;
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

    public void setEvent(MessageReceivedEvent event)
    {
        this.event = event;
    }

    public void setPlayerQuery()
    {
        this.playerData = new PlayerDataQuery[]{new PlayerDataQuery(this.playerIDs[0]), new PlayerDataQuery(this.playerIDs[1])};
    }

    public void setPlayerPokemon()
    {
        this.playerPokemon = new Pokemon[]{this.playerData[0].getSelectedPokemon(), this.playerData[1].getSelectedPokemon()};
    }

    public void setDuelWeather(Weather w)
    {
        this.duelWeather = w;
        this.weatherTurns = -1;
    }

    //Getters
    public List<String> getPlayers()
    {
        return Arrays.asList(this.playerIDs);
    }

    public Pokemon[] getPokemon()
    {
        return this.playerPokemon;
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

    public Weather getDuelWeather()
    {
        return this.duelWeather;
    }

    //Embeds
    @Deprecated
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

    private int winExp = 0;
    private int winCredits = 0;
    public void sendWinEmbed(MessageReceivedEvent event)
    {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle(this.getTurnTitle());
        embed.setDescription((this.playerPokemon[this.turn]).getName() + " defeated " + (this.playerPokemon[this.getOtherTurn()]).getName() + "!\n" + this.getNameFromID(getWinner()) + " has won! The winner got " + this.winExp + "XP and " + this.winCredits + " credits!");
        embed.setColor(this.getTurnColor());

        Achievements.grant(this.getWinner(), Achievements.WON_1ST_DUEL, event);

        event.getChannel().sendMessage(embed.build()).queue();
    }

    public void giveWinExp()
    {
        int winner = this.getWinner().equals(this.playerIDs[0]) ? 0 : 1;
        Pokemon win = this.playerPokemon[winner];
        double booster = this.playerData[winner].hasXPBooster() ? XPBooster.getInstance(this.playerData[winner].getXPBoosterLength()).boost : 1.0;

        this.winExp = (int)(booster * win.getDuelExp(this.playerPokemon[winner == 0 ? 1 : 0]));
        win.addExp(this.winExp);
        win.gainEVs(this.playerPokemon[winner == 0 ? 1 : 0]);

        Pokemon.updateExperience(win);
        Pokemon.updateEVs(win);
    }

    public void giveWinCredits()
    {
        this.winCredits = new Random().nextInt(501) + 500;
        new PlayerDataQuery(this.getWinner()).changeCredits(this.winCredits);
    }

    private String getHealthBars()
    {
        String healthBarP1 = this.getHB(0);
        String healthBarP2 = this.getHB(1);

        return this.isComplete() ? "" : this.turn == 1 ? healthBarP1 + "\n" + healthBarP2 : healthBarP2 + "\n" + healthBarP1;
    }

    private String getHB(int p)
    {
        //return this.getNameFromID(this.playerIDs[p]) + "'s " + this.playerPokemon[p].getName() + ": " + this.playerPokemon[p].getHealth() + " / " + this.playerPokemon[p].getStat(Stat.HP) + " HP" + (!this.playerPokemon[p].getStatusCondition().equals(StatusCondition.NORMAL) ? " (" + this.playerPokemon[p].getStatusCondition().getAbbrev() + ")" : "");
        return this.getNameFromID(this.playerIDs[p]) + "'s " + this.playerPokemon[p].getName() + ": " + this.playerPokemon[p].getHealth() + " / " + this.playerPokemon[p].getStat(Stat.HP) + " HP " + this.playerPokemon[p].getActiveStatusConditions();
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
        int size = 256 + 128 + 32;
        int hint = BufferedImage.TYPE_INT_ARGB;

        BufferedImage background = ImageIO.read(new URL(DUEL_BACKGROUND));
        Image p1 = ImageIO.read(this.playerPokemon[0].getURL()).getScaledInstance(size, size, hint);
        Image p2 = ImageIO.read(this.playerPokemon[1].getURL()).getScaledInstance(size, size, hint);

        BufferedImage combined = new BufferedImage(background.getWidth(), background.getHeight(), hint);
        combined.getGraphics().drawImage(background, 0, 0, null);
        combined.getGraphics().drawImage(p1, 100, 150, null);
        combined.getGraphics().drawImage(p2, 700, 150, null);
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
                '}';
    }
}
