package com.calculusmaster.pokecord.game.pokemon.evolution;

import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity.*;

public class MegaEvolutionRegistry
{
    public static final Map<PokemonEntity, MegaEvolutionData> MEGA_EVOLUTION_DATA = new LinkedHashMap<>();
    public static final EnumSet<PokemonEntity> MEGA_EVOLUTIONS = EnumSet.noneOf(PokemonEntity.class);
    public static final EnumSet<PokemonEntity> LEGENDARY_MEGA_EVOLUTIONS = EnumSet.noneOf(PokemonEntity.class);

    public static void init()
    {
        MegaEvolutionRegistry.register(VENUSAUR, VENUSAUR_MEGA);
        MegaEvolutionRegistry.register(CHARIZARD, CHARIZARD_MEGA_X, CHARIZARD_MEGA_Y);
        MegaEvolutionRegistry.register(BLASTOISE, BLASTOISE_MEGA);
        MegaEvolutionRegistry.register(BEEDRILL, BEEDRILL_MEGA);
        MegaEvolutionRegistry.register(PIDGEOT, PIDGEOT_MEGA);
        MegaEvolutionRegistry.register(ALAKAZAM, ALAKAZAM_MEGA);
        MegaEvolutionRegistry.register(SLOWBRO, SLOWBRO_MEGA);
        MegaEvolutionRegistry.register(GENGAR, GENGAR_MEGA);
        MegaEvolutionRegistry.register(KANGASKHAN, KANGASKHAN_MEGA);
        MegaEvolutionRegistry.register(PINSIR, PINSIR_MEGA);
        MegaEvolutionRegistry.register(GYARADOS, GYARADOS_MEGA);
        MegaEvolutionRegistry.register(AERODACTYL, AERODACTYL_MEGA);
        MegaEvolutionRegistry.register(AMPHAROS, AMPHAROS_MEGA);
        MegaEvolutionRegistry.register(STEELIX, STEELIX_MEGA);
        MegaEvolutionRegistry.register(SCIZOR, SCIZOR_MEGA);
        MegaEvolutionRegistry.register(HERACROSS, HERACROSS_MEGA);
        MegaEvolutionRegistry.register(HOUNDOOM, HOUNDOOM_MEGA);
        MegaEvolutionRegistry.register(TYRANITAR, TYRANITAR_MEGA);
        MegaEvolutionRegistry.register(SCEPTILE, SCEPTILE_MEGA);
        MegaEvolutionRegistry.register(BLAZIKEN, BLAZIKEN_MEGA);
        MegaEvolutionRegistry.register(SWAMPERT, SWAMPERT_MEGA);
        MegaEvolutionRegistry.register(GARDEVOIR, GARDEVOIR_MEGA);
        MegaEvolutionRegistry.register(SABLEYE, SABLEYE_MEGA);
        MegaEvolutionRegistry.register(MAWILE, MAWILE_MEGA);
        MegaEvolutionRegistry.register(AGGRON, AGGRON_MEGA);
        MegaEvolutionRegistry.register(MEDICHAM, MEDICHAM_MEGA);
        MegaEvolutionRegistry.register(MANECTRIC, MANECTRIC_MEGA);
        MegaEvolutionRegistry.register(SHARPEDO, SHARPEDO_MEGA);
        MegaEvolutionRegistry.register(CAMERUPT, CAMERUPT_MEGA);
        MegaEvolutionRegistry.register(ALTARIA, ALTARIA_MEGA);
        MegaEvolutionRegistry.register(BANETTE, BANETTE_MEGA);
        MegaEvolutionRegistry.register(ABSOL, ABSOL_MEGA);
        MegaEvolutionRegistry.register(GLALIE, GLALIE_MEGA);
        MegaEvolutionRegistry.register(SALAMENCE, SALAMENCE_MEGA);
        MegaEvolutionRegistry.register(METAGROSS, METAGROSS_MEGA);
        MegaEvolutionRegistry.register(LOPUNNY, LOPUNNY_MEGA);
        MegaEvolutionRegistry.register(GARCHOMP, GARCHOMP_MEGA);
        MegaEvolutionRegistry.register(LUCARIO, LUCARIO_MEGA);
        MegaEvolutionRegistry.register(ABOMASNOW, ABOMASNOW_MEGA);
        MegaEvolutionRegistry.register(GALLADE, GALLADE_MEGA);
        MegaEvolutionRegistry.register(AUDINO, AUDINO_MEGA);

        MegaEvolutionRegistry.registerLegendary(MEWTWO, MEWTWO_MEGA_X, MEWTWO_MEGA_Y);
        MegaEvolutionRegistry.registerLegendary(LATIAS, LATIAS_MEGA);
        MegaEvolutionRegistry.registerLegendary(LATIOS, LATIOS_MEGA);
        MegaEvolutionRegistry.registerLegendary(GROUDON, GROUDON_PRIMAL);
        MegaEvolutionRegistry.registerLegendary(KYOGRE, KYOGRE_PRIMAL);
        MegaEvolutionRegistry.registerLegendary(RAYQUAZA, RAYQUAZA_MEGA);
        MegaEvolutionRegistry.registerLegendary(DIANCIE, DIANCIE_MEGA); //TODO: should diancie be counted as a legendary mega?
    }

