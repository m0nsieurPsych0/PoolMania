package ca.ulaval.glo2004.gui.Panneau;

import ca.ulaval.glo2004.domaine.DTO.*;
import ca.ulaval.glo2004.gui.EcouteurEvt.Edition.EditionObjetEvn;
import ca.ulaval.glo2004.gui.ImperialMetricInputValidator;
import ca.ulaval.glo2004.utils.InputUtils;
import ca.ulaval.glo2004.gui.MainWindow;
import ca.ulaval.glo2004.gui.EcouteurEvt.Edition.EditionPannOutiEvn;

import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.*;
import javax.swing.text.NumberFormatter;

public class PanneauOutils extends JPanel implements ItemListener {

    private MainWindow fenetrePrincipale;
    private Dimension initialDimensions;

    private JPanel outilsObjets;
    private JToggleButton selectionner, ajouter, briser, supprimer, propPhysiques;
    private JPanel outilsFlow;
    private JButton undo, redo;
    private JPanel outilsMode;
    private JButton changerMode;
    private GridBagConstraints gridBagConstraints;
    private GridBagLayout panneauOutilsLayout;
    private ButtonGroup boutonOutils;
    private EditionPannOutiEvn editionPannOutiEvn;
    private JPanel outilsPane;
    private JSplitPane outilsSplitPane;
    private JPanel propObjetPane;
    private JLabel labelPositionX, labelPositionY, labelRayon, labelCouleur, labelAngle, labelLongueur, labelEpaisseur, propTitle, labelNumero;
    private JFormattedTextField inputAngle;
    private JTextField inputPositionX, inputPositionY, inputRayon, inputLongueur, inputEpaisseur, inputHauteurTable, inputLargeurTable;;
    private JFormattedTextField inputNumero, inputCoefRestitution, inputCinetiqueFriction, inputDensiteBoules;
    private JButton boutonOK;
    private Dimension jtextfieldPreferedSize = new Dimension(100, 22);
    private ColorPicker colorPicker;
    private JLabel labelObjetAAjouter, labelCouleurPortail, labelCoefRestitution, labelCinetiqueFriction, labelDensiteBoules, labelHauteurTable, labelLargeurTable;
    private JLabel messageErreur;
    private JComboBox<String> inputObjetAAjouter, inputCouleurPortail;
    private double inputPositionXInitial, inputPositionYInitial, inputRayonInitial, inputAngleInitial, inputLongueurInitial, inputEpaisseurInitial;
    private DecimalFormat formatDecimal;
    private Color couleurInitiale;

    public PanneauOutils(MainWindow fenetrePrincipale) {
        this.fenetrePrincipale = fenetrePrincipale;

        init();
    }

    private void init() {
        // Init objets
        gridBagConstraints = new GridBagConstraints();
        panneauOutilsLayout = new GridBagLayout();
        outilsObjets = new JPanel();
        selectionner = new JToggleButton();
        selectionner.setSelected(true);
        ajouter = new JToggleButton();
        briser = new JToggleButton();
        supprimer = new JToggleButton();
        propPhysiques = new JToggleButton();
        outilsFlow = new JPanel();
        undo = new JButton();
        redo = new JButton();
        outilsMode = new JPanel();
        changerMode = new JButton();
        boutonOutils = new ButtonGroup();
        editionPannOutiEvn = new EditionPannOutiEvn(fenetrePrincipale);
        propObjetPane = new JPanel();
        propTitle = new JLabel();

        //https://stackoverflow.com/questions/16062666/formatting-numbers-without-locale
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        formatDecimal = new DecimalFormat("##.##");
        formatDecimal.setDecimalFormatSymbols(otherSymbols);

        ImperialMetricInputValidator immv = new ImperialMetricInputValidator(fenetrePrincipale.getMetrique());

        labelPositionX = new JLabel();
        inputPositionX = new JTextField();
        inputPositionX.setInputVerifier(immv);
        labelPositionY = new JLabel();
        inputPositionY = new JTextField();
        inputPositionY.setInputVerifier(immv);
        labelRayon = new JLabel();
        inputRayon = new JTextField();
        inputRayon.setInputVerifier(immv);
        labelCouleur = new JLabel();
        labelAngle = new JLabel();
        inputAngle = new JFormattedTextField(formatDecimal);
        labelLongueur = new JLabel();
        inputLongueur = new JTextField();
        inputLongueur.setInputVerifier(immv);
        labelEpaisseur = new JLabel();
        inputEpaisseur = new JTextField();
        inputEpaisseur.setInputVerifier(immv);
        labelNumero = new JLabel();
        NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
        NumberFormatter formatterInt = new NumberFormatter(format);
        formatterInt.setValueClass(Integer.class);
        formatterInt.setMinimum(1);
        formatterInt.setMaximum(Integer.MAX_VALUE);
        formatterInt.setAllowsInvalid(true);
        inputNumero = new JFormattedTextField(formatterInt);
        inputNumero.setValue(1);
        inputCoefRestitution = new JFormattedTextField(formatDecimal);
        inputCinetiqueFriction = new JFormattedTextField(formatDecimal);
        inputDensiteBoules = new JFormattedTextField(formatDecimal);
        inputDensiteBoules.setInputVerifier(immv);
        inputHauteurTable = new JTextField();
        inputHauteurTable.setInputVerifier(immv);
        inputLargeurTable = new JTextField();
        inputLargeurTable.setInputVerifier(immv);

        labelCoefRestitution = new JLabel();
        labelCinetiqueFriction = new JLabel();
        labelDensiteBoules = new JLabel();
        labelHauteurTable = new JLabel();
        labelLargeurTable = new JLabel();

        outilsSplitPane = new JSplitPane();
        outilsPane = new JPanel();
        colorPicker = ColorPicker.getInstance();
        boutonOK = new JButton();
        String[] objetsAAjouter = {"Boule colorée", "Poche", "Mur d'obstacle", "Paquet de boules colorées", "Portail"};
        labelObjetAAjouter = new JLabel();
        inputObjetAAjouter = new JComboBox<>(objetsAAjouter);
        inputObjetAAjouter.addItemListener(this);
        String[] couleurPortail = {"Bleu", "Orange"};
        labelCouleurPortail = new JLabel();
        inputCouleurPortail = new JComboBox<>(couleurPortail);
        messageErreur = new JLabel();
        buildUp();
    }

