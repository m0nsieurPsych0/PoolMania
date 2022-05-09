package ca.ulaval.glo2004.gui.Panneau;

import javax.swing.*;
import javax.swing.colorchooser.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class ColorPicker extends JColorChooser {

    private Color currentColor;
    private AbstractColorChooserPanel HSB;
    private AbstractColorChooserPanel[] pickers;
    private final String[] spinnerText = {"Teinte", "Saturation", "Luminosité"};
    private JSpinner[] spinnerObject = new JSpinner[3];

    private float teinte = 0f, saturation = 0f, luminosite = 0f;

    private enum pickerType {SWATCH, HSV, HSL, RGB, CMYK}

    private GridBagLayout gb;
    private GridBagConstraints gc;
    private JPanel preview, hexValue, colorValue;

    private static ColorPicker instance = null;

    private ColorPicker() {
        removeUnwantedPanel();
        removeUnwantedComponent();
        setLayout();
        setSpinners();
        this.setVisible(false);

    }

    public static ColorPicker getInstance(){
        if (instance == null){
            instance = new ColorPicker();
        }
        return instance;
    }

    public Color getCouleur() {
        return currentColor;
    }

    public void setCouleur(Color color) {
        setHSBValuesFromColor(color);
    }

    public String getCurrentColorHex() {
        return Integer.toHexString(this.getCouleur().getRGB());
    }

    private void setSpinners() {
        for (int i = 0; i < colorValue.getComponentCount(); i++) {
            if (colorValue.getComponent(i).getClass().getName() == "javax.swing.JSpinner") {
                JSpinner spinner = (JSpinner) colorValue.getComponent(i);

                spinner.setToolTipText(spinnerText[i]);
                spinner.setName(spinnerText[i]);
                spinnerObject[i] = spinner;

                spinner.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent evt) {
                        int spinnerValue = 0;
                        JSpinner currentSpinner = ((JSpinner) evt.getSource());
                        try {
                            spinnerValue = Integer.parseInt(currentSpinner.getValue().toString());
                        } catch (NumberFormatException e) {
                            System.out.println(e);
                        }
                        switch (currentSpinner.getName()) {
                            case "Teinte":
                                teinte = (((float) spinnerValue / 6) / 60); // 0-360 = 0->1
                                break;
                            case "Saturation":
                                saturation = checkColorIsNotWhiteShade((spinnerValue / 100f), luminosite); // 0->100 = 0->1
                                break;
                            case "Luminosité":
                                luminosite = (spinnerValue / 100f); // 0->100 = 0->1
                                saturation = checkColorIsNotWhiteShade(saturation, luminosite);
                                break;
                        }
                        if(saturation != ((int) spinnerObject[1].getValue() / 100f)){
                            spinnerObject[1].setValue((int) (saturation * 100f));
                        }
                        setHSBtoCurrentColor(teinte, saturation, luminosite);
                    }
                });
            }

        }
    }

    private void setLayout() {
        gb = (GridBagLayout) HSB.getLayout();


        //Changer la couleur de fond
        HSB.setBackground(Color.white);
        this.setBackground(Color.white);


        colorValue.setBackground(Color.WHITE);
        colorValue.setPreferredSize(new Dimension(60, 100));


        // Component 3 = vertical rectangle = Teinte
        JComponent teinte = (JComponent) HSB.getComponent(3);

        gc = new GridBagConstraints();

        gc.gridx = 2;
        gc.gridy = -1;
        gc.gridwidth = 1;
        gc.gridheight = 2;
        gc.weightx = 0.0;
        gc.weighty = 0.0;
        gc.anchor = 11;
        gc.fill = 2;
        gc.insets = new Insets(0, 0, 0, 0);
        gc.ipadx = 15;
        gc.ipady = 100;

        gb.setConstraints(teinte, gc);

        // Component 4 = square = Saturation lumière
        JComponent saturationLumiere = (JComponent) HSB.getComponent(4);

        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = -1;
        gc.gridwidth = 1;
        gc.gridheight = 2;
        gc.weightx = 0.0;
        gc.weighty = 0.0;
        gc.anchor = 11;
        gc.fill = 0;
        gc.insets = new Insets(0, 0, 0, 0);
        gc.ipadx = 100;
        gc.ipady = 100;

        gb.setConstraints(saturationLumiere, gc);
    }

    private void setHSBtoCurrentColor(float teinte, float saturation, float luminosite) {
        currentColor = Color.getHSBColor(teinte, saturation, luminosite);

    }

    private void setHSBValuesFromColor(Color color) {

        float[] temp = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        int tempTeinte, tempSaturation, tempLuminosite;
        // Update
        teinte = temp[0];
        saturation = checkColorIsNotWhiteShade(temp[1], temp[2]);
        luminosite = temp[2];
        // Force update
        tempTeinte = (int)((temp[0] * 6) * 60);
        tempSaturation = (int) (saturation * 100);
        tempLuminosite = (int) (temp[2] * 100);
        spinnerObject[0].setValue(tempTeinte);
        spinnerObject[1].setValue(tempSaturation);
        spinnerObject[2].setValue(tempLuminosite);

        setHSBtoCurrentColor(teinte, saturation, luminosite);

    }

    private float checkColorIsNotWhiteShade(float saturation, float luminosite){

//        if(saturation < 0.10f){
//            if(luminosite > 0.90f){
//                saturation = 0.10f;
//            }
//        }

        return saturation;
    }

    private void removeUnwantedPanel() {

        pickers = this.getChooserPanels();
        // Remove all chooser panel except HSV
        for (int i = 0; i < pickers.length; i++) {

            if (i == pickerType.HSV.ordinal()) {
                HSB = pickers[pickerType.HSV.ordinal()];
            } else {
                this.removeChooserPanel(pickers[i]);
            }
        }
    }

    private void removeUnwantedComponent() {

        // Désactive le slider de transparence
        HSB.setColorTransparencySelectionEnabled(false);

        // Remove default preview panel
        this.setPreviewPanel(new JPanel()); // Aucun preview

        // Component 0 = Jpanel  -> Layout = gridbaglayout
        colorValue = (JPanel) HSB.getComponent(0);
        for (int i = 0; i < 5; ++i) {
            // Remove unwanted component
            for (int j = 0; j < colorValue.getComponentCount(); j++) {
                if (colorValue.getComponent(j).getClass().getName() == "javax.swing.JSlider") {
                    colorValue.remove(colorValue.getComponent(j));
                }
                if (colorValue.getComponent(j).getClass().getName() == "javax.swing.JRadioButton") {
                    colorValue.remove(colorValue.getComponent(j));
                }
                if (colorValue.getComponent(j).getClass().getName() == "javax.swing.JLabel") {
                    colorValue.remove(colorValue.getComponent(j));
                }
                if (colorValue.getComponent(j).getClass().getName() == "javax.swing.JSpinner") {
                    JSpinner spinner = (JSpinner) colorValue.getComponent(j);
                    if (!spinner.isVisible()) {
                        colorValue.remove(colorValue.getComponent(j));
                    }
                }
            }
        }

        // Disable Accessible Jtextfield and Jlabel pour prévenir des inputs indésirables et garder le gridbaglayout tel quel
        for (int i = 1; i < 3; ++i) {
            HSB.getComponent(i).setFocusable(false);
            HSB.getComponent(i).setEnabled(false);
        }
    }
    
}
