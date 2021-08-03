package com.calculusmaster.pokecord.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import java.util.Arrays;

public abstract class MongoQuery
{
    protected MongoCollection<Document> database;
    protected Bson query;
    protected Document document;

    public MongoQuery(String idKey, String idVal, MongoCollection<Document> database)
    {
        this.database = database;
        this.query = Filters.eq(idKey, idVal);
        this.document = database.find(this.query).first();
    }

    public JSONObject json()
    {
        if(this.document == null) System.out.println(this + ", Query: " + this.query);
        return new JSONObject(this.document.toJson());
    }

    protected void update()
    {
        this.document = this.database.find(this.query).first();
    }

    protected void update(Bson update)
    {
        this.database.updateOne(this.query, update);

        this.update();
    }

    protected void update(Bson... updates)
    {
        this.database.updateOne(this.query, Arrays.asList(updates));

        this.update();
    }

    public boolean isNull()
    {
        return this.document == null;
    }
}
