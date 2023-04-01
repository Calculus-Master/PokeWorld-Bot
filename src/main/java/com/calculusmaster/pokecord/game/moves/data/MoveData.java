package com.calculusmaster.pokecord.game.moves.data;

import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public final class MoveData
{
    private final MoveEntity entity; public MoveEntity getEntity() { return this.entity; }
    private final JSONObject json;

    private final String name; public String getName() { return this.name; }
    private final Type type; public Type getType() { return this.type; }
    private final Category category; public Category getCategory() { return this.category; }
    private final int basePower; public int getBasePower() { return this.basePower; }
    private final int baseAccuracy; public int getBaseAccuracy() { return this.baseAccuracy; }
    private final int priority; public int getPriority() { return this.priority; }

    private final List<String> flavorText; public List<String> getFlavorText() { return this.flavorText; }
    private final List<String> effectText; public List<String> getEffectText() { return this.effectText; }

    //For Moves sourced from JSONs
    public MoveData(MoveEntity entity)
    {
        this.entity = entity;
        this.json = this.readDataJSON();

        this.name = this.json.getString("name");
        this.type = Objects.requireNonNull(Type.cast(this.json.getString("type")));
        this.category = Objects.requireNonNull(Category.cast(this.json.getString("category")));
        this.basePower = this.json.getInt("power");
        this.baseAccuracy = this.json.getInt("accuracy");
        this.priority = this.json.getInt("priority");

        this.flavorText = this.json.getJSONArray("flavorTextEntries").toList().stream().map(Object::toString).toList();
        this.effectText = this.json.getJSONArray("effectEntries").toList().stream().map(Object::toString).toList();
    }

    //For Moves created in Runtime (+ Z-Moves, Max Moves, etc.)
    public MoveData(MoveEntity entity, String name, Type type, Category category, int basePower, int baseAccuracy)
    {
        this.entity = entity;
        this.json = null;

        this.name = name;
        this.type = type;
        this.category = category;
        this.basePower = basePower;
        this.baseAccuracy = baseAccuracy;
        this.priority = 0;

        this.flavorText = List.of();
        this.effectText = List.of();
    }

    private JSONObject readDataJSON()
    {
        try
        {
            URL path = this.getClass().getResource("/data/moves/" + this.entity.getJSONFileName() + ".json");
            File f = new File(path.toURI());
            return new JSONObject(new JSONTokener(new FileInputStream(f)));
        }
        catch(URISyntaxException | NullPointerException | IOException e)
        {
            LoggerHelper.error(MoveData.class, "Unable to find MoveData JSON. MoveEntity: %s, JSON Name: %s".formatted(this.entity.toString(), this.entity.getJSONFileName() + ".json"));
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public boolean hasJSONData()
    {
        return this.json != null;
    }

    //Null Attributes
    public boolean isBasePowerNull()
    {
        return !this.json.getJSONObject("nullAttributes").getBoolean("powerExists");
    }

    public boolean isBaseAccuracyNull()
    {
        return !this.json.getJSONObject("nullAttributes").getBoolean("accuracyExists");
    }

    //Extra Methods
    public int getOrder()
    {
        return this.json == null ? 50000 : this.json.getInt("order");
    }

    @Override
    public String toString()
    {
        return "MoveDataNew{" +
                "entity=" + this.entity +
                ", json=" + this.entity.getJSONFileName() + ".json" +
                ", name='" + this.name + '\'' +
                ", type=" + this.type +
                ", category=" + this.category +
                ", basePower=" + this.basePower +
                ", baseAccuracy=" + this.baseAccuracy +
                '}';
    }
}
