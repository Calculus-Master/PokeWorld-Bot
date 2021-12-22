package com.calculusmaster.pokecord;

import com.calculusmaster.pokecord.commands.Commands;
import com.calculusmaster.pokecord.commands.duel.CommandTarget;
import com.calculusmaster.pokecord.commands.economy.CommandShop;
import com.calculusmaster.pokecord.commands.moves.CommandAbilityInfo;
import com.calculusmaster.pokecord.commands.pokemon.CommandBreed;
import com.calculusmaster.pokecord.game.duel.players.GymLeader;
import com.calculusmaster.pokecord.game.duel.players.Trainer;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.registry.MaxMoveRegistry;
import com.calculusmaster.pokecord.game.moves.registry.MoveTutorRegistry;
import com.calculusmaster.pokecord.game.moves.registry.ZMoveRegistry;
import com.calculusmaster.pokecord.game.player.level.PlayerLevel;
import com.calculusmaster.pokecord.game.player.pokepass.PokePass;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.PokemonRarity;
import com.calculusmaster.pokecord.game.pokemon.SpecialEvolutionRegistry;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonData;
import com.calculusmaster.pokecord.util.PrivateInfo;
import com.calculusmaster.pokecord.util.helpers.*;
import com.calculusmaster.pokecord.util.helpers.event.LocationEventHelper;
import com.calculusmaster.pokecord.util.helpers.event.RaidEventHelper;
import com.calculusmaster.pokecord.util.helpers.event.SpawnEventHelper;
import com.calculusmaster.pokecord.util.listener.ButtonListener;
import com.calculusmaster.pokecord.util.listener.Listener;
import com.calculusmaster.pokecord.util.listener.MiscListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Pokecord
{
    public static JDA BOT_JDA;
    public static boolean INIT_COMPLETE;

    public static void main(String[] args) throws InterruptedException, LoginException
    {
        LoggerHelper.disableMongoLoggers();

        //Initializations
        long start = System.currentTimeMillis();

        INIT_COMPLETE = false;

        LoggerHelper.init("Config", ConfigHelper::init);
        LoggerHelper.init("Pokemon Data", PokemonData::init);
        LoggerHelper.init("Pokemon Data (Legacy)", DataHelper::createPokemonData, true);
        LoggerHelper.init("Pokemon (Legacy, DataHelper.POKEMON)", DataHelper::createPokemonList);
        LoggerHelper.init("Pokemon (Legacy, Global.POKEMON)", Pokemon::init);
        LoggerHelper.init("Move Data", DataHelper::createMoveData, true);
        LoggerHelper.init("Move", DataHelper::createMoveList);
        LoggerHelper.init("Move Tutor", MoveTutorRegistry::init);
        LoggerHelper.init("Z-Move", ZMoveRegistry::init);
        LoggerHelper.init("Max Move", MaxMoveRegistry::init);
        LoggerHelper.init("Gigantamax", DataHelper::createGigantamaxDataMap);
        LoggerHelper.init("EV Lists", DataHelper::createEVLists);
        LoggerHelper.init("Type Lists", DataHelper::createTypeLists);
        LoggerHelper.init("Species Descriptions (Legacy)", DataHelper::createSpeciesDescLists, true);
        LoggerHelper.init("Egg Groups (Legacy)", DataHelper::createEggGroupLists, true);
        LoggerHelper.init("Base Hatch Targets (Legacy)", DataHelper::createBaseEggHatchTargetsMap, true);
        LoggerHelper.init("Gender Rates (Legacy)", DataHelper::createGenderRateMap, true);
        LoggerHelper.init("Evolutions", SpecialEvolutionRegistry::init);
        LoggerHelper.init("Trainer", Trainer::init);
        LoggerHelper.init("Incomplete Moves", Move::init);
        LoggerHelper.init("Ability Info", CommandAbilityInfo::init);
        LoggerHelper.init("Pokemon Rarity", PokemonRarity::init);
        LoggerHelper.init("Command Handler", Commands::init);
        LoggerHelper.init("Gym Leader", GymLeader::init);
        LoggerHelper.init("Shops", CommandShop::updateShops);
        LoggerHelper.init("Pokemon Mastery Level", PlayerLevel::init);
        LoggerHelper.init("PokePass", PokePass::init);
        LoggerHelper.init("Achievement Cache", CacheHelper::initAchievementCache);
        LoggerHelper.init("Player Cache", CacheHelper::initPlayerCache);
        LoggerHelper.init("Market", CacheHelper::initMarketEntries, true);
        LoggerHelper.init("CommandPokemon", CacheHelper::initPokemonLists, true);

        long end = System.currentTimeMillis();

        LoggerHelper.info(Pokecord.class, "Initialization Finished - " + (end - start) + "ms!");

        start = System.currentTimeMillis();

        //Create Bot
        JDABuilder bot = JDABuilder
                .createDefault(PrivateInfo.TOKEN)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(EnumSet.allOf(GatewayIntent.class))
                .setChunkingFilter(ChunkingFilter.ALL)
                .setActivity(Activity.playing("Pokemon"))
                .addEventListeners(
                        new Listener(),
                        new ButtonListener(),
                        new MiscListener()
                );

        BOT_JDA = bot.build().awaitReady();

        //Initializations Requiring Bot to be Loaded
        LoggerHelper.init("Spawn Event & Location Event Thread Pools", ThreadPoolHandler::init);
        LoggerHelper.init("Spawn Event Interval Updater", Listener::startSpawnIntervalUpdater);

        end = System.currentTimeMillis();

        LoggerHelper.info(Pokecord.class, "Bot Loading Complete (" + (end - start) + "ms)!");

        INIT_COMPLETE = true;

        //PokecordGUI.launch(PokecordGUI.class, args);

        Random r = new Random();

        for(Guild g : BOT_JDA.getGuilds())
        {
            Thread.sleep(r.nextInt(1000) + 500);

            SpawnEventHelper.start(g);
            LocationEventHelper.start(g);

            CommandTarget.generateNewServerTarget(g);
        }

        TimeHelper.start();
    }

    public static void close()
    {
        SpawnEventHelper.close();
        RaidEventHelper.close();
        LocationEventHelper.close();
        CommandBreed.close();
        ThreadPoolHandler.close();

        Executors.newScheduledThreadPool(1).schedule(() -> {
            BOT_JDA.shutdownNow();

            LoggerHelper.info(Pokecord.class, "Bot has shutdown successfully!");
        }, 15, TimeUnit.SECONDS);
    }
}
