package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.duel.trainer.TrainerData;
import com.calculusmaster.pokecord.game.duel.trainer.TrainerManager;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CommandTrainerInfo extends Command
{
    public CommandTrainerInfo(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.VIEW_TRAINER_INFO)) return this.invalidMasteryLevel(Feature.VIEW_TRAINER_INFO);

        boolean view = this.msg.length == 2 && this.isNumeric(1) && this.getInt(1) > 0 && this.getInt(1) <= TrainerManager.REGULAR_TRAINERS.size();

        if(view)
        {
            TrainerData data = TrainerManager.REGULAR_TRAINERS.get(this.getInt(1) - 1);

            this.embed.setTitle("Trainer Information: " + data.getName())
                    .setDescription("Trainer ID: " + data.getTrainerID())
                    .addField("Class", "Class " + TrainerManager.getRoman(data.getTrainerClass()), true)
                    .addField("Z-Crystal", data.getZCrystal() == null ? "None" : data.getZCrystal().getStyledName(), true)
                    .addField("Average Level", "Level " + data.getAveragePokemonLevel(), true)
                    .addField("Team", IntStream.range(0, data.getTeam().size()).mapToObj(i -> (i + 1) + ": " + data.getTeam().get(i)).collect(Collectors.joining("\n")), false)
                    .addField("Team Restrictions", data.getRestrictions().isEmpty() ? "None" : data.getRestrictions().stream().map(tr -> "- " + tr.getDescription()).collect(Collectors.joining("\n")), false)
                    .setFooter(this.playerData.getDefeatedTrainers().contains(data.getTrainerID()) ? "You have defeated this Trainer on the current rotation!" : "You have not defeated this Trainer! Use p!trainerduel " + this.getInt(1) + " to challenge them.");
        }
        else this.response = CommandInvalid.getShort();

        return this;
    }
}
