package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.Mongo;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CommandDex extends Command
{
    public CommandDex(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        //Pokedex Command
        if(this.msg.length == 1 || (this.msg.length == 2 && this.isNumeric(1)))
        {
            List<String> uniquePokemon = Global.POKEMON.stream().filter(s -> !isForm(s)).collect(Collectors.toList());
            int total = uniquePokemon.size();

            List<String> collected = new ArrayList<>();
            Mongo.DexData.find(Filters.exists("name")).forEach(d -> {
                if(d.containsKey(this.player.getId()) && d.getInteger(this.player.getId()) > 0) collected.add(d.getString("name"));
            });
            this.embed.setFooter("Total Pokemon Collected: " + collected.size() + " / " + total);

            int[] indices = {this.msg.length == 1 ? 0 : (this.getInt(1) * 20), this.msg.length == 1 ? 20 : (this.getInt(1) * 20 + 20)};
            if(indices[1] > total) indices[1] = total;

            StringBuilder list = new StringBuilder();
            Document d;
            String name;
            for(int i = indices[0]; i < indices[1]; i++)
            {
                list.append("#").append(i + 1).append(": ");

                try
                {
                    name = Mongo.PokemonInfo.find(Filters.eq("dex", i + 1)).first().getString("name");
                    d = Mongo.DexData.find(Filters.eq("name", name)).first();

                    list.append(name).append(d.containsKey(this.player.getId()) && d.getInteger(this.player.getId()) > 0 ? ":white_check_mark:" : ":x:").append(" - Owned: ").append(d.getInteger(this.player.getId()) == null ? 0 : d.getInteger(this.player.getId())).append("\n");
                }
                catch (Exception e)
                {
                    System.out.println("Error Displaying " + (i + 1));
                    e.printStackTrace();

                    list.append("\n");
                }
            }

            this.embed.setDescription(list.toString());
            this.embed.setTitle(this.player.getName() + "'s Pokedex");
            return this;
        }
        else if(this.msg.length == 1)
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }

        //Dex Generic Info Command
        boolean isShiny = this.msg[1].toLowerCase().equals("shiny");

        if(!isPokemon(this.getPokemonName()))
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }

        String pokemon = this.getPokemonName();

        JSONObject info = Pokemon.genericJSON(Global.normalCase(pokemon));
        String[] fillerINFO = info.getString("fillerinfo").split("-");

        String title = "**" + info.getString("name") + " #" + info.getInt("dex") + "**";
        String filler = "*" + fillerINFO[0] + "*\nHeight: " + fillerINFO[1] + "m     Weight: " + fillerINFO[2] + "kg";
        String type = "**Type:** " + (info.getJSONArray("type").getString(0).equals(info.getJSONArray("type").getString(1)) ? info.getJSONArray("type").getString(0) : this.getJSONArrayFormatted(info.getJSONArray("type")));
        String abilities = "**Abilities:** " + this.getJSONArrayFormatted(info.getJSONArray("abilities"));
        String growth = "**Growth Rate:** " + info.getString("growthrate").replaceAll("_", " ") + "     *Base Yield:* " + info.getInt("exp") + " XP";
        String evYield = "**EV Yield:** " + this.getEVYieldFormatted(info.getJSONArray("ev"));
        //String evolutions = "**" + this.getEvolutionsFormatted(info.getJSONArray("evolutions"), info.getJSONArray("evolutionsLVL")) + "**";
        String forms = "**Forms:** " + this.getFormsFormatted(info.getJSONArray("forms"));
        String megas = "**Megas:** " + this.getMegasFormatted(info.getJSONArray("mega"));
        String tms = "**TMs**: " + (info.getJSONArray("movesTM").length() == 0 ? "None" : info.getJSONArray("movesTM").toString());
        String trs = "**TRs**: " + (info.getJSONArray("movesTR").length() == 0 ? "None" : info.getJSONArray("movesTR").toString());
        String baseStats = "**Base Stats:** \n" + this.getStatsFormatted(info.getJSONArray("stats"));

        String image = info.getString((isShiny ? "shiny" : "normal") + "URL");

        this.embed.setTitle(title);
        this.embed.setDescription(filler + "\n" + type + "\n" + abilities + "\n" + growth + "" +
                "\n" + evYield + "\n" + forms + "\n" + megas + "\n" + tms + "\n" + trs + "\n\n" + baseStats);
        this.color = Type.cast(info.getJSONArray("type").getString(0)).getColor();
        this.embed.setImage(image.equals("") ? Pokemon.getWIPImage() : image);

        return this;
    }

    public static boolean isForm(String name)
    {
        List<String> twoWordNonForm = Arrays.asList("Mr Mime", "Jangmo O", "Hakamo O", "Kommo O", "Ho Oh");
        return name.split(" ").length != 1 && !twoWordNonForm.contains(name);
    }

    private String getPokemonName()
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 1; i < this.msg.length; i++) sb.append(this.msg[i] + " ");
        return sb.toString().replaceAll("shiny", "").trim();
    }

    private String getJSONArrayFormatted(JSONArray arr)
    {
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < arr.length(); i++) s.append(arr.getString(i)).append(i == arr.length() - 1 ? "" : " | ");
        return s.toString();
    }

    private String getEVYieldFormatted(JSONArray yield)
    {
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < 6; i++)
        {
            if(yield.getInt(i) != 0) s.append(yield.getInt(i)).append(" ").append(Stat.values()[i].toString()).append(" ");
        }
        return s.toString();
    }

    private String getEvolutionsFormatted(JSONArray evos, JSONArray evosLVL)
    {
        StringBuilder s = new StringBuilder();
        switch(evos.length())
        {
            case 0: s.append("Does not evolve"); break;
            case 1: s.append("Evolves into ").append(evos.getString(0)).append(" at Level ").append(evosLVL.getInt(0)); break;
            case 2: s.append("Evolves into ").append(evos.getString(0)).append(" at Level ").append(evosLVL.getInt(0)).append(" and ").append(evos.getString(1)).append(" at level ").append(evosLVL.getInt(1)); break;
            default: s.append("ERROR – REPORT"); break;
        }
        return s.toString();
    }

    private String getFormsFormatted(JSONArray forms)
    {
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < forms.length(); i++) s.append(forms.getString(i)).append(i == forms.length() - 1 ? "" : " | ");
        if(forms.length() == 0) s.append("None");
        return s.toString();
    }

    private String getMegasFormatted(JSONArray megas)
    {
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < megas.length(); i++) s.append(megas.getString(i)).append(i == megas.length() - 1 ? "" : " | ");
        if(megas.length() == 0) s.append("None");
        return s.toString();
    }

    private String getStatsFormatted(JSONArray stats)
    {
        return "HP: " + stats.getInt(0) +
                "\nAttack: " + stats.getInt(1) +
                "\nDefense: " + stats.getInt(2) +
                "\nSp. Attack: " + stats.getInt(3) +
                "\nSp. Defense: " + stats.getInt(4) +
                "\nSpeed: " + stats.getInt(5);
    }
}
