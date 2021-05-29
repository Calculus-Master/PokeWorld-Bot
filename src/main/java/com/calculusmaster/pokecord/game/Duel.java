package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.commands.duel.CommandDuel;
import com.calculusmaster.pokecord.game.enums.elements.*;
import com.calculusmaster.pokecord.game.enums.items.PokeItem;
import com.calculusmaster.pokecord.game.enums.items.XPBooster;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.List;

import static com.calculusmaster.pokecord.game.DuelHelper.*;

public class Duel
{
    private DuelStatus status;
    private MessageReceivedEvent event;
    private int size;
    private Player[] players;
    private Map<String, TurnAction> queuedMoves = new HashMap<>();
    private Map<String, DuelPokemon> pokemonAttributes = new HashMap<>();

    private int turn;
    private int current;
    private int other;

    public String first;

    private List<String> results;

    public Weather weather;
    public int weatherTurns;
    public Terrain terrain;
    public int terrainTurns;

    public static Duel create(String player1ID, String player2ID, int size, MessageReceivedEvent event)
    {
        Duel duel = new Duel();

        duel.setStatus(DuelStatus.WAITING);
        duel.setEvent(event);
        duel.setPlayers(player1ID, player2ID, size);
        duel.setDefaults();
        duel.setDuelPokemonObjects(0);
        duel.setDuelPokemonObjects(1);

        DUELS.add(duel);
        return duel;
    }

    //Main Duel Logic
    public void turnHandler()
    {
        this.setStatus(DuelStatus.DUELING);

        this.results = new ArrayList<>();

        this.players[0].move = null;
        this.players[1].move = null;

        this.first = "";

        //Status Conditions
        if(!this.players[0].active.isFainted()) this.data(0).canUseMove = this.statusConditionEffects(0);
        if(!this.players[1].active.isFainted()) this.data(1).canUseMove = this.statusConditionEffects(1);

        //Both players are using a move
        if(!this.queuedMoves.get(this.players[0].ID).action().equals(ActionType.SWAP) && !this.queuedMoves.get(this.players[1].ID).action().equals(ActionType.SWAP))
        {
            //Base Move
            this.players[0].move = new Move(this.players[0].active.getLearnedMoves().get(this.queuedMoves.get(this.players[0].ID).moveInd() - 1));
            this.players[1].move = new Move(this.players[1].active.getLearnedMoves().get(this.queuedMoves.get(this.players[1].ID).moveInd() - 1));

            //Check if Z-Move
            if(this.getAction(0).equals(ActionType.ZMOVE)) this.players[0].move = DuelHelper.getZMove(this.players[0], this.players[0].move);
            if(this.getAction(1).equals(ActionType.ZMOVE)) this.players[1].move = DuelHelper.getZMove(this.players[1], this.players[1].move);

            //Set who attacks first
            int speed1 = this.players[0].active.getStat(Stat.SPD);
            int speed2 = this.players[1].active.getStat(Stat.SPD);

            if(this.players[0].move.getPriority() == this.players[1].move.getPriority()) this.current = speed1 == speed2 ? (new Random().nextInt(100) < 50 ? 0 : 1) : (speed1 > speed2 ? 0 : 1);
            else this.current = this.players[0].move.getPriority() > this.players[1].move.getPriority() ? 0 : 1;

            this.other = this.current == 0 ? 1 : 0;

            this.first = this.players[this.current].active.getUUID();

            //Do moves
            if(!this.players[this.current].active.isFainted())
            {
                results.add(this.turn(this.players[this.current].move));
            }
            else results.add("\n" + this.players[this.current].active.getName() + " fainted!");

            if(!this.players[this.other].active.isFainted())
            {
                this.current = this.current == 0 ? 1 : 0;
                this.other = this.current == 0 ? 1 : 0;

                results.add("\n" + this.turn(this.players[this.current].move));
            }
            else results.add("\n" + this.players[this.other].active.getName() + " fainted!");
        }
        //Both players are swapping
        else if(this.getAction(0).equals(ActionType.SWAP) && this.getAction(1).equals(ActionType.SWAP))
        {
            this.data(0).setDefaults();
            this.data(1).setDefaults();

            int ind = this.queuedMoves.get(this.players[0].ID).swapInd() - 1;
            this.players[0].swap(ind);

            results.add(this.players[0].data.getUsername() + " brought in " + this.players[0].active.getName() + "!\n");

            ind = this.queuedMoves.get(this.players[1].ID).swapInd() - 1;
            this.players[1].swap(ind);

            results.add(this.players[1].data.getUsername() + " brought in " + this.players[1].active.getName() + "!\n");
        }
        //Either player wants to swap out a pokemon
        else
        {
            boolean player1Swap = this.getAction(0).equals(ActionType.SWAP);

            if(player1Swap) //Player 1 wants to swap
            {
                this.current = 1;
                this.other = 0;

                boolean faintSwap = this.players[0].active.isFainted();

                Move move = new Move(this.players[1].active.getLearnedMoves().get(this.queuedMoves.get(this.players[1].ID).moveInd() - 1));
                if(this.getAction(1).equals(ActionType.ZMOVE)) move = DuelHelper.getZMove(this.players[1], move);

                int ind = this.queuedMoves.get(this.players[0].ID).swapInd() - 1;
                this.data(0).setDefaults();
                this.players[0].swap(ind);

                results.add(this.players[0].data.getUsername() + " brought in " + this.players[0].active.getName() + "!\n");
                if(!faintSwap) results.add(this.turn(move));
            }
            else //Player 2 wants to swap
            {
                this.current = 0;
                this.other = 1;

                boolean faintSwap = this.players[1].active.isFainted();

                Move move = new Move(this.players[0].active.getLearnedMoves().get(this.queuedMoves.get(this.players[0].ID).moveInd() - 1));
                if(this.getAction(0).equals(ActionType.ZMOVE)) move = DuelHelper.getZMove(this.players[0], move);

                int ind = this.queuedMoves.get(this.players[1].ID).swapInd() - 1;
                this.data(1).setDefaults();
                this.players[1].swap(ind);

                results.add(this.players[1].data.getUsername() + " brought in " + this.players[1].active.getName() + "!\n");
                if(!faintSwap) results.add(this.turn(move));
            }
        }

        this.updateWeatherTerrain();

        this.weatherEffects();

        if(this.isComplete())
        {
            this.sendTurnEmbed();
            this.sendWinEmbed();
            this.setStatus(DuelStatus.COMPLETE);
        }
        else this.sendTurnEmbed();

        this.queuedMoves.clear();
    }

