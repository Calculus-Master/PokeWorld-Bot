package com.calculusmaster.pokecord.commands.economy;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.game.player.PlayerInventory;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandInventory extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("inventory")
                .withConstructor(CommandInventory::new)
                .withCommand(Commands
                        .slash("inventory", "View your item, Z-Crystal and TM inventory.")
                        .addSubcommands(
                                new SubcommandData("items", "View the items you own."),
                                new SubcommandData("zcrystals", "View the Z-Crystals you own."),
                                new SubcommandData("tms", "View the TMs you own.")
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        if(this.isInvalidMasteryLevel(Feature.ACCESS_INVENTORY)) return this.respondInvalidMasteryLevel(Feature.ACCESS_INVENTORY);

        String page = Objects.requireNonNull(event.getSubcommandName());

        PlayerInventory inv = this.playerData.getInventory();

        String title = "";
        List<String> contents = new ArrayList<>();
        switch(page)
        {
            case "items" ->
            {
                title = "Items";

                List<Item> items = new ArrayList<>(inv.getItems().keySet());
                if(items.isEmpty()) contents.add("You do not own any Items.");
                else for(int i = 0; i < inv.getItems().keySet().size(); i++)
                {
                    Item item = items.get(i);

                    contents.add((i + 1) + ": **" + item.getStyledName() + "** | Count: " + inv.getItems().get(item));
                }

                //TODO: Descriptions for each page
            }
            case "zcrystals" ->
            {
                title = "Z-Crystals";

                List<ZCrystal> crystals = new ArrayList<>(inv.getZCrystals());
                if(crystals.isEmpty()) contents.add("You do not own any Z-Crystals.");
                else for (int i = 0; i < inv.getZCrystals().size(); i++)
                {
                    ZCrystal crystal = crystals.get(i);

                    contents.add((i + 1) + ": **" + crystal.getStyledName() + "**");
                }
            }
            case "tms" ->
            {
                title = "TMs";

                List<TM> tms = new ArrayList<>(inv.getTMs().keySet());
                if(tms.isEmpty()) contents.add("You do not own any TMs.");
                else for (int i = 0; i < inv.getTMs().keySet().size(); i++)
                {
                    TM tm = tms.get(i);

                    contents.add((i + 1) + ": **" + tm.toString() + "** | Count: " + inv.getTMs().get(tm) + " | Move: " + tm.getMove().getName());
                }
            }
        }

        this.embed.addField(title, String.join("\n", contents), false);

        return true;
    }
}
