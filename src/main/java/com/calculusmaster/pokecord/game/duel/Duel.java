package com.calculusmaster.pokecord.game.duel;

import com.calculusmaster.pokecord.commands.duel.CommandTarget;
import com.calculusmaster.pokecord.commands.pokemon.CommandTeam;
import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.duel.component.*;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.players.Player;
import com.calculusmaster.pokecord.game.enums.elements.*;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.TypeEffectiveness;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.PokemonRarity;
import com.calculusmaster.pokecord.game.pokemon.component.PokemonDuelAttributes;
import com.calculusmaster.pokecord.game.tournament.Tournament;
import com.calculusmaster.pokecord.game.tournament.TournamentHelper;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.calculusmaster.pokecord.util.listener.ButtonListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.calculusmaster.pokecord.game.duel.core.DuelHelper.*;

//PVP Duel - Infinitely Scalable
public class Duel
{
    protected DuelStatus status;
    protected MessageReceivedEvent event;
    protected int size;
    protected Player[] players;
    protected Map<String, TurnAction> queuedMoves = new HashMap<>();
    protected Map<String, PokemonDuelAttributes> pokemonAttributes = new HashMap<>();
    protected Map<String, Integer> expGains = new HashMap<>();
    protected Map<String, Integer> damageDealt = new HashMap<>();
    protected Map<String, List<String>> movesUsed = new HashMap<>();

    public int turn;
    public int current;
    public int other;

    public String first;

    protected List<String> results;

    //Common Field Effects
    public WeatherHandler weather;
    public TerrainHandler terrain;
    public RoomHandler room;

    //Sided Field Effects
    public EntryHazardHandler[] entryHazards;
    public FieldBarrierHandler[] barriers;
    public FieldGMaxDoTHandler[] gmaxDoT;
    public FieldEffectsHandler[] fieldEffects;

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

        //Both players are using a move
        if(this.isUsingMove(0) && this.isUsingMove(1))
        {
            //Establish moves
            this.moveAction(0);
            this.moveAction(1);

            this.fullMoveTurn();
        }
        else
        {
            this.checkFaintSwap(0);
            this.checkFaintSwap(1);

            if(this.getAction(0).equals(ActionType.SWAP)) this.swapAction(0);
            if(this.getAction(1).equals(ActionType.SWAP)) this.swapAction(1);

            if(this.isUsingMove(0))
            {
                this.moveAction(0);
                this.moveLogic(0);
            }
            else if(this.isUsingMove(1))
            {
                this.moveAction(1);
                this.moveLogic(1);
            }
        }

