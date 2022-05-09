package ca.ulaval.glo2004.gui.Panneau;

import ca.ulaval.glo2004.gui.EcouteurEvt.RaccourcisMenuEvn.MenuEvn;
import ca.ulaval.glo2004.gui.ImperialMetricInputValidator;
import ca.ulaval.glo2004.utils.InputUtils;
import ca.ulaval.glo2004.gui.MainWindow;
import ca.ulaval.glo2004.utils.DimensionDomaine;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;



public class PanneauAccueil extends JPanel{

    private MainWindow fenetrePrincipale;
    private Dimension initialDimensions;
    private GridBagConstraints gridBagConstraints;
    private GridBagLayout panneauAccueilLayout;
    private JButton nouveau, ouvrir, quitter;
    private JLabel poolmaniaLabel;
    private JLabel labelNbMurs, labelLongueurMurs, labelEpaisseurMurs, labelRayonBoules;
    private JFormattedTextField inputNbMurs;
    private JTextField inputLongueurMurs, inputEpaisseurMurs, inputRayonBoules;
    private JPanel propTablePane, boutonsPane;
    private Dimension jtextfieldPreferedSize = new Dimension(100, 22);
    private double inputEpaisseurMursInitial, inputRayonBoulesInitial, inputLongueurMursInitial;
    private DecimalFormat formatDecimal;
    private String currentPath;
    private ImageIcon logo;

    public PanneauAccueil(MainWindow fenetrePrincipale){
        this.fenetrePrincipale = fenetrePrincipale;
        setInitialDimensions(DimensionDomaine.getInstance().getLargeurEcran(), DimensionDomaine.getInstance().getHauteurEcran());
        init();
    }

    private void init(){
        panneauAccueilLayout = new GridBagLayout();
        nouveau = new JButton();
        ouvrir = new JButton();
        quitter = new JButton();

        propTablePane = new JPanel();
        boutonsPane = new JPanel();
        labelNbMurs = new JLabel("Nombre de murs: ");
        labelLongueurMurs = new JLabel("Longueur des murs de contour (cm): ");
        labelEpaisseurMurs = new JLabel("Épaisseur des murs de contour (cm): ");
        labelRayonBoules = new JLabel("Rayon des boules dans la table (cm): ");

        NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
        NumberFormatter formatterInt = new NumberFormatter(format);
        formatterInt.setValueClass(Integer.class);
        formatterInt.setMinimum(3);
        formatterInt.setMaximum(Integer.MAX_VALUE);
        formatterInt.setAllowsInvalid(true);

        ImperialMetricInputValidator immv = new ImperialMetricInputValidator(fenetrePrincipale.getMetrique());

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        formatDecimal = new DecimalFormat("##.####");
        formatDecimal.setDecimalFormatSymbols(otherSymbols);

        inputNbMurs = new JFormattedTextField(formatterInt);
        inputLongueurMurs = new JTextField();
        inputEpaisseurMurs = new JTextField();
        inputRayonBoules = new JTextField();
        inputLongueurMurs.setInputVerifier(immv);
        inputEpaisseurMurs.setInputVerifier(immv);
        inputRayonBoules.setInputVerifier(immv);


        inputNbMurs.setValue(4);
        setInputValue(inputLongueurMurs, 200d, 2.54);
        setInputValue(inputEpaisseurMurs, 10d, 2.54);
        setInputValue(inputRayonBoules, 5d, 2.54);

        inputLongueurMursInitial = getInputValue(inputLongueurMurs, 2.54);
        inputEpaisseurMursInitial = getInputValue(inputEpaisseurMurs, 2.54);
        inputRayonBoulesInitial = getInputValue(inputRayonBoules, 2.54);


        setLogo();
        buildUp();
    }

