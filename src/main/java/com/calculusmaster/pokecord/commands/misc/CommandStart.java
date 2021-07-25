package com.calculusmaster.pokecord.commands.misc;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.PrivateInfo;
import com.calculusmaster.pokecord.util.helpers.DataHelper;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Random;

public class CommandStart extends Command
{
    //Command Format: p!start <starter>
    public CommandStart(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        boolean isRegistered = PlayerDataQuery.isRegistered(this.player.getId());

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
                    "**Chikorita** | **Cyndaquil** | **Totodile**\n" +
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
            DataHelper.updateServerPlayers(this.server);

            PlayerDataQuery p = new PlayerDataQuery(this.player.getId());

            Pokemon starter = Pokemon.create(Global.normalCase(this.msg[1]));
            starter.setLevel(5);
            starter.setIVs(this.getStarterIVs());

            Achievements.grant(this.player.getId(), Achievements.START_JOURNEY, this.event);

            Pokemon.uploadPokemon(starter);
            p.addPokemon(starter.getUUID());

            if(!PrivateInfo.getPlayerMythical(this.player.getId()).equals(""))
            {
                Pokemon mythical = Pokemon.create(PrivateInfo.getPlayerMythical(this.player.getId()));
                Pokemon.uploadPokemon(mythical);
                p.addPokemon(mythical.getUUID());
                this.event.getChannel().sendMessage("You also acquired a " + mythical.getName() + "!").queue();
            }

            this.embed.setDescription("You started your journey with " + starter.getName() + "!");
        }

        return this;
    }

    private String getStarterIVs()
    {
        Random r = new Random();
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < 6; i++) s.append(r.nextInt(10) + 22).append("-");
        return s.deleteCharAt(s.length() - 1).toString().trim();
    }
}
