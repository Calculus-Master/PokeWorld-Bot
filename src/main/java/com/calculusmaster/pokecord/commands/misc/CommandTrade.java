package com.calculusmaster.pokecord.commands.misc;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.enums.items.TR;
import com.calculusmaster.pokecord.game.trade.Trade;
import com.calculusmaster.pokecord.game.trade.TradeHelper;
import com.calculusmaster.pokecord.game.trade.elements.TradeOffer;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            return this;
        }

        if(!TradeHelper.isInTrade(this.player.getId()) && this.mentions.size() > 0)
        {
            String otherID = this.mentions.get(0).getId();
            String otherName = new PlayerDataQuery(otherID).getUsername();

            if(TradeHelper.isInTrade(otherID))
            {
                this.sendMsg(otherName + " is already in a trade!");
            }
            else if(otherID.equals(this.player.getId()))
            {
                this.sendMsg("You can't trade with yourself!");
            }
            else
            {
                Trade.create(this.player.getId(), otherID);

                this.embed = null;
                this.event.getChannel().sendMessage("<@" + otherID + ">: " + this.player.getName() + " requested a trade!").queue();
            }
        }
        else if(TradeHelper.isInTrade(this.player.getId()) && this.msg.length >= 2)
        {
            boolean accept = Arrays.asList("accept").contains(this.msg[1]);
            boolean deny = Arrays.asList("deny").contains(this.msg[1]);
            boolean confirm = Arrays.asList("confirm").contains(this.msg[1]);

            boolean changeCredits = Arrays.asList("credits", "c").contains(this.msg[1]);
            boolean changeRedeems = Arrays.asList("redeems", "redeem", "r").contains(this.msg[1]);
            boolean changePokemon = Arrays.asList("pokemon", "poke", "p").contains(this.msg[1]);
            boolean changeTMs = Arrays.asList("tms", "tm").contains(this.msg[1]);
            boolean changeTRs = Arrays.asList("trs", "tr").contains(this.msg[1]);

            boolean editOffer = (changeCredits || changeRedeems || changePokemon || changeTMs || changeTRs)
                    && this.msg.length >= 4 && (this.msg[2].equals("add") || this.msg[2].equals("remove"));

            Trade trade = TradeHelper.instance(this.player.getId());

            if(accept)
            {
                trade.setStatus(TradeHelper.TradeStatus.TRADING);

                this.embed = trade.getTradeEmbed();
            }
            else if(deny)
            {
                String requestID = trade.getPlayers()[0].ID;
                TradeHelper.delete(this.player.getId());

                this.embed = null;
                this.event.getChannel().sendMessage("<@" + requestID + ">: " + this.player.getName() + " denied the trade request!").queue();
            }
            else if(confirm)
            {
                trade.confirm(this.player.getId());

                this.embed = trade.getTradeEmbed();

                if(trade.isComplete())
                {
                    trade.onComplete();

                    String mention1 = trade.getPlayers()[0].data.getMention();
                    String mention2 = trade.getPlayers()[1].data.getMention();

                    this.event.getChannel().sendMessage(mention1 + " and " + mention2 + ": Trade Complete!").queue();

                    TradeHelper.delete(this.player.getId());
                }
            }
            else if(editOffer)
            {
                //p!trade <type> <add:remove> <args>
                boolean add = this.msg[2].equals("add");
                boolean remove = this.msg[2].equals("remove");

                TradeOffer offer = trade.offer(this.player.getId());
                boolean success = false;

                if(changeCredits && this.isNumeric(3))
                {
                    if(add)
                    {
                        if(this.playerData.getCredits() > (this.getInt(3) + offer.credits))
                        {
                            success = true;

                            offer.add(TradeHelper.OfferType.CREDITS, this.getInt(3));
                        }
                    }
                    else if(remove)
                    {
                        if(this.getInt(3) <= offer.credits)
                        {
                            success = true;

                            offer.remove(TradeHelper.OfferType.CREDITS, this.getInt(3));
                        }
                    }
                }
                else if(changeRedeems && this.isNumeric(3))
                {
                    if(add)
                    {
                        if(this.playerData.getRedeems() > (this.getInt(3) + offer.redeems))
                        {
                            success = true;

                            offer.add(TradeHelper.OfferType.REDEEMS, this.getInt(3));
                        }
                    }
                    else if(remove)
                    {
                        if(this.getInt(3) <= offer.redeems)
                        {
                            success = true;

                            offer.remove(TradeHelper.OfferType.REDEEMS, this.getInt(3));
                        }
                    }
                }
                else if(changePokemon)
                {
                    List<Integer> pokemon = new ArrayList<>();
                    int listSize = this.playerData.getPokemonList().size();

                    for(int i = 3; i < this.msg.length; i++)
                    {
                        if(this.isNumeric(i) && listSize >= this.getInt(i))
                        {
                            pokemon.add(this.getInt(i));
                        }
                    }

                    String id;
                    for(int i : pokemon)
                    {
                        id = this.playerData.getPokemonList().get(i - 1);

                        if(add) offer.add(TradeHelper.OfferType.POKEMON, id);
                        else if(remove) offer.remove(TradeHelper.OfferType.POKEMON, id);
                    }

                    success = true;
                }
                else if(changeTMs)
                {
                    List<Integer> TMs = new ArrayList<>();

                    for(int i = 3; i < this.msg.length; i++)
                    {
                        this.msg[i] = this.msg[i].replaceAll("tm", "");

                        if(this.isNumeric(i) && this.getInt(i) >= 1 && this.getInt(i) <= 100 && this.playerData.getTMList().contains(TM.get(this.getInt(i)).toString()))
                        {
                            TMs.add(this.getInt(i));
                        }
                    }

                    for(int i : TMs)
                    {
                        if(add) offer.add(TradeHelper.OfferType.TM, i);
                        else if(remove) offer.remove(TradeHelper.OfferType.TM, i);
                    }

                    success = true;
                }
                else if(changeTRs)
                {
                    List<Integer> TRs = new ArrayList<>();

                    for(int i = 3; i < this.msg.length; i++)
                    {
                        this.msg[i] = this.msg[i].replaceAll("tr", "");

                        if(this.isNumeric(i) && this.getInt(i) >= 0 && this.getInt(i) <= 99 && this.playerData.getTRList().contains(TR.get(this.getInt(i)).toString()))
                        {
                            TRs.add(this.getInt(i));
                        }
                    }

                    for(int i : TRs)
                    {
                        if(add) offer.add(TradeHelper.OfferType.TR, i);
                        else if(remove) offer.remove(TradeHelper.OfferType.TR, i);
                    }

                    success = true;
                }

                if(success)
                {
                    this.embed = trade.getTradeEmbed();

                    trade.onOfferChanged();
                }
                else this.embed.setDescription(CommandInvalid.getShort());
            }
        }

        return this;
    }
}
