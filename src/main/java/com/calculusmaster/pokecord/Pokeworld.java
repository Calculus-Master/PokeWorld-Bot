package com.calculusmaster.pokecord;

import com.calculusmaster.pokecord.commands.CommandHandler;
import com.calculusmaster.pokecord.commandslegacy.CommandsLegacy;
import com.calculusmaster.pokecord.commandslegacy.pokemon.CommandLegacyBreed;
import com.calculusmaster.pokecord.game.duel.extension.CasualMatchmadeDuel;
import com.calculusmaster.pokecord.game.duel.restrictions.TeamRestrictionRegistry;
import com.calculusmaster.pokecord.game.duel.trainer.TrainerManager;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.data.CustomMoveDataRegistry;
import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.game.moves.registry.MoveTutorRegistry;
import com.calculusmaster.pokecord.game.player.leaderboard.PokeWorldLeaderboard;
import com.calculusmaster.pokecord.game.player.level.MasteryLevelManager;
import com.calculusmaster.pokecord.game.pokemon.augments.PokemonAugmentRegistry;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity;
import com.calculusmaster.pokecord.game.pokemon.evolution.*;
import com.calculusmaster.pokecord.game.world.*;
import com.calculusmaster.pokecord.mongo.Mongo;
import com.calculusmaster.pokecord.util.PrivateInfo;
import com.calculusmaster.pokecord.util.helpers.*;
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
import java.util.Scanner;

public class Pokeworld
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

        LoggerHelper.init("Move Entities", MoveEntity::init);
        LoggerHelper.init("Custom Move Data", CustomMoveDataRegistry::init);
        LoggerHelper.init("Move Tutor", MoveTutorRegistry::init);

        LoggerHelper.init("Pokemon Entities", PokemonEntity::init);

        LoggerHelper.init("Gigantamax", GigantamaxRegistry::init);
        LoggerHelper.init("Mega Evolutions", MegaEvolutionRegistry::init);
        LoggerHelper.init("Forms", FormRegistry::init);
        LoggerHelper.init("Evolutions", EvolutionRegistry::init);

        LoggerHelper.init("EV Lists", DataHelper::createEVLists);
        LoggerHelper.init("Type Lists", DataHelper::createTypeLists);

        LoggerHelper.init("Region Manager", RegionManager::init);
        LoggerHelper.init("PokeWorld Shop", PokeWorldShop::init);

        LoggerHelper.init("Team Restrictions", TeamRestrictionRegistry::init, true);
        LoggerHelper.init("Incomplete Moves", Move::init);
        LoggerHelper.init("Pokemon Rarity", PokemonRarity::init, true);
        LoggerHelper.init("Trainer Manager", TrainerManager::init);
        LoggerHelper.init("Pokemon Augments", PokemonAugmentRegistry::init);
        LoggerHelper.init("Command Handler", CommandsLegacy::init);
        LoggerHelper.init("Pokemon Mastery Level", MasteryLevelManager::init);
        LoggerHelper.init("Achievement Cache", CacheHelper::initAchievementCache);

        LoggerHelper.init("Global Leaderboard", PokeWorldLeaderboard::init);
        LoggerHelper.init("PokeWorld Research Board", PokeWorldResearchBoard::init);
        LoggerHelper.init("PokeWorld Market", PokeWorldMarket::init, true);
        //LoggerHelper.init("CommandPokemon", CacheHelper::initPokemonLists, true);

        long end = System.currentTimeMillis();

        LoggerHelper.info(Pokeworld.class, "Initialization Finished - " + (end - start) + "ms!");

        start = System.currentTimeMillis();

        //Create Bot
        Pokeworld.initializeDiscordBot();

        //Interaction Commands
        LoggerHelper.init("Commands V2", CommandHandler::init, true);

        //Initializations Requiring Bot to be Loaded
        LoggerHelper.init("Spawn Event & Location Event Thread Pools", ThreadPoolHandler::init);
        LoggerHelper.init("Spawn Event Interval Updater", Listener::startSpawnIntervalUpdater);
        LoggerHelper.init("Casual Matchmade Duels", CasualMatchmadeDuel::init);
        LoggerHelper.init("Mega Charge Manager", MegaChargeManager::init);

        LoggerHelper.init("Rotation Tasks", RotationManager::init);

        end = System.currentTimeMillis();

        LoggerHelper.info(Pokeworld.class, "Bot Loading Complete (" + (end - start) + "ms)!");

        INIT_COMPLETE = true;

        List<Document> servers = new ArrayList<>();
        Mongo.ServerData.find().forEach(servers::add);

        servers.forEach(d -> {
            Guild g = BOT_JDA.getGuildById(d.getString("serverID"));

            if(g != null)
            {
                SpawnEventHelper.start(g);
            }
            else
            {
                LoggerHelper.warn(Pokeworld.class, "Bot is not connected to server (%s), removing from Database.".formatted(d.getString("serverID")));

                Mongo.ServerData.deleteOne(Filters.eq("serverID", d.getString("serverID")));
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(Pokeworld::close));

        String command = new Scanner(System.in).nextLine();
        if(command.equalsIgnoreCase("shutdown")) Pokeworld.close();
    }

    public static void close()
    {
        LoggerHelper.info(Pokeworld.class, "Initiating shutdown sequence.");

        BOT_JDA.shutdown();

        try
        {
            BOT_JDA.awaitShutdown();

            SpawnEventHelper.close();
            RaidEventHelper.close();
            CommandLegacyBreed.close();

            ThreadPoolHandler.close();
        }
        catch (InterruptedException e ) { LoggerHelper.reportError(Pokeworld.class, "Bot shutdown was interrupted!", e); }

        LoggerHelper.info(Pokeworld.class, "Bot has shutdown successfully!");
    }
}
