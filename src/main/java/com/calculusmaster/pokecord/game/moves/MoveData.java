package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.util.helpers.CSVHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public final class MoveData
{
    public static final List<String> MOVES = new ArrayList<>();
    public static final LinkedHashMap<String, MoveData> MOVE_DATA = new LinkedHashMap<>();

    public static void init()
    {
        CSVHelper.CSV_MOVE_DATA.forEach(line -> {
            String name = line[0];

            MOVES.add(name);
            MOVE_DATA.put(name, new MoveData(name, Type.cast(line[1]), Category.cast(line[2]), Integer.parseInt(line[3]), Integer.parseInt(line[4]), List.of(line[5].split("\\|")), false, false));
        });
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
        MoveData data = MOVE_DATA.get(name);
        if(data == null) System.out.println(name);
        return MOVE_DATA.get(name).copy();
    }

    public static boolean hasData(String name)
    {
        //TODO: Remove after fixing the weird usse
        return MOVE_DATA.containsKey(name);
    }
}
