package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.Achievements;
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
        if(this.playerData.getRedeems() < 1) this.sendMsg(this.playerData.getMention() + ": You don't have any redeems!");
        else if(this.msg.length >= 2)
        {
            String pokemon = Global.normalCase(this.getMultiWordContent(1));

            if(!Global.POKEMON.contains(pokemon)) this.sendMsg("Invalid Pokemon!");
            else
            {
                Pokemon p = Pokemon.create(pokemon);
                p.setLevel(new Random().nextInt(100) + 1);
                p.setIVs(45);

                Pokemon.uploadPokemon(p);

                this.playerData.changeRedeems(-1);
                this.playerData.addPokemon(p.getUUID());

                Achievements.grant(this.player.getId(), Achievements.REDEEMED_FIRST_POKEMON, this.event);

                this.sendMsg(this.playerData.getMention() + ": You redeemed a Level " + p.getLevel() + " " + p.getName() + "!");
            }
        }
        else this.embed.setDescription(CommandInvalid.getShort());

        return this;
    }
}
