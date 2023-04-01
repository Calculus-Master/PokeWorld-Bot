package com.calculusmaster.pokecord.game.trade.elements;

import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.trade.TradeHelper;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TradeOffer
{
    public PlayerDataQuery player;

    public int credits;
    public int redeems;
    public List<String> pokemon;
    public List<TM> tms;

    public TradeOffer(String playerID)
    {
        this.player = PlayerDataQuery.of(playerID);

        this.clear();
    }

    public void clear()
    {
        for(TradeHelper.OfferType t : TradeHelper.OfferType.values()) this.clear(t);
    }

    public void clear(TradeHelper.OfferType type)
    {
        switch(type)
        {
            case CREDITS -> this.credits = 0;
            case REDEEMS -> this.redeems = 0;
            case POKEMON -> this.pokemon = new ArrayList<>();
            case TM -> this.tms = new ArrayList<>();
        }
    }

    public boolean isValid()
    {
        if(this.credits > 0)
        {
            if(this.player.getCredits() < this.credits) return false;
        }

        if(this.redeems > 0)
        {
            if(this.player.getRedeems() < this.redeems) return false;
        }

        if(!this.pokemon.isEmpty())
        {
            for(String s : this.pokemon) if(!this.player.getPokemonList().contains(s)) return false;
        }

        if(!this.tms.isEmpty())
        {
            for(TM tm : this.tms) if(!this.player.getTMList().contains(tm.toString())) return false;
        }

        return true;
    }

    public void transfer(PlayerDataQuery receiver)
    {
        if(this.credits > 0)
        {
            this.player.changeCredits(-1 * this.credits);

            receiver.changeCredits(this.credits);
        }

        if(this.redeems > 0)
        {
            this.player.changeRedeems(-1 * this.redeems);

            receiver.changeRedeems(this.redeems);
        }

        if(!this.pokemon.isEmpty())
        {
            for(String p : this.pokemon)
            {
                this.player.removePokemon(p);

                receiver.addPokemon(p);
            }
        }

        if(!this.tms.isEmpty())
        {
            for(TM tm : this.tms)
            {
                this.player.removeTM(tm.toString());

                receiver.addTM(tm.toString());
            }
        }
    }

    public String asString()
    {
        StringBuilder sb = new StringBuilder("```\n");

        if(this.credits > 0)
        {
            sb.append("Credits: ").append(this.credits).append("\n");
        }

        if(this.redeems > 0)
        {
            sb.append("Redeems: ").append(this.redeems).append("\n");
        }

        if(!this.pokemon.isEmpty())
        {
            for(String p : this.pokemon)
            {
                Pokemon poke = Pokemon.build(p);
                sb.append("Level ").append(poke.getLevel()).append(" ").append(poke.getName()).append(" (").append(poke.getTotalIV()).append(")\n");
            }
        }

        if(!this.tms.isEmpty())
        {
            sb.append("TMs: ").append(String.join(", ", this.tms.stream().map(TM::toString).toList())).append("\n");
        }

        if(sb.toString().equals("```\n")) sb.append("Nothing\n");

        sb.append("```");

        return sb.toString();
    }

    public void add(TradeHelper.OfferType offerType, int i)
    {
        switch(offerType)
        {
            case CREDITS -> this.credits += i;
            case REDEEMS -> this.redeems += i;
        }
    }

    public void add(TradeHelper.OfferType offerType, String s)
    {
        switch(offerType)
        {
            case TM -> this.tms.add(TM.cast(s));
            case POKEMON -> {
                this.pokemon.add(s);
                this.pokemon = this.pokemon.stream().distinct().collect(Collectors.toList());
            }
        }
    }

    public void remove(TradeHelper.OfferType offerType, int i)
    {
        switch(offerType)
        {
            case CREDITS -> this.credits -= i;
            case REDEEMS -> this.redeems -= i;
        }
    }

    public void remove(TradeHelper.OfferType offerType, String s)
    {
        switch(offerType)
        {
            case POKEMON -> this.pokemon.remove(s);
            case TM -> this.tms.remove(TM.cast(s));
        }
    }
}