    //Always use this.current!
    public String turn(Move move)
    {
        String turnResult = "";

        //Weather-based Move Changes
        this.moveWeatherEffects(move);

        //Terrain-based Move Changes
        this.turnTerrainEffects(move);

        //If the pokemon uses an unfreeze remove, remove the Frozen Status Condition
        if(this.players[this.current].active.hasStatusCondition(StatusCondition.FROZEN) && Arrays.asList("Fusion Flare", "Flame Wheel", "Sacred Fire", "Flare Blitz", "Scald", "Steam Eruption").contains(move.getName())) this.players[this.current].active.removeStatusCondition(StatusCondition.FROZEN);

        //Unfreeze opponent if this move is a Fire Type, Scald or Steam Eruption
        if(move.getType().equals(Type.FIRE) || move.getName().equals("Scald") || move.getName().equals("Steam Eruption")) this.players[this.other].active.removeStatusCondition(StatusCondition.FROZEN);

        //Use the result from the Status Conditions to determine whether or not the user can move
        if(!this.data(this.current).canUseMove) return "";

        //Z-Crystal Event
        if(new Random().nextInt(100) < 5) this.zcrystalEvent(move);

        //Pre-Move Checks
        boolean accurate = move.isAccurate();
        boolean otherImmune = false;
        boolean cantUse = false;

        if(move.isZMove)
        {
            accurate = true;
            this.players[this.current].usedZMove = true;
        }

        if(this.data(this.other).imprisonUsed && this.players[this.other].active.getLearnedMoves().contains(move.getName())) cantUse = true;

        if(move.getName().equals("Defense Curl")) this.data(this.current).defenseCurlUsed = true;

        if(move.getName().equals("Rollout"))
        {
            if(accurate) this.data(this.current).rolloutTurns++;
            else this.data(this.current).rolloutTurns = 1;

            move.setPower((this.data(this.current).defenseCurlUsed ? 2 : 1) * 30 * (int) Math.pow(2, this.data(this.current).rolloutTurns));
        }
        else this.data(this.current).rolloutTurns = 0;

        if(move.getName().equals("Ice Ball"))
        {
            if(accurate) this.data(this.current).iceballTurns++;
            else this.data(this.current).iceballTurns = 1;

            move.setPower((this.data(this.current).defenseCurlUsed ? 2 : 1) * 30 * (int) Math.pow(2, this.data(this.current).iceballTurns));
        }
        else this.data(this.current).iceballTurns = 0;

        if(this.data(this.current).rageUsed && this.data(this.current).lastDamageTaken > 0)
        {
            this.players[this.current].active.changeStatMultiplier(Stat.ATK, 1);

            turnResult += this.players[this.current].active.getName() + "'s Attack rose by 1 stage due to Rage!\n";
        }

        if(this.data(this.other).magnetRiseTurns > 0)
        {
            this.data(this.other).magnetRiseTurns--;

            if(move.getType().equals(Type.GROUND)) otherImmune = true;

            if(this.data(this.other).magnetRiseTurns <= 0)
            {
                this.data(this.other).magnetRiseTurns = 0;
            }
        }

        if(this.data(this.other).tauntTurns > 0)
        {
            this.data(this.other).tauntTurns--;

            if(move.getCategory().equals(Category.STATUS)) cantUse = true;

            if(this.data(this.other).tauntTurns <= 0)
            {
                this.data(this.other).tauntTurns = 0;
            }
        }

        if(move.getName().equals("Sheer Cold"))
        {
            move.setAccuracy((this.players[this.current].active.isType(Type.ICE) ? 30 : 20) + (this.players[this.current].active.getLevel() - this.players[this.other].active.getLevel()));
            accurate = move.isAccurate();
        }

        if(move.getName().equals("Fissure"))
        {
            move.setAccuracy(20 + (this.players[this.current].active.getLevel() - this.players[this.other].active.getLevel()));
            accurate = move.isAccurate();
        }

        if(this.data(this.other).detectUsed)
        {
            this.data(this.other).detectUsed = false;

            otherImmune = true;
        }

        if(this.data(this.current).chargeUsed)
        {
            this.data(this.current).chargeUsed = false;

            if(move.getType().equals(Type.ELECTRIC)) move.setPower(move.getPower() * 2);
        }

        if(this.players[this.current].active.isStatImmune())
        {
            this.data(this.current).statImmuneTurns--;

            if(this.data(this.current).statImmuneTurns <= 0)
            {
                this.data(this.current).statImmuneTurns = 0;
                this.players[this.current].active.setStatImmune(false);
            }
        }

        if(this.data(this.current).futureSightUsed)
        {
            this.data(this.current).futureSightTurns--;

            if(this.data(this.current).futureSightTurns <= 0)
            {
                this.data(this.current).futureSightTurns = 0;
                this.data(this.current).futureSightUsed = false;

                int damage = new Move("Future Sight").getDamage(this.players[this.current].active, this.players[this.other].active);
                this.players[this.other].active.damage(damage);

                turnResult += "Future Sight landed and dealt " + damage + " to " + this.players[this.other].active.getName() + "!\n";
            }
        }

        if(this.data(this.current).lockOnUsed)
        {
            this.data(this.current).lockOnUsed = false;

            accurate = true;
        }

        if(move.getName().equals("Fusion Bolt") && !this.first.equals(this.players[this.current].active.getUUID()) && this.players[this.other].move != null && this.players[this.other].move.getName().equals("Fusion Flare")) move.setPower(move.getPower() * 2);

        if(move.getName().equals("Fusion Flare") && !this.first.equals(this.players[this.current].active.getUUID()) && this.players[this.other].move != null && this.players[this.other].move.getName().equals("Fusion Bolt")) move.setPower(move.getPower() * 2);

        //Item-based Buffs
        if(this.players[this.current].active.hasItem() && PokeItem.asItem(this.players[this.current].active.getItem()).equals(PokeItem.METAL_COAT))
        {
            if(move.getType().equals(Type.STEEL)) move.setPower((int)(move.getPower() * 1.2));
        }

        if(this.players[this.current].active.hasItem() && this.players[this.current].active.getItem().toLowerCase().contains("plate"))
        {
            PokeItem item = PokeItem.asItem(this.players[this.current].active.getItem());

            boolean buff = switch(item) {
                case DRACO_PLATE -> move.getType().equals(Type.DRAGON);
                case DREAD_PLATE -> move.getType().equals(Type.DARK);
                case EARTH_PLATE -> move.getType().equals(Type.GROUND);
                case FIST_PLATE -> move.getType().equals(Type.FIGHTING);
                case FLAME_PLATE -> move.getType().equals(Type.FIRE);
                case ICICLE_PLATE -> move.getType().equals(Type.ICE);
                case INSECT_PLATE -> move.getType().equals(Type.BUG);
                case IRON_PLATE -> move.getType().equals(Type.STEEL);
                case MEADOW_PLATE -> move.getType().equals(Type.GRASS);
                case MIND_PLATE -> move.getType().equals(Type.PSYCHIC);
                case PIXIE_PLATE -> move.getType().equals(Type.FAIRY);
                case SKY_PLATE -> move.getType().equals(Type.FLYING);
                case SPLASH_PLATE -> move.getType().equals(Type.WATER);
                case SPOOKY_PLATE -> move.getType().equals(Type.GHOST);
                case STONE_PLATE -> move.getType().equals(Type.ROCK);
                case TOXIC_PLATE -> move.getType().equals(Type.POISON);
                case ZAP_PLATE -> move.getType().equals(Type.ELECTRIC);
                default -> false;
            };

            if(buff) move.setPower(move.getPower() * 1.2);
        }

        //Main Results
        String name = this.players[this.current].active.getName();
        List<String> rechargeMoves = Arrays.asList("Hyper Beam", "Blast Burn", "Hydro Cannon", "Frenzy Plant", "Roar Of Time", "Prismatic Laser", "Eternabeam", "Giga Impact");

        //Ensures the recharge occurs when the recharge move isn't used
        if(this.data(this.current).recharge && !rechargeMoves.contains(move.getName())) this.data(this.current).recharge = false;

        //Check if user has to recharge
        if(this.data(this.current).recharge && rechargeMoves.contains(move.getName()))
        {
            turnResult += name + " can't use " + move.getName() + " because it needs to recharge!";
            this.data(this.current).recharge = false;

            this.data(this.other).lastDamageTaken = 0;
        }
        //Check if something earlier made user not able to use its move
        else if(cantUse)
        {
            turnResult += name + " can't use " + move.getName() + " right now!";

            this.data(this.other).lastDamageTaken = 0;
        }
        //Check if the user missed its move
        else if(!accurate)
        {
            turnResult += move.getMissedResult(this.players[this.current].active);

            this.data(this.other).lastDamageTaken = 0;
        }
        //Check if opponent is immune
        else if(otherImmune)
        {
            turnResult += this.players[this.other].active.getName() + " is immune to the attack!";

            this.data(this.other).lastDamageTaken = 0;
        }
        //Do main move logic
        else
        {
            turnResult += move.logic(this.players[this.current].active, this.players[this.other].active, this);

            if(move.getCategory().equals(Category.STATUS)) this.data(this.other).lastDamageTaken = 0;

            if(rechargeMoves.contains(move.getName())) this.data(this.current).recharge = true;
        }

        //Give EVs if opponent has fainted
        if(this.players[this.other].active.isFainted())
        {
            this.players[this.turn].active.gainEVs(this.players[this.other].active);
            new Thread(() -> Pokemon.updateEVs(this.players[this.turn].active)).start();
        }

        return turnResult;
    }

