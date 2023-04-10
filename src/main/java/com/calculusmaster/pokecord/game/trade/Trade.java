package com.calculusmaster.pokecord.game.trade;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.trade.elements.TradeOffer;
import com.calculusmaster.pokecord.game.trade.elements.TradePlayer;
import com.calculusmaster.pokecord.util.enums.StatisticType;
import net.dv8tion.jda.api.EmbedBuilder;

import static com.calculusmaster.pokecord.game.trade.TradeHelper.TRADES;
import static com.calculusmaster.pokecord.game.trade.TradeHelper.TradeStatus;

public class Trade
{
    private TradePlayer[] players;
    private TradeStatus status;

    public static Trade create(String player1ID, String player2ID)
    {
        Trade t = new Trade();

        t.setPlayers(player1ID, player2ID);
        t.setStatus(TradeStatus.WAITING);

        TRADES.add(t);
        return t;
    }

    public void onComplete()
    {
        //Transfer Offer from 0 to 1
        this.players[0].offer.transfer(this.players[1].data);
        //Transfer Offer from 1 to 0
        this.players[1].offer.transfer(this.players[0].data);

        this.players[0].data.getStatistics().increase(StatisticType.TRADES_COMPLETED);
        this.players[1].data.getStatistics().increase(StatisticType.TRADES_COMPLETED);

        this.players[0].data.updateBountyProgression(ObjectiveType.COMPLETE_TRADE);
        this.players[1].data.updateBountyProgression(ObjectiveType.COMPLETE_TRADE);

        this.setStatus(TradeStatus.COMPLETE);

        TradeHelper.delete(this.players[0].ID);
    }

    public void onOfferChanged()
    {
        this.unconfirm();
    }

    public EmbedBuilder getTradeEmbed()
    {
        EmbedBuilder trade = new EmbedBuilder();

        trade.setTitle("Trade: " + this.players[0].data.getUsername() + " and " + this.players[1].data.getUsername());
        trade.setDescription(this.getPlayerSectionEmbed(0) + "\n\n" + this.getPlayerSectionEmbed(1));

        return trade;
    }

    private String getPlayerSectionEmbed(int p)
    {
        String start = this.players[p].data.getUsername() + "'s Offer:";
        String confirmEmote = this.players[p].confirmed ? ":white_check_mark:" : ":x:";
        return start + " " + confirmEmote + "\n" + this.players[p].offer.asString();
    }

    public void confirm(String id)
    {
        this.players[this.indexOf(id)].confirmed = true;
    }

    public void unconfirm()
    {
        this.players[0].confirmed = false;
        this.players[1].confirmed = false;
    }

    public boolean isComplete()
    {
        return this.players[0].confirmed && this.players[1].confirmed;
    }

    //Core
    public TradeOffer offer(String ID)
    {
        return this.players[this.indexOf(ID)].offer;
    }

    public void setPlayers(String p1, String p2)
    {
        this.players = new TradePlayer[]{new TradePlayer(p1), new TradePlayer(p2)};
    }

    public boolean hasPlayer(String ID)
    {
        return this.players[0].ID.equals(ID) || this.players[1].ID.equals(ID);
    }

    public TradePlayer[] getPlayers()
    {
        return this.players;
    }

    public int indexOf(String ID)
    {
        return this.getPlayers()[0].ID.equals(ID) ? 0 : 1;
    }

    public void setStatus(TradeStatus status)
    {
        this.status = status;
    }

    public TradeStatus getStatus()
    {
        return this.status;
    }
}
