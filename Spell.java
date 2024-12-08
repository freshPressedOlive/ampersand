package ampersand;

import java.lang.reflect.*;
import java.util.ArrayList;

import static java.lang.Class.forName;

public abstract class Spell
{
    public String name;
    // id is what a player will type in to identify a spell: all lowercase, no spaces or punctuation
    public String id;
    public String source;
    public int level;
    public String school;
    public boolean ritual;
    public String castingTime;
    public String range;

    public boolean verbal;
    public boolean somatic;
    public boolean material;
    public String materialComponents;

    public boolean concentration;
    public String duration; // rounds?

    public String description;

    // some form of scaling?

    public boolean[] spellLists = new boolean[13];

    public Character caster;

    public Method spellFunction;

    public int castsPerDay = -1;

    // maybe add a class that it was learned in?

    public static ArrayList<Spell> allSpells = new ArrayList<>();
    public static ArrayList<Spell> artificerSpells = new ArrayList<>();
    public static ArrayList<Spell> bardSpells = new ArrayList<>();
    public static ArrayList<Spell> clericSpells = new ArrayList<>();
    public static ArrayList<Spell> druidSpells = new ArrayList<>();
    public static ArrayList<Spell> paladinSpells = new ArrayList<>();
    public static ArrayList<Spell> rangerSpells = new ArrayList<>();
    public static ArrayList<Spell> sorcererSpells = new ArrayList<>();
    public static ArrayList<Spell> warlockSpells = new ArrayList<>();
    public static ArrayList<Spell> wizardSpells = new ArrayList<>();

    public static Class<?>[] defaultParameters;

    static
    {
        try
        {
            defaultParameters = new Class<?>[]{forName("com.github.freshPressedOlive.PlayerCharacter"), String.class};
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Couldn't establish default method parameters for spells!");
        }
    }

    public Spell()
    {

    }

    public Spell(String name, String source, int level, String school, boolean ritual, String castingTime, String range,
                 boolean verbal, boolean somatic, boolean material, String materialComponents, boolean concentration,
                 String duration, String description, String[] spellLists, Class<?>[] functionParameters)
    {
        this.name = name;
        this.id = StringMethods.removePunctuation(StringMethods.removeSpaces(name.toLowerCase()));
        this.source = source;
        this.level = level;
        this.school = school;
        this.ritual = ritual;
        this.concentration = concentration;
        this.castingTime = castingTime;
        this.range = range;
        this.verbal = verbal;
        this.somatic = somatic;
        this.material = material;
        this.duration = duration;
        this.description = description;
        this.materialComponents = materialComponents;

        // functionParameters is what type of parameters its function will take
        // if null, it'll do the default, which is a PC (the caster) and a String
            // if it's anything else, including if it's less than that, it'll store those
        try
        {
            if (functionParameters == null)
                this.spellFunction = this.getClass().getMethod(this.id, defaultParameters);
            else
                this.spellFunction = this.getClass().getMethod(this.id, functionParameters);
            System.out.println("spell function found for cantrip " + this.name);
        }
        catch (NoSuchMethodException e)
        {
            System.out.println("spell function not found for cantrip " + this.name);
        }

        setSpellLists(spellLists);

        this.addToStaticLists();
    }

    // sets which spell lists a spell is part of
    public void setSpellLists(String[] spellLists)
    {
        for (String spellList : spellLists) {
            if (spellList.equalsIgnoreCase("artificer"))
                this.spellLists[0] = true;
            else if (spellList.equalsIgnoreCase("bard"))
                this.spellLists[2] = true;
            else if (spellList.equalsIgnoreCase("cleric"))
                this.spellLists[3] = true;
            else if (spellList.equalsIgnoreCase("druid"))
                this.spellLists[4] = true;
            else if (spellList.equalsIgnoreCase("paladin"))
                this.spellLists[7] = true;
            else if (spellList.equalsIgnoreCase("ranger"))
                this.spellLists[8] = true;
            else if (spellList.equalsIgnoreCase("sorcerer"))
                this.spellLists[10] = true;
            else if (spellList.equalsIgnoreCase("warlock"))
                this.spellLists[11] = true;
            else if (spellList.equalsIgnoreCase("wizard"))
                this.spellLists[12] = true;
        }
    }

