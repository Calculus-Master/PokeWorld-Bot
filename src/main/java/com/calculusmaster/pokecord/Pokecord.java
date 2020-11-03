package com.calculusmaster.pokecord;

import com.calculusmaster.pokecord.game.MoveList;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.Listener;
import com.calculusmaster.pokecord.util.PokemonRarity;
import com.calculusmaster.pokecord.util.PrivateInfo;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class Pokecord
{
    public static void main(String[] args) throws LoginException
    {
        //Initializations
        Global.buildPokemonList();
        MoveList.init();
        PokemonRarity.init();

        System.out.println(Global.POKEMON.toString());
        System.out.println(Move.MOVES.toString());

        //SpawnEvent Timer
        //TODO: new Listener.SpawnEvent().run();

        //Create Bot
        JDABuilder bot = JDABuilder.createDefault(PrivateInfo.TOKEN);
        bot.setActivity(Activity.playing("Pokemon"));
        bot.addEventListeners(new Listener());
        bot.build();
    }


}
