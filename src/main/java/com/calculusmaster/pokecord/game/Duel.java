package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.game.enums.elements.*;
import com.calculusmaster.pokecord.game.enums.items.PokeItem;
import com.calculusmaster.pokecord.game.enums.items.XPBooster;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.game.moves.*;
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
    private String duelID;

    private String[] playerIDs;
    private MessageReceivedEvent event;
    private PlayerDataQuery[] playerData;
    private Pokemon[] playerPokemon;
    private boolean[] playerZMoves;
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
        d.setDuelID();
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
        this.playerZMoves = new boolean[]{false, false};
        this.usedDefenseCurl = new boolean[]{false, false};
        this.iceBallTurns = new int[]{1, 1};
        this.rolloutTurns = new int[]{1, 1};
        this.lastDamage = 0;
        this.usedRage = new boolean[]{false, false};
        this.electricTerrainTurns = -1;
        this.electricTerrainActive = false;
        this.magnetRiseTurns = new int[]{-1, -1};
        this.usedMagnetRise = new boolean[]{false, false};
        this.tauntTurns = new int[]{-1, -1};
        this.usedTaunt = new boolean[]{false, false};
        this.recharge = new boolean[]{false, false};
        this.statImmuneTurns = new int[]{-1, -1};
        this.futureSightTurns = new int[]{-1, -1};
        this.usedFutureSight = new boolean[]{false, false};
        this.usedDetect = new boolean[]{false, false};
        this.usedCharge = new boolean[]{false, false};
    }

    //Variable specific to certain moves
    private boolean[] usedDefenseCurl, usedRage, usedMagnetRise, usedTaunt, usedFutureSight;
    private int[] iceBallTurns, rolloutTurns, magnetRiseTurns, tauntTurns, futureSightTurns;
    public int lastDamage;

    private boolean[] recharge;

    public boolean electricTerrainActive;
    public int electricTerrainTurns;

    public int statImmuneTurns[];

    private boolean[] usedDetect, usedCharge;

    public boolean accurate;

    public String doTurn(int moveIndex, boolean z)
    {
        this.setDuelStatus(DuelStatus.DUELING);
        String moveString = this.playerPokemon[this.turn].getLearnedMoves().get(moveIndex - 1);
        Move move = new Move(moveString);
        String results = "\n";

        if(z) move = this.getZMove(move);

        //Chance to get Z Crystal
        if(new Random().nextInt(100) < 1) zcrystalEvent(move);

        //Weather effects
        String weatherEffects = "";
        if(this.duelWeather.equals(Weather.HAIL))
        {
            boolean isThisPokemonAffected = !this.playerPokemon[this.turn].isType(Type.ICE);
            boolean isOtherPokemonAffected = !this.playerPokemon[this.getOtherTurn()].isType(Type.ICE);

            if(isThisPokemonAffected) this.playerPokemon[this.turn].damage(this.playerPokemon[this.turn].getStat(Stat.HP) / 16, this);
            if(isOtherPokemonAffected) this.playerPokemon[this.getOtherTurn()].damage(this.playerPokemon[this.getOtherTurn()].getStat(Stat.HP) / 16, this);

            weatherEffects = (isThisPokemonAffected && isOtherPokemonAffected ? "Both pokemon" : (isThisPokemonAffected ? this.playerPokemon[this.turn].getName() : (isOtherPokemonAffected ? this.playerPokemon[this.getOtherTurn()].getName() : "Neither pokemon"))) + " took damage from the freezing hailstorm!";

            if(move.getName().equals("Blizzard")) move.setAccuracy(100);

            if(move.getName().equals("Solar Beam") || move.getName().equals("Solar Blade")) move.setPower(move.getPower() / 2);
        }
        else if(this.duelWeather.equals(Weather.HARSH_SUNLIGHT))
        {
            if(move.getType().equals(Type.FIRE)) move.setPower((int)(move.getPower() * 1.5));
            else if(move.getType().equals(Type.WATER)) move.setPower((int)(move.getPower() * 0.5));

            if(move.getName().equals("Thunder") || move.getName().equals("Hurricane")) move.setAccuracy(50);
        }
        else if(this.duelWeather.equals(Weather.RAIN))
        {
            if(move.getType().equals(Type.WATER)) move.setPower((int)(move.getPower() * 1.5));
            else if(move.getType().equals(Type.FIRE) || move.getName().equals("Solar Beam") || move.getName().equals("Solar Blade")) move.setPower((int)(move.getPower() * 0.5));

            if(move.getName().equals("Thunder") || move.getName().equals("Hurricane")) move.setAccuracy(100);
        }
        else if(this.duelWeather.equals(Weather.SANDSTORM))
        {
            boolean isThisPokemonAffected = !this.playerPokemon[this.turn].isType(Type.GROUND) && !this.playerPokemon[this.turn].isType(Type.ROCK) && !this.playerPokemon[this.turn].isType(Type.STEEL);
            boolean isOtherPokemonAffected = !this.playerPokemon[this.getOtherTurn()].isType(Type.GROUND) && !this.playerPokemon[this.getOtherTurn()].isType(Type.ROCK) && !this.playerPokemon[this.getOtherTurn()].isType(Type.STEEL);

            if(isThisPokemonAffected) this.playerPokemon[this.turn].damage(this.playerPokemon[this.turn].getStat(Stat.HP) / 16, this);
            if(isOtherPokemonAffected) this.playerPokemon[this.getOtherTurn()].damage(this.playerPokemon[this.getOtherTurn()].getStat(Stat.HP) / 16, this);

            weatherEffects = (isThisPokemonAffected && isOtherPokemonAffected ? "Both pokemon" : (isThisPokemonAffected ? this.playerPokemon[this.turn].getName() : (isOtherPokemonAffected ? this.playerPokemon[this.getOtherTurn()].getName() : "Neither pokemon"))) + " took damage from the sandstorm!";

            //TODO: Rock type SPDEF increases by 50% in Sandstorm

            if(move.getName().equals("Solar Beam") || move.getName().equals("Solar Blade")) move.setPower(move.getPower() / 2);
        }

        if(this.usedFutureSight[this.turn])
        {
            this.futureSightTurns[this.turn]--;

            if(this.futureSightTurns[this.turn] <= 0)
            {
                move = new Move("Future Sight");
                int damage = move.getDamage(this.playerPokemon[this.turn], this.playerPokemon[this.getOtherTurn()]);
                this.playerPokemon[this.getOtherTurn()].damage(damage, this);

                results += "\nFuture Sight hit " + this.playerPokemon[this.getOtherTurn()].getName() + " and dealt " + damage + " damage!";

                this.futureSightTurns[this.turn] = -1;
                this.usedFutureSight[this.turn] = false;
            }
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

        if(this.playerPokemon[this.turn].hasStatusCondition(StatusCondition.CURSED))
        {
            statusDamage = this.playerPokemon[this.turn].getStat(Stat.HP) / 4;
            this.playerPokemon[this.turn].damage(statusDamage, this);

            status.add(pokeName + " is cursed! The curse dealt " + statusDamage + " damage!");
        }

        if(this.playerPokemon[this.turn].hasStatusCondition(StatusCondition.CONFUSED))
        {
            if(new Random().nextInt(100) < 33)
            {
                statusDamage = new Move("Tackle").getDamage(this.playerPokemon[this.turn], this.playerPokemon[this.turn]);
                this.playerPokemon[this.turn].damage(statusDamage, this);

                status.add(results);
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
                status.add(results);
                status.add(pokeName + " is frozen and can't use any moves!");
                return this.getStatusResults(status);
            }
        }

        if(this.playerPokemon[this.turn].hasStatusCondition(StatusCondition.ASLEEP))
        {
            if(this.asleepTurn[this.turn] == 2 || (this.electricTerrainActive && !this.playerPokemon[this.turn].isType(Type.FLYING)))
            {
                this.playerPokemon[this.turn].removeStatusCondition(StatusCondition.ASLEEP);
                if(this.playerPokemon[this.turn].hasStatusCondition(StatusCondition.NIGHTMARE))
                {
                    status.add("The Nightmare was removed!");
                    this.playerPokemon[this.turn].removeStatusCondition(StatusCondition.NIGHTMARE);
                }

                status.add(this.playerPokemon[this.turn].getName() + " woke up" + (this.electricTerrainActive ? " due to the Electric Field!" : "!"));
                this.asleepTurn[this.turn] = 0;
            }
            else
            {
                this.asleepTurn[this.turn]++;

                if(this.playerPokemon[this.turn].hasStatusCondition(StatusCondition.NIGHTMARE))
                {
                    this.playerPokemon[this.turn].damage(this.playerPokemon[this.turn].getStat(Stat.HP) / 4, this);
                    status.add("The Nightmare dealt " + (this.playerPokemon[this.turn].getStat(Stat.HP) / 4) + " damage!");
                }

                status.add(results);
                status.add(pokeName + " is asleep!");
                return this.getStatusResults(status);
            }
        }

        if(this.playerPokemon[this.turn].hasStatusCondition(StatusCondition.PARALYZED))
        {
            if(new Random().nextInt(100) < 25)
            {
                status.add(results);
                status.add(pokeName + " is paralyzed and can't move!");
                return this.getStatusResults(status);
            }
        }

        if(this.playerPokemon[this.turn].hasStatusCondition(StatusCondition.FLINCHED))
        {
            this.playerPokemon[this.turn].removeStatusCondition(StatusCondition.FLINCHED);

            status.add(results);
            status.add(pokeName + " flinched and cannot move!");
            return this.getStatusResults(status);
        }

        //Checking if the user has fainted before moving onto the attack
        if(this.playerPokemon[this.turn].isFainted()) return results + this.getStatusResults(status);

        this.accurate = move.isAccurate();

        if(z)
        {
            this.accurate = true;
            results += this.playerPokemon[this.turn].getName() + " summons its Z-Power! ";

            this.setUsedZMove(this.turn);
        }

        //Add code here that needs to check things every turn
        boolean unableToUse = false;

        //Unfreeze opponent if move is fire type
        if((move.getType().equals(Type.FIRE) || move.getName().equals("Scald") || move.getName().equals("Steam Eruption")) && this.playerPokemon[this.getOtherTurn()].hasStatusCondition(StatusCondition.FROZEN)) this.playerPokemon[this.getOtherTurn()].removeStatusCondition(StatusCondition.FROZEN);

        if(move.getName().equals("Rollout"))
        {
            if(this.accurate) this.rolloutTurns[this.turn]++;
            else this.rolloutTurns[this.turn] = 1;

            move.setPower((this.usedDefenseCurl[this.turn] ? 2 : 1) * 30 * (int) Math.pow(2, this.rolloutTurns[this.turn]));
        }
        else this.rolloutTurns[this.turn] = 1;

        if(move.getName().equals("Ice Ball"))
        {
            if(this.accurate) this.iceBallTurns[this.turn]++;
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

        if(this.usedTaunt[this.getOtherTurn()])
        {
            this.tauntTurns[this.getOtherTurn()]--;

            if(move.getCategory().equals(Category.STATUS))
            {
                unableToUse = true;
            }
        }

        if(this.tauntTurns[this.getOtherTurn()] <= 0)
        {
            this.tauntTurns[this.getOtherTurn()] = -1;
            this.usedTaunt[this.getOtherTurn()] = false;
        }

        if(move.getName().equals("Sheer Cold"))
        {
            move.setAccuracy((this.playerPokemon[this.turn].isType(Type.ICE) ? 30 : 20) + (this.playerPokemon[this.turn].getLevel() - this.playerPokemon[this.getOtherTurn()].getLevel()));
            this.accurate = move.isAccurate();
        }

        if(this.usedDetect[this.getOtherTurn()])
        {
            this.usedDetect[this.getOtherTurn()] = false;

            immune = true;
        }

        if(this.usedCharge[this.turn])
        {
            this.usedCharge[this.turn] = false;

            if(move.getType().equals(Type.ELECTRIC)) move.setPower(move.getPower() * 2);
        }

        //Item-based Buffs
        if(this.playerPokemon[this.turn].hasItem() && PokeItem.asItem(this.playerPokemon[this.turn].getItem()).equals(PokeItem.METAL_COAT))
        {
            if(move.getType().equals(Type.STEEL)) move.setPower((int)(move.getPower() * 1.2));
        }

        //Main move results

        List<String> rechargeMoves = Arrays.asList("Hyper Beam", "Blast Burn", "Hydro Cannon", "Frenzy Plant", "Roar Of Time", "Prismatic Laser", "Eternabeam");

        if(this.recharge[this.turn] && rechargeMoves.contains(move.getName())) results += this.playerPokemon[this.turn].getName() + " can't use " + move.getName() + " this turn because it needs to recharge!";
        else if(unableToUse) results += this.playerPokemon[this.turn].getName() + " can't use " + move.getName() + " right now!";
        else if(!this.accurate) results += move.getMissedResult(this.playerPokemon[this.turn]);
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

            if(move.getName().equals("Taunt"))
            {
                this.usedTaunt[this.turn] = false;
                this.tauntTurns[this.turn] = this.tauntTurns[this.turn] <= 0 ? 3 : this.tauntTurns[this.turn];
            }

            if(move.getName().equals("Future Sight"))
            {
                this.usedFutureSight[this.turn] = true;
                this.futureSightTurns[this.turn] = 2;
            }

            if(move.getName().equals("Detect"))
            {
                this.usedDetect[this.turn] = true;
            }

            if(move.getName().equals("Charge")) this.usedCharge[this.turn] = true;

            if(move.getName().equals("Curse") && this.playerPokemon[this.turn].isType(Type.GHOST))
            {
                int curseDamage = this.playerPokemon[this.turn].getStat(Stat.HP) / 2;
                this.playerPokemon[this.turn].damage(curseDamage, this);

                results += " " + this.playerPokemon[this.turn].getName() + " sacrificed " + curseDamage + " HP for the curse!";
            }

            this.recharge[this.turn] = rechargeMoves.contains(move.getName());
        }

        if(this.playerPokemon[this.turn].isStatImmune())
        {
            this.statImmuneTurns[this.turn]--;

            if(this.statImmuneTurns[this.turn] <= 0)
            {
                this.statImmuneTurns[this.turn] = -1;
                this.playerPokemon[this.turn].setStatImmune(false);
            }
        }

        this.usedRage[this.turn] = accurate && move.getName().equals("Rage");

        //Status condition
        results += (!status.isEmpty() ? "\n" + this.getStatusResults(status) : "");

        //Weather (TODO: Maybe change image depending on weather? probably not since performance but cool)
        String weatherUpdate = this.duelWeather.getStatus();

        if(this.accurate)
        {
            switch (move.getName())
            {
                case "Hail" -> {
                    this.duelWeather = Weather.HAIL;
                    this.weatherTurns = 5;
                }
                case "Sunny Day" -> {
                    this.duelWeather = Weather.HARSH_SUNLIGHT;
                    this.weatherTurns = 5;
                }
                case "Rain Dance" -> {
                    this.duelWeather = Weather.RAIN;
                    this.weatherTurns = 5;
                }
                case "Sandstorm" -> {
                    this.duelWeather = Weather.SANDSTORM;
                    this.weatherTurns = 5;
                }
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

    public Move getZMove(Move baseMove)
    {
        ZCrystal z = ZCrystal.cast(this.playerData[this.turn].getEquippedZCrystal());

        Move fallback = new Move("Tackle");
        if(z == null) return fallback;

        Move ZMove = new Move("", baseMove.getType(), null, 0);

        switch(z)
        {
            //Types
            case BUGINIUM_Z -> {

                ZMove = new Move("Savage Spin Out", Type.BUG, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Fell Stinger", "Fury Cutter", "Infestation", "Struggle Bug", "Twineedle" -> ZMove.setPower(100);
                    case "Bug Bite", "Silver Wind", "Steamroller" -> ZMove.setPower(120);
                    case "Pin Missile", "Signal Beam", "U Turn" -> ZMove.setPower(140);
                    case "Leech Life", "Lunge", "X Scissor" -> ZMove.setPower(160);
                    case "Attack Order", "Bug Buzz", "First Impression", "Pollen Puff" -> ZMove.setPower(175);
                    case "Megahorn" -> ZMove.setPower(190);
                    default -> ZMove = fallback;
                }
            }
            case DARKINIUM_Z -> {

                ZMove = new Move("Black Hole Eclipse", Type.DARK, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Beat Up", "Fling", "Payback", "Pursuit", "Snarl" -> ZMove.setPower(100);
                    case "Assurance", "Bite", "Brutal Swing", "Feint Attack", "Knock Off", "Thief" -> ZMove.setPower(120);
                    case "Night Slash", "Sucker Punch" -> ZMove.setPower(140);
                    case "Crunch", "Dark Pulse", "Darkest Lariat", "Night Daze", "Power Trip", "Punishment", "Throat Chop" -> ZMove.setPower(160);
                    case "Foul Play" -> ZMove.setPower(175);
                    case "Hyperspace Fury" -> ZMove.setPower(190);
                    default -> ZMove = fallback;
                }
            }
            case DRAGONIUM_Z -> {

                ZMove = new Move("Devastating Drake", Type.DRAGON, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Dragon Rage", "Dual Chop", "Twister" -> ZMove.setPower(100);
                    case "Dragon Breath", "Dragon Tail" -> ZMove.setPower(120);
                    case "Core Enforcer" -> ZMove.setPower(140);
                    case "Dragon Claw", "Dragon Pulse" -> ZMove.setPower(160);
                    case "Dragon Hammer" -> ZMove.setPower(175);
                    case "Dragon Rush", "Spacial Rend" -> ZMove.setPower(180);
                    case "Clanging Scales" -> ZMove.setPower(185);
                    case "Outrage" -> ZMove.setPower(190);
                    case "Draco Meteor" -> ZMove.setPower(195);
                    case "Roar Of Time" -> ZMove.setPower(200);
                    default -> ZMove = fallback;
                }
            }
            case ELECTRIUM_Z -> {

                ZMove = new Move("Gigavolt Havoc", Type.ELECTRIC, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Charge Beam", "Electroweb", "Nuzzle", "Thunder Shock" -> ZMove.setPower(100);
                    case "Parabolic Charge", "Shock Wave", "Spark", "Thunder Fang" -> ZMove.setPower(120);
                    case "Thunder Punch", "Volt Switch" -> ZMove.setPower(140);
                    case "Discharge", "Electro Ball", "Zing Zap" -> ZMove.setPower(160);
                    case "Thunderbolt", "Wild Charge" -> ZMove.setPower(175);
                    case "Fusion Bolt", "Plasma Fists" -> ZMove.setPower(180);
                    case "Thunder" -> ZMove.setPower(185);
                    case "Volt Tackle", "Zap Cannon" -> ZMove.setPower(190);
                    case "Bolt Strike" -> ZMove.setPower(195);
                    default -> ZMove = fallback;
                }
            }
            case FAIRIUM_Z -> {

                ZMove = new Move("Twinkle Tackle", Type.FAIRY, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Disarming Voice", "Draining Kiss", "Fairy Wind", "Nature's Madness" -> ZMove.setPower(100);
                    case "Dazzling Gleam" -> ZMove.setPower(160);
                    case "Moonblast", "Play Rough" -> ZMove.setPower(175);
                    case "Fleur Cannon" -> ZMove.setPower(195);
                    case "Light Of Ruin" -> ZMove.setPower(200);
                    default -> ZMove = fallback;
                }
            }
            case FIGHTINIUM_Z -> {

                ZMove = new Move("All Out Pummeling", Type.FIGHTING, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Arm Thrust", "Counter", "Double Kick", "Karate Chop", "Mach Punch", "Power Up Punch", "Rock Smash", "Seismic Toss", "Vacuum Wave" -> ZMove.setPower(100);
                    case "Circle Throw", "Force Palm", "Low Sweep", "Revenge", "Rolling Kick", "Storm Throw", "Triple Kick" -> ZMove.setPower(120);
                    case "Brick Break", "Drain Punch", "Vital Throw", "Wake Up Slap" -> ZMove.setPower(140);
                    case "Aura Sphere", "Low Kick", "Reversal", "Secret Sword", "Sky Uppercut", "Submission" -> ZMove.setPower(160);
                    case "Flying Press" -> ZMove.setPower(170);
                    case "Sacred Sword" -> ZMove.setPower(175);
                    case "Cross Chop", "Dynamic Punch", "Final Gambit", "Hammer Arm", "Jump Kick" -> ZMove.setPower(180);
                    case "Close Combat", "Focus Blast", "Superpower" -> ZMove.setPower(190);
                    case "High Jump Kick" -> ZMove.setPower(195);
                    case "Focus Punch" -> ZMove.setPower(200);
                    default -> ZMove = fallback;
                }
            }
            case FIRIUM_Z -> {

                ZMove = new Move("Inferno Overdrive", Type.FIRE, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Ember", "Fire Spin", "Flame Charge" -> ZMove.setPower(100);
                    case "Fire Fang", "Flame Wheel", "Incinerate" -> ZMove.setPower(120);
                    case "Fire Punch", "Flame Burst", "Mystical Fire" -> ZMove.setPower(140);
                    case "Blaze Kick", "Fiery Dance", "Fire Lash", "Fire Pledge", "Heat Crash", "Lava Plume", "Weather Ball" -> ZMove.setPower(160);
                    case "Flamethrower", "Heat Wave" -> ZMove.setPower(175);
                    case "Fusion Flare", "Inferno", "Magma Storm", "Sacred Fire", "Searing Shot" -> ZMove.setPower(180);
                    case "Fire Blast" -> ZMove.setPower(185);
                    case "Flare Blitz" -> ZMove.setPower(190);
                    case "Blue Flare", "Burn Up", "Overheat" -> ZMove.setPower(195);
                    case "Blast Burn", "Eruption", "Mind Blown", "Shell Trap" -> ZMove.setPower(200);
                    case "V Create" -> ZMove.setPower(220);
                    default -> ZMove = fallback;
                }
            }
            case FLYINIUM_Z -> {

                ZMove = new Move("Supersonic Skystrike", Type.FLYING, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Acrobatics", "Gust", "Peck" -> ZMove.setPower(100);
                    case "Aerial Ace", "Air Cutter", "Chatter", "Pluck", "Sky Drop", "Wing Attack" -> ZMove.setPower(120);
                    case "Air Slash" -> ZMove.setPower(140);
                    case "Bounce", "Drill Peck" -> ZMove.setPower(160);
                    case "Fly" -> ZMove.setPower(175);
                    case "Aeroblast", "Beak Blast" -> ZMove.setPower(180);
                    case "Hurricane" -> ZMove.setPower(185);
                    case "Brave Bird", "Dragon Ascent" -> ZMove.setPower(190);
                    case "Sky Attack" -> ZMove.setPower(200);
                    default -> ZMove = fallback;
                }
            }
            case GHOSTIUM_Z -> {

                ZMove = new Move("Never Ending Nightmare", Type.GHOST, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Astonish", "Lick", "Night Shade", "Shadow Sneak" -> ZMove.setPower(100);
                    case "Omnious Wind", "Shadow Punch" -> ZMove.setPower(120);
                    case "Shadow Claw" -> ZMove.setPower(140);
                    case "Hex", "Shadow Ball", "Shadow Bone", "Spirit Shackle" -> ZMove.setPower(160);
                    case "Phantom Force", "Spectral Thief" -> ZMove.setPower(175);
                    case "Moongeist Beam" -> ZMove.setPower(180);
                    case "Shadow Force" -> ZMove.setPower(190);
                    default -> ZMove = fallback;
                }
            }
            case GRASSIUM_Z -> {

                ZMove = new Move("Bloom Doom", Type.GRASS, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Absorb", "Leafage", "Razor Leaf", "Vine Whip" -> ZMove.setPower(100);
                    case "Leaf Tornado", "Magical Leaf", "Mega Drain", "Needle Arm" -> ZMove.setPower(120);
                    case "Bullet Seed", "Giga Drain", "Horn Leech", "Trop Kick" -> ZMove.setPower(140);
                    case "Grass Knot", "Grass Pledge", "Seed Bomb" -> ZMove.setPower(160);
                    case "Energy Ball", "Leaf Blade", "Petal Blizzard" -> ZMove.setPower(175);
                    case "Petal Dance", "Power Whip", "Seed Flare", "Solar Beam", "Solar Blade", "Wood Hammer" -> ZMove.setPower(190);
                    case "Leaf Storm" -> ZMove.setPower(195);
                    case "Frenzy Plant" -> ZMove.setPower(200);
                    default -> ZMove = fallback;
                }
            }
            case GROUNDIUM_Z -> {

                ZMove = new Move("Tectonic Rage", Type.GROUND, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Bonemerang", "Mud Shot", "Mud Slap", "Sand Tomb" -> ZMove.setPower(100);
                    case "Bone Club", "Bulldoze", "Mud Bomb" -> ZMove.setPower(120);
                    case "Bone Rush", "Magnitude", "Stomping Tantrum" -> ZMove.setPower(140);
                    case "Dig", "Drill Run" -> ZMove.setPower(160);
                    case "Earth Power", "High Horsepower", "Thousand Waves" -> ZMove.setPower(175);
                    case "Earthquake", "Fissure", "Thousand Arrows" -> ZMove.setPower(180);
                    case "Land's Wrath" -> ZMove.setPower(185);
                    case "Precipice Blades" -> ZMove.setPower(190);
                    default -> ZMove = fallback;
                }

            }
            case ICIUM_Z -> {

                ZMove = new Move("Subzero Slammer", Type.ICE, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Ice Ball", "Ice Shard", "Icy Wind", "Powder Snow" -> ZMove.setPower(100);
                    case "Aurora Beam", "Avalanche", "Frost Breath", "Glaciate", "Ice Fang" -> ZMove.setPower(120);
                    case "Freeze Dry", "Ice Punch", "Icicle Spear" -> ZMove.setPower(140);
                    case "Icicle Crash", "Weather Ball" -> ZMove.setPower(160);
                    case "Ice Beam" -> ZMove.setPower(175);
                    case "Ice Hammer", "Sheer Cold" -> ZMove.setPower(180);
                    case "Blizzard" -> ZMove.setPower(185);
                    case "Freeze Shock", "Ice Burn" -> ZMove.setPower(200);
                    default -> ZMove = fallback;
                }

            }
            case NORMALIUM_Z -> {

                ZMove = new Move("Breakneck Blitz", Type.NORMAL, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Barrage", "Bide", "Bind", "Comet Punch", "Constrict", "Cut", "Double Slap", "Echoed Voice", "Fake Out", "False Swipe", "Feint", "Fury Attack", "Fury Swipes", "Hold Back", "Pay Day", "Pound", "Present", "Quick Attack", "Rage", "Rapid Spin", "Scratch", "Snore", "Sonic Boom", "Spike Cannon", "Spit Up", "Super Fang", "Tackle", "Vise Grip", "Wrap" -> ZMove.setPower(100);
                    case "Covet", "Hidden Power", "Horn Attack", "Round", "Stomp", "Swift" -> ZMove.setPower(120);
                    case "Chip Away", "Crush Claw", "Dizzy Punch", "Double Hit", "Facade", "Headbutt", "Relic Song", "Retaliate", "Secret Power", "Slash", "Smelling Salts", "Tail Slap" -> ZMove.setPower(140);
                    case "Body Slam", "Endeavor", "Extreme Speed", "Flail", "Frustration", "Hyper Fang", "Mega Punch", "Natural Gift", "Razor Wind", "Return", "Slam", "Strength", "Tri Attack", "Weather Ball" -> ZMove.setPower(160);
                    case "Hyper Voice", "Revelation Dance", "Rock Climb", "Take Down", "Uproar" -> ZMove.setPower(175);
                    case "Egg Bomb", "Guillotine", "Horn Drill", "Judgement" -> ZMove.setPower(180);
                    case "Multi Attack" -> ZMove.setPower(185);
                    case "Crush Grip", "Double Edge", "Head Charge", "Mega Kick", "Techno Blast", "Thrash", "Wring Out" -> ZMove.setPower(190);
                    case "Skull Bash" -> ZMove.setPower(195);
                    case "Boomburst", "Explosion", "Giga Impact", "Hyper Beam", "Last Resort", "Self Destruct" -> ZMove.setPower(200);
                    default -> ZMove = fallback;
                }

            }
            case POISONIUM_Z -> {

                ZMove = new Move("Acid Downpour", Type.POISON, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Acid", "Acid Spray", "Clear Smog", "Poison Fang", "Poison Sting", "Poison Tail", "Smog" -> ZMove.setPower(100);
                    case "Sludge", "Venoshock" -> ZMove.setPower(120);
                    case "Cross Poison" -> ZMove.setPower(140);
                    case "Poison Jab" -> ZMove.setPower(160);
                    case "Sludge Bomb", "Sludge Wave" -> ZMove.setPower(175);
                    case "Belch", "Gunk Shot" -> ZMove.setPower(190);
                    default -> ZMove = fallback;
                }

            }
            case PSYCHIUM_Z -> {

                ZMove = new Move("Shattered Psyche", Type.PSYCHIC, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Confusion", "Mirror Coat", "Psywave" -> ZMove.setPower(100);
                    case "Heart Stamp", "Psybeam" -> ZMove.setPower(120);
                    case "Luster Purge", "Mist Ball", "Psycho Cut" -> ZMove.setPower(140);
                    case "Extrasensory", "Hyperspace Hole", "Psychic Fangs", "Psyshock", "Stored Power", "Zen Headbutt" -> ZMove.setPower(160);
                    case "Psychic" -> ZMove.setPower(175);
                    case "Dream Eater", "Photon Geyser", "Psystrike" -> ZMove.setPower(180);
                    case "Future Sight", "Synchronoise" -> ZMove.setPower(190);
                    case "Prismatic Laser", "Psycho Boost" -> ZMove.setPower(200);
                    default -> ZMove = fallback;
                }

            }
            case ROCKIUM_Z -> {

                ZMove = new Move("Continental Crush", Type.ROCK, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Accelerock", "Rock Throw", "Rollout", "Smack Down" -> ZMove.setPower(100);
                    case "Ancient Power", "Rock Tomb" -> ZMove.setPower(120);
                    case "Rock Blast", "Rock Slide" -> ZMove.setPower(140);
                    case "Power Gem", "Weather Ball" -> ZMove.setPower(160);
                    case "Diamond Storm", "Stone Edge" -> ZMove.setPower(180);
                    case "Head Smash", "Rock Wrecker" -> ZMove.setPower(200);
                    default -> ZMove = fallback;
                }

            }
            case STEELIUM_Z -> {

                ZMove = new Move("Corkscrew Crash", Type.STEEL, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Bullet Punch", "Metal Burst", "Metal Claw" -> ZMove.setPower(100);
                    case "Magnet Bomb", "Mirror Shot" -> ZMove.setPower(120);
                    case "Smart Strike", "Steel Wing" -> ZMove.setPower(140);
                    case "Anchor Shot", "Flash Cannon", "Gyro Ball", "Heavy Slam", "Iron Head" -> ZMove.setPower(160);
                    case "Meteor Mash" -> ZMove.setPower(175);
                    case "Gear Grind", "Iron Tail", "Sunsteel Strike" -> ZMove.setPower(180);
                    case "Doom Desire" -> ZMove.setPower(200);
                    default -> ZMove = fallback;
                }

            }
            case WATERIUM_Z -> {

                ZMove = new Move("Hydro Vortex", Type.WATER, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Aqua Jet", "Bubble", "Clamp", "Water Gun", "Water Shuriken", "Whirlpool" -> ZMove.setPower(100);
                    case "Brine", "Bubble Beam", "Octazooka", "Water Pulse" -> ZMove.setPower(120);
                    case "Razor Shell" -> ZMove.setPower(140);
                    case "Dive", "Liquidation", "Scald", "Water Pledge", "Waterfall", "Weather Ball" -> ZMove.setPower(160);
                    case "Aqua Tail", "Muddy Water", "Sparkling Aria", "Surf" -> ZMove.setPower(175);
                    case "Crabhammer" -> ZMove.setPower(180);
                    case "Hydro Pump", "Origin Pulse", "Steam Eruption" -> ZMove.setPower(185);
                    case "Hydro Cannon", "Water Spout" -> ZMove.setPower(200);
                    default -> ZMove = fallback;
                }

            }
            //Uniques
            case ALORAICHIUM_Z -> ZMove = new Move("Stoked Sparksurfer", Type.ELECTRIC, Category.SPECIAL, 175);
            case DECIDIUM_Z -> ZMove = new Move("Sinister Arrow Raid", Type.GHOST, Category.PHYSICAL, 180);
            case EEVIUM_Z -> ZMove = new Move("Extreme Evoboost", Type.NORMAL, Category.STATUS, 0);
            case INCINIUM_Z -> ZMove = new Move("Malicious Moonsault", Type.DARK, Category.PHYSICAL, 180);
            case KOMMOIUM_Z -> ZMove = new Move("Clangorous Soulblaze", Type.DRAGON, Category.SPECIAL, 185);
            case LUNALIUM_Z -> ZMove = new Move("Menacing Moonraze Maelstrom", Type.GHOST, Category.SPECIAL, 200);
            case LYCANIUM_Z -> ZMove = new Move("Splintered Stormshards", Type.ROCK, Category.PHYSICAL, 190);
            case MARSHADIUM_Z -> ZMove = new Move("Soul Stealing 7 Star Strike", Type.GHOST, Category.PHYSICAL, 195);
            case MEWNIUM_Z -> ZMove = new Move("Genesis Supernova", Type.PSYCHIC, Category.SPECIAL, 185);
            case MIMIKIUM_Z -> ZMove = new Move("Let's Snuggle Forever", Type.FAIRY, Category.PHYSICAL, 190);
            case PIKANIUM_Z -> ZMove = new Move("Catastropika", Type.ELECTRIC, Category.PHYSICAL, 210);
            case PIKASHUNIUM_Z -> ZMove = new Move("10,000,000 Volt Thunderbolt", Type.ELECTRIC, Category.SPECIAL, 195);
            case PRIMARIUM_Z -> ZMove = new Move("Oceanic Operetta", Type.WATER, Category.SPECIAL, 195);
            case SNORLIUM_Z -> ZMove = new Move("Pulverizing Pancake", Type.NORMAL, Category.PHYSICAL, 210);
            case SOLGANIUM_Z -> ZMove = new Move("Searing Sunraze Smash", Type.STEEL, Category.PHYSICAL, 200);
            case TAPUNIUM_Z -> ZMove = new Move("Guardian of Alola", Type.FAIRY, Category.SPECIAL, 0);
            case ULTRANECROZIUM_Z -> ZMove = new Move("Light That Burns The Sky", Type.PSYCHIC, Category.SPECIAL, 200);
        }

        return ZMove;
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

    public void zcrystalEvent(Move move)
    {
        ZCrystal earnedZ = ZCrystal.getCrystalOfType(move.getType());

        System.out.println("Z-Crystal Event! " + earnedZ + ", " + move.getName() + " - " + move.getType() + "(" + this.playerData[this.turn].getUsername() + ")");

        if(!this.playerData[this.turn].hasZCrystal(earnedZ.getStyledName()))
        {
            this.playerData[this.turn].addZCrystal(earnedZ.getStyledName());

            this.event.getChannel().sendMessage("<@" + this.playerIDs[this.turn] + "> earned a Z-Crystal! You earned " + earnedZ.getStyledName() + "!");
        }
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

    public void setDuelID()
    {
        StringBuilder UUID = new StringBuilder(16);
        for(int i = 0; i < 16; i++) UUID.append("abcdefghijklmnopqrstuvwxyz".charAt((int)(Math.random() * 26)));
        this.duelID = UUID.toString();
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

    public void setUsedZMove(int turn)
    {
        this.playerZMoves[turn] = true;
    }

    //Getters
    public List<String> getPlayers()
    {
        return Arrays.asList(this.playerIDs);
    }

    public String getDuelID()
    {
        return this.duelID;
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

    public boolean hasUsedZMove(int turn)
    {
        return this.playerZMoves[turn];
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
