package com.calculusmaster.pokecord.commands.misc;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.pokemon.CommandInfo;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.PrivateInfo;
import com.calculusmaster.pokecord.util.helpers.DataHelper;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SplittableRandom;

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

        if(isRegistered) this.response = "You have already started your journey!";
        else if(this.msg.length != 2 || !this.msg[0].equals("start"))
        {
            this.embed
                    .setTitle("Welcome to the world of Pokemon!")
                    .addField("Starter", """
                            To being, use the `p!start <starter>` command to select your starter Pokemon. Replace <starter> with the name of the starter you pick.
                            Possible Starters:\s
                            Generation 1: **Bulbasaur** | **Charmander** | **Squirtle**
                            Generation 2: **Chikorita** | **Cyndaquil** | **Totodile**
                            Generation 3: **Treecko** | **Torchic** | **Mudkip**
                            Generation 4: **Turtwig** | **Chimchar** | **Piplup**
                            Generation 5: **Snivy** | **Tepig** | **Oshawott**
                            Generation 6: **Chespin** | **Fennekin** | **Froakie**
                            Generation 7: **Rowlet** | **Litten** | **Popplio**
                            Generation 8: **Grookey** | **Scorbunny** | **Sobble**
                            """, false)
                    .addField("Progression", """
                            This bot uses a Level-based progression system. To unlock certain features, you have to earn enough experience and complete certain tasks in order to advance to the next level.
                            Check what tasks you have to complete in order to level up by using the `p!level` command.
                            Upon reaching level 19, you will have unlocked all of the core features of the bot. Level 20 and beyond contains more advanced features that are tougher to unlock!
                            """, false)
                    .addField("Help", """
                            Use `p!help <command name>` to learn how to use certain commands!
                            Certain advanced commands will additionally have an `info` secondary option that provides a more detailed explanation (For example, `p!duel info` or `p!target info`)
                            """, false)
                    .setImage("https://vignette.wikia.nocookie.net/pokeverse/images/4/46/Pokemon_starters_.png/revision/latest/scale-to-width-down/1000?cb=20180424013225");
        }
        else if(!Global.isStarter(this.msg[1])) this.response = "Please input a valid starter!";
        else
        {
            PlayerDataQuery.register(this.player);
            DataHelper.updateServerPlayers(this.server);

            PlayerDataQuery p = PlayerDataQuery.of(this.player.getId());

            Pokemon starter = Pokemon.create(Global.normalize(this.msg[1]));
            starter.setLevel(5);
            starter.setIVs(this.getStarterIVs());

            Achievements.grant(this.player.getId(), Achievements.START_JOURNEY, this.event);

            starter.upload();
            p.addPokemon(starter.getUUID());

            if(!PrivateInfo.getPlayerMythical(this.player.getId()).equals(""))
            {
                Pokemon mythical = Pokemon.create(PrivateInfo.getPlayerMythical(this.player.getId()));
                mythical.upload(); //TODO: This is no longer needed, delete
                p.addPokemon(mythical.getUUID());
                this.event.getChannel().sendMessage("You also acquired a " + mythical.getName() + "!").queue();
            }

            this.response = "You started your journey with " + starter.getName() + "! Check out its stats below!";

            //Run a p!info command on the starter
            new CommandInfo(this.event, new String[]{"info"}).runCommand().send();
        }

        return this;
    }

    private Map<Stat, Integer> getStarterIVs()
    {
        LinkedHashMap<Stat, Integer> ivs = new LinkedHashMap<>();
        SplittableRandom r = new SplittableRandom();
        for(Stat s : Stat.values()) ivs.put(s, r.nextInt(22, 32));
        return ivs;
    }
}
