package com.calculusmaster.pokecord.game.pokemon.sort;

import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.trade.elements.MarketEntry;
import com.calculusmaster.pokecord.util.interfaces.Transformer;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MarketListSorter
{
    private Stream<MarketEntry> stream;
    private List<String> msg;

    private Stream<Pokemon> pokemonStream;

    public MarketListSorter(Stream<MarketEntry> stream, List<String> msg)
    {
        this.stream = stream;
        this.msg = msg;
    }

    public Stream<MarketEntry> rebuildStream()
    {
        return this.pokemonStream.map(this::entry);
    }

    public PokemonListSorter convert()
    {
        this.pokemonStream = this.stream.map(m -> m.pokemon);
        return new PokemonListSorter(this.pokemonStream, this.msg);
    }

    private MarketEntry entry(Pokemon p)
    {
        return this.stream.filter(m -> m.pokemonID.equals(p.getUUID())).findFirst().orElseThrow();
    }

    //Sorters

    public void sortGeneric(MarketSorterFlag enumFlag, Predicate<MarketEntry> predicate)
    {
        if(this.hasFlag(enumFlag)) this.stream = this.stream.filter(predicate);
    }

    public void sortNumeric(MarketSorterFlag enumFlag, Transformer<MarketEntry, Integer> value)
    {
        String flag = this.getExistentFlag(enumFlag);

        if(!flag.equals(""))
        {
            int index = this.msg.indexOf(flag) + 1;
            String after = this.msg.get(index);

            boolean valid = index + 1 < this.msg.size();

            if(after.equals(">") && valid && this.isNumeric(index + 1)) this.stream = this.stream.filter(m -> value.transform(m) > this.getInt(index + 1));
            else if(after.equals("<") && valid && this.isNumeric(index + 1)) this.stream = this.stream.filter(m -> value.transform(m) < this.getInt(index + 1));
            else if(this.isNumeric(index)) this.stream = this.stream.filter(m -> value.transform(m) == this.getInt(index));
        }
    }

    //Core

    private boolean hasFlag(MarketSorterFlag flag)
    {
        return this.msg.stream().anyMatch(s -> (flag.flags.contains(s)));
    }

    private int getInt(int index)
    {
        return Integer.parseInt(this.msg.get(index));
    }

    private boolean isNumeric(int index)
    {
        return this.msg.get(index).chars().allMatch(Character::isDigit);
    }

    private String getExistentFlag(MarketSorterFlag flag)
    {
        String existent = "";
        for(String s : flag.flags) if(this.msg.contains(s) && this.msg.indexOf(s) + 1 < this.msg.size()) existent = s;
        return existent;
    }
}
