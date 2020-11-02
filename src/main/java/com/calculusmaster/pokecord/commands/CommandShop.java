package com.calculusmaster.pokecord.commands;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.TM;
import com.calculusmaster.pokecord.game.enums.Nature;
import com.calculusmaster.pokecord.game.enums.Stat;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.Mongo;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommandShop extends Command
{
    private StringBuilder page;

    public CommandShop(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, "shop <page>");
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length == 1)
        {
            this.embed.setDescription("`p!shop mega` – Mega Evolutions\n`p!shop forms` – Pokemon Forms\n`p!shop nature` – Change your Pokemon's Nature\n`p!shop items` – Misc. Items");
            this.embed.setTitle("Pokecord Shop");
            return this;
        }

        this.page = new StringBuilder();

        switch (this.msg[1])
        {
            case "mega" -> page_mega();
            case "forms" -> page_forms();
            case "nature" -> page_nature();
            case "items" -> page_items();
            case "tm" -> page_tm_tr();
            case "tr" -> page_tm_tr();
            default -> this.embed.setDescription(CommandInvalid.getShort());
        }

        if(this.page.isEmpty()) return this;

        this.embed.setTitle("Shop – " + this.msg[1].toUpperCase());
        this.embed.setDescription(this.page.toString());

        return this;
    }

    private void page_items()
    {
        //TODO: Items page
    }

    //TODO: Convert to an actual time system rather than just an int
    private static int day = 0;
    public static final List<String> entriesTM = new ArrayList<>();
    public static final List<String> entriesTR = new ArrayList<>();

    //TODO: TM and TR page, plus buying TM and TR, plus giving TM and TR to Pokemon
    private void page_tm_tr()
    {
        this.page.append("TMs and TRs: \n\n");

        if(day < this.event.getMessage().getTimeCreated().getDayOfYear())
        {
            day = this.event.getMessage().getTimeCreated().getDayOfYear();
            entriesTM.clear();
            entriesTR.clear();

            for(int i = 0; i < 3; i++) entriesTM.add(newTMEntry());
            for(int i = 0; i < 3; i++) entriesTR.add(newTREntry());
        }

        this.page.append("\nTechnical Machines for Sale: \n");
        for(String s : entriesTM) this.page.append(s).append("\n");
        this.page.append("\nTechnical Records for Sale: \n");
        for(String s : entriesTR) this.page.append(s).append("\n");
    }


    //TODO: This whole thing
    private String newTMEntry()
    {
        TM tm = TM.values()[new Random().nextInt(TM.values().length)];
        return tm.toString() + " - " + tm.getMoveName();
    }

    private String newTREntry()
    {
        return "NYI";
    }

    private void page_mega()
    {
        this.page.append("Megas: \n\n")
                .append("`p!buy mega`" + " – Buy the mega of a pokemon (if it doesn't have X or Y megas).")
                .append("\n`p!buy mega x`" + " – Buy the x mega evolution of a pokemon.")
                .append("\n`p!buy mega y`" + " – Buy the y mega evolution of a pokemon.");
        this.embed.setFooter("All mega evolutions cost " + CommandBuy.COST_MEGA + "c each. Primal Groudon and Primal Kyogre both count as megas.");
    }

    private void page_forms()
    {
        Pokemon selected = this.playerData.getSelectedPokemon();

        this.page.append("Forms: \n\n").append("Selected Pokemon: ").append(selected.getName()).append("\n\n");

        for(int i = 0; i < selected.getGenericJSON().getJSONArray("forms").length(); i++) this.page.append(selected.getGenericJSON().getJSONArray("forms").getString(i)).append("\n");
        if(!selected.hasForms()) this.page.append(selected.getName()).append(" has no forms.");

        this.page.append("\nBuy forms with p!buy form <form> where <form> is the name of the form. All forms cost " + CommandBuy.COST_FORM + "c.");
        this.embed.setFooter("This page is dynamically updated based on your selected Pokemon.");
    }

    private void page_nature()
    {
        List<JSONObject> natures = new ArrayList<>();
        Mongo.NatureInfo.find(Filters.exists("name")).forEach(d -> natures.add(new JSONObject(d.toJson())));

        this.page.append("Natures: \n\n");
        for(JSONObject j : natures) this.page.append("`" + j.getString("name") + "`: " + this.getNatureEntry(j) + "\n");

        this.embed.setFooter("All natures cost " + CommandBuy.COST_NATURE + "c. Buy a nature with p!buy nature <nature>, where <nature> is the name of the nature.");
    }

    private String getNatureEntry(JSONObject j)
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
            statIncr = switch(Nature.cast(j.getString("name")))
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

        return " +10% **" + statIncr + "** | -10% **" + statDecr + "**";
    }
}
