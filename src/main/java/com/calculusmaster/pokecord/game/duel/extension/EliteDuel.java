package com.calculusmaster.pokecord.game.duel.extension;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.component.DuelStatus;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.players.TrainerPlayer;
import com.calculusmaster.pokecord.game.duel.players.UserPlayer;
import com.calculusmaster.pokecord.game.duel.trainer.TrainerData;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.game.player.level.PMLExperience;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity;
import com.calculusmaster.pokecord.game.pokemon.evolution.MegaEvolutionRegistry;
import com.calculusmaster.pokecord.util.enums.StatisticType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.*;

import static com.calculusmaster.pokecord.game.duel.core.DuelHelper.DUELS;

public class EliteDuel extends TrainerDuel
{
    public static Duel create(String playerID, TextChannel channel)
    {
        EliteDuel duel = new EliteDuel();

        duel.setStatus(DuelStatus.WAITING);
        duel.setTurn();
        duel.addChannel(channel);
        duel.setPlayers(playerID, "Elite Trainer", 6);
        duel.setEliteTrainer();
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

        //Player won
        if(this.getWinner() instanceof UserPlayer player)
        {
            int credits = new SplittableRandom().nextInt(500, 1000);
            player.data.changeCredits(credits);

            player.data.addExp(PMLExperience.DUEL_ELITE, 95);

            player.data.getStatistics().increase(StatisticType.ELITE_DUELS_WON);
            player.data.getStatistics().increase(StatisticType.TRAINER_DUELS_WON);

            embed.setDescription("You defeated the Elite Trainer and earned " + credits + " credits!");
        }
        //Player lost
        else embed.setDescription("You were defeated by the Elite Trainer!");

        this.sendEmbed(embed.build());

        this.uploadEVs(0);
        this.uploadExperience();

        this.getUser().data.getStatistics().increase(StatisticType.ELITE_DUELS_COMPLETED);

        DuelHelper.delete(this.players[0].ID);
    }

    private void setEliteTrainer()
    {
        Random r = new Random();

        //Create team
        List<PokemonEntity> pool = Arrays.stream(PokemonEntity.values())
                .filter(e -> PokemonRarity.isLegendary(e) || PokemonRarity.isMythical(e) || PokemonRarity.isUltraBeast(e) || MegaEvolutionRegistry.isMega(e)).toList();

        PokemonEntity[] team = new PokemonEntity[6];
        for(int i = 0; i < 6; i++) team[i] = pool.get(r.nextInt(pool.size()));

        List<ZCrystal> poolZCrystal = new ArrayList<>();
        Arrays.stream(team).forEach(e -> {
            ZCrystal crystal = ZCrystal.getRandomUniqueZCrystalFor(e);
            if(crystal != null) poolZCrystal.add(crystal);
        });

        //Set Z-Crystal
        ZCrystal z;
        if(poolZCrystal.isEmpty()) z = ZCrystal.values()[r.nextInt(18)];
        else z = poolZCrystal.get(r.nextInt(poolZCrystal.size()));

        //Create data
        TrainerData elite = new TrainerData("Elite Trainer", -1, List.of(team), z, 100, 1.25F);

        this.players[1] = new TrainerPlayer(elite);

        for(int i = 0; i < this.players[1].team.size(); i++)
        {
            Pokemon p = this.players[1].team.get(i);

            //Max EVs and IVs
            Stat max1 = Arrays.stream(Stat.values()).max(Comparator.comparingInt(s -> p.getData().getBaseStats().get(s))).orElse(Stat.values()[r.nextInt(Stat.values().length)]);
            Stat max2 = Arrays.stream(Stat.values()).filter(s -> s != max1).max(Comparator.comparingInt(s -> p.getData().getBaseStats().get(s))).orElse(Stat.values()[r.nextInt(Stat.values().length)]);

            Arrays.stream(Stat.values()).forEach(s -> {
                p.setIV(s, 31);
                p.setEV(s, s == max1 || s == max2 ? 252 : 3);
            });

            //Max prestige
            p.setPrestigeLevel(p.getMaxPrestigeLevel());

            //Special items given to requisite pokemon, otherwise every other pokemon gets a berry or Arceus plate item
            List<Item> itemPool;
            if(p.is(PokemonEntity.GENESECT)) p.setItem((itemPool = List.of(Item.BURN_DRIVE, Item.CHILL_DRIVE, Item.DOUSE_DRIVE, Item.SHOCK_DRIVE)).get(r.nextInt(itemPool.size())));
            else if(p.is(PokemonEntity.SILVALLY)) p.setItem((itemPool = Arrays.stream(Item.values()).filter(Item::isMemoryItem).toList()).get(r.nextInt(itemPool.size())));
            else if(p.is(PokemonEntity.ARCEUS)) p.setItem((itemPool = Arrays.stream(Item.values()).filter(Item::isPlateItem).toList()).get(r.nextInt(itemPool.size())));
            else if(r.nextBoolean()) p.setItem((itemPool = Arrays.stream(Item.values()).filter(Item::isBerry).toList()).get(r.nextInt(itemPool.size())));
            else p.setItem((itemPool = Arrays.stream(Item.values()).filter(Item::isPlateItem).toList()).get(r.nextInt(itemPool.size())));

            //Set health to max, also increased shiny odds
            p.setHealth(p.getStat(Stat.HP));
            p.setShiny(r.nextInt(100) < 5);
        }

        this.players[1].active = this.players[1].team.get(0);
    }
}
