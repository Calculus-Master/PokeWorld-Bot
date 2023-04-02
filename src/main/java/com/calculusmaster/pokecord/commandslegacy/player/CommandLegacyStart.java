package com.calculusmaster.pokecord.commandslegacy.player;

import com.calculusmaster.pokecord.commandslegacy.CommandLegacy;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.helpers.DataHelper;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SplittableRandom;

public class CommandLegacyStart extends CommandLegacy
{
    //Command Format: p!start <starter>
    public CommandLegacyStart(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public CommandLegacy runCommand()
    {
        boolean isRegistered = PlayerDataQuery.isRegistered(this.player.getId());

        if(isRegistered) this.response = "You have already started your journey!";
        else if(this.msg.length != 2 || !this.msg[0].contains("start"))
        {
            this.embed
                    .setTitle("Welcome to the world of Pokemon!")
                    .addField("Starter", """
                            To begin, use the `p!start <starter>` command to select your starter Pokemon. Replace <starter> with the name of the starter you pick.
                            Possible Starters:\s
                            Generation 1: **Bulbasaur** | **Charmander** | **Squirtle**
                            Generation 2: **Chikorita** | **Cyndaquil** | **Totodile**
                            Generation 3: **Treecko** | **Torchic** | **Mudkip**
                            Generation 4: **Turtwig** | **Chimchar** | **Piplup**
                            Generation 5: **Snivy** | **Tepig** | **Oshawott**
                            Generation 6: **Chespin** | **Fennekin** | **Froakie**
                            Generation 7: **Rowlet** | **Litten** | **Popplio**
                            Generation 8: **Grookey** | **Scorbunny** | **Sobble**
                            Generation 9: **Sprigatito** | **Fuecoco** | **Quaxly**
                            """, false)
                    .addField("Progression", """
                            This bot uses a Level-based progression system. To unlock certain features, you have to earn enough experience and complete certain tasks in order to advance to the next level.
                            After selecting a starter, you'll receive a DM with more information about Pokemon Mastery Level!
                            """, false)
                    .addField("Help", """
                            Use `/help` to learn how to use certain commands!
                            Certain commands will have built-in help sections as well.
                            """, false)
                    .setImage("https://vignette.wikia.nocookie.net/pokeverse/images/4/46/Pokemon_starters_.png/revision/latest/scale-to-width-down/1000?cb=20180424013225");
        }
        else if(!Global.isStarter(PokemonEntity.cast(this.msg[1]))) this.response = "Please input a valid starter!";
        else
        {
            PlayerDataQuery.register(this.player);
            DataHelper.updateServerPlayers(this.server);

            this.playerData = PlayerDataQuery.of(this.player.getId());

            Pokemon starter = Pokemon.create(PokemonEntity.cast(this.msg[1]));
            starter.setLevel(5);
            starter.setIVs(this.getStarterIVs());

            Achievements.grant(this.player.getId(), Achievements.START_JOURNEY, this.event);

            starter.upload();
            this.playerData.addPokemon(starter.getUUID());

            this.playerData.dmMasteryLevel();

            this.response = "You started your journey with " + starter.getName() + "! Check out its stats using %s!".formatted(this.getCommandFormatted("info latest"));
        }

        return this;
    }

    private Map<Stat, Integer> getStarterIVs()
    {
        LinkedHashMap<Stat, Integer> ivs = new LinkedHashMap<>();
        SplittableRandom r = new SplittableRandom();
        for(Stat s : Stat.values()) ivs.put(s, r.nextInt(22, 30));
        return ivs;
    }
}
