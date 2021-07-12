package com.calculusmaster.pokecord.game.enums.elements;

import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.Mongo;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public enum Nature
{
    HARDY, LONELY, BRAVE, ADAMANT, NAUGHTY, BOLD, DOCILE, RELAXED, IMPISH, LAX, TIMID, HASTY, SERIOUS, JOLLY, NAIVE, MODEST, MILD, QUIET, BASHFUL, RASH, CALM, GENTLE, SASSY, CAREFUL, QUIRKY;

    public static Nature cast(String nature)
    {
        return (Nature) Global.getEnumFromString(values(), nature);
    }

    public String getShopEntry()
    {
        Document natureDB = Mongo.NatureInfo.find(Filters.eq("name", this.toString())).first();

        if(natureDB == null) return "ERROR\nERROR";

        String statIncr = "ERROR";
        String statDecr = "ERROR";

        for(int i = 1; i < Stat.values().length; i++)
        {
            if(natureDB.getDouble(Stat.values()[i].toString()) == 1.1) statIncr = Stat.values()[i].toString();
            if(natureDB.getDouble(Stat.values()[i].toString()) == 0.9) statDecr = Stat.values()[i].toString();
        }

        if(statIncr.equals(statDecr) && statDecr.equals("ERROR"))
        {
            statIncr = switch(Nature.cast(natureDB.getString("name")))
                    {
                        case BASHFUL -> Stat.SPATK.toString();
                        case DOCILE -> Stat.DEF.toString();
                        case HARDY ->  Stat.ATK.toString();
                        case QUIRKY -> Stat.SPDEF.toString();
                        case SERIOUS -> Stat.SPD.toString();
                        default -> null;
                    };
            statDecr = statIncr;
            statDecr += "  *";
        }

        return "+10% **" + statIncr + "**\n-10% **" + statDecr + "**";
    }
}
