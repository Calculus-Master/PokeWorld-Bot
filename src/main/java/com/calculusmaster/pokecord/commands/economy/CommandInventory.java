package com.calculusmaster.pokecord.commands.economy;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.game.player.components.PlayerInventory;
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
                .withFeature(Feature.ACCESS_INVENTORY)
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
        String page = Objects.requireNonNull(event.getSubcommandName());

        PlayerInventory inv = this.playerData.getInventory();

        String title = "";
        String description = "";
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

                    contents.add((i + 1) + ": **" + item.getName() + "** | Count: " + inv.getItems().get(item));
                }

                description = """
                        Items can be given to Pokemon, and have a wide variety of uses.
                        They could provide minor boosts, immunities, affect specific Pokemon, assist in Evolutions, and much more.
                        
                        To give an item to your active Pokemon, use `/item give`.
                        Most items can be purchased from the shop – use `/shop items` to see current shop offerings.
                        
                        *Note: Items are one-time use! Giving them to a Pokemon will remove them from your inventory.
                        If you want to give multiple Pokemon the same item, you will need to acquire multiple copies of the item.*
                        """;
            }
            case "zcrystals" ->
            {
                title = "Z-Crystals";

                List<ZCrystal> crystals = new ArrayList<>(inv.getZCrystals());
                if(crystals.isEmpty()) contents.add("You do not own any Z-Crystals.");
                else for (int i = 0; i < inv.getZCrystals().size(); i++)
                {
                    ZCrystal crystal = crystals.get(i);

                    contents.add((i + 1) + ": **" + crystal.getName() + "**");
                }

                description = """
                        Z-Crystals are special kinds of Items that will empower your Pokemon to use a Z-Move in battle.
                        You can earn the typed Z-Crystals (ones that will empower a certain Typed move), by completing Z-Trial Duels.
                        Unique Z-Crystals, which are special to certain Pokemon & moves, can be purchased from the shop.
                        """;
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

                description = """
                        TMs (Technical Machines) are special items that let you teach your Pokemon moves that they normally could not learn by leveling up.
                        Every Pokemon can learn a certain number of TM moves, visible in their PokeDex entry.
                        You can purchase TMs from the shop!
                        """;
            }
        }

        this.embed
                .setTitle(this.playerData.getUsername() + "'s Inventory")
                .setDescription(description)
                .addField(title, String.join("\n", contents), false);

        return true;
    }
}
