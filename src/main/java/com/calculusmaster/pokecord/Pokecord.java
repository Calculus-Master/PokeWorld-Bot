package com.calculusmaster.pokecord;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.MoveList;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.Listener;
import com.calculusmaster.pokecord.util.PokemonRarity;
import com.calculusmaster.pokecord.util.PrivateInfo;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.util.stream.Collectors;

public class Pokecord
{
    public static void main(String[] args) throws LoginException
    {
        //Initializations
        Pokemon.init();
        MoveList.init();
        PokemonRarity.init();
        Command.init();

        System.out.println(Global.POKEMON.toString());
        System.out.println(Move.MOVES.stream().map(Move::getName).collect(Collectors.toList()).toString());

        //Create Bot
        JDABuilder bot = JDABuilder.createDefault(PrivateInfo.TOKEN);
        bot.setActivity(Activity.playing("Pokemon"));
        bot.addEventListeners(new Listener());
        bot.build();
    }


}
