package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandPokemon extends Command
{
    List<Pokemon> pokemon = new LinkedList<>();
    public CommandPokemon(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
        this.buildList();
    }

    @Override
    public Command runCommand()
    {
        List<String> msg = Arrays.asList(this.msg);

        if(msg.contains("--name") && msg.indexOf("--name") + 1 < msg.size())
        {
            String name = msg.get(msg.indexOf("--name") + 1);
            if(isPokemon(name)) this.pokemon = this.pokemon.stream().filter(p -> p.getName().equals(Global.normalCase(name))).collect(Collectors.toList());
        }

        if(msg.contains("--level") && msg.indexOf("--level") + 1 < msg.size())
        {
            int index = msg.indexOf("--level") + 1;
            String after = msg.get(index);
            if(after.equals(">") && isNumeric(index + 1)) this.pokemon = this.pokemon.stream().filter(p -> p.getLevel() > getInt(index + 1)).collect(Collectors.toList());
            else if(after.equals("<") && isNumeric(index + 1)) this.pokemon = this.pokemon.stream().filter(p -> p.getLevel() < getInt(index + 1)).collect(Collectors.toList());
            else if(isNumeric(index)) this.pokemon = this.pokemon.stream().filter(p -> p.getLevel() == getInt(index)).collect(Collectors.toList());
        }

        if(msg.contains("--iv") && msg.indexOf("--iv") + 1 < msg.size())
        {
            int index = msg.indexOf("--iv") + 1;
            String after = msg.get(index);
            if(after.equals(">") && isNumeric(index + 1)) this.pokemon = this.pokemon.stream().filter(p -> p.getTotalIVRounded() > getInt(index + 1)).collect(Collectors.toList());
            else if(after.equals("<") && isNumeric(index + 1)) this.pokemon = this.pokemon.stream().filter(p -> p.getTotalIVRounded() < getInt(index + 1)).collect(Collectors.toList());
            else if(isNumeric(index)) this.pokemon = this.pokemon.stream().filter(p -> (int)p.getTotalIVRounded() == getInt(index)).collect(Collectors.toList());
        }

        //TODO: --hpiv, --atkiv, --defiv, --spatkiv, --spdefiv, --spdiv

        if(msg.contains("--order") && msg.indexOf("--order") + 1 < msg.size())
        {
            String order = msg.get(msg.indexOf("--order") + 1);
            OrderSort o = OrderSort.cast(order);
            if(o != null) this.sortOrder(o);
        }
        else this.sortOrder(OrderSort.NUMBER);

        this.createListEmbed();

        return this;
    }

    private void sortOrder(OrderSort o)
    {
        switch (o)
        {
            case NUMBER -> this.pokemon.sort(Comparator.comparingInt(Pokemon::getNumber));
            case IV -> this.pokemon.sort((o1, o2) -> (int) (Double.parseDouble(o2.getTotalIV().substring(0, 5)) - Double.parseDouble(o1.getTotalIV().substring(0, 5))));
            case LEVEL -> this.pokemon.sort((o1, o2) -> o2.getLevel() - o1.getLevel());
            case NAME -> this.pokemon.sort(Comparator.comparing(Pokemon::getName));
        }
    }

    enum OrderSort
    {
        NUMBER,
        IV,
        LEVEL,
        NAME;

        static OrderSort cast(String s)
        {
            for(OrderSort o : values()) if(o.toString().equals(s.toUpperCase())) return o;
            return null;
        }
    }

    //Do sorting before this
    private void createListEmbed()
    {
        StringBuilder sb = new StringBuilder();
        boolean hasPage = this.msg.length >= 2 && this.isNumeric(1);
        int perPage = 20;
        int startIndex = hasPage ? ((getInt(1) - 1) * perPage > this.pokemon.size() ? 0 : getInt(1)) : 0;
        if(startIndex != 0) startIndex--;

        startIndex *= perPage;
        int endIndex = Math.min(startIndex + perPage, this.pokemon.size());
        for(int i = startIndex; i < endIndex; i++)
        {
            if(i > this.pokemon.size() - 1) break;
            sb.append(this.getLine(this.pokemon.get(i), i));
        }

        this.embed.setDescription(sb.toString());
        this.embed.setTitle(this.player.getName() + "'s Pokemon");
        this.embed.setFooter("Showing Numbers " + (startIndex + 1) + " to " + (endIndex) + " out of " + this.pokemon.size() + " Pokemon");
    }

    private void buildList()
    {
        for(int i = 0; i < this.playerData.getPokemonList().length(); i++) this.pokemon.add(Pokemon.buildCore(this.playerData.getPokemonList().getString(i), i));
    }

    private String getLine(Pokemon p, int index)
    {
        return "**" + p.getName() + "** | Number: " + (index + 1) + " | Level " + p.getLevel() + " | Total IV: " + p.getTotalIV() + "\n";
    }

    private int jsonIndexOf(String UUID)
    {
        for(int i = 0; i < this.playerData.getPokemonList().length(); i++) if(this.playerData.getPokemonList().getString(i).equals(UUID)) return i;
        return -1;
    }
}
