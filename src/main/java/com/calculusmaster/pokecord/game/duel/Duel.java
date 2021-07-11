package com.calculusmaster.pokecord.game.duel;

import com.calculusmaster.pokecord.commands.pokemon.CommandTeam;
import com.calculusmaster.pokecord.game.Achievements;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.TypeEffectiveness;
import com.calculusmaster.pokecord.game.duel.elements.Player;
import com.calculusmaster.pokecord.game.enums.elements.*;
import com.calculusmaster.pokecord.game.enums.items.PokeItem;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.mongo.PokemonStatisticsQuery;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import com.calculusmaster.pokecord.util.enums.PokemonStatistic;
import com.calculusmaster.pokecord.util.listener.ButtonListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.calculusmaster.pokecord.game.duel.DuelHelper.*;

//PVP Duel - Infinitely Scalable
public class Duel
{
    protected DuelStatus status;
    protected MessageReceivedEvent event;
    protected int size;
    protected Player[] players;
    protected Map<String, TurnAction> queuedMoves = new HashMap<>();
    protected Map<String, DuelPokemon> pokemonAttributes = new HashMap<>();
    protected Map<String, Integer> expGains = new HashMap<>();

    public int turn;
    public int current;
    public int other;

    public Map<Integer, String[]> moveLog;

    public String first;

    protected List<String> results;

    public Weather weather;
    public int weatherTurns;
    public Terrain terrain;
    public int terrainTurns;
    public Room room;
    public int roomTurns;
    public EntryHazardHandler[] entryHazards;

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
        this.turnSetup();

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

            //Check if Entering Dynamax
            if(this.getAction(0).equals(ActionType.DYNAMAX))
            {
                this.players[0].active.enterDynamax();
                this.players[0].dynamaxTurns = 3;
                this.players[0].usedDynamax = true;

                this.results.add(this.players[0].active.getName() + " dynamaxed!\n");
            }
            if(this.getAction(1).equals(ActionType.DYNAMAX))
            {
                this.players[1].active.enterDynamax();
                this.players[1].dynamaxTurns = 3;
                this.players[1].usedDynamax = true;

                this.results.add(this.players[1].active.getName() + " dynamaxed!\n");
            }

            //Check if Max Move (Dynamaxed)
            if(this.players[0].active.isDynamaxed()) this.players[0].move = DuelHelper.getMaxMove(this.players[0].active, this.players[0].move);
            if(this.players[1].active.isDynamaxed()) this.players[1].move = DuelHelper.getMaxMove(this.players[1].active, this.players[1].move);

            //Set who attacks first
            int speed1 = this.players[0].active.getStat(Stat.SPD);
            int speed2 = this.players[1].active.getStat(Stat.SPD);

            if(this.players[0].move.getPriority() == this.players[1].move.getPriority())
            {
                this.current = speed1 == speed2 ? (new Random().nextInt(100) < 50 ? 0 : 1) : (speed1 > speed2 ? 0 : 1);

                if(this.room.equals(Room.TRICK_ROOM)) this.current = this.current == 0 ? 1 : 0;
            }
            else
            {
                this.current = this.players[0].move.getPriority() > this.players[1].move.getPriority() ? 0 : 1;
            }

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

            this.entryHazardEffects(0);

            ind = this.queuedMoves.get(this.players[1].ID).swapInd() - 1;
            this.players[1].swap(ind);

            results.add(this.players[1].data.getUsername() + " brought in " + this.players[1].active.getName() + "!\n");

            this.entryHazardEffects(1);

            this.checkWeatherAbilities();
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
                if(this.getAction(1).equals(ActionType.DYNAMAX))
                {
                    this.players[1].active.enterDynamax();
                    this.players[1].dynamaxTurns = 3;
                    this.players[1].usedDynamax = true;
                    this.results.add(this.players[1].active.getName() + " dynamaxed!\n");
                }

                if(!faintSwap && this.players[1].active.isDynamaxed()) move = DuelHelper.getMaxMove(this.players[1].active, this.players[1].move);

                int ind = this.queuedMoves.get(this.players[0].ID).swapInd() - 1;
                this.data(0).setDefaults();
                this.players[0].swap(ind);

                results.add(this.players[0].data.getUsername() + " brought in " + this.players[0].active.getName() + "!\n");

                this.checkWeatherAbilities(0);
                this.entryHazardEffects(0);

