package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.items.PokeItem;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Random;

public class CommandActivate extends Command
{
    public CommandActivate(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length == 1)
        {
            this.embed.setDescription("Specify the item you want to activate! Use p!inventory to check what items you have.");
        }
        else if(!isNumeric(1) || this.playerData.getItemList().size() <= (Integer.parseInt(this.msg[1]) - 1))
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }

        PokeItem item = PokeItem.asItem(this.playerData.getItemList().get(this.getInt(1) - 1));
        Pokemon s = this.playerData.getSelectedPokemon();

        if(item != null && item.nonPokemon)
        {
           if(item.equals(PokeItem.IV_REROLLER))
           {
               double minIV = s.getTotalIVRounded() - (s.getTotalIVRounded() / 2);
               do { s.setIVs(); }
               while (s.getTotalIVRounded() < minIV);

               Pokemon.uploadPokemon(s);

               this.playerData.removeItem(PokeItem.IV_REROLLER.getName());

               this.event.getChannel().sendMessage(this.playerData.getMention() + ": " + s.getName() + "'s IVs were rerolled to " + s.getTotalIV() + "!").queue();
               this.embed = null;
               return this;
           }
           else if(item.equals(PokeItem.EV_REALLOCATOR))
           {
                if(this.msg.length == 4 && Stat.cast(this.msg[2]) != null && Stat.cast(this.msg[3]) != null)
                {
                    Stat from = Stat.cast(this.msg[2]);
                    Stat to = Stat.cast(this.msg[3]);

                    if(s.getEVs().get(from) == 0)
                    {
                        this.event.getChannel().sendMessage(this.playerData.getMention() + ": " + s.getName() + " doesn't have any " + from + " EVs!").queue();
                        this.embed = null;
                        return this;
                    }
                    else if(s.getEVs().get(to) >= 252)
                    {
                        this.event.getChannel().sendMessage(this.playerData.getMention() + ": " + s.getName() + " already has max " + to + " EVs!").queue();
                        this.embed = null;
                        return this;
                    }

                    int initialFrom = s.getEVs().get(from);
                    int initialTo = s.getEVs().get(to);

                    int transferEVs = (int)((new Random().nextInt(100) < 20 ? (new Random().nextInt(26) + 75) / 100D : 1D) * s.getEVs().get(from));

                    s.addEV(to, transferEVs);
                    s.addEV(from, -1 * transferEVs);

                    //Leftovers
                    s.addEV(from, initialFrom - (s.getEVs().get(to) - initialTo));
                }
                else
                {
                    this.event.getChannel().sendMessage(this.playerData.getMention() + ": You have to specific the source stat and destination stat! The command is p!activate <number> <source> <destination>").queue();
                    this.embed = null;
                    return this;
                }
           }
        }
        else if(item != null && !item.nonPokemon)
        {
            this.event.getChannel().sendMessage(this.playerData.getMention() + ": `" + item.getStyledName() + "` must be given to a Pokemon!").queue();
            this.embed = null;
        }
        else
        {
            this.event.getChannel().sendMessage(this.playerData.getMention() + ": You don't have any `" + item.getStyledName() + "`!").queue();
            this.embed = null;
        }

        return this;
    }
}
