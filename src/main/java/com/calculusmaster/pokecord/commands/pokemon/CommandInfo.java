package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.Pokecord;
import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.elements.GrowthRate;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.player.Settings;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.evolution.MegaEvolutionRegistry;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.util.function.Function;
import java.util.stream.Collectors;

public class CommandInfo extends Command
{
    public CommandInfo(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.VIEW_UNIQUE_INFO)) return this.invalidMasteryLevel(Feature.VIEW_UNIQUE_INFO);

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

        String title = "**Level " + chosen.getLevel() + " " + chosen.getDisplayName() + ((chosen.getDisplayName().equals(chosen.getName()) ? "" : " (" + chosen.getName() + ")")) + "**" + (chosen.isShiny() ? ":star2:" : "") + (chosen.isMastered() ? ":trophy:" : "") + (chosen.hasPrestiged() ? ":zap:".repeat(chosen.getPrestigeLevel()) : "");
        String exp = chosen.getLevel() == 100 ? " Max Level " : chosen.getExp() + " / " + GrowthRate.getRequiredExp(chosen.getData().getGrowthRate(), chosen.getLevel()) + " XP";
        String type = chosen.getType().stream().map(Type::getStyledName).collect(Collectors.joining("\n"));
        String nature = Global.normalize(chosen.getNature().toString());
        String gender = Global.normalize(chosen.getGender().toString());
        String dynamaxLevel = "" + chosen.getDynamaxLevel();
        String prestigeLevel = "" + chosen.getPrestigeLevel();
        String item = chosen.getItem().getStyledName();
        String tm = (chosen.hasTM() ? chosen.getTM()  + " - " + chosen.getTM().getMove().data().getName() : "None");
        String megaCharges = "**Mega Charges**: " + chosen.getMegaCharges() + " (Max: " + chosen.getMaxMegaCharges() + ")";

        this.embed
                .addField("General Info", "**Experience:** %s\n**Type:** %s\n**Nature:** %s\n**Gender:** %s\n**Dynamax Level**: %s\n**Prestige Level**: %s%s".formatted(exp, type, nature, gender, dynamaxLevel, prestigeLevel, MegaEvolutionRegistry.hasMegaData(chosen.getEntity()) ? "\n" + megaCharges : ""), true)
                .addField("Held", "**Item:** %s\n**TM:** %s".formatted(item, tm), true)
                .addField(this.getPrestigeField(chosen))
                .addField(this.getStatsField(chosen));

        if(this.playerData.getSettings().get(Settings.CLIENT_DETAILED, Boolean.class))
        {
            this.embed
                    .addField(this.getIVsField(chosen))
                    .addField(this.getEVsField(chosen));
        }

        this.embed.setTitle(title.length() > 256 ? title.substring(0, 247) + "..." : title);
        this.embed.setDescription("UUID: " + UUID);
        this.color = chosen.getType().get(0).getColor();
        this.embed.setFooter("Showing Pokemon " + (index + 1) + " / " + this.playerData.getPokemonList().size());

        String image = Pokemon.getImage(chosen.getEntity(), chosen.isShiny(), chosen, null);
        String imageAttachmentName = "info_" + chosen.getUUID() + ".png";
        this.embed.setImage("attachment://" + imageAttachmentName);
        this.event.getChannel().sendFiles(FileUpload.fromData(Pokecord.class.getResourceAsStream(image), imageAttachmentName)).setEmbeds(this.embed.build()).queue();
        this.embed = null;

        return this;
    }

    private MessageEmbed.Field getPrestigeField(Pokemon p)
    {
        StringBuilder sb = new StringBuilder();

        if(p.getPrestigeLevel() == 0)
        {
            if(p.getLevel() < 100) sb.append("*Your Pokemon will be able to Prestige at Level 100 to gain permanent stat boosts! Use the p!prestige command for more information.*");
            else sb.append("*Your Pokemon can now Prestige! Use the p!prestige command for more information.*");
        }
        else if(p.getPrestigeLevel() == p.getMaxPrestigeLevel()) sb.append("*Your Pokemon has reached its maximum Prestige Level!*");

        if(p.getPrestigeLevel() != 0)
        {
            Function<Double, String> truncate = i -> ((int)(((i - 1.0) * 100) * 100)) / 100. + "%";
            String healthBoost = "HP: " + truncate.apply(p.getPrestigeBonus(Stat.HP));
            String statBoost = "ATK/DEF/SPATK/SPDEF: " + truncate.apply(p.getPrestigeBonus(Stat.ATK));
            String speedBoost = "SPD: " + truncate.apply(p.getPrestigeBonus(Stat.SPD));

            sb.append("\n").append(healthBoost).append("\n").append(statBoost).append("\n").append(speedBoost).append("\n");
        }

        return new MessageEmbed.Field("Prestige Boosts", sb.toString(), false);
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
        for(Stat s : Stat.values()) sb.append(p.getIVs().get(s) == 31 ? "**" + p.getIVs().get(s) + "**" : p.getIVs().get(s)).append(" / 31\n");
        sb.append(p.getTotalIV());

        return new MessageEmbed.Field("IVs", sb.toString(), true);
    }

    private MessageEmbed.Field getEVsField(Pokemon p)
    {
        StringBuilder sb = new StringBuilder();
        for(Stat s : Stat.values()) sb.append(p.getEVs().get(s) >= 252 ? "**" + p.getEVs().get(s) + "**" : p.getEVs().get(s)).append("\n");
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