                if(!faintSwap) results.add(this.turn(move));
            }
            else //Player 2 wants to swap
            {
                this.current = 0;
                this.other = 1;

                boolean faintSwap = this.players[1].active.isFainted();
                //TODO: move to methods, also remove the entire zmove and dynamax check if this is an AI duel & the trainer swaps
                //TODO: Add TurnAction: Idle - player does nothing on that turn and Item - usable items (maybe????)

                Move move = new Move(this.players[0].active.getLearnedMoves().get(this.queuedMoves.get(this.players[0].ID).moveInd() - 1));
                if(this.getAction(0).equals(ActionType.ZMOVE)) move = DuelHelper.getZMove(this.players[0], move);
                if(this.getAction(0).equals(ActionType.DYNAMAX))
                {
                    this.players[0].active.enterDynamax();
                    this.players[0].dynamaxTurns = 3;
                    this.players[0].usedDynamax = true;
                    this.results.add(this.players[0].active.getName() + " dynamaxed!\n");
                }

                if(!faintSwap && this.players[0].active.isDynamaxed()) move = DuelHelper.getMaxMove(this.players[0].active, this.players[0].move);

                int ind = this.queuedMoves.get(this.players[1].ID).swapInd() - 1;
                this.data(1).setDefaults();
                this.players[1].swap(ind);

                results.add(this.players[1].data.getUsername() + " brought in " + this.players[1].active.getName() + "!\n");

                this.checkWeatherAbilities(1);
                this.entryHazardEffects(1);

                if(!faintSwap) results.add(this.turn(move));
            }
        }

        this.updateWeatherTerrainRoom();

        this.weatherEffects();

        this.checkDynamax(0);
        this.checkDynamax(1);

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

        //Arceus Plate
        if(this.players[this.current].active.getAbilities().contains("Multitype"))
        {
            PokeItem item = PokeItem.asItem(this.players[this.current].active.getItem());

            if(item.isPlateItem())
            {
                Type t = PokeItem.getArceusPlateType(item);

                this.players[this.current].active.setType(t, 0);
                this.players[this.current].active.setType(t, 1);

                if(move.getName().equals("Judgement")) move.setType(t);
            }
        }

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

        //Re-Check Certain Status Conditions
        if(this.players[this.current].active.hasStatusCondition(StatusCondition.ASLEEP))
        {
            return this.players[this.current].active.getName() + " is asleep!";
        }

        if(this.players[this.current].active.hasStatusCondition(StatusCondition.PARALYZED))
        {
            if(new Random().nextInt(100) < 20) return this.players[this.current].active.getName() + " is paralyzed!";
        }

        if(this.data(this.current).perishSongTurns > 0)
        {
            this.data(this.current).perishSongTurns--;

            if(this.data(this.current).perishSongTurns <= 0)
            {
                this.data(this.current).perishSongTurns = 0;

                this.players[this.current].active.damage(this.players[this.current].active.getHealth());
                return turnResult + " Perish Song hit! " + this.players[this.current].active.getName() + " fainted!";
            }
        }

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

            Achievements.grant(this.players[this.current].ID, Achievements.DUEL_USE_ZMOVE, this.event);
        }

        if(move.isMaxMove)
        {
            accurate = true;
            this.players[this.current].usedDynamax = true;

            Achievements.grant(this.players[this.current].ID, Achievements.DUEL_USE_DYNAMAX, this.event);
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

        if(move.getName().equals("Horn Drill"))
        {
            move.setAccuracy(30 + (this.players[this.current].active.getLevel() - this.players[this.other].active.getLevel()));
            accurate = move.isAccurate();
        }

        boolean bypass = (move.getName().equals("Phantom Force") && this.data(this.current).phantomForceUsed) || (move.getName().equals("Shadow Force") && this.data(this.current).shadowForceUsed) || move.getName().equals("Feint");

        if(this.data(this.other).detectUsed)
        {
            this.data(this.other).detectUsed = false;

            otherImmune = !bypass;
        }

        if(this.data(this.other).protectUsed)
        {
            this.data(this.other).protectUsed = false;

            otherImmune = !bypass;
        }

        if(this.data(this.other).matBlockUsed)
        {
            this.data(this.other).matBlockUsed = false;

            if(!move.getCategory().equals(Category.STATUS)) otherImmune = !bypass;
        }

        if(this.data(this.current).chargeUsed)
        {
            this.data(this.current).chargeUsed = false;

            if(move.getType().equals(Type.ELECTRIC)) move.setPower(move.getPower() * 2);
        }

        if(this.data(this.current).statImmuneTurns > 0)
        {
            this.data(this.current).statImmuneTurns--;

            if(this.data(this.current).statImmuneTurns <= 0)
            {
                this.data(this.current).statImmuneTurns = 0;
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

        if(this.data(this.current).doomDesireUsed)
        {
            this.data(this.current).doomDesireTurns--;

            if(this.data(this.current).doomDesireTurns <= 0)
            {
                this.data(this.current).doomDesireTurns = 0;
                this.data(this.current).doomDesireUsed = false;

                int damage = new Move("Doom Desire").getDamage(this.players[this.current].active, this.players[this.other].active);
                this.players[this.other].active.damage(damage);

                turnResult += "Doom Desire landed and dealt " + damage + " to " + this.players[this.other].active.getName() + "!\n";
            }
        }

        if(this.data(this.current).lockOnUsed)
        {
            this.data(this.current).lockOnUsed = false;

            accurate = true;
        }

        if(this.data(this.other).kingsShieldUsed)
        {
            this.data(this.other).kingsShieldUsed = false;

            if(!move.getCategory().equals(Category.STATUS))
            {
                this.players[this.current].active.changeStatMultiplier(Stat.ATK, -2);
                otherImmune = !bypass;

                turnResult += this.players[this.current].active.getName() + "'s Attack was lowered by 2 stages due to the King's Shield!\n";
            }
        }

        if(this.data(this.other).banefulBunkerUsed)
        {
            this.data(this.other).banefulBunkerUsed = false;

            if(!move.getCategory().equals(Category.STATUS))
            {
                this.players[this.current].active.addStatusCondition(StatusCondition.POISONED);
                otherImmune = !bypass;

                turnResult += this.players[this.current].active.getName() + " was poisoned due to the Baneful Bunker!";
            }
        }

        if(this.data(this.other).spikyShieldUsed)
        {
            this.data(this.other).spikyShieldUsed = false;

            if(!move.getCategory().equals(Category.STATUS))
            {
                int damage = this.players[this.current].active.getStat(Stat.HP) / 8;
                this.players[this.current].active.damage(damage);

                otherImmune = !bypass;

                turnResult += this.players[this.current].active.getName() + " took " + damage + " damage due to the Spiky Shield!";
            }
        }

        if(this.data(this.other).quickGuardUsed)
        {
            this.data(this.other).quickGuardUsed = false;

            if(move.getPriority() > 0) otherImmune = !bypass;
        }

        if(move.getName().equals("Fusion Bolt") && !this.first.equals(this.players[this.current].active.getUUID()) && this.players[this.other].move != null && this.players[this.other].move.getName().equals("Fusion Flare")) move.setPower(move.getPower() * 2);

        if(move.getName().equals("Fusion Flare") && !this.first.equals(this.players[this.current].active.getUUID()) && this.players[this.other].move != null && this.players[this.other].move.getName().equals("Fusion Bolt")) move.setPower(move.getPower() * 2);

        if(this.data(this.other).bideTurns <= 0 && this.data(this.other).bideDamage != 0)
        {
            this.players[this.current].active.damage(this.data(this.other).bideDamage);

            turnResult += this.players[this.other].active.getName() + " unleashed stored energy and dealt " + this.data(this.other).bideDamage + " damage!\n";

            this.data(this.other).bideTurns = 0;
            this.data(this.other).bideDamage = 0;
        }

        if(this.data(this.current).focusEnergyUsed) move.critChance *= 3;

        if(this.data(this.current).laserFocusUsed) move.critChance = 24;

        if(this.data(this.current).mudSportUsed || this.data(this.other).mudSportUsed)
        {
            if(move.getType().equals(Type.ELECTRIC)) move.setPower(move.getPower() * 0.5);
        }

        //Fly, Bounce, Dig and Dive

        if(this.data(this.current).flyUsed) move = new Move("Fly");

        if(this.data(this.current).bounceUsed) move = new Move("Bounce");

        if(this.data(this.current).digUsed) move = new Move("Dig");

        if(this.data(this.current).diveUsed) move = new Move("Dive");

        if(this.data(this.current).phantomForceUsed) move = new Move("Phantom Force");

        if(this.data(this.current).shadowForceUsed) move = new Move("Shadow Force");

        List<String> flyMoves = Arrays.asList("Gust", "Twister", "Thunder", "Sky Uppercut", "Smack Down");
        List<String> digMoves = Arrays.asList("Earthquake", "Magnitude", "Fissure");
        List<String> diveMoves = Arrays.asList("Surf", "Whirlpool", "Low Kick");

        if((this.data(this.other).flyUsed && !flyMoves.contains(move.getName())) || (this.data(this.other).bounceUsed && !flyMoves.contains(move.getName())) || (this.data(this.other).digUsed && !digMoves.contains(move.getName())) || (this.data(this.other).diveUsed && !diveMoves.contains(move.getName())) || this.data(this.other).phantomForceUsed || this.data(this.other).shadowForceUsed)
        {
            otherImmune = true;
        }

        //Lowered Accuracy of Defensive Moves
        List<String> defensiveMoves = Arrays.asList("Endure", "Protect", "Detect", "Wide Guard", "Quick Guard", "Spiky Shield", "Kings Shield", "Baneful Bunker");

        if(defensiveMoves.contains(move.getName()))
        {
            int i = this.turn - 1;
            while(this.moveLog.containsKey(i) && defensiveMoves.contains(this.moveLog.get(i)[this.current]) && (!move.getName().equals("Quick Guard") || !this.moveLog.get(i)[this.current].equals("Quick Guard")))
            {
                move.setAccuracy(move.getAccuracy() / 3);
                i--;
            }
        }

        //Item-based Buffs
        boolean itemsOff = this.room.equals(Room.MAGIC_ROOM);

        if(!itemsOff && this.players[this.current].active.hasItem() && PokeItem.asItem(this.players[this.current].active.getItem()).equals(PokeItem.METAL_COAT))
        {
            if(move.getType().equals(Type.STEEL)) move.setPower((int)(move.getPower() * 1.2));
        }

        if(!itemsOff && this.players[this.current].active.hasItem() && this.players[this.current].active.getItem().toLowerCase().contains("plate"))
        {
            PokeItem item = PokeItem.asItem(this.players[this.current].active.getItem());

            boolean buff = PokeItem.getArceusPlateType(item) != null && PokeItem.getArceusPlateType(item).equals(move.getType());

            if(buff) move.setPower(move.getPower() * 1.2);
        }

        //Main Results
        String name = this.players[this.current].active.getName();
        List<String> rechargeMoves = Arrays.asList("Hyper Beam", "Blast Burn", "Hydro Cannon", "Frenzy Plant", "Roar Of Time", "Prismatic Laser", "Eternabeam", "Giga Impact", "Meteor Assault");

        //Ensures the recharge occurs when the recharge move isn't used
        if(this.data(this.current).recharge && !rechargeMoves.contains(move.getName())) this.data(this.current).recharge = false;

        //Raised Immunity
        if(this.data(this.other).isRaised && move.getType().equals(Type.GROUND)) otherImmune = true;

        //Hyperspace Fury and Hyperspace Hole bypass immunities and always hit
        if(move.getName().equals("Hyperspace Fury") || move.getName().equals("Hyperspace Hole"))
        {
            accurate = true;
            otherImmune = false;
        }

        //Ability: Stance Change (Aegislash)
        if(!move.getCategory().equals(Category.STATUS) && this.players[this.current].active.getAbilities().contains("Stance Change"))
        {
            if(this.players[this.current].active.getName().equals("Aegislash"))
            {
                this.players[this.current].active.changeForm("Aegislash Blade");
            }
        }

        //Bide
        if(this.data(this.current).bideTurns > 0)
        {
            turnResult += this.players[this.current].active.getName() + " is storing energy!";
        }
        //Ability: Disguise (Mimikyu)
        if(this.players[this.other].active.getAbilities().contains("Disguise") && !this.data(this.other).disguiseActivated && !move.getCategory().equals(Category.STATUS))
        {
            this.data(this.other).disguiseActivated = true;

            turnResult += "Mimikyu's Disguise was activated and absorbed the attack!";
        }
        //Check if user has to recharge
        else if(this.data(this.current).recharge && rechargeMoves.contains(move.getName()))
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

            if(move.getName().equals("Jump Kick") || move.getName().equals("High Jump Kick"))
            {
                turnResult += " " + this.players[this.current].active.getName() + " kept going and crashed!";

                this.players[this.current].active.damage(this.players[this.current].active.getStat(Stat.HP) / 2);
            }

            this.data(this.other).lastDamageTaken = 0;
        }
        //Check if opponent is immune
        else if(otherImmune)
        {
            turnResult += move.getMoveUsedResult(this.players[this.current].active) + " " + this.players[this.other].active.getName() + " is immune to the attack!";

            this.data(this.other).lastDamageTaken = 0;
        }
        //Do main move logic
        else
        {
            int preMoveHP = this.players[this.other].active.getHealth();

            turnResult += move.logic(this.players[this.current].active, this.players[this.other].active, this);

            if(move.getCategory().equals(Category.STATUS)) this.data(this.other).lastDamageTaken = 0;

            if(rechargeMoves.contains(move.getName())) this.data(this.current).recharge = true;

            if(this.players[this.other].active.getAbilities().contains("Iron Barbs") && !move.getCategory().equals(Category.STATUS) && this.data(this.other).lastDamageTaken > 0)
            {
                int dmg = this.players[this.current].active.getStat(Stat.HP) / 8;
                this.players[this.current].active.damage(dmg);

                turnResult += "\n" + this.players[this.current].active.getName() + " took " + dmg + " damage from the Iron Barbs!";
            }

            if(this.data(this.other).bideTurns > 0)
            {
                this.data(this.other).bideTurns--;

                this.data(this.other).bideDamage += Math.max(preMoveHP - this.players[this.other].active.getHealth(), 0);
            }
        }

        //Give EVs and EXP if opponent has fainted
        if(this.players[this.other].active.isFainted())
        {
            this.players[this.current].active.gainEVs(this.players[this.other].active);

            String UUID = this.players[this.current].active.getUUID();
            int exp = this.players[this.current].active.getDuelExp(this.players[this.other].active);
            this.expGains.put(UUID, (this.expGains.getOrDefault(UUID, 0)) + exp);

            PokemonStatisticsQuery current = new PokemonStatisticsQuery(this.players[this.current].active.getUUID());
            PokemonStatisticsQuery other = new PokemonStatisticsQuery(this.players[this.other].active.getUUID());

            current.incr(PokemonStatistic.POKEMON_DEFEATED);
            other.incr(PokemonStatistic.TIMES_FAINTED);

            if(this.players[this.current].active.isDynamaxed() && this.players[this.current].active.getDynamaxLevel() < 10 && new Random().nextInt(100) < 40)
            {
                this.players[this.current].active.increaseDynamaxLevel();

                this.event.getChannel().sendMessage(this.players[this.current].data.getMention() + ": " + this.players[this.current].active.getName() + " earned a Dynamax Level!").queue();
            }

            if(this.data(this.other).destinyBondUsed)
            {
                this.data(this.other).destinyBondUsed = false;

                other.incr(PokemonStatistic.POKEMON_DEFEATED);
                current.incr(PokemonStatistic.TIMES_FAINTED);

                this.players[this.current].active.damage(this.players[this.current].active.getHealth());
                turnResult += " " + this.players[this.current].active.getName() + " fainted from the Destiny Bond!";
            }
        }

        this.moveLog.get(this.turn)[this.current] = move.getName();

        return turnResult;
    }

    public void zcrystalEvent(Move move)
    {
        //Z-Crystals can't be earned from 1v1 Wild Pokemon Duels
        if(this instanceof WildDuel) return;

        //AI Trainers can't earn Z Crystals
        if(this instanceof TrainerDuel && this.current == 1) return;

        ZCrystal earnedZ = ZCrystal.getCrystalOfType(move.getType());

        if(!this.players[this.current].data.hasZCrystal(earnedZ.getStyledName()))
        {
            this.players[this.current].data.addZCrystal(earnedZ.getStyledName());

            Achievements.grant(this.players[this.current].ID, Achievements.ACQUIRED_FIRST_TYPED_ZCRYSTAL, this.event);

            this.event.getChannel().sendMessage(this.players[this.current].data.getMention() + ": earned a Z-Crystal – " + earnedZ.getStyledName() + "!").queue();
        }
    }

    //Turn Helper Methods
    public void setDefaults()
    {
        this.weather = Weather.CLEAR;
        this.terrain = Terrain.NORMAL_TERRAIN;
        this.room = Room.NORMAL_ROOM;
        this.entryHazards = new EntryHazardHandler[]{new EntryHazardHandler(), new EntryHazardHandler()};
    }

    public void checkDynamax(int p)
    {
        if(this.players[p].active.isDynamaxed())
        {
            this.players[p].dynamaxTurns--;

            if(this.players[p].active.isFainted())
            {
                this.players[p].dynamaxTurns = 0;
            }
            else if(this.players[p].dynamaxTurns <= 0)
            {
                this.players[p].active.exitDynamax();
                this.results.add(this.players[p].active.getName() + " reverted to its original size!\n");
            }
        }
    }

    public void updateWeatherTerrainRoom()
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

        if(this.roomTurns <= 0)
        {
            this.roomTurns = 0;
            this.room = Room.NORMAL_ROOM;
        }

        if(this.roomTurns > 0) this.roomTurns--;
    }

    public void weatherEffects()
    {
        StringBuilder weatherResult = new StringBuilder().append("\n").append(this.weather.getStatus()).append("\n");

        switch(this.weather)
        {
            case HAIL -> {
                if(this.isAffectedByHail(0))
                {
                    this.players[0].active.damage(this.players[0].active.getStat(Stat.HP) / 16);
                    weatherResult.append(this.players[0].active.getName()).append(" took damage from the hailstorm!\n");
                }
                if(this.isAffectedByHail(1))
                {
                    this.players[1].active.damage(this.players[1].active.getStat(Stat.HP) / 16);
                    weatherResult.append(this.players[1].active.getName()).append(" took damage from the hailstorm!\n");
                }

            }
            case SANDSTORM -> {
                if(this.isAffectedBySandstorm(0))
                {
                    this.players[0].active.damage(this.players[0].active.getStat(Stat.HP) / 16);
                    weatherResult.append(this.players[0].active.getName()).append(" took damage from the sandstorm!\n");
                }
                if(this.isAffectedBySandstorm(1))
                {
                    this.players[1].active.damage(this.players[1].active.getStat(Stat.HP) / 16);
                    weatherResult.append(this.players[1].active.getName()).append(" took damage from the sandstorm!\n");
                }
            }
        }

        this.results.add("\n" + weatherResult.toString());
    }

    private boolean isAffectedByHail(int p)
    {
        return !this.players[p].active.isType(Type.ICE) && !this.data(p).digUsed && !this.data(p).diveUsed && !this.data(p).phantomForceUsed && !this.data(p).shadowForceUsed;
    }

    private boolean isAffectedBySandstorm(int p)
    {
        return !this.players[p].active.isType(Type.GROUND) && !this.players[p].active.isType(Type.ROCK) && !this.players[p].active.isType(Type.STEEL) && !this.data(p).digUsed && !this.data(p).diveUsed && !this.data(p).phantomForceUsed && !this.data(p).shadowForceUsed;
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

                if(!this.data(this.current).isRaised) this.players[this.current].active.heal(this.players[this.current].active.getStat(Stat.HP) / 16);
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
        int statusDamage;

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

        if(pokemon.hasStatusCondition(StatusCondition.BADLY_POISONED))
        {
            statusDamage = (int)(pokemon.getStat(Stat.HP) / 16.) * this.data(p).badlyPoisonedTurns;
            pokemon.damage(statusDamage);

            status.append(name).append(" is badly poisoned! The poison dealt ").append(statusDamage).append(" damage!\n");
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

    public void entryHazardEffects(int player)
    {
        String hazardResults = "";
        String name = this.players[player].active.getName();

        if(this.entryHazards[player].hasHazard(EntryHazard.SPIKES) && !this.data(player).isRaised)
        {
            int damage = (int)(this.players[player].active.getStat(Stat.HP) * switch(this.entryHazards[player].getHazard(EntryHazard.SPIKES)) {
                case 1 -> 1 / 8D;
                case 2 -> 1 / 6D;
                case 3 -> 1 / 4D;
                default -> 0;
            });

            this.players[player].active.damage(damage);

            hazardResults += "Spikes dealt " + damage + " damage to " + name + "!\n";
        }
        else if(this.data(player).isRaised)
        {
            hazardResults += "Spikes does not affect " + name;
        }

        if(this.entryHazards[player].hasHazard(EntryHazard.STEALTH_ROCK))
        {
            double rockEffective = TypeEffectiveness.getCombinedMap(this.players[player].active.getType()[0], this.players[player].active.getType()[1]).get(Type.ROCK);
            double damagePercent = 0.125 * rockEffective;

            int damage = (int)(this.players[player].active.getStat(Stat.HP) * damagePercent);

            this.players[player].active.damage(damage);

            hazardResults += "Stealth Rock dealt " + damage + " damage to " + name + "!\n";
        }

        if(this.entryHazards[player].hasHazard(EntryHazard.STICKY_WEB) && !this.data(player).isRaised)
        {
            this.players[player].active.changeStatMultiplier(Stat.SPD, -1);

            hazardResults += "Sticky Web lowered " + name + "'s Speed by 1 stage!\n";
        }
        else if(this.data(player).isRaised)
        {
            hazardResults += "Sticky Web does not affect " + name;
        }

        if(this.entryHazards[player].hasHazard(EntryHazard.TOXIC_SPIKES) && !this.data(player).isRaised)
        {
            int level = this.entryHazards[player].getHazard(EntryHazard.TOXIC_SPIKES);
            this.players[player].active.addStatusCondition(level == 1 ? StatusCondition.POISONED : StatusCondition.BADLY_POISONED);

            if(level > 1) this.data(player).badlyPoisonedTurns++;

            hazardResults += "Toxic Spikes caused " + name + " to be " + (level == 1 ? "poisoned!" : "badly poisoned!") + "\n";
        }
        else if(this.data(player).isRaised)
        {
            hazardResults += "Toxic Spikes does not affect " + name;
        }

        this.results.add(hazardResults);

        if(this.players[player].active.isFainted()) this.results.add(name + " fainted from the Entry Hazard(s)!");
    }

    public void setDuelPokemonObjects(int player)
    {
        for(Pokemon p : this.players[player].team)
        {
            this.pokemonAttributes.put(p.getUUID(), new DuelPokemon(p.getUUID()));
            this.data(player).isRaised = p.isType(Type.FLYING) || p.getAbilities().contains("Levitate");
        }
    }

    protected void turnSetup()
    {
        this.setStatus(DuelStatus.DUELING);

        this.results = new ArrayList<>();

        this.players[0].move = null;
        this.players[1].move = null;

        this.first = "";

        if(turn == 0)
        {
            this.checkWeatherAbilities();
        }

        this.turn++;

        this.moveLog.put(this.turn, new String[2]);
    }

    private void checkWeatherAbilities()
    {
        int faster = this.players[0].active.getStat(Stat.SPD) > this.players[1].active.getStat(Stat.SPD) ? 0 : 1;
        int slower = faster == 0 ? 1 : 0;

        this.checkWeatherAbilities(faster);
        this.checkWeatherAbilities(slower);
    }

    private void checkWeatherAbilities(int p)
    {
        List<String> abilities = this.players[p].active.getAbilities();

        if(abilities.contains("Drought"))
        {
            this.weather = Weather.HARSH_SUNLIGHT;
            this.weatherTurns = 5;
        }

        if(abilities.contains("Drizzle"))
        {
            this.weather = Weather.RAIN;
            this.weatherTurns = 5;
        }

        if(abilities.contains("Sand Stream"))
        {
            this.weather = Weather.SANDSTORM;
            this.weatherTurns = 5;
        }

        if(abilities.contains("Snow Warning"))
        {
            this.weather = Weather.HAIL;
            this.weatherTurns = 5;
        }
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
            if(this.isComplete())
            {
                this.event.getChannel().sendFile(this.getImage(), "duel.png").setEmbeds(embed.build()).queue();
            }
            else this.event.getChannel()
                    .sendFile(this.getImage(), "duel.png")
                    .setEmbeds(embed.build())
                    .flatMap(m -> m.reply("Turn Action Selection:")
                            .setActionRows(
                                    ActionRow.of(
                                            Button.primary(ButtonListener.DUEL_MOVE_BUTTONS.get(0), "Move 1"),
                                            Button.primary(ButtonListener.DUEL_MOVE_BUTTONS.get(1), "Move 2"),
                                            Button.primary(ButtonListener.DUEL_MOVE_BUTTONS.get(2), "Move 3"),
                                            Button.primary(ButtonListener.DUEL_MOVE_BUTTONS.get(3), "Move 4")
                                    )
                            ))
                    .delay(30, TimeUnit.SECONDS)
                    .flatMap(Message::delete)
                    .queue();
        }
        catch (Exception e)
        {
            this.event.getChannel().sendMessageEmbeds(embed.build()).queue();
            e.printStackTrace();
        }
    }

    public void sendWinEmbed()
    {
        EmbedBuilder embed = new EmbedBuilder();

        int c = this.players[0].team.size() >= 6 ? this.giveWinCredits() : 0;

        embed.setDescription(this.getWinner().data.getUsername() + " has won!" + (c != 0 ? "\nThey earned " + c + " credits!" : ""));

        this.event.getChannel().sendMessageEmbeds(embed.build()).queue();

        Achievements.grant(this.getWinner().ID, Achievements.WON_FIRST_PVP_DUEL, this.event);
        if(this.size == CommandTeam.MAX_TEAM_SIZE) Achievements.grant(this.getWinner().ID, Achievements.WON_FIRST_DUEL_MAX_SIZE, this.event);

        if(new Random().nextInt(100) < 20)
        {
            this.uploadEVs(0);
            this.uploadEVs(1);
        }

        int winner = this.getWinner().ID.equals(this.players[0].ID) ? 0 : 1;
        int loser = winner == 0 ? 1 : 0;

        this.players[winner].data.addPokePassExp(1000, this.event);
        this.players[loser].data.addPokePassExp(500, this.event);

        this.players[winner].data.getStats().incr(PlayerStatistic.PVP_DUELS_WON);

        this.uploadExperience();

        DuelHelper.delete(this.players[0].ID);
    }

    protected void uploadEVs(int player)
    {
        for(Pokemon p : this.players[player].team)
        {
            Pokemon.updateEVs(p);
        }
    }

    protected void uploadExperience()
    {
        Pokemon p;
        for(String uuid : this.expGains.keySet())
        {
            p = Pokemon.buildCore(uuid, -1);
            if(p != null)
            {
                p.addExp(this.expGains.get(uuid));
                Pokemon.updateExperience(p);
            }
        }
    }

    protected int giveWinCredits()
    {
        int winCredits = new Random().nextInt(501) + 500;
        this.getWinner().data.changeCredits(winCredits);
        return winCredits;
    }

    //Useful Getters/Setters

    public void submitMove(String id, int index, char type)
    {
        type = Character.toLowerCase(type);

        //index functions as both the swapIndex and moveIndex, which one is dictated by type == 's' and type == 'm'
        if(type == 's')
        {
            if(this.players[this.indexOf(id)].team.get(index - 1).isFainted())
            {
                this.event.getChannel().sendMessage("That pokemon is fainted!").queue();
                return;
            }

            this.queuedMoves.put(id, new TurnAction(ActionType.SWAP, -1, index));
        }
        else this.queuedMoves.put(id, new TurnAction(type == 'z' ? ActionType.ZMOVE : (type == 'd' ? ActionType.DYNAMAX : ActionType.MOVE), index, -1));
    }

    public void checkReady()
    {
        if(this.queuedMoves.containsKey(this.players[0].ID) && this.queuedMoves.containsKey(this.players[1].ID))
        {
            turnHandler();
        }

        boolean faintSwap1 = this.queuedMoves.containsKey(this.players[0].ID) && this.queuedMoves.get(this.players[0].ID).action().equals(ActionType.SWAP) && this.players[0].active.isFainted();
        boolean faintSwap2 = this.queuedMoves.containsKey(this.players[1].ID) && this.queuedMoves.get(this.players[1].ID).action().equals(ActionType.SWAP) && this.players[1].active.isFainted();

        //TODO: Add a TurnAction IDLE
        if((faintSwap1 || faintSwap2) && !(faintSwap1 && faintSwap2))
        {
            if(faintSwap1) this.submitMove(this.players[1].ID, 1, 'm');
            else this.submitMove(this.players[0].ID, 1, 'm');

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

    public EntryHazardHandler hazardData(String UUID)
    {
        return this.entryHazards[this.players[0].active.getUUID().equals(UUID) ? 0 : 1];
    }

    public Player getWinner()
    {
        return this.players[0].lost() ? this.players[1] : this.players[0];
    }

    public InputStream getImage() throws Exception
    {
        //Background is 800 x 480 -> 400 x 240
        int baseSize = 150;
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
            int size = this.players[0].active.isDynamaxed() ? (int)(baseSize * 1.25) : baseSize;

            Image p1 = ImageIO.read(new URL(this.getPokemonURL(0))).getScaledInstance(size, size, hint);
            combined.getGraphics().drawImage(p1, spacing, y, null);
        }

        if(!this.players[1].active.isFainted())
        {
            int size = this.players[1].active.isDynamaxed() ? (int)(baseSize * 1.25) : baseSize;

            Image p2 = ImageIO.read(new URL(this.getPokemonURL(1))).getScaledInstance(size, size, hint);
            combined.getGraphics().drawImage(p2, (backgroundW - spacing) - size, y, null);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(combined, "png", out);

        byte[] bytes = out.toByteArray(); //This is the slow line

        return new ByteArrayInputStream(bytes);
    }

    private String getPokemonURL(int player)
    {
        Pokemon p = this.players[player].active;
        Move m = this.players[player].move;

        if(p.getName().equals("Marshadow"))
        {
            return !p.isShiny() ? "https://archives.bulbagarden.net/media/upload/thumb/1/11/802Marshadow-Alt.png/600px-802Marshadow-Alt.png" : "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/ddn43ra-94a76142-591e-4f64-917a-3659635d4bff.png";
        }
        else if(m != null && p.getName().equals("Solgaleo") && (m.getName().equals("Sunsteel Strike") || m.getName().equals("Searing Sunraze Smash")))
        {
            return "https://archives.bulbagarden.net/media/upload/thumb/5/58/791Solgaleo-RadiantSunPhase.png/600px-791Solgaleo-RadiantSunPhase.png";
        }
        else if(m != null && p.getName().equals("Lunala") && (m.getName().equals("Moongeist Beam") || m.getName().equals("Menacing Moonraze Maelstrom")))
        {
            return "https://archives.bulbagarden.net/media/upload/thumb/6/64/792Lunala-FullMoonPhase.png/600px-792Lunala-FullMoonPhase.png";
        }
        else if(m != null && p.getName().equals("Eternatus") && m.getName().equals("Eternabeam"))
        {
            return "https://static.wikia.nocookie.net/villains/images/7/76/HOME890E.png/revision/latest/scale-to-width-down/512?cb=20200221025522";
        }
        else return p.getImage();
    }

    private String getHealthBars()
    {
        String healthBarP1 = this.getHB(0);
        String healthBarP2 = this.getHB(1);

        return this.isComplete() ? "" : healthBarP1 + "\n" + healthBarP2;
    }

    protected String getHB(int p)
    {
        StringBuilder sb = new StringBuilder().append(this.players[p].data.getUsername()).append("'s ").append(this.players[p].active.getDisplayName());
        sb.append(this.players[p].active.isDynamaxed() ? (this.players[p].active.canGigantamax() ? " (Gigantamaxed)" : " (Dynamaxed)") : "");

        sb.append(": ");
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

    protected void setEvent(MessageReceivedEvent event)
    {
        this.event = event;
        this.turn = 0;
        this.moveLog = new HashMap<>();
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public int getSize()
    {
        return this.players[0].team.size();
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

    public int playerIndexFromUUID(String UUID)
    {
        return this.players[0].active.getUUID().equals(UUID) ? 0 : 1;
    }

    public boolean hasPlayer(String id)
    {
        return this.players[0].ID.equals(id) || this.players[1].ID.equals(id);
    }
}
