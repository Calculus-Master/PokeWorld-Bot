package com.calculusmaster.pokecord;

import com.calculusmaster.pokecord.commands.Commands;
import com.calculusmaster.pokecord.commands.duel.CommandTarget;
import com.calculusmaster.pokecord.commands.economy.CommandShop;
import com.calculusmaster.pokecord.commands.pokemon.CommandBreed;
import com.calculusmaster.pokecord.commandsv2.CommandHandler;
import com.calculusmaster.pokecord.game.duel.extension.CasualMatchmadeDuel;
import com.calculusmaster.pokecord.game.duel.restrictions.TeamRestrictionRegistry;
import com.calculusmaster.pokecord.game.duel.trainer.TrainerManager;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.MoveData;
import com.calculusmaster.pokecord.game.moves.registry.MaxMoveRegistry;
import com.calculusmaster.pokecord.game.moves.registry.MoveTutorRegistry;
import com.calculusmaster.pokecord.game.moves.registry.ZMoveRegistry;
import com.calculusmaster.pokecord.game.player.level.MasteryLevelManager;
import com.calculusmaster.pokecord.game.pokemon.PokemonAI;
import com.calculusmaster.pokecord.game.pokemon.PokemonRarity;
import com.calculusmaster.pokecord.game.pokemon.SpecialEvolutionRegistry;
import com.calculusmaster.pokecord.game.pokemon.augments.PokemonAugmentRegistry;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonData;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.PrivateInfo;
import com.calculusmaster.pokecord.util.cache.PlayerDataCache;
import com.calculusmaster.pokecord.util.cache.PokemonDataCache;
import com.calculusmaster.pokecord.util.helpers.*;
import com.calculusmaster.pokecord.util.helpers.event.LocationEventHelper;
import com.calculusmaster.pokecord.util.helpers.event.RaidEventHelper;
import com.calculusmaster.pokecord.util.helpers.event.SpawnEventHelper;
import com.calculusmaster.pokecord.util.listener.Listener;
import com.calculusmaster.pokecord.util.listener.MiscListener;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.bson.Document;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Pokecord
{
    public static final String TEST_SERVER_ID = "873993084155887617";
    public static final String NAME = "PokeWorld";

    public static JDA BOT_JDA;
    public static boolean INIT_COMPLETE;

    public static void initializeDiscordBot() throws InterruptedException
    {
        JDABuilder bot = JDABuilder
                .createDefault(PrivateInfo.TOKEN)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(EnumSet.allOf(GatewayIntent.class))
                .setChunkingFilter(ChunkingFilter.ALL)
                .setActivity(Activity.playing("Pokemon"))
                .addEventListeners(
                        new Listener(),
                        new MiscListener(),
                        new CommandHandler()
                );

        BOT_JDA = bot.build().awaitReady();
    }

    public static void main(String[] args) throws InterruptedException, LoginException
    {
        LoggerHelper.disableMongoLoggers();

        //Initializations
        long start = System.currentTimeMillis();

        INIT_COMPLETE = false;

        LoggerHelper.init("Config", ConfigHelper::init);
        LoggerHelper.init("CSV Helper", CSVHelper::init);

        LoggerHelper.init("Pokemon Data", PokemonData::init, true);
        LoggerHelper.init("Move Data", MoveData::init, true);

        LoggerHelper.init("Move Tutor", MoveTutorRegistry::init);
        LoggerHelper.init("Z-Move", ZMoveRegistry::init);
        LoggerHelper.init("Max Move", MaxMoveRegistry::init);
        LoggerHelper.init("Gigantamax", DataHelper::createGigantamaxDataMap);

        LoggerHelper.init("EV Lists", DataHelper::createEVLists);
        LoggerHelper.init("Type Lists", DataHelper::createTypeLists);

        LoggerHelper.init("Player Data Cache", PlayerDataCache::init);

        LoggerHelper.init("Evolutions", SpecialEvolutionRegistry::init);
        LoggerHelper.init("Team Restrictions", TeamRestrictionRegistry::init, true);
        LoggerHelper.init("Incomplete Moves", Move::init);
        LoggerHelper.init("Pokemon Rarity", PokemonRarity::init);
        LoggerHelper.init("Trainer Manager", TrainerManager::init);
        LoggerHelper.init("Pokemon Augments", PokemonAugmentRegistry::init);
        LoggerHelper.init("Command Handler", Commands::init);
        LoggerHelper.init("Shops", CommandShop::updateShops);
        LoggerHelper.init("Pokemon Mastery Level", MasteryLevelManager::init);
        LoggerHelper.init("Achievement Cache", CacheHelper::initAchievementCache);
        LoggerHelper.init("Market", CacheHelper::initMarketEntries, true);

        LoggerHelper.init("Pokemon Data Cache", PokemonDataCache::init);
        //LoggerHelper.init("CommandPokemon", CacheHelper::initPokemonLists, true);
        if(PokemonAI.ENABLED) LoggerHelper.init("Pokemon AI", PokemonAI::init, true);

        long end = System.currentTimeMillis();

        LoggerHelper.info(Pokecord.class, "Initialization Finished - " + (end - start) + "ms!");

        start = System.currentTimeMillis();

        //Create Bot
        Pokecord.initializeDiscordBot();

        //Interaction Commands
        LoggerHelper.init("Commands V2", CommandHandler::init, true);

        //Initializations Requiring Bot to be Loaded
        LoggerHelper.init("Spawn Event & Location Event Thread Pools", ThreadPoolHandler::init);
        LoggerHelper.init("Spawn Event Interval Updater", Listener::startSpawnIntervalUpdater);
        LoggerHelper.init("Casual Matchmade Duels", CasualMatchmadeDuel::init);

        end = System.currentTimeMillis();

        LoggerHelper.info(Pokecord.class, "Bot Loading Complete (" + (end - start) + "ms)!");

        INIT_COMPLETE = true;

        List<Document> servers = new ArrayList<>();
        Mongo.ServerData.find().forEach(servers::add);

        servers.forEach(d -> {
            Guild g = BOT_JDA.getGuildById(d.getString("serverID"));

            if(g != null)
            {
                SpawnEventHelper.start(g);
                LocationEventHelper.start(g);

                CommandTarget.generateNewServerTarget(g);
            }
            else
            {
                LoggerHelper.warn(Pokecord.class, "Bot is not connected to server (%s), removing from Database.".formatted(d.getString("serverID")));

                Mongo.ServerData.deleteOne(Filters.eq("serverID", d.getString("serverID")));
            }
        });

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
