package com.calculusmaster.pokecord.commandslegacy.pokemon;

import com.calculusmaster.pokecord.commandslegacy.CommandLegacy;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.game.player.Settings;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.sort.PokemonListSorter;
import com.calculusmaster.pokecord.game.pokemon.sort.PokemonSorterFlag;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.collections4.list.TreeList;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandLegacyPokemon extends CommandLegacy
{
    private List<Pokemon> pokemon;
    private final List<String> team;
    private final List<String> favorites;

    private final boolean detailed;

    public CommandLegacyPokemon(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);

        this.pokemon = new TreeList<>(this.playerData.getPokemon());
        this.team = List.copyOf(this.playerData.getTeam());
        this.favorites = List.copyOf(this.playerData.getFavorites());

        this.detailed = this.playerData.getSettings().get(Settings.CLIENT_DETAILED, Boolean.class);
    }

    @Override
    public CommandLegacy runCommand()
    {
        List<String> msg = Arrays.asList(this.msg);
        //If the number of search parameters and the size of the search list is large enough, use a parallel stream. Otherwise a sequential stream should be fine
        Stream<Pokemon> stream = msg.size() > 4 && this.pokemon.size() > 500 ? this.pokemon.parallelStream() : this.pokemon.stream();

        PokemonListSorter sorter = new PokemonListSorter(stream, msg);

        sorter.sortSearchName(PokemonSorterFlag.NAME, (p, s) -> p.getName().toLowerCase().contains(s));
        sorter.sortSearchName(PokemonSorterFlag.ENTITY, (p, s) -> p.getEntity().toString().toLowerCase().contains(s));
        sorter.sortSearchName(PokemonSorterFlag.NICKNAME, (p, s) -> p.getNickname().toLowerCase().contains(s));

        sorter.sortSearchName(PokemonSorterFlag.MOVE, (p, s) -> p.getLevelUpMoves().stream().anyMatch(me -> me.data().getName().contains(Global.normalize(s))));
        sorter.sortSearchName(PokemonSorterFlag.LEARNED_MOVE, (p, s) -> p.getMoves().stream().anyMatch(me -> me.data().getName().contains(Global.normalize(s))));
        sorter.sortSearchName(PokemonSorterFlag.AVAILABLE_MOVE, (p, s) -> p.availableMoves().stream().anyMatch(me -> me.data().getName().contains(Global.normalize(s))));

        sorter.sortMachine(PokemonSorterFlag.TM);

        sorter.sortIsUUIDInList(PokemonSorterFlag.TEAM, this.team);
        sorter.sortIsUUIDInList(PokemonSorterFlag.FAVORITES, this.favorites);

        //Standards

        sorter.sortStandardNumeric();
        sorter.sortStandardEnum();
        sorter.sortStandardBoolean();
        sorter.sortStats();
        sorter.sortNameCategories();

        //Reobtain the Stream from the PokemonListSorter object
        stream = sorter.retrieveStream();

        //Convert Stream to List
        this.pokemon = stream.collect(Collectors.toList());

        if(msg.contains("--order") && msg.indexOf("--order") + 1 < msg.size())
        {
            String order = msg.get(msg.indexOf("--order") + 1);
            boolean asc = msg.indexOf("--order") + 2 < msg.size() && msg.get(msg.indexOf("--order") + 2).equals("a");
            OrderSort o = OrderSort.cast(order);
            if(o != null) this.sortOrder(o, !asc);
        }
        else this.sortOrder();

        if(!this.pokemon.isEmpty()) this.createListEmbed();
        else this.embed.setDescription("You have no Pokemon with those characteristics!");

        int owned = this.playerData.getPokemonList().size();
        if(owned >= 10) Achievements.grant(this.player.getId(), Achievements.OWNED_10_POKEMON, this.event);
        if(owned >= 100) Achievements.grant(this.player.getId(), Achievements.OWNED_100_POKEMON, this.event);
        if(owned >= 500) Achievements.grant(this.player.getId(), Achievements.OWNED_500_POKEMON, this.event);
        if(owned >= 1000) Achievements.grant(this.player.getId(), Achievements.OWNED_1000_POKEMON, this.event);
        if(owned >= 5000) Achievements.grant(this.player.getId(), Achievements.OWNED_5000_POKEMON, this.event);
        if(owned >= 10000) Achievements.grant(this.player.getId(), Achievements.OWNED_10000_POKEMON, this.event);

        return this;
    }

    private void sortOrder()
    {
        OrderSort o = OrderSort.cast(this.playerData.getSettings().get(Settings.CLIENT_DEFAULT_ORDER, String.class));

        if(o == null) o = OrderSort.RANDOM;

        this.sortOrder(o, !Arrays.asList(OrderSort.NAME, OrderSort.RANDOM, OrderSort.NUMBER).contains(o));
    }

    private void sortOrder(OrderSort o, boolean descending)
    {
        if(o == null) o = OrderSort.RANDOM;

        switch (o)
        {
            case NUMBER -> this.pokemon.sort(Comparator.comparingInt(Pokemon::getNumber));
            case IV -> this.pokemon.sort(Comparator.comparingDouble(Pokemon::getTotalIVRounded));
            case EV -> this.pokemon.sort(Comparator.comparingInt(Pokemon::getEVTotal));
            case STAT -> this.pokemon.sort(Comparator.comparingInt(Pokemon::getTotalStat));
            case LEVEL -> this.pokemon.sort(Comparator.comparingInt(Pokemon::getLevel));
            case NAME -> this.pokemon.sort(Comparator.comparing(Pokemon::getName));
            case RANDOM -> Collections.shuffle(this.pokemon);
        }

        if(descending) Collections.reverse(this.pokemon);
    }

    public enum OrderSort
    {
        NUMBER,
        IV,
        EV,
        STAT,
        LEVEL,
        NAME,
        RANDOM;

        public static OrderSort cast(String s)
        {
            for(OrderSort o : values()) if(o.toString().equals(s.toUpperCase())) return o;
            return null;
        }
    }

    //Do sorting before this
    private void createListEmbed()
    {
        boolean fields = this.playerData.getSettings().get(Settings.CLIENT_POKEMON_LIST_FIELDS, Boolean.class);

        boolean hasPage = this.msg.length >= 2 && this.isNumeric(1);
        int perPage = fields ? 15 : 20;

        int start = 0;
        if(hasPage && (this.getInt(1) - 1) * perPage <= this.pokemon.size()) start = this.getInt(1);
        if(start != 0) start--;
        start *= perPage;

        int end = Math.min(start + perPage, this.pokemon.size());

        if(fields) this.createFieldListEmbed(start, end);
        else this.createTextListEmbed(start, end);

        this.embed.setTitle(this.player.getName() + "'s Pokemon");
        this.embed.setFooter("Showing Numbers " + (start + 1) + " to " + end + " out of " + this.pokemon.size() + " Pokemon");
    }

    private void createFieldListEmbed(int start, int end)
    {
        for(int i = start; i < end; i++) this.embed.addField(this.getField(this.pokemon.get(i)));
    }

    private MessageEmbed.Field getField(Pokemon p)
    {
        return new MessageEmbed.Field(p.getDisplayName(),
                this.getCategoryFlags(p) + "\n" +
                "Number: " + p.getNumber() + " | " +
                "Level: " + p.getLevel() + "\n" +
                (this.detailed ? "IV: " + p.getTotalIV() + "\n" : "") +
                (this.detailed ? "EV: " + p.getEVTotal() + "\n" : ""),
                true);
    }

    private String getCategoryFlags(Pokemon p)
    {
        return (p.isShiny() ? ":star2:" : "") +
                (this.team.contains(p.getUUID()) ? ":regional_indicator_t: " : "") +
                (this.favorites.contains(p.getUUID()) ? ":regional_indicator_f: " : "");
    }

    private void createTextListEmbed(int start, int end)
    {
        List<String> lines = new ArrayList<>();
        for(int i = start; i < end; i++) lines.add(this.getLine(this.pokemon.get(i)));

        this.embed.setDescription(String.join("\n", lines));
    }

    private String getLine(Pokemon p)
    {
        return Stream.of(
                "**" + p.getDisplayName() + "** " + this.getTags(p),
                "Number: " + p.getNumber(),
                "Level " + p.getLevel(),
                this.detailed ? "IV: " + p.getTotalIV() : "",
                this.detailed ? "EV: " + p.getEVTotal() : ""
        ).filter(s -> !s.isEmpty()).collect(Collectors.joining(" | "));
    }

    private String getTags(Pokemon p)
    {
        List<String> tags = new ArrayList<>();

        if(p.isShiny()) tags.add(":star2:");

        if(p.isMastered()) tags.add(":trophy:");

        if(p.hasPrestiged()) tags.add(":zap:");

        if(this.team.contains(p.getUUID())) tags.add(":regional_indicator_t:");

        if(this.favorites.contains(p.getUUID())) tags.add(":regional_indicator_f:");

        return String.join(" ", tags);
    }
}
