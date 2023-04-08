package com.calculusmaster.pokecord.game.duel;

import com.calculusmaster.pokecord.Pokecord;
import com.calculusmaster.pokecord.commandslegacy.duel.CommandLegacyTarget;
import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.duel.component.*;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.extension.CasualMatchmadeDuel;
import com.calculusmaster.pokecord.game.duel.players.Player;
import com.calculusmaster.pokecord.game.duel.players.UserPlayer;
import com.calculusmaster.pokecord.game.duel.tournament.Tournament;
import com.calculusmaster.pokecord.game.duel.tournament.TournamentHelper;
import com.calculusmaster.pokecord.game.enums.elements.*;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.TypeEffectiveness;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.game.player.level.PMLExperience;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.augments.PokemonAugment;
import com.calculusmaster.pokecord.game.pokemon.component.PokemonDuelAttributes;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity;
import com.calculusmaster.pokecord.game.pokemon.evolution.GigantamaxRegistry;
import com.calculusmaster.pokecord.game.pokemon.evolution.MegaChargeManager;
import com.calculusmaster.pokecord.game.pokemon.evolution.MegaEvolutionRegistry;
import com.calculusmaster.pokecord.game.world.RegionManager;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;

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
import java.util.stream.Stream;

import static com.calculusmaster.pokecord.game.duel.core.DuelHelper.*;

//PVP Duel - Infinitely Scalable
public class Duel
{
    protected DuelStatus status;
    protected List<TextChannel> channels;
    protected int size;
    protected Player[] players;
    protected Map<String, TurnAction> queuedMoves = new HashMap<>();
    protected Map<String, PokemonDuelAttributes> pokemonAttributes = new HashMap<>();
    protected Map<String, Integer> expGains = new HashMap<>();
    protected Map<String, Integer> damageDealt = new HashMap<>();
    protected Map<String, List<MoveEntity>> movesUsed = new HashMap<>();

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
        duel.setSize(size);
        duel.setTurn();
        duel.addChannel(event.getChannel().asTextChannel());
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
            if(this.channels.isEmpty()) LoggerHelper.error(Duel.class, "Duel Error: Cannot send Embeds, Channels list is empty!");
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
            if(this.channels.isEmpty()) LoggerHelper.error(Duel.class, "Duel Error: Cannot send Embeds, Channels list is empty!");
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
        if(this.terrain.get().equals(Terrain.GRASSY_TERRAIN) && this.players[0].move.is(MoveEntity.GRASSY_GLIDE))
            this.players[0].move.setPriority(1);
        if(this.terrain.get().equals(Terrain.GRASSY_TERRAIN) && this.players[1].move.is(MoveEntity.GRASSY_GLIDE))
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

            if(this.players[p] instanceof UserPlayer user) user.data.getStatistics().incr(PlayerStatistic.DYNAMAXED_POKEMON);

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

        this.results.add(this.players[p].getName() + " brought in " + this.players[p].active.getName() + "!\n");

        //Check if the new Pokemon has a Weather-Causing Ability
        this.checkWeatherAbilities(p);

        //Check for Entry Hazard Effects
        this.entryHazardEffects(p);

        //Activate the first swap flag (this is then toggled off at the end of every turn)
        this.data(this.players[p].active.getUUID()).firstAfterSwap = true;

        //Ability: Dauntless Shield
        if(this.players[p].active.hasAbility(Ability.DAUNTLESS_SHIELD))
        {
            this.players[p].active.changes().change(Stat.DEF, 1);
            this.results.add(Ability.DAUNTLESS_SHIELD.formatActivation(this.players[p].active.getName(), this.players[p].active.getName() + "'s Defense rose by 1 stage!"));
        }

        //Ability: Intrepid Sword
        if(this.players[p].active.hasAbility(Ability.INTREPID_SWORD))
        {
            this.players[p].active.changes().change(Stat.ATK, 1);
            this.results.add(Ability.INTREPID_SWORD.formatActivation(this.players[p].active.getName(), this.players[p].active.getName() + "'s Attack rose by 1 stage!"));
        }

        //Ability: Electric Surge
        if(this.players[p].active.hasAbility(Ability.ELECTRIC_SURGE))
        {
            this.terrain.setTerrain(Terrain.ELECRIC_TERRAIN);
            this.results.add(Ability.ELECTRIC_SURGE.formatActivation(this.players[p].active.getName(), "An Electric Terrain was created!"));
        }

        //Ability: Psychic Surge
        if(this.players[p].active.hasAbility(Ability.PSYCHIC_SURGE))
        {
            this.terrain.setTerrain(Terrain.PSYCHIC_TERRAIN);
            this.results.add(Ability.PSYCHIC_SURGE.formatActivation(this.players[p].active.getName(), "A Psychic Terrain was created!"));
        }

        //Ability: Grassy Surge
        if(this.players[p].active.hasAbility(Ability.MISTY_SURGE))
        {
            this.terrain.setTerrain(Terrain.MISTY_TERRAIN);
            this.results.add(Ability.MISTY_SURGE.formatActivation(this.players[p].active.getName(), "A Misty Terrain was created!"));
        }

        //Ability: Misty Surge
        if(this.players[p].active.hasAbility(Ability.GRASSY_SURGE))
        {
            this.terrain.setTerrain(Terrain.GRASSY_TERRAIN);
            this.results.add(Ability.GRASSY_SURGE.formatActivation(this.players[p].active.getName(), "A Grassy Terrain was created!"));
        }

