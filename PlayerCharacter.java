package ampersand;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class PlayerCharacter extends Character
{
    // ArrayList of all PCs, called in getPC
    public static ArrayList<PlayerCharacter> party = new ArrayList<>();

    // PC-specific instance variables
    private int level;
    public Race race;
    private String thisPCClass;
    private String armorWorn;
    private int bonusToAC;
    private int numHitDice2;
    private int typeHitDice2;
    private boolean wearingArmorWithoutProficiency; // can't cast spells and disadvantage on strength/dex checks
    private boolean armorStealthDisadvantage;
    boolean lightArmorProf;
    boolean mediumArmorProf;
    boolean heavyArmorProf;
    boolean shieldProf;
    private ArrayList<PCClass> classes = new ArrayList<>();
    private ArrayList<Skill> skills = new ArrayList<>();
    private ArrayList<Weapon> weaponProfs = new ArrayList<>();

    // how many max spell slots a character has based on classes
    // maxSpellSlots[0] remains 0 no matter what, cantrips/spells known are by class
    private int[] maxSpellSlots = new int[10];

    // PC-specific constructors
    public PlayerCharacter()
    {
        super("", defaultStats, 4, new String[]{"Common"});
    }
    public PlayerCharacter(String error)
    {
        super(error, defaultStats, 0, new String[]{""});
    }

    public PlayerCharacter(String name, int[] stats, int maxHP, String[] pcClassNLevel, String skillProfs,
                           String armorWorn, String[] languages)
    {
        super(name, stats, maxHP, languages); // sets name and stats in super, which sets mods inside it
        makeWeaponProfs(); // creates the list of weapon proficiencies, so they can be set to prof or not
        setClasses(pcClassNLevel); // input class2; class1
        setProficiency();
        setSkills(skillProfs);
        this.armorWorn = armorWorn;
        setArmorClass(armorWorn);
        setSpellsSlots();
        party.add(this); // adds PC to party ArrayList
    }

    private void setClasses(String[] arr)
    {
        StringBuilder thisClass = new StringBuilder();

        // start by setting all levels to 0
        for (int i = 0; i < PCClass.NUM_CLASSES; i++)
            makeClass(PCClass.whichClassReturnString(i), 0);

        // get the first class
        String class1 = arr[0].substring(0, arr[0].length() - 2);
        thisClass.append(class1);

        // adds level and makes class
        int level1 = StringMethods.getLevel(arr[0]);
        level += level1;
        class1 = StringMethods.removeSpaces(class1);
        makeClass(class1, level1);

        // sets saving throw, armor, and weapon proficiencies
        setSaves(class1);
        setWeaponAndArmorProfs(class1);

        // goes through any additional classes
        for (int i = 1; i < arr.length; i++)
        {
            String class2 = arr[i].substring(0, arr[i].length() - 2);
            thisClass.append(", ").append(class2);

            int level2 = StringMethods.getLevel(arr[i]);
            level += level2;
            class2 = StringMethods.removeSpaces(class2);
            makeClass(class2, level2);

            setWeaponAndArmorProfsMulticlass(class2);
        }

        thisPCClass = thisClass.toString();

        if (!super.getName().isEmpty())
            System.out.println(super.getName() + " created");
    }

    // get the class and subclass from the constructor and turn that into an actual class type
        // have the for loop in setClasses use whichClassReturnString() to figure out what object type and constructor to use
        // instead of just setting the name, set that certain constructor and object type
        // for the class the character is, set the level to higher than 0 like it does now
    // for subclasses, put those in place of the normal class in the array
    public void makeClass(String whatClass, int level)
    {
        try
        {
            // determine which class it'll become
            Class<?> whichClass = Class.forName("com.github.freshPressedOlive." + whatClass);

            // make the list of parameters that the constructor will take
            Class<?>[] parameters = new Class[2];
            parameters[0] = Integer.TYPE;
            parameters[1] = Class.forName("com.github.freshPressedOlive.PlayerCharacter");

            // make and call the constructor for that class
            Constructor<?> ct = whichClass.getConstructor(parameters);
            PCClass thisClass = (PCClass) ct.newInstance(level, this);

            setTypeHitDicePC(whatClass);
            setNumHitDicePC(level);

            // adds the class to the array for level 0 classes
            // the class the pc is will be replaced
            if (level == 0)
                classes.add(thisClass);

            // sets the class to the proper index in the classes ArrayList
            // it does use a lot of if and else if, but only 13
            // if it's an artificer subclass, it should be an instance of Artificer, etc.
            // set method replaces the current class, which is set to level 0
            if (thisClass instanceof Artificer)
                classes.set(PCClass.whichClassReturnInt("Artificer"), thisClass);
            else if (thisClass instanceof Barbarian)
                classes.set(PCClass.whichClassReturnInt("Barbarian"), thisClass);
            else if (thisClass instanceof Bard)
                classes.set(PCClass.whichClassReturnInt("Bard"), thisClass);
            else if (thisClass instanceof Cleric)
                classes.set(PCClass.whichClassReturnInt("Cleric"), thisClass);
            else if (thisClass instanceof Druid)
                classes.set(PCClass.whichClassReturnInt("Druid"), thisClass);
            else if (thisClass instanceof Fighter)
                classes.set(PCClass.whichClassReturnInt("Fighter"), thisClass);
            else if (thisClass instanceof Monk)
                classes.set(PCClass.whichClassReturnInt("Monk"), thisClass);
            else if (thisClass instanceof Paladin)
                classes.set(PCClass.whichClassReturnInt("Paladin"), thisClass);
            else if (thisClass instanceof Ranger)
                classes.set(PCClass.whichClassReturnInt("Ranger"), thisClass);
            else if (thisClass instanceof Rogue)
                classes.set(PCClass.whichClassReturnInt("Rogue"), thisClass);
            else if (thisClass instanceof Sorcerer)
                classes.set(PCClass.whichClassReturnInt("Sorcerer"), thisClass);
            else if (thisClass instanceof Warlock)
                classes.set(PCClass.whichClassReturnInt("Warlock"), thisClass);
            else if (thisClass instanceof Wizard)
                classes.set(PCClass.whichClassReturnInt("Wizard"), thisClass);
        }
        catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
               IllegalAccessException e)
        {
            System.out.println("Class not found!");
            throw new RuntimeException(e);
        }
    }

    // sets proficiency bonus based on level
    private void setProficiency()
    {
        if (level < 5)
            super.setProficiencyBonus(2);
        else if (level < 9)
            super.setProficiencyBonus(3);
        else if (level < 13)
            super.setProficiencyBonus(4);
        else if (level < 17)
            super.setProficiencyBonus(5);
        else
            super.setProficiencyBonus(6);
    }

    // sets skill proficiencies
    private void setSkills(String skillProfs)
    {
        for (int i = 0; i < Skill.NUM_SKILLS; i++)
        {
            // gets the name of the skill form the static method in the Skill Class
            String name = Skill.whichSkillReturnString(i);
            boolean isProf = false;
            boolean isExpert = false;

            // if the skill is in the String
            if (skillProfs.contains(name))
            {
                isProf = true; // is proficient

                // if there's a comma and the word expertise before said comma or if there's no comma and the word expertise is there
                if ((skillProfs.contains(",") && skillProfs.substring(0, skillProfs.indexOf(",")).contains("expertise"))
                        || (!skillProfs.contains(",") && skillProfs.contains("expertise")))
                    isExpert = true;

                // if there's a comma, trims it down
                if (skillProfs.contains(","))
                    skillProfs = skillProfs.substring(skillProfs.indexOf(",") + 1);
            }

            Skill skill = new Skill(i, isProf, isExpert); // creates skill Object
            skills.add(skill);
        }
    }

    // sets saving throw proficiencies using class
    private void setSaves(String theClass)
    {
        String class2 = theClass.toLowerCase();

        if (class2.equals("barbarian") || class2.equals("fighter") || class2.equals("ranger"))
            super.getMods().get(0).setSaveProf(true); // sets strength save
        if (class2.equals("bard") || class2.equals("monk") || class2.equals("ranger") || class2.equals("rogue"))
            super.getMods().get(1).setSaveProf(true); // sets dex save
        if (class2.equals("artificer") || class2.equals("barbarian") || class2.equals("fighter") || class2.equals("sorcerer"))
            super.getMods().get(2).setSaveProf(true); // sets con save
        if (class2.equals("artificer") || class2.equals("druid") || class2.equals("rogue") || class2.equals("wizard"))
            super.getMods().get(3).setSaveProf(true); // sets int save
        if (class2.equals("cleric") || class2.equals("druid") || class2.equals("paladin") || class2.equals("warlock") || class2.equals("wizard"))
            super.getMods().get(4).setSaveProf(true); // sets wis save
        if (class2.equals("bard") || class2.equals("cleric") || class2.equals("paladin") || class2.equals("sorcerer") || class2.equals("warlock"))
            super.getMods().get(5).setSaveProf(true); // sets cha save
    }

    // sets weapon and armor proficiencies
    private void setWeaponAndArmorProfs(String classes)
    {
        int i;

        switch (classes.toLowerCase())
        {
            case "barbarian":
            case "fighter":
            case "paladin":
            case "ranger":
                // iterate through all weapons in profs, mark as proficient if martial
                for (i = 0; i <= Weapon.NUM_WEAPONS; i++)
                    weaponProfs.get(i).setProf();

                break;
            case "bard":
            case "rogue":
                for (i = 0; i <= Weapon.NUM_SIMPLE_WEAPONS; i++)
                    weaponProfs.get(i).setProf(); // simple weapons

                weaponProfs.get(33).setProf(); // hand crossbow

                weaponProfs.get(21).setProf(); // longsword

                weaponProfs.get(25).setProf(); // rapier

                weaponProfs.get(27).setProf(); // shortsword

                break;
            case "monk":
                weaponProfs.get(27).setProf(); // shortsword
            case "artificer":
            case "cleric":
            case "warlock":
                for (i = 0; i <= Weapon.NUM_SIMPLE_WEAPONS; i++)
                    weaponProfs.get(i).setProf(); // simple weapons

                break;
            case "druid":
                weaponProfs.get(0).setProf(); // club

                weaponProfs.get(1).setProf(); // dagger

                weaponProfs.get(11).setProf(); // dart

                weaponProfs.get(4).setProf(); // javelin

                weaponProfs.get(6).setProf(); // mace

                weaponProfs.get(7).setProf(); // quarterstaff

                weaponProfs.get(26).setProf(); // scimitar

                weaponProfs.get(8).setProf(); // sickle

                weaponProfs.get(13).setProf(); // sling

                weaponProfs.get(9).setProf(); // spear

                break;
            case "sorcerer":
            case "wizard":
                weaponProfs.get(1).setProf(); // dagger

                weaponProfs.get(11).setProf(); // dart

                weaponProfs.get(13).setProf(); // sling

                weaponProfs.get(7).setProf(); // quarterstaff

                weaponProfs.get(10).setProf(); // light crossbow

                break;
        }

        switch (classes.toLowerCase())
        {
            case "fighter":
            case "paladin":
                heavyArmorProf = true;
            case "artificer":
            case "barbarian":
            case "cleric":
            case "druid":
            case "ranger":
                mediumArmorProf = true;
                shieldProf = true;
            case "bard":
            case "rogue":
            case "warlock":
                lightArmorProf = true;
        }
    }

    private void setWeaponAndArmorProfsMulticlass(String classes)
    {
        int i;

        switch (classes.toLowerCase())
        {
            case "barbarian":
            case "fighter":
            case "paladin":
            case "ranger":
                // iterate through all weapons in profs, mark as proficient if martial
                for (i = 0; i <= Weapon.NUM_WEAPONS; i++)
                    weaponProfs.get(i).setProf();
                break;
            case "monk":
                weaponProfs.get(27).setProf(); // shortsword
            case "warlock":
                for (i = 0; i <= Weapon.NUM_SIMPLE_WEAPONS; i++)
                    weaponProfs.get(i).setProf(); // simple weapons
                break;
        }

        switch (classes.toLowerCase())
        {
            case "barbarian":
                shieldProf = true;
                break;
            case "artificer":
            case "cleric":
            case "druid":
            case "fighter":
            case "paladin":
            case "ranger":
                mediumArmorProf = true;
                shieldProf = true;
            case "bard":
            case "rogue":
            case "warlock":
                lightArmorProf = true;
        }
    }

    public void setArmorClass(String armorWorn)
    {
        // if wearing no armor or just a shield, check for unarmored defense or leave as default
        if (armorWorn.equals("none") || armorWorn.isBlank() || armorWorn.equals("shield"))
        {
            if (getBarbarian().unarmoredDefense)
            {
                // if not wearing anything
                if (armorWorn.equals("none") || getArmorWorn().isBlank())
                    setArmorClass(10 + getMod(1) + getMod(2)); // AC = 10 + dex + con
                // barbarian allows this benefit with a shield
                else if (getArmorWorn().equalsIgnoreCase("shield"))
                    setArmorClass(10 + getMod(1) + getMod(2) + 2); // AC = 10 + dex + con + shield
            }
            // monk requires no shield
            else if (getMonk().unarmoredDefense && !armorWorn.contains("shield"))
                    setArmorClass(10 + getMod(1) + getMod(3)); // AC = 10 + dex + wis
            return;
        }

        int newAC;

        for (Armor currentArmor: Armor.allArmors)
        {
            if (armorWorn.contains(currentArmor.name))
            {
                // prevents it thinking studded leather is just leather because studded leather contains leather
                if (armorWorn.contains("studded") && currentArmor.name.equals("leather"))
                    continue;

                // set the AC
                newAC = currentArmor.baseAC;
                if (currentArmor.proficiencyType.equals("light") || currentArmor.proficiencyType.equals("other"))
                    newAC += getMod("dex");
                else if (currentArmor.proficiencyType.equals("medium"))
                    newAC += Math.min(getMod("dex"), 2);

                setArmorClass(newAC);

                // set disadvantages and stuff
                if ((currentArmor.proficiencyType.equals("light") && !lightArmorProf)
                        || (currentArmor.proficiencyType.equals("medium") && !mediumArmorProf)
                        || (currentArmor.proficiencyType.equals("heavy") && !heavyArmorProf))
                    wearingArmorWithoutProficiency = true;
                if (currentArmor.stealthDisadvantage)
                    armorStealthDisadvantage = true;
                if (currentArmor.strengthRequirement > stats[0] && !(race instanceof Dwarf))
                    speed -= 10;

                // prevents it thinking breastplate is plate
                break;
            }
        }

        if (armorWorn.contains("shield"))
        {
            increaseArmorClass(2);

            // without shield proficiency or a certain feat, which Anashu has
            if (!shieldProf && !getName().equalsIgnoreCase("Anashu"))
                wearingArmorWithoutProficiency = true;

        }

        if (armorWorn.contains("+1"))
        {
            increaseArmorClass(1);
        }
        // in a future setClassFeatures method, determine whether the armor is a class-dependent armors like unarmored or draconic ancestry
                // armorWorn should be draconic ancestry if that method says so and armorWorn is still blank
            // for now, it should simply take in those armor types as normal armor types, written in the constructor
        // defense fighting style is determined in the setClassFeatures method and put in a bonusToAC field that's added in at the end of this method
            // draconic ancestry is an armor type of 13 + dex mod, while unarmored defense is a + con in the bonusToAC field
                // both dependent on having none as armorWorn type
    }

    public void setSpellsSlots()
    {
        int level = 0;

        // iterates through a PC's classes to figure out what level of spell slots it is
        for (PCClass current : classes)
        {
            // for full casters, take the full level
            if (current instanceof Bard || current instanceof Cleric || current instanceof Druid
                    || current instanceof Sorcerer || current instanceof Wizard)
                level += current.level;
            // for half casters, take half rounded down
            else if (current instanceof Paladin || current instanceof Ranger)
                level += current.level / 2;
            // for artificer, take half but round up
            else if (current instanceof Artificer)
                level += Math.ceil(current.level / 2.0);
            // for eldritch knight or arcane trickster, take one third rounded down
            else if (current instanceof EldritchKnight || current instanceof ArcaneTrickster)
                level += current.level / 3;
            // for warlock, add spell slots of the appropriate level to the array
            else if (current instanceof Warlock)
            {
                int numSpellSlots = 1;
                if (current.level > 1)
                    numSpellSlots = 2;
                if (current.level > 10)
                    numSpellSlots = 3;
                if (current.level > 16)
                    numSpellSlots = 4;

                int spellLevel = 1;
                if (current.level > 2)
                    spellLevel = 2;
                if (current.level > 4)
                    spellLevel = 3;
                if (current.level > 6)
                    spellLevel = 4;
                if (current.level > 8)
                    spellLevel = 5;

                maxSpellSlots[spellLevel] += numSpellSlots;
            }
        }

        switch (level)
        {
            case 20:
                maxSpellSlots[7]++;
            case 19:
                maxSpellSlots[6]++;
            case 18:
                maxSpellSlots[5]++;
            case 17:
                maxSpellSlots[9]++;
            case 16:

            case 15:
                maxSpellSlots[8]++;
            case 14:

            case 13:
                maxSpellSlots[7]++;
            case 12:

            case 11:
                maxSpellSlots[6]++;
            case 10:
                maxSpellSlots[5]++;
            case 9:
                maxSpellSlots[4]++;
                maxSpellSlots[5]++;
            case 8:
                maxSpellSlots[4]++;
            case 7:
                maxSpellSlots[4]++;
            case 6:
                maxSpellSlots[3]++;
            case 5:
                maxSpellSlots[3] += 2;
            case 4:
                maxSpellSlots[2]++;
            case 3:
                maxSpellSlots[1]++;
                maxSpellSlots[2] += 2;
            case 2:
                maxSpellSlots[1]++;
            case 1:
                maxSpellSlots[1] += 2;
        }
    }

    // sets a character's race using reflection
    // the parameters vary per race depending on requirements, so maybe use the array and get whatever constructor is array[0]
    private void setRace(String theRace)
    {
        // remove the spaces so the reflection works
        theRace = StringMethods.removeSpaces(theRace);

        try
        {
            // determine which class it'll become
            Class<?> whichRace = Class.forName("com.github.freshPressedOlive." + theRace);

            // make the list of parameters that the constructor will take
            Class<?>[] parameters = new Class[1];
            parameters[0] = Class.forName("com.github.freshPressedOlive.PlayerCharacter");

            // make and call the constructor for that class
            Constructor<?> ct = whichRace.getConstructor(parameters);
            PCClass thisClass = (PCClass) ct.newInstance(this);

        }
        catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
               IllegalAccessException e)
        {
            System.out.println("Race not found!");
        }
    }

    // sets the type of hit dice based on the class
    public void setTypeHitDicePC(String theClass)
    {
        setTypeHitDice(PCClass.getTypeHitDiceClass(theClass));
    }
    // sets the type of hit dice for the second class
    public void setTypeHitDicePC2(String theClass)
    {
        typeHitDice2 = PCClass.getTypeHitDiceClass(theClass);
    }

    // sets the number of hit dice based on level
    public void setNumHitDicePC(int level)
    {
        setNumHitDice(level);
    }
    // sets the number of hit dice for the second class
    public void setNumHitDicePC2(int level)
    {
        numHitDice2 = level;
    }


    public String getPCClass()
    {
        return thisPCClass;
    }
    public int getLevel()
    {
        return level;
    }
    public ArrayList<PCClass> getClasses()
    {
        return classes;
    }
    public ArrayList<Skill> getSkills()
    {
        return skills;
    }
    public ArrayList<Weapon> getWeaponProfs()
    {
        return weaponProfs;
    }
    public String getArmorWorn()
    {
        return armorWorn;
    }
    public boolean isWearingArmorWithoutProficiency()
    {
        return this.wearingArmorWithoutProficiency;
    }
    public boolean hasArmorStealthDisadvantage()
    {
        return armorStealthDisadvantage;
    }
    public int getNumHitDice2()
    {
        return numHitDice2;
    }
    public int getTypeHitDice2()
    {
        return typeHitDice2;
    }

    public int[] getMaxSpellSlots()
    {
        return this.maxSpellSlots;
    }

    public Race getRace()
    {
        return this.race;
    }


    // casts a spell by finding the function for it
    public String castSpellPC(String spellName, String extraInfo)
    {
        System.out.println("in the PC version of spellcasting!");

        boolean cantLearnSpells = true;

        ArrayList<Class<?>> casterClasses = new ArrayList<>();

        boolean knowsSpells = false;
        boolean preparesSpells = false;
        boolean isWizard = false;

        //System.out.println(spellName);

        // look through a PC's classes
        for (PCClass theClass : classes)
        {
            // if it's a caster one has a level in
            if (theClass instanceof Caster && theClass.level > 0)
            {
                cantLearnSpells = false;
                casterClasses.add(theClass.getClass());

                // iterate through spells available and return on a success
                String attempt = findSpell(((Caster)theClass).spellsAvailable, spellName, extraInfo, theClass);
                if (attempt != null)
                    return attempt;

                if (theClass instanceof Artificer || theClass instanceof Cleric || theClass instanceof Druid
                || theClass instanceof Paladin)
                    preparesSpells = true;
                else if (theClass instanceof Bard || theClass instanceof Ranger || theClass instanceof Sorcerer
                        || theClass instanceof Warlock)
                    knowsSpells = true;

                // looks through spells known to see if it's known but not prepared
                if (theClass instanceof Wizard)
                {
                    preparesSpells = knowsSpells = true;

                    for (int i = 0; i < ((Wizard)theClass).spellsKnown.size(); i++)
                        if (spellName.equalsIgnoreCase(((Wizard)theClass).spellsKnown.get(i).id))
                            return "The spell " + ((Wizard)theClass).spellsKnown.get(i).name + " is known but not prepared";
                }
            }
        }

        if (cantLearnSpells)
            return "You don't have levels in a class that can learn spells!";

        boolean spellExists = false;
        boolean inSpellList = false;

        // checks if the spell is real
        // if it is, it should also do some stuff about whether your class(es) can learn it or not
        for (Spell theSpell : Spell.allSpells)
            if (theSpell.id.equalsIgnoreCase(spellName))
            {
                spellExists = true;

                for (Class<?> c : casterClasses)
                {
                    if ((c == Artificer.class || c.getSuperclass() == Artificer.class) && theSpell.spellLists[0])
                        inSpellList = true;
                    else if ((c == Bard.class || c.getSuperclass() == Bard.class) && theSpell.spellLists[2])
                        inSpellList = true;
                    else if ((c == Cleric.class || c.getSuperclass() == Cleric.class) && theSpell.spellLists[3])
                        inSpellList = true;
                    else if ((c == Druid.class || c.getSuperclass() == Druid.class) && theSpell.spellLists[4])
                        inSpellList = true;
                    else if ((c == Paladin.class || c.getSuperclass() == Paladin.class) && theSpell.spellLists[7])
                        inSpellList = true;
                    else if ((c == Ranger.class || c.getSuperclass() == Ranger.class) && theSpell.spellLists[8])
                        inSpellList = true;
                    else if ((c == Sorcerer.class || c.getSuperclass() == Sorcerer.class) && theSpell.spellLists[10])
                        inSpellList = true;
                    else if ((c == Warlock.class || c.getSuperclass() == Warlock.class) && theSpell.spellLists[11])
                        inSpellList = true;
                    else if ((c == Wizard.class || c.getSuperclass() == Wizard.class) && theSpell.spellLists[12])
                        inSpellList = true;
                }

                break;
            }

        if (!spellExists)
            return "The spell you're trying to cast doesn't exist";

        if (!inSpellList)
            return "This spell isn't in your class' spell list";
        else if (preparesSpells && knowsSpells)
            return "You don't have this spell either known or prepared";
        else if (preparesSpells)
            return "You don't have this spell prepared";
        else if (knowsSpells)
            return "You don't know this spell";


        // now look through all spells to see if it's valid, if not give a formatting direction
        // if it is, say it's not known/prepared based on the class
            // maybe use some flags to find which class or if any classes are casters or anything

        // look through the PC's classes and see if the spell can be cast
            // if not, return a message saying the spell is not known/prepared
                // if wizard, send message in case of known but not prepared
                // send a message if it's not a valid spell and give a format:
                    // &cast id other data
                    // id is the name of the spell, all one lowercase word to identify the spell
            // if so, call a findSpell function in Spell that uses reflection and determines the right function
                // these functions will need a Character as a parameter, most likely
        return "A PC is casting a spell!";
    }



    // these methods return the PCClass object at the proper index for that class
    // made to avoid weird typecasting and repeated code for dice-rolling methods and the like
    public Artificer getArtificer()
    {
        return (Artificer) classes.get(PCClass.whichClassReturnInt("artificer"));
    }

    public Barbarian getBarbarian()
    {
        return (Barbarian) classes.get(PCClass.whichClassReturnInt("barbarian"));
    }

    public Bard getBard()
    {
        return (Bard) classes.get(PCClass.whichClassReturnInt("bard"));
    }

    public Cleric getCleric()
    {
        return (Cleric) classes.get(PCClass.whichClassReturnInt("cleric"));
    }

    public Druid getDruid()
    {
        return (Druid) classes.get(PCClass.whichClassReturnInt("druid"));
    }

    public Fighter getFighter()
    {
        return (Fighter) classes.get(PCClass.whichClassReturnInt("fighter"));
    }

    public Monk getMonk()
    {
        return (Monk) classes.get(PCClass.whichClassReturnInt("monk"));
    }

    public Paladin getPaladin()
    {
        return (Paladin) classes.get(PCClass.whichClassReturnInt("paladin"));
    }

    public Ranger getRanger()
    {
        return (Ranger) classes.get(PCClass.whichClassReturnInt("ranger"));
    }

    public Rogue getRogue()
    {
        return (Rogue) classes.get(PCClass.whichClassReturnInt("rogue"));
    }

    public Sorcerer getSorcerer()
    {
        return (Sorcerer) classes.get(PCClass.whichClassReturnInt("sorcerer"));
    }

    public Warlock getWarlock()
    {
        return (Warlock) classes.get(PCClass.whichClassReturnInt("warlock"));
    }

    public Wizard getWizard()
    {
        return (Wizard) classes.get(PCClass.whichClassReturnInt("wizard"));
    }


    private void makeWeaponProfs()
    {
        // simple melee weapons
        Weapon club = new Weapon("club", 0.1, 4, "bludgeoning", 2, false, true, "light");
        weaponProfs.add(club); // 0

        Weapon dagger = new Weapon("dagger", 2, 4, "piercing", 1, false, true, "finesse, light, thrown");
        dagger.setRange(20, 60);
        weaponProfs.add(dagger); // 1

        Weapon greatclub = new Weapon("greatclub", 0.2, 8, "bludgeoning", 10, false, true, "two-handed");
        weaponProfs.add(greatclub); // 2

        Weapon handaxe = new Weapon("handaxe", 5, 6, "slashing", 2, false, true, "light, thrown");
        handaxe.setRange(20, 60);
        weaponProfs.add(handaxe); // 3

        Weapon javelin = new Weapon("javelin", 0.5, 6, "piercing", 2, false, true, "thrown");
        javelin.setRange(30, 120);
        weaponProfs.add(javelin); // 4

        Weapon lightHammer = new Weapon("light hammer", 2, 4, "bludgeoning", 2, false, true, "light, thrown");
        weaponProfs.add(lightHammer); // 5

        Weapon mace = new Weapon("mace", 5, 6, "bludgeoning", 4, false, true, "");
        weaponProfs.add(mace); // 6

        Weapon quarterstaff = new Weapon("quarterstaff", 2, 6, "bludgeoning", 4, false, true, "versatile (1d8)");
        weaponProfs.add(quarterstaff); // 7

        Weapon sickle = new Weapon("sickle", 1, 4, "slashing", 2, false, true, "light");
        weaponProfs.add(sickle); // 8

        Weapon spear = new Weapon("spear", 1, 6, "piercing", 3, false, true, "thrown, versatile (1d8)");
        spear.setRange(20, 60);
        weaponProfs.add(spear); // 9


        //simple ranged weapons
        Weapon lightCrossbow = new Weapon("light crossbow", 25, 8, "piercing", 5, true, true, "ammunition, loading, two-handed");
        lightCrossbow.setRange(80, 320);
        weaponProfs.add(lightCrossbow); // 10

        Weapon dart = new Weapon("dart", 0.05, 4, "piercing", 1, true, true, "finesse, thrown");
        dart.setRange(20, 60);
        weaponProfs.add(dart); // 11

        Weapon shortbow = new Weapon("shortbow", 25, 6, "piercing", 2, true, true, "ammunition, two-handed");
        shortbow.setRange(80, 320);
        weaponProfs.add(shortbow); // 12

        Weapon sling = new Weapon("sling", 0.1, 4, "bludgeoning", 0, true, true, "ammunition");
        sling.setRange(30, 120);
        weaponProfs.add(sling); // 13


        // martial melee weapons
        Weapon battleaxe = new Weapon("battleaxe", 10, 8, "slashing", 4, false, false, "versatile (1d10)");
        weaponProfs.add(battleaxe); // 14

        Weapon flail = new Weapon("flail", 10, 8, "bludgeoning", 2, false, false, "");
        weaponProfs.add(flail); // 15

        Weapon glaive = new Weapon("glaive", 20, 10, "slashing", 6, false, false, "heavy, reach, two-handed");
        weaponProfs.add(glaive); // 16

        Weapon greataxe = new Weapon("greataxe", 30, 12, "slashing", 6, false, false, "heavy, two-handed");
        weaponProfs.add(greataxe); // 17

        Weapon greatsword = new Weapon("greatsword", 50, 6, "slashing", 6, false, false, "heavy, two-handed");
        greatsword.setNumDice();
        weaponProfs.add(greatsword); // 18

        Weapon halberd = new Weapon("halberd", 20, 10, "slashing", 6, false, false, "heavy, reach, two-handed");
        weaponProfs.add(halberd); // 19

        Weapon lance = new Weapon("lance", 10, 12, "piercing", 6, false, false, "reach, special");
        // do something to make disadvantage if within 5 feet and two-handed if not mounted
        weaponProfs.add(lance); // 20

        Weapon longsword = new Weapon("longsword", 15, 8, "slashing", 3, false, false, "versatile (1d10)");
        weaponProfs.add(longsword); // 21

        Weapon maul = new Weapon("maul", 10, 6, "bludgeoning", 10, false, false, "heavy, two-handed");
        maul.setNumDice();
        weaponProfs.add(maul); // 22

        Weapon morningstar = new Weapon("morningstar", 15, 8, "piercing", 4, false, false, "");
        weaponProfs.add(morningstar); // 23

        Weapon pike = new Weapon("pike", 5, 10, "piercing", 18, false, false, "heavy, reach, two-handed");
        weaponProfs.add(pike); // 24

        Weapon rapier = new Weapon("rapier", 25, 8, "piercing", 2, false, false, "finesse");
        weaponProfs.add(rapier); // 25

        Weapon scimitar = new Weapon("scimitar", 25, 6, "slashing", 3, false, false, "finesse, light");
        weaponProfs.add(scimitar); // 26

        Weapon shortsword = new Weapon("shortsword", 10, 6, "piercing", 2, false, false, "finesse, light");
        weaponProfs.add(shortsword); // 27

        Weapon trident = new Weapon("trident", 5, 6, "piercing", 4, false, false, "thrown, versatile (1d8)");
        trident.setRange(20, 60);
        weaponProfs.add(trident); // 28

        Weapon warPick = new Weapon("war pick", 5, 8, "piercing", 2, false, false, "");
        weaponProfs.add(warPick); // 29

        Weapon warhammer = new Weapon("warhammer", 15, 8, "bludgeoning", 2, false, false, "versatile (1d10)");
        weaponProfs.add(warhammer); // 30

        Weapon whip = new Weapon("whip", 2, 4, "slashing", 3, false, false, "finesse, reach");
        weaponProfs.add(whip); // 31


        // martial ranged weapons
        Weapon blowgun = new Weapon("blowgun", 10, 1, "piercing", 1, true, false, "ammunition, loading");
        blowgun.setRange(25, 100);
        weaponProfs.add(blowgun); // 32

        Weapon handCrossbow = new Weapon("hand crossbow", 75, 6, "piercing", 3, true, false, "ammunition, light, loading");
        handCrossbow.setRange(30, 120);
        weaponProfs.add(handCrossbow); // 33

        Weapon heavyCrossbow = new Weapon("heavy crossbow", 50, 10, "piercing", 18, true, false, "ammunition, heavy, loading, two-handed");
        heavyCrossbow.setRange(100, 400);
        weaponProfs.add(heavyCrossbow); // 34

        Weapon longbow = new Weapon("longbow", 50, 8, "piercing", 2, true, false, "ammunition, heavy, two-handed");
        longbow.setRange(150, 600);
        weaponProfs.add(longbow); // 35

        Weapon net = new Weapon("net", 1, 0, "", 3, true, false, "special, thrown");
        net.setRange(5, 15);
        weaponProfs.add(net); // 36

        Weapon unarmedStrikes = new Weapon("unarmed", 0, 1, "bludgeoning", 0, false, true, "");
        unarmedStrikes.setProf();
        weaponProfs.add(unarmedStrikes); // 37, but everyone is proficient already
    }
}