    // adds a spell to all of the static general compendium lists
    public void addToStaticLists()
    {
        allSpells.add(this);

        if (spellLists[0])
            artificerSpells.add(this);
        if (spellLists[2])
            bardSpells.add(this);
        if (spellLists[3])
            clericSpells.add(this);
        if (spellLists[4])
            druidSpells.add(this);
        if (spellLists[7])
            paladinSpells.add(this);
        if (spellLists[8])
            rangerSpells.add(this);
        if (spellLists[10])
            sorcererSpells.add(this);
        if (spellLists[11])
            warlockSpells.add(this);
        if (spellLists[12])
            wizardSpells.add(this);
    }

    public static Spell findSpell(String spellName)
    {
        for (Spell s : allSpells)
            if (s.id.equalsIgnoreCase(spellName) || s.name.equalsIgnoreCase(spellName))
                return s;

        return null;
    }

    public static void createAllSpells()
    {
        Cantrip.createAllCantrips();
    }
}

class Cantrip extends Spell
{
    public Cantrip(String name, String source, int level, String school, boolean ritual, String castingTime, String range,
                   boolean verbal, boolean somatic, boolean material, String materialComponents, boolean concentration,
                   String duration, String description, String[] spellLists, Class<?>[] functionParameters)
    {
        super(name, source, level, school, false, castingTime, range, verbal, somatic, material, materialComponents,
                concentration, duration, description, spellLists, functionParameters);

        if (ritual)
            System.out.println("no cantrip can be a ritual, bozo");
    }

