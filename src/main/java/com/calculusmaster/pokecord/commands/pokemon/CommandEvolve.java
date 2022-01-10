package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.elements.Location;
import com.calculusmaster.pokecord.game.enums.elements.Region;
import com.calculusmaster.pokecord.game.enums.elements.Time;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.SpecialEvolutionRegistry;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import com.calculusmaster.pokecord.util.helpers.CacheHelper;
import com.calculusmaster.pokecord.util.helpers.event.LocationEventHelper;
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
        if(this.insufficientMasteryLevel(Feature.EVOLVE_POKEMON)) return this.invalidMasteryLevel(Feature.EVOLVE_POKEMON);

        if(DuelHelper.isInDuel(this.player.getId()))
        {
            this.response = "You cannot evolve Pokemon while in a duel!";
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
            if(selected.hasItem() && Item.asItem(selected.getItem()).equals(Item.FRIENDSHIP_BAND) && SpecialEvolutionRegistry.hasFriendship(selected) && time.isNight())
                target = "Umbreon";

            if(selected.hasItem() && Item.asItem(selected.getItem()).equals(Item.FRIENDSHIP_BAND) && SpecialEvolutionRegistry.hasFriendship(selected) && time.isDay())
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

        if(selected.getName().equals("Cubone") && selected.getLevel() >= 28 && location.region.equals(Region.ALOLA) && time.isNight())
            target = "Alolan Marowak";

        if(selected.getName().equals("Mantyke") && CacheHelper.POKEMON_LISTS.get(this.player.getId()).stream().anyMatch(p -> p.getName().equals("Remoraid")))
            target = "Mantine";

        if(selected.getName().equals("Koffing") && selected.getLevel() >= 35 && location.region.equals(Region.GALAR))
            target = "Galarian Weezing";

        if(selected.getName().equals("Exeggcute") && location.region.equals(Region.ALOLA) && selected.hasItem() && Item.asItem(selected.getItem()).equals(Item.LEAF_STONE))
            target = "Alolan Exeggutor";

        if(selected.getName().equals("Crabrawler") && location.equals(Location.MOUNT_LANAKILA))
            target = "Crabominable";

        if(selected.getName().equals("Galarian Yamask") && location.equals(Location.DUSTY_BOWL))
            target = "Runerigus";

        //Basic Special & Normal (Level Up) Evolutions
        if(target.equals("") && special) target = SpecialEvolutionRegistry.getTarget(selected);

        if(target.equals("") && !special && normal) target = new ArrayList<>(selected.getData().evolutions.keySet()).get(0);

        //Evolve
        if(!target.equals(""))
        {
            Pokemon.updateName(selected, target);

            this.playerData.updateBountyProgression(ObjectiveType.EVOLVE_POKEMON);
            this.playerData.getStatistics().incr(PlayerStatistic.POKEMON_EVOLVED);

            this.response = "`" + selected.getName() + "` evolved into `" + target + "`!";
        }
        else this.response = selected.getName() + " cannot evolve right now!";

        return this;
    }
}