        this.onTurnEnd();
    }

    protected void onTurnEnd()
    {
        this.updateWeatherTerrainRoom();

        this.weatherEffects();

        this.checkDynamax(0);
        this.checkDynamax(1);

        this.barriers[0].updateTurns();
        this.barriers[1].updateTurns();

        if(this.isComplete())
        {
            if(this.event == null) LoggerHelper.error(Duel.class, "Duel Error: Cannot send Embeds, MessageReceivedEvent not initialized!");
            else
            {
                this.onWin();

                this.sendTurnEmbed();
                this.sendWinEmbed();
                this.setStatus(DuelStatus.COMPLETE);
            }
        }
        else
        {
            if(this.event == null) LoggerHelper.error(Duel.class, "Duel Error: Cannot send Embeds, MessageReceivedEvent not initialized!");
            else this.sendTurnEmbed();
        }

        this.queuedMoves.clear();
    }

    protected void checkFaintSwap(int p)
    {
        int o = p == 0 ? 1 : 0;
        if(this.getAction(p).equals(ActionType.SWAP) && this.players[p].active.isFainted() && !this.players[0].active.isFainted()) this.queuedMoves.put(this.players[o].ID, new TurnAction(ActionType.IDLE, -1, -1));
    }

    protected boolean isUsingMove(int p)
    {
        return Arrays.asList(ActionType.MOVE, ActionType.ZMOVE, ActionType.DYNAMAX).contains(this.getAction(p));
    }

    protected void fullMoveTurn()
    {
        //Set who goes first (set this.current and this.other)
        this.setMoveOrder();

        //Move Logic - Player 1 (index 0)
        this.moveLogic(this.current);

        //Switch players
        this.current = this.current == 0 ? 1 : 0;
        this.other = this.current == 0 ? 1 : 0;

        //Move Logic - Player 2 (index 1)
        this.moveLogic(this.current);
    }

    protected void moveLogic(int p)
    {
        this.current = p;
        if(!this.players[p].active.isFainted())
        {
            results.add(this.turn(this.players[p].move));
        }
        else results.add("\n" + this.players[p].active.getName() + " fainted!");
    }

    private void setMoveOrder()
    {
        int speed1 = this.players[0].active.getStat(Stat.SPD);
        int speed2 = this.players[1].active.getStat(Stat.SPD);

        //Grassy Terrain & Grassy Glide Effects
        if(this.terrain.get().equals(Terrain.GRASSY_TERRAIN) && this.players[0].move.getName().equals("Grassy Glide"))
            this.players[0].move.setPriority(1);
        if(this.terrain.get().equals(Terrain.GRASSY_TERRAIN) && this.players[1].move.getName().equals("Grassy Glide"))
            this.players[1].move.setPriority(1);

        //Ability: Gale Wings
        if(this.players[0].active.hasAbility(Ability.GALE_WINGS) && this.players[0].move.is(Type.FLYING))
            this.players[0].move.setPriority(this.players[0].move.getPriority() + 1);
        if(this.players[1].active.hasAbility(Ability.GALE_WINGS) && this.players[1].move.is(Type.FLYING))
            this.players[1].move.setPriority(this.players[1].move.getPriority() + 1);

        if(this.players[0].move.getPriority() == this.players[1].move.getPriority())
        {
            this.current = speed1 == speed2 ? (new Random().nextInt(100) < 50 ? 0 : 1) : (speed1 > speed2 ? 0 : 1);

            if(this.room.isActive(Room.TRICK_ROOM))
                this.current = this.current == 0 ? 1 : 0;

            if(this.players[this.other].active.hasItem(Item.CUSTAP_BERRY) && this.players[this.other].active.getHealth() < this.players[this.other].active.getMaxHealth(1 / 4.))
            {
                this.players[this.other].active.removeItem();
                this.results.add(this.players[this.other].active.getName() + " consumed its Custap Berry and will attack first!");

                this.current = this.other;
            }
        }
        else
        {
            this.current = this.players[0].move.getPriority() > this.players[1].move.getPriority() ? 0 : 1;
        }

        this.other = this.current == 0 ? 1 : 0;

        this.first = this.players[this.current].active.getUUID();
    }

    protected void moveAction(int p)
    {
        if(!this.isUsingMove(p)) return;

        //Basic Move
        Move move = new Move(this.players[p].active.getMoves().get(this.queuedMoves.get(this.players[p].ID).moveInd() - 1));

        //Z Move
        if(this.getAction(p).equals(ActionType.ZMOVE)) move = DuelHelper.getZMove(this.players[p], move);

        //Dynamax - Request (If the player is entering Dynamax this turn)
        if(this.getAction(p).equals(ActionType.DYNAMAX))
        {
            this.players[p].active.enterDynamax();
            this.players[p].dynamaxTurns = 3;
            this.players[p].usedDynamax = true;
            this.results.add(this.players[p].active.getName() + " dynamaxed!\n");
        }

        //Max Move
        if(this.players[p].active.isDynamaxed()) move = DuelHelper.getMaxMove(this.players[p].active, move);

        this.players[p].move = move;
    }

    private void swapAction(int p)
    {
        //If current has a primal weather, the weather gets removed
        if(this.players[p].active.hasAbility(Ability.DESOLATE_LAND, Ability.PRIMORDIAL_SEA, Ability.DELTA_STREAM))
        {
            BiFunction<Ability, Weather, Boolean> check =
                    (a, w) -> this.players[p].active.hasAbility(a) && this.weather.get().equals(w);

            if(check.apply(Ability.DESOLATE_LAND, Weather.EXTREME_HARSH_SUNLIGHT) ||
                    check.apply(Ability.PRIMORDIAL_SEA, Weather.HEAVY_RAIN) ||
                    check.apply(Ability.DELTA_STREAM, Weather.STRONG_WINDS))
            {
                this.results.add(this.weather.get().getName() + " disappeared!\n\n");

                this.weather.removeWeather();
            }
        }

        //Swap Action
        int index = this.queuedMoves.get(this.players[p].ID).swapInd() - 1;
        this.data(p).setDefaults();
        this.players[p].swap(index);

        results.add(this.players[p].data.getUsername() + " brought in " + this.players[p].active.getName() + "!\n");

        //Check if the new Pokemon has a Weather-Causing Ability
        this.checkWeatherAbilities(p);

        //Check for Entry Hazard Effects
        this.entryHazardEffects(p);

        //Ability: Dauntless Shield
        if(this.players[p].active.hasAbility(Ability.DAUNTLESS_SHIELD))
        {
            this.players[p].active.changes().change(Stat.DEF, 1);
            this.results.add(Ability.DAUNTLESS_SHIELD.formatActivation(this.players[p].active.getName(), this.players[p].active.getName() + "'s Defense rose by 1 stage!"));
        }

        if(this.isNonBotPlayer(p)) this.players[p].data.updateBountyProgression(ObjectiveType.SWAP_POKEMON);
    }

    //Always use this.current!
    public String turn(Move move)
    {
        List<String> turnResult = new ArrayList<>();

        //Setup
        Pokemon c = this.players[this.current].active;
        Pokemon o = this.players[this.other].active;

        //Ability: Multitype
        if(this.players[this.current].active.hasAbility(Ability.MULTITYPE))
        {
            Item item = this.players[this.current].active.getItem();

            if(item.isPlateItem())
            {
                Type t = item.getArceusPlateType();

                this.players[this.current].active.setType(t);

                if(move.getName().equals("Judgement")) move.setType(t);
            }
        }

        //Plasma Fists Type Change: Normal -> Electric
        if(this.data(this.current).plasmaFistsUsed && move.getType().equals(Type.NORMAL)) move.setType(Type.ELECTRIC);

        //Ability: Pixilate
        if(c.hasAbility(Ability.PIXILATE) && move.is(Type.NORMAL))
        {
            move.setType(Type.FAIRY);
            move.setPower(1.2);
        }

        //Electrify Type Change: Normal -> Electric (If Opponent goes after User)
        if(this.players[this.other].active.getUUID().equals(this.first) && this.data(this.other).electrifyUsed)
        {
            move.setType(Type.ELECTRIC);
            this.data(this.other).electrifyUsed = false;
        }

        //Terrain Pulse Type Change
        if(move.getName().equals("Terrain Pulse"))
        {
            move.setType(switch(this.terrain.get()) {
                case NORMAL_TERRAIN -> Type.NORMAL;
                case GRASSY_TERRAIN -> Type.GRASS;
                case MISTY_TERRAIN -> Type.FAIRY;
                case ELECRIC_TERRAIN -> Type.ELECTRIC;
                case PSYCHIC_TERRAIN -> Type.PSYCHIC;
            });

            if(!this.terrain.get().equals(Terrain.NORMAL_TERRAIN) && !this.data(this.current).isRaised) move.setPower(2.0);
        }

        //Weather Ball
        if(move.getName().equals("Weather Ball"))
        {
            move.setType(switch(this.weather.get()) {
                case RAIN, HEAVY_RAIN -> Type.WATER;
                case HARSH_SUNLIGHT, EXTREME_HARSH_SUNLIGHT -> Type.FIRE;
                case SANDSTORM -> Type.ROCK;
                case HAIL -> Type.ICE;
                default -> Type.NORMAL;
            });

            if(!this.weather.get().equals(Weather.CLEAR)) move.setPower(2.0);
        }

        //Weather-based Move Changes

        switch(this.weather.get())
        {
            case HAIL -> {
                if(move.getName().equals("Blizzard")) move.setAccuracy(100);

                if(move.getName().equals("Solar Beam") || move.getName().equals("Solar Blade")) move.setPower(move.getPower() / 2);
            }
            case HARSH_SUNLIGHT, EXTREME_HARSH_SUNLIGHT -> {
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

        //Terrain-based Move Changes

        switch(this.terrain.get())
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

        //If the pokemon uses an unfreeze remove, remove the Frozen Status Condition
        if(this.players[this.current].active.hasStatusCondition(StatusCondition.FROZEN) && Arrays.asList("Fusion Flare", "Flame Wheel", "Sacred Fire", "Flare Blitz", "Scald", "Steam Eruption").contains(move.getName())) this.players[this.current].active.removeStatusCondition(StatusCondition.FROZEN);

        //Unfreeze opponent if this move is a Fire Type, Scald or Steam Eruption
        if(move.getType().equals(Type.FIRE) || move.getName().equals("Scald") || move.getName().equals("Steam Eruption")) this.players[this.other].active.removeStatusCondition(StatusCondition.FROZEN);

        //Check Status Conditions
        if(this.players[this.current].active.hasAnyStatusCondition())
        {
            List<String> statusResults = new ArrayList<>();
            SplittableRandom r = new SplittableRandom();
            int statusDamage;

            BiFunction<List<String>, List<String>, String> compileResults = (turn, status) -> {
                this.results.add(String.join(" ", turn));
                this.results.add(String.join(" ", status));
                this.results.add("\n\n");
                return "";
            };

            //Check Berries before Status Conditions

            if(!this.room.isActive(Room.MAGIC_ROOM))
            {
                Consumer<StatusCondition> removeStatusAndConsume = s -> {
                    c.removeStatusCondition(s);
                    c.removeItem();
                };

                if(c.hasItem(Item.ASPEAR_BERRY) && c.hasStatusCondition(StatusCondition.FROZEN))
                {
                    removeStatusAndConsume.accept(StatusCondition.FROZEN);

                    turnResult.add(c.getName() + " consumed its Aspear Berry and thawed out!");
                }

                if(c.hasItem(Item.CHERI_BERRY) && c.hasStatusCondition(StatusCondition.PARALYZED))
                {
                    removeStatusAndConsume.accept(StatusCondition.PARALYZED);

                    turnResult.add(c.getName() + " consumed its Cheri Berry and was cured from paralysis!");
                }

                if(c.hasItem(Item.CHESTO_BERRY) && c.hasStatusCondition(StatusCondition.ASLEEP))
                {
                    removeStatusAndConsume.accept(StatusCondition.ASLEEP);

                    turnResult.add(c.getName() + " consumed its Chesto Berry and woke up!");
                }

                if(c.hasItem(Item.LUM_BERRY) && c.getStatusConditions().stream().anyMatch(s -> s.isNonVolatile() || s.equals(StatusCondition.CONFUSED)))
                {
                    c.removeItem();
                    Arrays.stream(StatusCondition.values()).filter(s -> s.isNonVolatile() || s.equals(StatusCondition.CONFUSED)).forEach(c::removeStatusCondition);

                    turnResult.add(c.getName() + " consumed its Lum Berry and was cured of all non-volatile Status Conditions!");
                }

                if(c.hasItem(Item.PECHA_BERRY) && (c.hasStatusCondition(StatusCondition.POISONED) || c.hasStatusCondition(StatusCondition.BADLY_POISONED)))
                {
                    removeStatusAndConsume.accept(StatusCondition.POISONED);
                    c.removeStatusCondition(StatusCondition.BADLY_POISONED);

                    turnResult.add(c.getName() + " consumed its Pecha Berry and was cured of its poison!");
                }

                if(c.hasItem(Item.PERSIM_BERRY) && c.hasStatusCondition(StatusCondition.CONFUSED))
                {
                    removeStatusAndConsume.accept(StatusCondition.CONFUSED);

                    turnResult.add(c.getName() + " consumed its Persim Berry and snapped out of its confusion!");
                }

                if(c.hasItem(Item.RAWST_BERRY) && c.hasStatusCondition(StatusCondition.BURNED))
                {
                    removeStatusAndConsume.accept(StatusCondition.BURNED);

                    turnResult.add(c.getName() + " consumed its Rawst Berry and its burn was healed!");
                }
            }

            //Abilities that Cure Status Conditions
            if(c.hasAbility(Ability.PASTEL_VEIL))
            {
                c.removeStatusCondition(StatusCondition.POISONED);
                c.removeStatusCondition(StatusCondition.BADLY_POISONED);

                this.players[this.current].team.forEach(p -> {
                    p.removeStatusCondition(StatusCondition.POISONED);
                    p.removeStatusCondition(StatusCondition.BADLY_POISONED);
                });

                statusResults.add(Ability.PASTEL_VEIL.formatActivation(c.getName(), "%s and its team's poison was cured!".formatted(c.getName())));
            }

            if(c.hasAbility(Ability.LIMBER))
            {
                c.removeStatusCondition(StatusCondition.PARALYZED);

                statusResults.add(Ability.LIMBER.formatActivation(c.getName(), "%s's paralysis was cured!".formatted(c.getName())));
            }

            //Damage Status Conditions
            if(c.hasStatusCondition(StatusCondition.BURNED))
            {
                statusDamage = c.getMaxHealth(1 / 16.);
                c.damage(statusDamage);

                statusResults.add("%s is burned! The burn dealt %s damage!".formatted(c.getName(), statusDamage));
            }

            if(c.hasStatusCondition(StatusCondition.POISONED))
            {
                statusDamage = c.getMaxHealth(1 / 8.);
                c.damage(statusDamage);

                statusResults.add("%s is poisoned! The poison dealt %s damage!".formatted(c.getName(), statusDamage));
            }

            if(c.hasStatusCondition(StatusCondition.BADLY_POISONED))
            {
                statusDamage = c.getMaxHealth(1 / 16.) * this.data(this.current).badlyPoisonedTurns;
                c.damage(statusDamage);

                statusResults.add("%s is badly poisoned! The poison dealt %s damage!".formatted(c.getName(), statusDamage));
            }

            if(c.hasStatusCondition(StatusCondition.CURSED))
            {
                statusDamage = c.getMaxHealth(1 / 4.);
                c.damage(statusDamage);

                statusResults.add("%s is cursed! The curse dealt %s damage!".formatted(c.getName(), statusDamage));
            }

            if(c.hasStatusCondition(StatusCondition.BOUND))
            {
                this.data(this.current).boundTurns++;

                if(this.data(this.current).boundTurns == 5)
                {
                    this.data(this.current).boundTurns = 0;

                    statusResults.add(c.getName() + " is no longer bound!");
                }
                else
                {
                    statusDamage = c.getMaxHealth(1 / 8.);
                    c.damage(statusDamage);

                    statusResults.add("%s is bound! The binding dealt %s damage!".formatted(c.getName(), statusDamage));
                }
            }

            //Termination-Capable Status Conditions

            if(c.hasStatusCondition(StatusCondition.CONFUSED))
            {
                if(r.nextInt(100) < 33)
                {
                    statusDamage = new Move("Tackle").getDamage(c, c);
                    c.damage(statusDamage);

                    statusResults.add("%s is confused! %s hurt itself in its confusion for %s damage!".formatted(c.getName(), c.getName(), statusDamage));

                    return compileResults.apply(turnResult, statusResults);
                }
                else if(r.nextInt(100) < 50) c.removeStatusCondition(StatusCondition.CONFUSED);
            }

            if(c.hasStatusCondition(StatusCondition.FROZEN))
            {
                boolean sun = this.weather.get().equals(Weather.HARSH_SUNLIGHT) || this.weather.get().equals(Weather.EXTREME_HARSH_SUNLIGHT);

                if(r.nextInt(100) < 20 || sun)
                {
                    c.removeStatusCondition(StatusCondition.FROZEN);

                    statusResults.add("%s has thawed out%s!".formatted(c.getName(), sun ? " due to the Harsh Sunlight" : ""));
                }
                else
                {
                    statusResults.add("%s is frozen and cannot move!");
                    return compileResults.apply(turnResult, statusResults);
                }
            }

            if(c.hasStatusCondition(StatusCondition.ASLEEP))
            {
                if(this.data(this.current).asleepTurns == 2 || this.terrain.get().equals(Terrain.ELECRIC_TERRAIN))
                {
                    c.removeStatusCondition(StatusCondition.ASLEEP);
                    this.data(this.current).asleepTurns = 0;

                    statusResults.add("%s woke up%s!".formatted(c.getName(), this.terrain.get().equals(Terrain.ELECRIC_TERRAIN) ? " due to the Electric Terrain" : ""));

                    if(c.hasStatusCondition(StatusCondition.NIGHTMARE))
                    {
                        c.removeStatusCondition(StatusCondition.NIGHTMARE);
                        statusResults.add("%s's nightmare has ended!".formatted(c.getName()));
                    }
                }
                else
                {
                    this.data(this.current).asleepTurns++;

                    if(c.hasStatusCondition(StatusCondition.NIGHTMARE))
                    {
                        statusDamage = c.getMaxHealth(1 / 4.);
                        c.damage(statusDamage);

                        statusResults.add("%s is in a nightmare! The nightmare dealt %s damage!".formatted(c.getName(), statusDamage));
                    }

                    statusResults.add("%s is asleep!".formatted(c.getName()));

                    if(!move.getName().equals("Snore"))
                    {
                        return compileResults.apply(turnResult, statusResults);
                    }
                }
            }

            if(c.hasStatusCondition(StatusCondition.INFATUATED))
            {
                if(r.nextInt(100) < 50)
                {
                    statusResults.add("%s is infatuated and will not attack!".formatted(c.getName()));

                    return compileResults.apply(turnResult, statusResults);
                }
            }

            if(c.hasStatusCondition(StatusCondition.PARALYZED))
            {
                if(r.nextInt(100) < 25)
                {
                    statusResults.add("%s is paralyzed!".formatted(c.getName()));

                    return compileResults.apply(turnResult, statusResults);
                }
            }

            if(c.hasStatusCondition(StatusCondition.FLINCHED))
            {
                c.removeStatusCondition(StatusCondition.FLINCHED);

                statusResults.add("%s flinched and cannot move!".formatted(c.getName()));

                return compileResults.apply(turnResult, statusResults);
            }

            //Add a space between Status Results and the rest of the turn
            if(!statusResults.isEmpty()) statusResults.add("\n\n");

            this.results.add(String.join(" ", statusResults));
        }

        //Re-Check Certain Status Conditions
        if(this.players[this.current].active.hasStatusCondition(StatusCondition.ASLEEP))
        {
            return this.players[this.current].active.getName() + " is asleep!";
        }

        if(this.players[this.current].active.hasStatusCondition(StatusCondition.PARALYZED))
        {
            if(new Random().nextInt(100) < 20) return this.players[this.current].active.getName() + " is paralyzed!";
        }

        if(this.players[this.current].active.hasStatusCondition(StatusCondition.INFATUATED))
        {
            if(new Random().nextInt(100) < 50) return this.players[this.current].active.getName() + " is infatuated and will not attack!";
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

        if(this.data(this.current).futureSightUsed)
        {
            this.data(this.current).futureSightTurns--;

            if(this.data(this.current).futureSightTurns <= 0)
            {
                this.data(this.current).futureSightTurns = 0;
                this.data(this.current).futureSightUsed = false;

                int damage = new Move("Future Sight").getDamage(this.players[this.current].active, this.players[this.other].active);
                this.players[this.other].active.damage(damage);

                turnResult.add("Future Sight landed and dealt **" + damage + "** damage to " + this.players[this.other].active.getName() + "!");
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

                turnResult.add("Doom Desire landed and dealt **" + damage + "** damage to " + this.players[this.other].active.getName() + "!");
            }
        }

        //Pre-Move Checks
        boolean accurate = move.isAccurate(this.players[this.current].active, this.players[this.other].active);
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

        if(this.data(this.other).imprisonUsed && this.players[this.other].active.getMoves().contains(move.getName())) cantUse = true;

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
            this.players[this.current].active.changes().change(Stat.ATK, 1);

            turnResult.add(this.players[this.current].active.getName() + "'s Attack rose by 1 stage due to Rage!");
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

        if(this.data(this.other).waterSportUsed && move.getType().equals(Type.FIRE)) move.setPower(0.5);

        if(move.getName().equals("Sheer Cold"))
        {
            move.setAccuracy((this.players[this.current].active.isType(Type.ICE) ? 30 : 20) + (this.players[this.current].active.getLevel() - this.players[this.other].active.getLevel()));
            accurate = move.isAccurate(this.players[this.current].active, this.players[this.other].active);
        }

        if(move.getName().equals("Fissure"))
        {
            move.setAccuracy(20 + (this.players[this.current].active.getLevel() - this.players[this.other].active.getLevel()));
            accurate = move.isAccurate(this.players[this.current].active, this.players[this.other].active);
        }

        if(move.getName().equals("Horn Drill"))
        {
            move.setAccuracy(30 + (this.players[this.current].active.getLevel() - this.players[this.other].active.getLevel()));
            accurate = move.isAccurate(this.players[this.current].active, this.players[this.other].active);
        }

        if(move.getName().equals("Guillotine"))
        {
            move.setAccuracy(30 + (this.players[this.current].active.getLevel() - this.players[this.other].active.getLevel()));
            accurate = move.isAccurate(this.players[this.current].active, this.players[this.other].active);
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

        if(this.data(this.current).mistTurns > 0)
        {
            this.data(this.current).mistTurns--;

            if(this.data(this.current).mistTurns <= 0)
            {
                this.data(this.current).mistTurns = 0;
            }
        }

        if(this.data(this.current).wishUsed)
        {
            this.data(this.current).wishUsed = false;

            int heal = this.players[this.current].active.getStat(Stat.HP) / 2;
            this.players[this.current].active.heal(heal);

            turnResult.add(this.players[this.current].active.getName() + "'s wish was granted! It healed " + heal + " HP!");
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
                this.players[this.current].active.changes().change(Stat.ATK, -2);
                otherImmune = !bypass;

                turnResult.add(this.players[this.current].active.getName() + "'s Attack was lowered by 2 stages due to the King's Shield!");
            }
        }

        if(this.data(this.other).banefulBunkerUsed)
        {
            this.data(this.other).banefulBunkerUsed = false;

            if(!move.getCategory().equals(Category.STATUS))
            {
                this.players[this.current].active.addStatusCondition(StatusCondition.POISONED);
                otherImmune = !bypass;

                turnResult.add(this.players[this.current].active.getName() + " was poisoned due to the Baneful Bunker!");
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

                turnResult.add(this.players[this.current].active.getName() + " took " + damage + " damage due to the Spiky Shield!");
            }
        }

        if(this.data(this.other).quickGuardUsed)
        {
            this.data(this.other).quickGuardUsed = false;

            if(move.getPriority() > 0) otherImmune = !bypass;
        }

        if(this.data(this.other).craftyShieldUsed)
        {
            this.data(this.other).craftyShieldUsed = false;

            List<String> bypassStatusMoves = Arrays.asList("Perish Song", "Spikes", "Stealth Rock", "Toxic Spikes", "Sticky Web");
            if(move.getCategory().equals(Category.STATUS) && !bypassStatusMoves.contains(move.getName())) otherImmune = !bypass;
        }

        if(move.getName().equals("Fusion Bolt") && !this.first.equals(this.players[this.current].active.getUUID()) && this.players[this.other].move != null && this.players[this.other].move.getName().equals("Fusion Flare")) move.setPower(move.getPower() * 2);

        if(move.getName().equals("Fusion Flare") && !this.first.equals(this.players[this.current].active.getUUID()) && this.players[this.other].move != null && this.players[this.other].move.getName().equals("Fusion Bolt")) move.setPower(move.getPower() * 2);

        if(this.data(this.other).bideTurns <= 0 && this.data(this.other).bideDamage != 0)
        {
            this.players[this.current].active.damage(this.data(this.other).bideDamage);

            turnResult.add(this.players[this.other].active.getName() + " unleashed stored energy and dealt " + this.data(this.other).bideDamage + " damage!");

            this.data(this.other).bideTurns = 0;
            this.data(this.other).bideDamage = 0;
        }

        if(this.data(this.current).focusEnergyUsed) move.critChance *= 3;

        if(this.data(this.current).laserFocusUsed) move.critChance = 24;

        if(this.data(this.current).mudSportUsed || this.data(this.other).mudSportUsed)
        {
            if(move.getType().equals(Type.ELECTRIC)) move.setPower(move.getPower() * 0.5);
        }

        if(move.getName().equals("Smart Strike"))
        {
            accurate = true;
            otherImmune = false;
        }

        if(this.data(this.other).isTarShotTarget && move.getType().equals(Type.FIRE)) move.setPower(2.0);

        if(this.data(this.current).isOctolocked)
        {
            this.players[this.current].active.changes().change(Stat.DEF, -1);
            this.players[this.current].active.changes().change(Stat.SPDEF, -1);

            this.results.add(this.players[this.current].active.getName() + " is locked! It's Defense and Special Defense were lowered.");
        }

        if(this.data(this.current).unableToUseSoundMoves)
        {
            this.data(this.current).unableToUseSoundMovesTurns--;

            List<String> soundMoves = List.of("Boomburst", "Bug Buzz", "Chatter", "Clanging Scales", "Clangorous Soul", "Clangorous Soulblaze", "Confide", "Disarming Voice", "Echoed Voice", "Eerie Spell", "Grass Whistle", "Growl", "Heal Bell", "Howl", "Hyper Voice", "Metal Sound", "Noble Roar", "Overdrive", "Parting Shot", "Perish Song", "Relic Song", "Roar", "Round", "Screech", "Shadow Panic", "Sing", "Snarl", "Snore", "Sparkling Aria", "Supersonic", "Uproar");

            if(soundMoves.contains(move.getName())) cantUse = true;

            if(this.data(this.current).unableToUseSoundMovesTurns <= 0)
            {
                this.data(this.current).unableToUseSoundMoves = false;
                this.data(this.current).unableToUseSoundMovesTurns = 0;

                this.results.add(this.players[this.current].active.getName() + " is able to use Sound-based Moves again.");
            }
        }

        if(move.is(Type.FIRE) && this.data(this.current).isCoveredPowder)
        {
            this.data(this.current).isCoveredPowder = false;

            int damage = this.players[this.current].active.getMaxHealth() / 4;
            this.players[this.current].active.damage(damage);

            this.results.add(this.players[this.current].active.getName() + " took " + damage + " damage from its Powder covering!");

            cantUse = true;
        }

        //G-Max Damage Over Time
        if(this.gmaxDoT[this.current].exists())
        {
            this.gmaxDoT[this.current].updateTurns();

            if(this.gmaxDoT[this.current].applies(this.players[this.current].active))
            {
                int damage = this.players[this.current].active.getMaxHealth(1 / 6.);
                this.players[this.current].active.damage(damage);

                turnResult.add(this.players[this.current].active.getName() + " took " + damage + " damage from the G-Max " + this.gmaxDoT[this.current].getEffectName() + "!");
            }
        }

        if(this.data(this.current).ingrainUsed)
        {
            int amount = this.players[this.current].active.getMaxHealth(1 / 16.);
            this.players[this.current].active.heal(amount);

            turnResult.add(this.players[this.current].active.getName() + " healed for " + amount + " HP due to its roots!");
        }

        if(this.data(this.current).aquaRingUsed)
        {
            int amount = this.players[this.current].active.getMaxHealth(1 / 16.);
            this.players[this.current].active.heal(amount);

            turnResult.add(this.players[this.current].active.getName() + " healed for " + amount + " HP due to its Aqua Ring!");
        }

        List<String> minimizeBoostMoves = List.of("Body Slam", "Stomp", "Dragon Rush", "Steamroller", "Heat Crash", "Heavy Slam", "Flying Press", "Malicious Moonsault", "Double Iron Bash");
        if(this.data(this.other).isMinimized && minimizeBoostMoves.contains(move.getName())) move.setPower(2.0);

        //Fly, Bounce, Dig and Dive

        if(this.data(this.current).flyUsed) move = new Move("Fly");

        if(this.data(this.current).bounceUsed) move = new Move("Bounce");

        if(this.data(this.current).digUsed) move = new Move("Dig");

        if(this.data(this.current).diveUsed) move = new Move("Dive");

        if(this.data(this.current).phantomForceUsed) move = new Move("Phantom Force");

        if(this.data(this.current).shadowForceUsed) move = new Move("Shadow Force");

        List<String> flyMoves = List.of("Gust", "Twister", "Thunder", "Sky Uppercut", "Smack Down");
        List<String> digMoves = List.of("Earthquake", "Magnitude", "Fissure");
        List<String> diveMoves = List.of("Surf", "Whirlpool", "Low Kick");

        if((this.data(this.other).flyUsed && !flyMoves.contains(move.getName())) || (this.data(this.other).bounceUsed && !flyMoves.contains(move.getName())) || (this.data(this.other).digUsed && !digMoves.contains(move.getName())) || (this.data(this.other).diveUsed && !diveMoves.contains(move.getName())) || this.data(this.other).phantomForceUsed || this.data(this.other).shadowForceUsed)
        {
            otherImmune = true;
        }

        //Two-Turn Charge Moves

        if(this.data(this.current).meteorBeamUsed) move = new Move("Meteor Beam");

        if(this.data(this.current).solarBeamUsed) move = new Move("Solar Beam");

        //Lowered Accuracy of Defensive Moves
        List<String> defensiveMoves = Arrays.asList("Endure", "Protect", "Detect", "Wide Guard", "Quick Guard", "Spiky Shield", "Kings Shield", "Baneful Bunker");

        if(defensiveMoves.contains(move.getName()) && !move.getName().equals("Quick Guard"))
        {
            List<String> log = this.movesUsed.get(this.players[this.current].active.getUUID());

            int i = this.turn - 1;
            while((i > 0 && i < log.size()) && log.get(i).equals(move.getName()) && !log.get(i).equals("Quick Guard"))
            {
                move.setAccuracy(move.getAccuracy() / 3);
                i--;
            }
        }

        //Ability: Dragon's Maw
        if(c.hasAbility(Ability.DRAGONS_MAW) && move.is(Type.DRAGON))
        {
            move.setDamageMultiplier(1.5);

            turnResult.add(Ability.DRAGONS_MAW.formatActivation(c.getName(), move.getName() + "'s power was boosted by 50%!"));
        }

        //Ability: Transistor
        if(c.hasAbility(Ability.TRANSISTOR) && move.is(Type.ELECTRIC))
        {
            move.setDamageMultiplier(1.5);

            turnResult.add(Ability.TRANSISTOR.formatActivation(c.getName(), move.getName() + "'s power was boosted by 50%!"));
        }

        //Ability: Steely Spirit
        if(c.hasAbility(Ability.STEELY_SPIRIT) && move.is(Type.STEEL))
        {
            move.setDamageMultiplier(1.5);

            turnResult.add(Ability.STEELY_SPIRIT.formatActivation(c.getName(), move.getName() + "'s power was boosted by 50%!"));
        }

        //Item-based Buffs

        boolean itemsOff = this.room.isActive(Room.MAGIC_ROOM);

        if(!itemsOff && this.players[this.current].active.getItem().equals(Item.METAL_COAT))
        {
            if(move.getType().equals(Type.STEEL)) move.setPower((int)(move.getPower() * 1.2));
        }

        if(!itemsOff && this.players[this.current].active.getItem().isPlateItem())
        {
            Item item = this.players[this.current].active.getItem();

            boolean buff = item.getArceusPlateType() != null && item.getArceusPlateType().equals(move.getType());

            if(buff) move.setPower(move.getPower() * 1.2);
        }

        //Berries – Post-Move Healing Effects

        MoveEffectBuilder builder = MoveEffectBuilder.make(this.players[this.current].active, this.players[this.other].active, this, move);
        Consumer<String> consumeBerry = berryName -> {
            this.players[this.current].active.removeItem();
            turnResult.add(this.players[this.current].active.getName() + " consumed their " + berryName +  " Berry!");
        };

        if(!itemsOff && c.hasItem(Item.AGUAV_BERRY) && c.getHealth() < c.getMaxHealth(1 / 4.))
        {
            consumeBerry.accept("Aguav");

            turnResult.add(builder
                    .addFractionHealEffect(1 / 2.)
                    .addConditionalEffect(List.of(Nature.NAUGHTY, Nature.RASH, Nature.NAIVE, Nature.LAX).contains(c.getNature()), b -> b.addStatusEffect(StatusCondition.CONFUSED, 100, true))
                    .execute()
            );
        }

        if(!itemsOff && c.hasItem(Item.FIGY_BERRY) && c.getHealth() < c.getMaxHealth(1 / 4.))
        {
            consumeBerry.accept("Figy");

            turnResult.add(builder
                    .addFractionHealEffect(1 / 3.)
                    .addConditionalEffect(List.of(Nature.MODEST, Nature.TIMID, Nature.CALM, Nature.BOLD).contains(c.getNature()), b -> b.addStatusEffect(StatusCondition.CONFUSED, 100, true))
                    .execute()
            );
        }

        if(!itemsOff && c.hasItem(Item.IAPAPA_BERRY) && c.getHealth() < c.getMaxHealth(1 / 4.))
        {
            consumeBerry.accept("Iapapa");

            turnResult.add(builder
                    .addFractionHealEffect(1 / 3.)
                    .addConditionalEffect(List.of(Nature.LONELY, Nature.MILD, Nature.GENTLE, Nature.HASTY).contains(c.getNature()), b -> b.addStatusEffect(StatusCondition.CONFUSED, 100, true))
                    .execute()
            );
        }

        if(!itemsOff && c.hasItem(Item.MAGO_BERRY) && c.getHealth() < c.getMaxHealth(1 / 4.))
        {
            consumeBerry.accept("Mago");

            turnResult.add(builder
                    .addFractionHealEffect(1 / 3.)
                    .addConditionalEffect(List.of(Nature.BRAVE, Nature.QUIET, Nature.SASSY, Nature.RELAXED).contains(c.getNature()), b -> b.addStatusEffect(StatusCondition.CONFUSED, 100, true))
                    .execute()
            );
        }

        if(!itemsOff && c.hasItem(Item.WIKI_BERRY) && c.getHealth() < c.getMaxHealth(1 / 4.))
        {
            consumeBerry.accept("Wiki");

            turnResult.add(builder
                    .addFractionHealEffect(1 / 3.)
                    .addConditionalEffect(List.of(Nature.ADAMANT, Nature.JOLLY, Nature.CAREFUL, Nature.IMPISH).contains(c.getNature()), b -> b.addStatusEffect(StatusCondition.CONFUSED, 100, true))
                    .execute()
            );
        }

        if(!itemsOff && c.hasItem(Item.APICOT_BERRY) && c.getHealth() < c.getMaxHealth(1 / 4.))
        {
            consumeBerry.accept("Apicot");

            turnResult.add(builder.addStatChangeEffect(Stat.SPDEF, 1, 100, true).execute());
        }

        if(!itemsOff && c.hasItem(Item.GANLON_BERRY) && c.getHealth() < c.getMaxHealth(1 / 4.))
        {
            consumeBerry.accept("Ganlon");

            turnResult.add(builder.addStatChangeEffect(Stat.DEF, 1, 100, true).execute());
        }

        if(!itemsOff && c.hasItem(Item.LIECHI_BERRY) && c.getHealth() < c.getMaxHealth(1 / 4.))
        {
            consumeBerry.accept("Liechi");

            turnResult.add(builder.addStatChangeEffect(Stat.ATK, 1, 100, true).execute());
        }

        if(!itemsOff && c.hasItem(Item.MICLE_BERRY) && c.getHealth() < c.getMaxHealth(1 / 4.))
        {
            consumeBerry.accept("Micle");

            turnResult.add(builder.addAccuracyChangeEffect(1, 100, true).execute());
        }

        if(!itemsOff && c.hasItem(Item.PETAYA_BERRY) && c.getHealth() < c.getMaxHealth(1 / 4.))
        {
            consumeBerry.accept("Petaya");

            turnResult.add(builder.addStatChangeEffect(Stat.SPATK, 1, 100, true).execute());
        }

        if(!itemsOff && c.hasItem(Item.SALAC_BERRY) && c.getHealth() < c.getMaxHealth(1 / 4.))
        {
            consumeBerry.accept("Salac");

            turnResult.add(builder.addStatChangeEffect(Stat.SPD, 1, 100, true).execute());
        }

        if(!itemsOff && c.hasItem(Item.ORAN_BERRY) && c.getHealth() < c.getMaxHealth(1 / 2.))
        {
            consumeBerry.accept("Oran");

            turnResult.add(builder.addFixedHealEffect(10).execute());
        }

        if(!itemsOff && c.hasItem(Item.SITRUS_BERRY) && c.getHealth() < c.getMaxHealth(1 / 2.))
        {
            consumeBerry.accept("Sitrus");

            turnResult.add(builder.addFractionHealEffect(1 / 2.).execute());
        }

        if(!itemsOff && c.hasItem(Item.STARF_BERRY) && c.getHealth() < c.getMaxHealth(1 / 4.))
        {
            consumeBerry.accept("Starf");

            int num = new SplittableRandom().nextInt(Stat.values().length + 2);

            if(num < Stat.values().length) turnResult.add(builder.addStatChangeEffect(Stat.values()[num], 2, 100, true).execute());
            else if(num == Stat.values().length) turnResult.add(builder.addAccuracyChangeEffect(2, 100, true).execute());
            else if(num == Stat.values().length + 1) turnResult.add(builder.addEvasionChangeEffect(2, 100, true).execute());
        }

        //Barrier Effects
        if(this.barriers[this.other].has(FieldBarrier.AURORA_VEIL))
        {
            if(move.is(Category.SPECIAL, Category.PHYSICAL)) move.setDamageMultiplier(0.5);
        }

        if(this.barriers[this.other].has(FieldBarrier.REFLECT))
        {
            if(move.is(Category.PHYSICAL)) move.setDamageMultiplier(0.5);
        }

        if(this.barriers[this.other].has(FieldBarrier.LIGHT_SCREEN))
        {
            if(move.is(Category.SPECIAL)) move.setDamageMultiplier(0.5);
        }

        //Main Results
        String name = this.players[this.current].active.getName();
        List<String> rechargeMoves = List.of("Hyper Beam", "Blast Burn", "Hydro Cannon", "Frenzy Plant", "Roar Of Time", "Prismatic Laser", "Eternabeam", "Giga Impact", "Meteor Assault", "Rock Wrecker");

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

        //Mind Reader
        if(this.data(this.current).mindReaderUsed)
        {
            this.data(this.current).mindReaderUsed = false;

            accurate = true;
            otherImmune = false;
        }

        //Ability: Stance Change (Aegislash)
        if(!move.getCategory().equals(Category.STATUS) && this.players[this.current].active.hasAbility(Ability.STANCE_CHANGE))
        {
            if(this.players[this.current].active.getName().equals("Aegislash"))
            {
                this.players[this.current].active.changeForm("Aegislash Blade");
                this.players[this.current].active.updateName();
            }
        }

        boolean isMoveSuccess = false;

        //Focus Punch
        if(move.getName().equals("Focus Punch") && this.data(this.current).isFocusPunchFailed)
        {
            turnResult.add(this.players[this.current].active.getName() + " lost its focus!");

            this.data(this.other).lastDamageTaken = 0;
        }
        //Bide
        else if(this.data(this.current).bideTurns > 0)
        {
            turnResult.add(this.players[this.current].active.getName() + " is storing energy!");
        }
        //Torment
        else if(this.data(this.current).isTormented && this.getLastUsedMove(this.players[this.current].active.getUUID()).equals(move.getName()))
        {
            turnResult.add(name + " can't use " + move.getName() + " due to Torment!");

            this.data(this.other).lastDamageTaken = 0;
        }
        //Check if user has to recharge
        else if(this.data(this.current).recharge && rechargeMoves.contains(move.getName()))
        {
            turnResult.add(name + " can't use " + move.getName() + " because it needs to recharge!");
            this.data(this.current).recharge = false;

            this.data(this.other).lastDamageTaken = 0;
        }
        //Damage-Dealing Water Moves fail in Extreme Harsh Sunlight
        else if(this.weather.get().equals(Weather.EXTREME_HARSH_SUNLIGHT) && move.getType().equals(Type.WATER) && move.getPower() > 0)
        {
            turnResult.add(move.getName() + " failed due to the Extreme Harsh Sunlight!");

            this.data(this.other).lastDamageTaken = 0;
        }
        //Damage-Dealing Fire Moves fail in Heavy Rain
        else if(this.weather.get().equals(Weather.HEAVY_RAIN) && move.getType().equals(Type.FIRE) && move.getPower() > 0)
        {
            turnResult.add(move.getName() + " failed due to the Heavy Rain!");

            this.data(this.other).lastDamageTaken = 0;
        }
        //Check if something earlier made user not able to use its move
        else if(cantUse)
        {
            turnResult.add(name + " can't use " + move.getName() + " right now!");

            this.data(this.other).lastDamageTaken = 0;
        }
        //Check if the user missed its move
        else if(!accurate)
        {
            turnResult.add(move.getMissedResult(this.players[this.current].active));

            if(move.getName().equals("Jump Kick") || move.getName().equals("High Jump Kick"))
            {
                turnResult.add(" " + this.players[this.current].active.getName() + " kept going and crashed!");

                this.players[this.current].active.damage(this.players[this.current].active.getStat(Stat.HP) / 2);
            }

            this.data(this.other).lastDamageTaken = 0;
        }
        //Check if opponent is immune
        else if(otherImmune)
        {
            turnResult.add(move.getMoveUsedResult(this.players[this.current].active) + " " + this.players[this.other].active.getName() + " is immune to the attack!");

            this.data(this.other).lastDamageTaken = 0;
        }
        //Ability: Disguise (Mimikyu)
        else if(this.players[this.other].active.hasAbility(Ability.DISGUISE) && !this.data(this.other).disguiseActivated && !move.getCategory().equals(Category.STATUS))
        {
            this.data(this.other).disguiseActivated = true;

            turnResult.add("Mimikyu's Disguise was activated and absorbed the attack!");
        }
        //Ability: Damp
        else if((c.hasAbility(Ability.DAMP) || o.hasAbility(Ability.DAMP)) && move.is("Explosion", "Self Destruct", "Mind Blown", "Misty Explosion"))
        {
            turnResult.add(Ability.DAMP.formatActivation((c.hasAbility(Ability.DAMP) ? c : o).getName(), move.getName() + " failed!"));
        }
        //Do main move logic
        else
        {
            isMoveSuccess = true;

            int preMoveHP = this.players[this.other].active.getHealth();

            //Berry Effects – Pre-Successful Move Execution

            Type moveType = move.getType();

            Function<Type, Boolean> effectiveMoveBerryApplies = t -> moveType.equals(t) && TypeEffectiveness.getEffectiveness(o.getType()).get(t) > 1.0;

            BiConsumer<String, Move> effectiveMoveBerryResult = (berryName, moveCopy) -> {
                o.removeItem();

                moveCopy.setDamageMultiplier(0.5);

                turnResult.add(o.getName() + " consumed its " + berryName + " Berry! The damage dealt by " + moveCopy.getName() + " was reduced significantly!");
            };

            if(o.hasItem(Item.BABIRI_BERRY) && effectiveMoveBerryApplies.apply(Type.STEEL))
                effectiveMoveBerryResult.accept("Babiri", move);

            if(o.hasItem(Item.CHARTI_BERRY) && effectiveMoveBerryApplies.apply(Type.ROCK))
                effectiveMoveBerryResult.accept("Charti", move);

            if(o.hasItem(Item.CHILAN_BERRY) && move.is(Type.NORMAL))
                effectiveMoveBerryResult.accept("Chilan", move);

            if(o.hasItem(Item.CHOPLE_BERRY) && effectiveMoveBerryApplies.apply(Type.FIGHTING))
                effectiveMoveBerryResult.accept("Chople", move);

            if(o.hasItem(Item.COBA_BERRY) && effectiveMoveBerryApplies.apply(Type.FLYING))
                effectiveMoveBerryResult.accept("Coba", move);

            if(o.hasItem(Item.COLBUR_BERRY) && effectiveMoveBerryApplies.apply(Type.DARK))
                effectiveMoveBerryResult.accept("Colbur", move);

            if(o.hasItem(Item.HABAN_BERRY) && effectiveMoveBerryApplies.apply(Type.DRAGON))
                effectiveMoveBerryResult.accept("Haban", move);

            if(o.hasItem(Item.KASIB_BERRY) && effectiveMoveBerryApplies.apply(Type.GHOST))
                effectiveMoveBerryResult.accept("Kasib", move);

            if(o.hasItem(Item.KEBIA_BERRY) && effectiveMoveBerryApplies.apply(Type.POISON))
                effectiveMoveBerryResult.accept("Kebia", move);

            if(o.hasItem(Item.OCCA_BERRY) && effectiveMoveBerryApplies.apply(Type.FIRE))
                effectiveMoveBerryResult.accept("Occa", move);

            if(o.hasItem(Item.PASSHO_BERRY) && effectiveMoveBerryApplies.apply(Type.WATER))
                effectiveMoveBerryResult.accept("Passho", move);

            if(o.hasItem(Item.PAYAPA_BERRY) && effectiveMoveBerryApplies.apply(Type.PSYCHIC))
                effectiveMoveBerryResult.accept("Payapa", move);

            if(o.hasItem(Item.RINDO_BERRY) && effectiveMoveBerryApplies.apply(Type.GRASS))
                effectiveMoveBerryResult.accept("Rindo", move);

            if(o.hasItem(Item.ROSELI_BERRY) && effectiveMoveBerryApplies.apply(Type.FAIRY))
                effectiveMoveBerryResult.accept("Roseli", move);

            if(o.hasItem(Item.SHUCA_BERRY) && effectiveMoveBerryApplies.apply(Type.GROUND))
                effectiveMoveBerryResult.accept("Shuca", move);

            if(o.hasItem(Item.TANGA_BERRY) && effectiveMoveBerryApplies.apply(Type.BUG))
                effectiveMoveBerryResult.accept("Tanga", move);

            if(o.hasItem(Item.WACAN_BERRY) && effectiveMoveBerryApplies.apply(Type.ELECTRIC))
                effectiveMoveBerryResult.accept("Wacan", move);

            if(o.hasItem(Item.YACHE_BERRY) && effectiveMoveBerryApplies.apply(Type.ICE))
                effectiveMoveBerryResult.accept("Yache", move);

            //Strong Winds
            if(this.weather.get().equals(Weather.STRONG_WINDS) && move.is(Type.FLYING) && TypeEffectiveness.getEffectiveness(this.players[this.other].active.getType()).get(move.getType()) > 1.0)
                move.setDamageMultiplier(0.5);

            //Primary Move Logic

            turnResult.add(move.logic(this.players[this.current].active, this.players[this.other].active, this));

            //Post-Move Execution

            if(move.getCategory().equals(Category.STATUS)) this.data(this.other).lastDamageTaken = 0;

            if(rechargeMoves.contains(move.getName())) this.data(this.current).recharge = true;

            if(this.players[this.other].active.hasAbility(Ability.IRON_BARBS) && !move.getCategory().equals(Category.STATUS) && this.data(this.other).lastDamageTaken > 0)
            {
                int dmg = this.players[this.current].active.getStat(Stat.HP) / 8;
                this.players[this.current].active.damage(dmg);

                turnResult.add(this.players[this.current].active.getName() + " took " + dmg + " damage from the Iron Barbs!");
            }

            int damageDealt = preMoveHP - this.players[this.other].active.getHealth();

            if(damageDealt > 0 && this.isNonBotPlayer(this.current)) this.damageDealt.put(this.players[this.current].active.getUUID(), this.damageDealt.getOrDefault(this.players[this.current].active.getUUID(), 0) + damageDealt);

            if(this.data(this.other).bideTurns > 0)
            {
                this.data(this.other).bideTurns--;

                this.data(this.other).bideDamage += Math.max(damageDealt, 0);
            }

            //Berry Effects - Post-Move Damage Dealt

            if(damageDealt > 0)
            {
                if(o.hasItem(Item.ENIGMA_BERRY) && TypeEffectiveness.getEffectiveness(o.getType()).get(move.getType()) > 1.0)
                {
                    int amount = o.getMaxHealth(1 / 4.);

                    o.heal(amount);
                    o.removeItem();

                    turnResult.add(o.getName() + " consumed its Enigma Berry! It healed for " + amount + " HP!");
                }

                if(o.hasItem(Item.JABOCA_BERRY) && move.is(Category.PHYSICAL))
                {
                    int amount = c.getMaxHealth(1 / 8.);

                    c.damage(amount);
                    o.removeItem();

                    turnResult.add(o.getName() + " consumed its Jaboca Berry! " + c.getName() + " took " + amount + " recoil damage!");
                }

                if(o.hasItem(Item.ROWAP_BERRY) && move.is(Category.SPECIAL))
                {
                    int amount = c.getMaxHealth(1 / 8.);

                    c.damage(amount);
                    o.removeItem();

                    turnResult.add(o.getName() + " consumed its Rowap Berry! " + c.getName() + " took " + amount + " recoil damage!");
                }

                if(o.hasItem(Item.KEE_BERRY) && move.is(Category.PHYSICAL))
                {
                    o.changes().change(Stat.DEF, 1);
                    o.removeItem();

                    turnResult.add(o.getName() + " consumed its Kee Berry! Its Defense rose by 1 stage!");
                }

                if(o.hasItem(Item.MARANGA_BERRY) && move.is(Category.PHYSICAL))
                {
                    o.changes().change(Stat.SPDEF, 1);
                    o.removeItem();

                    turnResult.add(o.getName() + " consumed its Maranga Berry! Its Special Defense rose by 1 stage!");
                }
            }

            //Other Stuff

            if(damageDealt > 0 && this.isNonBotPlayer(this.current))
            {
                final Move m = move;
                this.players[this.current].data.updateBountyProgression(b -> {
                    switch(b.getType()) {
                        case DAMAGE_POKEMON -> b.update(damageDealt);
                        case DAMAGE_POKEMON_TYPE -> b.updateIf(this.players[this.other].active.isType(b.getObjective().asTypeObjective().getType()), damageDealt);
                        case DAMAGE_POKEMON_CATEGORY -> b.updateIf(b.getObjective().asCategoryObjective().getCategory().equals(m.getCategory()), damageDealt);
                    }
                });
            }

            if(this.isNonBotPlayer(this.current))
            {
                final Move m = move;
                this.players[this.current].data.updateBountyProgression(b -> {
                    switch(b.getType()) {
                        case USE_MOVES -> b.update();
                        case USE_MOVES_TYPE -> b.updateIf(b.getObjective().asTypeObjective().getType().equals(m.getType()));
                        case USE_MOVES_CATEGORY -> b.updateIf(b.getObjective().asCategoryObjective().getCategory().equals(m.getCategory()));
                        case USE_MOVES_NAME -> b.updateIf(b.getObjective().asNameObjective().getName().equals(m.getName()));
                        case USE_MOVES_POOL -> b.updateIf(b.getObjective().asPoolObjective().getPool().contains(m.getName()));
                        case USE_MOVES_POWER_LESS -> b.updateIf(m.getPower() < b.getObjective().asPowerObjective().getPower());
                        case USE_MOVES_POWER_GREATER -> b.updateIf(m.getPower() > b.getObjective().asPowerObjective().getPower());
                        case USE_MOVES_ACCURACY_LESS -> b.updateIf(m.getAccuracy() < b.getObjective().asAccuracyObjective().getAccuracy());
                        case USE_MOVES_PRIORITY_HIGH -> b.updateIf(m.getPriority() > 0);
                        case USE_MOVES_PRIORITY_LOW -> b.updateIf(m.getPriority() < 0);
                        case USE_ZMOVE -> b.updateIf(m.isZMove);
                        case USE_ZMOVE_TYPE -> b.updateIf(m.isZMove && b.getObjective().asTypeObjective().getType().equals(m.getType()));
                        case USE_MAX_MOVE -> b.updateIf(m.isMaxMove);
                        case USE_MAX_MOVE_TYPE -> b.updateIf(m.isMaxMove && b.getObjective().asTypeObjective().getType().equals(m.getType()));
                    }
                });
            }
        }

        //Post-Move Updates

        if(!isMoveSuccess || (this.data(this.current).furyCutterUsed && !move.getName().equals("Fury Cutter")))
        {
            this.data(this.current).furyCutterUsed = false;
            this.data(this.current).furyCutterTurns = 0;
        }

        if(c.hasAbility(Ability.SPEED_BOOST))
        {
            this.players[this.current].active.changes().change(Stat.SPD, 1);

            turnResult.add(Ability.SPEED_BOOST.formatActivation(c.getName(), c.getName() + "'s Speed rose by 1 stage!"));
        }

        //Field Effects

        if(this.fieldEffects[this.current].has(FieldEffect.TAILWIND))
        {
            this.fieldEffects[this.current].tailwindTurns--;

            if(this.fieldEffects[this.current].tailwindTurns <= 0)
            {
                this.fieldEffects[this.current].tailwindTurns = 0;
                this.fieldEffects[this.current].remove(FieldEffect.TAILWIND);

                for(Pokemon p : this.players[this.current].team) p.overrides().remove(Stat.SPD);

                turnResult.add("Tailwind's effects wore off!");
            }
        }

        if(this.data(this.current).yawnTurns > 0)
        {
            this.data(this.current).yawnTurns++;

            if(this.data(this.current).yawnTurns >= 2)
            {
                this.data(this.current).yawnTurns = 0;

                this.players[this.current].active.addStatusCondition(StatusCondition.ASLEEP);
                this.data(this.current).asleepTurns = 0;

                turnResult.add(this.players[this.current].active.getName() + " has fallen asleep!");
            }
        }

        //Update Move Log
        this.movesUsed.get(this.players[this.current].active.getUUID()).add(move.getName());

        //Give EVs and EXP if opponent has fainted
        if(this.players[this.other].active.isFainted())
        {
            this.players[this.current].active.gainEVs(this.players[this.other].active);

            String UUID = this.players[this.current].active.getUUID();
            int exp = this.players[this.current].active.getDefeatExp(this.players[this.other].active);
            this.expGains.put(UUID, (this.expGains.getOrDefault(UUID, 0)) + exp);

            if(this.isNonBotPlayer(this.current))
            {
                this.players[this.current].data.updateBountyProgression(b -> {
                    switch(b.getType())
                    {
                        case DEFEAT_POKEMON -> b.update();
                        case DEFEAT_POKEMON_TYPE -> {
                            if(this.players[this.other].active.isType(b.getObjective().asTypeObjective().getType())) b.update();
                        }
                        case DEFEAT_POKEMON_POOL -> {
                            if(b.getObjective().asPoolObjective().getPool().contains(this.players[this.other].active.getName())) b.update();
                        }
                        case DEFEAT_LEGENDARY -> {
                            String otherName = this.players[this.other].active.getName();
                            if(PokemonRarity.LEGENDARY.contains(otherName) || PokemonRarity.MYTHICAL.contains(otherName) || PokemonRarity.ULTRA_BEAST.contains(otherName)) b.update();
                        }
                        case EARN_EVS -> b.update(this.players[this.other].active.getEVYield().values().stream().mapToInt(e -> e).sum());
                        case EARN_EVS_STAT -> {
                            int statYield = this.players[this.other].active.getEVYield().get(b.getObjective().asStatObjective().getStat());
                            if(statYield != 0) b.update(statYield);
                        }
                    }
                });

                this.players[this.current].data.getStatistics().incr(PlayerStatistic.POKEMON_DEFEATED);
            }

            if(this.isNonBotPlayer(this.other)) this.players[this.other].data.getStatistics().incr(PlayerStatistic.POKEMON_FAINTED);

            if(this.players[this.current].active.isDynamaxed() && this.players[this.current].active.getDynamaxLevel() < 10 && new Random().nextInt(100) < 40)
            {
                this.players[this.current].active.increaseDynamaxLevel();
                this.players[this.current].active.updateDynamaxLevel();

                this.event.getChannel().sendMessage(this.players[this.current].data.getMention() + ": " + this.players[this.current].active.getName() + " earned a Dynamax Level!").queue();
            }

            if(this.players[this.current].active.hasAbility(Ability.GRIM_NEIGH))
            {
                this.players[this.current].active.changes().change(Stat.SPATK, 1);

                turnResult.add(Ability.GRIM_NEIGH.formatActivation(c.getName(), c.getName() + "'s Special Attack rose by 1 stage!"));
            }

            if(this.players[this.current].active.hasAbility(Ability.CHILLING_NEIGH))
            {
                this.players[this.current].active.changes().change(Stat.ATK, 1);

                turnResult.add(Ability.CHILLING_NEIGH.formatActivation(c.getName(), c.getName() + "'s Attack rose by 1 stage!"));
            }

            if(this.data(this.other).destinyBondUsed)
            {
                this.data(this.other).destinyBondUsed = false;

                this.players[this.current].active.damage(this.players[this.current].active.getHealth());
                turnResult.add(this.players[this.current].active.getName() + " fainted from the Destiny Bond!");
            }
        }

        if(this.first.equals(this.players[this.current].active.getUUID())) turnResult.add("\n\n");

        return String.join(" ", turnResult);
    }

    //Turn Helper Methods
    public void setDefaults()
    {
        this.weather = new WeatherHandler();
        this.terrain = new TerrainHandler();
        this.room = new RoomHandler();
        this.entryHazards = new EntryHazardHandler[]{new EntryHazardHandler(), new EntryHazardHandler()};
        this.barriers = new FieldBarrierHandler[]{new FieldBarrierHandler(), new FieldBarrierHandler()};
        this.gmaxDoT = new FieldGMaxDoTHandler[]{new FieldGMaxDoTHandler(), new FieldGMaxDoTHandler()};
        this.fieldEffects = new FieldEffectsHandler[]{new FieldEffectsHandler(), new FieldEffectsHandler()};
        this.queuedMoves = new HashMap<>();

        for(Player player : this.players) for(int i = 0; i < player.team.size(); i++) player.team.get(i).setHealth(player.team.get(i).getMaxHealth());

        for(Player p : this.players)
        {
            for(Pokemon pokemon : p.team) this.movesUsed.put(pokemon.getUUID(), new ArrayList<>());
        }
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
        this.weather.updateTurns();
        this.terrain.updateTurns();
        this.room.updateTurns();
    }

    public void weatherEffects()
    {
        StringBuilder weatherResult = new StringBuilder().append("\n").append(this.weather.get().getStatus()).append("\n");

        switch(this.weather.get())
        {
            case HAIL -> {
                if(this.isAffectedByHail(0)) this.doHailEffect(weatherResult, 0);

                if(this.isAffectedByHail(1)) this.doHailEffect(weatherResult, 1);
            }
            case SANDSTORM -> {
                if(this.isAffectedBySandstorm(0)) this.doSandstormEffect(weatherResult, 0);

                if(this.isAffectedBySandstorm(1)) this.doSandstormEffect(weatherResult, 1);
            }
        }

        this.results.add("\n" + weatherResult);
    }

    private void doHailEffect(StringBuilder weatherResult, int p)
    {
        int amount = this.players[p].active.getMaxHealth() / 16;
        this.players[p].active.damage(amount);
        weatherResult.append(this.players[p].active.getName()).append(" took %s damage from the hailstorm!\n".formatted(amount));
    }

    private void doSandstormEffect(StringBuilder weatherResult, int p)
    {
        int amount = this.players[p].active.getMaxHealth() / 16;
        this.players[p].active.damage(amount);
        weatherResult.append(this.players[p].active.getName()).append(" took %s damage from the sandstorm!\n".formatted(amount));
    }

    private boolean isAffectedByHail(int p)
    {
        return !this.players[p].active.isType(Type.ICE) && !this.data(p).digUsed && !this.data(p).diveUsed && !this.data(p).phantomForceUsed && !this.data(p).shadowForceUsed;
    }

    private boolean isAffectedBySandstorm(int p)
    {
        return !this.players[p].active.isType(Type.GROUND) && !this.players[p].active.isType(Type.ROCK) && !this.players[p].active.isType(Type.STEEL) && !this.data(p).digUsed && !this.data(p).diveUsed && !this.data(p).phantomForceUsed && !this.data(p).shadowForceUsed;
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
            double rockEffective = TypeEffectiveness.getEffectiveness(this.players[player].active.getType()).get(Type.ROCK);
            double damagePercent = 0.125 * rockEffective;

            int damage = (int)(this.players[player].active.getStat(Stat.HP) * damagePercent);

            this.players[player].active.damage(damage);

            hazardResults += "Stealth Rock dealt " + damage + " damage to " + name + "!\n";
        }

        if(this.entryHazards[player].hasHazard(EntryHazard.STICKY_WEB) && !this.data(player).isRaised)
        {
            this.players[player].active.changes().change(Stat.SPD, -1);

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
            this.pokemonAttributes.put(p.getUUID(), new PokemonDuelAttributes(p.getUUID()));
            this.data(player).isRaised = p.isType(Type.FLYING) || p.hasAbility(Ability.LEVITATE);
        }
    }

    protected void turnSetup()
    {
        this.setStatus(DuelStatus.DUELING);

        this.results = new ArrayList<>();

        this.players[0].move = null;
        this.players[1].move = null;

        this.first = "";

        //First turn effects
        if(turn == 0)
        {
            this.checkWeatherAbilities();

            //Effects that occur on the first turn of the battle
            this.onBattleStart(0);
            this.onBattleStart(1);
        }

        this.turn++;
    }

    private void onBattleStart(int p)
    {
        //Ability: Dauntless Shield
        if(this.players[p].active.hasAbility(Ability.DAUNTLESS_SHIELD))
        {
            this.players[p].active.changes().change(Stat.DEF, 1);
            this.results.add(Ability.DAUNTLESS_SHIELD.formatActivation(this.players[p].active.getName(), this.players[p].active.getName() + "'s Defense rose by 1 stage!"));
        }
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
        Pokemon active = this.players[p].active;
        String name = this.players[p].active.getName();

        List<Ability> standard = List.of(Ability.DROUGHT, Ability.DRIZZLE, Ability.SAND_STREAM, Ability.SNOW_WARNING);
        List<Ability> primal = List.of(Ability.DESOLATE_LAND, Ability.PRIMORDIAL_SEA, Ability.DELTA_STREAM);
        List<Ability> weatherAbilities = new ArrayList<>(); weatherAbilities.addAll(standard); weatherAbilities.addAll(primal);

        //If the user doesn't have any weather abilities, return
        if(active.getAbilities().stream().noneMatch(weatherAbilities::contains)) return;

        Ability ab = active.getAbilities().stream().filter(weatherAbilities::contains).findFirst().orElseThrow();
        Weather w = switch(ab) {
            case DROUGHT -> Weather.HARSH_SUNLIGHT;
            case DRIZZLE -> Weather.RAIN;
            case SAND_STREAM -> Weather.SANDSTORM;
            case SNOW_WARNING -> Weather.HAIL;
            case DESOLATE_LAND -> Weather.EXTREME_HARSH_SUNLIGHT;
            case PRIMORDIAL_SEA -> Weather.HEAVY_RAIN;
            case DELTA_STREAM -> Weather.STRONG_WINDS;
            default -> throw new IllegalStateException("Invalid Weather Ability");
        };

        String activationFailedResult = name + "'s " + ab.getName() + " failed to activate!";

        //If the weather effect is already in play, the ability should fail
        if(this.weather.get().equals(w))
        {
            this.results.add(activationFailedResult + " " + this.weather.get().getName() + " already present!");
            return;
        }

        //If the ability is a Standard Weather Ability and a Primal Weather is active, return
        if(this.weather.get().isPrimalWeather() && standard.contains(ab))
        {
            this.results.add(activationFailedResult + " " + this.weather.get().getName() + " could not be removed!");
            return;
        }

        //Otherwise, the Weather Ability should activate and change the weather
        String standardActivatedResult = ab.formatActivation(name, w.getName() + " covers the battlefield for 5 turns!");
        String primalActivatedResult = ab.formatActivation(name, w.getName() + "'s presence will be felt permanently!");

        //Final Weather Change Effect
        if(standard.contains(ab))
        {
            this.results.add(standardActivatedResult);

            this.weather.setWeather(w);
        }
        else if(primal.contains(ab))
        {
            this.results.add(primalActivatedResult);

            this.weather.setPermanentWeather(w);
        }
        else LoggerHelper.error(Duel.class, "Weather Ability failed to activate after passing checks! Ability {%s} & Weather {%s}".formatted(ab.toString(), w.toString()));

        this.results.add("\n");
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
            ButtonListener.ZMOVE_SELECTIONS.remove(this.players[0].ID);
            if(this.isComplete())
            {
                this.event.getChannel().sendFile(this.getImage(), "duel.png").setEmbeds(embed.build()).queue();
            }
            else this.event.getChannel()
                    .sendFile(this.getImage(), "duel.png")
                    .setEmbeds(embed.build())
                    /*.flatMap(m -> m.reply("Turn Action Selection:")
                            .setActionRows(
                                    ActionRow.of(
                                            Button.primary(ButtonListener.DUEL_MOVE_BUTTONS.get(0), "Move 1"),
                                            Button.primary(ButtonListener.DUEL_MOVE_BUTTONS.get(1), "Move 2"),
                                            Button.primary(ButtonListener.DUEL_MOVE_BUTTONS.get(2), "Move 3"),
                                            Button.primary(ButtonListener.DUEL_MOVE_BUTTONS.get(3), "Move 4")
                                    ),
                                    ActionRow.of(
                                            Button.primary(ButtonListener.DUEL_ZMOVE_BUTTON, "Z-Move"),
                                            Button.primary(ButtonListener.DUEL_DYNAMAX_BUTTON, "Dynamax")
                                    )
                            ))
                    .delay(30, TimeUnit.SECONDS)
                    .flatMap(Message::delete)*/
                    .queue();
        }
        catch (Exception e)
        {
            LoggerHelper.reportError(Duel.class, "Duel Image generation failed!", e);

            this.event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }
    }

    public void onWin()
    {
        for(Player p : this.players)
        {
            for(int i = 0; i < p.team.size(); i++)
            {
                Pokemon pokemon = p.team.get(i);

                if(pokemon.hasConsumedItem()) pokemon.updateItem();
            }
        }
    }

    public void sendWinEmbed()
    {
        EmbedBuilder embed = new EmbedBuilder();

        int winner = this.getWinner().ID.equals(this.players[0].ID) ? 0 : 1;
        int loser = winner == 0 ? 1 : 0;

        int c = this.players[0].team.size() >= 6 ? this.giveWinCredits() : new Random().nextInt(11) + 10;

        if(CommandTarget.isTarget(this.event.getGuild(), this.players[winner].ID))
        {
            c = (new Random().nextInt(201) + 50) * (CommandTarget.SERVER_TARGET_DUELS_WON.get(this.event.getGuild().getId()) + 1);

            CommandTarget.SERVER_TARGET_DUELS_WON.put(this.event.getGuild().getId(), CommandTarget.SERVER_TARGET_DUELS_WON.get(this.event.getGuild().getId()) + 1);

            embed.setFooter("The Server Target has won another duel!");
        }
        else if(CommandTarget.isTarget(this.event.getGuild(), this.players[loser].ID))
        {
            CommandTarget.SERVER_TARGETS.remove(this.event.getGuild().getId());

            c = new Random().nextInt(501) + 500;

            CommandTarget.generateNewServerTarget(this.event.getGuild());

            embed.setFooter("The Server Target has been defeated!");
        }

        embed.setDescription(this.getWinner().data.getUsername() + " has won!" + (c != 0 ? "\nThey earned " + c + " credits!" : ""));

        this.event.getChannel().sendMessageEmbeds(embed.build()).queue();

        Achievements.grant(this.getWinner().ID, Achievements.WON_FIRST_PVP_DUEL, this.event);
        if(this.size == CommandTeam.MAX_TEAM_SIZE) Achievements.grant(this.getWinner().ID, Achievements.WON_FIRST_DUEL_MAX_SIZE, this.event);

        this.players[winner].data.getStatistics().incr(PlayerStatistic.PVP_DUELS_WON);
        this.players[winner].data.getStatistics().incr(PlayerStatistic.PVP_DUELS_COMPLETED);
        this.players[loser].data.getStatistics().incr(PlayerStatistic.PVP_DUELS_COMPLETED);

        if(new Random().nextInt(100) < 20)
        {
            this.uploadEVs(0);
            this.uploadEVs(1);
        }

        this.players[winner].data.addExp(20);

        this.players[winner].data.getStatistics().incr(PlayerStatistic.PVP_DUELS_WON);
        this.players[winner].data.updateBountyProgression(b -> {
            if(b.getType().equals(ObjectiveType.WIN_PVP_DUEL) || b.getType().equals(ObjectiveType.COMPLETE_PVP_DUEL)) b.update();
        });
        this.players[loser].data.updateBountyProgression(ObjectiveType.COMPLETE_PVP_DUEL);

        if(TournamentHelper.isInTournament(this.players[winner].ID) && TournamentHelper.isInTournament(this.players[loser].ID))
        {
            Tournament t = TournamentHelper.instance(this.getWinner().ID);

            boolean neitherElim = !t.isPlayerEliminated(this.players[winner].ID) && !t.isPlayerEliminated(this.players[loser].ID);
            boolean matchupValid = t.getMatchups().stream().anyMatch(m -> m.has(this.players[winner].ID) && m.has(this.players[loser].ID));

            if(neitherElim && matchupValid) t.addDuelResults(this.players[winner].ID, this.players[loser].ID);
        }

        this.uploadExperience();

        DuelHelper.delete(this.players[0].ID);
    }

    protected void uploadEVs(int player)
    {
        for(Pokemon p : this.players[player].team)
        {
            p.updateEVs();
        }
    }

    protected void uploadExperience()
    {
        Pokemon p;
        for(String uuid : this.expGains.keySet())
        {
            p = Pokemon.build(uuid);

            if(p != null)
            {
                p.addExp(this.expGains.get(uuid));
                p.updateExperience();
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

        if(type == 'i') this.queuedMoves.put(id, new TurnAction(ActionType.IDLE, -1, -1));
        //index functions as both the swapIndex and moveIndex, which one is dictated by type == 's' and type == 'm'
        else if(type == 's')
        {
            if(this.players[this.indexOf(id)].team.get(index - 1).isFainted()) this.event.getChannel().sendMessage("That pokemon is fainted!").queue();
            else this.queuedMoves.put(id, new TurnAction(ActionType.SWAP, -1, index));
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

        if((faintSwap1 || faintSwap2) && !(faintSwap1 && faintSwap2))
        {
            if(faintSwap1) this.submitMove(this.players[1].ID, -1, 'i');
            else this.submitMove(this.players[0].ID, -1, 'i');

            turnHandler();
        }
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

    public PokemonDuelAttributes data(String UUID)
    {
        return this.pokemonAttributes.get(UUID);
    }

    public PokemonDuelAttributes data(int player)
    {
        return this.data(this.players[player].active.getUUID());
    }

    public EntryHazardHandler hazardData(String UUID)
    {
        return this.entryHazards[this.players[0].active.getUUID().equals(UUID) ? 0 : 1];
    }

    public List<String> getMovesUsed(String UUID)
    {
        return this.movesUsed.get(UUID);
    }

    public String getLastUsedMove(String UUID)
    {
        return this.getMovesUsed(UUID).isEmpty() ? "" : this.getMovesUsed(UUID).get(this.getMovesUsed(UUID).size() - 1);
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

    protected String getPokemonURL(int player)
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

    protected String getHealthBars()
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

    public void setEvent(MessageReceivedEvent event)
    {
        this.event = event;
        this.turn = 0;
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
        this.size = size;
    }

    public Player[] getPlayers()
    {
        return this.players;
    }

    public Player getPlayer(String ID)
    {
        return this.players[this.indexOf(ID)];
    }

    public boolean isNonBotPlayer(int p)
    {
        return this.players[p].ID.chars().allMatch(Character::isDigit);
    }

    public int indexOf(String id)
    {
        for(int i = 0; i < this.players.length; i++) if(this.players[i].ID.equals(id)) return i;
        return -1;
    }

    public int playerIndexFromUUID(String UUID)
    {
        for(int i = 0; i < this.players.length; i++) if(this.players[i].active.getUUID().equals(UUID)) return i;
        return -1;
    }

    public boolean hasPlayer(String id)
    {
        if(this.players == null) return false;
        else return Arrays.stream(this.players).anyMatch(p -> p.ID.equals(id));
    }
}