    public static void createAllCantrips()
    {
        Spell acidSplash = new Cantrip("Acid Splash", "Player's Handbook", 0, "conjuration",
                false, "1 action", "60 feet", true, true, false,
                "", false, "Instantaneous",
                "You hurl a bubble of acid. Choose one creature you can see within range, or " +
                        "choose two creatures you can see within range that are within 5 feet of each other. A target " +
                        "must succeed on a Dexterity saving throw or take 1d6 acid damage." +
                        "\nAt Higher Levels. This spell’s damage increases by 1d6 when you reach 5th level (2d6), " +
                        "11th level (3d6), and 17th level (4d6).", new String[]{"Artificer", "Sorcerer", "Wizard"}, null);

        Spell bladeWard = new Cantrip("Blade Ward", "Player's Handbook", 0, "abjuration",
                false, "1 action", "Self", true, true, false,
                "", false, "1 round", "You extend your hand and trace" +
                "a sigil of warding in the air. Until the end of your next turn, you have resistance against " +
                "bludgeoning, piercing, and slashing damage dealt by weapon attacks.", new String[]{"Bard", "Sorcerer",
                "Warlock", "Wizard"}, new Class[]{PlayerCharacter.class});

        Spell boomingBlade = new Cantrip("Booming Blade", "Tasha's Cauldron of Everything", 0,
                "evocation", false, "1 action", "Self (5-foot radius)", false,
                true, true, "a melee weapon worth at least 1 sp", false,
                "1 round", "You brandish the weapon used in the spell’s casting and make a melee " +
                "attack with it against one creature within 5 feet of you. On a hit, the target suffers the weapon " +
                "attack’s normal effects and then becomes sheathed in booming energy until the start of your next " +
                "turn. If the target willingly moves 5 feet or more before then, the target takes 1d8 thunder damage, " +
                "and the spell ends." +
                "\nAt Higher Levels. At 5th level, the melee attack deals an extra 1d8 thunder damage to the target on " +
                "a hit, and the damage the target takes for moving increases to 2d8. Both damage rolls increase by 1d8 " +
                "at 11th level (2d8 and 3d8) and again at 17th level (3d8 and 4d8).", new String[]{"Artificer",
                "Sorcerer", "Warlock", "Wizard"}, null);

        Spell chillTouch = new Cantrip("Chill Touch", "Player's Handbook", 0, "necromancy",
                false, "1 action", "120 feet", true, true, false,
                "", false, "1 round", "You create a ghostly, skeletal " +
                "hand in the space of a creature within range. Make a ranged spell attack against the creature to assail " +
                "it with the chill of the grave. On a hit, the target takes 1d8 necrotic damage, and it can’t regain hit " +
                "points until the start of your next turn. Until then, the hand clings to the target. If you hit an " +
                "undead target, it also has disadvantage on attack rolls against you until the end of your next turn." +
                "\nAt Higher Levels. This spell’s damage increases by 1d8 when you reach 5th level (2d8), 11th " +
                "level (3d8), and 17th level (4d8).", new String[]{"Sorcerer", "Warlock", "Wizard"}, null);

        Spell controlFlames = new Cantrip("Control Flames", "Xanathar's Guide to Everything", 0,
                "transmutation", false, "1 action", "60 feet", false, true,
                false, "", false, "Instantaneous or 1 hour",
                "You choose nonmagical flame that you can see within range and that fits within a 5-foot" +
                        "cube. You affect it in one of the following ways:\n You instantaneously expand the flame" +
                        "5 feet in one direction, provided that wood or other" +
                        "fuel is present in the new location.\n You instantaneously extinguish the flames within the" +
                        "cube.\n You double or halve the area of bright light and dim light cast by the flame, change" +
                        "its color, or both. The change lasts for 1 hour.\n You cause simple shapes—such as the vague" +
                        "form of a creature, an inanimate object, or a location—to appear within the flames and" +
                        "animate as you like. The shapes last for 1 hour.\n If you cast this spell multiple times," +
                        "you can have up to three of its non-instantaneous effects active at a time, and you can" +
                        "dismiss such an effect as an action.", new String[]{"Druid", "Sorcerer", "Wizard"}, null);

        Spell createBonfire = new Cantrip("Create Bonfire", "Xanathar's Guide to Everything", 0,
                "conjuration", false, "1 action", "60 feet", true, true,
                false, "", true, "up to 1 minute", "You create a " +
                "bonfire on ground that you can see within range. Until the spell ends, the bonfire fills a 5-foot" +
                " cube. Any creature in the bonfire’s space when you cast the spell must succeed on a Dexterity saving" +
                " throw or take 1d8 fire damage. A creature must also make the saving throw when it enters the bonfire’s" +
                " space for the first time on a turn or ends its turn there." +
                "\nAt Higher Levels. The spell’s damage increases by 1d8 when you reach 5th level (2d8), 11th level" +
                " (3d8), and 17th level (4d8).", new String[]{"Artificer", "Druid", "Sorcerer", "Warlock", "Wizard"},
                null);
    }

    // the actual function when one casts Acid Splash
    // can call a "damage()" method in Spell
    public static String acidsplash(PlayerCharacter caster, String extraInfo)
    {
        // extra info is the target
        return caster.getName() + " is casting Acid Splash!";
    }

    public static String bladeward(PlayerCharacter caster)
    {
        return caster.getName() + " is casting Blade Ward!";
    }

    public static String boomingblade(PlayerCharacter caster, String extraInfo)
    {
        // extra info is the target
        return caster.getName() + " is casting Booming Blade!";
    }

    public static String chilltouch(PlayerCharacter caster, String extraInfo)
    {
        // extra info is the target
        return caster.getName() + " is casting Chill Touch!";
    }

    public static String controlflames(PlayerCharacter caster, String extraInfo)
    {
        // extra info is description of effect
        return caster.getName() + " is casting Control Flames!";
    }

    public static String createbonfire(PlayerCharacter caster, String extraInfo)
    {
        // extra info is the target
        return caster.getName() + " is casting Create Bonfire!";
    }
}