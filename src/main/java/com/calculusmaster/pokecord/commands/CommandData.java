package com.calculusmaster.pokecord.commands;

import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;
import java.util.function.Supplier;

public class CommandData
{
    //Core
    private final String commandName;
    private Supplier<PokeWorldCommand> supplier;
    private SlashCommandData slashCommandData;
    private Feature feature;

    //Interaction Components
    private List<String> buttonIDs;
    private List<String> modalIDs;
    private List<String> stringSelectIDs;
    private List<String> entitySelectIDs;

    private CommandData(String commandName)
    {
        this.commandName = commandName;
        this.supplier = null;
        this.slashCommandData = null;
        this.feature = null;

        this.buttonIDs = List.of();
        this.modalIDs = List.of();
        this.stringSelectIDs = List.of();
        this.entitySelectIDs = List.of();
    }

    public void register()
    {
        CommandHandler.COMMANDS.add(this);
        CommandHandler.COMMAND_DATA.put(this.commandName, this);
    }

    public static CommandData create(String commandName)
    {
        return new CommandData(commandName);
    }

    public CommandData withConstructor(Supplier<PokeWorldCommand> supplier)
    {
        this.supplier = supplier;
        return this;
    }

    public CommandData withCommand(SlashCommandData slashCommandData)
    {
        this.slashCommandData = slashCommandData;
        if(!slashCommandData.getName().equals(this.commandName))
        {
            LoggerHelper.warn(CommandData.class, "Slash Command Name (" + this.slashCommandData.getName() + ") does not match CommandData name (" + this.commandName + ")! Manually overwriting Slash Command Name to CommandData name!");
            this.slashCommandData.setName(this.commandName);
        }
        return this;
    }

    public CommandData withFeature(Feature feature)
    {
        this.feature = feature;
        return this;
    }

    public CommandData withButtons(String... buttonIDs)
    {
        this.buttonIDs = List.of(buttonIDs);
        return this;
    }

    public CommandData withModals(String... modalIDs)
    {
        this.modalIDs = List.of(modalIDs);
        return this;
    }

    public CommandData withStringSelects(String... stringSelectIDs)
    {
        this.stringSelectIDs = List.of(stringSelectIDs);
        return this;
    }

    public CommandData withEntitySelects(String... entitySelectIDs)
    {
        this.entitySelectIDs = List.of(entitySelectIDs);
        return this;
    }

    public boolean hasButton(String buttonID)
    {
        return this.buttonIDs.contains(buttonID);
    }

    public boolean hasModal(String modalID)
    {
        return this.modalIDs.contains(modalID);
    }

    public boolean hasStringSelect(String stringSelectID)
    {
        return this.stringSelectIDs.contains(stringSelectID);
    }

    public boolean hasEntitySelect(String entitySelectID)
    {
        return this.entitySelectIDs.contains(entitySelectID);
    }

    public boolean hasFeature()
    {
        return this.feature != null;
    }

    public Feature getFeature()
    {
        return this.feature;
    }

    public SlashCommandData getSlashCommandData()
    {
        return this.slashCommandData;
    }

    public PokeWorldCommand getInstance()
    {
        PokeWorldCommand instance = this.supplier.get();
        instance.setCommandData(this);
        return instance;
    }

    public String getCommandName()
    {
        return this.commandName;
    }
}
