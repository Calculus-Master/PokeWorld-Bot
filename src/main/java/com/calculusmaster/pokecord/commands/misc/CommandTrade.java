package com.calculusmaster.pokecord.commands.misc;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.Trade;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;

public class CommandTrade extends Command
{
    public CommandTrade(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        System.out.println("TRADE: " + Arrays.toString(this.msg));
        if(this.msg.length == 1)
        {
            this.embed.setDescription(CommandInvalid.getShort());
        }
        else if(this.msg.length == 2)
        {
            if(Trade.isInTrade(this.player.getId()) && this.msg[1].equals("accept"))
            {
                Trade t = Trade.getInstance(this.player.getId());
                t.setStatus(Trade.TradeStatus.TRADING);
                this.embed = t.getTradeEmbed();
            }
            else if(Trade.isInTrade(this.player.getId()) && this.msg[1].equals("deny"))
            {
                Trade.remove(this.player.getId());
                this.embed.setDescription(this.playerData.getUsername() + " has cancelled the trade request!");
            }
            else if(Trade.isInTrade(this.player.getId()) && this.msg[1].equals("confirm"))
            {
                Trade t = Trade.getInstance(this.player.getId());
                t.confirmTrade(this.player.getId());
                this.event.getChannel().editMessageById(t.getChannelID(), t.getTradeEmbed().build()).queue();

                if(t.isComplete()) t.onComplete();
            }
            else if(this.msg[1].contains("<@!") && this.msg[1].contains(">"))
            {
                String otherID = this.msg[1].substring("<@!".length(), this.msg[1].lastIndexOf(">"));
                if(!Trade.isInTrade(otherID))
                {
                    Trade.initiate(this.player.getId(), otherID).setChannel(this.event.getChannel().getId());
                    this.embed.setDescription(this.player.getName() + " has requested a trade!");
                }
                else this.embed.setDescription(new PlayerDataQuery(otherID).getUsername() + " is already in a trade!");
            }
            else
            {
                this.embed.setDescription(CommandInvalid.getShort());
            }
        }
        else if(Trade.isInTrade(this.player.getId()) && this.msg.length >= 4)
        {
            boolean hasAddRemove = this.msg[2].equals("add") || this.msg[2].equals("remove");
            boolean isNumeric = this.msg[3].chars().allMatch(Character::isDigit);
            Trade t = Trade.getInstance(this.player.getId());

            if(t.getStatus().equals(Trade.TradeStatus.TRADING) && hasAddRemove && isAllNumeric(this.msg))
            {
                if(this.msg[1].equals("credits") || this.msg[1].equals("c"))
                {
                    if(this.msg[2].equals("add")) t.addCredits(this.player.getId(), Integer.parseInt(this.msg[3]));
                    else if(this.msg[2].equals("remove")) t.removeCredits(this.player.getId(), Integer.parseInt(this.msg[3]));

                    this.event.getChannel().editMessageById(t.getChannelID(), t.getTradeEmbed().build()).queue();
                }
                else if(this.msg[1].equals("pokemon") || this.msg[1].equals("p"))
                {
                    int[] rest = this.getPokemonNumbers(this.msg);
                    if(this.msg[2].equals("add")) t.addPokemon(this.player.getId(), rest);
                    else if(this.msg[2].equals("remove")) t.removePokemon(this.player.getId(), rest);

                    this.event.getChannel().editMessageById(t.getChannelID(), t.getTradeEmbed().build()).queue();
                }
            }
            else this.embed.setDescription(CommandInvalid.getShort());
        }
        return this;
    }

    private boolean isAllNumeric(String[] msg)
    {
        String[] strs = new String[msg.length - 3];
        for(int i = 3; i < msg.length; i++) strs[i] = msg[i];
        return Arrays.stream(strs).map(String::chars).filter(s -> s.allMatch(Character::isDigit)).count() == strs.length;
    }

    private int[] getPokemonNumbers(String[] msg)
    {
        int[] nums = new int[msg.length - 3];
        for(int i = 3; i < nums.length; i++) nums[i - 3] = Integer.parseInt(msg[i]);
        return nums;
    }
}
