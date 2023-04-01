package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.Pokecord;
import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.enums.elements.Ability;
import com.calculusmaster.pokecord.game.enums.elements.EggGroup;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.component.PokemonStats;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonData;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.mongo.CollectionsQuery;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.util.*;
import java.util.stream.Collectors;

public class CommandDex extends Command
{
    public CommandDex(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.VIEW_DEX_INFO)) return this.invalidMasteryLevel(Feature.VIEW_DEX_INFO);

        if(this.msg.length == 1)
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }

        //Dex Generic Info Command
        boolean isShiny = this.msg[1].equalsIgnoreCase("shiny");
        boolean isGigantamax = this.msg[isShiny ? 2 : 1].equalsIgnoreCase("gigantamax");

        if(!isPokemon(this.getPokemonName()) )//TODO: In / command, add boolean options for GMax, Shiny, etc|| (isGigantamax && !DataHelper.hasGigantamax(Global.normalize(this.getPokemonName()))))
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }

        String pokemon = Global.normalize(this.getPokemonName());

        PokemonEntity entity = PokemonEntity.cast(pokemon);
        PokemonData data = entity.data();

        String flavor = data.getFlavorText().isEmpty() ? "No Flavor Text Available" : data.getFlavorText().get(new Random().nextInt(data.getFlavorText().size()));

        this.embed
                .setDescription(data.getGenus() + "\nHeight: " + data.getHeight() + "     |     Weight: " + data.getWeight() + "\n" + flavor)
                .addField("Type", data.getTypes().get(0).equals(data.getTypes().get(1)) ? data.getTypes().get(0).getStyledName() : data.getTypes().get(0).getStyledName() + "\n" + data.getTypes().get(1).getStyledName(), true)
                .addField("Abilities", data.getMainAbilities().stream().map(Ability::getName).collect(Collectors.joining("\n")), true)
                .addField("Egg Group", this.listToMultiLineString(data.getEggGroups().stream().map(EggGroup::getName).toList()), true)
                .addField("Growth Rate", Global.normalize(data.getGrowthRate().toString().replaceAll("_", "")), true)
                .addField("EXP Yield", String.valueOf(data.getBaseExperience()), true)
                .addField("EV Yield", this.getEVYield(data.getEVYield()), true)
                .addField("Evolutions", this.getEvolutionsFormatted(new HashMap<>()), true)
                .addField("Forms", this.listToMultiLineString(new ArrayList<>()), true)
                .addField("Mega", this.listToMultiLineString(new ArrayList<>()), true)
                .addField("TMs", data.getTMs().isEmpty() ? "None" : data.getTMs().toString().substring(1, data.getTMs().toString().length() - 1).replaceAll("TM", ""), false)
                .addField(this.getStatsField(data));

        String image = Pokemon.getImage(entity, isShiny, null, null);
        String imageAttachmentName = "info_" + entity.toString().toLowerCase() + ".png";
        this.embed.setImage("attachment://" + imageAttachmentName);

        this.embed.setTitle("**" + data.getName() + (isShiny ? ":star2:" : "") + " (#" + data.getDex() + " – Gen. " + Global.getGeneration(data) + ")**");
        this.color = data.getTypes().get(0).getColor();
        this.embed.setImage(image.equals("") ? Pokemon.getWIPImage() : image);
        this.embed.setFooter("You have collected " + new CollectionsQuery(pokemon, this.player.getId()).getCaughtAmount() + "!");

        this.event.getChannel().sendFiles(FileUpload.fromData(Pokecord.class.getResourceAsStream(image), imageAttachmentName)).setEmbeds(this.embed.build()).queue();
        this.embed = null;

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

    private String getEVYield(PokemonStats stats)
    {
        Map<Stat, Integer> yield = stats.get();
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
        for(Stat s : Stat.values()) sb.append("**").append(s.shortName()).append("**: ").append(p.getBaseStats().get().get(s)).append("\n");
        sb.append("**Total**: ").append(p.getBaseStats().get().values().stream().mapToInt(s -> s).sum());

        return new MessageEmbed.Field("Base Stats", sb.toString(), false);
    }
}