    public void zcrystalEvent(Move move)
    {
        ZCrystal earnedZ = ZCrystal.getCrystalOfType(move.getType());

        System.out.println("Z-Crystal Event! " + earnedZ + ", " + move.getName() + " - " + move.getType() + "(" + this.players[this.current].data.getUsername() + ")");

        if(!this.players[this.current].data.hasZCrystal(earnedZ.getStyledName()))
        {
            this.players[this.current].data.addZCrystal(earnedZ.getStyledName());

            this.event.getChannel().sendMessage("<@" + this.players[this.current].ID + "> earned a Z-Crystal! You earned " + earnedZ.getStyledName() + "!").queue();
        }
    }

    //Turn Helper Methods
    public void setDefaults()
    {
        this.weather = Weather.CLEAR;
        this.terrain = Terrain.NORMAL_TERRAIN;
    }

    public void updateWeatherTerrain()
    {
        if(this.weatherTurns <= 0)
        {
            this.weatherTurns = 0;
            this.weather = Weather.CLEAR;
        }

        if(this.weatherTurns > 0) this.weatherTurns--;

        if(this.terrainTurns <= 0)
        {
            this.terrainTurns = 0;
            this.terrain = Terrain.NORMAL_TERRAIN;
        }

        if(this.terrainTurns > 0) this.terrainTurns--;
    }

