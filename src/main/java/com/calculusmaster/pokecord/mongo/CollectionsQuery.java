package com.calculusmaster.pokecord.mongo;

import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.Mongo;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

public class CollectionsQuery
{
    private Bson query;
    private Document document;
    private String player;

    public CollectionsQuery(String pokemon, String player)
    {
        pokemon = Global.normalize(pokemon);

        this.query = Filters.eq("name", pokemon);
        this.update();

        this.player = player;
    }

    private void update()
    {
        this.document = Mongo.DexData.find(this.query).first();
    }

    public void increase()
    {
        if(!this.document.containsKey(this.player)) Mongo.DexData.updateOne(this.query, Updates.set(this.player, 1));
        else Mongo.DexData.updateOne(this.query, Updates.inc(this.player, 1));

        this.update();
    }

    public int getCaughtAmount()
    {
        return !this.document.containsKey(this.player) ? 0 : this.document.getInteger(this.player);
    }
}
