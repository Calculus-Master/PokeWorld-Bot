package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.extension.TrainerDuel;
import com.calculusmaster.pokecord.game.duel.trainer.TrainerData;
import com.calculusmaster.pokecord.game.duel.trainer.TrainerManager;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class CommandTrainerDuel extends Command
{
    public CommandTrainerDuel(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.PVE_DUELS_TRAINER)) return this.invalidMasteryLevel(Feature.PVE_DUELS_TRAINER);

        boolean regular = this.msg.length == 2 && this.isNumeric(1) && this.getInt(1) >= 1 && this.getInt(1) <= TrainerManager.REGULAR_TRAINERS.size();

        if(regular)
        {
            if(DuelHelper.isInDuel(this.player.getId()))
            {
                this.response = "You are already in a duel!";
                return this;
            }

            TrainerData trainer = TrainerManager.REGULAR_TRAINERS.get(this.getInt(1) - 1);

            if(this.playerData.getTeam().size() < trainer.getTeam().size())
                this.event.getChannel().sendMessage("Warning! The trainer's Pokemon team is larger than yours. It has " + trainer.getTeam().size() + " Pokemon.").queue();

            if(this.playerData.getTeam().size() > 6)
            {
                this.response = "Your team is too large! You can have a maximum of 6 Pokemon in your team in Trainer Duels.";
                return this;
            }

            List<Pokemon> team = this.playerData.getTeamPokemon();
            if(!trainer.getRestrictions().stream().allMatch(tr -> tr.validate(team)))
            {
                this.response = "Your team does not meet this Trainer's Team Restrictions! For more information on the Trainer's Team Restrictions, use the `p!trainerinfo <num>` command!";
                return this;
            }

            Duel d = TrainerDuel.create(this.player.getId(), this.event, trainer);
            this.event.getChannel().sendMessage("You challenged %s!".formatted(trainer.getName())).queue();
            this.embed = null;
            d.sendTurnEmbed();
        }
        else
        {
            this.embed
                    .setTitle("Pokemon Trainer Duels")
                    .setDescription("Every rotation, a new set of Trainers will be available for you to challenge. They are divided by their trainer class, which affects the types of Team restrictions and nature of the Trainer's team that you will fight. Defeat all the Trainers of each class to earn rewards!");

            int c = 1;
            for(int i = 1; i <= TrainerManager.getMax(); i++)
            {
                List<String> desc = new ArrayList<>();

                for(TrainerData d : TrainerManager.getTrainersOfClass(i))
                {
                    desc.add("%s: %s | Level %s | Team: %s | Restrictions: %s | Defeated: %s".formatted(c, d.getName(), d.getAveragePokemonLevel(), d.getTeam().size(), d.getRestrictions().size(), this.playerData.hasDefeatedTrainer(d.getTrainerID()) ? ":white_check_mark:" : ":x:"));
                    c++;
                }

                this.embed.addField("Class " + TrainerManager.getRoman(i) + " Trainers", String.join("\n", desc), false);
            }

            this.embed.addField("Elite Trainers", "You can duel challenging, randomized Elite Trainers using the `r!elite` command.", false);
        }
        return this;
    }
}
