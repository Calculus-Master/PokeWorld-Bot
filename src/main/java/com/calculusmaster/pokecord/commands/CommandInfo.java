package com.calculusmaster.pokecord.commands;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.GrowthRate;
import com.calculusmaster.pokecord.game.enums.Stat;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandInfo extends Command
{
    public CommandInfo(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, "info <number>");
    }

    @Override
    public Command runCommand()
    {
        int index = this.playerData.getSelected();
        if(this.msg.length == 2)
        {
            if(this.msg[1].equals("latest")) index = this.playerData.getPokemonList().length() - 1;
            else if(!this.msg[1].chars().allMatch(Character::isDigit)) this.embed.setDescription("Use p!dex instead!");
            else if(Integer.parseInt(this.msg[1]) <= this.playerData.getPokemonList().length()) index = Integer.parseInt(this.msg[1]) - 1;
        }

        String UUID = this.playerData.getPokemonList().getString(index);
        Pokemon chosen = Pokemon.build(UUID);

        String title = "**Level " + chosen.getLevel() + " " + chosen.getName() + "**" + (chosen.isShiny() ? " :star2:" : "");
        String exp = chosen.getLevel() == 100 ? " Max Level " : chosen.getExp() + " / " + GrowthRate.getRequiredExp(chosen.getGenericJSON().getString("growthrate"), chosen.getLevel()) + " XP";
        String type = "Type: " + (chosen.getType()[0].equals(chosen.getType()[1]) ? Global.normalCase(chosen.getType()[0].toString()) : Global.normalCase(chosen.getType()[0].toString()) + " | " + Global.normalCase(chosen.getType()[1].toString()));
        String nature = "Nature: " + Global.normalCase(chosen.getNature().toString());
        String stats = this.getStatsFormatted(chosen);

        this.embed.setTitle(title);
        this.embed.setDescription(exp + "\n" + type + "\n" + nature + "\n\n" + stats);
        this.color = chosen.getType()[0].getColor();
        this.embed.setImage(chosen.getGenericJSON().getString((chosen.isShiny() ? "shiny" : "normal") + "URL"));
        this.embed.setFooter("Showing Pokemon " + (index + 1) + " / " + this.playerData.getPokemonList().length());

        return this;
    }

    private String getStatsFormatted(Pokemon p)
    {
        String HP = this.formatStat(p, Stat.HP, "HP");
        String ATK = this.formatStat(p, Stat.ATK, "Attack");
        String DEF = this.formatStat(p, Stat.DEF, "Defense");
        String SPATK = this.formatStat(p, Stat.SPATK, "Sp. Attack");
        String SPDEF = this.formatStat(p, Stat.SPDEF, "Sp. Defense");
        String SPD = this.formatStat(p, Stat.SPD, "Speed");
        String total = "Total IV %: " + p.getTotalIV();

        return HP + ATK + DEF + SPATK + SPDEF + SPD + total;
    }

    private String formatStat(Pokemon p, Stat s, String statName)
    {
        return "**" + statName + ":** " + p.getStat(s) + " - IV: " + p.getIVs().get(s) + " / 31\n";
    }
}
