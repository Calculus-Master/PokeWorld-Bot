package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.enums.elements.EggGroup;
import com.calculusmaster.pokecord.game.enums.elements.Gender;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.game.player.Settings;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.PokemonListSorter;
import com.calculusmaster.pokecord.game.pokemon.PokemonRarity;
import com.calculusmaster.pokecord.game.pokemon.PokemonSorterFlag;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.collections4.list.TreeList;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandPokemon extends Command
{
    private List<Pokemon> pokemon;
    private List<String> team;
    private List<String> favorites;

    public CommandPokemon(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);

        this.pokemon = new TreeList<>(this.playerData.getPokemon());
        this.team = List.copyOf(this.playerData.getTeam());
        this.favorites = List.copyOf(this.playerData.getFavorites());
    }

    @Override
    public Command runCommand()
    {
        List<String> msg = Arrays.asList(this.msg);
        //If the number of search parameters and the size of the search list is large enough, use a parallel stream. Otherwise a sequential stream should be fine
        Stream<Pokemon> stream = msg.size() > 4 && this.pokemon.size() > 500 ? this.pokemon.parallelStream() : this.pokemon.stream();

        PokemonListSorter sorter = new PokemonListSorter(stream, msg);

        sorter.sortSearchName(PokemonSorterFlag.NAME, (p, s) -> p.getName().toLowerCase().contains(s));

        sorter.sortSearchName(PokemonSorterFlag.NICKNAME, (p, s) -> p.getNickname().toLowerCase().contains(s));

        sorter.sortSearchName(PokemonSorterFlag.MOVE, (p, s) -> p.allMoves().contains(Global.normalize(s)));

        sorter.sortSearchName(PokemonSorterFlag.LEARNED_MOVE, (p, s) -> p.getMoves().contains(Global.normalize(s)));

        sorter.sortSearchName(PokemonSorterFlag.AVAILABLE_MOVE, (p, s) -> p.availableMoves().contains(Global.normalize(s)));

        sorter.sortNumeric(PokemonSorterFlag.LEVEL, Pokemon::getLevel);

        sorter.sortNumeric(PokemonSorterFlag.LEVEL, Pokemon::getDynamaxLevel);

        sorter.sortNumeric(PokemonSorterFlag.IV, p -> (int)(p.getTotalIVRounded()));

        sorter.sortNumeric(PokemonSorterFlag.EV, Pokemon::getEVTotal);

        sorter.sortMachine(PokemonSorterFlag.TM);

        sorter.sortMachine(PokemonSorterFlag.TR);

        sorter.sortIsUUIDInList(PokemonSorterFlag.TEAM, this.team);

        sorter.sortIsUUIDInList(PokemonSorterFlag.FAVORITES, this.favorites);

        sorter.sortEnum(PokemonSorterFlag.TYPE, Type::cast, Pokemon::isType);

        sorter.sortEnum(PokemonSorterFlag.MAIN_TYPE, Type::cast, (p, t) -> p.getType()[0].equals(t));

        sorter.sortEnum(PokemonSorterFlag.SIDE_TYPE, Type::cast, (p, t) -> p.getType()[1].equals(t));

        sorter.sortEnum(PokemonSorterFlag.GENDER, Gender::cast, (p, g) -> p.getGender().equals(g));

        sorter.sortEnum(PokemonSorterFlag.EGG_GROUP, EggGroup::cast, (p, e) -> p.getEggGroups().contains(e));

        sorter.sortGeneric(PokemonSorterFlag.SHINY, Pokemon::isShiny);

        sorter.sortGeneric(PokemonSorterFlag.MASTERED, Pokemon::isMastered);

        sorter.sortNumeric(PokemonSorterFlag.HPIV, p -> p.getIVs().get(Stat.HP));

        sorter.sortNumeric(PokemonSorterFlag.ATKIV, p -> p.getIVs().get(Stat.ATK));

        sorter.sortNumeric(PokemonSorterFlag.DEFIV, p -> p.getIVs().get(Stat.DEF));

        sorter.sortNumeric(PokemonSorterFlag.SPATKIV, p -> p.getIVs().get(Stat.SPATK));

        sorter.sortNumeric(PokemonSorterFlag.SPDEFIV, p -> p.getIVs().get(Stat.SPDEF));

        sorter.sortNumeric(PokemonSorterFlag.SPDIV, p -> p.getIVs().get(Stat.SPD));

        sorter.sortNumeric(PokemonSorterFlag.HPEV, p -> p.getEVs().get(Stat.HP));

        sorter.sortNumeric(PokemonSorterFlag.ATKEV, p -> p.getEVs().get(Stat.ATK));

        sorter.sortNumeric(PokemonSorterFlag.DEFEV, p -> p.getEVs().get(Stat.DEF));

        sorter.sortNumeric(PokemonSorterFlag.SPATKEV, p -> p.getEVs().get(Stat.SPATK));

        sorter.sortNumeric(PokemonSorterFlag.SPDEFEV, p -> p.getEVs().get(Stat.SPDEF));

        sorter.sortNumeric(PokemonSorterFlag.SPDEV, p -> p.getEVs().get(Stat.SPD));

        sorter.sortIsNameInList(PokemonSorterFlag.LEGENDARY, PokemonRarity.LEGENDARY);

        sorter.sortIsNameInList(PokemonSorterFlag.MYTHICAL, PokemonRarity.MYTHICAL);

        sorter.sortIsNameInList(PokemonSorterFlag.ULTRA_BEAST, PokemonRarity.ULTRA_BEAST);

        sorter.sortGeneric(PokemonSorterFlag.MEGA, p -> p.getName().toLowerCase().contains("mega"));

        sorter.sortGeneric(PokemonSorterFlag.PRIMAL, p -> p.getName().toLowerCase().contains("primal"));

        sorter.sortGeneric(PokemonSorterFlag.MEGA_OR_PRIMAL, p -> p.getName().toLowerCase().contains("mega") || p.getName().toLowerCase().contains("primal"));

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

    public static List<String> getSearchNames(List<String> msg, String flag)
    {
        int start = msg.indexOf(flag) + 1;
        int end = msg.size() - 1;

        for(int i = start; i < msg.size(); i++)
        {
            if(msg.get(i).contains("--"))
            {
                end = i - 1;
                i = msg.size();
            }
        }

        StringBuilder names = new StringBuilder();

        for(int i = start; i <= end; i++)
        {
            names.append(msg.get(i)).append(" ");
        }

        String delimiter = "\\|"; //Currently the OR delimiter is |

        return new ArrayList<>(Arrays.asList(names.toString().trim().split(delimiter))).stream().map(String::trim).map(String::toLowerCase).collect(Collectors.toList());
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
        boolean detailed = this.playerData.getSettings().get(Settings.CLIENT_DETAILED, Boolean.class);

        return new MessageEmbed.Field(p.getDisplayName(),
                this.getCategoryFlags(p) + "\n" +
                "Number: " + p.getNumber() + " | " +
                "Level: " + p.getLevel() + "\n" +
                (detailed ? "IV: " + p.getTotalIV() + "\n" : "") +
                (detailed ? "EV: " + p.getEVTotal() + "\n" : ""),
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
        StringBuilder sb = new StringBuilder();

        for(int i = start; i < end; i++) sb.append(this.getLine(this.pokemon.get(i)));

        this.embed.setDescription(sb.toString());
    }

    private String getLine(Pokemon p)
    {
        return "**" + p.getDisplayName() + "** " +
                (p.isShiny() ? ":star2: " : "") +
                (p.isMastered() ? ":trophy: " : "") +
                (this.team.contains(p.getUUID()) ? ":regional_indicator_t: " : "") +
                (this.favorites.contains(p.getUUID()) ? ":regional_indicator_f: " : "") +
                "| Number: " + p.getNumber() +
                " | Level " + p.getLevel() +
                (this.playerData.getSettings().get(Settings.CLIENT_DETAILED, Boolean.class) ? " | IV: " + p.getTotalIV() : "") +
                "\n";
    }
}
