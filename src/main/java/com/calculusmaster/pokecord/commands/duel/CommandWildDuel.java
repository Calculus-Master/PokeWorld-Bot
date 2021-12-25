package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.extension.WildDuel;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.enums.Prices;
import com.calculusmaster.pokecord.util.helpers.DataHelper;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Random;

public class CommandWildDuel extends Command
{
    public CommandWildDuel(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        //Possible options: p!wildduel (random pokemon), p!wildduel STAT (random pokemon with evs in STAT), p!wildduel PKMN (specifically battle PKMN)
        boolean random = this.msg.length == 1;
        boolean stat = this.msg.length == 2 && Stat.cast(this.msg[1]) != null;
        boolean type = this.msg.length == 2 && Type.cast(this.msg[1]) != null;
        boolean specific = this.msg.length >= 2 && this.isPokemon(this.getMultiWordContent(1));

        if(DuelHelper.isInDuel(this.player.getId())) this.response = CommandInvalid.ALREADY_IN_DUEL;
        else if(random || stat || type || specific)
        {
            int price = stat ? Prices.WILDDUEL_STAT.get() : (type ? Prices.WILDDUEL_TYPE.get() : (specific ? Prices.WILDDUEL_SPECIFIC.get() : 0));

            if(price != 0 && this.playerData.getCredits() < price)
            {
                this.invalidCredits(price);
            }
            else
            {
                String pokemon;

                if(specific) pokemon = Global.normalize(this.getMultiWordContent(1));
                else if(stat)
                {
                    List<String> statList = DataHelper.EV_LISTS.get(Stat.cast(this.msg[1]).ordinal());
                    pokemon = statList.get(new Random().nextInt(statList.size()));
                }
                else if(type)
                {
                    List<String> typeList = DataHelper.TYPE_LISTS.get(Type.cast(this.msg[1]));
                    pokemon = typeList.get(new Random().nextInt(typeList.size()));
                }
                else pokemon = "";

                Duel d = WildDuel.create(this.player.getId(), this.event, pokemon);

                if(price != 0) this.playerData.changeCredits(-1 * price);

                this.response = "A wild Pokemon appeared, and it wants to challenge you!";

                d.sendTurnEmbed();

            }
        }
        else this.response = "Invalid arguments! Make sure your Pokemon name, Stat name (HP, ATK, DEF, SPATK, SPDEF, and SPD), or Type name is correct.";

        return this;
    }
}
