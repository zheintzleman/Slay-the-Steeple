package app;

/** The available Primaries for all Effects.
 * 
 * @see Effect
 * @see Combat Combat.playCard()
 * @see Combat Combat.playEffect()
 * @see Card Card.Description.reconstructDescription()
 */
public enum Eff {
    SearingBlow,
    Attack,
    HeavyAttack,
    BodySlam,
    Dropkick,
    AtkAll,
    Whirlwind,
    SpotWeakness,
    Apply,
    Unapply,
    Block,
    Entrench,
    AtkRandom,
    AppPlayer,
    ClearStatusPlayer,
    AppAll,
    DmgPlayerC,
    DmgPlayerNC,
    LoseHPC,
    LoseHPNC,
    Ethereal,
    Exhaust,
    Draw,
    Upgrade,
    PutOnDrawPile,
    CopyToHand,
    CopyToHandFree,
    GainToDraw,
    Anger,
    Rampage,
    ExhaustNonattacks,
    Havoc,
    LoseEnergy,
    GainEnergy,
    AddCost,
    IncrCombustCnt,
    Combust,
    Clash,
    Unplayable,
    Innate,
    Exhume,
    Feed,
    FiendFire
  }