    public void weatherEffects()
    {
        StringBuilder weatherResult = new StringBuilder().append("\n").append(this.weather.getStatus()).append("\n");

        switch(this.weather)
        {
            case HAIL -> {

                boolean is1Affected = !this.players[0].active.isType(Type.ICE);
                boolean is2Affected = !this.players[1].active.isType(Type.ICE);

                if(is1Affected)
                {
                    this.players[0].active.damage(this.players[0].active.getStat(Stat.HP) / 16);
                    weatherResult.append(this.players[0].active.getName()).append(" took damage from the hailstorm!\n");
                }
                if(is2Affected)
                {
                    this.players[1].active.damage(this.players[1].active.getStat(Stat.HP) / 16);
                    weatherResult.append(this.players[1].active.getName()).append(" took damage from the hailstorm!\n");
                }

            }
            case SANDSTORM -> {

                boolean is1Affected = !this.players[0].active.isType(Type.GROUND) && !this.players[0].active.isType(Type.ROCK) && !this.players[0].active.isType(Type.STEEL);
                boolean is2Affected = !this.players[1].active.isType(Type.GROUND) && !this.players[1].active.isType(Type.ROCK) && !this.players[1].active.isType(Type.STEEL);

                if(is1Affected)
                {
                    this.players[0].active.damage(this.players[0].active.getStat(Stat.HP) / 16);
                    weatherResult.append(this.players[0].active.getName()).append(" took damage from the sandstorm!\n");
                }
                if(is2Affected)
                {
                    this.players[1].active.damage(this.players[1].active.getStat(Stat.HP) / 16);
                    weatherResult.append(this.players[1].active.getName()).append(" took damage from the sandstorm!\n");
                }
            }
        }

        this.results.add(weatherResult.toString());
    }