    private void buildUp(){
        this.setPreferredSize(initialDimensions);
        this.setVisible(true);
        this.setLayout(panneauAccueilLayout);

        poolmaniaLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.ipady = 30;
        add(poolmaniaLabel, gridBagConstraints);

        boutonsPane.setMinimumSize(new Dimension(400, 600));
        boutonsPane.setName("boutonPane");
        boutonsPane.setLayout(new GridBagLayout());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new Insets(10, 0, 10,0);
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.ipady = 30;
        add(boutonsPane, gridBagConstraints);


        nouveau.setText("Nouvelle table");
        nouveau.setHorizontalTextPosition(SwingConstants.CENTER);
        nouveau.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(inputEpaisseurMurs.getInputVerifier().verify(inputEpaisseurMurs) && inputLongueurMurs.getInputVerifier().verify(inputLongueurMurs)
                && inputRayonBoules.getInputVerifier().verify(inputRayonBoules)) {
                    fenetrePrincipale.getControleur().nouvelleTable(getInputNbMurs(),getInputLongueurMurs(), getInputEpaisseurMurs(),getInputRayonBoules());
                    fenetrePrincipale.setModeEdition(true);
                    fenetrePrincipale.getPanneauOutils().setAucunProps();
                    fenetrePrincipale.getPanneauOutils().setSelectionner();
                    fenetrePrincipale.setBoolSelectionnerActive();
                }
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.ipadx = 0;
        gridBagConstraints.ipady = 30;
        gridBagConstraints.insets = new Insets(10, 0, 10,0);
        boutonsPane.add(nouveau, gridBagConstraints);

        ouvrir.setText("Ouvrir un fichier table");
        ouvrir.setHorizontalTextPosition(SwingConstants.CENTER);
        ouvrir.addActionListener(new MenuEvn(fenetrePrincipale, KeyEvent.VK_O));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(10, 0, 10,0);
        gridBagConstraints.ipadx = 0;
        gridBagConstraints.ipady = 30;
        boutonsPane.add(ouvrir, gridBagConstraints);

        quitter.setText("Quitter");
        quitter.setHorizontalTextPosition(SwingConstants.CENTER);
        quitter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                System.exit(0);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(10, 0, 10,0);
        gridBagConstraints.ipadx = 0;
        gridBagConstraints.ipady = 30;
        boutonsPane.add(quitter, gridBagConstraints);

        propTablePane.setBackground(new Color(255, 255, 255));
        propTablePane.setBorder(BorderFactory.createLineBorder(Color.black));
        propTablePane.setMinimumSize(new Dimension(600, 600));
        propTablePane.setName("propTablePane");
        propTablePane.setLayout(new GridBagLayout());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new Insets(10, 0, 10,0);
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.ipady = 30;
        add(propTablePane, gridBagConstraints);


        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 0, 5, 5);
        propTablePane.add(labelNbMurs, gridBagConstraints);

        inputNbMurs.setPreferredSize(jtextfieldPreferedSize);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        gridBagConstraints.fill = 2;
        propTablePane.add(inputNbMurs, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 0, 5, 5);
        propTablePane.add(labelLongueurMurs, gridBagConstraints);

        inputLongueurMurs.setPreferredSize(jtextfieldPreferedSize);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        gridBagConstraints.fill = 2;
        propTablePane.add(inputLongueurMurs, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 0, 5, 5);
        propTablePane.add(labelEpaisseurMurs, gridBagConstraints);

        inputNbMurs.setPreferredSize(jtextfieldPreferedSize);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        gridBagConstraints.fill = 2;
        propTablePane.add(inputEpaisseurMurs, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 0, 5, 5);
        propTablePane.add(labelRayonBoules, gridBagConstraints);

