package com.calculusmaster.pokecord.util.helpers;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.DuelHelper;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ButtonEventHelper extends ListenerAdapter
{
    public static final List<String> DUEL_MOVE_BUTTONS = Arrays.asList("pokecord2_duel_move1", "pokecord2_duel_move2", "pokecord2_duel_move3", "pokecord2_duel_move4");

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event)
    {
        if(event.getComponentId().startsWith("pokecord2_duel_move"))
        {
            int moveNum = Integer.parseInt(event.getComponentId().substring(event.getComponentId().length() - 1));
            String playerID = event.getMember().getId();

            if(DuelHelper.isInDuel(playerID))
            {
                Duel d = DuelHelper.instance(playerID);

                if(d.hasPlayerSubmittedMove(playerID))
                {
                    this.sendMsg("You already used a move!", event);
                }
                else if(d.getPlayers()[d.indexOf(playerID)].active.isFainted() && !d.isComplete())
                {
                    this.sendMsg("Your pokemon has fainted! You have to swap to another!", event);
                }
                else
                {
                    d.submitMove(playerID, moveNum, 'm');

                    event.editButton(Button.success(DUEL_MOVE_BUTTONS.get(moveNum - 1), "Move " + moveNum)).queue();
                    event.deferEdit().queue();

                    event.getChannel()
                            .sendMessage("<@" + playerID + ">: Move Submitted!")
                            .delay(15, TimeUnit.SECONDS)
                            .flatMap(Message::delete)
                            .queue();

                    d.checkReady();
                }
            }
            else this.sendMsg("You aren't in a duel!", event);
        }
    }

    private void sendMsg(String content, ButtonClickEvent event)
    {
        event.getChannel().sendMessage("<@" + event.getMember().getId() + ">: " + content).queue();
    }
}
