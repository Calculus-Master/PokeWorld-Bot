package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.DuelHelper;
import com.calculusmaster.pokecord.game.duel.TrainerDuel;
import com.calculusmaster.pokecord.game.duel.elements.Trainer;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandTrainerDuel extends Command
{
    public CommandTrainerDuel(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length == 2 && this.isNumeric(1) && this.getInt(1) >= 1 && this.getInt(1) <= Trainer.DAILY_TRAINERS.size())
        {
            if(DuelHelper.isInDuel(this.player.getId()))
            {
                this.event.getMessage().getChannel().sendMessage(this.playerData.getMention() + ": You are already in a duel!").queue();
                this.embed = null;
                return this;
            }

            Trainer.TrainerInfo trainer = Trainer.DAILY_TRAINERS.get(this.getInt(1) - 1);
            Duel d = TrainerDuel.create(this.player.getId(), this.event, trainer);

            this.event.getMessage().getChannel().sendMessage(this.playerData.getMention() + ": You challenged " + trainer.name + " !").queue();
            this.embed = null;

            d.sendTurnEmbed();
        }
        else
        {
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < Trainer.DAILY_TRAINERS.size(); i++) sb.append(i + 1).append(": ").append(Trainer.DAILY_TRAINERS.get(i).name).append(Trainer.PLAYER_TRAINERS_DEFEATED.get(Trainer.DAILY_TRAINERS.get(i).name).contains(this.player.getId()) ? " (Defeated)" : "").append("\n");

            this.embed.setDescription(sb.toString());
            this.embed.setTitle("Daily Trainers");
        }
        return this;
    }
}
