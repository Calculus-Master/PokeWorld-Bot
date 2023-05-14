package com.calculusmaster.pokecord.commands.player;

import com.calculusmaster.pokecord.Pokeworld;
import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.pokemon.sort.PokemonListOrderType;
import com.calculusmaster.pokecord.game.settings.Settings;
import com.calculusmaster.pokecord.game.settings.value.DoubleValue;
import com.calculusmaster.pokecord.game.settings.value.SingleValue;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CommandSettings extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("settings")
                .withConstructor(CommandSettings::new)
                .withCommand(Commands
                        .slash("settings", "Change settings!")
                        .addSubcommandGroups(
                                new SubcommandGroupData("player", "Edit player settings.")
                                        .addSubcommands(
                                                new SubcommandData("default-sort-order", "Change the default sort order for /pokemon and /market.")
                                                        .addOption(OptionType.STRING, "order", "The order to sort by.", false, true)
                                                        .addOption(OptionType.BOOLEAN, "direction", "The direction to sort in. True = Descending, False = Ascending.", false)
                                        ),
                                new SubcommandGroupData("server", "Edit server settings. (Requires Admin permissions!)")
                                        .addSubcommands(
                                                new SubcommandData("spawn-channel", "Set specific channels for Pokemon to spawn in.")
                                                        .addOption(OptionType.CHANNEL, "channel", "The channel to set.", false),
                                                new SubcommandData("duel-channel", "Set specific channels for Duels to take place in.")
                                                        .addOption(OptionType.CHANNEL, "channel", "The channel to set.", false),
                                                new SubcommandData("trade-channel", "Set specific channels for Trades to take place in.")
                                                        .addOption(OptionType.CHANNEL, "channel", "The channel to set.", false)
                                        )
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        String group = Objects.requireNonNull(event.getSubcommandGroup());
        String settingName = Objects.requireNonNull(event.getSubcommandName());

        if(group.equals("server") && !this.isPokeAdmin(Objects.requireNonNull(event.getMember())))
            return this.error("You must have " + Pokeworld.NAME + " Admin permissions to edit server settings!");

        //Options
        OptionMapping orderOption = event.getOption("order");
        OptionMapping directionOption = event.getOption("direction");
        OptionMapping channelOption = event.getOption("channel");

        switch(settingName)
        {
            case "default-sort-order" ->
            {
                var defaultSetting = Settings.DEFAULT_SORT_ORDER.getDefault();
                var current = Settings.DEFAULT_SORT_ORDER.getSetting(this.playerData);

                PokemonListOrderType newOrder = orderOption == null ? defaultSetting.get1() : PokemonListOrderType.cast(orderOption.getAsString());
                if(newOrder == null) return this.error("Invalid sort order name.");

                boolean newDirection = directionOption == null ? defaultSetting.get2() : directionOption.getAsBoolean();
                var newOption = new DoubleValue<>(newOrder, newDirection);

                if(newOption.equals(current)) return this.error("This is already your current default sort order!");

                Settings.DEFAULT_SORT_ORDER.update(this.player.getId(), newOption);
                this.response = "Updated your default sort order to **" + newOrder.getName() + "** (*" + (newDirection ? "Descending" : "Ascending") + "*)!";
            }
            case "spawn-channel", "duel-channel", "trade-channel" ->
            {
                boolean spawn = settingName.equals("spawn-channel");
                boolean duel = settingName.equals("duel-channel");
                boolean trade = settingName.equals("trade-channel");

                var holder = spawn ? Settings.SPAWN_CHANNEL : duel ? Settings.DUEL_CHANNEL : Settings.TRADE_CHANNEL;
                var current = holder.getSetting(this.serverData);

                TextChannel channel = channelOption != null ? channelOption.getAsChannel().asTextChannel() : event.getChannel().asTextChannel();
                List<TextChannel> list = new ArrayList<>(current.get());

                //Remove
                if(current.get().stream().anyMatch(t -> t.getId().equals(channel.getId())))
                {
                    list.removeIf(t -> t.getId().equals(channel.getId()));
                    holder.update(this.server.getId(), new SingleValue<>(list));

                    String extra = list.isEmpty() ? (spawn ? "WARNING: You have no spawn channels set up. No Pokemon will spawn!" : duel ? "You have no Duel channels restricted. Duels will be allowed in any channel!" : "You have no Trade channels restricted. Trades will be allowed in any channel!") : "";
                    this.response = "Removed " + channel.getAsMention() + " from the " + (spawn ? "Spawn" : duel ? "Duel" : "Trade") + " Channel list!" + (extra.isEmpty() ? "" : "\n*" + extra + "*");
                }
                //Add
                else
                {
                    list.add(channel);
                    holder.update(this.server.getId(), new SingleValue<>(list));

                    String extra = list.size() == 1 ? (duel ? "You have have now added a Duel channel. Duels will only be allowed in this channel!" : trade ? "You have have now added a Trade channel. Trades will only be allowed in this channel!" : "") : "";
                    this.response = "Added " + channel.getAsMention() + " to the " + (spawn ? "Spawn" : duel ? "Duel" : "Trade") + " Channel list!" + (extra.isEmpty() ? "" : "\n*" + extra + "*");
                }
            }
        }

        return true;
    }

    @Override
    protected boolean autocompleteLogic(CommandAutoCompleteInteractionEvent event)
    {
        String name = event.getFocusedOption().getName();
        String input = event.getFocusedOption().getValue();

        if(name.equals("default-sort-order"))
            event.replyChoiceStrings(this.getAutocompleteOptions(input, Arrays.stream(PokemonListOrderType.values()).map(PokemonListOrderType::getName).toList())).queue();

        return true;
    }
}
