package com.calculusmaster.pokecord.util.listener;

import com.calculusmaster.pokecord.commands.duel.CommandUse;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ButtonListener extends ListenerAdapter
{
    public static final List<String> DUEL_MOVE_BUTTONS = Arrays.asList("pokecord2_duel_move1", "pokecord2_duel_move2", "pokecord2_duel_move3", "pokecord2_duel_move4");
    public static final String DUEL_ZMOVE_BUTTON = "pokecord2_duel_zmove";
    public static final String DUEL_DYNAMAX_BUTTON = "pokecord2_duel_dynamax";

    public static final Map<String, Boolean> ZMOVE_SELECTIONS = new HashMap<>();
    public static final Map<String, Boolean> DYNAMAX_SELECTIONS = new HashMap<>();

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event)
    {
        String playerID = event.getMember().getId();
        String mention = "<@" + playerID + ">: ";

        if(event.getComponentId().startsWith("pokecord2_duel_move"))
        {
            int moveNum = Integer.parseInt(event.getComponentId().substring(event.getComponentId().length() - 1));

            String[] msg;

            if(ZMOVE_SELECTIONS.getOrDefault(playerID, false)) msg = new String[]{"use", "z", "" + moveNum};
            else if(DYNAMAX_SELECTIONS.getOrDefault(playerID, false)) msg = new String[]{"use", "d", "" + moveNum};
            else msg = new String[]{"use", "" + moveNum};

            new CommandUse(event, msg).runCommand();

            event.editButton(Button.success(DUEL_MOVE_BUTTONS.get(moveNum - 1), "Move " + moveNum)).queue();

            event.getChannel()
                    .sendMessage(mention + "Move Submitted!")
                    .delay(5, TimeUnit.SECONDS)
                    .flatMap(Message::delete)
                    .queue();
        }
        else if(event.getComponentId().equals(DUEL_ZMOVE_BUTTON))
        {
            boolean newValue = !ZMOVE_SELECTIONS.containsKey(playerID) || !ZMOVE_SELECTIONS.get(playerID);

            ZMOVE_SELECTIONS.put(playerID, newValue);

            event.editButton(Button.success(DUEL_ZMOVE_BUTTON, "Z-Move")).queue();

            event.getChannel()
                    .sendMessage(mention + "Z-Move " + (newValue ? " Selected" : " Unselected") + "!")
                    .delay(5, TimeUnit.SECONDS)
                    .flatMap(Message::delete)
                    .queue();
        }
        else if(event.getComponentId().equals(DUEL_DYNAMAX_BUTTON))
        {
            boolean newValue = !DYNAMAX_SELECTIONS.containsKey(playerID) || !DYNAMAX_SELECTIONS.get(playerID);

            DYNAMAX_SELECTIONS.put(playerID, newValue);

            event.editButton(Button.success(DUEL_DYNAMAX_BUTTON, "Dynamax")).queue();

            event.getChannel()
                    .sendMessage(mention + "Dynamax " + (newValue ? " Selected" : " Unselected") + "!")
                    .delay(5, TimeUnit.SECONDS)
                    .flatMap(Message::delete)
                    .queue();
        }
    }
}
