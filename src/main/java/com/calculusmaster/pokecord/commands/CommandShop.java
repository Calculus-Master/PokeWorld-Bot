package com.calculusmaster.pokecord.commands;

import com.calculusmaster.pokecord.game.enums.Nature;
import com.calculusmaster.pokecord.game.enums.Stat;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.Mongo;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CommandShop extends Command
{
    private StringBuilder page;

    //TODO: WIP (shop)
    public CommandShop(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, "shop <page>");
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length == 1)
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }

        this.page = new StringBuilder();

        switch (this.msg[1])
        {
            case "mega" -> page_mega();
            case "forms" -> page_forms();
            case "nature" -> page_nature();
            case "items" -> page_items();
            default -> this.embed.setDescription(CommandInvalid.getShort());
        }

        if(this.page.isEmpty()) return this;

        this.embed.setTitle("Shop – " + this.msg[1].toUpperCase());
        this.embed.setDescription(this.page.toString());

        return this;
    }

    private void page_mega()
    {

    }

    private void page_forms()
    {

    }

    private void page_nature()
    {
        List<JSONObject> natures = new ArrayList<>();
        Mongo.NatureInfo.find(Filters.exists("name")).forEach(d -> natures.add(new JSONObject(d.toJson())));

        this.page.append("Natures: \n");
        for(JSONObject j : natures) this.page.append("`" + j.getString("name") + "` | " + this.natureIncreaseDecrease(j) + "\n");

        this.embed.setFooter("All natures cost 100c. Buy a nature with p!buy nature <nature>, where <nature> is the name of the nature.");
    }

    private String natureIncreaseDecrease(JSONObject j)
    {
        String statIncr = "ERROR";
        String statDecr = "ERROR";

        for(int i = 1; i < Stat.values().length; i++)
        {
            if(j.getDouble(Stat.values()[i].toString()) == 1.1) statIncr = Stat.values()[i].toString();
            if(j.getDouble(Stat.values()[i].toString()) == 0.9) statDecr = Stat.values()[i].toString();
        }

        if(statIncr.equals(statDecr) && statDecr.equals("ERROR"))
        {
            statIncr = getUnchangedNatureStats(Nature.cast(j.getString("name"))).toString();
            statDecr = statIncr;
            statDecr += " *";
        }

        return " +10% " + statIncr + " | -10% " + statDecr;
    }

    private Stat getUnchangedNatureStats(Nature n)
    {
        return switch(n)
        {
            case BASHFUL -> Stat.SPATK;
            case DOCILE -> Stat.DEF;
            case HARDY ->  Stat.ATK;
            case QUIRKY -> Stat.SPDEF;
            case SERIOUS -> Stat.SPD;
            default -> null;
        };
    }

    private void page_items()
    {

    }
}
