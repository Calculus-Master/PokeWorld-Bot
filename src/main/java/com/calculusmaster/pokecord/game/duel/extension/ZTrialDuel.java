package com.calculusmaster.pokecord.game.duel.extension;

import com.calculusmaster.pokecord.Pokeworld;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.component.DuelStatus;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.players.UserPlayer;
import com.calculusmaster.pokecord.game.duel.players.WildPlayer;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

import static com.calculusmaster.pokecord.game.duel.core.DuelHelper.BACKGROUND;
import static com.calculusmaster.pokecord.game.duel.core.DuelHelper.DUELS;
import static com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity.Rarity.*;

//PVE Duel to Earn a Typed Z-Crystal
public class ZTrialDuel extends WildDuel
{
    private Type type;

    public static Duel create(String playerID, TextChannel channel, Type type)
    {
        ZTrialDuel duel = new ZTrialDuel();

        duel.setStatus(DuelStatus.WAITING);
        duel.setTurn();
        duel.addChannel(channel);
        duel.setPlayers(playerID, "BOT", 1);
        duel.setWildPokemon(type);
        duel.setDefaults();
        duel.setDuelPokemonObjects(0);
        duel.setDuelPokemonObjects(1);

        DUELS.put(playerID, duel);
        return duel;
    }

    @Override
    public void sendWinEmbed()
    {
        EmbedBuilder embed = new EmbedBuilder();
        ZCrystal crystal = Objects.requireNonNull(ZCrystal.getCrystalOfType(this.type));

        //Player won
        if(this.getWinner() instanceof UserPlayer player)
        {
            this.onWildDuelWon(true);

            player.data.getInventory().addZCrystal(crystal);

            embed.setDescription("You won! You acquired a new Z-Crystal: `%s`.".formatted(crystal.getStyledName()));
        }
        //Player lost
        else
        {
            embed.setDescription("You lost! You weren't able to earn a Z-Crystal.");
        }

        this.sendEmbed(embed.build());
        DuelHelper.delete(this.players[0].ID);
    }

    private void setWildPokemon(Type type)
    {
        this.type = type;

        PokemonEntity pokemon = PokemonRarity.getPokemon(false, type, DIAMOND, PLATINUM, MYTHICAL, ULTRA_BEAST, LEGENDARY);

        this.players[1] = new WildPlayer(pokemon, Math.max(80, this.players[0].active.getLevel()));

        this.players[1].active.getBoosts().setStatBoost(1.2);
        this.players[1].active.getBoosts().setHealthBoost(1.8);
        Arrays.stream(Stat.values()).forEach(s -> this.players[1].active.setEV(s, 50));
        this.players[1].active.setHealth(this.players[1].active.getMaxHealth());
    }

    @Override
    protected String getHB(int p)
    {
        StringBuilder sb = new StringBuilder();

        if(this.players[p] instanceof UserPlayer player) sb.append(player.getName()).append("'s ");
        else sb.append("Trial Pokemon ");

        sb.append(this.players[p].active.getName()).append(": ");

        if(this.players[p].active.isFainted()) sb.append("FAINTED");
        else sb.append(this.players[p].active.getHealth()).append(" / ").append(this.players[p].active.getStat(Stat.HP)).append(" HP ").append(this.players[p].active.getActiveStatusConditions());

        return sb.toString();
    }

    @Override
    public InputStream getImage() throws Exception
    {
        //Background is 800 x 480 -> 400 x 240
        int spacing = 25;
        int backgroundW = 800;
        int backgroundH = 480;
        int hint = BufferedImage.TYPE_INT_ARGB;

        Image background = ImageIO.read(new URL(BACKGROUND)).getScaledInstance(backgroundW, backgroundH, hint);
        BufferedImage combined = new BufferedImage(background.getWidth(null), background.getHeight(null), hint);

        combined.getGraphics().drawImage(background, 0, 0, null);

        int baseSize = 250;
        int y = (backgroundH - baseSize) / 2;

        if(!this.players[0].active.isFainted())
        {
            int size = this.players[0].active.isDynamaxed() ? (int)(baseSize * 1.25) : baseSize;

            String image = Pokemon.getImage(this.players[0].active.getEntity(), this.players[0].active.isShiny(), this.players[0].active, this.players[0].move == null ? null : this.players[0].move.getEntity());
            URL resource = Pokeworld.class.getResource(image);

            if(resource != null)
            {
                Image p = ImageIO.read(resource).getScaledInstance(size, size, hint);
                combined.getGraphics().drawImage(p, spacing, y, null);
            }
            else LoggerHelper.warn(Duel.class, "Pokemon Image not found. Entity: " + this.players[0].active.getEntity().toString() + ", Image File: " + image);
        }

        if(!this.players[1].active.isFainted())
        {
            baseSize = 400;
            y = (backgroundH - baseSize) / 2;

            int size = this.players[1].active.isDynamaxed() ? (int)(baseSize * 1.25) : baseSize;

            String image = Pokemon.getImage(this.players[1].active.getEntity(), this.players[1].active.isShiny(), this.players[1].active, this.players[1].move == null ? null : this.players[1].move.getEntity());
            URL resource = Pokeworld.class.getResource(image);

            if(resource != null)
            {
                Image p = ImageIO.read(resource).getScaledInstance(size, size, hint);
                combined.getGraphics().drawImage(p, (backgroundW - spacing) - size, y, null);
            }
            else LoggerHelper.warn(Duel.class, "Pokemon Image not found. Entity: " + this.players[1].active.getEntity().toString() + ", Image File: " + image);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(combined, "png", out);

        byte[] bytes = out.toByteArray(); //This is the slow line

        return new ByteArrayInputStream(bytes);
    }
}