// the old version of determining classes, when it was just one String
//    // class1 is before the semicolon and is the first class you got a level in for multiclass purposes
//    // class2 is after and is any secondary multiclass
//    // currently only allows for two classes
//    private void setClassesOld(String pcClassNLevel)
//    {
//        level = 0;
//        int level1;
//        int level2;
//        String class1;
//        String class2 = "";
//
//        // the place where the last level is
//        String levelPlacement = pcClassNLevel.substring(pcClassNLevel.length() - 2);
//
//        // sets all the classes to 0
//        for (int i = 0; i < PCClass.NUM_CLASSES; i++)
//            makeClass(PCClass.whichClassReturnString(i), 0);
//
//        // determines what classes to put in the array
//        // if it's a multiclass, it contains a semicolon
//        if (pcClassNLevel.contains((";")))
//        {
//            class1 = pcClassNLevel.substring(0, pcClassNLevel.indexOf(";") - 2); // sets class before ;
//            class2 = pcClassNLevel.substring(pcClassNLevel.indexOf(";") + 2, pcClassNLevel.length() - 2); // sets class after ;
//            thisPCClass = class1 + ", " + class2;
//
//            // adds level before ;
//            // keeps in mind double-digit levels
//            level1 = Integer.parseInt(StringMethods.removeSpaces(pcClassNLevel.substring(pcClassNLevel.indexOf(";") - 2, pcClassNLevel.indexOf(";"))));
//            level += level1;
//            class1 = StringMethods.removeSpaces(class1);
//            makeClass(class1, level1);
//
//            // adds level after ;
//            level2 = Integer.parseInt(StringMethods.removeSpaces(levelPlacement));
//            level += level2;
//            class2 = StringMethods.removeSpaces(class2);
//            makeClass(class2, level2);
//        }
//        // sets class if no multiclass
//        else
//        {
//            class1 = pcClassNLevel.substring(0, pcClassNLevel.length() - 2);
//            thisPCClass = class1;
//
//            // adds level and makes class
//            level1 = StringMethods.getLevel(pcClassNLevel);
//            level += level1;
//            class1 = StringMethods.removeSpaces(class1);
//            makeClass(class1, level1);
//        }
//
//        setSaves(class1); // sets saving throw proficiencies
//        setWeaponAndArmorProfs(class1);
//
//        if (!class2.isEmpty())
//            setWeaponAndArmorProfsMulticlass(class2);
//
//        // since Joe is our default name, we don't want it printing every time getPC is called
//        if (!super.getName().equals("Joe"))
//            System.out.println(super.getName() + " created"); // prints that the character has been made
//    }