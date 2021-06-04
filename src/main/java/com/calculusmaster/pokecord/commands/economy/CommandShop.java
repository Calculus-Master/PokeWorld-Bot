package com.calculusmaster.pokecord.commands.economy;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.items.*;
import com.calculusmaster.pokecord.game.enums.elements.Nature;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.util.Mongo;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONObject;

import java.time.OffsetDateTime;
import java.util.*;

public class CommandShop extends Command
{
    private StringBuilder page;

    public CommandShop(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length == 1)
        {
            this.embed.setDescription("`p!shop mega` – Mega Evolutions\n`p!shop forms` – Pokemon Forms\n`p!shop nature` – Change your Pokemon's Nature\n`p!shop tm:tr` – Buy TMs and TRs\n`p!shop movetutor:tutor:mt` – Buy Move Tutor moves\n`p!shop zcrystals:zcrystal:z` – Buy Unique Z-Crystals\n`p!shop items` – Misc. Items");
            this.embed.setTitle("Pokecord Shop");
            return this;
        }

        if(this.isUpdateTime()) this.updateDailyShops();

        this.page = new StringBuilder();

        switch (this.msg[1])
        {
            case "mega" -> page_mega();
            case "forms" -> page_forms();
            case "nature" -> page_nature();
            case "items" -> page_items();
            case "tm", "tr" -> page_tm_tr();
            case "movetutor", "mt", "tutor" -> page_movetutor();
            case "zcrystals", "zcrystal", "z" -> page_zcrystals();
            default -> this.embed.setDescription(CommandInvalid.getShort());
        }

        if(this.page.isEmpty()) return this;

        this.embed.setTitle("Shop – " + this.msg[1].toUpperCase());
        this.embed.setDescription(this.page.toString());

        return this;
    }

    public static final List<PokeItem> entriesItem = new ArrayList<>();
    public static final List<Integer> itemPrices = new ArrayList<>();

    private void page_items()
    {
        this.page.append("Rare Candies (Level up Pokemon once per candy) : `p!buy candy <amount>`");

        this.page.append("\n\n**Items**:\n");
        for(PokeItem i : entriesItem) this.page.append((entriesItem.indexOf(i) + 1) + ": " + i.getStyledName() + " - " + itemPrices.get(entriesItem.indexOf(i)) + "c\n");
    }

    private static OffsetDateTime time;
    public static int currentTMPrice = 10000;
    public static int currentTRPrice = 10000;

    public static final List<String> entriesTM = new ArrayList<>();
    public static final List<String> entriesTR = new ArrayList<>();

    private void page_tm_tr()
    {
        this.page.append("\n**Technical Machines (TMs) for " + currentTMPrice + "c each: **\n");
        for(String s : entriesTM) this.page.append(s).append("\n");
        this.page.append("\n**Technical Records (TRs) for " + currentTRPrice + "c each: **\n");
        for(String s : entriesTR) this.page.append(s).append("\n");
    }

    private String newTMEntry()
    {
        TM tm = TM.values()[new Random().nextInt(TM.values().length)];
        return "`" + tm.toString() + "` - " + tm.getMoveName();
    }

    private String newTREntry()
    {
        TR tr = TR.values()[new Random().nextInt(TR.values().length)];
        return "`" + tr.toString() + "` - " + tr.getMoveName();
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

    public static List<String> MOVE_TUTOR_MOVES = Arrays.asList("Blast Burn", "Hydro Cannon", "Frenzy Plant", "Draco Meteor", "Volt Tackle", "Dragon Ascent", "Secret Sword", "Relic Song");

    private void page_movetutor()
    {
        this.page.append("Move Tutor Moves: \n\n");

        for(String s : MOVE_TUTOR_MOVES) this.page.append("`").append(s).append("`\n");

        this.embed.setFooter("All move tutor moves cost " + CommandBuy.COST_MOVETUTOR + "c. Buying a move tutor move will automatically set the move in your first slot to the move tutor move.");
    }

    public static final List<String> entriesZCrystal = new ArrayList<>();
    public static int priceZCrystal = 200000;

    private void page_zcrystals()
    {
        this.page.append("Z Crystals: \n\n");

        for(String s : entriesZCrystal) this.page.append(s).append("\n");

        this.page.append("Z Crystal Price: ").append(priceZCrystal).append("c!");
    }

    private boolean isUpdateTime()
    {
        if(time == null) return true;

        int lastHours = time.getHour() + time.getDayOfYear() * 24;
        int currentHours = this.event.getMessage().getTimeCreated().getHour() + this.event.getMessage().getTimeCreated().getHour() * 24;

        int interval = 4; //Every <interval> hours, shop updates
        return currentHours - lastHours >= interval;
    }

    private void updateDailyShops()
    {
        System.out.println("Updating Daily Shops!");

        time = this.event.getMessage().getTimeCreated();

        //TMs and TRs
        entriesTM.clear();
        entriesTR.clear();

        for(int i = 0; i < 10; i++)
        {
            if(entriesTM.contains(newTMEntry())) i--;
            else entriesTM.add(newTMEntry());
        }
        for(int i = 0; i < 10; i++)
        {
            if(entriesTR.contains(newTREntry())) i--;
            else entriesTR.add(newTREntry());
        }

        currentTMPrice = 4000 + new Random().nextInt(5000);
        currentTRPrice = 4000 + new Random().nextInt(5000);

        //Items
        int num = new Random().nextInt(8) + 6;
        entriesItem.clear();

        PokeItem item;
        for(int i = 0; i < num; i++)
        {
            item = PokeItem.values()[new Random().nextInt(PokeItem.values().length)];

            if(item.equals(PokeItem.NONE)) i--;

            if(!entriesItem.contains(item) && !item.equals(PokeItem.NONE))
            {
                entriesItem.add(item);
                itemPrices.add(item.cost + (new Random().nextInt(item.cost / 2) * (new Random().nextInt(2) == 1 ? 1 : -1)));
            }
        }

        //Z-Crystals
        entriesZCrystal.clear();

        String z;
        for(int i = 0; i < 2; i++)
        {
            z = ZCrystal.getRandomUniqueZCrystal().getStyledName();
            if(entriesZCrystal.contains(z)) i--;
            else entriesZCrystal.add(z);
        }

        priceZCrystal = (int)(50000 * (Math.random() * 3 + 1));
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
