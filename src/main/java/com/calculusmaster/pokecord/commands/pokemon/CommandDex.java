package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.PokemonData;
import com.calculusmaster.pokecord.mongo.CollectionsQuery;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.helpers.DataHelper;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CommandDex extends Command
{
    public CommandDex(MessageReceivedEvent event, String[] msg)
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

        //Dex Generic Info Command
        boolean isShiny = this.msg[1].equalsIgnoreCase("shiny");
        boolean isGigantamax = this.msg[isShiny ? 2 : 1].equalsIgnoreCase("gigantamax");

        if(!isPokemon(this.getPokemonName()) || (isGigantamax && !Pokemon.existsGigantamax(Global.normalCase(this.getPokemonName()))))
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }

        String pokemon = Global.normalCase(this.getPokemonName());

        PokemonData data = DataHelper.pokeData(pokemon);
        String flavor = DataHelper.POKEMON_SPECIES_DESC.get(data.dex).get(new Random().nextInt(DataHelper.POKEMON_SPECIES_DESC.get(data.dex).size()));

        this.embed
                .setDescription(data.species + "\nHeight: " + data.height + "     |     Weight: " + data.weight + "\n" + flavor)
                .addField("Type", data.types.get(0).equals(data.types.get(1)) ? data.types.get(0).getStyledName() : data.types.get(0).getStyledName() + "\n" + data.types.get(1).getStyledName(), true)
                .addField("Abilities", this.listToMultiLineString(data.abilities), true)
                .addField("Egg Group", DataHelper.POKEMON_EGG_GROUPS.get(data.dex).getName(), true)
                .addField("Growth Rate", Global.normalCase(data.growthRate.toString().replaceAll("_", "")), true)
                .addField("EXP Yield", String.valueOf(data.baseEXP), true)
                .addField("EV Yield", this.getEVYield(data.yield), true)
                .addField("Evolutions", this.getEvolutionsFormatted(data.evolutions), true)
                .addField("Forms", this.listToMultiLineString(data.forms), true)
                .addField("Mega", this.listToMultiLineString(data.megas), true)
                .addField("TMs", data.validTMs.isEmpty() ? "None" : data.validTMs.toString().substring(1, data.validTMs.toString().length() - 1).replaceAll("TM", ""), false)
                .addField("TRs", data.validTRs.isEmpty() ? "None" : data.validTRs.toString().substring(1, data.validTRs.toString().length() - 1).replaceAll("TR", ""), false)
                .addField(this.getStatsField(data));

        String image = isGigantamax ? (isShiny ? Pokemon.getGigantamaxData(pokemon).shinyImage() : Pokemon.getGigantamaxData(pokemon).normalImage()) : (isShiny ? data.shinyURL : data.normalURL);

        if(pokemon.equals("Deerling")) image = Global.getDeerlingImage(isShiny);
        else if(pokemon.equals("Sawsbuck")) image = Global.getSawsbuckImage(isShiny);

        this.embed.setTitle("**" + data.name + (isShiny ? ":star2:" : "") + " (#" + data.dex + ")**");
        this.color = data.types.get(0).getColor();
        this.embed.setImage(image.equals("") ? Pokemon.getWIPImage() : image);
        this.embed.setFooter("You have collected " + new CollectionsQuery(pokemon, this.player.getId()).getCaughtAmount() + "!");

        this.playerData.addPokePassExp(50, this.event);
        return this;
    }

    private String getPokemonName()
    {
        return this.getMultiWordContent(1).replaceAll("shiny", "").replaceAll("gigantamax", "").trim();
    }

    private String listToMultiLineString(List<String> list)
    {
        if(list.isEmpty()) return "None";
        StringBuilder s = new StringBuilder();
        for(String str : list) s.append(str).append("\n");
        return s.deleteCharAt(s.length() - 1).toString();
    }

    private String getEVYield(Map<Stat, Integer> yield)
    {
        StringBuilder s = new StringBuilder();
        for(Stat stat : yield.keySet()) if(yield.get(stat) != 0) s.append(stat.toString()).append(": ").append(yield.get(stat)).append("\n");
        return s.deleteCharAt(s.length() - 1).toString();
    }

    private String getEvolutionsFormatted(Map<String, Integer> evos)
    {
        StringBuilder s = new StringBuilder();
        List<String> names = new ArrayList<>(evos.keySet());
        switch (names.size())
        {
            case 0 -> s.append("Either does not evolve or has a special evolution");
            case 1 -> s.append("Evolves into ").append(names.get(0)).append(" at Level ").append(evos.get(names.get(0)));
            case 2 -> s.append("Evolves into ").append(names.get(0)).append(" at Level ").append(evos.get(names.get(0))).append(" and ").append(names.get(1)).append(" at level ").append(evos.get(names.get(1)));
            default -> s.append("ERROR – REPORT");
        }
        return s.toString();
    }

    private MessageEmbed.Field getStatsField(PokemonData p)
    {
        StringBuilder sb = new StringBuilder();
        for(Stat s : Stat.values()) sb.append("**").append(s.shortName()).append("**: ").append(p.baseStats.get(s)).append("\n");
        sb.append("**Total**: ").append(p.baseStats.values().stream().mapToInt(s -> s).sum());

        return new MessageEmbed.Field("Base Stats", sb.toString(), false);
    }
}
