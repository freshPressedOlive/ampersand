package ampersand;

// in the PC constructor, include a field for random race choices like tool proficiency choice, cantrip choice, etc.
// pass that string into the specific constructors, but not the super Race constructor because it doesn't need it

public class Race
{
    PlayerCharacter PC;
    boolean feyAncestry;

    // ASIs are ignored because Tasha's
    // age, alignment, and languages aren't included in this because they're kinda flavor text and can be done elsewhere
    public Race()
    {

    }
    public Race(PlayerCharacter PC)
    {
        this.PC = PC;
    }
}

class Dragonborn extends Race
{
    public Dragonborn(PlayerCharacter PC, String draconicAncestry)
    {
        super(PC);

        // set a draconic ancestry based on the given string, including a breath weapon type and damage resistance
        // also make a subclass for the Fizban's version
    }
}

class Dwarf extends Race
{
    public Dwarf(PlayerCharacter PC)
    {
        super(PC);

        PC.speed = 25;

        PC.setDarkvision();

        // dwarven resilience,  sets poison damage resistance
        PC.damageTypes[9].resistant = true;
        PC.advantageAgainstPoison = true;

        // sets battleaxe, handaxe, light hammer, and warhammer proficiencies
        PC.getWeaponProfs().get(14).setProf();
        PC.getWeaponProfs().get(3).setProf();
        PC.getWeaponProfs().get(5).setProf();
        PC.getWeaponProfs().get(30).setProf();

        // set a tool proficiency between smith's, brewer's, and mason's

        // stonecunning, expertise in history if it's about stone
    }
}

class HillDwarf extends Dwarf
{
    public HillDwarf(PlayerCharacter PC)
    {
        super(PC);

        // dwarven toughness, hp increases by 1 for every level
    }
}

class MountainDwarf extends Dwarf
{
    public MountainDwarf(PlayerCharacter PC)
    {
        super(PC);

        // gives light and medium armor proficiency
        PC.lightArmorProf = true;
        PC.mediumArmorProf = true;
    }
}

class Elf extends Race
{
    public Elf(PlayerCharacter PC)
    {
        super(PC);

        // set darkvision
        this.PC.setDarkvision();

        // sets perception proficiency
        PC.getSkills().get(11).setProf();

        // just sets a boolean to true, this can be dealt with later with sleep, charm, etc. spell by spell
        feyAncestry = true;
    }

    public void elfWeaponTraining()
    {
        // sets longsword, shortsword, shortbow, and longbow proficiencies
        PC.getWeaponProfs().get(27).setProf();
        PC.getWeaponProfs().get(21).setProf();
        PC.getWeaponProfs().get(12).setProf();
        PC.getWeaponProfs().get(35).setProf();
    }
}

class HighElf extends Elf
{
    public HighElf(PlayerCharacter PC, String cantripName)
    {
        super(PC);
        elfWeaponTraining();

        // add a cantrip
            // use a similar mechanism to the drow one, but take in the cantrip's name as a parameter
        Spell cantrip = Spell.findSpell(cantripName);

        if (cantrip == null)
            System.out.println("The cantrip you chose wasn't found!");
        else if (!cantrip.spellLists[12])
            System.out.println("This isn't a wizard cantrip!");
        else
            PC.learnSpell(cantripName);

        // +1 language
    }
}

class WoodElf extends Elf
{
    public WoodElf(PlayerCharacter PC)
    {
        super(PC);

        elfWeaponTraining();

        PC.speed = 35;

        // mask of the wild: you can hide whenever
        // idk how to do this?
    }
}

class DarkElf extends Elf
{
    public DarkElf(PlayerCharacter PC)
    {
        super(PC);
        PC.darkvisionRange = 120;
        PC.sunlightSensitivity = true;

        // you know dancing lights, faerie fire, and darkness
        PC.learnSpell("dancinglights");

        if (PC.getLevel() >= 3)
            PC.learnSpell("faeriefire");

        if (PC.getLevel() >= 5)
            PC.learnSpell("darkness");

        // sets rapier, shortsword, and hand crossbow proficiencies
        PC.getWeaponProfs().get(25).setProf();
        PC.getWeaponProfs().get(21).setProf();
        PC.getWeaponProfs().get(33).setProf();
    }
}

class Halfling extends Race
{
    public Halfling(PlayerCharacter PC)
    {
        super(PC);

        PC.speed = 25;
        PC.size = "Small";

        // lucky, reroll a 1
        // brave, advantage against being frightened
        // halfling nimbleness, can move through creatures larger than medium
    }
}

class LightfootHalfling extends Halfling
{
    public LightfootHalfling(PlayerCharacter PC)
    {
        super(PC);

        // naturally stealthy, can attempt to hide when obscured by a creature at least one size larger
    }
}

class StoutHalfling extends Halfling
{
    public StoutHalfling(PlayerCharacter PC)
    {
        super(PC);

        // stout resilience, sets poison damage resistance
        PC.damageTypes[9].resistant = true;
        PC.advantageAgainstPoison = true;
    }
}

class Harengon extends Race
{
    public Harengon(PlayerCharacter PC)
    {
        super(PC);

        // sets perception proficiency
        PC.getSkills().get(11).setProf();

        // when you fail a dex save, you're not prone, and your speed isn't 0, you can add a d4 to the roll

        // can jump 5 * prof as a bonus action without triggering opportunity attacks if speed > 0
            // can do this a number of times equal to prof bonus per long rest
    }
}

class Human extends Race
{
    public Human(PlayerCharacter PC)
    {
        super(PC);

        if (!(this instanceof VariantHuman))
        {
            // increase all stats by 1
        }
    }
}

class VariantHuman extends Human
{
    public VariantHuman(PlayerCharacter PC)
    {
        super(PC);

        // give one skill proficiency and one feat
    }
}