    private void buildUp() {


        this.setBackground(new Color(255, 255, 255));
//        this.setCursor(new Cursor(Cursor.MOVE_CURSOR));
        this.setDoubleBuffered(false);
        this.setMinimumSize(new Dimension(163, 350));
        this.setLayout(new BorderLayout());

        outilsSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        outilsSplitPane.setContinuousLayout(true);
        outilsSplitPane.setDividerSize(4);

        // Proportion du splitpane de la barre d'outils
        outilsSplitPane.setResizeWeight(0.5);

        outilsPane.setBackground(new Color(255, 255, 255));
        outilsPane.setAutoscrolls(true);
        outilsPane.setPreferredSize(new Dimension(166, 350));
        outilsPane.setMinimumSize(new Dimension(166, 350));
        outilsPane.setLayout(new GridBagLayout());

        outilsObjets.setBackground(new Color(255, 255, 255));
        GridBagLayout outilsObjetsLayout = new GridBagLayout();
        outilsObjetsLayout.columnWidths = new int[]{1};
        outilsObjets.setLayout(outilsObjetsLayout);

        selectionner.setText("Sélectionner");
        selectionner.setHorizontalTextPosition(SwingConstants.CENTER);
        selectionner.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                editionPannOutiEvn.selectionnerActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 35;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.BASELINE;
        gridBagConstraints.insets = new Insets(0, 0, 11, 0);
        outilsObjets.add(selectionner, gridBagConstraints);
        boutonOutils.add(selectionner);

        ajouter.setText("Ajouter un objet");
        ajouter.setHorizontalTextPosition(SwingConstants.CENTER);
        ajouter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                editionPannOutiEvn.ajouterActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 35;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.BASELINE;
        gridBagConstraints.insets = new Insets(11, 0, 11, 0);
        outilsObjets.add(ajouter, gridBagConstraints);
        boutonOutils.add(ajouter);

        briser.setText("Briser un mur");
        briser.setHorizontalTextPosition(SwingConstants.CENTER);
        briser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                editionPannOutiEvn.briserActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 35;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(11, 0, 11, 0);
        outilsObjets.add(briser, gridBagConstraints);
        boutonOutils.add(briser);

        supprimer.setText("Supprimer");
        supprimer.setHorizontalTextPosition(SwingConstants.CENTER);
        supprimer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                editionPannOutiEvn.supprimerActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 35;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(11, 0, 0, 0);
        outilsObjets.add(supprimer, gridBagConstraints);
        boutonOutils.add(supprimer);


        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.47;
        outilsPane.add(outilsObjets, gridBagConstraints);

        outilsFlow.setBackground(new Color(255, 255, 255));
        outilsFlow.setLayout(new GridBagLayout());

        undo.setText("Undo");
        undo.setHorizontalTextPosition(SwingConstants.CENTER);
        undo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                editionPannOutiEvn.undoActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 66;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(0, 0, 8, 0);
        outilsFlow.add(undo, gridBagConstraints);

        redo.setText("Redo");
        redo.setHorizontalTextPosition(SwingConstants.CENTER);
        redo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                editionPannOutiEvn.redoActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 66;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(10, 0, 0, 0);
        outilsFlow.add(redo, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.87;
        outilsPane.add(outilsFlow, gridBagConstraints);

        outilsMode.setBackground(new Color(255, 255, 255));
        outilsMode.setLayout(new GridBagLayout());

        changerMode.setText("Changer de mode");
        changerMode.setHorizontalTextPosition(SwingConstants.CENTER);
        changerMode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                editionPannOutiEvn.changerModeActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        outilsMode.add(changerMode, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.47;
        outilsPane.add(outilsMode, gridBagConstraints);

        propPhysiques.setText("Propriétés de la table");
        propPhysiques.setHorizontalTextPosition(SwingConstants.CENTER);
        propPhysiques.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                editionPannOutiEvn.propPhysiquesActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 35;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(11, 0, 0, 0);
        outilsObjets.add(propPhysiques, gridBagConstraints);
        boutonOutils.add(propPhysiques);

        outilsSplitPane.setTopComponent(outilsPane);

        propObjetPane.setBackground(new Color(255, 255, 255));
        propObjetPane.setMinimumSize(new Dimension(166, 400));
        propObjetPane.setName("propObjetPane");
        propObjetPane.setLayout(new GridBagLayout());

        propTitle.setFont(new Font("Tahoma", Font.BOLD, 14));
        propTitle.setText("Propriétés de l'objet sélectionné");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = GridBagConstraints.ABOVE_BASELINE;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new Insets(10, 0, 20, 0);
        propObjetPane.add(propTitle, gridBagConstraints);

        labelObjetAAjouter.setText("Objet à ajouter");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 0, 5, 5);
        propObjetPane.add(labelPositionX, gridBagConstraints);

        inputPositionX.setPreferredSize(jtextfieldPreferedSize);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        gridBagConstraints.fill = 2;
        propObjetPane.add(inputObjetAAjouter, gridBagConstraints);

        labelPositionX.setText("Position x (cm)");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 0, 5, 5);
        propObjetPane.add(labelPositionX, gridBagConstraints);

        inputPositionX.setPreferredSize(jtextfieldPreferedSize);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        gridBagConstraints.fill = 2;
        propObjetPane.add(inputPositionX, gridBagConstraints);

        labelPositionY.setText("Position y (cm)");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 0, 5, 5);
        propObjetPane.add(labelPositionY, gridBagConstraints);

