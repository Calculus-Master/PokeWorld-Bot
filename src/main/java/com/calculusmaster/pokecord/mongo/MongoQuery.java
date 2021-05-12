package com.calculusmaster.pokecord.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

public abstract class MongoQuery
{
    protected Bson query;
    protected Document document;

    public MongoQuery(String idKey, String idVal, MongoCollection<Document> database)
    {
        this.query = Filters.eq(idKey, idVal);
        this.document = database.find(this.query).first();
    }

    public JSONObject json()
    {
        return new JSONObject(this.document == null ? "" : this.document.toJson());
    }

    public boolean isNull()
    {
        return this.document == null;
    }
}
