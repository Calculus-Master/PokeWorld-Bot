package com.calculusmaster.pokecord.mongo;

import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

public abstract class MongoQuery
{
    protected MongoCollection<Document> database;
    protected Bson query;
    protected Document document;

    public MongoQuery(String idKey, String idVal, MongoCollection<Document> database)
    {
        this.database = database;
        this.query = Filters.eq(idKey, idVal);
        this.document = idVal.contains("BOT") ? null : database.find(this.query).first();
    }

    protected void update(Bson update)
    {
        this.document = this.database.findOneAndUpdate(this.query, update, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));

        LoggerHelper.logDatabaseUpdate(MongoQuery.class, update);
    }

    public boolean isNull()
    {
        return this.document == null;
    }
}
