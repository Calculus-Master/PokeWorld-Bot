package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.GrowthRate;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.items.PokeItem;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.enums.items.TR;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.helpers.SettingsHelper;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandInfo extends Command
{
    public CommandInfo(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        int index = this.playerData.getSelected();
        if(this.msg.length == 2)
        {
            if("latest".contains(this.msg[1])) index = this.playerData.getPokemonList().size() - 1;
            else if(!this.isNumeric(1))
            {
                this.embed.setDescription("Use p!dex instead!");
                return this;
            }
            else if(this.getInt(1) <= this.playerData.getPokemonList().size()) index = this.getInt(1) - 1;
        }

        String UUID = this.playerData.getPokemonList().get(index);
        Pokemon chosen = Pokemon.build(UUID);

        String title = "**Level " + chosen.getLevel() + " " + chosen.getDisplayName() + "**" + (chosen.isShiny() ? " :star2:" : "");
        String exp = chosen.getLevel() == 100 ? " Max Level " : chosen.getExp() + " / " + GrowthRate.getRequiredExp(chosen.getGenericJSON().getString("growthrate"), chosen.getLevel()) + " XP";
        String type = (chosen.getType()[0].equals(chosen.getType()[1]) ? Global.normalCase(chosen.getType()[0].toString()) : Global.normalCase(chosen.getType()[0].toString()) + "\n" + Global.normalCase(chosen.getType()[1].toString()));
        String nature = Global.normalCase(chosen.getNature().toString());
        String dynamaxLevel = "" + chosen.getDynamaxLevel();
        String item = PokeItem.asItem(chosen.getItem()).getStyledName();
        String tm = (chosen.hasTM() ? "TM" + (chosen.getTM() < 10 ? "0" : "") + chosen.getTM()  + " - " + TM.get(chosen.getTM()).getMoveName() : "None");
        String tr =  (chosen.hasTR() ? "TR" + (chosen.getTR() < 10 ? "0" : "") + chosen.getTR()  + " - " + TR.get(chosen.getTR()).getMoveName() : "None");
        String stats = getStatsFormatted(chosen, this.playerData.getSettings().getSettingBoolean(SettingsHelper.Setting.CLIENT_DETAILED));
        String kd = "**Defeats/Faints Ratio**: " + chosen.getKDRatio();

        this.embed
                .addField("Experience", exp, true)
                .addField("Type", type, true)
                .addField("Nature", nature, true)
                .addField("Dynamax Level", dynamaxLevel, true)
                .addField("Item", item, true)
                .addField("TM/TR", "TM: " + tm + "\nTR: " + tr, true)
                //.addBlankField(false)
                .addField(this.getStatsField(chosen));

        if(this.playerData.getSettings().getSettingBoolean(SettingsHelper.Setting.CLIENT_DETAILED))
        {
            this.embed
                    .addField(this.getIVsField(chosen))
                    .addField(this.getEVsField(chosen));
        }

        this.embed.setTitle(title);
        this.color = chosen.getType()[0].getColor();
        this.embed.setImage(chosen.getImage());
        this.embed.setFooter("Showing Pokemon " + (index + 1) + " / " + this.playerData.getPokemonList().size());

        this.playerData.addPokePassExp(50, this.event);
        return this;
    }

    private MessageEmbed.Field getStatsField(Pokemon p)
    {
        StringBuilder sb = new StringBuilder();
        for(Stat s : Stat.values()) sb.append("**").append(s.shortName()).append("**: ").append(p.getStat(s)).append("\n");
        sb.append("**Total**: ").append(p.getTotalStat());

        return new MessageEmbed.Field("Stats", sb.toString(), true);
    }

    private MessageEmbed.Field getIVsField(Pokemon p)
    {
        StringBuilder sb = new StringBuilder();
        for(Stat s : Stat.values()) sb.append(p.getIVs().get(s)).append(" / 31\n");
        sb.append(p.getTotalIV());

        return new MessageEmbed.Field("IVs", sb.toString(), true);
    }

    private MessageEmbed.Field getEVsField(Pokemon p)
    {
        StringBuilder sb = new StringBuilder();
        for(Stat s : Stat.values()) sb.append(p.getEVs().get(s)).append("\n");
        sb.append(p.getEVTotal());

        return new MessageEmbed.Field("EVs", sb.toString(), true);
    }

    public static String getStatsFormatted(Pokemon p, boolean detailedEnabled)
    {
        String HP = formatStat(p, Stat.HP, "HP", detailedEnabled);
        String ATK = formatStat(p, Stat.ATK, "Attack", detailedEnabled);
        String DEF = formatStat(p, Stat.DEF, "Defense", detailedEnabled);
        String SPATK = formatStat(p, Stat.SPATK, "Sp. Attack", detailedEnabled);
        String SPDEF = formatStat(p, Stat.SPDEF, "Sp. Defense", detailedEnabled);
        String SPD = formatStat(p, Stat.SPD, "Speed", detailedEnabled);
        String total = "**Total IV %:** " + p.getTotalIV();

        return HP + ATK + DEF + SPATK + SPDEF + SPD + total;
    }

    private static String formatStat(Pokemon p, Stat s, String statName, boolean detailedEnabled)
    {
        String statHeader = "**" + statName + ":** " + p.getStat(s);
        String detailed = "IV: " + p.getIVs().get(s) + " / 31 " + "(EV: " + p.getEVs().get(s) + ")";
        return statHeader + (detailedEnabled ? " - " + detailed : "") + "\n";
    }
}
