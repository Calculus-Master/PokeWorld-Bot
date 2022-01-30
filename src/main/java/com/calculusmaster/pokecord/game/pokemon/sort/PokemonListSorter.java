package com.calculusmaster.pokecord.game.pokemon.sort;

import com.calculusmaster.pokecord.game.enums.elements.EggGroup;
import com.calculusmaster.pokecord.game.enums.elements.Gender;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.enums.items.TR;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.PokemonRarity;
import com.calculusmaster.pokecord.util.interfaces.Transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PokemonListSorter
{
    private Stream<Pokemon> stream;
    private List<String> msg;

    public PokemonListSorter(Stream<Pokemon> stream, List<String> msg)
    {
        this.stream = stream;
        this.msg = msg;
    }

    public Stream<Pokemon> retrieveStream()
    {
        return this.stream;
    }

    //Common Sorts
    public void sortStandardNumeric()
    {
        this.sortNumeric(PokemonSorterFlag.LEVEL, Pokemon::getLevel);
        this.sortNumeric(PokemonSorterFlag.LEVEL, Pokemon::getDynamaxLevel);
        this.sortNumeric(PokemonSorterFlag.IV, p -> (int)(p.getTotalIVRounded()));
        this.sortNumeric(PokemonSorterFlag.EV, Pokemon::getEVTotal);
        this.sortNumeric(PokemonSorterFlag.STAT, Pokemon::getTotalStat);
    }

    public void sortStandardEnum()
    {
        this.sortEnum(PokemonSorterFlag.TYPE, Type::cast, Pokemon::isType);
        this.sortEnum(PokemonSorterFlag.MAIN_TYPE, Type::cast, (p, t) -> p.getType().get(0).equals(t));
        this.sortEnum(PokemonSorterFlag.GENDER, Gender::cast, (p, g) -> p.getGender().equals(g));
        this.sortEnum(PokemonSorterFlag.EGG_GROUP, EggGroup::cast, (p, e) -> p.getEggGroups().contains(e));
    }

    public void sortStandardBoolean()
    {
        this.sortGeneric(PokemonSorterFlag.SHINY, Pokemon::isShiny);
        this.sortGeneric(PokemonSorterFlag.MASTERED, Pokemon::isMastered);
        this.sortGeneric(PokemonSorterFlag.PRESTIGED, Pokemon::hasPrestiged);
    }

    public void sortStats()
    {
        this.sortNumeric(PokemonSorterFlag.HPIV, p -> p.getIVs().get(Stat.HP));
        this.sortNumeric(PokemonSorterFlag.ATKIV, p -> p.getIVs().get(Stat.ATK));
        this.sortNumeric(PokemonSorterFlag.DEFIV, p -> p.getIVs().get(Stat.DEF));
        this.sortNumeric(PokemonSorterFlag.SPATKIV, p -> p.getIVs().get(Stat.SPATK));
        this.sortNumeric(PokemonSorterFlag.SPDEFIV, p -> p.getIVs().get(Stat.SPDEF));
        this.sortNumeric(PokemonSorterFlag.SPDIV, p -> p.getIVs().get(Stat.SPD));
        this.sortNumeric(PokemonSorterFlag.HPEV, p -> p.getEVs().get(Stat.HP));
        this.sortNumeric(PokemonSorterFlag.ATKEV, p -> p.getEVs().get(Stat.ATK));
        this.sortNumeric(PokemonSorterFlag.DEFEV, p -> p.getEVs().get(Stat.DEF));
        this.sortNumeric(PokemonSorterFlag.SPATKEV, p -> p.getEVs().get(Stat.SPATK));
        this.sortNumeric(PokemonSorterFlag.SPDEFEV, p -> p.getEVs().get(Stat.SPDEF));
        this.sortNumeric(PokemonSorterFlag.SPDEV, p -> p.getEVs().get(Stat.SPD));
    }

    public void sortNameCategories()
    {
        this.sortIsNameInList(PokemonSorterFlag.LEGENDARY, PokemonRarity.LEGENDARY);
        this.sortIsNameInList(PokemonSorterFlag.MYTHICAL, PokemonRarity.MYTHICAL);
        this.sortIsNameInList(PokemonSorterFlag.ULTRA_BEAST, PokemonRarity.ULTRA_BEAST);
        this.sortGeneric(PokemonSorterFlag.MEGA, p -> p.getName().toLowerCase().contains("mega"));
        this.sortGeneric(PokemonSorterFlag.PRIMAL, p -> p.getName().toLowerCase().contains("primal"));
        this.sortGeneric(PokemonSorterFlag.MEGA_OR_PRIMAL, p -> p.getName().toLowerCase().contains("mega") || p.getName().toLowerCase().contains("primal"));
    }

    //Sorters
    public void sortGeneric(PokemonSorterFlag enumFlag, Predicate<Pokemon> predicate)
    {
        if(this.hasFlag(enumFlag)) this.stream = this.stream.filter(predicate);
    }

    public void sortSearchName(PokemonSorterFlag enumFlag, Matcher matcher)
    {
        final String flag = this.getExistentFlag(enumFlag);

        if(this.isIndexedFlagValid(flag))
        {
            this.stream = this.stream.filter(p -> this.getSearchNames(flag).stream().anyMatch(s -> matcher.match(p, s)));
        }
    }

    public void sortIsUUIDInList(PokemonSorterFlag enumFlag, List<String> validList)
    {
        if(this.hasFlag(enumFlag)) this.stream = this.stream.filter(p -> validList.contains(p.getUUID()));
    }

    public void sortIsNameInList(PokemonSorterFlag enumFlag, List<String> validList)
    {
        if(this.hasFlag(enumFlag)) this.stream = this.stream.filter(p -> validList.contains(p.getName()));
    }

    public <E extends Enum<E>> void sortEnum(PokemonSorterFlag enumFlag, Caster<E> caster, EnumChecker<E> checker)
    {
        String flag = this.getExistentFlag(enumFlag);

        if(!flag.equals(""))
        {
            E enumValue = caster.cast(this.msg.get(this.msg.indexOf(flag) + 1));

            if(enumValue != null) this.stream = this.stream.filter(p -> checker.has(p, enumValue));
        }
    }

    public void sortNumeric(PokemonSorterFlag enumFlag, Transformer<Pokemon, Integer> value)
    {
        String flag = this.getExistentFlag(enumFlag);

        if(!flag.equals(""))
        {
            int index = this.msg.indexOf(flag) + 1;
            String after = this.msg.get(index);

            boolean valid = index + 1 < this.msg.size();

            if(after.equals(">") && valid && this.isNumeric(index + 1)) this.stream = this.stream.filter(p -> value.transform(p) > this.getInt(index + 1));
            else if(after.equals("<") && valid && this.isNumeric(index + 1)) this.stream = this.stream.filter(p -> value.transform(p) < this.getInt(index + 1));
            else if(this.isNumeric(index)) this.stream = this.stream.filter(p -> value.transform(p) == this.getInt(index));
        }
    }

    public void sortMachine(PokemonSorterFlag enumFlag)
    {
        String flag = this.getExistentFlag(enumFlag);

        if(!flag.equals(""))
        {
            boolean tm = flag.equals("--tm");

            int index = this.msg.indexOf(flag) + 1;
            String input = this.msg.get(index);

            String machineFlag = flag.replaceAll("--", "");
            if(input.startsWith(machineFlag) && input.length() > machineFlag.length()) input = input.substring(machineFlag.length());

            if(!input.equals("") && input.chars().allMatch(Character::isDigit))
            {
                int num = Integer.parseInt(input);
                if(tm && num >= 1 && num <= 100) this.stream = this.stream.filter(p -> p.getData().validTMs.contains(TM.get(num)));
                else if(num >= 0 && num <= 99) this.stream = this.stream.filter(p -> p.getData().validTRs.contains(TR.get(num)));
            }
        }
    }

    //Functional Interfaces
    public interface Matcher
    {
        boolean match(Pokemon p, String s);
    }

    public interface Caster<E extends Enum<E>>
    {
        E cast(String s);
    }

    public interface EnumChecker<E extends Enum<E>>
    {
        boolean has(Pokemon p, E value);
    }

    //Utility
    private List<String> getSearchNames(String flag)
    {
        int start = this.msg.indexOf(flag) + 1;
        int end = this.msg.size() - 1;

        for(int i = start; i < this.msg.size(); i++)
        {
            if(this.msg.get(i).contains("--"))
            {
                end = i - 1;
                i = this.msg.size();
            }
        }

        StringBuilder names = new StringBuilder();

        for(int i = start; i <= end; i++)
        {
            names.append(this.msg.get(i)).append(" ");
        }

        String delimiter = "\\|"; //Currently the OR delimiter is |

        return new ArrayList<>(Arrays.asList(names.toString().trim().split(delimiter))).stream().map(String::trim).map(String::toLowerCase).collect(Collectors.toList());
    }

    private int getInt(int index)
    {
        return Integer.parseInt(this.msg.get(index));
    }

    private boolean isNumeric(int index)
    {
        return this.msg.get(index).chars().allMatch(Character::isDigit);
    }

    private boolean hasFlag(PokemonSorterFlag flag)
    {
        return this.msg.stream().anyMatch(s -> (flag.flags.contains(s)));
    }

    private boolean isIndexedFlagValid(String flag)
    {
        return this.msg.contains(flag) && this.msg.indexOf(flag) + 1 < this.msg.size();
    }

    private String getExistentFlag(PokemonSorterFlag flag)
    {
        String existent = "";
        for(String s : flag.flags) if(this.msg.contains(s) && this.msg.indexOf(s) + 1 < this.msg.size()) existent = s;
        return existent;
    }
}
