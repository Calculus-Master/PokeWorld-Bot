package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.enums.elements.EggGroup;
import com.calculusmaster.pokecord.game.enums.elements.Gender;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.PokemonRarity;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.helpers.CacheHelper;
import com.calculusmaster.pokecord.util.helpers.SettingsHelper;
import com.calculusmaster.pokecord.util.interfaces.Transformer;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.collections4.list.TreeList;

import java.util.*;
import java.util.function.Predicate;
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
        //this.buildList();

        if(CacheHelper.DYNAMIC_CACHING_ACTIVE && (!CacheHelper.POKEMON_LISTS.containsKey(this.player.getId()) || CacheHelper.UUID_LISTS.get(this.player.getId()).size() != this.playerData.getPokemonList().size()))
        {
            event.getChannel().sendMessage(this.playerData.getMention() + ": Initializing your Pokemon list (this may take a while and will only happen once)!").queue();

            CacheHelper.createPokemonList(this.player.getId());

            event.getChannel().sendMessage(this.playerData.getMention() + ": Your pokemon list has been initialized! Running command...").queue();
        }

        this.pokemon = new TreeList<>(CacheHelper.POKEMON_LISTS.get(this.player.getId()));
        this.team = List.copyOf(this.playerData.getTeam());
        this.favorites = List.copyOf(this.playerData.getFavorites());
    }

    @Override
    public Command runCommand()
    {
        List<String> msg = Arrays.asList(this.msg);
        Stream<Pokemon> stream = this.pokemon.stream();

        stream = this.sortSearchNames(stream, msg, (p, s) -> p.getName().toLowerCase().contains(s), "--name");

        stream = this.sortSearchNames(stream, msg, (p, s) -> p.getNickname().toLowerCase().contains(s), "--nickname");

        stream = this.sortSearchNames(stream, msg, (p, s) -> p.getLearnedMoves().contains(Global.normalCase(s)), "--move");

        stream = this.sortNumeric(stream, msg, Pokemon::getLevel, "--level", "--lvl");

        stream = this.sortNumeric(stream, msg, Pokemon::getDynamaxLevel, "--dlevel", "--dlvl", "--dynamaxlevel");

        stream = this.sortNumeric(stream, msg, p -> (int)(p.getTotalIVRounded()), "--iv");

        stream = this.sortNumeric(stream, msg, Pokemon::getEVTotal, "--ev");

        stream = this.sortMachine(stream, msg, "--tm");

        stream = this.sortMachine(stream, msg, "--tr");

        stream = this.sortIsUUIDInList(stream, msg, this.team, "--team");

        stream = this.sortIsUUIDInList(stream, msg, this.favorites, "--favorites");

        stream = this.sortEnum(stream, msg, Type::cast, Pokemon::isType, "--type");

        stream = this.sortEnum(stream, msg, Type::cast, (p, t) -> p.getType()[0].equals(t), "--maintype", "--mtype");

        stream = this.sortEnum(stream, msg, Type::cast, (p, t) -> p.getType()[1].equals(t), "--sidetype", "--stype");

        stream = this.sortEnum(stream, msg, Gender::cast, (p, g) -> p.getGender().equals(g), "--gender", "--g");

        stream = this.sortEnum(stream, msg, EggGroup::cast, (p, e) -> p.getEggGroup().contains(e), "--egggroup", "--egg", "--eg");

        stream = this.sortGeneric(stream, msg, Pokemon::isShiny, "--shiny");

        stream = this.sortNumeric(stream, msg, p -> p.getIVs().get(Stat.HP), "--hpiv", "--healthiv");

        stream = this.sortNumeric(stream, msg, p -> p.getIVs().get(Stat.ATK), "--atkiv", "--attackiv");

        stream = this.sortNumeric(stream, msg, p -> p.getIVs().get(Stat.DEF), "--defiv", "--defenseiv");

        stream = this.sortNumeric(stream, msg, p -> p.getIVs().get(Stat.SPATK), "--spatkiv", "--specialattackiv");

        stream = this.sortNumeric(stream, msg, p -> p.getIVs().get(Stat.SPDEF), "--spdefiv", "--specialdefenseiv");

        stream = this.sortNumeric(stream, msg, p -> p.getIVs().get(Stat.SPD), "--spdiv", "--speediv");

        stream = this.sortNumeric(stream, msg, p -> p.getEVs().get(Stat.HP), "--hpev", "--healthev");

        stream = this.sortNumeric(stream, msg, p -> p.getEVs().get(Stat.ATK), "--atkev", "--attackev");

        stream = this.sortNumeric(stream, msg, p -> p.getEVs().get(Stat.DEF), "--defev", "--defenseev");

        stream = this.sortNumeric(stream, msg, p -> p.getEVs().get(Stat.SPATK), "--spatkev", "--specialattackev");

        stream = this.sortNumeric(stream, msg, p -> p.getEVs().get(Stat.SPDEF), "--spdefev", "--specialdefenseev");

        stream = this.sortNumeric(stream, msg, p -> p.getEVs().get(Stat.SPD), "--spdev", "--speedev");

        stream = this.sortIsNameInList(stream, msg, PokemonRarity.LEGENDARY, "--legendary", "--leg");

        stream = this.sortIsNameInList(stream, msg, PokemonRarity.MYTHICAL, "--mythical", "--myth");

        stream = this.sortIsNameInList(stream, msg, PokemonRarity.ULTRA_BEAST, "--ub", "--ultrabeast", "--ultra", "--beast");

        stream = this.sortGeneric(stream, msg, p -> p.getName().toLowerCase().contains("mega"), "--mega");

        stream = this.sortGeneric(stream, msg, p -> p.getName().toLowerCase().contains("primal"), "--primal");

        stream = this.sortGeneric(stream, msg, p -> p.getName().toLowerCase().contains("mega") || p.getName().toLowerCase().contains("primal"), "--mega|primal", "--primal|mega");

        //Convert Stream to List
        this.pokemon = stream.collect(Collectors.toList());

        if(msg.contains("--order") && msg.indexOf("--order") + 1 < msg.size())
        {
            String order = msg.get(msg.indexOf("--order") + 1);
            boolean asc = msg.indexOf("--order") + 2 < msg.size() && msg.get(msg.indexOf("--order") + 2).equals("a");
            OrderSort o = OrderSort.cast(order);
            if(o != null) this.sortOrder(o, !asc);
        }
        else this.sortOrder(OrderSort.cast(this.playerData.getSettings().getSettingString(SettingsHelper.Setting.CLIENT_DEFAULT_ORDER)), false);

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

    private Stream<Pokemon> sortGeneric(Stream<Pokemon> stream, List<String> msg, Predicate<Pokemon> predicate, String... flags)
    {
        if(msg.stream().anyMatch(s -> Arrays.asList(flags).contains(s))) return stream.filter(predicate);
        else return stream;
    }

    private Stream<Pokemon> sortMachine(Stream<Pokemon> stream, List<String> msg, String flag)
    {
        boolean tm = flag.equals("--tm");

        int index = msg.indexOf(flag) + 1;
        String input = msg.get(index);

        String machineFlag = flag.replaceAll("--", "");
        if(input.startsWith(machineFlag) && input.length() > machineFlag.length()) input = input.substring(machineFlag.length());

        if(!input.equals("") && input.chars().allMatch(Character::isDigit))
        {
            int num = Integer.parseInt(input);
            if(tm && num >= 1 && num <= 100) return stream.filter(p -> p.getAllValidTMs().contains(num));
            else if(num >= 0 && num <= 99) return stream.filter(p -> p.getAllValidTRs().contains(num));
            else return stream;
        }
        else return stream;
    }

    private Stream<Pokemon> sortSearchNames(Stream<Pokemon> stream, List<String> msg, Matcher matcher, String... flags)
    {
        String flag = "";
        for(String s : flags) if(msg.contains(s) && msg.indexOf(s) + 1 < msg.size()) flag = s;

        if(msg.contains(flag) && msg.indexOf(flag) + 1 < msg.size())
        {
            final String lambdaFlag = flag;
            return stream.filter(p -> getSearchNames(msg, lambdaFlag).stream().anyMatch(s -> matcher.match(p, s)));
        }
        else return stream;
    }

    private Stream<Pokemon> sortIsUUIDInList(Stream<Pokemon> stream, List<String> msg, List<String> validList, String... flags)
    {
        if(msg.stream().anyMatch(s -> Arrays.asList(flags).contains(s))) return stream.filter(p -> validList.contains(p.getUUID()));
        else return stream;
    }

    private Stream<Pokemon> sortIsNameInList(Stream<Pokemon> stream, List<String> msg, List<String> validList, String... flags)
    {
        if(msg.stream().anyMatch(s -> Arrays.asList(flags).contains(s))) return stream.filter(p -> validList.contains(p.getName()));
        else return stream;
    }

    private <T extends Enum<T>> Stream<Pokemon> sortEnum(Stream<Pokemon> stream, List<String> msg, Caster<T> caster, EnumChecker<T> checker, String... flags)
    {
        String flag = "";
        for(String s : flags) if(msg.contains(s) && msg.indexOf(s) + 1 < msg.size()) flag = s;

        if(!flag.equals(""))
        {
            T enumValue = caster.cast(msg.get(msg.indexOf(flag) + 1));

            if(enumValue != null) return stream.filter(p -> checker.has(p, enumValue));
            else return stream;
        }
        else return stream;
    }

    private interface Matcher
    {
        boolean match(Pokemon p, String s);
    }

    private interface Caster<T extends Enum<T>>
    {
        T cast(String s);
    }

    private interface EnumChecker<E extends Enum<E>>
    {
        boolean has(Pokemon p, E value);
    }

    private Stream<Pokemon> sortNumeric(Stream<Pokemon> stream, List<String> msg, Transformer<Pokemon, Integer> value, String... flags)
    {
        String flag = "";
        for(String s : flags) if(msg.contains(s) && msg.indexOf(s) + 1 < msg.size()) flag = s;

        if(!flag.equals(""))
        {
            int index = msg.indexOf(flag) + 1;
            String after = msg.get(index);

            boolean valid = index + 1 < msg.size();

            if(after.equals(">") && valid && this.isNumeric(index + 1)) return stream.filter(p -> value.transform(p) > this.getInt(index + 1));
            else if(after.equals("<") && valid && this.isNumeric(index + 1)) return stream.filter(p -> value.transform(p) < this.getInt(index + 1));
            else if(this.isNumeric(index)) return stream.filter(p -> value.transform(p) == this.getInt(index));
            else return stream;
        }
        else return stream;
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
        StringBuilder sb = new StringBuilder();
        boolean hasPage = this.msg.length >= 2 && this.isNumeric(1);
        int perPage = 20;
        int startIndex = hasPage ? ((getInt(1) - 1) * perPage > this.pokemon.size() ? 0 : getInt(1)) : 0;
        if(startIndex != 0) startIndex--;

        startIndex *= perPage;
        int endIndex = Math.min(startIndex + perPage, this.pokemon.size());
        for(int i = startIndex; i < endIndex; i++)
        {
            sb.append(this.getLine(this.pokemon.get(i)));
        }

        this.embed.setDescription(sb.toString());
        this.embed.setTitle(this.player.getName() + "'s Pokemon");
        this.embed.setFooter("Showing Numbers " + (startIndex + 1) + " to " + (endIndex) + " out of " + this.pokemon.size() + " Pokemon");
    }

    private String getLine(Pokemon p)
    {
        return "**" + p.getDisplayName() + "**" +
                (p.isShiny() ? ":star2:" : "") + " " +
                (this.team.contains(p.getUUID()) ? ":regional_indicator_t: " : "") +
                (this.favorites.contains(p.getUUID()) ? ":regional_indicator_f: " : "") +
                "| Number: " + p.getNumber() +
                " | Level " + p.getLevel() +
                (this.playerData.getSettings().getSettingBoolean(SettingsHelper.Setting.CLIENT_DETAILED) ? " | IV: " + p.getTotalIV() : "") +
                "\n";
    }
}
