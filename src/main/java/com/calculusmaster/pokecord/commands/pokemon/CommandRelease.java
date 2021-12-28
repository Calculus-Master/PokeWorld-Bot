package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.bounties.objectives.ReleaseNameObjective;
import com.calculusmaster.pokecord.game.bounties.objectives.ReleasePoolObjective;
import com.calculusmaster.pokecord.game.bounties.objectives.ReleaseTypeObjective;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CommandRelease extends Command
{
    private static final Map<String, Integer> releaseRequests = new HashMap<>();

    public CommandRelease(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.RELEASE_POKEMON)) return this.invalidMasteryLevel(Feature.RELEASE_POKEMON);

        boolean hasActiveRequest = releaseRequests.containsKey(this.player.getId());

        boolean confirm = this.msg.length == 2 && this.msg[1].equals("confirm");
        boolean deny = this.msg.length == 2 && this.msg[1].equals("deny");
        boolean requestRelease = this.msg.length == 2 && (this.isNumeric(1) || "latest".contains(this.msg[1]) || this.msg[1].equals("random"));

        if(confirm || deny)
        {
            if(!hasActiveRequest)
            {
                this.response = "You don't have any active release requests!";
                return this;
            }

            String UUID = this.playerData.getPokemonList().get(releaseRequests.get(this.player.getId()) - 1);
            Pokemon p = Pokemon.build(UUID);
            String label = "**Level " + p.getLevel() + " " + p.getName() + "**";

            if(confirm)
            {
                this.playerData.removePokemon(UUID);
                Pokemon.deletePokemon(p);

                this.playerData.updateBountyProgression(b -> {
                    switch(b.getType()) {
                        case RELEASE_POKEMON -> b.update();
                        case RELEASE_POKEMON_TYPE -> {
                            if(p.isType(((ReleaseTypeObjective)b.getObjective()).getType())) b.update();
                        }
                        case RELEASE_POKEMON_NAME -> {
                            if(p.getName().equals(((ReleaseNameObjective)b.getObjective()).getName())) b.update();
                        }
                        case RELEASE_POKEMON_POOL -> {
                            if(((ReleasePoolObjective)b.getObjective()).getPool().contains(p.getName())) b.update();
                        }
                    }
                });

                releaseRequests.remove(this.player.getId());

                this.response = "Released your " + label + "!";
                return this;
            }
            else if(deny)
            {
                releaseRequests.remove(this.player.getId());

                this.response = "Cancelled release of your " + label + "!";
                return this;
            }
        }
        else if(requestRelease)
        {
            if(this.playerData.getPokemonList().size() == 1)
            {
                this.response = "You can't release your last Pokemon!";
                return this;
            }
            else if(hasActiveRequest)
            {
                this.response = "You already have an active release request (Type `p!release deny` to remove it)!";
                return this;
            }
            else
            {
                int index = "latest".contains(this.msg[1]) ? this.playerData.getPokemonList().size() : (this.msg[1].equals("random") ? new Random().nextInt(this.playerData.getPokemonList().size()) + 1 : this.getInt(1));
                releaseRequests.put(this.player.getId(), index);

                String UUID = this.playerData.getPokemonList().get(releaseRequests.get(this.player.getId()) - 1);
                Pokemon p = Pokemon.build(UUID);
                String label = "**Level " + p.getLevel() + " " + p.getName() + "**";

                this.response = "Do you want to release your " + label + "? Type `p!release confirm` or `p!release deny` to continue!";
                return this;
            }
        }
        else
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }
        return this;
    }
}
