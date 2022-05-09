package ca.ulaval.glo2004.utils;

import java.awt.*;
import java.util.regex.Pattern;

public class DimensionDomaine extends Dimension {

    private GraphicsEnvironment environment;
    private Dimension limites;
    private Dimension minimumSize;
    private Dimension ecranCourant;
    private  GraphicsDevice ecranPrincipal;
    private String os;
    private int dpi;


    private static DimensionDomaine instance = null;

    private DimensionDomaine(){
        settingVariables();
        setInitialDimensions();
    }

    private void settingVariables(){
        os = System.getProperty("os.name");
        environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ecranPrincipal = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice(); //Écran principal
        ecranCourant = Toolkit.getDefaultToolkit().getScreenSize(); //Taille de l'écran courant
        dpi = Toolkit.getDefaultToolkit().getScreenResolution(); //Résolution d'écran (pixel par pouce)
        limites = new Dimension(environment.getMaximumWindowBounds().getSize());
    }

    public static DimensionDomaine getInstance(){
        if(instance == null){
            instance = new DimensionDomaine();
        }
        return instance;
    }

    private void setInitialDimensions() {
        // Taille max de l'écran
        // GetMaxBound
        this.height = ecranCourant.height;
        this.width = ecranCourant.width;
    }

    private void setMinimumSize(){
        minimumSize = new Dimension(ecranCourant.width/2,ecranCourant.height/2);
    }


    public DimensionDomaine getInitialDimensions() {
        return this;
    }
    public Dimension getLimites(){
        return limites;
    }
    public Dimension getMinimumSize(){
        return minimumSize;
    }
    public  Dimension getTailleEcran() {return ecranCourant; }
    public  GraphicsDevice getGd() { return ecranPrincipal; }

    public  int getLargeurEcran() {
        return ecranCourant.width;
    }
    public  int getHauteurEcran(){
        return ecranCourant.height;
    }


    private Boolean isRunningWindows(){
        Pattern regx = Pattern.compile("windows", Pattern.CASE_INSENSITIVE);
        return regx.matcher(os).find();
    }



    // Return windows scaling factor
    public float getWindowsScalingFactor() {
        // Il y a deux façon de régler le problème de scaling dans l'objet graphic:
        // 1- On réinitialise l'objet graphic à un scaling de 1
        // 2- On accepte le scaling graphique, mais on adapte le système de coordonnée qui lui n'est pas scaler

        //https://stackoverflow.com/questions/43057457/jdk-9-high-dpi-disable-for-specific-panel/46630710#46630710
        //https://stackoverflow.com/questions/26877517/java-swing-on-high-dpi-screen

        // Dans la classe WindowsLookAndFeel.java, on présume un dpi de 96.
        // si on fait la différence avec le dpi du système on connait le scaling effectif.
        float scaling = (dpi / 96f);

        if (scaling < 1 || !isRunningWindows()){
            scaling = 1;
        }

        return scaling;
    }

}