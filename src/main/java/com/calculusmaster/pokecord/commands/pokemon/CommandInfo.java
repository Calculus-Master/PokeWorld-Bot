package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.Pokeworld;
import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.elements.GrowthRate;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.FileUpload;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommandInfo extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("info")
                .withConstructor(CommandInfo::new)
                .withFeature(Feature.VIEW_UNIQUE_INFO)
                .withCommand(Commands
                        .slash("info", "View information about your Pokemon.")
                        .addSubcommands(
                                new SubcommandData("active", "View information about your active Pokemon."),
                                new SubcommandData("pokemon", "View information about one of your Pokemon.")
                                        .addOption(OptionType.INTEGER, "number", "The number of the Pokemon.", true),
                                new SubcommandData("latest", "View information about your latest acquired Pokemon.")
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        //Input
        String subcommand = Objects.requireNonNull(event.getSubcommandName());
        OptionMapping numberOption = event.getOption("number");

        Pokemon p = null;

        switch(subcommand)
        {
            case "active" -> p = this.playerData.getSelectedPokemon();
            case "pokemon" -> {
                int num = Objects.requireNonNull(numberOption).getAsInt();
                if(num < 1 || num > this.playerData.getPokemonList().size()) return this.error("Invalid Pokemon number.");
                else p = Pokemon.build(this.playerData.getPokemonList().get(num - 1), num);
            }
            case "latest" -> p = Pokemon.build(this.playerData.getPokemonList().get(this.playerData.getPokemonList().size() - 1), this.playerData.getPokemonList().size());
        }

        if(p == null) return this.error();

        //Info
        String tags = (p.isShiny() ? ":star2:" : "") + (p.getPrestigeLevel() > 0 ? ":zap:" : "") + (p.isMastered() ? ":trophy:" : "");
        String title = "Info: %s%s (#%s)".formatted(p.getDisplayName(), tags.isEmpty() ? "" : " " + tags, p.getNumber());

        String level = "**Level %s** (%s)".formatted(p.getLevel(), p.getLevel() == 100 ? "MAX" : p.getExp() + " / " + GrowthRate.getRequiredExp(p.getData().getGrowthRate(), p.getLevel() + 1) + " XP");
        String type = p.getType().stream().map(Type::getStyledName).collect(Collectors.joining("\n"));

        String ability = "*" + p.getAbility().getName() + "*";
        String item = p.hasItem() ? p.getItem().getStyledName() : "None";
        String tm = "%s / %s".formatted(p.getTMs().size(), p.getMaxTMs());

        this.embed
                .setTitle(title)
                .setDescription("""
                        %s
                        Dynamax Level %d (Max: 10)
                        Prestige Level %d (Max: %d)
                        Mega Charges: %s
                        """.formatted(level, p.getDynamaxLevel(), p.getPrestigeLevel(), p.getMaxPrestigeLevel(), p.getMaxMegaCharges() == 0 ? "N/A" : p.getMegaCharges() + " (Max: " + p.getMaxMegaCharges() + ")"))

                .addField("Type", type, true)
                .addField("Gender", Global.normalize(p.getGender().toString()), true)
                .addField("Nature", Global.normalize(p.getNature().toString()), true)

                .addField("Ability", ability, true)
                .addField("Held Item", item, true)
                .addField("TMs", tm, true)

                .setFooter("Pokemon UUID: " + p.getUUID() + "\nEntity: " + p.getEntity() + "\nNumber " + p.getNumber() + " / " + this.playerData.getPokemonList().size());

        if(p.getPrestigeLevel() > 0)
        {
            Function<Double, String> truncate = i -> ((int)(((i - 1.0) * 100) * 100)) / 100. + "%";

            this.embed.addField("Prestige Boosts", """
                    HP: **+%s**
                    SPD: **+%s**
                    Other: **+%s**
                    """.formatted(truncate.apply(p.getPrestigeBonus(Stat.HP)),
                    truncate.apply(p.getPrestigeBonus(Stat.SPD)),
                    truncate.apply(p.getPrestigeBonus(Stat.ATK))), false);
        }

        List<String> statCalcs = new ArrayList<>(), statIVs = new ArrayList<>(), statEVs = new ArrayList<>();
        for(Stat s : Stat.values())
        {
            statCalcs.add(s.toString() + ": **" + p.getStat(s) + "**");
            statIVs.add(p.getIVs().get(s) + " / 31");
            statEVs.add(String.valueOf(p.getEVs().get(s)));
        }

        statCalcs.add("__Total__: **" + p.getTotalStat() + "**");
        statIVs.add("**" + p.getTotalIV() + "**");
        statEVs.add("**" + p.getTotalEV() + "**");

        this.embed.addField("Stats", String.join("\n", statCalcs), true);
        this.embed.addField("IVs", String.join("\n", statIVs), true);
        this.embed.addField("EVs", String.join("\n", statEVs), true);

        String image = Pokemon.getImage(p.getEntity(), p.isShiny(), p, null);
        String attachment = "pokemon_info.png";

        this.embed.setImage("attachment://" + attachment);
        event.replyFiles(FileUpload.fromData(Pokeworld.class.getResourceAsStream(image), attachment)).setEmbeds(this.embed.build()).queue();
        this.embed = null;

        return true;
    }
}
