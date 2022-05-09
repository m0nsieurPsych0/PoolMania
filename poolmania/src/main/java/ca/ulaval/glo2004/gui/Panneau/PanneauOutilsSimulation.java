package ca.ulaval.glo2004.gui.Panneau;

import ca.ulaval.glo2004.gui.EcouteurEvt.Simulation.SimulationPannOutiEvn;
import ca.ulaval.glo2004.gui.MainWindow;
import ca.ulaval.glo2004.utils.InputUtils;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class PanneauOutilsSimulation extends JPanel{

    private MainWindow fenetrePrincipale;
    private Dimension initialDimensions;

    private JPanel outilsObjets;
    private JToggleButton baguetteSouris, frapper;
    private JPanel outilsFlow;
    private JPanel outilsMode;
    private JButton changerMode;
    private JButton replacerBoule;
    private GridBagConstraints gridBagConstraints;
    private GridBagLayout panneauOutilsLayout;
    private ButtonGroup boutonOutils;
    private SimulationPannOutiEvn simulationPannOutiEvn;
    private JPanel outilsPane;
    private JSplitPane outilsSplitPane;
    private JPanel propSimulationPane;
    private JLabel labelAngleTir, labelForceTir, propTitle;
    private JFormattedTextField inputAngleTir, inputForceTir;
    private Dimension jtextfieldPreferedSize = new Dimension(100, 22);
    private JLabel messageErreur;
    private DecimalFormat formatDecimal;

    public PanneauOutilsSimulation(MainWindow fenetrePrincipale) {
        this.fenetrePrincipale = fenetrePrincipale;
        init();
    }


    private void init() {
        // Init objets
        gridBagConstraints = new GridBagConstraints();
        panneauOutilsLayout = new GridBagLayout();
        outilsObjets = new JPanel();
        baguetteSouris = new JToggleButton();
        baguetteSouris.setSelected(true);
        frapper = new JToggleButton();
        outilsFlow = new JPanel();
        outilsMode = new JPanel();
        changerMode = new JButton();
        replacerBoule = new JButton();
        boutonOutils = new ButtonGroup();
        simulationPannOutiEvn = new SimulationPannOutiEvn(fenetrePrincipale);
        propSimulationPane = new JPanel();
        propTitle = new JLabel();

        //https://stackoverflow.com/questions/16062666/formatting-numbers-without-locale
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        formatDecimal = new DecimalFormat("##.##");
        formatDecimal.setDecimalFormatSymbols(otherSymbols);

        //Propriété de la simulation (force de coup, angle de tir)
        labelAngleTir = new JLabel();
        inputAngleTir = new JFormattedTextField(formatDecimal);
        labelForceTir = new JLabel();
        inputForceTir = new JFormattedTextField(formatDecimal);

        outilsSplitPane = new JSplitPane();
        outilsPane = new JPanel();

        messageErreur = new JLabel();
        buildUp();
    }

    private void buildUp() {

        this.setBackground(new Color(255, 255, 255));
        this.setDoubleBuffered(false);
        this.setMinimumSize(new Dimension(163, 350));
        this.setLayout(new BorderLayout());

        outilsSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        outilsSplitPane.setContinuousLayout(true);
        outilsSplitPane.setDividerSize(4);

        // Proportion du splitpane de la barre d'outils
        outilsSplitPane.setResizeWeight(0.35);

        outilsPane.setBackground(new Color(255, 255, 255));
        outilsPane.setAutoscrolls(true);
        outilsPane.setPreferredSize(new Dimension(166, 350));
        outilsPane.setMinimumSize(new Dimension(166, 350));
        outilsPane.setLayout(new GridBagLayout());

        outilsObjets.setBackground(new Color(255, 255, 255));
        GridBagLayout outilsObjetsLayout = new GridBagLayout();
        outilsObjetsLayout.columnWidths = new int[]{1};
        outilsObjets.setLayout(outilsObjetsLayout);

        baguetteSouris.setText("Utiliser la baguette avec la souris");
        baguetteSouris.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulationPannOutiEvn.baguetteSouris(e);
            }
        });
        baguetteSouris.setHorizontalTextPosition(SwingConstants.CENTER);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 35;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.BASELINE;
        gridBagConstraints.insets = new Insets(0, 0, 11, 0);
        outilsObjets.add(baguetteSouris, gridBagConstraints);
        boutonOutils.add(baguetteSouris);

        frapper.setText("Frapper manuellement");
        frapper.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulationPannOutiEvn.frapperManuelle(e);
            }
        });
        frapper.setHorizontalTextPosition(SwingConstants.CENTER);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 35;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.BASELINE;
        gridBagConstraints.insets = new Insets(0, 0, 11, 0);
        outilsObjets.add(frapper, gridBagConstraints);
        boutonOutils.add(frapper);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.1;
        outilsPane.add(outilsObjets, gridBagConstraints);

        outilsFlow.setBackground(new Color(255, 255, 255));
        outilsFlow.setLayout(new GridBagLayout());

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.1;
        outilsPane.add(outilsFlow, gridBagConstraints);

        outilsMode.setBackground(new Color(255, 255, 255));
        outilsMode.setLayout(new GridBagLayout());


        replacerBoule.setText("Replacer la boule blanche");
        replacerBoule.setHorizontalTextPosition(SwingConstants.CENTER);
        replacerBoule.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (fenetrePrincipale.ballesImmobiles())
                    fenetrePrincipale.getControleur().retirerBouleBlanche();
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new Insets(0, 0, 25, 0);
        outilsMode.add(replacerBoule, gridBagConstraints);

        changerMode.setText("Changer de mode");
        changerMode.setHorizontalTextPosition(SwingConstants.CENTER);
        changerMode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                simulationPannOutiEvn.changerModeActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(15, 0, 5, 0);
        outilsMode.add(changerMode, gridBagConstraints);
        
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.1;
        outilsPane.add(outilsMode, gridBagConstraints);

        outilsSplitPane.setTopComponent(outilsPane);

        propSimulationPane.setBackground(new Color(255, 255, 255));
        propSimulationPane.setMinimumSize(new Dimension(166, 400));
        propSimulationPane.setName("propSimulationPane");
        propSimulationPane.setLayout(new GridBagLayout());

        propTitle.setFont(new Font("Tahoma", Font.BOLD, 14));
        propTitle.setText("Propriétés de la simulation");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = GridBagConstraints.ABOVE_BASELINE;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new Insets(10, 0, 20, 0);
        propSimulationPane.add(propTitle, gridBagConstraints);


        // Angle de tir
        labelAngleTir.setText("Angle de tir");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 0, 5, 5);
        propSimulationPane.add(labelAngleTir, gridBagConstraints);

        setInputValue(inputAngleTir, 0, 2.54);
        inputAngleTir.setPreferredSize(jtextfieldPreferedSize);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 5, 5, 0);
        gridBagConstraints.fill = 2;
        propSimulationPane.add(inputAngleTir, gridBagConstraints);

        // Force de tir
        labelForceTir.setText("Force de tir");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 0, 5, 5);
        propSimulationPane.add(labelForceTir, gridBagConstraints);

        inputForceTir.setPreferredSize(jtextfieldPreferedSize);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(5, 5, 0, 0);
        gridBagConstraints.fill = 2;
        propSimulationPane.add(inputForceTir, gridBagConstraints);

        // Message Erreur
        messageErreur.setText("Place holder Text");
        messageErreur.setForeground(Color.white);
        messageErreur.setVisible(true);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = GridBagConstraints.CENTER;
        gridBagConstraints.anchor = GridBagConstraints.BELOW_BASELINE;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new Insets(0, 0, 10, 0);
        propSimulationPane.add(messageErreur, gridBagConstraints);


        // Ajout au panneau outils
        outilsSplitPane.setBottomComponent(propSimulationPane);
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
        labelAngleTir.setVisible(false);
        inputAngleTir.setVisible(false);
        labelForceTir.setVisible(false);
        inputForceTir.setVisible(false);
    }

    public void setPropsSimulation() {
        propTitle.setVisible(true);
        labelAngleTir.setVisible(true);
        inputAngleTir.setVisible(true);
        inputAngleTir.setEnabled(false);
        labelForceTir.setVisible(true);

        inputForceTir.setVisible(true);
        inputForceTir.setEnabled(false);

        setInputValue(inputAngleTir, 0, 2.54);
    }


    public void setPropsSimulationModifier() {
        propTitle.setText("Propriétés de la boule sélectionnée");
        propTitle.setVisible(true);
        labelAngleTir.setVisible(true);
        inputAngleTir.setVisible(true);
        inputAngleTir.setEnabled(true);
        labelForceTir.setVisible(true);


        inputForceTir.setVisible(true);
        inputForceTir.setEnabled(true);

        //Todo Déterminer si on veut avoir la position dans les propriétés aussi
        // Ce n'est probablement pas nécessaire considérant que c'est déjà dans la barre de status pour le curseur
//


    }

    public void setInputAngle(double angle) {
        inputAngleTir.setText(formatDecimal.format(angle));
    }

    public double getInputAngle() {
        return (2.0 * Math.PI) * InputUtils.tryParseDouble(inputAngleTir.getText()) / 360.0;
    }

    public double getInputForceTir() {
        return InputUtils.tryParseDouble(inputForceTir.getText());
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

    public void setForceTir(double force) {
        if (force > fenetrePrincipale.getControleur().getRayonBoules() && force <= 200*fenetrePrincipale.getControleur().getPoidsBoules()) {
            inputForceTir.setText(formatDecimal.format(force));
        } else if(force < fenetrePrincipale.getControleur().getRayonBoules()){
            inputForceTir.setText(formatDecimal.format(fenetrePrincipale.getControleur().getRayonBoules()+0.01));
        } else {
            inputForceTir.setText(formatDecimal.format(200*fenetrePrincipale.getControleur().getPoidsBoules()));
        }
    }

}