        inputPositionY.setPreferredSize(jtextfieldPreferedSize);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        gridBagConstraints.fill = 2;
        propObjetPane.add(inputPositionY, gridBagConstraints);

        labelRayon.setText("Rayon (cm)");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 0, 5, 5);
        propObjetPane.add(labelRayon, gridBagConstraints);

        inputRayon.setPreferredSize(jtextfieldPreferedSize);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        gridBagConstraints.fill = 2;
        propObjetPane.add(inputRayon, gridBagConstraints);

        labelCouleur.setText("Couleur");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 0, 5, 5);
        propObjetPane.add(labelCouleur, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 5, 0, 0);
        gridBagConstraints.fill = 2;
        propObjetPane.add(colorPicker, gridBagConstraints);
        // TODO Déterminer la couleur par défaut
        colorPicker.setCouleur(Color.PINK);

        labelAngle.setText("Angle (deg)");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 0, 5, 5);
        propObjetPane.add(labelAngle, gridBagConstraints);

        inputAngle.setPreferredSize(jtextfieldPreferedSize);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        gridBagConstraints.fill = 2;
        propObjetPane.add(inputAngle, gridBagConstraints);

        labelLongueur.setText("Longueur (cm)");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 0, 5, 5);
        propObjetPane.add(labelLongueur, gridBagConstraints);

        inputLongueur.setPreferredSize(jtextfieldPreferedSize);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        gridBagConstraints.fill = 2;
        propObjetPane.add(inputLongueur, gridBagConstraints);

        labelEpaisseur.setText("Épaisseur (cm)");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 0, 5, 5);
        propObjetPane.add(labelEpaisseur, gridBagConstraints);

        inputEpaisseur.setPreferredSize(jtextfieldPreferedSize);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        gridBagConstraints.fill = 2;
        propObjetPane.add(inputEpaisseur, gridBagConstraints);

        labelCouleurPortail.setText("Couleur");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 0, 5, 5);
        propObjetPane.add(labelCouleurPortail, gridBagConstraints);

        inputCouleurPortail.setPreferredSize(jtextfieldPreferedSize);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        gridBagConstraints.fill = 2;
        propObjetPane.add(inputCouleurPortail, gridBagConstraints);

        boutonOK.setText("OK");
        boutonOK.addActionListener(new EditionObjetEvn(fenetrePrincipale));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = GridBagConstraints.CENTER;
        gridBagConstraints.anchor = GridBagConstraints.BELOW_BASELINE;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new Insets(5, 0, 20, 0);
        propObjetPane.add(boutonOK, gridBagConstraints);

        messageErreur.setText("Place holder Text");
        messageErreur.setForeground(Color.white);
        messageErreur.setVisible(true);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = GridBagConstraints.CENTER;
        gridBagConstraints.anchor = GridBagConstraints.BELOW_BASELINE;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new Insets(0, 0, 10, 0);
        propObjetPane.add(messageErreur, gridBagConstraints);


        labelNumero.setText("Numero: ");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 0, 5, 5);
        propObjetPane.add(labelNumero, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        gridBagConstraints.fill = 2;
        propObjetPane.add(inputNumero, gridBagConstraints);


        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        gridBagConstraints.fill = 2;
        propObjetPane.add(inputCinetiqueFriction, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        gridBagConstraints.fill = 2;
        propObjetPane.add(inputDensiteBoules, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        gridBagConstraints.fill = 2;
        propObjetPane.add(inputCoefRestitution, gridBagConstraints);


        labelCinetiqueFriction.setText("Coefficient de frottement cinétique: ");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        gridBagConstraints.fill = 2;
        propObjetPane.add(labelCinetiqueFriction, gridBagConstraints);

        labelDensiteBoules.setText("Densité des boules (kg/m³): ");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        gridBagConstraints.fill = 2;
        propObjetPane.add(labelDensiteBoules, gridBagConstraints);

        labelCoefRestitution = new JLabel("Coefficient de restitution: ");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        gridBagConstraints.fill = 2;
        propObjetPane.add(labelCoefRestitution, gridBagConstraints);

        labelLargeurTable = new JLabel("Largeur de la table (cm): ");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        gridBagConstraints.fill = 2;
        propObjetPane.add(labelLargeurTable, gridBagConstraints);

        labelHauteurTable = new JLabel("Hauteur de la table (cm): ");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        gridBagConstraints.fill = 2;
        propObjetPane.add(labelHauteurTable, gridBagConstraints);

        inputLargeurTable.setText("0");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        gridBagConstraints.fill = 2;
        propObjetPane.add(inputLargeurTable , gridBagConstraints);

        inputHauteurTable.setText("0");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        gridBagConstraints.fill = 2;
        propObjetPane.add(inputHauteurTable, gridBagConstraints);

        // Ajout au panneau outils
        outilsSplitPane.setBottomComponent(propObjetPane);
        this.add(outilsSplitPane, BorderLayout.CENTER);

        setAucunProps();
    }

    public void afficherMessage(String message) {
        messageErreur.setText(message);
        messageErreur.setForeground(new Color(255, 0, 0));
    }

    public void resetMessage() {
        messageErreur.setForeground(Color.white);
    }

    public void setAucunProps() {
        propTitle.setVisible(false);
        labelObjetAAjouter.setVisible(false);
        inputObjetAAjouter.setVisible(false);
        labelPositionX.setVisible(false);
        inputPositionX.setVisible(false);
        labelPositionY.setVisible(false);
        inputPositionY.setVisible(false);
        labelRayon.setVisible(false);
        inputRayon.setVisible(false);
        labelCouleur.setVisible(false);
        colorPicker.setVisible(false);
        labelAngle.setVisible(false);
        inputAngle.setVisible(false);
        labelLongueur.setVisible(false);
        inputLongueur.setVisible(false);
        labelEpaisseur.setVisible(false);
        inputEpaisseur.setVisible(false);
        labelNumero.setVisible(false);
        inputNumero.setVisible(false);
        boutonOK.setVisible(false);
        inputCoefRestitution.setVisible(false);
        inputHauteurTable.setVisible(false);
        inputLargeurTable.setVisible(false);
        inputCinetiqueFriction.setVisible(false);
        inputDensiteBoules.setVisible(false);
        labelCoefRestitution.setVisible(false);
        labelCinetiqueFriction.setVisible(false);
        labelDensiteBoules.setVisible(false);
        labelHauteurTable.setVisible(false);
        labelLargeurTable.setVisible(false);
        labelCouleurPortail.setVisible(false);
        inputCouleurPortail.setVisible(false);
    }

    public void setPropsBouleAjouter() {
        propTitle.setText("Propriétés de la boule à ajouter");
        setAucunProps();
        propTitle.setVisible(true);
        labelObjetAAjouter.setVisible(true);
        inputObjetAAjouter.setVisible(true);
        labelPositionX.setVisible(true);
        inputPositionX.setVisible(true);
        inputPositionX.setEnabled(true);
        labelPositionY.setVisible(true);
        inputPositionY.setVisible(true);
        inputPositionY.setEnabled(true);
        labelRayon.setVisible(true);
        inputRayon.setVisible(true);
        labelCouleur.setVisible(true);
        colorPicker.setVisible(true);
        colorPicker.setEnabled(true);
        labelNumero.setVisible(true);
        inputNumero.setVisible(true);
        boutonOK.setVisible(true);
        inputPositionX.setText("0");
        inputPositionY.setText("0");
        setInputValue(inputRayon, fenetrePrincipale.getControleur().getRayonBoules(), 2.54);
        boutonOK.setText("Ajouter");
    }

    public void setPropsPocheAjouter() {
        propTitle.setText("Propriétés de la poche à ajouter");
        setAucunProps();
        propTitle.setVisible(true);
        labelObjetAAjouter.setVisible(true);
        inputObjetAAjouter.setVisible(true);
        labelPositionX.setVisible(true);
        inputPositionX.setVisible(true);
        inputPositionX.setEnabled(true);
        labelPositionY.setVisible(true);
        inputPositionY.setVisible(true);
        inputPositionY.setEnabled(true);
        labelRayon.setVisible(true);
        inputRayon.setVisible(true);
        inputRayon.setEnabled(true);
        boutonOK.setVisible(true);

        inputPositionX.setText("0");
        inputPositionY.setText("0");
        setInputValue(inputRayon, 1.5 * fenetrePrincipale.getControleur().getRayonBoules(), 2.54);
        boutonOK.setText("Ajouter");
    }

    public void setPropsMurAjouter() {
        propTitle.setText("Propriétés du mur d'obstacle à ajouter");
        setAucunProps();
        propTitle.setVisible(true);
        labelObjetAAjouter.setVisible(true);
        inputObjetAAjouter.setVisible(true);
        labelPositionX.setVisible(true);
        inputPositionX.setVisible(true);
        inputPositionX.setEnabled(true);
        labelPositionY.setVisible(true);
        inputPositionY.setVisible(true);
        inputPositionY.setEnabled(true);
        labelAngle.setVisible(true);
        inputAngle.setVisible(true);
        inputAngle.setEnabled(true);
        labelLongueur.setVisible(true);
        inputLongueur.setVisible(true);
        inputLongueur.setEnabled(true);
        labelEpaisseur.setVisible(true);
        inputEpaisseur.setVisible(true);
        inputEpaisseur.setEnabled(true);
        boutonOK.setVisible(true);

        inputPositionX.setText("0");
        inputPositionY.setText("0");
        labelAngle.setText("Angle (deg)");
        inputAngle.setText("0");
        setInputValue(inputLongueur, fenetrePrincipale.getControleur().getEpaisseurMursContour(), 2.54);
        setInputValue(inputEpaisseur, fenetrePrincipale.getControleur().getEpaisseurMursContour(), 2.54);
        boutonOK.setText("Ajouter");
    }

    public void setPropsPaquetBoulesColoreesAjouter() {
        propTitle.setText("Propriétés du paquet de boules colorées à ajouter");
        setAucunProps();
        propTitle.setVisible(true);
        labelObjetAAjouter.setVisible(true);
        inputObjetAAjouter.setVisible(true);
        labelPositionX.setVisible(true);
        inputPositionX.setVisible(true);
        inputPositionX.setEnabled(true);
        labelPositionY.setVisible(true);
        inputPositionY.setVisible(true);
        inputPositionY.setEnabled(true);
        labelAngle.setVisible(true);
        inputAngle.setVisible(true);
        inputAngle.setEnabled(true);
        boutonOK.setVisible(true);
        inputPositionX.setText("0");
        inputPositionY.setText("0");
        labelAngle.setText("Rotation (deg)");
        inputAngle.setText("0");
        boutonOK.setText("Ajouter");
    }

    public void setPropsPortailAjouter() {
        propTitle.setText("Propriétés du portail à ajouter");
        setAucunProps();
        propTitle.setVisible(true);
        labelObjetAAjouter.setVisible(true);
        inputObjetAAjouter.setVisible(true);
        labelPositionX.setVisible(true);
        inputPositionX.setVisible(true);
        inputPositionX.setEnabled(true);
        labelPositionY.setVisible(true);
        inputPositionY.setVisible(true);
        inputPositionY.setEnabled(true);
        labelAngle.setVisible(true);
        inputAngle.setVisible(true);
        inputAngle.setEnabled(true);
        labelLongueur.setVisible(true);
        inputLongueur.setVisible(true);
        inputLongueur.setEnabled(true);
        labelCouleurPortail.setVisible(true);
        inputCouleurPortail.setVisible(true);
        boutonOK.setVisible(true);

        inputPositionX.setText("0");
        inputPositionY.setText("0");
        labelAngle.setText("Angle (deg)");
        inputAngle.setText("0");
        setInputValue(inputLongueur, 1.5*fenetrePrincipale.getControleur().getRayonBoules(), 2.54);
        boutonOK.setText("Ajouter");
    }

    public void setPropsBouleModifier(BouleDTO boule) {
        propTitle.setText("Propriétés de la boule sélectionnée");
        setAucunProps();
        propTitle.setVisible(true);
        labelPositionX.setVisible(true);
        inputPositionX.setVisible(true);
        inputPositionX.setEnabled(true);
        labelPositionY.setVisible(true);
        inputPositionY.setVisible(true);
        inputPositionY.setEnabled(true);
        labelNumero.setVisible(true);
        inputNumero.setVisible(true);
        labelRayon.setVisible(true);
        inputRayon.setVisible(true);
        inputRayon.setEnabled(true);
        labelCouleur.setVisible(true);

        colorPicker.setVisible(true);
        colorPicker.setEnabled(true);
        boutonOK.setVisible(true);

        setInputValue(inputPositionX, boule.getPosition().getX(), 2.54);
        setInputValue(inputPositionY, boule.getPosition().getY(), 2.54);
        setInputValue(inputRayon, boule.getRayon(), 2.54);

        colorPicker.setCouleur(boule.getCouleur());

        boutonOK.setText("Modifier");

        inputPositionXInitial = getInputValue(inputPositionX, 2.54);
        inputPositionYInitial = getInputValue(inputPositionY, 2.54);
        inputRayonInitial = getInputValue(inputRayon, 2.54);
        couleurInitiale = colorPicker.getCouleur();

        labelMetrique();

        if (boule.isBouleBlancheOriginale()) {
            colorPicker.setEnabled(false);
        }
    }

    public void setPropsPocheModifier(PocheDTO poche) {
        propTitle.setText("Propriétés de la poche sélectionnée");
        setAucunProps();
        propTitle.setVisible(true);
        labelPositionX.setVisible(true);
        inputPositionX.setVisible(true);
        inputPositionX.setEnabled(true);
        labelPositionY.setVisible(true);
        inputPositionY.setVisible(true);
        inputPositionY.setEnabled(true);
        labelRayon.setVisible(true);
        inputRayon.setVisible(true);
        inputRayon.setEnabled(true);
        boutonOK.setVisible(true);
        setInputValue(inputPositionX, poche.getPosition().getX(), 2.54);
        setInputValue(inputPositionY, poche.getPosition().getY(), 2.54);
        setInputValue(inputRayon, poche.getRayon(), 2.54);

        boutonOK.setText("Modifier");


        inputPositionXInitial = getInputValue(inputPositionX, 2.54);
        inputPositionYInitial = getInputValue(inputPositionY, 2.54);
        inputRayonInitial = getInputValue(inputRayon, 2.54);
        labelMetrique();


    }

    public void setPropsBrisureMurModifier(BrisureMurDTO brisureMur) {
        propTitle.setText("Propriétés de la brisure de mur sélectionnée");
        propTitle.setVisible(true);
        labelPositionX.setVisible(true);
        inputPositionX.setVisible(true);
        inputPositionX.setEnabled(true);
        labelPositionY.setVisible(true);
        inputPositionY.setVisible(true);
        inputPositionY.setEnabled(true);

        if (brisureMur.isPourMurContour()) {
            labelAngle.setVisible(false);
            inputAngle.setVisible(false);
            inputAngle.setEnabled(false);
        }
        else {
            labelAngle.setVisible(true);
            inputAngle.setVisible(true);
            inputAngle.setEnabled(true);
        }

        boutonOK.setVisible(true);
        setInputValue(inputPositionX, brisureMur.getPosition().getX(), 2.54);
        setInputValue(inputPositionY, brisureMur.getPosition().getY(), 2.54);

        labelAngle.setText("Rotation (deg)");
        boutonOK.setText("Modifier");

        inputPositionXInitial = getInputValue(inputPositionX, 2.54);
        inputPositionYInitial = getInputValue(inputPositionY, 2.54);

        if (!brisureMur.isPourMurContour()) {
            setInputValue(inputAngle, 0, 2.54);
            inputAngleInitial = InputUtils.tryParseDouble(inputAngle.getText());
        }

        labelMetrique();
    }

    public void setPropsMurModifier(MurDTO mur) {
        propTitle.setText("Propriétés du mur sélectionné");
        setAucunProps();
        propTitle.setVisible(true);
        labelPositionX.setVisible(true);
        inputPositionX.setVisible(true);
        inputPositionX.setEnabled(true);
        labelPositionY.setVisible(true);
        inputPositionY.setVisible(true);
        inputPositionY.setEnabled(true);
        labelAngle.setVisible(true);
        inputAngle.setVisible(true);
        inputAngle.setEnabled(true);
        labelLongueur.setVisible(true);
        inputLongueur.setVisible(true);
        inputLongueur.setEnabled(true);
        labelEpaisseur.setVisible(true);
        inputEpaisseur.setVisible(true);
        inputEpaisseur.setEnabled(true);
        boutonOK.setVisible(true);

        setInputValue(inputPositionX, mur.getPosition().getX()+(mur.getLongueur()/2)*Math.cos(mur.getAngle()), 2.54);
        setInputValue(inputPositionY, mur.getPosition().getY()+(mur.getLongueur()/2)*Math.sin(mur.getAngle()), 2.54);
        labelAngle.setText("Angle (deg)");
        inputAngle.setValue(360.0 * mur.getAngle() / (2.0 * Math.PI));
        setInputValue(inputLongueur, mur.getLongueur(), 2.54);
        setInputValue(inputEpaisseur, mur.getEpaisseur(), 2.54);
        boutonOK.setText("Modifier");

        inputPositionXInitial = getInputValue(inputPositionX, 2.54);
        inputPositionYInitial = getInputValue(inputPositionY, 2.54);
        inputAngleInitial = InputUtils.tryParseDouble(inputAngle.getText());
        inputLongueurInitial = getInputValue(inputLongueur, 2.54);
        inputEpaisseurInitial = getInputValue(inputEpaisseur, 2.54);

        labelMetrique();

        if (mur.isMurContour()) {
            inputPositionX.setEnabled(false);
            inputPositionY.setEnabled(false);
            inputAngle.setEnabled(false);
            inputLongueur.setEnabled(false);
        }
    }

    public void setPropsPortailModifier(PortailDTO portail) {
        propTitle.setText("Propriétés du portail sélectionné");
        setAucunProps();
        propTitle.setVisible(true);
        labelPositionX.setVisible(true);
        inputPositionX.setVisible(true);
        inputPositionX.setEnabled(true);
        labelPositionY.setVisible(true);
        inputPositionY.setVisible(true);
        inputPositionY.setEnabled(true);
        labelAngle.setVisible(true);
        inputAngle.setVisible(true);
        inputAngle.setEnabled(true);
        labelLongueur.setVisible(true);
        inputLongueur.setVisible(true);
        inputLongueur.setEnabled(true);
        boutonOK.setVisible(true);

        setInputValue(inputPositionX, portail.getPosition().getX(), 2.54);
        setInputValue(inputPositionY, portail.getPosition().getY(), 2.54);
        labelAngle.setText("Angle (deg)");
        inputAngle.setValue(360.0 * portail.getAngle() / (2.0 * Math.PI));
        setInputValue(inputLongueur, portail.getLongueur(), 2.54);
        boutonOK.setText("Modifier");

        inputPositionXInitial = getInputValue(inputPositionX, 2.54);
        inputPositionYInitial = getInputValue(inputPositionY, 2.54);
        inputAngleInitial = InputUtils.tryParseDouble(inputAngle.getText());
        inputLongueurInitial = getInputValue(inputLongueur, 2.54);

        labelMetrique();
    }

    public void setPropsPhysique() {
        propTitle.setText("Propriétés de la table");
        setAucunProps();
        propTitle.setVisible(true);
        inputCoefRestitution.setVisible(true);
        inputHauteurTable.setVisible(true);
        inputLargeurTable.setVisible(true);
        inputCinetiqueFriction.setVisible(true);
        inputDensiteBoules.setVisible(true);
        labelCoefRestitution.setVisible(true);
        labelLargeurTable.setVisible(true);
        labelHauteurTable.setVisible(true);
        labelCinetiqueFriction.setVisible(true);
        labelDensiteBoules.setVisible(true);
        boutonOK.setVisible(true);

        inputCinetiqueFriction.setValue(fenetrePrincipale.getControleur().getCinetiqueFriction());
        inputCoefRestitution.setValue(fenetrePrincipale.getControleur().getCoefRestitution());
        inputDensiteBoules.setValue(fenetrePrincipale.getControleur().getDensiteBoules());

        setInputValue(inputHauteurTable, fenetrePrincipale.getControleur().getHauteurTable(), 2.54);
        setInputValue(inputLargeurTable, fenetrePrincipale.getControleur().getLargeurTable(), 2.54);

        boutonOK.setText("Modifier");
        labelMetrique();
    }

    public double getInputDensiteBoules() { return InputUtils.tryParseDouble(inputDensiteBoules.getText()); }

    public double getInputCoefRestitution() { return InputUtils.tryParseDouble(inputCoefRestitution.getText()); }

    public double getInputCinetiqueFriction() {
        return InputUtils.tryParseDouble(inputCinetiqueFriction.getText());
    }

    public double getInputPositionX() {
        return getInputValue(inputPositionX, 2.54);
    }

    public double getInputPositionY() {
        return getInputValue(inputPositionY, 2.54);
    }

    public double getInputHauteurTable() {
        return getInputValue(inputHauteurTable, 2.54);
    }

    public double getInputLargeurTable() {
        return getInputValue(inputLargeurTable, 2.54);
    }

    public double getInputRayon() {
        return getInputValue(inputRayon, 2.54);
    }

    public Color getInputCouleur() {
        return colorPicker.getCouleur();
    }

    public double getInputAngle() {
        return (2.0 * Math.PI) * InputUtils.tryParseDouble(inputAngle.getText()) / 360.0;
    }

    public double getInputLongueur() {
        return getInputValue(inputLongueur, 2.54);
    }

    public double getInputEpaisseur() {
        return getInputValue(inputEpaisseur, 2.54);
    }

    public double getInputPositionXInitial() {
        return inputPositionXInitial;
    }

    public double getInputPositionYInitial() {
        return inputPositionYInitial;
    }

    public double getInputRayonInitial() {
        return inputRayonInitial;
    }

    public Color getCouleurInitiale() {
        return couleurInitiale;
    }

    public double getInputAngleInitial() { return (2.0 * Math.PI) * inputAngleInitial / 360.0; }

    public double getInputLongueurInitial() {
        return inputLongueurInitial;
    }

    public double getInputEpaisseurInitial() {
        return inputEpaisseurInitial;
    }

    public boolean getCouleurPortail() { return inputCouleurPortail.getSelectedIndex() == 0; }

    public void itemStateChanged(ItemEvent evt) {
        if (evt.getSource() == inputObjetAAjouter) {
            if (inputObjetAAjouter.getSelectedIndex() == 0) {
                setPropsBouleAjouter();
                resetMessage();
            } else if (inputObjetAAjouter.getSelectedIndex() == 1) {
                setPropsPocheAjouter();
                resetMessage();
            } else if (inputObjetAAjouter.getSelectedIndex() == 2) {
                setPropsMurAjouter();
                resetMessage();
            } else if (inputObjetAAjouter.getSelectedIndex() == 3) {
                setPropsPaquetBoulesColoreesAjouter();
                resetMessage();
            } else if (inputObjetAAjouter.getSelectedIndex() == 4) {
                setPropsPortailAjouter();
                resetMessage();
            } else throw new RuntimeException();
        } else throw new RuntimeException();
    }

    public int getIndexObjetAAjouter() {
        return inputObjetAAjouter.getSelectedIndex();
    }

    public void setIndexObjetAAjouter(int index) {
        inputObjetAAjouter.setSelectedIndex(index);
    }

    public void setTextBoutonOK(String text) {
        boutonOK.setText(text);
    }

    public void labelMetrique() {
        if (fenetrePrincipale.getMetrique()) {
            labelPositionX.setText("Position x (cm)");
            labelPositionY.setText("Position y (cm)");
            labelRayon.setText("Rayon (cm)");
            labelLongueur.setText("Longueur (cm)");
            labelEpaisseur.setText("Épaisseur (cm)");
            labelHauteurTable.setText("Hauteur de la table (cm): ");
            labelLargeurTable.setText("Largeur de la table (cm): ");
            labelLargeurTable.setText("Largeur de la table (cm): ");
            labelDensiteBoules.setText("Densité des boules (kg/m³): ");

            ((ImperialMetricInputValidator) inputPositionX.getInputVerifier()).setMetrique(fenetrePrincipale.getMetrique());
            ((ImperialMetricInputValidator) inputPositionY.getInputVerifier()).setMetrique(fenetrePrincipale.getMetrique());
            ((ImperialMetricInputValidator) inputRayon.getInputVerifier()).setMetrique(fenetrePrincipale.getMetrique());
            ((ImperialMetricInputValidator) inputEpaisseur.getInputVerifier()).setMetrique(fenetrePrincipale.getMetrique());
            ((ImperialMetricInputValidator) inputLongueur.getInputVerifier()).setMetrique(fenetrePrincipale.getMetrique());
            ((ImperialMetricInputValidator) inputLargeurTable.getInputVerifier()).setMetrique(fenetrePrincipale.getMetrique());
            ((ImperialMetricInputValidator) inputHauteurTable.getInputVerifier()).setMetrique(fenetrePrincipale.getMetrique());
            ((ImperialMetricInputValidator) inputDensiteBoules.getInputVerifier()).setMetrique(fenetrePrincipale.getMetrique());

        } else {
            labelPositionX.setText("Position x (po)");
            labelPositionY.setText("Position y (po)");
            labelRayon.setText("Rayon (po)");
            labelLongueur.setText("Longueur (po)");
            labelEpaisseur.setText("Épaisseur (po)");
            labelHauteurTable.setText("Hauteur de la table (po): ");
            labelLargeurTable.setText("Largeur de la table (po): ");
            labelDensiteBoules.setText("Densité des boules (lb/ft³): ");

            ((ImperialMetricInputValidator) inputPositionX.getInputVerifier()).setMetrique(fenetrePrincipale.getMetrique());
            ((ImperialMetricInputValidator) inputPositionY.getInputVerifier()).setMetrique(fenetrePrincipale.getMetrique());
            ((ImperialMetricInputValidator) inputRayon.getInputVerifier()).setMetrique(fenetrePrincipale.getMetrique());
            ((ImperialMetricInputValidator) inputEpaisseur.getInputVerifier()).setMetrique(fenetrePrincipale.getMetrique());
            ((ImperialMetricInputValidator) inputLongueur.getInputVerifier()).setMetrique(fenetrePrincipale.getMetrique());
            ((ImperialMetricInputValidator) inputLargeurTable.getInputVerifier()).setMetrique(fenetrePrincipale.getMetrique());
            ((ImperialMetricInputValidator) inputHauteurTable.getInputVerifier()).setMetrique(fenetrePrincipale.getMetrique());
            ((ImperialMetricInputValidator) inputDensiteBoules.getInputVerifier()).setMetrique(fenetrePrincipale.getMetrique());
        }
    }

    public void changerMesure() {
        if (fenetrePrincipale.getMetrique()) {
            //Imperial -> Metrique
            setInputValue(inputPositionX, InputUtils.fractionToDouble(inputPositionX.getText(), 2.54), 2.54);
            setInputValue(inputPositionY, InputUtils.fractionToDouble(inputPositionY.getText(), 2.54), 2.54);
            setInputValue(inputRayon, InputUtils.fractionToDouble(inputRayon.getText(), 2.54), 2.54);
            setInputValue(inputEpaisseur, InputUtils.fractionToDouble(inputEpaisseur.getText(), 2.54), 2.54);
            setInputValue(inputLongueur, InputUtils.fractionToDouble(inputLongueur.getText(), 2.54), 2.54);
            setInputValue(inputLargeurTable, InputUtils.fractionToDouble(inputLargeurTable.getText(), 2.54), 2.54);
            setInputValue(inputHauteurTable, InputUtils.fractionToDouble(inputHauteurTable.getText(), 2.54), 2.54);
            setInputValue(inputDensiteBoules, InputUtils.fractionToDouble(inputDensiteBoules.getText(), 16.018463), 16.018463);
        } else {
            //Metrique -> Imperial
            setInputValue(inputPositionX, InputUtils.tryParseDouble(inputPositionX.getText()), 2.54);
            setInputValue(inputPositionY, InputUtils.tryParseDouble(inputPositionY.getText()), 2.54);
            setInputValue(inputRayon, InputUtils.tryParseDouble(inputRayon.getText()), 2.54);
            setInputValue(inputEpaisseur, InputUtils.tryParseDouble(inputEpaisseur.getText()), 2.54);
            setInputValue(inputLongueur, InputUtils.tryParseDouble(inputLongueur.getText()), 2.54);
            setInputValue(inputLargeurTable, InputUtils.tryParseDouble(inputLargeurTable.getText()), 2.54);
            setInputValue(inputHauteurTable, InputUtils.tryParseDouble(inputHauteurTable.getText()), 2.54);
            setInputValue(inputDensiteBoules, InputUtils.tryParseDouble(inputDensiteBoules.getText()), 16.018463);
        }
    }

    private void setInputValue(JTextField textField, double value, double ratio) {
        if (fenetrePrincipale.getMetrique()) {
            textField.setText(formatDecimal.format(value));
        } else {
            textField.setText(InputUtils.doubleToFraction(value, ratio));
        }
    }

    private double getInputValue(JTextField textField, double ratio) {
        if(textField.getInputVerifier().verify(textField)) {
            if(fenetrePrincipale.getMetrique()){
                return Double.parseDouble(textField.getText());
            } else {
                return InputUtils.fractionToDouble(textField.getText(), ratio);
            }
        } else {
            afficherMessage("Entrée invalide");
            throw new NumberFormatException();
        }
    }

    public int getInputNumero(){
        return Integer.parseInt(inputNumero.getText());
    }

    public void setSelectionner(){
        selectionner.setSelected(true);
    }
}
