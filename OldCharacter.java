/*package com.github.olive; //old character class

import java.util.ArrayList;

import static com.github.olive.Modifier.whichStatReturnInt;
import static com.github.olive.Modifier.whichStatReturnString;
import static com.github.olive.PCClass.whichClassReturnString;

public class Character{

    //static all-encompassing arrays, move once subclasses made
    public static ArrayList<Character> party = new ArrayList<>();
    public static ArrayList<Character> everyone = new ArrayList<>();
    public static ArrayList<Character> enemies = new ArrayList<>(); //move to enemies class


    //the default average array
    public static int[] defaultStats = {10, 10, 10, 10, 10, 10};
    public static String[] defaultSkills = {"n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n", "n"};


    //instance variables
    private String name; //keep
    private int[] stats; //array of actual stat array //keep
    private ArrayList<Modifier> mods = new ArrayList<>(); //arraylist of modifiers //keep
    private int prof; //keep

    //move to PlayerCharacter Class
    private int level;
    private String PCClass;
    private ArrayList<com.github.olive.PCClass> classes = new ArrayList<>();
    private ArrayList<Skill> skills = new ArrayList<>();


    //constructors
    public Character(){ //makes a default character, edit as you go along
        name = "Joe";
        stats = defaultStats;
        setMods();
        setClasses("Fighter 1");
        setProficiency();
        setSkills(defaultSkills);
        party.add(this);
        everyone.add(this);
    }
    public Character(String name, int[] stats, String pcClassNLevel, String[] skillProfs){
        this.name = name; //sets name
        this.stats = stats; //sets stat array
        this.setMods(); //sets modifiers
        this.setClasses(pcClassNLevel); //sets class(es), adds to classes array
        this.setProficiency(); //sets proficiency bonus depending on level
        this.setSkills(skillProfs); //sets which skills they're proficient in
        party.add(this); //adds to party, move to PC class
        everyone.add(this); //adds to "everyone" array, make sure to declare as Character so this keeps working, although we don't need this
    }

    //setters
    private void setMods(){
        for(int i = 0; i < 6; i++){ //iterates through 6 stat values
            Modifier thisMod = new Modifier(Modifier.whichStatReturnString(i), stats[i]); //makes new modifier
            //whichStatReturnString returns something like "str", and constructor sets that as modifier object name
            //stats[i] checks for the stat value at that i
            mods.add(thisMod); //adds mod to arraylist of modifiers for later use
        }
    }
    public void increaseStat(String whichStat){ //use upon reaching level 4, maybe
        stats[whichStatReturnInt(whichStat)] += 1; //increases the stat within the array
        Modifier theMod = new Modifier(whichStat, stats[whichStatReturnInt(whichStat)]);
        //makes new mod with "whichStat", which should be something like "str", and then uses the stat from the array
        mods.set(whichStatReturnInt(whichStat), theMod); //places the mod in the mod array
    }
    private void setClasses(String pcClassNLevel){
        pcClassNLevel = pcClassNLevel.toLowerCase();
        level = 0; //sets default level to 0 to add to it
        int level2 = 0; //level2 is the one before the semicolon, if any
        String class1; //class after the ;
        String class2 = ""; //class b4 ;
        if(pcClassNLevel.contains((";"))){ //if multiclass
            class2 = pcClassNLevel.substring(0, pcClassNLevel.indexOf(";") - 2); //sets class b4 ;
            class1 = pcClassNLevel.substring(pcClassNLevel.indexOf(";") + 2, pcClassNLevel.length() - 2); //sets class after ;
            this.PCClass = class2 + ", " + class1;
            level2 = Integer.parseInt(pcClassNLevel.substring(pcClassNLevel.indexOf(";") - 1, pcClassNLevel.indexOf(";"))); //adds level b4 ;
            level += level2;
        } else{
            class1 = pcClassNLevel.substring(0, pcClassNLevel.length() - 2); //sets class after ;
            this.PCClass = class1;
        }
        int level1 = Integer.parseInt(pcClassNLevel.substring(pcClassNLevel.length() - 1)); //adds level after ;
        level += level1;
        for(int i = 0; i < 13; i++){ //iterates through 13 classes
            if(class1.equals(whichClassReturnString(i))){ //if class contains this class
                PCClass thisClass = new PCClass(whichClassReturnString(i), level1);
                classes.add(thisClass); //adds class to arraylist of classes for later use
            } else if(class2.equals(whichClassReturnString(i))){
                PCClass thisClass = new PCClass(whichClassReturnString(i), level2);
                classes.add(thisClass); //adds class to arraylist of classes for later use
            }
            else{
                PCClass thisClass = new PCClass(whichClassReturnString(i), 0);
                classes.add(thisClass); //adds class to arraylist of classes for later use
            }
        }
        setSaves(class1); //sets saving throw proficiencies
        System.out.println("character created");
    }
    private void setProficiency(){
        if(level < 5)
            prof = 2;
        else if(level < 9)
            prof = 3;
        else if(level < 13)
            prof = 4;
        else if(level < 17)
            prof = 5;
        else
            prof = 6;
    }
    private void setSkills(String[] skillProfs){
        for(int i = 0; i < 18; i++){
            Skill skill = new Skill(i, skillProfs[i]);
            skills.add(skill);
        }
    }
    private void setSaves(String class2){ //sets saving throw proficiencies using class AFTER the semicolon, it's just misnamed
        if(class2.equals("barbarian") || class2.equals("fighter") || class2.equals("ranger")){ //sets strength save
            mods.get(0).setSaveProf(true);
        }
        if(class2.equals("bard") || class2.equals("monk") || class2.equals("ranger") || class2.equals("rogue")){ //sets dex save
            mods.get(1).setSaveProf(true);
        }
        if(class2.equals("barbarian") || class2.equals("fighter") || class2.equals("sorcerer")){ //sets con save
            mods.get(2).setSaveProf(true);
        }
        if(class2.equals("druid") || class2.equals("rogue") || class2.equals("wizard")){ //sets int save
            mods.get(3).setSaveProf(true);
        }
        if(class2.equals("cleric") || class2.equals("druid") || class2.equals("paladin") || class2.equals("warlock") || class2.equals("wizard")){ //sets wis save
            mods.get(4).setSaveProf(true);
        }
        if(class2.equals("bard") || class2.equals("cleric") || class2.equals("paladin") || class2.equals("sorcerer") || class2.equals("warlock")){ //sets cha save
            mods.get(5).setSaveProf(true);
        }
    }


    //getters
    public String getName(){
        return name;
    }
    public String sendName(){
        return "character is named " + name; //made for print statements in testing, not that necessary
    }
    public int getStat(int stat){ //stat should be between 0 and 5 inclusive
        return stats[stat];
    }
    public int getStat(String stat){ //stat should be str, dex, etc
        return stats[whichStatReturnInt(stat)];
    }
    public int[] getStatArray(){
        return stats;
    }
    public int getMod(String stat){ //returns a certain modifier if given str, dex, whatever
        int mod = 0;
        for (Modifier modifier : mods) { //iterates through modifiers
            if (stat.equals(modifier.getName())){ //if given stat name equals modifier stat name
                mod = modifier.getModifier();
            }
        }
        return mod;
    }
    public int getMod(int stat){ //returns a certain modifier if given 0-5 inclusive
        int mod = 0;
        for (Modifier modifier : mods) { //iterates through modifiers
            if (whichStatReturnString(stat).equals(modifier.getName())){ //if name associated with stat equals mod name
                mod = modifier.getModifier();
            }
        }
        return mod;
    }
    public Modifier getModObject(String stat){ //returns modifier as an object for saves code
        for (Modifier modifier : mods) { //iterates through modifiers
            if (stat.equals(modifier.getName())){ //if given stat name equals modifier stat name
                return modifier; //returns the modifier
            }
        }
        return new Modifier("mod not identified", 0); //if stuff goes wrong
    }
    public int getProf(){
        return prof;
    }
    public String getPCClass(){
        return PCClass;
    }
    public int getLevel(){
        return level;
    }
    public ArrayList<com.github.olive.PCClass> getClasses(){
        return classes;
    }
    public ArrayList<Skill> getSkills(){
        return skills;
    }
}*/

