package com.mygdx.purefaithstudio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class Config {
	// DON'T CHANGE FILE NAME!!!
	// Android have a lot of shit in shared preferences part.
	// Just don't touch a name ))
	public static Preferences preferences = null;// = Gdx.app.getPreferences("preferences");
	public static boolean lockScreen=false,persistent = false,moving=false,backColorchange=false;
	public static String listTest = "12";
	public static int points = 0;
	public static float[] backColor = {0,0,0};
    public static String promo="LWP#193#sin";

	public static void load() {
        preferences = Gdx.app.getPreferences("preferences");
        if(preferences !=null) {
            if (!preferences.contains("checkBoxTest") ||
                    !preferences.contains("listTest") ||
                    !preferences.contains("moveBox")  )
                save();

            //listTest = preferences.getString("listTest", "0");
            persistent = preferences.getBoolean("checkBoxTest", false);
            moving = preferences.getBoolean("moveBox", false);
            //loadPoints();
        }
	}

	public static void save() {
        if(preferences !=null) {
            preferences.putString("listTest", listTest);
            preferences.putBoolean("checkBoxTest", persistent);
            preferences.putBoolean("moveBox", moving);
            preferences.flush();
        }
	}

	public static void setBackColor(float r,float g,float b){
        backColor[0] = r;
        backColor[1] = g;
        backColor[2] = b;
        preferences.putFloat("red", backColor[0]);
        preferences.putFloat("green", backColor[1]);
        preferences.putFloat("blue", backColor[2]);
    }
    public static void loadBackColor(){
        if(!preferences.contains("red"))
            setBackColor(0,0,0);
        backColor[0]=preferences.getFloat("red");
        backColor[1]=preferences.getFloat("green");
        backColor[2]=preferences.getFloat("blue");
    }

   /* public static void loadPoints(){
        if(preferences !=null) {
            if (!preferences.contains("points"))
                savePoints();
            points = preferences.getInteger("points");
        }
    }
    public static void savePoints(){
        if(preferences !=null) {
            preferences.putInteger("points", points);
        }
    }*/

/*  public void LoadColor(){
        if(preferences !=null) {
            preferences.putFloat("red", color[0]);
            preferences.putFloat("green", color[1]);
            preferences.putFloat("blue", color[2]);
        }
    }

    public static void saveColor(){
        if(preferences !=null) {
            preferences.putInteger("points", points);
        }
    }

    */
}
