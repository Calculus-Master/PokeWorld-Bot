package com.calculusmaster.pokecord.commands.economy;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.enums.items.TR;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

public class CommandInventory extends Command
{
    public CommandInventory(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length == 1)
        {
            this.embed.setTitle(this.playerData.getUsername() + "'s Inventory");
            for(Page p : Page.values()) this.embed.addField(p.title, "`" + this.serverData.getPrefix() + "inventory " + p.commands.get(0) + "`", false);
            this.embed.setDescription("Use the subcommand for an individual section to see your inventory of that specific kind of item!");
        }
        else
        {
            if(Page.isInvalid(this.msg[1])) this.response = "Invalid page! Use `p!inventory` to see the possible inventory pages.";
            else
            {
                Page p = Page.cast(this.msg[1]);

                StringBuilder s = new StringBuilder();

                if(Page.ITEMS.matches(this.msg[1]))
                {
                    if(!this.playerData.getItemList().isEmpty())
                    {
                        for(int i = 0; i < this.playerData.getItemList().size(); i++) s.append(i + 1).append(": ").append(Item.asItem(this.playerData.getItemList().get(i)).getStyledName()).append("\n");
                        s.append("\n");
                    }
                    else s.append("You don't own any Items!");
                }
                else if(Page.ZCRYSTALS.matches(this.msg[1]))
                {
                    if(!this.playerData.getZCrystalList().isEmpty())
                    {
                        for(int i = 0; i < this.playerData.getZCrystalList().size(); i++) s.append(i + 1).append(": ").append(this.playerData.getZCrystalList().get(i)).append("\n");
                        s.append("\n");
                        s.append("`Equipped:` ").append(this.playerData.getEquippedZCrystal() != null && !this.playerData.getEquippedZCrystal().isEmpty() ? this.playerData.getEquippedZCrystal() : "None");
                    }
                    else s.append("You don't own any Z-Crystals!");
                }
                else if(Page.TMS.matches(this.msg[1]))
                {
                    if(!this.playerData.getTMList().isEmpty())
                    {
                        for(String tm : this.playerData.getTMList()) s.append(tm).append(" - ").append(TM.get(tm).getMoveName()).append("\n");
                        s.deleteCharAt(s.length() - 1);
                    }
                    else s.append("You don't own any Technical Machines (TMs)!");
                }
                else if(Page.TRS.matches(this.msg[1]))
                {
                    if(!this.playerData.getTRList().isEmpty())
                    {
                        for(String tr : this.playerData.getTRList()) s.append(tr).append(" - ").append(TR.get(tr).getMoveName()).append("\n");
                        s.deleteCharAt(s.length() - 1);
                    }
                    else s.append("You don't own any Technical Records (TRs)!");
                }

                this.embed.setTitle(this.playerData.getUsername() + "'s Inventory - " + p.title);
                this.embed.setDescription(s.toString());
            }
        }

        return this;
    }

    private enum Page
    {
        ITEMS("Items", "item", "items", "i"),
        ZCRYSTALS("Z Crystals", "zcrystal", "zcrystals", "z"),
        TMS("TMs", "tm", "tms"),
        TRS("TRs", "tr", "trs");

        private List<String> commands;
        public String title;

        Page(String title, String... commands)
        {
            this.title = title;
            this.commands = Arrays.asList(commands);
        }

        public boolean matches(String s)
        {
            return this.commands.contains(s);
        }

        public static boolean isInvalid(String s)
        {
            return Arrays.stream(values()).noneMatch(p -> p.matches(s));
        }

        public static Page cast(String s)
        {
            for(Page p : values()) if(p.matches(s)) return p;
            return null;
        }
    }
}
