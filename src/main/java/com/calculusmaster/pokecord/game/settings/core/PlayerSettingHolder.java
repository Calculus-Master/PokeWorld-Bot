package com.calculusmaster.pokecord.game.settings.core;

import com.calculusmaster.pokecord.mongo.Mongo;
import com.calculusmaster.pokecord.mongo.PlayerData;
import com.calculusmaster.pokecord.util.Global;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.function.Function;
import java.util.function.Supplier;

public class PlayerSettingHolder<V extends SettingValue> extends SettingHolder<V>
{
    public PlayerSettingHolder(String key, Supplier<V> defaultValue, Function<String, V> valueReader, Function<V, String> valueSerializer)
    {
        super(key, defaultValue, valueReader, valueSerializer);
    }

    public V getSetting(PlayerData playerData)
    {
        Document data = playerData.getSettingsData();

        if(!this.settingMap.containsKey(playerData.getID()) && data.containsKey(this.key))
            this.settingMap.put(playerData.getID(), this.valueReader.apply(data.getString(this.key)));

        return this.settingMap.getOrDefault(playerData.getID(), this.defaultValue.get());
    }

    public void update(String playerID, V value)
    {
        String key = this.getKey();
        Bson query = Filters.eq("playerID", playerID);

        Runnable task;
        if(value.equals(this.defaultValue.get())) task = () -> Mongo.PlayerData.updateOne(query, Updates.unset(key));
        else
        {
            this.settingMap.put(playerID, value);
            task = () -> Mongo.PlayerData.updateOne(query, Updates.set(key, this.valueSerializer.apply(value)));
        }

        Global.GLOBAL.submit(task);
    }
}
