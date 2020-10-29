package com.calculusmaster.pokecord.commands;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Random;

public class CommandStart extends Command
{
    //Command Format: p!start <starter>
    public CommandStart(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, "start <starter>");
    }

    @Override
    public Command runCommand()
    {
        boolean isRegistered = PlayerDataQuery.isRegistered(this.player);

        if(isRegistered)
        {
            this.embed.setDescription("You have already started your journey!");
        }
        else if(this.msg.length != 2 || !this.msg[0].equals("start"))
        {
            this.embed.setTitle("Welcome to the world of Pokemon!");
            this.embed.setDescription("To begin, use p!start <starter>, where <starter> is the name of the starter you wish to pick.\n" +
                    "Possible Starters: \n" +
                    "**Bulbasaur** | **Charmander** | **Squirtle**\n" +
                    "**Chikorita** | Cyndaquil | **Totodile**\n" +
                    "**Treecko** | **Torchic** | **Mudkip**\n" +
                    "**Turtwig** | **Chimchar** | **Piplup**\n" +
                    "**Snivy** | **Tepig** | **Oshawott**\n" +
                    "**Chespin** | **Fennekin** | **Froakie**\n" +
                    "**Rowlet** | **Litten** | **Popplio**\n" +
                    "**Grookey** | **Scorbunny** | **Sobble**\n");
            this.embed.setImage("https://vignette.wikia.nocookie.net/pokeverse/images/4/46/Pokemon_starters_.png/revision/latest/scale-to-width-down/1000?cb=20180424013225");
        }
        else if(!Global.isStarter(this.msg[1]))
        {
            this.embed.setDescription("Please input a valid starter");
        }
        else
        {
            PlayerDataQuery.register(this.player);
            PlayerDataQuery p = new PlayerDataQuery(this.player.getId());

            Pokemon starter = Pokemon.create(Global.STARTERS.get(new Random().nextInt(Global.STARTERS.size())));
            starter.setLevel(5);

            Pokemon.uploadPokemon(starter);
            p.addPokemon(starter.getUUID());

            this.embed.setDescription("You started your journey with " + starter.getName() + "!");
        }

        return this;
    }
}
