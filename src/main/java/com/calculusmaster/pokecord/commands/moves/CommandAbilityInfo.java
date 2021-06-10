package com.calculusmaster.pokecord.commands.moves;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;

public class CommandAbilityInfo extends Command
{
    private static final Map<String, String> ABILITY_INFO = new HashMap<>();

    public static void init()
    {
        ABILITY_INFO.put("Adaptability", "Adaptability increases the effectiveness of STAB moves from the usual 1.5x to 2x.");
        ABILITY_INFO.put("Disguise", "Disguise allows the bearer to avoid damage for one attack.");
        ABILITY_INFO.put("Iron Barbs", "When a Pokemon with Iron Barbs is hit by a move that makes contact, the attacker receives damage equal to 1⁄8 of their maximum HP.");
        ABILITY_INFO.put("Drought", "Drought creates harsh sunlight when the ability-bearer enters battle. The effect lasts 5 turns.");
        ABILITY_INFO.put("Drizzle", "Drizzle creates a rain shower when the ability-bearer enters battle. The effect lasts 5 turns.");
        ABILITY_INFO.put("Sand Stream", "Sand Stream creates a sandstorm when the ability-bearer enters battle. The effect lasts 5 turns.");
        ABILITY_INFO.put("Snow Warning", "Snow Warning creates a hailstorm when the ability-bearer enters battle. The effect lasts 5 turns.");
        ABILITY_INFO.put("Stance Change", "Stance Change is exclusive to Aegislash. When using a damage-dealing move, Aegislash switches to its Blade Forme. When using the move King's Shield, Aegislash switches to its Shield Forme.");
        ABILITY_INFO.put("Technician", "Technician increases the power of moves - that are usually base power 60 or below - by 50%.");
        ABILITY_INFO.put("Serene Grace", "Serene Grace doubles the chance of moves' secondary effects occurring - specifically stat changes, status ailments, or flinching.");
        ABILITY_INFO.put("Levitate", "Levitate causes the Pokemon to be raised (like Flying-type Pokemon).");
    }

    public CommandAbilityInfo(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length >= 2 && ABILITY_INFO.containsKey(this.getAbility()))
        {
            this.embed.setTitle("Ability Info: " + this.getAbility());
            this.embed.setDescription(ABILITY_INFO.get(this.getAbility()));

        }
        else this.embed.setDescription(CommandInvalid.getShort());
        return this;
    }

    private String getAbility()
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 1; i < this.msg.length; i++) sb.append(this.msg[i]).append(" ");
        return Global.normalCase(sb.toString().trim());
    }
}
