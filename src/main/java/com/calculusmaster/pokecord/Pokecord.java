package com.calculusmaster.pokecord;

import com.calculusmaster.pokecord.commands.Commands;
import com.calculusmaster.pokecord.commands.duel.CommandWildDuel;
import com.calculusmaster.pokecord.commands.moves.CommandAbilityInfo;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.PokePass;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.duel.elements.GymLeader;
import com.calculusmaster.pokecord.game.duel.elements.Trainer;
import com.calculusmaster.pokecord.util.PokemonRarity;
import com.calculusmaster.pokecord.util.PrivateInfo;
import com.calculusmaster.pokecord.util.helpers.CacheHelper;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.calculusmaster.pokecord.util.helpers.SpawnEventHelper;
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

public class Pokecord
{
    public static JDA BOT_JDA;

    public static void main(String[] args) throws InterruptedException, LoginException
    {
        LoggerHelper.disableMongoLoggers();

        //Initializations
        long start = System.currentTimeMillis();

        LoggerHelper.init("Pokemon", Pokemon::init);
        LoggerHelper.init("Gigantamax", Pokemon::gigantamaxInit);
        LoggerHelper.init("EV Lists", CommandWildDuel::init);
        LoggerHelper.init("Daily Trainer", Trainer::setDailyTrainers);
        LoggerHelper.init("Move", Move::init);
        LoggerHelper.init("Ability Info", CommandAbilityInfo::init);
        LoggerHelper.init("Pokemon Rarity", PokemonRarity::init);
        LoggerHelper.init("Command Handler", Commands::init);
        LoggerHelper.init("Gym Leader", GymLeader::init);
        LoggerHelper.init("PokePass", PokePass::init);
        LoggerHelper.init("Achievement Cache", CacheHelper::initAchievementCache);
        LoggerHelper.init("Market", CacheHelper::initMarketEntries);
        LoggerHelper.init("CommandPokemon", CacheHelper::initPokemonLists);

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

        end = System.currentTimeMillis();

        LoggerHelper.info(Pokecord.class, "Bot Loading Complete (" + (end - start) + "ms)!");

        for(Guild g : BOT_JDA.getGuilds())
        {
            Thread.sleep(1000);
            SpawnEventHelper.start(g);
        }
    }
}
