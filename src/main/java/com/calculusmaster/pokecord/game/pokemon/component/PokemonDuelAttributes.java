package com.calculusmaster.pokecord.game.pokemon.component;

public class PokemonDuelAttributes
{
    public String UUID;
    public int lastDamageTaken;
    public int statImmuneTurns;
    public boolean recharge;
    public boolean isRaised;
    public boolean canSwap;

    public int asleepTurns;
    public int boundTurns;
    public int badlyPoisonedTurns;

    public boolean flyUsed;
    public boolean bounceUsed;
    public boolean digUsed;
    public boolean diveUsed;
    public boolean phantomForceUsed;
    public boolean shadowForceUsed;

    public boolean defenseCurlUsed;
    public int rolloutTurns;
    public int iceballTurns;
    public boolean rageUsed;
    public int magnetRiseTurns;
    public int tauntTurns;
    public boolean detectUsed;
    public boolean protectUsed;
    public boolean chargeUsed;
    public boolean futureSightUsed;
    public int futureSightTurns;
    public boolean lockOnUsed;
    public boolean imprisonUsed;
    public boolean destinyBondUsed;
    public int perishSongTurns;
    public boolean kingsShieldUsed;
    public int bideTurns;
    public int bideDamage;
    public boolean focusEnergyUsed;
    public boolean laserFocusUsed;
    public boolean endureUsed;
    public boolean banefulBunkerUsed;
    public boolean spikyShieldUsed;
    public boolean quickGuardUsed;
    public boolean mudSportUsed;
    public boolean matBlockUsed;
    public boolean doomDesireUsed;
    public int doomDesireTurns;
    public boolean craftyShieldUsed;
    public boolean auroraVeilUsed;
    public int auroraVeilTurns;
    public boolean plasmaFistsUsed;
    public boolean electrifyUsed;
    public boolean wishUsed;
    public boolean waterSportUsed;
    public boolean meteorBeamUsed;
    public boolean solarBeamUsed;
    public boolean isTarShotTarget;
    public boolean isOctolocked;
    public boolean unableToUseSoundMoves;
    public int unableToUseSoundMovesTurns;
    public boolean furyCutterUsed;
    public int furyCutterTurns;
    public boolean isCoveredPowder;
    public int stockpileCount;
    public int gmaxWildfireTurns;
    public int gmaxVineLashTurns;
    public int gmaxCannonadeTurns;
    public int gmaxVolcalithTurns;

    public boolean disguiseActivated;

    public void setDefaults()
    {
        this.lastDamageTaken = 0;
        this.statImmuneTurns = 0;
        this.recharge = false;
        this.isRaised = false;
        this.canSwap = true;

        this.asleepTurns = 0;
        this.boundTurns = 0;
        this.badlyPoisonedTurns = 0;

        this.flyUsed = false;
        this.bounceUsed = false;
        this.digUsed = false;
        this.diveUsed = false;
        this.phantomForceUsed = false;
        this.shadowForceUsed = false;

        this.defenseCurlUsed = false;
        this.rolloutTurns = 0;
        this.iceballTurns = 0;
        this.rageUsed = false;
        this.magnetRiseTurns = 0;
        this.tauntTurns = 0;
        this.detectUsed = false;
        this.protectUsed = false;
        this.chargeUsed = false;
        this.futureSightUsed = false;
        this.futureSightTurns = 0;
        this.lockOnUsed = false;
        this.imprisonUsed = false;
        this.destinyBondUsed = false;
        this.perishSongTurns = 0;
        this.kingsShieldUsed = false;
        this.bideTurns = 0;
        this.bideDamage = 0;
        this.focusEnergyUsed = false;
        this.laserFocusUsed = false;
        this.endureUsed = false;
        this.banefulBunkerUsed = false;
        this.spikyShieldUsed = false;
        this.quickGuardUsed = false;
        this.mudSportUsed = false;
        this.matBlockUsed = false;
        this.doomDesireUsed = false;
        this.doomDesireTurns = 0;
        this.craftyShieldUsed = false;
        this.auroraVeilUsed = false;
        this.auroraVeilTurns = 0;
        this.plasmaFistsUsed = false;
        this.electrifyUsed = false;
        this.wishUsed = false;
        this.waterSportUsed = false;
        this.isOctolocked = false;
        this.unableToUseSoundMoves = false;
        this.unableToUseSoundMovesTurns = 0;
        this.furyCutterUsed = false;
        this.furyCutterTurns = 0;
        this.isCoveredPowder = false;
        this.stockpileCount = 0;
        this.gmaxWildfireTurns = 0;
        this.gmaxVineLashTurns = 0;
        this.gmaxCannonadeTurns = 0;
        this.gmaxVolcalithTurns = 0;

        this.disguiseActivated = false;
    }

    public PokemonDuelAttributes(String UUID)
    {
        this.UUID = UUID;
        this.setDefaults();
    }
}
