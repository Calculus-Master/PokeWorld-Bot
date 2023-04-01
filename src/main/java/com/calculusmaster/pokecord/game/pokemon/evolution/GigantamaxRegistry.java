package com.calculusmaster.pokecord.game.pokemon.evolution;

import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.calculusmaster.pokecord.game.moves.data.MoveEntity.*;
import static com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity.*;

public class GigantamaxRegistry
{
    private static final Map<PokemonEntity, MoveEntity> GIGANTAMAX_DATA = new LinkedHashMap<>();

    public static void init()
    {
        GIGANTAMAX_DATA.put(CHARIZARD, GMAX_WILDFIRE);
        GIGANTAMAX_DATA.put(BUTTERFREE, GMAX_BEFUDDLE);
        GIGANTAMAX_DATA.put(PIKACHU, GMAX_VOLT_CRASH);
        GIGANTAMAX_DATA.put(MEOWTH, GMAX_GOLD_RUSH);
        GIGANTAMAX_DATA.put(MACHAMP, GMAX_CHI_STRIKE);
        GIGANTAMAX_DATA.put(GENGAR, GMAX_TERROR);
        GIGANTAMAX_DATA.put(KINGLER, GMAX_FOAM_BURST);
        GIGANTAMAX_DATA.put(LAPRAS, GMAX_RESONANCE);
        GIGANTAMAX_DATA.put(EEVEE, GMAX_CUDDLE);
        GIGANTAMAX_DATA.put(SNORLAX, GMAX_REPLENISH);
        GIGANTAMAX_DATA.put(GARBODOR, GMAX_MALODOR);
        GIGANTAMAX_DATA.put(MELMETAL, GMAX_MELTDOWN);
        GIGANTAMAX_DATA.put(CORVIKNIGHT, GMAX_WIND_RAGE);
        GIGANTAMAX_DATA.put(ORBEETLE, GMAX_GRAVITAS);
        GIGANTAMAX_DATA.put(DREDNAW, GMAX_STONESURGE);
        GIGANTAMAX_DATA.put(COALOSSAL, GMAX_VOLCALITH);
        GIGANTAMAX_DATA.put(FLAPPLE, GMAX_TARTNESS);
        GIGANTAMAX_DATA.put(APPLETUN, GMAX_SWEETNESS);
        GIGANTAMAX_DATA.put(SANDACONDA, GMAX_SANDBLAST);
        GIGANTAMAX_DATA.put(TOXTRICITY_LOW_KEY, GMAX_STUNSHOCK);
        GIGANTAMAX_DATA.put(TOXTRICITY_AMPED, GMAX_STUNSHOCK);
        GIGANTAMAX_DATA.put(CENTISKORCH, GMAX_CENTIFERNO);
        GIGANTAMAX_DATA.put(HATTERENE, GMAX_SMITE);
        GIGANTAMAX_DATA.put(GRIMMSNARL, GMAX_SNOOZE);
        GIGANTAMAX_DATA.put(ALCREMIE, GMAX_FINALE);
        GIGANTAMAX_DATA.put(COPPERAJAH, GMAX_STEELSURGE);
        GIGANTAMAX_DATA.put(DURALUDON, GMAX_DEPLETION);
        GIGANTAMAX_DATA.put(VENUSAUR, GMAX_VINE_LASH);
        GIGANTAMAX_DATA.put(BLASTOISE, GMAX_CANNONADE);
        GIGANTAMAX_DATA.put(RILLABOOM, GMAX_DRUM_SOLO);
        GIGANTAMAX_DATA.put(CINDERACE, GMAX_FIREBALL);
        GIGANTAMAX_DATA.put(INTELEON, GMAX_HYDROSNIPE);
        GIGANTAMAX_DATA.put(URSHIFU_SINGLE_STRIKE, GMAX_ONE_BLOW);
        GIGANTAMAX_DATA.put(URSHIFU_RAPID_STRIKE, GMAX_RAPID_FLOW);

        //Custom TODO - Add Rayquaza/Kyogre/Groudon G-Max forms?
        //Rayquaza, Groudon and Kyogre images were made by User "KingofAnime-KoA" on DeviantArt
        //GigantamaxRegistry.register("Rayquaza", "Stratoblast", Type.FLYING, "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/8ba904d2-8453-4d48-aa9f-1c01fb79c3af/de45v3l-207fe813-2cae-4df9-9990-fef5d4500738.png/v1/fill/w_1280,h_1467,strp/gigantamax_rayquaza__the_gmax_lord_of_the_skies_by_kingofanime_koa_de45v3l-fullview.png?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7ImhlaWdodCI6Ijw9MTQ2NyIsInBhdGgiOiJcL2ZcLzhiYTkwNGQyLTg0NTMtNGQ0OC1hYTlmLTFjMDFmYjc5YzNhZlwvZGU0NXYzbC0yMDdmZTgxMy0yY2FlLTRkZjktOTk5MC1mZWY1ZDQ1MDA3MzgucG5nIiwid2lkdGgiOiI8PTEyODAifV1dLCJhdWQiOlsidXJuOnNlcnZpY2U6aW1hZ2Uub3BlcmF0aW9ucyJdfQ.1ZIszvLOVzHyEzH7NcOo-y0LmeX7UTwDnsv6ziuZNdg");
        //GigantamaxRegistry.register("Kyogre", "Oceanize", Type.WATER, "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/8ba904d2-8453-4d48-aa9f-1c01fb79c3af/de4o4sh-d052123f-4620-41d2-8188-fc193ae3962c.png/v1/fill/w_1280,h_1377,strp/gigantimax_kyogre__the_colossal_lord_of_the_oceans_by_kingofanime_koa_de4o4sh-fullview.png?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7ImhlaWdodCI6Ijw9MTM3NyIsInBhdGgiOiJcL2ZcLzhiYTkwNGQyLTg0NTMtNGQ0OC1hYTlmLTFjMDFmYjc5YzNhZlwvZGU0bzRzaC1kMDUyMTIzZi00NjIwLTQxZDItODE4OC1mYzE5M2FlMzk2MmMucG5nIiwid2lkdGgiOiI8PTEyODAifV1dLCJhdWQiOlsidXJuOnNlcnZpY2U6aW1hZ2Uub3BlcmF0aW9ucyJdfQ.x7DsTDYYQLuzVIV5xlzSQcnRASW5oCcb3wLaZtGWIUY");
        //GigantamaxRegistry.register("Groudon", "Evaporation", Type.GROUND, "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/8ba904d2-8453-4d48-aa9f-1c01fb79c3af/de4x2c3-4b0bcdd8-5f75-47ef-b952-31875146991a.png/v1/fill/w_1280,h_1370,strp/gigantamax_groudon__the_gmax_molten_kaiju__by_kingofanime_koa_de4x2c3-fullview.png?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7ImhlaWdodCI6Ijw9MTM3MCIsInBhdGgiOiJcL2ZcLzhiYTkwNGQyLTg0NTMtNGQ0OC1hYTlmLTFjMDFmYjc5YzNhZlwvZGU0eDJjMy00YjBiY2RkOC01Zjc1LTQ3ZWYtYjk1Mi0zMTg3NTE0Njk5MWEucG5nIiwid2lkdGgiOiI8PTEyODAifV1dLCJhdWQiOlsidXJuOnNlcnZpY2U6aW1hZ2Uub3BlcmF0aW9ucyJdfQ.3TMzdvI8QLmyfo1wkh6ZScaUVzD-eRsgbwG54hn70dE");
    }

    public static boolean hasGMax(PokemonEntity p)
    {
        return GIGANTAMAX_DATA.containsKey(p);
    }

    public static MoveEntity getGMaxMove(PokemonEntity p)
    {
        return GIGANTAMAX_DATA.get(p);
    }
}
