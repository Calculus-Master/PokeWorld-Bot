package com.calculusmaster.pokecord.game.pokemon;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.enums.elements.*;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.game.objectives.ObjectiveType;
import com.calculusmaster.pokecord.game.pokemon.augments.PokemonAugment;
import com.calculusmaster.pokecord.game.pokemon.augments.PokemonAugmentRegistry;
import com.calculusmaster.pokecord.game.pokemon.component.*;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonData;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity;
import com.calculusmaster.pokecord.game.pokemon.evolution.EvolutionData;
import com.calculusmaster.pokecord.game.pokemon.evolution.GigantamaxRegistry;
import com.calculusmaster.pokecord.game.pokemon.evolution.MegaChargeManager;
import com.calculusmaster.pokecord.game.pokemon.evolution.MegaEvolutionRegistry;
import com.calculusmaster.pokecord.mongo.Mongo;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.cacheold.PlayerDataCache;
import com.calculusmaster.pokecord.util.cacheold.PokemonDataCache;
import com.calculusmaster.pokecord.util.enums.StatisticType;
import com.calculusmaster.pokecord.util.helpers.IDHelper;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Month;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Pokemon
{
    //Fields
    private PokemonData data;

    private String UUID;
    private PokemonEntity entity;
    private String nickname;
    private int number;
    private boolean shiny;
    private Gender gender;
    private Nature nature;
    private int level;
    private int exp;
    private int dynamaxLevel;
    private int prestigeLevel;
    private PokemonStats ivs;
    private PokemonStats evs;
    private List<MoveEntity> moves;
    private Ability ability;
    private Item item;
    private TM tm;
    private EnumSet<PokemonAugment> augments;
    private int megaCharges;
    private CustomPokemonData customData;

    //Duel Fields
    private List<Type> type;
    private PokemonDuelStatChanges statChanges;
    private PokemonBoosts boosts;
    private int health;
    private EnumSet<StatusCondition> statusConditions;
    private boolean isDynamaxed;
    private PokemonDuelStatOverrides statOverrides;
    private Item consumedItem;
    private boolean abilitiesIgnored;
    private boolean statChangesIgnored;

    //Misc
    private final Random random;

    //Private Constructor
    private Pokemon() {}

    {
        this.random = new Random();
    }

    //Factory Creator and Builder
    public static Pokemon create(PokemonEntity entity)
    {
        Pokemon p = new Pokemon();

        p.setEntity(entity);
        p.setData(entity);
        p.setUUID();
        p.setNickname("");
        p.setNumber(-1);
        p.setShiny();
        p.setGender();
        p.setNature();
        p.setLevel(1);
        p.setDynamaxLevel(0);
        p.setPrestigeLevel(0);
        p.setExp(0);
        p.setIVs();
        p.setEVs();
        p.setMoves();
        p.setAbility();
        p.setItem(Item.NONE);
        p.setTM();
        p.setAugments(List.of());
        p.setDefaultMegaCharges();
        p.setCustomData();

        p.setupMisc();
        p.setDuelDefaults();

        return p;
    }

    private static Pokemon build(Document data, int number)
    {
        Pokemon p = new Pokemon();

        p.setEntity(data.getString("entity"));
        p.setData(PokemonEntity.valueOf(data.getString("entity")));
        p.setUUID(data.getString("UUID"));
        p.setNickname(data.getString("nickname"));
        p.setNumber(number);
        p.setShiny(data.getBoolean("shiny"));
        p.setGender(Gender.valueOf(data.getString("gender")));
        p.setNature(Nature.valueOf(data.getString("nature")));
        p.setLevel(data.getInteger("level"));
        p.setDynamaxLevel(data.getInteger("dynamaxLevel"));
        p.setPrestigeLevel(data.getInteger("prestigeLevel"));
        p.setExp(data.getInteger("exp"));
        p.setIVs(data.get("ivs", Document.class));
        p.setEVs(data.get("evs", Document.class));
        p.setMoves(data.getList("moves", String.class).stream().map(MoveEntity::valueOf).toList());
        p.setAbility(Ability.valueOf(data.getString("ability")));
        p.setItem(Item.valueOf(data.getString("item")));
        p.setTM(data.getString("tm").isEmpty() ? null : TM.valueOf(data.getString("tm")));
        p.setAugments(data.getList("augments", String.class));
        p.setMegaCharges(data.getInteger("megacharges"));
        p.setCustomData(data.get("custom", Document.class));

        p.setupMisc();
        p.setDuelDefaults();

        return p;
    }

    private void setDuelDefaults()
    {
        this.type = new ArrayList<>(List.copyOf(this.data.getTypes()));
        this.statChanges = new PokemonDuelStatChanges();
        this.boosts = new PokemonBoosts();
        this.health = this.getMaxHealth();
        this.statusConditions = EnumSet.noneOf(StatusCondition.class);
        this.isDynamaxed = false;
        this.statOverrides = new PokemonDuelStatOverrides();
        this.consumedItem = Item.NONE;
        this.abilitiesIgnored = false;
        this.statChangesIgnored = false;
    }

    private void setupMisc()
    {

    }

    public static Pokemon build(String UUID)
    {
        return Pokemon.build(UUID, -1);
    }

    public static Pokemon build(String UUID, int number)
    {
        Document cache = PokemonDataCache.getCache(UUID);

        //This usually means that the data wasn't cached on bot initialization
        if(cache == null)
        {
            cache = Mongo.PokemonData.find(Filters.eq("UUID", UUID)).first();
            PokemonDataCache.addCacheData(UUID, cache);

            //This most likely means the UUID isn't in the database
            if(cache == null) LoggerHelper.error(com.calculusmaster.pokecord.game.pokemon.Pokemon.class, "UUID {%s} not found in Database!".formatted(UUID));
        }

        return cache == null ? null : Pokemon.build(PokemonDataCache.getCache(UUID), number);
    }

    //Querying for the Player Owner of this Pokemon; if no owner (AI Pokemon), returns null
    public PlayerDataQuery getOwner()
    {
        String poke = "{Name: %s, UUID: %s}".formatted(this.data.getName(), this.UUID);
        LoggerHelper.info(Pokemon.class, "Searching for Player Owner of " + poke);

        PlayerDataQuery out =  PlayerDataCache.CACHE.values().stream().map(PlayerDataCache::data).filter(data -> data.getPokemonList().contains(this.getUUID())).findFirst().orElse(null);

        if(out == null) LoggerHelper.warn(Pokemon.class, "Unable to find Player Owner of " + poke);

        return out;
    }

    //Database
    private Document buildDatabaseDocument()
    {
        return new Document()
                .append("UUID", this.UUID)
                .append("entity", this.entity.toString())
                .append("nickname", this.nickname)
                .append("shiny", this.shiny)
                .append("gender", this.gender.toString())
                .append("nature", this.nature.toString())
                .append("level", this.level)
                .append("exp", this.exp)
                .append("dynamaxLevel", this.dynamaxLevel)
                .append("prestigeLevel", this.prestigeLevel)
                .append("ivs", this.ivs.serialized())
                .append("evs", this.evs.serialized())
                .append("moves", this.moves.stream().map(Enum::toString).toList())
                .append("ability", this.ability.toString())
                .append("item", this.item.toString())
                .append("tm", this.tm == null ? "" : this.tm.toString())
                .append("augments", this.augments.stream().map(Enum::toString).toList())
                .append("megacharges", this.megaCharges)
                .append("custom", this.customData.serialize());
    }

    public void upload()
    {
        Document data = this.buildDatabaseDocument();

        LoggerHelper.logDatabaseInsert(Pokemon.class, data);

        Mongo.PokemonData.insertOne(data);

        PokemonDataCache.addCacheData(this.getUUID(), data);
    }

    public void delete()
    {
        Mongo.PokemonData.deleteOne(this.query());
    }

    public void completeUpdate()
    {
        Mongo.PokemonData.replaceOne(this.query(), this.buildDatabaseDocument());

        PokemonDataCache.updateCache(this.getUUID());
    }

    private void update(Bson... updates)
    {
        LoggerHelper.logDatabaseUpdate(Pokemon.class, updates);

        Mongo.PokemonData.updateOne(this.query(), List.of(updates));

        PokemonDataCache.updateCache(this.getUUID());
    }

    public void updateEntity()
    {
        this.update(Updates.set("entity", this.entity.toString()));
    }

    public void updateNickname()
    {
        this.update(Updates.set("nickname", this.nickname));
    }

    public void updateNature()
    {
        this.update(Updates.set("nature", this.nature));
    }

    public void updateExperience()
    {
        this.update(Updates.set("level", this.level), Updates.set("exp", this.exp));
    }

    public void updateDynamaxLevel()
    {
        this.update(Updates.set("dynamaxLevel", this.dynamaxLevel));
    }

    public void updatePrestigeLevel()
    {
        this.update(Updates.set("prestigeLevel", this.prestigeLevel));
    }

    public void updateEVs()
    {
        this.update(Updates.set("evs", this.evs.serialized()));
    }

    public void updateIVs()
    {
        this.update(Updates.set("ivs", this.ivs.serialized()));
    }

    public void updateMoves()
    {
        this.update(Updates.set("moves", this.moves.stream().map(Enum::toString).toList()));
    }

    public void updateAbility()
    {
        this.update(Updates.set("ability", this.ability.toString()));
    }

    public void updateItem()
    {
        this.update(Updates.set("item", this.item.toString()));
    }

    public void updateTM()
    {
        this.update(Updates.set("tm", this.tm == null ? "" : this.tm.toString()));
    }

    public void updateAugments()
    {
        this.update(Updates.set("augments", this.augments));
    }

    public void resetAugments()
    {
        if(this.augments.isEmpty()) return;

        this.clearAugments();
        this.updateAugments();
    }

    public void updateMegaCharges()
    {
        this.update(Updates.set("megacharges", this.megaCharges));
    }

    //Mastery
    public boolean isMastered()
    {
        return this.getLevel() == 100 //Level must be 100
                && this.getTotalEV() == 510 //EVs must be maxed out
                && this.moves.stream().noneMatch(m -> m.equals(MoveEntity.TACKLE)) //Must have 4 moves that aren't Tackle
                && this.getDynamaxLevel() == 10 //Dynamax Level must be 10
                && this.getPrestigeLevel() == this.getMaxPrestigeLevel(); //Prestige Level must be at its maximum
    }

    //Custom Data
    public CustomPokemonData getCustomData()
    {
        return this.customData;
    }

    public void setCustomData()
    {
        this.customData = new CustomPokemonData();
    }

    public void setCustomData(Document data)
    {
        this.customData = new CustomPokemonData(data);
    }

    public void setCustomData(CustomPokemonData data)
    {
        this.customData = data;
    }

    //Entity
    public PokemonEntity getEntity()
    {
        return this.entity;
    }

    public void setEntity(PokemonEntity entity)
    {
        this.entity = entity;
    }

    public void setEntity(String entity)
    {
        this.entity = Objects.requireNonNull(PokemonEntity.valueOf(entity));
    }

    public boolean is(PokemonEntity... entity)
    {
        return List.of(entity).contains(this.entity);
    }

    //Other

    public void addStatusCondition(StatusCondition s)
    {
        List<Predicate<Pokemon>> immunities = List.of(
                p -> p.isType(Type.ELECTRIC) && s.equals(StatusCondition.PARALYZED),
                p -> p.isType(Type.ICE) && s.equals(StatusCondition.FROZEN),
                p -> p.isType(Type.FIRE) && s.equals(StatusCondition.BURNED),
                p -> (p.isType(Type.POISON) || p.isType(Type.STEEL)) && s.equals(StatusCondition.POISONED)
        );

        if(immunities.stream().noneMatch(p -> p.test(this))) this.statusConditions.add(s);
    }

    public void removeStatusCondition(StatusCondition s)
    {
        this.statusConditions.remove(s);
    }

    public boolean hasStatusCondition(StatusCondition s)
    {
        return this.statusConditions.contains(s);
    }

    public boolean hasAnyStatusCondition()
    {
        return !this.statusConditions.isEmpty();
    }

    public void clearStatusConditions()
    {
        this.statusConditions.clear();
    }

    public void clearStatusConditions(StatusCondition... conditions)
    {
        Arrays.stream(conditions).forEach(this::removeStatusCondition);
    }

    public EnumSet<StatusCondition> getStatusConditions()
    {
        return this.statusConditions;
    }

    public String getActiveStatusConditions()
    {
        List<String> active = new ArrayList<>();
        for(StatusCondition s : this.statusConditions) active.add(s.getAbbrev());

        if(active.isEmpty()) return "";

        StringBuilder s = new StringBuilder().append("(");
        for (String str : active) s.append(str).append(", ");
        s.deleteCharAt(s.length() - 1).deleteCharAt(s.length() - 1).append(")");

        return s.toString();
    }

    public PokemonDuelStatOverrides overrides()
    {
        return this.statOverrides;
    }

    public PokemonDuelStatChanges changes()
    {
        return this.statChanges;
    }

    public PokemonBoosts getBoosts()
    {
        return this.boosts;
    }

    public int getBaseStat(Stat s)
    {
        return this.data.getBaseStats().get(s);
    }

    public int getStat(Stat s)
    {
        if(this.statOverrides != null && this.statOverrides.has(s)) return this.statOverrides.get(s);

        double masteredBoost = this.isMastered() ? 1.05 : 1.0;
        double prestigeBoost = this.getPrestigeBonus(s);
        double generalAugmentBoost = this.getAugmentStatBoost(s);

        double commonBoosts = masteredBoost * prestigeBoost * generalAugmentBoost;

        //Augment: Pinnacle Evasion
        if(this.hasAugment(PokemonAugment.PINNACLE_EVASION) && s.equals(Stat.SPD)) commonBoosts *= 0.9;

        //Augment: Electrified Hyper Speed
        if(this.hasAugment(PokemonAugment.ELECTRIFIED_HYPER_SPEED) && s.equals(Stat.SPD)) commonBoosts *= 2.0;

        //Augment: Aerial Evasion
        if(this.hasAugment(PokemonAugment.AERIAL_EVASION) && s.equals(Stat.SPD)) commonBoosts *= 0.9;

        //Augment: Draconic Enrage
        if(this.hasAugment(PokemonAugment.DRACONIC_ENRAGE))
        {
            if(List.of(Stat.ATK, Stat.SPATK, Stat.SPD).contains(s)) commonBoosts *= 1.15;
            else if(List.of(Stat.DEF, Stat.SPDEF).contains(s)) commonBoosts *= 0.85;
        }

        //Ability: Swift Swim
        if(this.hasAbility(Ability.SWIFT_SWIM))
        {
            Duel d = DuelHelper.findDuel(this);
            if(List.of(Weather.RAIN, Weather.HEAVY_RAIN).contains(d.weather.get())) commonBoosts *= 2.0;
        }

        if(s.equals(Stat.HP))
        {
            //HP = Level + 10 + [((2 * Base + IV + EV / 4) * Level) / 100]
            double base = this.getBaseStat(Stat.HP);
            int IV = this.ivs.get(Stat.HP);
            int EV = this.evs.get(Stat.HP);
            double maxHP = this.level + 10 + ((this.level * (2 * base + IV + EV / 4.0)) / 100);

            double dynamaxBoost = this.isDynamaxed ? 1.0 + (this.getDynamaxLevel() * 0.05 + 0.5) : 1.0;
            double rawBoost = this.boosts.getHealthBoost();

            return (int)(maxHP * dynamaxBoost * commonBoosts * rawBoost);
        }
        else
        {
            //Stat = Nature * [5 + ((2 * Base + IV + EV / 4) * Level) / 100]
            double nature = this.nature.getMap().get(s);
            double base = this.getBaseStat(s);
            int IV = this.ivs.get(s);
            int EV = this.evs.get(s);
            double stat = nature * (5 + ((this.level * (2 * base + IV + EV / 4.0)) / 100));

            double modifier = this.statChangesIgnored ? 1.0 : this.statChanges.getModifier(s);
            double rawBoost = this.boosts.getStatBoost();

            double paralysisModifier = s.equals(Stat.SPD) && this.hasStatusCondition(StatusCondition.PARALYZED) ? 0.5 : 1.0;

            return (int)(stat * modifier * commonBoosts * rawBoost * paralysisModifier);
        }
    }

    public int getMaxHealth()
    {
        return this.getStat(Stat.HP);
    }

    public int getMaxHealth(double fraction)
    {
        return (int)(this.getMaxHealth() * fraction);
    }

    public int getTotalStat()
    {
        return Arrays.stream(Stat.values()).mapToInt(this::getStat).sum();
    }

    public void heal(int amount)
    {
        if(!this.isFainted()) this.setHealth(Math.min(this.health + amount, this.getMaxHealth()));
    }

    public void damage(int amount)
    {
        this.setHealth(Math.max(0, this.health - amount));
    }

    public boolean isFainted()
    {
        return this.health <= 0;
    }

    public void setHealth(int health)
    {
        this.health = health;
    }

    public int getHealth()
    {
        return this.health;
    }

    public boolean isDynamaxed()
    {
        return this.isDynamaxed;
    }

    public void enterDynamax()
    {
        double healthRatio = (double)this.getHealth() / this.getStat(Stat.HP);

        this.isDynamaxed = true;

        this.setHealth((int)(healthRatio * this.getStat(Stat.HP)));
    }

    public void exitDynamax()
    {
        double healthRatio = (double)this.getHealth() / this.getStat(Stat.HP);

        this.isDynamaxed = false;

        this.setHealth((int)(healthRatio * this.getStat(Stat.HP)));
    }

    public double getWeight()
    {
        double modifiers = 1.0;

        if(this.hasAbility(Ability.HEAVY_METAL)) modifiers *= 2.0;

        return this.data.getWeight() * modifiers;
    }

    public int getMegaCharges()
    {
        return this.megaCharges;
    }

    public int getMaxMegaCharges()
    {
        if(MegaEvolutionRegistry.isMegaLegendary(this.entity)) return 3;
        else if(MegaEvolutionRegistry.isMega(this.entity)) return 5;

        MegaEvolutionRegistry.MegaEvolutionData d = MegaEvolutionRegistry.getData(this.entity);

        if(d != null)
        {
            PokemonEntity target = d.isSingle() ? d.getMega() : d.getMegaX();
            return MegaEvolutionRegistry.isMegaLegendary(target) ? 3 : 5;
        }
        else return 0;
    }

    public void setMegaCharges(int megaCharge)
    {
        this.megaCharges = megaCharge;
    }

    public void setDefaultMegaCharges()
    {
        this.megaCharges = this.getMaxMegaCharges();
    }

    public void removeMegaCharge()
    {
        this.megaCharges--;
    }

    public void regenerateMegaCharge()
    {
        this.megaCharges++;
    }

    public void removeMegaEvolution()
    {
        if(!MegaEvolutionRegistry.isMega(this.entity))
            throw new IllegalStateException(this.getName() + " is not Mega-Evolved! Cannot remove Mega-Evolution.");

        PokemonEntity baseForm = MegaEvolutionRegistry.getData(this.entity).getBase();

        this.changePokemon(baseForm);
        this.updateEntity();
        this.updateAbility();
        this.resetAugments();
    }

    //Mega Evolutions, Forms, Evolution
    private void changePokemon(PokemonEntity entity)
    {
        this.transformAbility(entity);

        this.entity = entity;
        this.data = entity.data();

        this.setDefaultMegaCharges();
    }

    //Endpoint that involves database calls - specifically for Evolution
    public void evolve(EvolutionData data, PlayerDataQuery playerData)
    {
        this.changePokemon(data.getTarget());
        this.updateEntity();

        this.updateAbility();
        this.resetAugments();

        playerData.updateObjective(ObjectiveType.EVOLVE_POKEMON, 1);
        playerData.getStatistics().increase(StatisticType.POKEMON_EVOLVED);

        if(data.hasItemTrigger())
        {
            this.removeItem();
            this.updateItem();

            LoggerHelper.info(Pokemon.class, "Item Evolution Triggered – Removing Item | Evolution: %s -> %s.".formatted(data.getSource().getName(), data.getTarget().getName()));
        }

        if(data.getTarget().isNotSpawnable() && !playerData.getPokedex().hasCollected(data.getTarget()))
        {
            playerData.getPokedex().add(data.getTarget());
            playerData.updatePokedex();
            playerData.directMessage("*" + data.getTarget().getName() + " has been registered to your PokeDex!*");
        }
    }

    //Endpoint that involves database calls - specifically for Form changes
    public void changeForm(PokemonEntity target, PlayerDataQuery playerData)
    {
        this.changePokemon(target);

        if(playerData != null)
        {
            this.updateEntity();
            this.updateAbility();
            this.resetAugments();

            if(target.isNotSpawnable() && !playerData.getPokedex().hasCollected(target))
            {
                playerData.getPokedex().add(target);
                playerData.updatePokedex();
                playerData.directMessage("*" + target.getName() + " has been registered to your PokeDex!*");
            }
        }
    }

    //Endpoint that involves database calls - specifically for Mega-Evolutions
    public void megaEvolve(PokemonEntity target, PlayerDataQuery playerData)
    {
        this.changePokemon(target);
        this.updateEntity();

        this.updateAbility();
        this.resetAugments();

        MegaChargeManager.setBlocked(this.getUUID());

        if(target.isNotSpawnable() && !playerData.getPokedex().hasCollected(target))
        {
            playerData.getPokedex().add(target);
            playerData.updatePokedex();
            playerData.directMessage("*" + target.getName() + " has been registered to your PokeDex!*");
        }
    }

    //If pokemon == null, this is being called from somewhere without a relevant Pokemon object.
    //If moveEntity == null, this is being called outside a Duel.
    public static String getImage(@NotNull PokemonEntity pokemonEntity, boolean shiny, @Nullable Pokemon pokemon, @Nullable MoveEntity moveEntity)
    {
        String type = shiny ? "shiny" : "normal";

        //Default
        String dir = type + "/";
        String image = pokemonEntity.getImageName(shiny);

        //Stuff that requires a Pokemon object
        if(pokemon != null)
        {
            //TODO: Dynamax pictures that just have a cloud attached
            if(pokemon.isDynamaxed() && GigantamaxRegistry.hasGMax(pokemonEntity))
            {
                dir = "gigantamax_" + type + "/";
                image = image + "_Gigantamax";
            }
            else if(pokemonEntity == PokemonEntity.ARCEUS && pokemon.hasItem() && pokemon.getItem().isPlateItem())
            {
                String plateItemName = pokemon.getItem().getStyledName();
                plateItemName = plateItemName.split(" ")[0];

                dir = "extra_" + type + "/arceus/";
                image = image + "_" + plateItemName;
            }
            else if(pokemonEntity == PokemonEntity.SILVALLY && pokemon.hasItem() && pokemon.getItem().isMemoryItem())
            {
                String memoryType = pokemon.getItem().getMemoryType().getStyledName();

                dir = "extra_" + type + "/silvally/";
                image = image + "_" + memoryType;
            }
            else if(pokemonEntity == PokemonEntity.GENESECT && pokemon.hasItem() && pokemon.getItem().isDriveItem())
            {
                String driveItemName = pokemon.getItem().getStyledName();
                driveItemName = driveItemName.split(" ")[0];

                dir = "extra_" + type + "/genesect/";
                image = image + "_" + driveItemName;
            }
            else if(pokemonEntity == PokemonEntity.MINIOR_CORE)
            {
                dir = "extra_" + type + "/minior/";
                image = image + "_" + pokemon.getCustomData().getMiniorCoreColor();
            }
            else if(EnumSet.of(PokemonEntity.FLABEBE, PokemonEntity.FLOETTE, PokemonEntity.FLORGES).contains(pokemonEntity))
            {
                dir = "extra_" + type + "/flabebe_floette_florges/";
                image = image + "_" + pokemon.getCustomData().getFlowerColor();
            }
            else if(pokemonEntity == PokemonEntity.MAGEARNA && pokemon.getCustomData().isOriginalColorMagearna())
            {
                dir = "extra_" + type + "/magearna/";
                image = image + "_Original";
            }
            else if(EnumSet.of(PokemonEntity.UNFEZANT, PokemonEntity.FRILLISH, PokemonEntity.JELLICENT, PokemonEntity.PYROAR).contains(pokemonEntity))
            {
                dir = "extra_" + type + "/gender_differences/";
                image = image + "_" + (pokemon.getGender().equals(Gender.MALE) ? "Male" : "Female");
            }
            else if(pokemonEntity == PokemonEntity.SHELLOS || pokemonEntity == PokemonEntity.GASTRODON)
            {
                dir = "extra_" + type + "/shellos_gastrodon/";
                image = image + "_" + Global.normalize(pokemon.getCustomData().getSlugDirection());
            }
        }

        //Stuff that requires a MoveEntity and Pokemon object
        if(pokemon != null && moveEntity != null)
        {
            if(pokemonEntity == PokemonEntity.SOLGALEO && (moveEntity == MoveEntity.SUNSTEEL_STRIKE || moveEntity == MoveEntity.SEARING_SUNRAZE_SMASH))
            {
                dir = "extra_move/";
                image = image + "_RadiantSunPhase";
            }
            else if(pokemonEntity == PokemonEntity.LUNALA && (moveEntity == MoveEntity.MOONGEIST_BEAM || moveEntity == MoveEntity.MENACING_MOONRAZE_MAELSTROM))
            {
                dir = "extra_move/";
                image = image + "_FullMoonPhase";
            }
            else if(pokemonEntity == PokemonEntity.MARSHADOW)
            {
                dir = "extra_move/";
                image = image + "_Zenith_" + (shiny ? "S" : "N");
            }
            else if(pokemonEntity == PokemonEntity.ARTICUNO_GALAR && moveEntity == MoveEntity.FREEZING_GLARE)
            {
                dir = "extra_move/";
                image = image + "_FreezingGlare";
            }
            else if(pokemonEntity == PokemonEntity.ZAPDOS_GALAR && moveEntity == MoveEntity.THUNDEROUS_KICK)
            {
                dir = "extra_move/";
                image = image + "_ThunderousKick";
            }
            else if(pokemonEntity == PokemonEntity.MOLTRES_GALAR && moveEntity == MoveEntity.FIERY_WRATH)
            {
                dir = "extra_move/";
                image = image + "_FieryWrath";
            }

            if((pokemonEntity == PokemonEntity.RESHIRAM && (moveEntity == MoveEntity.BLUE_FLARE || moveEntity == MoveEntity.FUSION_FLARE))
                    || (pokemonEntity == PokemonEntity.ZEKROM && (moveEntity == MoveEntity.BOLT_STRIKE || moveEntity == MoveEntity.FUSION_BOLT))
                    || (pokemonEntity == PokemonEntity.KYUREM_WHITE && (moveEntity == MoveEntity.FUSION_FLARE || moveEntity == MoveEntity.ICE_BURN))
                    || (pokemonEntity == PokemonEntity.KYUREM_BLACK && (moveEntity == MoveEntity.FUSION_BOLT || moveEntity == MoveEntity.FREEZE_SHOCK)))
            {
                dir = "extra_" + type + "/unova_dragons/";
                image = image + "_Activated";
            }
        }

        //Deerling & Sawsbuck
        if(pokemonEntity == PokemonEntity.DEERLING || pokemonEntity == PokemonEntity.SAWSBUCK)
        {
            Month month = Global.timeNow().getMonth();

            String season = switch(month) {
                case DECEMBER, JANUARY, FEBRUARY -> "Winter";
                case MARCH, APRIL, MAY -> "Spring";
                case JUNE, JULY, AUGUST -> "Summer";
                case SEPTEMBER, OCTOBER, NOVEMBER -> "Autumn";
            };

            dir = "extra_" + type + "/deerling_sawsbuck/";
            image = image + "_" + season;
        }

        return "/data/images/" + dir + image + ".png";
    }

    public PokemonRarity.Rarity getRarity()
    {
        return this.entity.getRarity();
    }

    public Ability getAbility()
    {
        return this.ability;
    }

    public void transformAbility(PokemonEntity target)
    {
        List<Ability> sourceMain = this.data.getMainAbilities();
        List<Ability> sourceHidden = this.data.getHiddenAbilities();
        List<Ability> targetMain = target.data().getMainAbilities();
        List<Ability> targetHidden = target.data().getHiddenAbilities();

        boolean isMain = sourceMain.contains(this.ability);
        boolean isHidden = sourceHidden.contains(this.ability);

        if(isMain)
        {
            if(sourceMain.size() == targetMain.size()) //Main Ability slot transfer
            {
                //Random chance of hidden if target has Hidden but source didn't
                if(sourceHidden.isEmpty() && !targetHidden.isEmpty() && this.random.nextFloat() < 0.33F)
                    this.ability = targetHidden.get(0);
                else
                    this.ability = targetMain.get(sourceMain.indexOf(this.ability));
            }
            else //If target has different # of main abilities, randomly pick
                this.ability = targetMain.get(this.random.nextInt(targetMain.size()));
        }
        else if(isHidden)
        {
            if(sourceHidden.size() == targetHidden.size()) //Hidden Ability slot transfer
                this.ability = targetHidden.get(0);
            else if(targetHidden.isEmpty()) //Source has hidden but target doesn't
                this.ability = targetMain.get(this.random.nextInt(targetMain.size()));
        }
        else LoggerHelper.warn(Pokemon.class, "Pokemon " + this.entity + " has an ability that is not in its data: " + this.ability);
    }

    public void setAbility()
    {
        //75% chance of main ability (100% if Pokemon has no hidden abilities)
        if(this.random.nextFloat() < 0.75 || this.data.getHiddenAbilities().isEmpty())
            this.ability = this.data.getMainAbilities().stream().toList().get(this.random.nextInt(this.data.getMainAbilities().size()));
        //25% chance of hidden ability
        else
            this.ability = this.data.getHiddenAbilities().stream().toList().get(this.random.nextInt(this.data.getHiddenAbilities().size()));
    }

    public void setAbility(Ability ability)
    {
        this.ability = ability;
    }

    public boolean hasAbility(Ability ability)
    {
        if(this.ability == null) return false;
        else if(this.abilitiesIgnored && Ability.IGNORABLE.contains(ability)) return false;
        else return this.ability.equals(ability);
    }

    public boolean hasAbility(Ability... abilities)
    {
        return Stream.of(abilities).anyMatch(this::hasAbility);
    }

    public void removeAbility()
    {
        this.ability = null;
    }

    //Type

    public List<Type> getType()
    {
        return this.type;
    }

    //Replaces given type with newType
    public void replaceType(Type current, Type replacement)
    {
        this.type.set(this.type.indexOf(current), replacement);
    }

    //Remove Types
    public void removeType(Type remove)
    {
        this.replaceType(remove, Type.NORMAL);
    }

    //Replaces all types with the parameter
    public void setType(Type t)
    {
        this.setType(List.of(t));
    }

    //Replaces all types with the parameter types
    public void setType(List<Type> t)
    {
        this.type = new ArrayList<>(t);
    }

    //Adds a third typing to the Pokemon (Forest's Curse and Trick-or-Treat)
    public void addType(Type t)
    {
       if(this.type.size() == 3) this.type.set(2, t);
       else this.type.add(t);
    }

    public boolean isType(Type t)
    {
        return this.type.contains(t);
    }

    //TM & TR

    public boolean hasTM()
    {
        return this.tm != null;
    }

    public void setTM()
    {
        this.tm = null;
    }

    public void setTM(TM tm)
    {
        this.tm = tm;
    }

    public TM getTM()
    {
        return this.tm;
    }

    //Item
    public boolean hasItem()
    {
        return !this.item.equals(Item.NONE);
    }

    public void setItem(Item item)
    {
        this.item = item;
    }

    public void removeItem()
    {
        if(this.item.isConsumable()) this.consumedItem = this.item;
        this.item = Item.NONE;
    }

    public boolean hasConsumedItem()
    {
        return !this.consumedItem.equals(Item.NONE);
    }

    public Item getConsumedItem()
    {
        return this.consumedItem;
    }

    public void restoreItem()
    {
        if(this.hasConsumedItem())
        {
            this.item = this.consumedItem;
            this.consumedItem = Item.NONE;
        }
    }

    public boolean hasItem(Item item)
    {
        return this.item.equals(item);
    }

    public Item getItem()
    {
        return this.item;
    }

    //Ignored Abilities
    public void setAbilitiesIgnored(boolean v)
    {
        this.abilitiesIgnored = v;
    }

    //Ignored Stat Changes
    public void setStatChangesIgnored(boolean v)
    {
        this.statChangesIgnored = v;
    }

    //Moves

    public List<MoveEntity> getLevelUpMoves()
    {
        Map<MoveEntity, Integer> moves = this.data.getLevelUpMoves();

        List<MoveEntity> all = new ArrayList<>(moves.keySet());
        all.sort(Comparator.comparingInt(moves::get));

        return all;
    }

    public List<MoveEntity> availableMoves()
    {
        List<MoveEntity> available = new ArrayList<>(
                this.getLevelUpMoves().stream()
                        .filter(s -> this.data.getLevelUpMoves().get(s) <= this.getLevel())
                        .sorted(Comparator.comparingInt(s -> this.data.getLevelUpMoves().get(s)))
                        .toList()
        );

        if(this.hasTM()) available.add(this.tm.getMove());

        return this.withMovesOverride(available);
    }

    private List<MoveEntity> withMovesOverride(List<MoveEntity> input)
    {
        if(this.is(PokemonEntity.ZYGARDE_10, PokemonEntity.ZYGARDE_50, PokemonEntity.ZYGARDE_COMPLETE) && this.item.equals(Item.ZYGARDE_CUBE))
            input.addAll(Move.ZYGARDE_CUBE_MOVES);

        return input;
    }

    public void learnMove(MoveEntity move, int index)
    {
        this.moves.set(index, move);
    }

    public void setMoves()
    {
        this.setMoves(List.of(MoveEntity.TACKLE, MoveEntity.TACKLE, MoveEntity.TACKLE, MoveEntity.TACKLE));
    }

    public void setMoves(List<MoveEntity> moves)
    {
        this.moves = new ArrayList<>(moves);
    }

    public List<MoveEntity> getMoves()
    {
        return this.moves;
    }

    public Move getMove(int index)
    {
        return new Move(this.moves.get(index));
    }

    //IVs & EVs

    public void setIVs(Document data)
    {
        this.ivs = new PokemonStats(data);
    }

    public void setIVs(int min)
    {
        do this.setIVs();
        while(this.getTotalIVRounded() < min);
    }

    public void setEVs(Document data)
    {
        this.evs = new PokemonStats(data);
    }

    public void setEVs(Map<Stat, Integer> evs)
    {
        evs.forEach((key, value) -> this.evs.set(key, value));
    }

    public void setIVs()
    {
        this.ivs = new PokemonStats();
        Arrays.stream(Stat.values()).forEach(s -> this.ivs.set(s, new SplittableRandom().nextInt(31) + 1));
    }

    public void setIVs(Map<Stat, Integer> ivs)
    {
        ivs.forEach((key, value) -> this.ivs.set(key, value));
    }

    public void setEVs()
    {
        this.evs = new PokemonStats();
    }

    public void setIV(Stat s, int IV)
    {
        this.ivs.set(s, IV);
    }

    public void setEV(Stat s, int EV)
    {
        this.evs.set(s, EV);
    }

    public void addEVs(Stat stat, int amount)
    {
        if(this.getTotalEV() < 510 && this.getEVs().get(stat) + amount <= 252) this.evs.increase(stat, amount);
    }

    public void gainEVs(Pokemon other)
    {
        int boost = 1;
        for(Stat s : other.getEVYield().keySet()) this.addEVs(s, other.getEVYield().get(s) * boost);
    }

    public int getTotalEV()
    {
        return this.getEVs().values().stream().mapToInt(i -> i).sum();
    }

    public String getTotalIV()
    {
        return String.format("%.2f", this.getIVs().values().stream().mapToDouble(iv -> iv / 31D).sum() * 100 / 6D) + "%";
    }

    public double getTotalIVRounded()
    {
        String iv = this.getTotalIV();
        return Double.parseDouble(iv.substring(0, iv.indexOf("%")));
    }

    public LinkedHashMap<Stat, Integer> getEVYield()
    {
        return this.data.getEVYield().get();
    }

    public LinkedHashMap<Stat, Integer> getIVs()
    {
        return this.ivs.get();
    }

    public LinkedHashMap<Stat, Integer> getEVs()
    {
        return this.evs.get();
    }

    //Augments
    public int getTotalAugmentSlots()
    {
        List<Integer> scaling = PokemonAugmentRegistry.AUGMENT_SLOTS.get(this.getRarity());

        int slots = 0;
        for(Integer slotLevel : scaling) if(slotLevel <= this.getLevel()) slots++;
        return slots;
    }

    public int getLevelForNextSlot()
    {
        List<Integer> slotUnlocks = PokemonAugmentRegistry.AUGMENT_SLOTS.get(this.getRarity());

        for(int lvl : slotUnlocks) if(lvl > this.getLevel()) return lvl;
        return -1;
    }

    public void equipAugment(PokemonAugment augment)
    {
        this.augments.add(augment);
    }

    public void removeAugment(PokemonAugment augment)
    {
        this.augments.remove(augment);
    }

    public void setAugments(List<String> augments)
    {
        this.augments = EnumSet.noneOf(PokemonAugment.class);
        augments.forEach(s -> this.augments.add(PokemonAugment.cast(s)));
    }

    public void clearAugments()
    {
        this.augments = EnumSet.noneOf(PokemonAugment.class);
    }

    public int getAvailableAugmentSlots()
    {
        return this.getTotalAugmentSlots() - this.augments.stream().mapToInt(PokemonAugment::getSlotCost).sum();
    }

    public EnumSet<PokemonAugment> getAugments()
    {
        return this.augments;
    }

    public boolean hasAugment(PokemonAugment augment)
    {
        return this.augments.contains(augment);
    }

    public boolean isValidAugment(PokemonAugment augment)
    {
        return PokemonAugmentRegistry.AUGMENT_DATA.get(this.entity).has(augment);
    }

    public double getAugmentStatBoost(Stat s)
    {
        if(s.equals(Stat.HP)) return this.hasAugment(PokemonAugment.HP_BOOST) ? 1.05 : 1.0;
        else if(s.equals(Stat.ATK)) return this.hasAugment(PokemonAugment.ATK_BOOST) ? 1.05 : 1.0;
        else if(s.equals(Stat.DEF)) return this.hasAugment(PokemonAugment.DEF_BOOST) ? 1.05 : 1.0;
        else if(s.equals(Stat.SPATK)) return this.hasAugment(PokemonAugment.SPATK_BOOST) ? 1.05 : 1.0;
        else if(s.equals(Stat.SPDEF)) return this.hasAugment(PokemonAugment.SPDEF_BOOST) ? 1.05 : 1.0;
        else if(s.equals(Stat.SPD)) return this.hasAugment(PokemonAugment.SPD_BOOST) ? 1.05 : 1.0;
        else return 1.0;
    }

    //Prestige Level
    public double getPrestigeBonus(Stat s)
    {
        if(!this.hasPrestiged()) return 1.0;
        else
        {
            double ratio = (double)this.getPrestigeLevel() / (double)this.getMaxPrestigeLevel();

            boolean hp = s.equals(Stat.HP);
            boolean speed = s.equals(Stat.SPD);
            double maxBonus = switch(this.getRarity()) {
                case COPPER, SILVER, GOLD -> hp ? 0.75 : (speed ? 0.30 : 0.50);
                case DIAMOND, PLATINUM -> hp ? 0.50 : (speed ? 0.15 : 0.35);
                case MYTHICAL, ULTRA_BEAST, LEGENDARY -> hp ? 0.30 : (speed ? 0.05 : 0.25);
            };

            return 1.0 + (ratio * maxBonus);
        }
    }

    public int getPrestigeLevel()
    {
        return this.prestigeLevel;
    }

    public boolean hasPrestiged()
    {
        return this.prestigeLevel != 0;
    }

    public void setPrestigeLevel(int prestigeLevel)
    {
        this.prestigeLevel = prestigeLevel;
    }

    public int getMaxPrestigeLevel()
    {
        return switch(this.getRarity()) {
            case COPPER -> 7;
            case SILVER -> 6;
            case GOLD -> 5;
            case DIAMOND -> 4;
            case PLATINUM -> 3;
            case MYTHICAL, ULTRA_BEAST -> 2;
            case LEGENDARY -> 1;
        };
    }

    public void increasePrestigeLevel()
    {
        if(this.prestigeLevel < this.getMaxPrestigeLevel()) this.prestigeLevel++;
    }

    //Dynamax Level

    public void increaseDynamaxLevel()
    {
        if(this.dynamaxLevel < 10) this.dynamaxLevel++;
    }

    public void setDynamaxLevel(int dynamaxLevel)
    {
        this.dynamaxLevel = dynamaxLevel;
    }

    public int getDynamaxLevel()
    {
        return this.dynamaxLevel;
    }

    //Level & Experience

    public void addExp(int exp)
    {
        this.exp += exp;

        int req = GrowthRate.getRequiredExp(this.data.getGrowthRate(), this.level);

        while(this.exp >= req && this.level < 100)
        {
            this.level++;
            this.exp -= req;

            req = GrowthRate.getRequiredExp(this.data.getGrowthRate(), this.level);
        }
    }

    public int getDefeatExp(Pokemon other)
    {
        int a = 1;
        int b = this.data.getBaseExperience();
        int e = 1;
        int L = other.getLevel();
        int Lp = this.getLevel();
        int p = 1;
        int s = 1;
        int t = 1;

        return (int) (t * e * p * (1 + (a * b * L / (5.0 * s)) * (Math.pow(2 * L + 10, 2.5) / Math.pow(L + Lp + 10, 2.5))));
    }

    public void setExp(int exp)
    {
        this.exp = exp;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public int getExp()
    {
        return this.exp;
    }

    public int getLevel()
    {
        return this.level;
    }

    //Nature
    public void setNature()
    {
        this.setNature(Nature.values()[new SplittableRandom().nextInt(Nature.values().length)]);
    }

    public void setNature(Nature nature)
    {
        this.nature = nature;
    }

    public Nature getNature()
    {
        return this.nature;
    }

    //Gender
    public void setGender()
    {
        this.setGender(this.data.getGenderRate() == -1 ? Gender.UNKNOWN : (new SplittableRandom().nextInt(8) < this.data.getGenderRate() ? Gender.FEMALE : Gender.MALE));
    }

    public void setGender(Gender gender)
    {
        this.gender = gender;
    }

    public Gender getGender()
    {
        return this.gender;
    }

    public EnumSet<EggGroup> getEggGroups()
    {
        return EnumSet.copyOf(this.data.getEggGroups());
    }

    //Shiny
    public void setShiny()
    {
        this.setShiny(new SplittableRandom().nextInt(4096) < 1);
    }

    public void setShiny(boolean shiny)
    {
        this.shiny = shiny;
    }

    public boolean isShiny()
    {
        return this.shiny;
    }

    //Number
    public void setNumber(int number)
    {
        this.number = number;
    }

    public int getNumber()
    {
        return this.number;
    }

    //Nickname / Name
    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    public String getNickname()
    {
        return this.nickname;
    }

    public boolean hasNickname()
    {
        return !this.nickname.equals("");
    }

    public String getDisplayName()
    {
        return this.hasNickname() ? "\"" + this.getNickname() + "\"" : this.getName();
    }

    public String getName()
    {
        return this.data.getName();
    }

    //UUID

    public void setUUID()
    {
        this.setUUID(IntStream.rangeClosed(1, 6).mapToObj(i -> IDHelper.alphanumeric(4)).collect(Collectors.joining("")));
    }

    public void setUUID(String UUID)
    {
        this.UUID = UUID;
    }

    public String getUUID()
    {
        return this.UUID;
    }

    //Data

    public void setData(PokemonEntity entity)
    {
        this.data = entity.data();
    }

    public PokemonData getData()
    {
        return this.data;
    }

    //Core

    private Bson query()
    {
        return Filters.eq("UUID", this.UUID);
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        else if(o == null || this.getClass() != o.getClass()) return false;
        else return this.UUID.equals(((Pokemon)o).UUID);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.UUID);
    }
}
