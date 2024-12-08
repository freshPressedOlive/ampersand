package ampersand;

import java.util.ArrayList;

public class Weapon
{
    public static final int NUM_WEAPONS = 37;
    public static final int NUM_SIMPLE_WEAPONS = 13;

    public String name;
    public double costInGP;
    public int numDice;
    public int damageDie;
    public int versatileDie;
    public String damageType;
    public int weight;
    public boolean simple;
    public String properties;
    public boolean isProficient;
    public int range;
    public int rangeDis;


    // weapon properties
    public boolean isAmmunition;
    public boolean isFinesse;
    public boolean isHeavy;
    public boolean isLight;
    public boolean isLoading;
    public boolean isRanged;
    public boolean isReach;
    public boolean isSpecial;
    public boolean isThrown;
    public boolean isTwoHanded;
    public boolean isVersatile;

    public boolean error;

    public static ArrayList<Weapon> allWeapons = new ArrayList<>();

    public Weapon(String error)
    {
        name = error;
        this.error = true;
    }

    public Weapon(String name, double costInGP, int damageDie, String damageType, int weight, boolean ranged, boolean simple, String properties)
    {
        this.name = name;
        this.costInGP = costInGP;
        this.numDice = 1;
        this.damageDie = damageDie;
        this.damageType = damageType;
        this.weight = weight;
        this.isRanged = ranged;
        this.simple = simple;
        this.properties = properties;
        range = 5;
        this.setProperties();

        if (isVersatile)
            this.versatileDie = Integer.parseInt(properties.substring(properties.indexOf("(") + 3, properties.indexOf(")")));
    }

    public static Weapon findWeapon(String message)
    {
        // creates blank weapon
        String weaponName = "";
        Weapon currentWeapon = null;

        // iterates through weapon list
        for (int i = 0; i <= NUM_WEAPONS; i++)
        {
            currentWeapon = allWeapons.get(i);
            String currentWeaponName = currentWeapon.getName();
            if (message.contains(currentWeaponName))
            {
                weaponName = currentWeaponName;
                break;
            }
        }

        if (weaponName.isBlank())
        {
            MyListener.attackSent = false;
            return new Weapon("roll again, this time specifying the weapon you'd like to attack/damage with\n" +
                    "if you are attacking with a finesse weapon and would like to use dexterity, include \"dex\" in your message");
        }
        MyListener.attackSent = true;

        return currentWeapon;
    }

    public String dexOrStr(String message)
    {
        return (isRanged() || (isFinesse() && message.contains("dex"))) ? "dex" : "str";
    }

    public String toString()
    {
        if (name.equals("unarmed"))
            return "An unarmed strike deals 1 bludgeoning damage. Punching is free and weighs nothing.";

        StringBuilder retval = new StringBuilder("A " + name + " deals " + numDice + "d" + damageDie + " "
                + damageType + " damage, costs " + costInGP + " GP, and weighs " + weight + " lbs. ");

        if (isRanged)
            retval.append("It is a ranged weapon. ");
        if (!properties.isBlank())
        {
            retval.append("It has the ").append(properties);

            if (properties.contains("and"))
                retval.append(" properties. ");
            else
                retval.append(" property. ");
        }
        if (simple)
            retval.append("It is a simple weapon.");
        else
            retval.append("It is a martial weapon.");

        return retval.toString();
    }

