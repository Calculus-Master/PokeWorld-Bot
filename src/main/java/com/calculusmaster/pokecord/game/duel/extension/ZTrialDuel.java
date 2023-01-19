package com.calculusmaster.pokecord.game.duel.extension;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.players.UserPlayer;
import com.calculusmaster.pokecord.game.duel.players.WildPlayer;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.util.helpers.DataHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.calculusmaster.pokecord.game.duel.core.DuelHelper.BACKGROUND;
import static com.calculusmaster.pokecord.game.duel.core.DuelHelper.DUELS;

//PVE Duel to Earn a Typed Z-Crystal
public class ZTrialDuel extends WildDuel
{
    private Type type;

    public static Duel create(String playerID, MessageReceivedEvent event, Type type)
    {
        ZTrialDuel duel = new ZTrialDuel();

        duel.setStatus(DuelHelper.DuelStatus.WAITING);
        duel.setTurn();
        duel.addChannel(event.getChannel().asTextChannel());
        duel.setPlayers(playerID, "BOT", 1);
        duel.setWildPokemon(type);
        duel.setDefaults();
        duel.setDuelPokemonObjects(0);
        duel.setDuelPokemonObjects(1);

        DUELS.add(duel);
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

            player.data.addZCrystal(crystal.toString());
            Achievements.grant(this.players[this.current].ID, Achievements.ACQUIRED_FIRST_TYPED_ZCRYSTAL, null);

            embed.setDescription("You won! You acquired a new Z-Crystal: `%s`.".formatted(crystal.getStyledName()));
        }
        //Player lost
        else
        {
            this.getUser().data.updateBountyProgression(ObjectiveType.COMPLETE_WILD_DUEL);
            embed.setDescription("You lost! You weren't able to earn a Z-Crystal.");
        }

        this.sendEmbed(embed.build());
        DuelHelper.delete(this.players[0].ID);
    }

    private void setWildPokemon(Type type)
    {
        List<String> pool = DataHelper.TYPE_LISTS.get(type);
        String pokemon = pool.get(new Random().nextInt(pool.size()));

        this.players[1] = new WildPlayer(pokemon, Math.max(80, this.players[0].active.getLevel()));
        this.type = type;

        this.players[1].active.getBoosts().setStatBoost(1.5);
        this.players[1].active.getBoosts().setHealthBoost(2.75);
        Arrays.stream(Stat.values()).forEach(s -> this.players[1].active.setEV(s, 50));
        this.players[1].active.setHealth(this.players[1].active.getMaxHealth());
    }

    @Override
    protected String getHB(int p)
    {
        StringBuilder sb = new StringBuilder();

        if(this.players[p] instanceof UserPlayer player) sb.append(player.getName()).append("'s ");
        else sb.append("Z-Trial Leader ");

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

            Image p1 = ImageIO.read(new URL(this.getPokemonURL(0))).getScaledInstance(size, size, hint);
            combined.getGraphics().drawImage(p1, spacing, y, null);
        }

        if(!this.players[1].active.isFainted())
        {
            baseSize = 400;
            y = (backgroundH - baseSize) / 2;

            int size = this.players[1].active.isDynamaxed() ? (int)(baseSize * 1.25) : baseSize;

            Image p2 = ImageIO.read(new URL(this.getPokemonURL(1))).getScaledInstance(size, size, hint);
            combined.getGraphics().drawImage(p2, (backgroundW - spacing) - size, y, null);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(combined, "png", out);

        byte[] bytes = out.toByteArray(); //This is the slow line

        return new ByteArrayInputStream(bytes);
    }
}