        inputNbMurs.setPreferredSize(jtextfieldPreferedSize);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        gridBagConstraints.fill = 2;
        propTablePane.add(inputRayonBoules, gridBagConstraints);
    }

    public void setInitialDimensions(int largeurEcran, int hauteurEcran) {
        initialDimensions = new Dimension(largeurEcran, hauteurEcran);
    }
    //TODO Menu d'acceuil
    //TODO Entrée d'informations: Taille mur, nombre de mur, Nom du projet etc.

    public void labelMetrique(){
        if(fenetrePrincipale.getMetrique()){
            labelLongueurMurs.setText("Longueur des murs de contour (cm): ");
            labelEpaisseurMurs.setText("Épaisseur des murs de contour (cm): ");
            labelRayonBoules.setText("Rayon des boules dans la table (cm): ");

            ((ImperialMetricInputValidator) inputLongueurMurs.getInputVerifier()).setMetrique(fenetrePrincipale.getMetrique());
            ((ImperialMetricInputValidator) inputEpaisseurMurs.getInputVerifier()).setMetrique(fenetrePrincipale.getMetrique());
            ((ImperialMetricInputValidator) inputRayonBoules.getInputVerifier()).setMetrique(fenetrePrincipale.getMetrique());
        }
        else{
            labelLongueurMurs.setText("Longueur des murs de contour (po): ");
            labelEpaisseurMurs.setText("Épaisseur des murs de contour (po): ");
            labelRayonBoules.setText("Rayon des boules dans la table (po): ");

            ((ImperialMetricInputValidator) inputLongueurMurs.getInputVerifier()).setMetrique(fenetrePrincipale.getMetrique());
            ((ImperialMetricInputValidator) inputEpaisseurMurs.getInputVerifier()).setMetrique(fenetrePrincipale.getMetrique());
            ((ImperialMetricInputValidator) inputRayonBoules.getInputVerifier()).setMetrique(fenetrePrincipale.getMetrique());

        }
    }

    private void setLogo(){
        try{
            logo = new ImageIcon(getClass().getResource("/nom.png"));
            poolmaniaLabel = new JLabel(logo);
        }
        catch(Exception e){
            try{
                BufferedImage img;
                currentPath = fenetrePrincipale.getCurrentPath();
                currentPath += "/poolmania/src/main/resources/nom.png";
                img = ImageIO.read(new File(currentPath));
                logo = new ImageIcon(img);
                poolmaniaLabel = new JLabel(logo);
            }
            catch (Exception exception){
                poolmaniaLabel = new JLabel("Poolmania");
                poolmaniaLabel.setFont(new Font(this.getFont().getFontName(), Font.BOLD, 100));
            }
        }


    }

    public double getInputRayonBoules() {
        return getInputValue(inputRayonBoules, 2.54);

    }

    public double getInputLongueurMurs() {
        return getInputValue(inputLongueurMurs, 2.54);

    }

    public double getInputEpaisseurMurs() {
        return getInputValue(inputEpaisseurMurs, 2.54);
    }

    public double getinputRayonBoulesInitial() {
        return inputRayonBoulesInitial;
    }

    public double getInputLongueurMursInitial() {
        return inputRayonBoulesInitial;
    }
    public double getInputEpaisseurMursInitial() {
        return inputRayonBoulesInitial;
    }

    public int getInputNbMurs() {
        return (int)inputNbMurs.getValue();
    }

    public void changerMesure(){
        if(fenetrePrincipale.getMetrique()){
            //Imperial -> Metrique
            setInputValue(inputRayonBoules, InputUtils.fractionToDouble(inputRayonBoules.getText(), 2.54), 2.54);
            setInputValue(inputEpaisseurMurs, InputUtils.fractionToDouble(inputEpaisseurMurs.getText(), 2.54), 2.54);
            setInputValue(inputLongueurMurs, InputUtils.fractionToDouble(inputLongueurMurs.getText(), 2.54), 2.54);
        }
        else{
            //Metrique -> Imperial
            setInputValue(inputRayonBoules, InputUtils.tryParseDouble(inputRayonBoules.getText()), 2.54);
            setInputValue(inputEpaisseurMurs, InputUtils.tryParseDouble(inputEpaisseurMurs.getText()), 2.54);
            setInputValue(inputLongueurMurs, InputUtils.tryParseDouble(inputLongueurMurs.getText()), 2.54);
        }
    }

    private void setInputValue(JTextField textField, double value, double ratio) {
        if(fenetrePrincipale.getMetrique()){
            textField.setText(formatDecimal.format(value));
        } else {
            textField.setText(InputUtils.doubleToFraction(value, ratio));
        }
    }

    private double getInputValue(JTextField textField, double ratio) {
        if(fenetrePrincipale.getMetrique()){
            return Double.parseDouble(textField.getText());
        } else {
            return InputUtils.fractionToDouble(textField.getText(), ratio);
        }
    }


}
