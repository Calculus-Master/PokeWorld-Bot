package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandEvolve extends Command
{
    public CommandEvolve(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        this.response = "Manual evolution is currently disabled.";
//        if(this.insufficientMasteryLevel(Feature.EVOLVE_POKEMON)) return this.invalidMasteryLevel(Feature.EVOLVE_POKEMON);
//
//        if(DuelHelper.isInDuel(this.player.getId()))
//        {
//            this.response = "You cannot evolve Pokemon while in a duel!";
//            return this;
//        }
//
//        Pokemon selected = this.playerData.getSelectedPokemon();
//
//        boolean normal = selected.getData().evolutions.size() > 0 && selected.getLevel() >= new ArrayList<>(selected.getData().evolutions.values()).get(0);
//        boolean special = SpecialEvolutionRegistry.hasSpecialEvolution(selected.getRealName()) && SpecialEvolutionRegistry.canEvolve(selected);
//
//        String target = "";
//        Location location = LocationEventHelper.getLocation(this.server.getId());
//        Time time = LocationEventHelper.getTime();
//
//        //Custom Evolution Overrides
//        if(Arrays.asList("Magneton", "Nosepass", "Charjabug").contains(selected.getRealName()) && location.isMagneticField())
//        {
//            target = switch(selected.getRealName()) {
//                case "Magneton" -> "Magnezone";
//                case "Nosepass" -> "Probopass";
//                case "Charjabug" -> "Vikavolt";
//                default -> "";
//            };
//        }
//
//        if(selected.getRealName().equals("Eevee"))
//        {
//            if(selected.hasItem() && selected.getItem().equals(Item.FRIENDSHIP_BAND) && SpecialEvolutionRegistry.hasFriendship(selected) && time.isNight())
//                target = "Umbreon";
//
//            if(selected.hasItem() && selected.getItem().equals(Item.FRIENDSHIP_BAND) && SpecialEvolutionRegistry.hasFriendship(selected) && time.isDay())
//                target = "Espeon";
//
//            if(target.equals("") && !special && location.isMossyRock()) target = "Leafeon";
//
//            if(target.equals("") && !special && location.isIcyRock()) target = "Glaceon";
//        }
//
//        if(selected.getRealName().equals("Kubfu"))
//        {
//            if(location.equals(Location.TOWER_OF_WATER)) target = "Urshifu Rapid Strike";
//            else if(location.equals(Location.TOWER_OF_DARKNESS)) target = "Urshifu";
//        }
//
//        if(selected.getRealName().equals("Cosmoem") && selected.getLevel() >= 53)
//        {
//            if(time.isDay()) target = "Solgaleo";
//            else if(time.isNight()) target = "Lunala";
//        }
//
//        if(selected.getRealName().equals("Rockruff") && selected.getLevel() >= 25)
//        {
//            if(time.equals(Time.DUSK)) target = "Lycanroc Dusk";
//            else if(time.isDay()) target = "Lycanroc";
//            else target = "Lycanroc Night";
//        }
//
//        if(selected.getRealName().equals("Cubone") && selected.getLevel() >= 28 && location.region.equals(Region.ALOLA) && time.isNight())
//            target = "Alolan Marowak";
//
//        if(selected.getRealName().equals("Mantyke") && this.playerData.getPokemon().stream().anyMatch(p -> p.getRealName().equals("Remoraid")))
//            target = "Mantine";
//
//        if(selected.getRealName().equals("Koffing") && selected.getLevel() >= 35 && location.region.equals(Region.GALAR))
//            target = "Galarian Weezing";
//
//        if(selected.getRealName().equals("Exeggcute") && location.region.equals(Region.ALOLA) && selected.hasItem() && selected.getItem().equals(Item.LEAF_STONE))
//            target = "Alolan Exeggutor";
//
//        if(selected.getRealName().equals("Crabrawler") && location.equals(Location.MOUNT_LANAKILA))
//            target = "Crabominable";
//
//        if(selected.getRealName().equals("Galarian Yamask") && location.equals(Location.DUSTY_BOWL))
//            target = "Runerigus";
//
//        //Basic Special & Normal (Level Up) Evolutions
//        if(target.equals("") && special) target = SpecialEvolutionRegistry.getTarget(selected);
//
//        if(target.equals("") && !special && normal) target = new ArrayList<>(selected.getData().evolutions.keySet()).get(0);
//
//        //Evolve
//        if(!target.equals(""))
//        {
//            selected.evolve(target);
//            selected.updateName();
//
//            selected.resetAugments();
//            selected.setDefaultMegaCharges();
//
//            this.playerData.updateBountyProgression(ObjectiveType.EVOLVE_POKEMON);
//            this.playerData.getStatistics().incr(PlayerStatistic.POKEMON_EVOLVED);
//
//            this.response = "`" + selected.getRealName() + "` evolved into `" + target + "`!";
//        }
//        else this.response = selected.getRealName() + " cannot evolve right now!";

        return this;
    }
}