    private static void register(PokemonEntity base, PokemonEntity mega)
    {
        MegaEvolutionData data = new MegaEvolutionData(base, mega);

        MEGA_EVOLUTION_DATA.put(base, data);
        MEGA_EVOLUTION_DATA.put(mega, data);

        MEGA_EVOLUTIONS.add(mega);
    }

    private static void register(PokemonEntity base, PokemonEntity megaX, PokemonEntity megaY)
    {
        MegaEvolutionData data = new MegaEvolutionData(base, megaX, megaY);

        MEGA_EVOLUTION_DATA.put(base, data);
        MEGA_EVOLUTION_DATA.put(megaX, data);
        MEGA_EVOLUTION_DATA.put(megaY, data);

        MEGA_EVOLUTIONS.add(megaX);
        MEGA_EVOLUTIONS.add(megaY);
    }

    private static void registerLegendary(PokemonEntity base, PokemonEntity mega)
    {
        MegaEvolutionRegistry.register(base, mega);

        MEGA_EVOLUTIONS.add(mega);
        LEGENDARY_MEGA_EVOLUTIONS.add(mega);
    }

    private static void registerLegendary(PokemonEntity base, PokemonEntity megaX, PokemonEntity megaY)
    {
        MegaEvolutionRegistry.register(base, megaX, megaY);

        MEGA_EVOLUTIONS.add(megaX);
        MEGA_EVOLUTIONS.add(megaY);
        LEGENDARY_MEGA_EVOLUTIONS.add(megaX);
        LEGENDARY_MEGA_EVOLUTIONS.add(megaY);
    }

    public static MegaEvolutionData getData(PokemonEntity entity)
    {
        return MEGA_EVOLUTION_DATA.get(entity);
    }

    public static boolean isMega(PokemonEntity entity)
    {
        return MEGA_EVOLUTIONS.contains(entity);
    }

    public static boolean isMegaLegendary(PokemonEntity entity)
    {
        return LEGENDARY_MEGA_EVOLUTIONS.contains(entity);
    }

    public static boolean hasMegaData(PokemonEntity entity)
    {
        return MEGA_EVOLUTION_DATA.containsKey(entity);
    }

    public static class MegaEvolutionData
    {
        private final PokemonEntity base;

        private final PokemonEntity mega; //For single Mega Evolutions

        private final PokemonEntity megaX; //For Mega Evolutions with two forms - Mega X
        private final PokemonEntity megaY; //For Mega Evolutions with two forms - Mega Y

        //Mega Evolution Data – Single Mega Forms
        public MegaEvolutionData(PokemonEntity base, PokemonEntity mega)
        {
            this.base = base;

            this.mega = mega;

            this.megaX = null;
            this.megaY = null;
        }

        //Mega Evolution Data – X & Y Mega Forms
        public MegaEvolutionData(PokemonEntity base, PokemonEntity megaXForm, PokemonEntity megaYForm)
        {
            this.base = base;

            this.mega = null;

            this.megaX = megaXForm;
            this.megaY = megaYForm;
        }

        public boolean isSingle()
        {
            return this.mega != null;
        }

        public boolean isXY()
        {
            return this.megaX != null && this.megaY != null;
        }

        public PokemonEntity getBase()
        {
            return this.base;
        }

        public PokemonEntity getMega()
        {
            return this.mega;
        }

        public PokemonEntity getMegaX()
        {
            return this.megaX;
        }

        public PokemonEntity getMegaY()
        {
            return this.megaY;
        }
    }
}