    public static void makeWeapons()
    {
        // simple melee weapons
        Weapon club = new Weapon("club", 0.1, 4, "bludgeoning", 2, false, true, "light");
        allWeapons.add(club); // 0

        Weapon dagger = new Weapon("dagger", 2, 4, "piercing", 1, false, true, "finesse, light, and thrown");
        dagger.setRange(20, 60);
        allWeapons.add(dagger); // 1

        Weapon greatclub = new Weapon("greatclub", 0.2, 8, "bludgeoning", 10, false, true, "two-handed");
        allWeapons.add(greatclub); // 2

        Weapon handaxe = new Weapon("handaxe", 5, 6, "slashing", 2, false, true, "light and thrown");
        handaxe.setRange(20, 60);
        allWeapons.add(handaxe); // 3

        Weapon javelin = new Weapon("javelin", 0.5, 6, "piercing", 2, false, true, "thrown");
        javelin.setRange(30, 120);
        allWeapons.add(javelin); // 4

        Weapon lightHammer = new Weapon("light hammer", 2, 4, "bludgeoning", 2, false, true, "light and thrown");
        allWeapons.add(lightHammer); // 5

        Weapon mace = new Weapon("mace", 5, 6, "bludgeoning", 4, false, true, "");
        allWeapons.add(mace); // 6

        Weapon quarterstaff = new Weapon("quarterstaff", 2, 6, "bludgeoning", 4, false, true, "versatile (1d8)");
        allWeapons.add(quarterstaff); // 7

        Weapon sickle = new Weapon("sickle", 1, 4, "slashing", 2, false, true, "light");
        allWeapons.add(sickle); // 8

        Weapon spear = new Weapon("spear", 1, 6, "piercing", 3, false, true, "thrown and versatile (1d8)");
        spear.setRange(20, 60);
        allWeapons.add(spear); // 9


        //simple ranged weapons
        Weapon lightCrossbow = new Weapon("light crossbow", 25, 8, "piercing", 5, true, true, "ammunition, loading, and two-handed");
        lightCrossbow.setRange(80, 320);
        allWeapons.add(lightCrossbow); // 10

        Weapon dart = new Weapon("dart", 0.05, 4, "piercing", 1, true, true, "finesse and thrown");
        dart.setRange(20, 60);
        allWeapons.add(dart); // 11

        Weapon shortbow = new Weapon("shortbow", 25, 6, "piercing", 2, true, true, "ammunition and two-handed");
        shortbow.setRange(80, 320);
        allWeapons.add(shortbow); // 12

        Weapon sling = new Weapon("sling", 0.1, 4, "bludgeoning", 0, true, true, "ammunition");
        sling.setRange(30, 120);
        allWeapons.add(sling); // 13


        // martial melee weapons
        Weapon battleaxe = new Weapon("battleaxe", 10, 8, "slashing", 4, false, false, "versatile (1d10)");
        allWeapons.add(battleaxe); // 14

        Weapon flail = new Weapon("flail", 10, 8, "bludgeoning", 2, false, false, "");
        allWeapons.add(flail); // 15

        Weapon glaive = new Weapon("glaive", 20, 10, "slashing", 6, false, false, "heavy, reach, and two-handed");
        allWeapons.add(glaive); // 16

        Weapon greataxe = new Weapon("greataxe", 30, 12, "slashing", 6, false, false, "heavy and two-handed");
        allWeapons.add(greataxe); // 17

        Weapon greatsword = new Weapon("greatsword", 50, 6, "slashing", 6, false, false, "heavy and two-handed");
        greatsword.setNumDice();
        allWeapons.add(greatsword); // 18

        Weapon halberd = new Weapon("halberd", 20, 10, "slashing", 6, false, false, "heavy, reach, and two-handed");
        allWeapons.add(halberd); // 19

        Weapon lance = new Weapon("lance", 10, 12, "piercing", 6, false, false, "reach and special");
        // do something to make disadv if within 5 feet and two-handed if not mounted
        allWeapons.add(lance); // 20

        Weapon longsword = new Weapon("longsword", 15, 8, "slashing", 3, false, false, "versatile (1d10)");
        allWeapons.add(longsword); // 21

        Weapon maul = new Weapon("maul", 10, 6, "bludgeoning", 10, false, false, "heavy and two-handed");
        maul.setNumDice();
        allWeapons.add(maul); // 22

        Weapon morningstar = new Weapon("morningstar", 15, 8, "piercing", 4, false, false, "");
        allWeapons.add(morningstar); // 23

        Weapon pike = new Weapon("pike", 5, 10, "piercing", 18, false, false, "heavy, reach, and two-handed");
        allWeapons.add(pike); // 24

        Weapon rapier = new Weapon("rapier", 25, 8, "piercing", 2, false, false, "finesse");
        allWeapons.add(rapier); // 25

        Weapon scimitar = new Weapon("scimitar", 25, 6, "slashing", 3, false, false, "finesse and light");
        allWeapons.add(scimitar); // 26

        Weapon shortsword = new Weapon("shortsword", 10, 6, "piercing", 2, false, false, "finesse and light");
        allWeapons.add(shortsword); // 27

        Weapon trident = new Weapon("trident", 5, 6, "piercing", 4, false, false, "thrown and versatile (1d8)");
        trident.setRange(20, 60);
        allWeapons.add(trident); // 28

        Weapon warPick = new Weapon("war pick", 5, 8, "piercing", 2, false, false, "");
        allWeapons.add(warPick); // 29

        Weapon warhammer = new Weapon("warhammer", 15, 8, "bludgeoning", 2, false, false, "versatile (1d10)");
        allWeapons.add(warhammer); // 30

        Weapon whip = new Weapon("whip", 2, 4, "slashing", 3, false, false, "finesse and reach");
        allWeapons.add(whip); // 31


        // martial ranged weapons
        Weapon blowgun = new Weapon("blowgun", 10, 1, "piercing", 1, true, false, "ammunition and loading");
        blowgun.setRange(25, 100);
        allWeapons.add(blowgun); // 32

        Weapon handCrossbow = new Weapon("hand crossbow", 75, 6, "piercing", 3, true, false, "ammunition, light, and loading");
        handCrossbow.setRange(30, 120);
        allWeapons.add(handCrossbow); // 33

        Weapon heavyCrossbow = new Weapon("heavy crossbow", 50, 10, "piercing", 18, true, false, "ammunition, heavy, loading, and two-handed");
        heavyCrossbow.setRange(100, 400);
        allWeapons.add(heavyCrossbow); // 34

        Weapon longbow = new Weapon("longbow", 50, 8, "piercing", 2, true, false, "ammunition, heavy, and two-handed");
        longbow.setRange(150, 600);
        allWeapons.add(longbow); // 35

        Weapon net = new Weapon("net", 1, 0, "", 3, true, false, "special and thrown");
        net.setRange(5, 15);
        allWeapons.add(net); // 36


        // unarmed strikes
        Weapon unarmedStrikes = new Weapon("unarmed", 0, 1, "bludgeoning", 0, false, true, "");
        unarmedStrikes.setProf();
        allWeapons.add(unarmedStrikes); // 37
    }

    public void setRange(int range, int rangeDis){
        this.range = range;
        this.rangeDis = rangeDis;
    }
    public void setNumDice(){
        numDice = 2;
    }
    private void setProperties()
    {
        if (properties.contains("ammunition"))
            isAmmunition = true;
        if (properties.contains("finesse"))
            isFinesse = true;
        if (properties.contains("heavy"))
            isHeavy = true;
        if (properties.contains("light"))
            isLight = true;
        if (properties.contains("loading"))
            isLoading = true;
        if (properties.contains("reach"))
        {
            isReach = true;
            range += 5;
        }
        if(properties.contains("special"))
            // also panic because it's weird
            isSpecial = true;
        if (properties.contains("thrown"))
            isThrown = true;
        if (properties.contains("two-handed"))
            isTwoHanded = true;
        if (properties.contains("versatile"))
            isVersatile = true;
    }
    public void setProf()
    {
        isProficient = true;
    }
    public String getName(){
        return name;
    }
    public boolean isProf(){
        return isProficient;
    }
    public boolean isFinesse()
    {
        return isFinesse;
    }
    public boolean isRanged(){
        return isRanged;
    }
}