package com.calculusmaster.pokecord.commandslegacy.pokemon;

import com.calculusmaster.pokecord.commandslegacy.CommandLegacy;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandLegacyActivate extends CommandLegacy
{
    public CommandLegacyActivate(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public CommandLegacy runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.ACTIVATE_ITEMS)) return this.invalidMasteryLevel(Feature.ACTIVATE_ITEMS);

        this.response = "Text based activate command is disabled";

//        if(this.msg.length == 1)
//        {
//            this.embed.setDescription("Specify the item you want to activate! Use p!inventory to check what items you have.");
//            return this;
//        }
//        else if(!isNumeric(1) || this.playerData.getItemList().size() <= (Integer.parseInt(this.msg[1]) - 1))
//        {
//            this.embed.setDescription(CommandLegacyInvalid.getShort());
//            return this;
//        }
//
//        Item item = Item.cast(this.playerData.getItemList().get(this.getInt(1) - 1));
//        Pokemon s = this.playerData.getSelectedPokemon();
//
//        if(item != null && item.isFunctionalItem())
//        {
//           if(item.equals(Item.IV_REROLLER))
//           {
//               double minIV = s.getTotalIVRounded() - (s.getTotalIVRounded() / 2);
//               s.setIVs((int)minIV);
//
//               s.updateIVs();
//
//               this.playerData.removeItem(Item.IV_REROLLER.getName());
//
//               this.response = s.getName() + "'s IVs were rerolled to " + s.getTotalIV() + "!";
//               return this;
//           }
//           else if(item.equals(Item.EV_REALLOCATOR))
//           {
//                if(this.msg.length == 4 && Stat.cast(this.msg[2]) != null && Stat.cast(this.msg[3]) != null)
//                {
//                    Stat from = Stat.cast(this.msg[2]);
//                    Stat to = Stat.cast(this.msg[3]);
//
//                    if(s.getEVs().get(from) == 0)
//                    {
//                        this.response = s.getName() + " doesn't have any " + from + " EVs!";
//                        return this;
//                    }
//                    else if(s.getEVs().get(to) >= 252)
//                    {
//                        this.response = s.getName() + " already has max " + to + " EVs!";
//                        return this;
//                    }
//
//                    int initialFrom = s.getEVs().get(from);
//                    int initialTo = s.getEVs().get(to);
//
//                    int transferEVs = (int)((new Random().nextInt(100) < 20 ? (new Random().nextInt(26) + 75) / 100D : 1D) * s.getEVs().get(from));
//
//                    s.addEVs(to, transferEVs);
//                    s.addEVs(from, -1 * transferEVs);
//
//                    //Leftovers
//                    s.addEVs(from, initialFrom - (s.getEVs().get(to) - initialTo));
//
//                    s.updateEVs();
//
//                    this.playerData.removeItem(Item.EV_REALLOCATOR.getName());
//
//                    this.embed.setDescription("Transferred " + transferEVs + " " + from.toString() + " EVs to " + s.getName() + "'s " + to.toString() + "!");
//                }
//                else
//                {
//                    this.response = "You have to specify the source stat and destination stat! The command is p!activate <number> <source> <destination>";
//                    return this;
//                }
//           }
//           else if(item.equals(Item.EV_CLEARER))
//           {
//               if(this.msg.length == 3 && Stat.cast(this.msg[2]) != null)
//               {
//                   Stat target = Stat.cast(this.msg[2]);
//
//                   if(s.getEVs().get(target) == 0)
//                   {
//                       this.event.getChannel().sendMessage(this.playerData.getMention() + ": " + s.getName() + " doesn't have any " + target + " EVs!").queue();
//                       this.embed = null;
//                       return this;
//                   }
//
//                   s.addEVs(target, -1 * s.getEVs().get(target));
//
//                   s.updateEVs();
//
//                   this.playerData.removeItem(Item.EV_CLEARER.getName());
//
//                   this.embed.setDescription("Successfully cleared all of " + s.getName() + "'s " + target + " EVs!");
//               }
//               else this.response = "You have to specify the stat! The command is p!activate <number> <stat>";
//           }
//        }
//        else if(item != null) this.response = "`" + item.getStyledName() + "` must be given to a Pokemon!";
//        else this.response = "You don't have any `" + item.getStyledName() + "`!";

        return this;
    }
}
