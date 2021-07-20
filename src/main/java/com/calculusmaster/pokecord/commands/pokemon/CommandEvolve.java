package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.SpecialEvolutionRegistry;
import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.duel.DuelHelper;
import com.calculusmaster.pokecord.game.enums.elements.Location;
import com.calculusmaster.pokecord.game.enums.elements.Time;
import com.calculusmaster.pokecord.game.enums.items.PokeItem;
import com.calculusmaster.pokecord.util.helpers.LocationEventHelper;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandEvolve extends Command
{
    public CommandEvolve(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(DuelHelper.isInDuel(this.player.getId()))
        {
            this.sendMsg("You cannot evolve Pokemon while in a duel!");
            return this;
        }

        Pokemon selected = this.playerData.getSelectedPokemon();

        boolean normal = selected.getData().evolutions.size() > 0 && selected.getLevel() >= new ArrayList<>(selected.getData().evolutions.values()).get(0);
        boolean special = SpecialEvolutionRegistry.hasSpecialEvolution(selected.getName()) && SpecialEvolutionRegistry.canEvolve(selected);

        String target = "";
        Location location = LocationEventHelper.getLocation(this.server.getId());
        Time time = LocationEventHelper.getTime();

        //Custom Evolution Overrides
        if(Arrays.asList("Magneton", "Nosepass", "Charjabug").contains(selected.getName()) && location.isMagneticField())
        {
            target = switch(selected.getName()) {
                case "Magneton" -> "Magnezone";
                case "Nosepass" -> "Probopass";
                case "Charjabug" -> "Vikavolt";
                default -> "";
            };
        }

        if(selected.getName().equals("Eevee"))
        {
            if(selected.hasItem() && PokeItem.asItem(selected.getItem()).equals(PokeItem.FRIENDSHIP_BAND) && SpecialEvolutionRegistry.hasFriendship(selected) && time.isNight())
                target = "Umbreon";

            if(selected.hasItem() && PokeItem.asItem(selected.getItem()).equals(PokeItem.FRIENDSHIP_BAND) && SpecialEvolutionRegistry.hasFriendship(selected) && time.isDay())
                target = "Espeon";

            if(target.equals("") && !special && location.isMossyRock()) target = "Leafeon";

            if(target.equals("") && !special && location.isIcyRock()) target = "Glaceon";
        }

        if(selected.getName().equals("Kubfu"))
        {
            if(location.equals(Location.TOWER_OF_WATER)) target = "Urshifu Rapid Strike";
            else if(location.equals(Location.TOWER_OF_DARKNESS)) target = "Urshifu";
        }

        if(selected.getName().equals("Cosmoem") && selected.getLevel() >= 53)
        {
            if(time.isDay()) target = "Solgaleo";
            else if(time.isNight()) target = "Lunala";
        }

        if(selected.getName().equals("Rockruff") && selected.getLevel() >= 25)
        {
            if(time.equals(Time.DUSK)) target = "Lycanroc Dusk";
            else if(time.isDay()) target = "Lycanroc";
            else target = "Lycanroc Night";
        }

        //Basic Special & Normal (Level Up) Evolutions
        if(target.equals("") && special) target = SpecialEvolutionRegistry.getTarget(selected);

        if(target.equals("") && !special && normal) target = new ArrayList<>(selected.getData().evolutions.keySet()).get(0);

        //Evolve
        if(!target.equals(""))
        {
            Pokemon.updateName(selected, target);

            this.playerData.addPokePassExp(500, this.event);
            this.playerData.updateBountyProgression(ObjectiveType.EVOLVE_POKEMON);

            this.sendMsg("`" + selected.getName() + "` evolved into `" + target + "`!");

        }
        else this.sendMsg(selected.getName() + " cannot evolve right now!");

        return this;
    }
}
