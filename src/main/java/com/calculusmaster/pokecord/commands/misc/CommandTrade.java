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
                //this.event.getChannel().editMessageById(t.getMessageID(), t.getTradeEmbed().build()).queue();
                this.embed = t.getTradeEmbed();

                if(t.isComplete())
                {
                    t.onComplete();
                    this.embed.setDescription("Trade complete!");
                }
            }
            else if(this.mentions.size() > 0)
            {
                String otherID = this.mentions.get(0).getId();
                if(!Trade.isInTrade(otherID) && !this.player.getId().equals(otherID))
                {
                    Trade.initiate(this.player.getId(), otherID, this.event.getMessageId());
                    this.embed.setDescription(this.player.getName() + " has requested a trade!");
                }
                else if(this.player.getId().equals(otherID))
                {
                    this.embed.setDescription("Don't trade with yourself.");
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
            Trade t = Trade.getInstance(this.player.getId());

            if(t.getStatus().equals(Trade.TradeStatus.TRADING) && hasAddRemove && isAllNumeric(this.msg))
            {
                if(this.msg[1].equals("credits") || this.msg[1].equals("c"))
                {
                    if(this.msg[2].equals("add"))
                    {
                        if(this.playerData.getCredits() >= this.getInt(3)) t.addCredits(this.player.getId(), Integer.parseInt(this.msg[3]));
                        else
                        {
                            this.event.getChannel().sendMessage(this.playerData.getMention() + ": You don't have that many credits!").queue();
                            this.embed = null;
                            return this;
                        }
                    }
                    else if(this.msg[2].equals("remove")) t.removeCredits(this.player.getId(), Integer.parseInt(this.msg[3]));

                    this.embed = t.getTradeEmbed();
                    //this.event.getChannel().editMessageById(t.getMessageID(), t.getTradeEmbed().build()).queue();
                }
                else if(this.msg[1].equals("pokemon") || this.msg[1].equals("p"))
                {
                    int[] rest = this.getPokemonNumbers(this.msg);
                    //System.out.println(Arrays.toString(rest));
                    if(this.msg[2].equals("add")) t.addPokemon(this.player.getId(), rest);
                    else if(this.msg[2].equals("remove")) t.removePokemon(this.player.getId(), rest);

                    this.embed = t.getTradeEmbed();
                    //this.event.getChannel().editMessageById(t.getMessageID(), t.getTradeEmbed().build()).queue();
                }
                else if(this.msg[1].equals("redeems") || this.msg[1].equals("r"))
                {
                    if(this.msg[2].equals("add"))
                    {
                        if(this.playerData.getRedeems() >= this.getInt(3)) t.addRedeems(this.player.getId(), Integer.parseInt(this.msg[3]));
                        else
                        {
                            this.event.getChannel().sendMessage(this.playerData.getMention() + ": You don't have that many redeems!").queue();
                            this.embed = null;
                            return this;
                        }
                    }
                    else if(this.msg[2].equals("remove")) t.removeRedeems(this.player.getId(), Integer.parseInt(this.msg[3]));

                    this.embed = t.getTradeEmbed();
                }
            }
            else this.embed.setDescription(CommandInvalid.getShort());
        }
        return this;
    }

    private boolean isAllNumeric(String[] msg)
    {
        String[] strs = new String[msg.length - 3];
        for(int i = 3; i < msg.length; i++) strs[i - 3] = msg[i];
        return Arrays.stream(strs).map(String::chars).filter(s -> s.allMatch(Character::isDigit)).count() == strs.length;
    }

    private int[] getPokemonNumbers(String[] msg)
    {
        int[] nums = new int[msg.length - 3];
        for(int i = 3; i < msg.length; i++) nums[i - 3] = Integer.parseInt(msg[i]);
        return nums;
    }
}