    public void moveWeatherEffects(Move move)
    {
        switch(this.weather)
        {
            case HAIL -> {
                if(move.getName().equals("Blizzard")) move.setAccuracy(100);

                if(move.getName().equals("Solar Beam") || move.getName().equals("Solar Blade")) move.setPower(move.getPower() / 2);
            }
            case HARSH_SUNLIGHT -> {
                if(move.getType().equals(Type.FIRE)) move.setPower((int)(move.getPower() * 1.5));
                else if(move.getType().equals(Type.WATER)) move.setPower((int)(move.getPower() * 0.5));

                if(move.getName().equals("Thunder") || move.getName().equals("Hurricane")) move.setAccuracy(50);
            }
            case RAIN -> {
                if(move.getType().equals(Type.WATER)) move.setPower((int)(move.getPower() * 1.5));
                else if(move.getType().equals(Type.FIRE) || move.getName().equals("Solar Beam") || move.getName().equals("Solar Blade")) move.setPower((int)(move.getPower() * 0.5));

                if(move.getName().equals("Thunder") || move.getName().equals("Hurricane")) move.setAccuracy(100);
            }
            case SANDSTORM -> {
                if(move.getName().equals("Solar Beam") || move.getName().equals("Solar Blade")) move.setPower(move.getPower() / 2);
            }
        }
    }

