package com.calculusmaster.pokecord.mongo;

import com.calculusmaster.pokecord.Pokecord;
import com.calculusmaster.pokecord.commands.pokemon.CommandTeam;
import com.calculusmaster.pokecord.game.bounties.Bounty;
import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.duel.trainer.TrainerData;
import com.calculusmaster.pokecord.game.duel.trainer.TrainerManager;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.game.player.level.MasteryLevelManager;
import com.calculusmaster.pokecord.game.player.level.PMLExperience;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.PokemonEgg;
import com.calculusmaster.pokecord.game.pokemon.augments.PokemonAugment;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.cache.PlayerDataCache;
import com.calculusmaster.pokecord.util.cache.PokemonDataCache;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.calculusmaster.pokecord.util.helpers.ThreadPoolHandler;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PlayerDataQuery extends MongoQuery
{
    private Optional<PlayerSettingsQuery> settings = Optional.empty();
    private Optional<PlayerStatisticsQuery> statistics = Optional.empty();

    public PlayerDataQuery(String playerID)
    {
        super("playerID", playerID, Mongo.PlayerData);
    }

    //Cache
    public static PlayerDataQuery of(String playerID)
    {
        if(PlayerDataCache.CACHE.containsKey(playerID)) return PlayerDataCache.CACHE.get(playerID).data();
        else
        {
            try { LoggerHelper.info(PlayerDataQuery.class, "Cache does not contain " + playerID + "! Searching for: " + Objects.requireNonNull(Mongo.PlayerData.find(Filters.eq("playerID", playerID)).first())); }
            catch(NullPointerException e) { return null; }

            PlayerDataCache.addCache(playerID);
            return PlayerDataQuery.of(playerID);
        }
    }

    @NotNull
    public static PlayerDataQuery ofNonNull(String playerID)
    {
        return Objects.requireNonNull(PlayerDataQuery.of(playerID));
    }

    //Registered

    public static boolean isRegistered(String id)
    {
        return PlayerDataCache.CACHE.containsKey(id);
    }

    public static void register(User player)
    {
        Document data = new Document()
                .append("playerID", player.getId())
                .append("username", player.getName())
                .append("level", 0)
                .append("exp", 0)
                .append("credits", 100)
                .append("redeems", 0)
                .append("selected", 1)
                .append("pokemon", new ArrayList<>())
                .append("team", new ArrayList<>())
                .append("favorites", new ArrayList<>())
                .append("items", new ArrayList<>())
                .append("tms", new ArrayList<>())
                .append("trs", new ArrayList<>())
                .append("zcrystals", new ArrayList<>())
                .append("active_zcrystal", "")
                .append("achievements", new ArrayList<>())
                .append("owned_forms", new ArrayList<>())
                .append("owned_megas", new ArrayList<>())
                .append("bounties", new ArrayList<>())
                .append("owned_eggs", new ArrayList<>())
                .append("active_egg", "")
                .append("owned_augments", new ArrayList<>())
                .append("defeated_trainers", new ArrayList<>())
                .append("saved_teams", IntStream.range(0, CommandTeam.MAX_TEAM_SIZE).mapToObj(i -> new Document("name", "").append("team", new ArrayList<>())).toList());

        LoggerHelper.logDatabaseInsert(PlayerDataQuery.class, data);

        Mongo.PlayerData.insertOne(data);

        PlayerSettingsQuery.register(player.getId());
        PlayerStatisticsQuery.register(player.getId());

        PlayerDataCache.addCache(player.getId());
    }

    public void directMessage(String msg)
    {
        try
        {
            Pokecord.BOT_JDA.openPrivateChannelById(this.getID()).flatMap(channel -> channel.sendMessage(msg)).queue();
        }
        catch (Exception e)
        {
            LoggerHelper.reportError(PlayerDataQuery.class, "Failed to DM " + this.getUsername() + " (ID: " + this.getID() + ")!", e);
        }
    }

    public void directMessage(MessageEmbed embed)
    {
        try
        {
            Pokecord.BOT_JDA.openPrivateChannelById(this.getID()).flatMap(channel -> channel.sendMessageEmbeds(embed)).queue();
        }
        catch (Exception e)
        {
            LoggerHelper.reportError(PlayerDataQuery.class, "Failed to DM " + this.getUsername() + " (ID: " + this.getID() + ")!", e);
        }
    }

    public void dmMasteryLevel()
    {
        this.directMessage(MasteryLevelManager.MASTERY_LEVELS.get(this.getLevel()).getEmbed().build());
    }

    //Get the SettingsHelper object
    public PlayerSettingsQuery getSettings()
    {
        if(this.settings.isEmpty()) this.settings = Optional.of(new PlayerSettingsQuery(this.getID()));
        return this.settings.get();
    }

    //Get the PlayerStatisticsQuery object
    public PlayerStatisticsQuery getStatistics()
    {
        if(this.statistics.isEmpty()) this.statistics = Optional.of(new PlayerStatisticsQuery(this.getID()));
        return this.statistics.get();
    }

    //key: "playerID"
    public String getID()
    {
        return this.document.getString("playerID");
    }

    public String getMention()
    {
        return "<@" + this.getID() + ">";
    }

    //key: "username"
    public String getUsername()
    {
        return this.document.getString("username");
    }

    //key: "level"
    public int getLevel()
    {
        return this.document.getInteger("level");
    }

    public void increaseLevel()
    {
        this.update(Updates.inc("level", 1));

        this.clearExp();
    }

    //key: "exp"
    public int getExp()
    {
        return this.document.getInteger("exp");
    }

    public void clearExp()
    {
        this.update(Updates.set("exp", 0));
    }

    public void addExp(PMLExperience experienceEnum, int chance)
    {
        int amount = experienceEnum.experience;

        if(new SplittableRandom().nextInt(100) < chance)
        {
            this.update(Updates.inc("exp", amount));

            this.getStatistics().incr(PlayerStatistic.MASTERY_EXP_EARNED, amount);

            if(!MasteryLevelManager.isMax(this) && MasteryLevelManager.MASTERY_LEVELS.get(this.getLevel() + 1).canLevelUp(this))
            {
                this.increaseLevel();

                //Starting Augments
                if(MasteryLevelManager.MASTERY_LEVELS.get(this.getLevel()).getFeatures().contains(Feature.AUGMENT_POKEMON))
                {
                    EnumSet.of(PokemonAugment.HP_BOOST, PokemonAugment.ATK_BOOST, PokemonAugment.DEF_BOOST, PokemonAugment.SPATK_BOOST, PokemonAugment.SPDEF_BOOST, PokemonAugment.SPD_BOOST)
                            .forEach(p -> this.addAugment(p.getAugmentID()));
                }

                if(this.getLevel() == 20) Achievements.grant(this.getID(), Achievements.REACH_MASTERY_LEVEL_20, null);

                this.directMessage("You are now **Pokemon Mastery Level " + this.getLevel() + "**! You've unlocked the following features:\n" + MasteryLevelManager.MASTERY_LEVELS.get(this.getLevel()).getUnlockedFeaturesOverview());
                this.dmMasteryLevel();
            }
        }
    }

    //key: "credits"
    public int getCredits()
    {
        return this.document.getInteger("credits");
    }

    public void changeCredits(int amount)
    {
        this.update(Updates.set("credits", this.getCredits() + amount));

        if(amount < 0) this.getStatistics().incr(PlayerStatistic.CREDITS_SPENT, Math.abs(amount));
        else if(amount > 0) this.getStatistics().incr(PlayerStatistic.CREDITS_EARNED, Math.abs(amount));
    }

    //key: "redeems"
    public int getRedeems()
    {
        return this.document.getInteger("redeems");
    }

    public void changeRedeems(int amount)
    {
        this.update(Updates.set("redeems", this.getRedeems() + amount));
    }

    //key: "selected"
    public int getSelected()
    {
        int selected = this.document.getInteger("selected");
        int pokemonListSize = this.getPokemonList().size();

        if(selected > pokemonListSize) this.setSelected(pokemonListSize);
        else if(selected <= 0) this.setSelected(1);

        return this.document.getInteger("selected") - 1;
    }

    public void setSelected(int num)
    {
        this.update(Updates.set("selected", num));
    }

    public Pokemon getSelectedPokemon()
    {
        return Pokemon.build(this.getPokemonList().get(this.getSelected()));
    }

    //key: "pokemon"
    public List<String> getPokemonList()
    {
        return this.document.getList("pokemon", String.class);
    }

    public List<Pokemon> getPokemon()
    {
        List<String> in = this.getPokemonList();
        List<Pokemon> out = new ArrayList<>();
        for(int i = 0; i < in.size(); i++) out.add(Pokemon.build(in.get(i), i + 1));
        return out;
    }

    public void addPokemon(String UUID)
    {
        this.update(Updates.push("pokemon", UUID));

    }

    public void removePokemon(String UUID)
    {
        this.update(Updates.pull("pokemon", UUID));
        PokemonDataCache.removeCache(UUID);

        if(this.getTeam().contains(UUID)) this.removePokemonFromTeam(this.getTeam().indexOf(UUID));
        if(this.getFavorites().contains(UUID)) this.removePokemonFromFavorites(UUID);
    }

    public void removePokemon(int index)
    {
        removePokemon(this.getPokemonList().get(index - 1));
    }

    //key: "team"
    public List<String> getTeam()
    {
        return this.document.getList("team", String.class);
    }

    public void setTeam(List<String> team)
    {
        this.update(Updates.set("team", team));
    }

    public List<Pokemon> getTeamPokemon()
    {
        return this.getTeam().stream().map(s -> Pokemon.build(s, this.getPokemonList().indexOf(s) + 1)).toList();
    }

    public void clearTeam()
    {
        this.setTeam(new ArrayList<>());
    }

    public void addPokemonToTeam(String UUID, int index)
    {
        List<String> team = new ArrayList<>(this.getTeam());

        index--;

        if(index >= team.size()) team.add(UUID);
        else team.set(index, UUID);

        this.setTeam(team);
    }

    public void removePokemonFromTeam(int index)
    {
        List<String> team = new ArrayList<>(this.getTeam());

        index--;

        team.remove(index);

        this.setTeam(team);
    }

    public void swapPokemonInTeam(int from, int to)
    {
        List<String> team = new ArrayList<>(this.getTeam());

        from--;
        to--;

        String temp = team.get(from);
        team.set(from, team.get(to));
        team.set(to, temp);

        this.setTeam(team);
    }

    //key: "favorites"
    public List<String> getFavorites()
    {
        return this.document.getList("favorites", String.class);
    }

    public void addPokemonToFavorites(String UUID)
    {
        this.update(Updates.push("favorites", UUID));
    }

    public void removePokemonFromFavorites(String UUID)
    {
        this.update(Updates.pull("favorites", UUID));
    }

    public void clearFavorites()
    {
        this.update(Updates.set("favorites", new ArrayList<>()));
    }


    //key: "items"
    public List<String> getItemList()
    {
        return this.document.getList("items", String.class);
    }

    public void addItem(String item)
    {
        this.update(Updates.push("items", item));
    }

    public void removeItem(String item)
    {
        int counts = 0;
        for(String s : this.getItemList()) if(s.equalsIgnoreCase(item)) counts++;

        this.update(Updates.pull("items", item));

        for(int i = 0; i < counts - 1; i++) this.addItem(item);
    }

    //key: "tms"
    public List<String> getTMList()
    {
        return this.document.getList("tms", String.class);
    }

    public void addTM(String TM)
    {
        this.update(Updates.push("tms", TM));
    }

    public void removeTM(String TM)
    {
        int counts = 0;
        for (int i = 0; i < this.getTMList().size(); i++) if(this.getTMList().get(i).equals(TM)) counts++;

        this.update(Updates.pull("tms", TM));

        for(int i = 0; i < counts - 1; i++) this.addTM(TM);
    }

    //key: "trs"
    public List<String> getTRList()
    {
        return this.document.getList("trs", String.class);
    }

    public void addTR(String TR)
    {
        this.update(Updates.push("trs", TR));
    }

    //Removes a TR from the player's owned TRs
    public void removeTR(String TR)
    {
        int counts = 0;
        for (int i = 0; i < this.getTRList().size(); i++) if(this.getTRList().get(i).equals(TR)) counts++;

        this.update(Updates.pull("trs", TR));

        for(int i = 0; i < counts - 1; i++) this.addTR(TR);
    }

    //key: "zcrystals"
    public List<String> getZCrystalList()
    {
        return this.document.getList("zcrystals", String.class);
    }

    public boolean hasZCrystal(String z)
    {
        return this.getZCrystalList().contains(z);
    }

    public void addZCrystal(String z)
    {
        this.update(Updates.push("zcrystals", z));
    }

    //key: "active_zcrystal"
    public String getEquippedZCrystal()
    {
        return this.document.getString("active_zcrystal");
    }

    public void equipZCrystal(String z)
    {
        this.update(Updates.set("active_zcrystal", z));
    }

    //key: "achievements"
    public List<String> getAchievementsList()
    {
        return this.document.getList("achievements", String.class);
    }

    public void addAchievement(Achievements a)
    {
        this.update(Updates.push("achievements", a.toString()));
    }

    public void grantAchievement(Achievements a, MessageReceivedEvent event)
    {
        Achievements.grant(this.getID(), a, event);
    }

    //key: "owned_forms"
    public List<String> getOwnedForms()
    {
        return this.document.getList("owned_forms", String.class);
    }

    public void addOwnedForm(String form)
    {
        this.update(Updates.push("owned_forms", form));
    }

    //key: "owned_megas"
    public List<String> getOwnedMegas()
    {
        return this.document.getList("owned_megas", String.class);
    }

    public void addOwnedMegas(String mega)
    {
        this.update(Updates.push("owned_megas", mega));
    }

    //key: "bounties"
    public List<String> getBountyIDs()
    {
        return this.document.getList("bounties", String.class);
    }

    public List<Bounty> getBounties()
    {
        return this.getBountyIDs().stream().map(Bounty::fromDB).collect(Collectors.toList());
    }

    private void updateBounty(Consumer<Bounty> checker)
    {
        if(this.document == null) this.update(null);

        //Basic Bounties
        for(String ID : this.getBountyIDs())
        {
            Bounty b = Bounty.fromDB(ID);

            if(!b.getObjective().isComplete())
            {
                checker.accept(b);

                b.updateProgression();

                if(b.getObjective().isComplete()) this.directMessage("You have unclaimed bounties!");
            }
        }
    }

    public void updateBountyProgression(final Consumer<Bounty> checker)
    {
        ThreadPoolHandler.BOUNTY.execute(() -> this.updateBounty(checker));
    }

    public void updateBountyProgression(ObjectiveType type, Consumer<Bounty> checker)
    {
        this.updateBountyProgression((b) -> {
            if(b.getType().equals(type)) checker.accept(b);
        });
    }

    public void updateBountyProgression(ObjectiveType type)
    {
        this.updateBountyProgression(type, Bounty::update);
    }

    public void updateBountyProgression(ObjectiveType type, int amount)
    {
        this.updateBountyProgression(type, b -> b.update(amount));
    }

    public void addBounty(String ID)
    {
        this.update(Updates.push("bounties", ID));
    }

    public void removeBounty(String ID)
    {
        this.update(Updates.pull("bounties", ID));
    }

    //key: "owned_eggs"
    public List<String> getOwnedEggIDs()
    {
        return this.document.getList("owned_eggs", String.class);
    }

    public List<PokemonEgg> getOwnedEggs()
    {
        return this.getOwnedEggIDs().stream().map(PokemonEgg::fromDB).collect(Collectors.toList());
    }

    public boolean hasEggs()
    {
        return !this.getOwnedEggIDs().isEmpty();
    }

    public void addEgg(String eggID)
    {
        this.update(Updates.push("owned_eggs", eggID));
    }

    public void removeEgg(String eggID)
    {
        this.update(Updates.pull("owned_eggs", eggID));
    }

    //key: "active_egg"
    public String getActiveEggID()
    {
        return this.document.getString("active_egg");
    }

    public PokemonEgg getActiveEgg()
    {
        return PokemonEgg.fromDB(this.getActiveEggID());
    }

    public boolean hasActiveEgg()
    {
        return !this.document.getString("active_egg").equals("");
    }

    public void setActiveEgg(String eggID)
    {
        this.update(Updates.set("active_egg", eggID));
    }

    public void removeActiveEgg()
    {
        this.update(Updates.set("active_egg", ""));
    }

    //key: "owned_augments"
    public List<String> getOwnedAugmentIDs()
    {
        return this.document.getList("owned_augments", String.class);
    }

    public List<PokemonAugment> getOwnedAugments()
    {
        return this.getOwnedAugmentIDs().stream().map(PokemonAugment::fromID).collect(Collectors.toList());
    }

    public boolean isAugmentUnlocked(String augmentID)
    {
        return this.getOwnedAugmentIDs().contains(augmentID);
    }

    public void addAugment(String augmentID)
    {
        this.update(Updates.push("owned_augments", augmentID));
    }

    //key: "defeated_trainers"
    public List<String> getDefeatedTrainers()
    {
        return this.document.getList("defeated_trainers", String.class);
    }

    public boolean hasDefeatedTrainer(String trainerID)
    {
        return this.getDefeatedTrainers().contains(trainerID);
    }

    public void addDefeatedTrainer(String trainerID)
    {
        this.update(Updates.push("defeated_trainers", trainerID));
    }

    public boolean hasDefeatedAllTrainersOfClass(int clazz)
    {
        return TrainerManager.getTrainersOfClass(clazz).stream().map(TrainerData::getTrainerID).allMatch(s -> this.getDefeatedTrainers().contains(s));
    }

    public boolean hasDefeatedAllTrainerClasses()
    {
        return TrainerManager.REGULAR_TRAINERS.stream().map(TrainerData::getTrainerClass).distinct().allMatch(this::hasDefeatedAllTrainersOfClass);
    }

    //key: "saved_teams"
    public List<Document> getSavedTeams()
    {
        return this.document.getList("saved_teams", Document.class);
    }

    public void setSavedTeam(int slot, List<String> team)
    {
        this.update(Updates.set("saved_teams." + slot + ".team", team));
    }

    public List<String> getSavedTeam(int slot)
    {
        return this.getSavedTeams().get(slot).getList("team", String.class);
    }

    public String getSavedTeamName(int slot)
    {
        return this.getSavedTeams().get(slot).getString("name");
    }

    public void renameSavedTeam(int slot, String name)
    {
        this.update(Updates.set("saved_teams." + slot + ".name", name));
    }
}
