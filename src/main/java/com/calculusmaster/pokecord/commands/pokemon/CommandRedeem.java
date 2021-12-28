package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Random;

public class CommandRedeem extends Command
{
    public CommandRedeem(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.REDEEM_POKEMON)) return this.invalidMasteryLevel(Feature.REDEEM_POKEMON);

        if(this.playerData.getRedeems() < 1) this.response = "You don't have any redeems!";
        else if(this.msg.length >= 2)
        {
            String pokemon = Global.normalize(this.getMultiWordContent(1));

            if(!this.isPokemon(pokemon)) this.response = "Invalid Pokemon!";
            else
            {
                Pokemon p = Pokemon.create(pokemon);
                p.setLevel(new Random().nextInt(100) + 1);

                Pokemon.uploadPokemon(p);

                this.playerData.changeRedeems(-1);
                this.playerData.addPokemon(p.getUUID());

                Achievements.grant(this.player.getId(), Achievements.REDEEMED_FIRST_POKEMON, this.event);

                this.response = "You redeemed a Level " + p.getLevel() + " " + p.getName() + "!";
            }
        }
        else this.embed.setDescription(CommandInvalid.getShort());

        return this;
    }
}
