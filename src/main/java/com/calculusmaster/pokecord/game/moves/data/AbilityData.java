package com.calculusmaster.pokecord.game.moves.data;

import com.calculusmaster.pokecord.game.enums.elements.Ability;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class AbilityData
{
    private final Ability ability; public Ability getAbility() { return this.ability; }
    private final JSONObject json;

    private final String name; public String getName() { return this.name; }

    private final List<String> effectText; public List<String> getEffectText() { return this.effectText; }
    private final List<String> flavorText; public List<String> getFlavorText() { return this.flavorText; }

    public AbilityData(Ability ability)
    {
        this.ability = ability;
        this.json = this.readDataJSON();

        this.name = this.json.getString("name");

        this.flavorText = this.json.getJSONArray("flavorTextEntries").toList().stream().map(Object::toString).toList();
        this.effectText = this.json.getJSONArray("effectEntries").toList().stream().map(Object::toString).toList();
    }

    private JSONObject readDataJSON()
    {
        try
        {
            URL path = this.getClass().getResource("/data/abilities/" + this.ability.getJSONFileName() + ".json");
            File f = new File(path.toURI());
            return new JSONObject(new JSONTokener(new FileInputStream(f)));
        }
        catch(URISyntaxException | NullPointerException | IOException e)
        {
            LoggerHelper.error(MoveData.class, "Unable to find AbilityData JSON. Ability: %s, JSON Name: %s".formatted(this.ability.toString(), this.ability.getJSONFileName() + ".json"));
            e.printStackTrace();
            return new JSONObject();
        }
    }
}
