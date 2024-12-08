package ampersand;

public class Modifier
{
    private final String name; // str, dex, whatever, NO MOD AT THE END
    private int mod; // usually between -5 and 5
    private boolean saveProf; // if a class is proficient in the save for it

    public Modifier(String name, int stat)
    {
        this.name = name;
        mod = setMod(stat);
        saveProf = false;
    }
    public String getName(){
        return name;
    }
    public int getModifier(){
        return mod;
    }
    public static int setMod(int stat)
    {
        if(stat >= 10)
            return (stat - 10)/2;
        else
            return (11 - stat)/(-2);
    }
    public static String whichStatReturnString(int number)
    {
        if(number == 0)
            return "str";
        if(number == 1)
            return "dex";
        if(number == 2)
            return "con";
        if(number == 3)
            return "int";
        if(number == 4)
            return "wis";
        if(number == 5)
            return "cha";
        return "you messed up, sorry";
    }

    public static int whichStatReturnInt(String stat)
    {
        if(stat.equals("str"))
            return 0;
        if(stat.equals("dex"))
            return 1;
        if(stat.equals("con"))
            return 2;
        if(stat.equals("int"))
            return 3;
        if(stat.equals("wis"))
            return 4;
        if(stat.equals("cha"))
            return 5;
        return 0; //guess the default is strength
    }

    public void setSaveProf(boolean isProf)
    {
        saveProf = isProf;
    }
    public boolean isProf(){
        return saveProf;
    }
}