    public void turnTerrainEffects(Move move)
    {
        switch(this.terrain)
        {
            case ELECRIC_TERRAIN -> {
                if(move.getType().equals(Type.ELECTRIC)) move.setPower(move.getPower() * 1.5);
            }
            case GRASSY_TERRAIN -> {
                if(move.getType().equals(Type.GRASS)) move.setPower(move.getPower() * 1.5);
            }
            case MISTY_TERRAIN -> {
                if(move.getType().equals(Type.DRAGON)) move.setDamageMultiplier(0.5);
            }
            case PSYCHIC_TERRAIN -> {
                if(move.getType().equals(Type.PSYCHIC)) move.setPower(move.getPower() * 1.5);
            }
        }
    }

    //Returns true if the pokemon can attack
    public boolean statusConditionEffects(int p)
    {
        StringBuilder status = new StringBuilder();
        Random r = new Random();
        int statusDamage = 0;

        String name = this.players[p].active.getName();
        Pokemon pokemon = this.players[p].active;

        if(pokemon.hasStatusCondition(StatusCondition.BURNED))
        {
            statusDamage = (int)(this.players[0].active.getStat(Stat.HP) / 16.);
            pokemon.damage(statusDamage);

            status.append(name).append(" is burned! The burn dealt ").append(statusDamage).append(" damage!\n");
        }

        if(pokemon.hasStatusCondition(StatusCondition.POISONED))
        {
            statusDamage = (int)(pokemon.getStat(Stat.HP) / 8.);
            pokemon.damage(statusDamage);

            status.append(name).append(" is poisoned! The poison dealt ").append(statusDamage).append(" damage!\n");
        }

        if(pokemon.hasStatusCondition(StatusCondition.CURSED))
        {
            statusDamage = pokemon.getStat(Stat.HP) / 4;
            pokemon.damage(statusDamage);

            status.append(name).append(" is cursed! The curse dealt ").append(statusDamage).append(" damage!\n");
        }

        if(pokemon.hasStatusCondition(StatusCondition.BOUND))
        {
            this.data(p).boundTurns++;

            if(this.data(p).boundTurns == 5)
            {
                this.data(p).boundTurns = 0;

                status.append(name).append(" is no longer bound!");
            }
            else
            {
                statusDamage = pokemon.getStat(Stat.HP) / 8;
                pokemon.damage(statusDamage);

                status.append(name).append(" is bound! The binding dealt ").append(statusDamage).append(" damage!\n");
            }
        }

        if(pokemon.hasStatusCondition(StatusCondition.CONFUSED))
        {
            if(r.nextInt(100) < 33)
            {
                statusDamage = new Move("Tackle").getDamage(pokemon, pokemon);
                pokemon.damage(statusDamage);

                status.append(name).append(" is confused! It hurt itself in its confusion for ").append(statusDamage).append(" damage!\n");
                results.add(status.toString());
                return false;
            }
            else if(r.nextInt(100) < 50) pokemon.removeStatusCondition(StatusCondition.CONFUSED);
        }

        if(pokemon.hasStatusCondition(StatusCondition.FROZEN))
        {
            if(r.nextInt(100) < 20 || this.weather.equals(Weather.HARSH_SUNLIGHT))
            {
                pokemon.removeStatusCondition(StatusCondition.FROZEN);

                status.append(name).append(" has thawed out").append(this.weather.equals(Weather.HARSH_SUNLIGHT) ? " due to the harsh sunlight!" : "!");
            }
            else
            {
                status.append(name).append(" is frozen and can't use any moves!");
                results.add(status.toString());
                return false;
            }
        }

        if(pokemon.hasStatusCondition(StatusCondition.ASLEEP))
        {
            if(this.data(p).asleepTurns == 2 || this.terrain.equals(Terrain.ELECRIC_TERRAIN))
            {
                pokemon.removeStatusCondition(StatusCondition.ASLEEP);
                this.data(p).asleepTurns = 0;

                status.append(name).append(" woke up").append(this.terrain.equals(Terrain.ELECRIC_TERRAIN) ? " due to the Electric Field!\n" : "!\n");
                if(pokemon.hasStatusCondition(StatusCondition.NIGHTMARE))
                {
                    pokemon.removeStatusCondition(StatusCondition.NIGHTMARE);
                    status.append("The nightmare was removed!\n");
                }
            }
            else
            {
                this.data(p).asleepTurns++;

                if(pokemon.hasStatusCondition(StatusCondition.NIGHTMARE))
                {
                    statusDamage = pokemon.getStat(Stat.HP) / 4;
                    pokemon.damage(statusDamage);

                    status.append(name).append(" has a nightmare!").append("The nightmare dealt ").append(statusDamage).append(" damage!");
                }

                status.append(name).append(" is asleep!");

                results.add(status.toString());
                return false;
            }
        }

        if(pokemon.hasStatusCondition(StatusCondition.PARALYZED))
        {
            if(r.nextInt(100) < 25)
            {
                status.append(name).append(" is paralyzed and can't move!");

                results.add(status.toString());
                return false;
            }
        }

        if(pokemon.hasStatusCondition(StatusCondition.FLINCHED))
        {
            pokemon.removeStatusCondition(StatusCondition.FLINCHED);

            status.append(name).append(" flinched and can't move!");

            results.add(status.toString());
            return false;
        }

        results.add(status.toString());
        return true;
    }

