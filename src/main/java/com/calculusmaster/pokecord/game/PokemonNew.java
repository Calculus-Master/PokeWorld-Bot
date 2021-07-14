package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.PokemonData;
import com.calculusmaster.pokecord.util.helpers.DataHelper;
import com.calculusmaster.pokecord.util.helpers.IDHelper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

public class PokemonNew
{
    private PokemonData data;
    private Document specific;

    private String UUID;

    private String nickname;

    public static PokemonNew create(String name)
    {
        PokemonNew p = new PokemonNew();
        p.setUUID();
        p.setData(name);

        return p;
    }

    public static PokemonNew build(String UUID)
    {
        PokemonNew p = new PokemonNew();
        p.setUUID(UUID);
        p.setSpecificDocument();
        p.setData(p.getName());

        p.specific = Mongo.PokemonData.find(Filters.eq("UUID", UUID)).first();
        p.data = DataHelper.pokeData(p.specific.getString("name"));

        return p;
    }

    //Database Upload/Delete
    public static void upload(PokemonNew p)
    {
        Document data = new Document()
                .append("UUID", p.getUUID())
                .append("name", p.getName())
                .append("nickname", p.getNickname());

        Mongo.PokemonData.insertOne(data);
    }

    public static void delete(PokemonNew p)
    {
        Mongo.PokemonData.deleteOne(p.query());
    }

    //Backend Database Updates
    public void update()
    {
        this.setSpecificDocument();
    }

    public void update(Bson update)
    {
        Mongo.PokemonData.updateOne(this.query(), update);
    }

    //Specific Database Updates
    public void updateName(String newName)
    {
        this.update(Updates.set("name", newName));
    }

    public void updateNickname(String nickname)
    {
        this.update(Updates.set("nickname", nickname));
    }

    //Name and Nickname
    public String getName()
    {
        return this.data.name;
    }

    public String getNickname()
    {
        return this.specific.getString("nickname");
    }

    public boolean hasNickname()
    {
        return !this.getNickname().equals("");
    }

    public String getDisplayName()
    {
        return this.hasNickname() ? "\"" + this.getNickname() + "\"" : this.getName();
    }

    //UUID
    private void setUUID(String UUID)
    {
        this.UUID = UUID;
    }

    private void setUUID()
    {
        StringBuilder uuid = new StringBuilder();
        for(int i = 0; i < 6; i++) uuid.append(IDHelper.alphanumeric(4)).append("-");
        this.setUUID(uuid.substring(0, uuid.toString().length() - 1));
    }

    public String getUUID()
    {
        return this.UUID;
    }

    public Bson query()
    {
        return Filters.eq("UUID", this.UUID);
    }

    //Data and PokemonData DB Document
    private void setData(String name)
    {
        this.data = DataHelper.pokeData(name).copy();
    }

    private void setSpecificDocument()
    {
        this.specific = Mongo.PokemonData.find(this.query()).first();
    }
}
