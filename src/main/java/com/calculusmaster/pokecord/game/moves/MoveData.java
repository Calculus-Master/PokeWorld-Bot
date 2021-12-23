package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.custom.ExtendedHashMap;
import com.calculusmaster.pokecord.util.helpers.CSVHelper;
import org.apache.commons.lang3.Range;

import java.util.*;
import java.util.stream.Collectors;

public final class MoveData
{
    public static final List<String> MOVES = new ArrayList<>();
    public static final LinkedHashMap<String, MoveData> MOVE_DATA = new LinkedHashMap<>();

    public static void init()
    {
        //id,identifier,generation_id,damage_class_id
        final Map<String, String> typeID = new HashMap<>();
        CSVHelper.readPokemonCSV("types").forEach(s -> typeID.put(s[0], Global.normalize(s[1])));

        //Category (Damage Class)
        final Map<String, String> categoryID = new ExtendedHashMap<String, String>().insert("1", "STATUS").insert("2", "PHYSICAL").insert("3", "SPECIAL");

        //move_id,version_group_id,language_id,flavor_text
        final List<String[]> movesFlavorCSV = CSVHelper.readPokemonCSV("move_flavor_text").stream().filter(l -> l[2].equals("9")).toList();

        //id,identifier,generation_id,type_id,power,pp,accuracy,priority,target_id,damage_class_id,effect_id,effect_chance,contest_type_id,contest_effect_id,super_contest_effect_id
        final List<Range<Integer>> skipIDs = Arrays.asList(Range.between(622, 658), Range.between(695, 703), Range.between(719, 719), Range.between(723, 741), Range.between(757, 774), Range.between(10001, 10018));
        List<String[]> movesCSV = CSVHelper.readPokemonCSV("moves").stream().filter(line -> skipIDs.stream().noneMatch(r -> r.contains(Integer.parseInt(line[0])))).toList();

        for(String[] moveLine : movesCSV)
        {
            String name = Global.normalize(moveLine[1].replaceAll("-", " ")).replace("Vice Grip", "Vise Grip");

            String type = typeID.get(moveLine[3]);
            String category = categoryID.get(moveLine[9]);
            int power = moveLine[4].equals("") ? 0 : Integer.parseInt(moveLine[4]);
            int accuracy = moveLine[6].equals("") ? 100 : Integer.parseInt(moveLine[6]);
            List<String> flavor = movesFlavorCSV.stream().filter(line -> line[0].equals(moveLine[0])).map(s -> s[3]).map(s -> s.replaceAll("\n", " ")).distinct().collect(Collectors.toList());

            MOVES.add(name);
            MOVE_DATA.put(name, new MoveData(name, Type.cast(type), Category.cast(category), power, accuracy, flavor, false, false));
        }
    }

    public static void registerNew(String name, MoveData data)
    {
        MOVES.add(name);
        MOVE_DATA.put(name, data);
    }

    //Fields

    public final String name;
    public final Type type;
    public final Category category;
    public final int basePower;
    public final int baseAccuracy;
    public final List<String> flavor;

    public final boolean isZMove;
    public final boolean isMaxMove;

    public MoveData(String name, Type type, Category category, int basePower, int baseAccuracy, List<String> flavor, boolean isZMove, boolean isMaxMove)
    {
        this.name = name;
        this.type = type;
        this.category = category;
        this.basePower = basePower;
        this.baseAccuracy = baseAccuracy;
        this.flavor = new ArrayList<>(List.copyOf(flavor));

        this.isZMove = isZMove;
        this.isMaxMove = isMaxMove;
    }

    public MoveData copy()
    {
        return new MoveData(this.name, this.type, this.category, this.basePower, this.baseAccuracy, this.flavor, this.isZMove, this.isMaxMove);
    }

    public static MoveData get(String name)
    {
        return MOVE_DATA.get(name).copy();
    }
}