    public void setDuelPokemonObjects(int player)
    {
        for(Pokemon p : this.players[player].team) this.pokemonAttributes.put(p.getUUID(), new DuelPokemon(p.getUUID()));
    }

    //Response Embeds

    public void sendTurnEmbed()
    {
        EmbedBuilder embed = new EmbedBuilder();

        StringBuilder s = new StringBuilder();

        s.append(this.getHealthBars()).append("\n\n");

        if(this.results != null) for(String str : this.results) s.append(str).append(" ");

        embed.setDescription(s.toString().trim());

        embed.setImage("attachment://duel.png");

        try
        {
            this.event.getChannel().sendFile(this.getImage(), "duel.png").embed(embed.build()).queue();
        }
        catch (Exception e)
        {
            this.event.getChannel().sendMessage(embed.build()).queue();
            e.printStackTrace();
        }
    }

    public void sendWinEmbed()
    {
        EmbedBuilder embed = new EmbedBuilder();

        int c = this.giveWinCredits();
        int exp = this.giveWinExp();

        embed.setDescription(this.getWinner().data.getUsername() + " has won!\nThey earned " + c + " credits and no exp (WIP)!");

        this.event.getChannel().sendMessage(embed.build()).queue();

        DuelHelper.delete(this.players[0].ID);
    }

    private int giveWinExp()
    {
        double booster = this.getWinner().data.hasXPBooster() ? XPBooster.getInstance(this.getWinner().data.getXPBoosterLength()).boost : 1.0;

        //int winExp = (int)(booster * win.getDuelExp(this.playerPokemon[winner == 0 ? 1 : 0]));
        //win.addExp(this.winExp);
        //win.gainEVs(this.playerPokemon[winner == 0 ? 1 : 0]);

        //Pokemon.updateExperience(win);
        //Pokemon.updateEVs(win);
        return 0;
    }

    private int giveWinCredits()
    {
        int winCredits = new Random().nextInt(501) + 500;
        this.getWinner().data.changeCredits(winCredits);
        return winCredits;
    }

    //Useful Getters/Setters
    public void submitMove(String id, int moveIndex, boolean z)
    {
        this.queuedMoves.put(id, new TurnAction(z ? ActionType.ZMOVE : ActionType.MOVE, moveIndex, -1));
    }

    public void submitMove(String id, int swapIndex)
    {
        if(this.players[this.indexOf(id)].team.get(swapIndex - 1).isFainted())
        {
            this.event.getChannel().sendMessage("That pokemon is fainted!").queue();
            return;
        }

        this.queuedMoves.put(id, new TurnAction(ActionType.SWAP, -1, swapIndex));
    }