        if(this.players[p] instanceof UserPlayer player) player.data.updateBountyProgression(ObjectiveType.SWAP_POKEMON);
    }

    //Always use this.current!
    public String turn(Move move)
    {
        final Random random = new Random();
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

                if(move.is(MoveEntity.JUDGMENT)) move.setType(t);
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
        if(move.is(MoveEntity.TERRAIN_PULSE))
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
        if(move.is(MoveEntity.WEATHER_BALL))
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

        //Ability: Liquid Voice
        if(c.hasAbility(Ability.LIQUID_VOICE) && move.is(Move.SOUND_BASED_MOVES))
        {
            move.setType(Type.WATER);

            turnResult.add(Ability.LIQUID_VOICE.formatActivation(c.getName(), move.getName() + " is now a Water Type!"));
        }

        //Ability: Aerilate
        if(c.hasAbility(Ability.AERILATE) && move.is(Type.NORMAL))
        {
            move.setType(Type.FLYING);

            turnResult.add(Ability.AERILATE.formatActivation(c.getName(), move.getName() + " is now a Flying Type!"));
        }

        //Augment: Spectral Supercharge
        if(c.hasAugment(PokemonAugment.SPECTRAL_SUPERCHARGE) && move.is(Type.FIGHTING))
        {
            move.setType(Type.GHOST);

            turnResult.add(move.getName() + " is now a Ghost-type move due to the %s Augment!".formatted(PokemonAugment.SPECTRAL_SUPERCHARGE.getAugmentName()));
        }

        //Weather-based Move Changes

        switch(this.weather.get())
        {
            case HAIL -> {
                if(move.is(MoveEntity.BLIZZARD)) move.setAccuracy(100);

                if(move.is(MoveEntity.SOLAR_BEAM, MoveEntity.SOLAR_BLADE)) move.setPower(0.5);
            }
            case HARSH_SUNLIGHT, EXTREME_HARSH_SUNLIGHT -> {
                if(move.is(Type.FIRE)) move.setPower(1.5);
                else if(move.is(Type.WATER)) move.setPower(0.5);

                if(move.is(MoveEntity.THUNDER, MoveEntity.HURRICANE)) move.setAccuracy(50);
            }
            case RAIN -> {
                if(move.is(Type.WATER)) move.setPower(1.5);
                else if(move.is(Type.FIRE) || move.is(MoveEntity.SOLAR_BEAM, MoveEntity.SOLAR_BLADE)) move.setPower(0.5);

                if(move.is(MoveEntity.THUNDER, MoveEntity.HURRICANE)) move.setAccuracy(100);
            }
            case SANDSTORM -> {
                if(move.is(MoveEntity.SOLAR_BEAM, MoveEntity.SOLAR_BLADE)) move.setPower(0.5);
            }
        }

        //Terrain-based Move Changes

        switch(this.terrain.get())
        {
            case ELECRIC_TERRAIN -> {
                if(move.getType().equals(Type.ELECTRIC)) move.setPower(1.5);
            }
            case GRASSY_TERRAIN -> {
                if(move.getType().equals(Type.GRASS)) move.setPower(1.5);

                if(!this.data(this.current).isRaised) this.players[this.current].active.heal(c.getMaxHealth(1 / 16.));
            }
            case MISTY_TERRAIN -> {
                if(move.getType().equals(Type.DRAGON)) move.setDamageMultiplier(0.5);
            }
            case PSYCHIC_TERRAIN -> {
                if(move.getType().equals(Type.PSYCHIC)) move.setPower(1.5);
            }
        }

        //If the pokemon uses an unfreeze remove, remove the Frozen Status Condition
        if(this.players[this.current].active.hasStatusCondition(StatusCondition.FROZEN) && move.is(MoveEntity.FUSION_FLARE, MoveEntity.FLAME_WHEEL, MoveEntity.SACRED_FIRE, MoveEntity.FLARE_BLITZ, MoveEntity.SCALD, MoveEntity.STEAM_ERUPTION))
        {
            this.players[this.current].active.removeStatusCondition(StatusCondition.FROZEN);
            turnResult.add(c.getName() + " was thawed by using " + move.getName() + "!");
        }

        //Unfreeze opponent if this move is a Fire Type, Scald or Steam Eruption
        if(move.is(Type.FIRE))
        {
            this.players[this.other].active.removeStatusCondition(StatusCondition.FROZEN);
            turnResult.add(c.getName() + " is no longer frozen due to their Fire Type!");
        }

        //Check Status Conditions
        if(this.players[this.current].active.hasAnyStatusCondition())
        {
            List<String> statusResults = new ArrayList<>();
            Random r = new Random();
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

            if(c.hasAbility(Ability.LIMBER) && c.hasStatusCondition(StatusCondition.PARALYZED))
            {
                c.removeStatusCondition(StatusCondition.PARALYZED);

                statusResults.add(Ability.LIMBER.formatActivation(c.getName(), "%s's paralysis was cured!".formatted(c.getName())));
            }

            if(c.hasAbility(Ability.WATER_VEIL) && c.hasStatusCondition(StatusCondition.BURNED))
            {
                c.removeStatusCondition(StatusCondition.BURNED);

                statusResults.add(Ability.WATER_VEIL.formatActivation(c.getName(), "%s is no longer burned!".formatted(c.getName())));
            }

            if(c.hasAbility(Ability.VITAL_SPIRIT) && c.hasStatusCondition(StatusCondition.ASLEEP))
            {
                c.removeStatusCondition(StatusCondition.ASLEEP);
                this.data(this.current).asleepTurns = 0;

                statusResults.add(Ability.VITAL_SPIRIT.formatActivation(c.getName(), c.getName() + " woke up!"));
            }

            if(c.hasAbility(Ability.AROMA_VEIL) && c.hasStatusCondition(StatusCondition.INFATUATED))
            {
                statusResults.add(Ability.AROMA_VEIL.formatActivation(c.getName(), "The infatuation has no effect!"));

                c.removeStatusCondition(StatusCondition.INFATUATED);
            }

            if(c.hasAbility(Ability.INNER_FOCUS) && c.hasStatusCondition(StatusCondition.FLINCHED))
            {
                statusResults.add(Ability.INNER_FOCUS.formatActivation(c.getName(), "Flinching was prevented!"));

                c.removeStatusCondition(StatusCondition.FLINCHED);
            }

            if(c.hasAbility(Ability.MAGMA_ARMOR) && c.hasStatusCondition(StatusCondition.FROZEN))
            {
                statusResults.add(Ability.MAGMA_ARMOR.formatActivation(c.getName(), c.getName() + " thawed out!"));

                c.removeStatusCondition(StatusCondition.FROZEN);
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

                String singeEffect = "";
                if(o.hasAugment(PokemonAugment.POISONOUS_SINGE))
                {
                    statusDamage *= 2;
                    singeEffect = " The poison was especially toxic due to " + o.getName() + "'s " + PokemonAugment.POISONOUS_SINGE.getAugmentName() + " Augment!";
                }

                c.damage(statusDamage);

                statusResults.add("%s is poisoned! The poison dealt %s damage!%s".formatted(c.getName(), statusDamage, singeEffect));
            }

            if(c.hasStatusCondition(StatusCondition.BADLY_POISONED))
            {
                statusDamage = c.getMaxHealth(1 / 16.) * this.data(this.current).badlyPoisonedTurns;

                String singeEffect = "";
                if(o.hasAugment(PokemonAugment.POISONOUS_SINGE))
                {
                    statusDamage *= 2;
                    singeEffect = " The poison was especially toxic due to " + o.getName() + "'s " + PokemonAugment.POISONOUS_SINGE.getAugmentName() + " Augment!";
                }

                c.damage(statusDamage);

                statusResults.add("%s is badly poisoned! The poison dealt %s damage!%s".formatted(c.getName(), statusDamage, singeEffect));
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
                    statusDamage = new Move(MoveEntity.TACKLE).getDamage(c, c);
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

                    if(o.hasAbility(Ability.BAD_DREAMS))
                    {
                        statusDamage = c.getMaxHealth(1 / 8.);
                        c.damage(statusDamage);

                        statusResults.add(Ability.BAD_DREAMS.formatActivation(o.getName(), c.getName() + " took an additional " + statusDamage + " damage!"));
                    }

                    statusResults.add("%s is asleep!".formatted(c.getName()));

                    if(!move.is(MoveEntity.SNORE))
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

                if(c.hasAbility(Ability.STEADFAST))
                {
                    c.changes().change(Stat.SPD, 1);
                    statusResults.add(Ability.STEADFAST.formatActivation(c.getName(), c.getName() + "'s Speed rose by 1 stage!"));
                }

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

        //Check Ignored Abilities
        EnumSet<MoveEntity> ignoreAbilityMoves = EnumSet.of(MoveEntity.SUNSTEEL_STRIKE, MoveEntity.MOONGEIST_BEAM, MoveEntity.PHOTON_GEYSER, MoveEntity.SEARING_SUNRAZE_SMASH, MoveEntity.MENACING_MOONRAZE_MAELSTROM, MoveEntity.LIGHT_THAT_BURNS_THE_SKY, MoveEntity.THE_BLINDING_ONE, MoveEntity.GMAX_DRUM_SOLO, MoveEntity.GMAX_FIREBALL, MoveEntity.GMAX_HYDROSNIPE);
        List<Ability> ignoreAbilityAbilities = List.of(Ability.MOLD_BREAKER, Ability.TURBOBLAZE, Ability.TERAVOLT); //TODO: Mycelium Might

        if(move.is(ignoreAbilityMoves))
            o.setAbilitiesIgnored(true);
        else if(ignoreAbilityAbilities.stream().anyMatch(c::hasAbility))
        {
            o.setAbilitiesIgnored(true);
            turnResult.add(ignoreAbilityAbilities.stream().filter(c::hasAbility).findFirst().get().formatActivation(c.getName(), o.getName() + "'s Abilities have been ignored!"));
        }

        //Special Move Logic
        if(this.data(this.current).perishSongTurns > 0)
        {
            this.data(this.current).perishSongTurns--;

            if(this.data(this.current).perishSongTurns <= 0)
            {
                this.data(this.current).perishSongTurns = 0;

                this.players[this.current].active.damage(this.players[this.current].active.getHealth());
                return String.join(", ", turnResult) + " Perish Song hit! " + this.players[this.current].active.getName() + " fainted!";
            }
        }

        if(this.data(this.current).futureSightUsed)
        {
            this.data(this.current).futureSightTurns--;

            if(this.data(this.current).futureSightTurns <= 0)
            {
                this.data(this.current).futureSightTurns = 0;
                this.data(this.current).futureSightUsed = false;

                int damage = new Move(MoveEntity.FUTURE_SIGHT).getDamage(this.players[this.current].active, this.players[this.other].active);
                this.players[this.other].active.damage(damage);

                turnResult.add("Future Sight landed and dealt **" + damage + "** damage to " + o.getName() + "!");
            }
        }

        if(this.data(this.current).doomDesireUsed)
        {
            this.data(this.current).doomDesireTurns--;

            if(this.data(this.current).doomDesireTurns <= 0)
            {
                this.data(this.current).doomDesireTurns = 0;
                this.data(this.current).doomDesireUsed = false;

                int damage = new Move(MoveEntity.DOOM_DESIRE).getDamage(this.players[this.current].active, this.players[this.other].active);
                this.players[this.other].active.damage(damage);

                turnResult.add("Doom Desire landed and dealt **" + damage + "** damage to " + o.getName() + "!");
            }
        }

        if(this.weather.get().equals(Weather.SANDSTORM) && o.hasAbility(Ability.SAND_VEIL))
        {
            move.setAccuracyMultiplier(4 / 5.);

            turnResult.add(Ability.SAND_VEIL.formatActivation(o.getName(), o.getName() + "'s Sand Veil tried to interrupt the move!"));
        }

        if(c.hasAbility(Ability.VICTORY_STAR))
        {
            boolean augment = c.hasAugment(PokemonAugment.SHINING_STAR);
            double multiplier = augment ? 1.3 : 1.1;
            move.setAccuracyMultiplier(multiplier);

            turnResult.add(Ability.VICTORY_STAR.formatActivation(c.getName(), "The accuracy of " + move.getName() + " was %s!".formatted(augment ? " greatly raised due to the " + PokemonAugment.SHINING_STAR.getAugmentName() + " augment!" : " raised!")));
        }

        if(c.hasAbility(Ability.HUSTLE) && move.is(Category.PHYSICAL) && !move.is(Move.OHKO_MOVES))
        {
            move.setAccuracyMultiplier(0.8);

            turnResult.add(Ability.HUSTLE.formatActivation(c.getName(), "The accuracy of " + move.getName() + " was lowered!"));
        }

        //Pre-Move Checks
        boolean accurate = move.isAccurate(this.players[this.current].active, this.players[this.other].active);
        boolean otherImmune = false;
        boolean cantUse = false;

        if(move.isZMove())
        {
            accurate = true;
            this.players[this.current].usedZMove = true;

            if(c.hasAugment(PokemonAugment.Z_AFFINITY)) move.setPower(2.0);
        }

        if(move.isMaxMove())
        {
            accurate = true;
            this.players[this.current].usedDynamax = true;
        }

        if(this.data(this.other).imprisonUsed && this.players[this.other].active.getMoves().contains(move.getEntity())) cantUse = true;

        if(move.is(MoveEntity.DEFENSE_CURL)) this.data(this.current).defenseCurlUsed = true;

        if(move.is(MoveEntity.ROLLOUT))
        {
            if(accurate) this.data(this.current).rolloutTurns++;
            else this.data(this.current).rolloutTurns = 1;

            move.setPower((this.data(this.current).defenseCurlUsed ? 2 : 1) * 30 * (int) Math.pow(2, this.data(this.current).rolloutTurns));
        }
        else this.data(this.current).rolloutTurns = 0;

        if(move.is(MoveEntity.ICE_BALL))
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

            if(move.is(Type.GROUND)) otherImmune = true;

            if(this.data(this.other).magnetRiseTurns <= 0)
            {
                this.data(this.other).magnetRiseTurns = 0;
            }
        }

        if(this.data(this.other).tauntTurns > 0)
        {
            this.data(this.other).tauntTurns--;

            if(move.is(Category.STATUS)) cantUse = true;

            if(this.data(this.other).tauntTurns <= 0)
            {
                this.data(this.other).tauntTurns = 0;
            }
        }

        if(this.data(this.other).waterSportUsed && move.getType().equals(Type.FIRE)) move.setPower(0.5);

        if(move.is(MoveEntity.SHEER_COLD))
        {
            move.setAccuracy((this.players[this.current].active.isType(Type.ICE) ? 30 : 20) + (this.players[this.current].active.getLevel() - this.players[this.other].active.getLevel()));
            accurate = move.isAccurate(this.players[this.current].active, this.players[this.other].active);
        }

        if(move.is(MoveEntity.FISSURE))
        {
            move.setAccuracy(20 + (this.players[this.current].active.getLevel() - this.players[this.other].active.getLevel()));
            accurate = move.isAccurate(this.players[this.current].active, this.players[this.other].active);
        }

        if(move.is(MoveEntity.HORN_DRILL))
        {
            move.setAccuracy(30 + (this.players[this.current].active.getLevel() - this.players[this.other].active.getLevel()));
            accurate = move.isAccurate(this.players[this.current].active, this.players[this.other].active);
        }

        if(move.is(MoveEntity.GUILLOTINE))
        {
            move.setAccuracy(30 + (this.players[this.current].active.getLevel() - this.players[this.other].active.getLevel()));
            accurate = move.isAccurate(this.players[this.current].active, this.players[this.other].active);
        }

        boolean bypass = (move.is(MoveEntity.PHANTOM_FORCE) && this.data(this.current).phantomForceUsed)
                || (move.is(MoveEntity.SHADOW_FORCE) && this.data(this.current).shadowForceUsed)
                || move.is(MoveEntity.FEINT, MoveEntity.GMAX_ONE_BLOW, MoveEntity.GMAX_BEFUDDLE);

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

            if(move.isContact())
            {
                this.players[this.current].active.changes().change(Stat.ATK, -2);
                otherImmune = !bypass;

                turnResult.add(this.players[this.current].active.getName() + "'s Attack was lowered by 2 stages due to the King's Shield!");
            }
        }

        if(this.data(this.other).banefulBunkerUsed)
        {
            this.data(this.other).banefulBunkerUsed = false;

            if(move.isContact())
            {
                this.players[this.current].active.addStatusCondition(StatusCondition.POISONED);
                otherImmune = !bypass;

                turnResult.add(this.players[this.current].active.getName() + " was poisoned due to the Baneful Bunker!");
            }
        }

        if(this.data(this.other).spikyShieldUsed)
        {
            this.data(this.other).spikyShieldUsed = false;

            if(move.isContact())
            {
                int damage = this.players[this.current].active.getStat(Stat.HP) / 8;
                this.players[this.current].active.damage(damage);

                otherImmune = !bypass;

                turnResult.add(this.players[this.current].active.getName() + " took " + damage + " damage due to the Spiky Shield!");
            }
        }

        if(this.data(this.other).obstructUsed)
        {
            this.data(this.other).obstructUsed = false;

            if(move.isContact())
            {
                this.players[this.current].active.changes().change(Stat.DEF, -2);
                otherImmune = !bypass;

                turnResult.add(this.players[this.current].active.getName() + "'s Defense was lowered by 2 stages due to the Obstruction!");
            }
        }

        if(o.hasAbility(Ability.STATIC) && move.isContact())
        {
            c.addStatusCondition(StatusCondition.PARALYZED);

            turnResult.add(Ability.STATIC.formatActivation(o.getName(), c.getName() + " is now paralyzed!"));
        }

        if(this.data(this.other).quickGuardUsed)
        {
            this.data(this.other).quickGuardUsed = false;

            if(move.getPriority() > 0) otherImmune = !bypass;
        }

        if(this.data(this.other).craftyShieldUsed)
        {
            this.data(this.other).craftyShieldUsed = false;

            EnumSet<MoveEntity> bypassStatusMoves = EnumSet.of(MoveEntity.PERISH_SONG, MoveEntity.SPIKES, MoveEntity.STEALTH_ROCK, MoveEntity.TOXIC_SPIKES, MoveEntity.STICKY_WEB);
            if(move.getCategory().equals(Category.STATUS) && move.is(bypassStatusMoves)) otherImmune = !bypass;
        }

        if(this.data(this.other).maxGuardUsed && !move.is(MoveEntity.GMAX_ONE_BLOW, MoveEntity.GMAX_RAPID_FLOW))
        {
            otherImmune = true;
        }

        if(move.is(MoveEntity.FUSION_BOLT) && !this.first.equals(c.getUUID())
                && this.players[this.other].move != null && this.players[this.other].move.is(MoveEntity.FUSION_FLARE)) move.setPower(move.getPower() * 2);

        if(move.is(MoveEntity.FUSION_FLARE) && !this.first.equals(c.getUUID())
                && this.players[this.other].move != null && this.players[this.other].move.is(MoveEntity.FUSION_BOLT)) move.setPower(move.getPower() * 2);

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

        if(move.is(MoveEntity.SMART_STRIKE))
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

            if(move.is(Move.SOUND_BASED_MOVES)) cantUse = true;

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

        if(this.data(this.current).flashFireActivated && move.is(Type.FIRE))
            move.setDamageMultiplier(1.5);

        EnumSet<MoveEntity> minimizeBoostMoves = EnumSet.of(MoveEntity.BODY_SLAM, MoveEntity.STOMP, MoveEntity.DRAGON_RUSH, MoveEntity.STEAMROLLER, MoveEntity.HEAT_CRASH, MoveEntity.HEAVY_SLAM, MoveEntity.FLYING_PRESS, MoveEntity.MALICIOUS_MOONSAULT, MoveEntity.DOUBLE_IRON_BASH, MoveEntity.QUADRUPLE_STEEL_SMASH);
        if(this.data(this.other).isMinimized && move.is(minimizeBoostMoves)) move.setPower(move.is(MoveEntity.QUADRUPLE_STEEL_SMASH) ? 4.0 : 2.0);

        //Fly, Bounce, Dig and Dive

        if(this.data(this.current).flyUsed) move = new Move(MoveEntity.FLY);

        if(this.data(this.current).bounceUsed) move = new Move(MoveEntity.BOUNCE);

        if(this.data(this.current).digUsed) move = new Move(MoveEntity.DIG);

        if(this.data(this.current).diveUsed) move = new Move(MoveEntity.DIVE);

        if(this.data(this.current).phantomForceUsed) move = new Move(MoveEntity.PHANTOM_FORCE);

        if(this.data(this.current).shadowForceUsed) move = new Move(MoveEntity.SHADOW_FORCE);

        EnumSet<MoveEntity> flyMoves = EnumSet.of(MoveEntity.GUST, MoveEntity.TWISTER, MoveEntity.THUNDER, MoveEntity.SKY_UPPERCUT, MoveEntity.SMACK_DOWN);
        EnumSet<MoveEntity> digMoves = EnumSet.of(MoveEntity.EARTHQUAKE, MoveEntity.MAGNITUDE, MoveEntity.FISSURE);
        EnumSet<MoveEntity> diveMoves = EnumSet.of(MoveEntity.SURF, MoveEntity.WHIRLPOOL, MoveEntity.LOW_KICK);

        if(     (this.data(this.other).flyUsed && !move.is(flyMoves))
                || (this.data(this.other).bounceUsed && !move.is(flyMoves))
                || (this.data(this.other).digUsed && !move.is(digMoves))
                || (this.data(this.other).diveUsed && !move.is(diveMoves))
                || this.data(this.other).phantomForceUsed || this.data(this.other).shadowForceUsed)
        {
            otherImmune = true;
        }

        //Two-Turn Charge Moves

        if(this.data(this.current).meteorBeamUsed) move = new Move(MoveEntity.METEOR_BEAM);

        if(this.data(this.current).solarBeamUsed) move = new Move(MoveEntity.SOLAR_BEAM);

        //Lowered Accuracy of Defensive Moves
        EnumSet<MoveEntity> defensiveMoves = EnumSet.of(MoveEntity.ENDURE, MoveEntity.PROTECT, MoveEntity.DETECT, MoveEntity.OBSTRUCT, MoveEntity.WIDE_GUARD, MoveEntity.QUICK_GUARD, MoveEntity.MAX_GUARD, MoveEntity.SPIKY_SHIELD, MoveEntity.KINGS_SHIELD, MoveEntity.BANEFUL_BUNKER);

        if(move.is(defensiveMoves) && !move.is(MoveEntity.QUICK_GUARD))
        {
            List<MoveEntity> log = this.movesUsed.get(this.players[this.current].active.getUUID());

            for(int i = log.size() - 1; i > 0 && defensiveMoves.contains(log.get(i)); i--)
                move.setAccuracy(move.getAccuracy() - (int)(move.getAccuracy() * 0.33));

            accurate = move.isAccurate(this.players[this.current].active, this.players[this.other].active);
        }

        //Ability: Soundproof
        if(o.hasAbility(Ability.SOUNDPROOF) && move.is(Move.SOUND_BASED_MOVES))
        {
            otherImmune = true;

            turnResult.add(Ability.SOUNDPROOF.formatActivation(o.getName(), o.getName() + " is immune to " + move.getName() + "!"));
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

        //Ability: Strong Jaw
        if(c.hasAbility(Ability.STRONG_JAW) && move.is(Move.BITING_MOVES))
        {
            move.setPower(1.5);

            turnResult.add(Ability.STRONG_JAW.formatActivation(c.getName(), move.getName() + "'s power was boosted by 50%!"));
        }

        //Ability: Sand Force
        if(c.hasAbility(Ability.SAND_FORCE) && this.weather.get().equals(Weather.SANDSTORM) && move.is(Type.ROCK, Type.GROUND, Type.STEEL))
        {
            move.setPower(1.3);

            turnResult.add(Ability.SAND_FORCE.formatActivation(c.getName(), move.getName() + "'s power was boosted by 30% from the Sandstorm!"));
        }

        //Ability: Torrent
        if(c.hasAbility(Ability.TORRENT) && move.is(Type.WATER) && c.getHealth() < c.getMaxHealth(1 / 3.))
        {
            move.setPower(1.5);

            turnResult.add(Ability.TORRENT.formatActivation(c.getName(), move.getName() + "'s power was boosted by 50%!"));
        }

        //Ability: Toxic Boost
        if(c.hasAbility(Ability.TOXIC_BOOST) && move.is(Category.PHYSICAL) && c.hasStatusCondition(StatusCondition.POISONED))
        {
            move.setPower(1.5);

            turnResult.add(Ability.TOXIC_BOOST.formatActivation(c.getName(), move.getName() + "'s power was boosted by 50% due to the poison!"));
        }

        //Ability: Mega Launcher
        if(c.hasAbility(Ability.MEGA_LAUNCHER) && move.is(Move.PULSE_MOVES) && !move.is(Category.STATUS))
        {
            if(move.is(MoveEntity.HEAL_PULSE))
                turnResult.add(Ability.MEGA_LAUNCHER.formatActivation(c.getName(), "Heal Pulse's Healing Power was boosted!"));
            else
            {
                move.setDamageMultiplier(1.5);

                turnResult.add(Ability.MEGA_LAUNCHER.formatActivation(c.getName(), move.getName() + "'s power was boosted by 50%!"));
            }
        }

        //Ability: Shadow Shield
        if(o.hasAbility(Ability.SHADOW_SHIELD) && o.getHealth() == o.getMaxHealth() && move.getPower() > 0)
        {
            move.setDamageMultiplier(0.5);

            turnResult.add(Ability.SHADOW_SHIELD.formatActivation(o.getName(), move.getName() + "'s damage was reduced by 50%!"));
        }

        //Ability: Bulletproof
        if(o.hasAbility(Ability.BULLETPROOF) && move.is(Move.BALL_AND_BOMB_MOVES))
        {
            otherImmune = true;

            turnResult.add(Ability.BULLETPROOF.formatActivation(o.getName(), move.getName() + " does not affect " + o.getName() + "!"));
        }

        //Ability: Fluffy
        if(o.hasAbility(Ability.FLUFFY))
        {
            if(move.isContact() && !move.is(Type.FIRE))
            {
                move.setDamageMultiplier(0.5);

                turnResult.add(Ability.FLUFFY.formatActivation(o.getName(), move.getName() + "'s damage was reduced by 50%!"));
            }

            if(move.is(Type.FIRE) && !move.isContact())
            {
                move.setDamageMultiplier(2.0);

                turnResult.add(Ability.FLUFFY.formatActivation(o.getName(), move.getName() + "'s damage was doubled!"));
            }
        }

        //Ability: Ice Scales
        if(o.hasAbility(Ability.ICE_SCALES))
        {
            if(move.is(Category.SPECIAL))
            {
                move.setDamageMultiplier(0.5);

                turnResult.add(Ability.ICE_SCALES.formatActivation(o.getName(), move.getName() + "'s damage was reduced by 50%!"));
            }
        }

        //Augment: Weighted Punch
        if(c.hasAugment(PokemonAugment.WEIGHTED_PUNCH) && move.is(Move.PUNCH_MOVES))
        {
            move.setPower(c.getWeight() / o.getWeight());

            turnResult.add(move.getName() + "'s power was amplified by the %s Augment!".formatted(PokemonAugment.WEIGHTED_PUNCH.getAugmentName()));
        }

        //Augment: Umbral Enhancements
        if(c.hasAugment(PokemonAugment.UMBRAL_ENHANCEMENTS) && move.is(Type.DARK) && !move.is(Category.STATUS))
        {
            boolean night = RegionManager.getCurrentTime().isNight();

            if(night) move.setPower(1.3);
            else move.setPower(0.9);

            turnResult.add(move.getName() + "'s power was " + (night ? "increased" : "reduced") + " by the %s Augment!".formatted(PokemonAugment.UMBRAL_ENHANCEMENTS.getAugmentName()));
        }

        //Augment: Swarm Collective
        if(c.hasAugment(PokemonAugment.SWARM_COLLECTIVE) && move.is(Type.BUG) && !move.is(Category.STATUS))
        {
            double modifier = 1.0;

            for(MoveEntity e : c.getMoves()) if(e.data().getType().equals(Type.BUG)) modifier += 0.05;
            for(Pokemon p : this.players[this.current].team) if(p.getType().get(0).equals(Type.BUG)) modifier += p.hasAugment(PokemonAugment.SWARM_COLLECTIVE) ? 0.25 : 0.1;

            if(modifier != 1.0)
            {
                move.setPower(modifier);

                turnResult.add(move.getName() + "'s power was amplified through the %s Augment!".formatted(PokemonAugment.SWARM_COLLECTIVE.getAugmentName()));
            }
        }

        //Augment: Heavyweight Bash
        if(c.hasAugment(PokemonAugment.HEAVYWEIGHT_BASH))
        {
            if(move.is(Type.ROCK) && move.getPower() >= 80)
            {
                move.setPower(move.getPower() + 60);

                turnResult.add(move.getName() + " became heavy due to the %s Augment!".formatted(PokemonAugment.HEAVYWEIGHT_BASH.getAugmentName()));
            }
            else if(!move.is(Type.ROCK) && move.getPower() > 20)
            {
                move.setPower(move.getPower() - 20);

                turnResult.add(move.getName() + " became lighter due to the %s Augment!".formatted(PokemonAugment.HEAVYWEIGHT_BASH.getAugmentName()));
            }
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

        double aguavSitrusHeal = 1 / 2.;
        double standardHeal = 1 / 3.;
        int standardStat = 1;
        int oranHeal = 10;
        int starfStat = 2;

        if(c.hasAbility(Ability.RIPEN))
        {
            aguavSitrusHeal *= 2;
            standardHeal *= 2;
            standardStat *= 2;
            oranHeal *= 2;
            starfStat *= 2;
        }

        if(!itemsOff && c.hasItem(Item.AGUAV_BERRY) && c.getHealth() < c.getMaxHealth(1 / 4.))
        {
            consumeBerry.accept("Aguav");

            turnResult.add(builder
                    .addFractionHealEffect(aguavSitrusHeal)
                    .addConditionalEffect(List.of(Nature.NAUGHTY, Nature.RASH, Nature.NAIVE, Nature.LAX).contains(c.getNature()), b -> b.addStatusEffect(StatusCondition.CONFUSED, 100, true))
                    .execute()
            );
        }

        if(!itemsOff && c.hasItem(Item.FIGY_BERRY) && c.getHealth() < c.getMaxHealth(1 / 4.))
        {
            consumeBerry.accept("Figy");

            turnResult.add(builder
                    .addFractionHealEffect(standardHeal)
                    .addConditionalEffect(List.of(Nature.MODEST, Nature.TIMID, Nature.CALM, Nature.BOLD).contains(c.getNature()), b -> b.addStatusEffect(StatusCondition.CONFUSED, 100, true))
                    .execute()
            );
        }

        if(!itemsOff && c.hasItem(Item.IAPAPA_BERRY) && c.getHealth() < c.getMaxHealth(1 / 4.))
        {
            consumeBerry.accept("Iapapa");

            turnResult.add(builder
                    .addFractionHealEffect(standardHeal)
                    .addConditionalEffect(List.of(Nature.LONELY, Nature.MILD, Nature.GENTLE, Nature.HASTY).contains(c.getNature()), b -> b.addStatusEffect(StatusCondition.CONFUSED, 100, true))
                    .execute()
            );
        }

        if(!itemsOff && c.hasItem(Item.MAGO_BERRY) && c.getHealth() < c.getMaxHealth(1 / 4.))
        {
            consumeBerry.accept("Mago");

            turnResult.add(builder
                    .addFractionHealEffect(standardHeal)
                    .addConditionalEffect(List.of(Nature.BRAVE, Nature.QUIET, Nature.SASSY, Nature.RELAXED).contains(c.getNature()), b -> b.addStatusEffect(StatusCondition.CONFUSED, 100, true))
                    .execute()
            );
        }

        if(!itemsOff && c.hasItem(Item.WIKI_BERRY) && c.getHealth() < c.getMaxHealth(1 / 4.))
        {
            consumeBerry.accept("Wiki");

            turnResult.add(builder
                    .addFractionHealEffect(standardHeal)
                    .addConditionalEffect(List.of(Nature.ADAMANT, Nature.JOLLY, Nature.CAREFUL, Nature.IMPISH).contains(c.getNature()), b -> b.addStatusEffect(StatusCondition.CONFUSED, 100, true))
                    .execute()
            );
        }

        if(!itemsOff && c.hasItem(Item.APICOT_BERRY) && c.getHealth() < c.getMaxHealth(1 / 4.))
        {
            consumeBerry.accept("Apicot");

            turnResult.add(builder.addStatChangeEffect(Stat.SPDEF, standardStat, 100, true).execute());
        }

        if(!itemsOff && c.hasItem(Item.GANLON_BERRY) && c.getHealth() < c.getMaxHealth(1 / 4.))
        {
            consumeBerry.accept("Ganlon");

            turnResult.add(builder.addStatChangeEffect(Stat.DEF, standardStat, 100, true).execute());
        }

        if(!itemsOff && c.hasItem(Item.LIECHI_BERRY) && c.getHealth() < c.getMaxHealth(1 / 4.))
        {
            consumeBerry.accept("Liechi");

            turnResult.add(builder.addStatChangeEffect(Stat.ATK, standardStat, 100, true).execute());
        }

        if(!itemsOff && c.hasItem(Item.MICLE_BERRY) && c.getHealth() < c.getMaxHealth(1 / 4.))
        {
            consumeBerry.accept("Micle");

            turnResult.add(builder.addAccuracyChangeEffect(standardStat, 100, true).execute());
        }

        if(!itemsOff && c.hasItem(Item.PETAYA_BERRY) && c.getHealth() < c.getMaxHealth(1 / 4.))
        {
            consumeBerry.accept("Petaya");

            turnResult.add(builder.addStatChangeEffect(Stat.SPATK, standardStat, 100, true).execute());
        }

        if(!itemsOff && c.hasItem(Item.SALAC_BERRY) && c.getHealth() < c.getMaxHealth(1 / 4.))
        {
            consumeBerry.accept("Salac");

            turnResult.add(builder.addStatChangeEffect(Stat.SPD, standardStat, 100, true).execute());
        }

        if(!itemsOff && c.hasItem(Item.ORAN_BERRY) && c.getHealth() < c.getMaxHealth(1 / 2.))
        {
            consumeBerry.accept("Oran");

            turnResult.add(builder.addFixedHealEffect(oranHeal).execute());
        }

        if(!itemsOff && c.hasItem(Item.SITRUS_BERRY) && c.getHealth() < c.getMaxHealth(1 / 2.))
        {
            consumeBerry.accept("Sitrus");

            turnResult.add(builder.addFractionHealEffect(aguavSitrusHeal).execute());
        }

        if(!itemsOff && c.hasItem(Item.STARF_BERRY) && c.getHealth() < c.getMaxHealth(1 / 4.))
        {
            consumeBerry.accept("Starf");

            int num = new SplittableRandom().nextInt(Stat.values().length + 2);

            if(num < Stat.values().length) turnResult.add(builder.addStatChangeEffect(Stat.values()[num], starfStat, 100, true).execute());
            else if(num == Stat.values().length) turnResult.add(builder.addAccuracyChangeEffect(starfStat, 100, true).execute());
            else if(num == Stat.values().length + 1) turnResult.add(builder.addEvasionChangeEffect(starfStat, 100, true).execute());
        }

        //Barrier Effects
        if(!bypass && this.barriers[this.other].has(FieldBarrier.AURORA_VEIL))
        {
            if(move.is(Category.SPECIAL, Category.PHYSICAL)) move.setDamageMultiplier(0.5);
        }

        if(!bypass && this.barriers[this.other].has(FieldBarrier.REFLECT))
        {
            if(move.is(Category.PHYSICAL)) move.setDamageMultiplier(0.5);
        }

        if(!bypass && this.barriers[this.other].has(FieldBarrier.LIGHT_SCREEN))
        {
            if(move.is(Category.SPECIAL)) move.setDamageMultiplier(0.5);
        }

        //Ability: Volt Absorb
        if(!otherImmune && o.hasAbility(Ability.VOLT_ABSORB) && move.is(Type.ELECTRIC))
        {
            int health = o.getMaxHealth(1 / 4.);
            if(!move.is(Category.STATUS)) o.heal(health);

            otherImmune = true;

            turnResult.add(Ability.VOLT_ABSORB.formatActivation(o.getName(), move.getName() + " has no affect on " + o.getName() + "!" + (!move.is(Category.STATUS) ? " " + o.getName() + " healed for " + health + " HP!" : "")));
        }

        //Main Results
        String name = this.players[this.current].active.getName();
        EnumSet<MoveEntity> rechargeMoves = EnumSet.of(MoveEntity.HYPER_BEAM, MoveEntity.BLAST_BURN, MoveEntity.HYDRO_CANNON, MoveEntity.FRENZY_PLANT, MoveEntity.ROAR_OF_TIME, MoveEntity.PRISMATIC_LASER, MoveEntity.ETERNABEAM, MoveEntity.GIGA_IMPACT, MoveEntity.METEOR_ASSAULT, MoveEntity.ROCK_WRECKER);

        //Ensures the recharge occurs when the recharge move isn't used
        if(this.data(this.current).recharge && !move.is(rechargeMoves)) this.data(this.current).recharge = false;

        //Raised Immunity
        if(this.data(this.other).isRaised && move.getType().equals(Type.GROUND)) otherImmune = true;

        //Hyperspace Fury and Hyperspace Hole bypass immunities and always hit
        if(move.is(MoveEntity.HYPERSPACE_FURY, MoveEntity.HYPERSPACE_HOLE))
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
            //TODO: Rethink this form change
            if(this.players[this.current].active.is(PokemonEntity.AEGISLASH_SHIELD))
            {
                this.players[this.current].active.changePokemon(PokemonEntity.AEGISLASH_BLADE);
                this.players[this.current].active.updateEntity();
            }
        }

        boolean isMoveSuccess = false;

        //Focus Punch
        if(move.is(MoveEntity.FOCUS_PUNCH) && this.data(this.current).isFocusPunchFailed)
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
        else if(this.data(this.current).isTormented && this.getLastUsedMove(this.players[this.current].active.getUUID()) != null && this.getLastUsedMove(this.players[this.current].active.getUUID()).equals(move.getEntity()))
        {
            turnResult.add(name + " can't use " + move.getName() + " due to Torment!");

            this.data(this.other).lastDamageTaken = 0;
        }
        //Check if user has to recharge
        else if(this.data(this.current).recharge && move.is(rechargeMoves))
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
        else if(o.hasAbility(Ability.LIGHTNING_ROD) && move.is(Type.ELECTRIC) && !move.is(Category.STATUS) && !move.is(MoveEntity.JUDGMENT, MoveEntity.NATURAL_GIFT, MoveEntity.HIDDEN_POWER))
        {
            turnResult.add(Ability.LIGHTNING_ROD.formatActivation(o.getName(), o.getName() + " was immune to the attack, and its Special Attack rose by 1 stage!"));

            o.changes().change(Stat.SPATK, 1);
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

            if(move.is(MoveEntity.JUMP_KICK, MoveEntity.HIGH_JUMP_KICK))
            {
                turnResult.add(" " + this.players[this.current].active.getName() + " kept going and crashed!");

                this.players[this.current].active.damage(this.players[this.current].active.getStat(Stat.HP) / 2);
            }

            //Augment: V Rush
            if(c.hasAugment(PokemonAugment.V_RUSH))
            {
                c.changes().change(Stat.ATK, 1);
                c.changes().change(Stat.SPATK, 1);

                turnResult.add(c.getName() + "'s Attack and Special Attack rose by 1 stage, due to the %s Augment!".formatted(PokemonAugment.V_RUSH.getAugmentName()));
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

            turnResult.add(Ability.DISGUISE.formatActivation(o.getName(), "The Disguise absorbed the attack!"));
        }
        //Ability: Damp
        else if((c.hasAbility(Ability.DAMP) || o.hasAbility(Ability.DAMP)) && move.is(MoveEntity.EXPLOSION, MoveEntity.SELF_DESTRUCT, MoveEntity.MIND_BLOWN, MoveEntity.MISTY_EXPLOSION))
        {
            turnResult.add(Ability.DAMP.formatActivation((c.hasAbility(Ability.DAMP) ? c : o).getName(), move.getName() + " failed!"));
        }
        //Ability: Flash Fire
        else if(o.hasAbility(Ability.FLASH_FIRE) && move.is(Type.FIRE))
        {
            this.data(o.getUUID()).flashFireActivated = true;

            turnResult.add(Ability.FLASH_FIRE.formatActivation(o.getName(), o.getName() + " is immune to the attack! The power of its Fire-type moves is boosted by 50%!"));
        }
        //Ability: Flash Fire
        else if(o.hasAbility(Ability.SAP_SIPPER) && move.is(Type.GRASS) && !move.is(MoveEntity.AROMATHERAPY))
        {
            o.changes().change(Stat.ATK, 1);

            turnResult.add(Ability.SAP_SIPPER.formatActivation(o.getName(), o.getName() + " is immune to the move! It's Attack was raised by 1 stage!"));
        }
        //Augment: Pinnacle Evasion
        else if(o.hasAugment(PokemonAugment.PINNACLE_EVASION) && new Random().nextFloat() < 0.05)
        {
            turnResult.add(o.getName() + " evaded the attack, due to the %s Augment!".formatted(PokemonAugment.PINNACLE_EVASION.getAugmentName()));
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

                moveCopy.setDamageMultiplier(o.hasAbility(Ability.RIPEN) ? 0.25 : 0.5);

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

            //Ability: Multiscale
            if(o.hasAbility(Ability.MULTISCALE) && o.getHealth() == o.getMaxHealth() && !move.is(Move.DIRECT_DAMAGE_MOVES))
                move.setDamageMultiplier(0.5);

            //Augment: True Strike
            boolean isTrueStrikeValid = c.hasAugment(PokemonAugment.TRUE_STRIKE) && move.is(Category.PHYSICAL) && move.is(Type.FIGHTING) && move.getPower() > 50;
            if(isTrueStrikeValid) move.setPower(move.getPower() - 30);

            //Augment: Grounded Empowerment
            if(c.hasAugment(PokemonAugment.GROUNDED_EMPOWERMENT) && move.is(Type.GROUND))
            {
                if(c.getWeight() > o.getWeight())
                {
                    move.setPower(1.3);
                    turnResult.add(move.getName() + "'s was empowered by %s's %s Augment!".formatted(c.getName(), PokemonAugment.GROUNDED_EMPOWERMENT.getAugmentName()));
                }
                else
                {
                    c.changes().change(Stat.SPD, -1);
                    turnResult.add(c.getName() + "'s Speed was reduced from the %s Augment!".formatted(PokemonAugment.GROUNDED_EMPOWERMENT.getAugmentName()));
                }
            }

            //Augment: Flowering Grace
            boolean isValidFloweringGrace = c.hasAugment(PokemonAugment.FLOWERING_GRACE) && move.is(Category.SPECIAL) && move.is(Type.FAIRY) && move.getPower() > 40;
            if(isValidFloweringGrace) move.setPower(move.getPower() - 40);

            //Augment: Final Resort V
            boolean isFinalResortVActivated = c.hasAugment(PokemonAugment.FINAL_RESORT_V) && move.is(MoveEntity.V_CREATE) && c.getHealth() < c.getMaxHealth(1 / 10.);
            if(isFinalResortVActivated)
            {
                move.setPower(2.0);
            }

            //Primary Move Logic

            turnResult.add(move.logic(this.players[this.current].active, this.players[this.other].active, this));

            //Post-Move Execution

            if(move.getCategory().equals(Category.STATUS)) this.data(this.other).lastDamageTaken = 0;

            if(move.is(rechargeMoves)) this.data(this.current).recharge = true;

            if(this.players[this.other].active.hasAbility(Ability.IRON_BARBS) && !move.getCategory().equals(Category.STATUS) && this.data(this.other).lastDamageTaken > 0)
            {
                int dmg = this.players[this.current].active.getStat(Stat.HP) / 8;
                this.players[this.current].active.damage(dmg);

                turnResult.add(this.players[this.current].active.getName() + " took " + dmg + " damage from the Iron Barbs!");
            }

            if(o.hasAbility(Ability.WEAK_ARMOR) && move.is(Category.PHYSICAL))
            {
                o.changes().change(Stat.DEF, -1);
                o.changes().change(Stat.SPD, 1);

                turnResult.add(Ability.WEAK_ARMOR.formatActivation(o.getName(), o.getName() + "'s Speed rose by 1 stage, and its Defense was lowered by 1 stage!"));
            }

            if(o.hasAbility(Ability.JUSTIFIED) && move.is(Type.DARK))
            {
                o.changes().change(Stat.ATK, 1);

                turnResult.add(Ability.JUSTIFIED.formatActivation(o.getName(), o.getName() + "'s Attack rose by 1 stage!"));
            }

            if(o.hasAbility(Ability.ANGER_POINT) && move.hitCrit)
            {
                o.changes().change(Stat.ATK, 12);

                turnResult.add(Ability.ANGER_POINT.formatActivation(o.getName(), o.getName() + "'s Attack was maximized!"));
            }

            if(o.hasAbility(Ability.COLOR_CHANGE) && !o.isType(move.getType()))
            {
                o.setType(move.getType());

                turnResult.add(Ability.COLOR_CHANGE.formatActivation(o.getName(), o.getName() + "'s Type was changed to " + move.getType().getStyledName() + "!"));
            }

            if(c.hasAbility(Ability.POISON_TOUCH) && move.isContact() && new Random().nextInt(100) < 30 && !o.hasStatusCondition(StatusCondition.POISONED))
            {
                o.addStatusCondition(StatusCondition.POISONED);

                turnResult.add(Ability.POISON_TOUCH.formatActivation(c.getName(), o.getName() + " was poisoned!"));
            }

            if((c.hasItem(Item.KINGS_ROCK) || c.hasItem(Item.RAZOR_FANG)) && !move.is(Category.STATUS) && !o.hasStatusCondition(StatusCondition.FLINCHED))
            {
                int percent = 10;
                if(c.hasAbility(Ability.SERENE_GRACE))
                {
                    percent *= 2;

                    turnResult.add(Ability.SERENE_GRACE.formatActivation(c.getName(), move.getName() + " has been graced!"));
                }

                if(new Random().nextInt(100) < percent)
                {
                    o.addStatusCondition(StatusCondition.FLINCHED);

                    turnResult.add(c.getName() + "'s " + c.getItem().getStyledName() + " activated! " + o.getName() + " flinched!");
                }
            }

            if(o.hasAbility(Ability.STEAM_ENGINE) && move.is(Type.FIRE, Type.WATER))
            {
                o.changes().change(Stat.SPD, 6);

                turnResult.add(Ability.STEAM_ENGINE.formatActivation(o.getName(), o.getName() + "'s Speed was increased by 6 stages!"));
            }

            if(c.hasAugment(PokemonAugment.SHADOW_PROPULSION) && move.is(Type.GHOST) && new Random().nextFloat() < 0.33F)
            {
                c.changes().change(Stat.SPD, -1);

                turnResult.add(c.getName() + "'s Speed rose by 1 stage due to the %s Augment!".formatted(PokemonAugment.SHADOW_PROPULSION.getAugmentName()));
            }

            //Augment: Searing Shot
            if(c.hasAugment(PokemonAugment.SEARING_SHOT) && move.is(Type.FIRE) && !o.hasStatusCondition(StatusCondition.BURNED) && new Random().nextFloat() < 0.05F)
            {
                o.addStatusCondition(StatusCondition.BURNED);

                turnResult.add(o.getName() + " is now burned, due to the %s Augment!".formatted(PokemonAugment.SEARING_SHOT.getAugmentName()));
            }

            //Augment: Aerial Evasion
            if(c.hasAugment(PokemonAugment.AERIAL_EVASION) && move.is(Type.FLYING))
            {
                c.changes().changeEvasion(1);

                turnResult.add(c.getName() + "'s Evasion was increased by 1 stage due to its %s Augment!".formatted(PokemonAugment.AERIAL_EVASION.getAugmentName()));
            }

            //Augment: Phase Shifter
            if(c.hasAugment(PokemonAugment.PHASE_SHIFTER) && move.is(Type.GHOST) && new Random().nextFloat() < 0.2F)
            {
                c.changes().changeEvasion(4);

                turnResult.add(c.getName() + "'s Evasion was increased greatly due to its %s Augment!".formatted(PokemonAugment.PHASE_SHIFTER.getAugmentName()));
            }
            if(o.hasAugment(PokemonAugment.PHASE_SHIFTER) && move.is(Type.NORMAL) && new Random().nextFloat() < 0.1F)
            {
                c.damage(10);

                turnResult.add(c.getName() + " took 10 true damage due to %s's %s Augment!".formatted(o.getName(), PokemonAugment.PHASE_SHIFTER.getAugmentName()));
            }

            int damageDealt = preMoveHP - this.players[this.other].active.getHealth();

            if(damageDealt > 0 && this.players[this.current] instanceof UserPlayer) this.damageDealt.put(this.players[this.current].active.getUUID(), this.damageDealt.getOrDefault(this.players[this.current].active.getUUID(), 0) + damageDealt);

            if(this.data(this.other).bideTurns > 0)
            {
                this.data(this.other).bideTurns--;

                this.data(this.other).bideDamage += Math.max(damageDealt, 0);
            }

            if(move.isContact()) this.data(this.other).isFocusPunchFailed = true;

            if(damageDealt > 0 && c.hasAugment(PokemonAugment.STATIC) && move.is(Type.ELECTRIC) && this.players[this.other].team.size() > 1 && new Random().nextFloat() < 0.1F)
            {
                int randomIndex = new Random().nextInt(this.players[this.other].team.size());
                Pokemon target = this.players[this.other].team.get(randomIndex);

                int targetDamage = (int)(damageDealt * 0.15F);
                target.damage(targetDamage);

                turnResult.add(target.getName() + " took " + targetDamage + " damage due to the %s Augment!".formatted(PokemonAugment.STATIC.getAugmentName()));
            }

            if(c.hasAugment(PokemonAugment.ICY_AURA) && move.is(Type.ICE) && new Random().nextFloat() < (this.weather.get().equals(Weather.HAIL) ? 0.8F : 0.2F))
            {
                o.changes().change(Stat.SPD, -1);

                turnResult.add(o.getName() + "'s Speed was lowered by 1 stage, due to " + c.getName() + "'s " + PokemonAugment.ICY_AURA.getAugmentName() + " Augment!");
            }

            if(isTrueStrikeValid && damageDealt > 0)
            {
                float r = new Random().nextFloat();
                int trueDamage;

                if(r < 0.2F) trueDamage = 30;
                else if(r < 0.5F) trueDamage = 20;
                else trueDamage = 10;

                o.damage(trueDamage);

                turnResult.add(move.getName() + " dealt an additional " + trueDamage + " true damage, due to the " + PokemonAugment.TRUE_STRIKE.getAugmentName() + " Augment!");
            }

            if(move.is(Category.PHYSICAL) && c.hasAugment(PokemonAugment.PLATED_ARMOR) && damageDealt > 0)
            {
                c.changes().change(Stat.DEF, 1);

                turnResult.add(c.getName() + "'s Defense rose by 1 stage due to the " + PokemonAugment.PLATED_ARMOR.getAugmentName() + " Augment!");
            }

            //Augment: Flowering Grace
            if(isValidFloweringGrace && damageDealt > 0)
            {
                int HP = 40;
                for(MoveEntity e : c.getMoves()) if(e.data().getType().equals(Type.FAIRY)) HP += 15;
                for(MoveEntity e : o.getMoves()) if(e.data().getType().equals(Type.FAIRY)) HP += 15;

                c.heal(HP);

                turnResult.add(c.getName() + " healed for " + HP + " due to the " + PokemonAugment.FLOWERING_GRACE.getAugmentName() + " Augment!");
            }

            //Augment: Drench
            if(c.hasAugment(PokemonAugment.DRENCH) && move.is(Type.WATER) && new Random().nextFloat() < 0.05)
            {
                o.changes().change(Stat.SPD, -2);
                o.changes().changeEvasion(-1);
                o.changes().changeAccuracy(-1);

                turnResult.add(o.getName() + "'s Speed was lowered by 2 stages, and its Evasion and Accuracy were reduced by 1 stage, due to " + c.getName() + "'s " + PokemonAugment.DRENCH.getAugmentName() + " Augment!");
            }

            //Augment: Standardization
            if(c.hasAugment(PokemonAugment.STANDARDIZATION) && move.is(Type.NORMAL) && new Random().nextFloat() < 0.1)
            {
                List<Stat> valid = new ArrayList<>();
                for(Stat s : Stat.values()) if(c.changes().get(s) < 0) valid.add(s);

                if(!valid.isEmpty())
                {
                    Stat s = valid.get(new Random().nextInt(valid.size()));
                    int value = c.changes().get(s);
                    c.changes().change(s, value * -1);

                    turnResult.add(c.getName() + "'s negative " + s.toString() + " modifiers were removed due to the " + PokemonAugment.STANDARDIZATION.getAugmentName() + " Augment!");
                }
            }

            //Augment: Final Resort V
            if(isFinalResortVActivated)
            {
                c.changes().change(Stat.DEF, -6);
                c.changes().change(Stat.SPDEF, -6);

                turnResult.add(c.getName() + "'s Defense and Special Defense were lowered by 6 stages due to the " + PokemonAugment.FINAL_RESORT_V.getAugmentName() + " Augment!");
            }

            //Berry Effects - Post-Move Damage Dealt

            if(damageDealt > 0)
            {
                int ripenBoost = o.hasAbility(Ability.RIPEN) ? 2 : 1;

                if(o.hasItem(Item.ENIGMA_BERRY) && TypeEffectiveness.getEffectiveness(o.getType()).get(move.getType()) > 1.0)
                {
                    int amount = o.getMaxHealth(1 / 4. * ripenBoost);

                    o.heal(amount);
                    o.removeItem();

                    turnResult.add(o.getName() + " consumed its Enigma Berry! It healed for " + amount + " HP!");
                }

                if(o.hasItem(Item.JABOCA_BERRY) && move.is(Category.PHYSICAL))
                {
                    int amount = c.getMaxHealth(1 / 8. * ripenBoost);

                    c.damage(amount);
                    o.removeItem();

                    turnResult.add(o.getName() + " consumed its Jaboca Berry! " + c.getName() + " took " + amount + " recoil damage!");
                }

                if(o.hasItem(Item.ROWAP_BERRY) && move.is(Category.SPECIAL))
                {
                    int amount = c.getMaxHealth(1 / 8. * ripenBoost);

                    c.damage(amount);
                    o.removeItem();

                    turnResult.add(o.getName() + " consumed its Rowap Berry! " + c.getName() + " took " + amount + " recoil damage!");
                }

                if(o.hasItem(Item.KEE_BERRY) && move.is(Category.PHYSICAL))
                {
                    o.changes().change(Stat.DEF, 1 * ripenBoost);
                    o.removeItem();

                    turnResult.add(o.getName() + " consumed its Kee Berry! Its Defense rose by 1 stage!");
                }

                if(o.hasItem(Item.MARANGA_BERRY) && move.is(Category.PHYSICAL))
                {
                    o.changes().change(Stat.SPDEF, 1 * ripenBoost);
                    o.removeItem();

                    turnResult.add(o.getName() + " consumed its Maranga Berry! Its Special Defense rose by 1 stage!");
                }
            }

            //Other Stuff

            //Ability: Cotton Down
            if(o.hasAbility(Ability.COTTON_DOWN) && damageDealt > 0)
            {
                c.changes().change(Stat.SPD, -1);

                turnResult.add(Ability.COTTON_DOWN.formatActivation(o.getName(), c.getName() + "'s Speed decreased by 1 stage!"));
            }

            if(damageDealt > 0 && this.players[this.current] instanceof UserPlayer player)
            {
                final Move m = move;
                player.data.updateBountyProgression(b -> {
                    switch(b.getType()) {
                        case DAMAGE_POKEMON -> b.update(damageDealt);
                        case DAMAGE_POKEMON_TYPE -> b.updateIf(this.players[this.other].active.isType(b.getObjective().asTypeObjective().getType()), damageDealt);
                        case DAMAGE_POKEMON_CATEGORY -> b.updateIf(b.getObjective().asCategoryObjective().getCategory().equals(m.getCategory()), damageDealt);
                    }
                });
            }

            if(this.players[this.current] instanceof UserPlayer player)
            {
                final Move m = move;
                player.data.updateBountyProgression(b -> {
                    switch(b.getType()) {
                        case USE_MOVES -> b.update();
                        case USE_MOVES_TYPE -> b.updateIf(b.getObjective().asTypeObjective().getType().equals(m.getType()));
                        case USE_MOVES_CATEGORY -> b.updateIf(b.getObjective().asCategoryObjective().getCategory().equals(m.getCategory()));
                        case USE_MOVES_NAME -> b.updateIf(b.getObjective().asNameObjective().getName().equals(m.getEntity().toString()));
                        case USE_MOVES_POOL -> b.updateIf(b.getObjective().asPoolObjective().getPool().contains(m.getEntity().toString()));
                        case USE_MOVES_POWER_LESS -> b.updateIf(m.getPower() < b.getObjective().asPowerObjective().getPower());
                        case USE_MOVES_POWER_GREATER -> b.updateIf(m.getPower() > b.getObjective().asPowerObjective().getPower());
                        case USE_MOVES_ACCURACY_LESS -> b.updateIf(m.getAccuracy() < b.getObjective().asAccuracyObjective().getAccuracy());
                        case USE_MOVES_PRIORITY_HIGH -> b.updateIf(m.getPriority() > 0);
                        case USE_MOVES_PRIORITY_LOW -> b.updateIf(m.getPriority() < 0);
                        case USE_ZMOVE -> b.updateIf(m.isZMove());
                        case USE_ZMOVE_TYPE -> b.updateIf(m.isZMove() && b.getObjective().asTypeObjective().getType().equals(m.getType()));
                        case USE_MAX_MOVE -> b.updateIf(m.isMaxMove());
                        case USE_MAX_MOVE_TYPE -> b.updateIf(m.isMaxMove() && b.getObjective().asTypeObjective().getType().equals(m.getType()));
                    }
                });
            }
        }

        //Post-Move Updates

        if(!isMoveSuccess || (this.data(this.current).furyCutterUsed && !move.is(MoveEntity.FURY_CUTTER)))
        {
            this.data(this.current).furyCutterUsed = false;
            this.data(this.current).furyCutterTurns = 0;
        }

        if(c.hasAbility(Ability.SPEED_BOOST))
        {
            this.players[this.current].active.changes().change(Stat.SPD, 1);

            turnResult.add(Ability.SPEED_BOOST.formatActivation(c.getName(), c.getName() + "'s Speed rose by 1 stage!"));
        }

        if(this.players[this.other].team.size() > 1)
        {
            List<String> heals = new ArrayList<>();
            Random r = new Random();

            for(Pokemon p : this.players[this.other].team) if(p.hasAugment(PokemonAugment.FLORAL_HEALING) && r.nextFloat() < 0.1F)
            {
                int healAmount = p.getMaxHealth(0.15F);

                p.heal(healAmount);
                heals.add(p.getName() + " healed for " + healAmount + "HP!");
            }

            if(!heals.isEmpty()) turnResult.add("\n" + PokemonAugment.FLORAL_HEALING.getAugmentName() + " Augment: " + String.join(" ", heals) + "\n");
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
        this.movesUsed.get(this.players[this.current].active.getUUID()).add(move.getEntity());

        //Give EVs and EXP if opponent has fainted
        if(this.players[this.other].active.isFainted())
        {
            this.players[this.current].active.gainEVs(this.players[this.other].active);

            String UUID = this.players[this.current].active.getUUID();
            int exp = this.players[this.current].active.getDefeatExp(this.players[this.other].active);
            this.expGains.put(UUID, (this.expGains.getOrDefault(UUID, 0)) + exp);

            if(this.players[this.current] instanceof UserPlayer player)
            {
                player.data.updateBountyProgression(b -> {
                    switch(b.getType())
                    {
                        case DEFEAT_POKEMON -> b.update();
                        case DEFEAT_POKEMON_TYPE -> {
                            if(this.players[this.other].active.isType(b.getObjective().asTypeObjective().getType())) b.update();
                        }
                        case DEFEAT_POKEMON_POOL -> {
                            if(b.getObjective().asPoolObjective().getPool().contains(this.players[this.other].active.getEntity().toString())) b.update();
                        }
                        case DEFEAT_LEGENDARY -> {
                            PokemonEntity otherName = this.players[this.other].active.getEntity();
                            if(EnumSet.of(PokemonRarity.Rarity.LEGENDARY, PokemonRarity.Rarity.MYTHICAL, PokemonRarity.Rarity.ULTRA_BEAST).contains(otherName.getRarity())) b.update();
                        }
                        case EARN_EVS -> b.update(this.players[this.other].active.getEVYield().values().stream().mapToInt(e -> e).sum());
                        case EARN_EVS_STAT -> {
                            int statYield = this.players[this.other].active.getEVYield().get(b.getObjective().asStatObjective().getStat());
                            if(statYield != 0) b.update(statYield);
                        }
                    }
                });

                player.data.getStatistics().incr(PlayerStatistic.POKEMON_DEFEATED);

                //General Augments
                boolean augmentEarned = false;

                if(random.nextFloat() < 0.1F)
                {
                    List<PokemonAugment> pool = new ArrayList<>(List.of(PokemonAugment.SUPERCHARGED, PokemonAugment.SUPERFORTIFIED, PokemonAugment.HARMONY, PokemonAugment.PINNACLE_EVASION, PokemonAugment.PRECISION_STRIKES, PokemonAugment.PRECISION_BURST, PokemonAugment.RAW_FORCE, PokemonAugment.MODIFYING_FORCE));
                    pool.removeIf(pa -> player.data.isAugmentUnlocked(pa.getAugmentID()));

                    if(!pool.isEmpty())
                    {
                        Collections.shuffle(pool);

                        PokemonAugment chosen = pool.get(0);
                        player.data.addAugment(chosen.getAugmentID());

                        augmentEarned = true;
                        turnResult.add(player.data.getUsername() + " has found an Augment! They earned: " + chosen.getAugmentName());
                    }
                }

                if(!augmentEarned && random.nextFloat() < 0.05F)
                {
                    PokemonAugment typeAugment = switch(move.getType()) {
                        case NORMAL -> PokemonAugment.STANDARDIZATION;
                        case FIRE -> PokemonAugment.SEARING_SHOT;
                        case WATER -> PokemonAugment.DRENCH;
                        case ELECTRIC -> PokemonAugment.STATIC;
                        case GRASS -> PokemonAugment.FLORAL_HEALING;
                        case ICE -> PokemonAugment.ICY_AURA;
                        case FIGHTING -> PokemonAugment.TRUE_STRIKE;
                        case POISON -> PokemonAugment.POISONOUS_SINGE;
                        case GROUND -> PokemonAugment.GROUNDED_EMPOWERMENT;
                        case FLYING -> PokemonAugment.AERIAL_EVASION;
                        case PSYCHIC -> PokemonAugment.SURE_SHOT;
                        case BUG -> PokemonAugment.SWARM_COLLECTIVE;
                        case ROCK -> PokemonAugment.HEAVYWEIGHT_BASH;
                        case GHOST -> PokemonAugment.PHASE_SHIFTER;
                        case DRAGON -> PokemonAugment.DRACONIC_ENRAGE;
                        case DARK -> PokemonAugment.UMBRAL_ENHANCEMENTS;
                        case STEEL -> PokemonAugment.PLATED_ARMOR;
                        case FAIRY -> PokemonAugment.FLOWERING_GRACE;
                    };

                    if(!player.data.isAugmentUnlocked(typeAugment.getAugmentID()))
                    {
                        player.data.addAugment(typeAugment.getAugmentID());

                        augmentEarned = true;

                        turnResult.add(player.data.getUsername() + " has found an Augment! They earned: " + typeAugment.getAugmentName());
                    }
                }
            }

            if(this.players[this.other] instanceof UserPlayer player) player.data.getStatistics().incr(PlayerStatistic.POKEMON_FAINTED);

            if(this.players[this.current].active.isDynamaxed() && this.players[this.current].active.getDynamaxLevel() < 10 && new Random().nextInt(100) < 40)
            {
                this.players[this.current].active.increaseDynamaxLevel();

                if(this.players[this.current] instanceof UserPlayer player)
                {
                    this.players[this.current].active.updateDynamaxLevel();

                    player.data.directMessage(this.players[this.current].active.getName() + " earned a Dynamax Level!");
                }
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

            //Augment: Victory Resolve
            if(c.hasAugment(PokemonAugment.VICTORY_RESOLVE))
            {
                int health = c.getHealth() / 2;
                c.heal(health);
                c.clearStatusConditions();

                turnResult.add(c.getName() + " healed for %s HP and was cured of all Status Conditions, due to the %s Augment!".formatted(health, PokemonAugment.VICTORY_RESOLVE.getAugmentName()));
            }

            //Augment: Victory Ensured
            if(o.hasAugment(PokemonAugment.VICTORY_ENSURED))
            {
                int damage = c.getHealth() / 2;
                c.damage(damage);

                turnResult.add(c.getName() + " took " + damage + " damage, due to the %s Augment!".formatted(PokemonAugment.VICTORY_ENSURED.getAugmentName()));
            }

            if(this.data(this.other).destinyBondUsed)
            {
                this.data(this.other).destinyBondUsed = false;

                this.players[this.current].active.damage(this.players[this.current].active.getHealth());
                turnResult.add(this.players[this.current].active.getName() + " fainted from the Destiny Bond!");
            }
        }

        //Reset Ignored Abilities
        o.setAbilitiesIgnored(false);

        //Reset first swap flag
        this.data(c.getUUID()).firstAfterSwap = false;

        if(this.first.equals(this.players[this.current].active.getUUID())) turnResult.add("\n\n");

        return String.join(" ", turnResult);
    }

    //Turn Helper Methods
    public void setDefaults()
    {
        //Queued Moves
        this.queuedMoves = new HashMap<>();

        //Global (Non-Sided) Effects: Weather, Terrain, Room
        this.weather = new WeatherHandler();
        this.terrain = new TerrainHandler();
        this.room = new RoomHandler();

        //Field Effects: Entry Hazards, Barriers, Damage over Time from G-Max, Misc. Field Effects
        this.entryHazards = new EntryHazardHandler[this.players.length];
        this.barriers = new FieldBarrierHandler[this.players.length];
        this.gmaxDoT = new FieldGMaxDoTHandler[this.players.length];
        this.fieldEffects = new FieldEffectsHandler[this.players.length];

        this.iteratePlayers(i -> {
            this.entryHazards[i] = new EntryHazardHandler();
            this.barriers[i] = new FieldBarrierHandler();
            this.gmaxDoT[i] = new FieldGMaxDoTHandler();
            this.fieldEffects[i] = new FieldEffectsHandler();
        });

        //Set Default Move Logs
        for(Player p : this.players) for(Pokemon pokemon : p.team) this.movesUsed.put(pokemon.getUUID(), new ArrayList<>());

        //Set every Pokemon's Health to their Max
        for(Player player : this.players) for(int i = 0; i < player.team.size(); i++) player.team.get(i).setHealth(player.team.get(i).getMaxHealth());
    }

    public void checkDynamax(int p)
    {
        if(this.players[p].active.isDynamaxed())
        {
            this.players[p].dynamaxTurns--;

            if(this.players[p].active.isFainted()) this.players[p].dynamaxTurns = 0;
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
        StringJoiner weatherResult = new StringJoiner("\n");
        this.iteratePlayers(i -> this.checkWeatherEffects(weatherResult, i));

        this.results.add("\n\n\n" + this.weather.get().getStatus() + "\n" + weatherResult);
    }

    private void checkWeatherEffects(StringJoiner weatherResult, int p)
    {
        boolean immuneMoveUsed = this.data(p).digUsed || this.data(p).diveUsed || this.data(p).phantomForceUsed || this.data(p).shadowForceUsed;
        Pokemon active = this.players[p].active;

        switch(this.weather.get())
        {
            case HAIL -> {
                if(active.hasAugment(PokemonAugment.RESTORATIVE_HAIL))
                {
                    int health = active.getMaxHealth(1 / 10.);
                    active.heal(health);

                    weatherResult.add(active.getName() + " restored %s HP from the Hailstorm, due to the %s Augment!".formatted(health, PokemonAugment.RESTORATIVE_HAIL.getAugmentName()));
                }

                if(active.isType(Type.ICE) || immuneMoveUsed) weatherResult.add(active.getName() + " was unaffected by the hailstorm!");
                else
                {
                    int damage = active.getMaxHealth(1 / 16.);
                    active.damage(damage);

                    weatherResult.add(active.getName() + " took " + damage + " damage from the hailstorm!");
                }
            }
            case SANDSTORM -> {
                if(active.hasAugment(PokemonAugment.RESTORATIVE_SANDSTORM))
                {
                    int health = active.getMaxHealth(1 / 10.);
                    active.heal(health);

                    weatherResult.add(active.getName() + " restored %s HP from the Sandstorm, due to the %s Augment!".formatted(health, PokemonAugment.RESTORATIVE_SANDSTORM.getAugmentName()));
                }

                if(active.hasAbility(Ability.SAND_FORCE)) weatherResult.add(Ability.SAND_FORCE.formatActivation(active.getName(), active.getName() + " is immune to the sandstorm's effects!"));
                else if(active.hasAbility(Ability.SAND_VEIL)) weatherResult.add(Ability.SAND_VEIL.formatActivation(active.getName(), active.getName() + " is immune to the sandstorm's effects!"));
                else if(active.isType(Type.ROCK) || active.isType(Type.GROUND) || active.isType(Type.STEEL) || immuneMoveUsed) weatherResult.add(active.getName() + " was unaffected by the sandstorm!");
                else
                {
                    int damage = active.getMaxHealth(1 / 16.);
                    active.damage(damage);

                    weatherResult.add(active.getName() + " took " + damage + " damage from the sandstorm!");
                }
            }
        }
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

        this.iteratePlayers(i -> this.players[i].move = null);

        this.first = "";

        //First turn effects
        if(turn == 0)
        {
            List<Player> sortedBySpeed = Stream.of(this.players).sorted(Comparator.comparingInt(p -> ((Player)p).active.getStat(Stat.SPD)).reversed()).toList();

            //Check Turn 1 weather abilities
            sortedBySpeed.forEach(p -> this.checkWeatherAbilities(this.indexOf(p.ID)));

            //Check generic Turn 1 effects
            sortedBySpeed.forEach(p -> this.onBattleStart(this.indexOf(p.ID)));
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

        //Ability: Intrepid Sword
        if(this.players[p].active.hasAbility(Ability.INTREPID_SWORD))
        {
            this.players[p].active.changes().change(Stat.ATK, 1);
            this.results.add(Ability.INTREPID_SWORD.formatActivation(this.players[p].active.getName(), this.players[p].active.getName() + "'s Attack rose by 1 stage!"));
        }

        //Ability: Electric Surge
        if(this.players[p].active.hasAbility(Ability.ELECTRIC_SURGE) && this.terrain.get().equals(Terrain.NORMAL_TERRAIN))
        {
            this.terrain.setTerrain(Terrain.ELECRIC_TERRAIN);
            this.results.add(Ability.ELECTRIC_SURGE.formatActivation(this.players[p].active.getName(), "An Electric Terrain was created!"));
        }

        //Ability: Psychic Surge
        if(this.players[p].active.hasAbility(Ability.PSYCHIC_SURGE) && this.terrain.get().equals(Terrain.NORMAL_TERRAIN))
        {
            this.terrain.setTerrain(Terrain.PSYCHIC_TERRAIN);
            this.results.add(Ability.PSYCHIC_SURGE.formatActivation(this.players[p].active.getName(), "A Psychic Terrain was created!"));
        }

        //Ability: Misty Surge
        if(this.players[p].active.hasAbility(Ability.MISTY_SURGE) && this.terrain.get().equals(Terrain.NORMAL_TERRAIN))
        {
            this.terrain.setTerrain(Terrain.MISTY_TERRAIN);
            this.results.add(Ability.MISTY_SURGE.formatActivation(this.players[p].active.getName(), "A Misty Terrain was created!"));
        }

        //Ability: Grassy Surge
        if(this.players[p].active.hasAbility(Ability.GRASSY_SURGE) && this.terrain.get().equals(Terrain.NORMAL_TERRAIN))
        {
            this.terrain.setTerrain(Terrain.GRASSY_TERRAIN);
            this.results.add(Ability.GRASSY_SURGE.formatActivation(this.players[p].active.getName(), "A Grassy Terrain was created!"));
        }
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

        for(TextChannel c : this.channels)
        {
            try
            {
                if(this.isComplete()) c.sendFiles(FileUpload.fromData(this.getImage(), "duel.png")).setEmbeds(embed.build()).queue();
                else c.sendFiles(FileUpload.fromData(this.getImage(), "duel.png")).setEmbeds(embed.build()).queue();
            }
            catch (Exception e)
            {
                LoggerHelper.reportError(Duel.class, "Duel Image generation failed!", e);

                c.sendMessageEmbeds(embed.build()).queue();
            }
        }
    }

    public void onWin()
    {
        for(Player p : this.players)
        {
            for(int i = 0; i < p.team.size(); i++)
            {
                Pokemon pokemon = p.team.get(i);

                //Replenish items
                if(p instanceof UserPlayer && pokemon.hasConsumedItem()) pokemon.updateItem();

                //Mega Charge
                if(p instanceof UserPlayer user && MegaEvolutionRegistry.isMega(pokemon.getEntity()))
                {
                    pokemon.removeMegaCharge();
                    pokemon.updateMegaCharges();

                    MegaChargeManager.addChargeEvent(pokemon.getUUID(), MegaEvolutionRegistry.isMega(pokemon.getEntity()));

                    if(pokemon.getMegaCharges() == 0)
                    {
                        pokemon.removeMegaEvolution();
                        MegaChargeManager.removeBlocking(pokemon.getUUID());

                        user.data.directMessage(pokemon.getName() + " has returned to its original form! Its Mega Charges have been depleted, and will slowly regenerate while the Pokemon is not Mega-Evolved.");
                        LoggerHelper.info(Duel.class, "Removing Mega-Evolution from " + pokemon.getName() + " (" + pokemon.getUUID() + ") as its Mega Charges have been depleted.");
                    }
                }
            }
        }
    }

    public void sendWinEmbed()
    {
        EmbedBuilder embed = new EmbedBuilder();

        int winner = this.getWinner().ID.equals(this.players[0].ID) ? 0 : 1;
        int loser = winner == 0 ? 1 : 0;

        int c = new Random().nextInt(11) + 10;

        //If not matchmade Duel, Target system
        if(this.channels.size() == 1)
        {
            Guild g = this.channels.get(0).getGuild();

            if(CommandLegacyTarget.isTarget(g, this.players[winner].ID))
            {
                c = (new Random().nextInt(201) + 50) * (CommandLegacyTarget.SERVER_TARGET_DUELS_WON.get(g.getId()) + 1);

                CommandLegacyTarget.SERVER_TARGET_DUELS_WON.put(g.getId(), CommandLegacyTarget.SERVER_TARGET_DUELS_WON.get(g.getId()) + 1);

                embed.setFooter("The Server Target has won another duel!");
            }
            else if(CommandLegacyTarget.isTarget(g, this.players[loser].ID))
            {
                CommandLegacyTarget.SERVER_TARGETS.remove(g.getId());

                c = new Random().nextInt(501) + 500;

                CommandLegacyTarget.generateNewServerTarget(g);

                embed.setFooter("The Server Target has been defeated!");
            }
        }
        // Casual Matchmade Duel Rewards
        else if(this instanceof CasualMatchmadeDuel) c = new Random().nextInt(100, 201);

        embed.setDescription(this.getWinner().getName() + " has won!" + (c != 0 ? "\nThey earned " + c + " credits!" : ""));

        this.sendEmbed(embed.build());

        if(this.players[winner] instanceof UserPlayer player)
        {
            player.data.getStatistics().incr(PlayerStatistic.PVP_DUELS_WON);
            player.data.getStatistics().incr(PlayerStatistic.PVP_DUELS_COMPLETED);

            player.data.addExp(PMLExperience.DUEL_PVP, 95);

            player.data.updateBountyProgression(b -> {
                if(b.getType().equals(ObjectiveType.WIN_PVP_DUEL) || b.getType().equals(ObjectiveType.COMPLETE_PVP_DUEL)) b.update();
            });
        }

        if(this.players[loser] instanceof UserPlayer player)
        {
            player.data.getStatistics().incr(PlayerStatistic.PVP_DUELS_COMPLETED);

            player.data.updateBountyProgression(ObjectiveType.COMPLETE_PVP_DUEL);
        }

        if(TournamentHelper.isInTournament(this.players[winner].ID) && TournamentHelper.isInTournament(this.players[loser].ID))
        {
            Tournament t = TournamentHelper.instance(this.getWinner().ID);

            boolean neitherElim = !t.isPlayerEliminated(this.players[winner].ID) && !t.isPlayerEliminated(this.players[loser].ID);
            boolean matchupValid = t.getMatchups().stream().anyMatch(m -> m.has(this.players[winner].ID) && m.has(this.players[loser].ID));

            if(neitherElim && matchupValid) t.addDuelResults(this.players[winner].ID, this.players[loser].ID);
        }

        this.uploadExperience();

        if(new Random().nextInt(100) < 20)
        {
            this.uploadEVs(0);
            this.uploadEVs(1);
        }

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
                double experience = this.expGains.get(uuid);

                p.addExp((int)(experience));
                p.updateExperience();
            }
        }
    }

    @Deprecated //TODO: Remove
    protected int giveWinCredits()
    {
        if(this.getWinner() instanceof UserPlayer player)
        {
            int winCredits = new Random().nextInt(501) + 500;
            player.data.changeCredits(winCredits);
            return winCredits;
        }
        return 0;
    }

    protected void sendMessage(String text)
    {
        this.channels.forEach(t -> t.sendMessage(text).queue());
    }

    protected void sendEmbed(MessageEmbed embed)
    {
        this.channels.forEach(t -> t.sendMessageEmbeds(embed).queue());
    }

    //Useful Getters/Setters

    public void submitMove(String id, int index, char type)
    {
        type = Character.toLowerCase(type);

        if(type == 'i') this.queuedMoves.put(id, new TurnAction(ActionType.IDLE, -1, -1));
        //index functions as both the swapIndex and moveIndex, which one is dictated by type == 's' and type == 'm'
        else if(type == 's')
        {
            Player p = this.players[this.indexOf(id)];
            if(p.team.get(index - 1).isFainted()) ((UserPlayer)p).data.directMessage("That pokemon is fainted!");
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
        return Arrays.stream(this.players).anyMatch(Player::lost);
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

    public List<MoveEntity> getMovesUsed(String UUID)
    {
        return this.movesUsed.get(UUID);
    }

    public MoveEntity getLastUsedMove(String UUID)
    {
        return this.getMovesUsed(UUID).isEmpty() ? null : this.getMovesUsed(UUID).get(this.getMovesUsed(UUID).size() - 1);
    }

    public Player getWinner()
    {
        return this.players[0].lost() ? this.players[1] : this.players[0];
    }

    public InputStream getImage() throws Exception
    {
        //Background is 800 x 480 -> 400 x 240
        int baseSize = 300;
        int spacing = 25;
        int backgroundW = 800;
        int backgroundH = 480;
        int hint = BufferedImage.TYPE_INT_ARGB;

        int y = (backgroundH - baseSize) / 2;

        Image background = ImageIO.read(new URL(BACKGROUND)).getScaledInstance(backgroundW, backgroundH, hint);
        BufferedImage combined = new BufferedImage(background.getWidth(null), background.getHeight(null), hint);

        combined.getGraphics().drawImage(background, 0, 0, null);

        if(!this.players[0].active.isFainted())
        {
            int size = this.players[0].active.isDynamaxed() ? (int)(baseSize * 1.25) : baseSize;

            String image = Pokemon.getImage(this.players[0].active.getEntity(), this.players[0].active.isShiny(), this.players[0].active, this.players[0].move == null ? null : this.players[0].move.getEntity());
            URL resource = Pokecord.class.getResource(image);

            if(resource != null)
            {
                Image p = ImageIO.read(resource).getScaledInstance(size, size, hint);
                combined.getGraphics().drawImage(p, spacing, y, null);
            }
            else LoggerHelper.warn(Duel.class, "Pokemon Image not found. Entity: " + this.players[0].active.getEntity().toString() + ", Image File: " + image);
        }

        if(!this.players[1].active.isFainted())
        {
            int size = this.players[1].active.isDynamaxed() ? (int)(baseSize * 1.25) : baseSize;

            String image = Pokemon.getImage(this.players[1].active.getEntity(), this.players[1].active.isShiny(), this.players[1].active, this.players[1].move == null ? null : this.players[1].move.getEntity());
            URL resource = Pokecord.class.getResource(image);

            if(resource != null)
            {
                Image p = ImageIO.read(resource).getScaledInstance(size, size, hint);
                combined.getGraphics().drawImage(p, (backgroundW - spacing) - size, y, null);
            }
            else LoggerHelper.warn(Duel.class, "Pokemon Image not found. Entity: " + this.players[1].active.getEntity().toString() + ", Image File: " + image);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(combined, "png", out);

        byte[] bytes = out.toByteArray(); //This is the slow line

        return new ByteArrayInputStream(bytes);
    }

    protected String getHealthBars()
    {
        String healthBarP1 = this.getHB(0);
        String healthBarP2 = this.getHB(1);

        return this.isComplete() ? "" : healthBarP1 + "\n" + healthBarP2;
    }

    protected String getHB(int p)
    {
        StringBuilder sb = new StringBuilder().append(this.players[p].getName()).append("'s ").append(this.players[p].active.getDisplayName());
        sb.append(this.players[p].active.isDynamaxed() ? (GigantamaxRegistry.hasGMax(this.players[p].active.getEntity()) ? " (Gigantamaxed)" : " (Dynamaxed)") : "");

        sb.append(": ");
        if(this.players[p].active.isFainted()) sb.append("FAINTED");
        else sb.append(this.players[p].active.getHealth()).append(" / ").append(this.players[p].active.getStat(Stat.HP)).append(" HP ").append(this.players[p].active.getActiveStatusConditions());

        return sb.toString();
    }

    protected void iteratePlayers(Consumer<Integer> playerAction)
    {
        for(int i = 0; i < this.players.length; i++) playerAction.accept(i);
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

    //TODO: maybe remove and just default initialize the turn var
    public void setTurn()
    {
        this.turn = 0;
    }

    public void addChannel(TextChannel channel)
    {
        if(this.channels == null) this.channels = new ArrayList<>();
        this.channels.add(channel);
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
        PlayerDataQuery a = PlayerDataQuery.ofNonNull(player1ID);
        PlayerDataQuery b = PlayerDataQuery.ofNonNull(player2ID);

        if(size == 1) this.players = new Player[]{new UserPlayer(a, a.getSelectedPokemon()), new UserPlayer(b, b.getSelectedPokemon())};
        else this.players = new Player[]{new UserPlayer(a, size), new UserPlayer(b, size)};

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

    public Player getOpponent(String ID)
    {
        return this.players[this.indexOf(ID) == 0 ? 1 : 0];
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
