package com.calculusmaster.pokecord.game.settings.holder;

import com.calculusmaster.pokecord.game.settings.value.SettingValue;
import com.calculusmaster.pokecord.mongo.Mongo;
import com.calculusmaster.pokecord.mongo.ServerData;
import com.calculusmaster.pokecord.util.Global;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.function.Function;
import java.util.function.Supplier;

public class ServerSettingHolder<V extends SettingValue> extends SettingHolder<V>
{
    public ServerSettingHolder(String key, Supplier<V> defaultValue, Function<String, V> valueReader, Function<V, String> valueSerializer)
    {
        super(key, defaultValue, valueReader, valueSerializer);
    }

    public V getSetting(ServerData serverData)
    {
        Document data = serverData.getSettingsData();

        if(!this.settingMap.containsKey(serverData.getID()) && data.containsKey(this.key))
            this.settingMap.put(serverData.getID(), this.valueReader.apply(data.getString(this.key)));

        return this.settingMap.getOrDefault(serverData.getID(), this.defaultValue.get());
    }

    public void update(String serverID, V value)
    {
        String key = this.getKey();
        Bson query = Filters.eq("serverID", serverID);

        Runnable task;
        if(value.equals(this.defaultValue.get())) task = () -> Mongo.ServerData.updateOne(query, Updates.unset(key));
        else
        {
            this.settingMap.put(serverID, value);
            task = () -> Mongo.ServerData.updateOne(query, Updates.set(key, this.valueSerializer.apply(value)));
        }

        Global.GLOBAL.submit(task);
    }
}