    public void checkReady()
    {
        if(this.queuedMoves.containsKey(this.players[0].ID) && this.queuedMoves.containsKey(this.players[1].ID))
        {
            turnHandler();
        }

        boolean faintSwap1 = this.queuedMoves.containsKey(this.players[0].ID) && this.queuedMoves.get(this.players[0].ID).action().equals(ActionType.SWAP) && this.players[0].active.isFainted();
        boolean faintSwap2 = this.queuedMoves.containsKey(this.players[1].ID) && this.queuedMoves.get(this.players[1].ID).action().equals(ActionType.SWAP) && this.players[1].active.isFainted();

        if(faintSwap1 || faintSwap2)
        {
            turnHandler();
        }
    }

    public void addDamage(int damage, String UUID)
    {
        this.data(UUID).lastDamageTaken = damage;
    }

    public boolean hasPlayerSubmittedMove(String id)
    {
        return this.queuedMoves.containsKey(id);
    }

    public boolean isComplete()
    {
        return this.players[0].lost() || this.players[1].lost();
    }

    public ActionType getAction(int player)
    {
        return this.queuedMoves.get(this.players[player].ID).action();
    }

    public DuelPokemon data(String UUID)
    {
        return this.pokemonAttributes.get(UUID);
    }

    public DuelPokemon data(int player)
    {
        return this.data(this.players[player].active.getUUID());
    }

    public Player getWinner()
    {
        return this.players[0].lost() ? this.players[1] : this.players[0];
    }

    public InputStream getImage() throws Exception
    {
        //Background is 800 x 480 -> 400 x 240

        int size = 150;
        int y = 50;
        int spacing = 25;
        int backgroundW = 400;
        int backgroundH = 240;
        int hint = BufferedImage.TYPE_INT_ARGB;

        Image background = ImageIO.read(new URL(BACKGROUND)).getScaledInstance(backgroundW, backgroundH, hint);
        BufferedImage combined = new BufferedImage(background.getWidth(null), background.getHeight(null), hint);

        combined.getGraphics().drawImage(background, 0, 0, null);

        if(!this.players[0].active.isFainted())
        {
            Image p1 = ImageIO.read(this.players[0].active.getURL()).getScaledInstance(size, size, hint);
            combined.getGraphics().drawImage(p1, spacing, y, null);
        }

        if(!this.players[1].active.isFainted())
        {
            Image p2 = ImageIO.read(this.players[1].active.getURL()).getScaledInstance(size, size, hint);
            combined.getGraphics().drawImage(p2, (backgroundW - spacing) - size, y, null);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(combined, "png", out);

        byte[] bytes = out.toByteArray(); //This is the slow line

        return new ByteArrayInputStream(bytes);
    }

    private String getHealthBars()
    {
        String healthBarP1 = this.getHB(0);
        String healthBarP2 = this.getHB(1);

        return this.isComplete() ? "" : healthBarP1 + "\n" + healthBarP2;
    }

    private String getHB(int p)
    {
        StringBuilder sb = new StringBuilder().append(this.players[p].data.getUsername()).append("'s ").append(this.players[p].active.getName()).append(": ");

        if(this.players[p].active.isFainted()) sb.append("FAINTED");
        else sb.append(this.players[p].active.getHealth()).append(" / ").append(this.players[p].active.getStat(Stat.HP)).append(" HP ").append(this.players[p].active.getActiveStatusConditions());

        return sb.toString();
    }

    //Core Getters and Setters

    public void setStatus(DuelStatus status)
    {
        this.status = status;
    }

    public DuelStatus getStatus()
    {
        return this.status;
    }

    private void setEvent(MessageReceivedEvent event)
    {
        this.event = event;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public int getSize()
    {
        return this.size;
    }

    public void setPlayers(String player1ID, String player2ID, int size)
    {
        this.players = new Player[]{new Player(player1ID, size), new Player(player2ID, size)};
    }

    public Player[] getPlayers()
    {
        return this.players;
    }

    public int indexOf(String id)
    {
        return this.players[0].ID.equals(id) ? 0 : 1;
    }

    public boolean hasPlayer(String id)
    {
        return this.players[0].ID.equals(id) || this.players[1].ID.equals(id);
    }
}
