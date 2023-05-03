package com.calculusmaster.pokecord.game.settings;

import com.calculusmaster.pokecord.Pokeworld;
import com.calculusmaster.pokecord.game.pokemon.sort.PokemonListOrderType;
import com.calculusmaster.pokecord.game.settings.core.DoubleValue;
import com.calculusmaster.pokecord.game.settings.core.PlayerSettingHolder;
import com.calculusmaster.pokecord.game.settings.core.ServerSettingHolder;
import com.calculusmaster.pokecord.game.settings.core.SingleValue;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Settings
{
    //Server
    public static final ServerSettingHolder<SingleValue<List<TextChannel>>> SPAWN_CHANNEL = new ServerSettingHolder<>(
            "spawn_channel",
            () -> new SingleValue<>(new ArrayList<>()),
            s -> new SingleValue<>(Arrays.stream(s.split("\\|")).map(Pokeworld.BOT_JDA::getTextChannelById).toList()),
            v -> v.get().stream().map(TextChannel::getId).collect(Collectors.joining("|"))
    );

    public static final ServerSettingHolder<SingleValue<List<TextChannel>>> DUEL_CHANNEL = new ServerSettingHolder<>(
            "duel_channel",
            () -> new SingleValue<>(new ArrayList<>()),
            s -> new SingleValue<>(Arrays.stream(s.split("\\|")).map(Pokeworld.BOT_JDA::getTextChannelById).toList()),
            v -> v.get().stream().map(TextChannel::getId).collect(Collectors.joining("|"))
    );

    public static final ServerSettingHolder<SingleValue<List<TextChannel>>> TRADE_CHANNEL = new ServerSettingHolder<>(
            "trade_channel",
            () -> new SingleValue<>(new ArrayList<>()),
            s -> new SingleValue<>(Arrays.stream(s.split("\\|")).map(Pokeworld.BOT_JDA::getTextChannelById).toList()),
            v -> v.get().stream().map(TextChannel::getId).collect(Collectors.joining("|"))
    );

    //Player
    public static final PlayerSettingHolder<DoubleValue<PokemonListOrderType, Boolean>> DEFAULT_SORT_ORDER = new PlayerSettingHolder<>(
            "default_sort_order",
            () -> new DoubleValue<>(PokemonListOrderType.NUMBER, false),
            s -> new DoubleValue<>(PokemonListOrderType.valueOf(s.split("\\|")[0]), Boolean.parseBoolean(s.split("\\|")[1])),
            v -> v.get1().toString() + "|" + v.get2()
    );
}
