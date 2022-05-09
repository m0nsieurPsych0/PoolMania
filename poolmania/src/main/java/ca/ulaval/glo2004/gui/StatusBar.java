package ca.ulaval.glo2004.gui;

import ca.ulaval.glo2004.utils.DimensionDomaine;
import ca.ulaval.glo2004.utils.InputUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StatusBar extends JPanel implements Runnable{
    private MainWindow fenetrePrincipale = null;
    private DimensionDomaine dimensionDomaine = DimensionDomaine.getInstance();
    private JPanel zoomPan, tailleGrillePan, msgStatusPan, coordPan;
    private GridBagConstraints gridBagConstraints;
    private GridBagLayout gridBagLayout;
    private JTextField inputTaille;
    private JLabel mouseCoord, labelTaille, zoomLevel, msgInfo, zoomValue;
    private int preferedWidth = 300;
    private int preferedHeight = (int) (dimensionDomaine.getLimites().height * 0.025);
    private ImperialMetricInputValidator immv;
    private DecimalFormat formatDecimal;
    private StringBuilder stringBuilder;

    public StatusBar(MainWindow fenetrePrincipale) {
        this.fenetrePrincipale = fenetrePrincipale;
        formatDecimal = new DecimalFormat();
        BuildUp();
    }


    public void setMessage(String msg) {
        //Todo Ajouter du formating pour rendre ça plus pretty
        msgInfo.setText(msg);
        msgInfo.setVisible(true);
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(this, 5, TimeUnit.SECONDS);

    }

    @Override
    public void run() {
        msgInfo.setVisible(false);
    }


    public void changerMesure(){
        if (fenetrePrincipale.getMetrique()){
            setInputValue(inputTaille, InputUtils.fractionToDouble(inputTaille.getText(), 2.54));
        }
        else{
            setInputValue(inputTaille, InputUtils.tryParseDouble(inputTaille.getText()));
        }
    }

    public void labelMetrique(){
        if (fenetrePrincipale.getMetrique()) {
            labelTaille.setText("Taille de la grille (cm): ");
            ((ImperialMetricInputValidator) inputTaille.getInputVerifier()).setMetrique(fenetrePrincipale.getMetrique());
        }
        else{
            labelTaille.setText("Taille de la grille (po): ");
            ((ImperialMetricInputValidator) inputTaille.getInputVerifier()).setMetrique(fenetrePrincipale.getMetrique());
        }
    }

    public void changerGrille() {
        if (!fenetrePrincipale.getBoolGrilleActive()) {
            inputTaille.setEnabled(false);
        } else {
            inputTaille.setEnabled(true);
        }
    }

    private void setInputValue(JTextField textField, double value) {
        if (fenetrePrincipale.getMetrique()) {
            textField.setText(formatDecimal.format(value));
        } else {
            textField.setText(InputUtils.doubleToFraction(value, 2.54));
        }
    }


    private void initMouseCoord(){
        coordPan = new JPanel();
        mouseCoord = new JLabel();
        coordPan.setOpaque(false);
        coordPan.setPreferredSize(new Dimension(preferedWidth, preferedHeight));
        coordPan.setMinimumSize(new Dimension(preferedWidth, preferedHeight));
//        coordPan.setBorder(BorderFactory.createLineBorder(Color.PINK));

        mouseCoord.setText("[0.0 ; 0.0]");

        GroupLayout coordLayout = new GroupLayout(coordPan);
        coordPan.setLayout(coordLayout);
        coordLayout.setHorizontalGroup(
                coordLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(coordLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addGroup(coordLayout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(mouseCoord)
                                        .addGap(0, 0, Short.MAX_VALUE)))
        );
        coordLayout.setVerticalGroup(
                coordLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(coordLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addGroup(coordLayout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(mouseCoord)
                                        .addGap(0, 0, Short.MAX_VALUE)))
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        this.add(coordPan, gridBagConstraints);
    }
    public void setMouseCoord(Point2D point) {
        if (fenetrePrincipale.getMetrique()) {
            stringBuilder = new StringBuilder("[" + (int) point.getX() + " cm , " + (int) point.getY() + " cm]");
        } else {
            stringBuilder = new StringBuilder("[" + (int) (point.getX() / 2.54) + " po , " + (int) (point.getY() / 2.54) + " po]");
        }
        mouseCoord.setText(stringBuilder.toString());
        this.revalidate();
        this.repaint();
    }

    private void initZoomLevel(){
        zoomPan = new JPanel();
        zoomValue = new JLabel();
        zoomPan.setOpaque(false);

        zoomPan.setPreferredSize(new Dimension(preferedWidth, preferedHeight));
        zoomPan.setMinimumSize(new Dimension(preferedWidth, preferedHeight));

        zoomValue.setText("Zoom: 100%");

        GroupLayout zoomPanLayout = new GroupLayout(zoomPan);
        zoomPan.setLayout(zoomPanLayout);
        zoomPanLayout.setHorizontalGroup(
                zoomPanLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(zoomPanLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(zoomPanLayout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(zoomValue)
                                        .addGap(0, 0, Short.MAX_VALUE)))
        );
        zoomPanLayout.setVerticalGroup(
                zoomPanLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(zoomPanLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(zoomPanLayout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(zoomValue)
                                        .addGap(0, 0, Short.MAX_VALUE)))
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        this.add(zoomPan, gridBagConstraints);
    }
    public void setZoomLevel(int zoomValue){
        switch (zoomValue){
            case 0:
                this.zoomValue.setText("Zoom: < 1%");
                break;
            default:
                stringBuilder = new StringBuilder("Zoom: " + zoomValue + "%");
                this.zoomValue.setText(stringBuilder.toString());
        }
        this.revalidate();
        this.repaint();
    }

    private void initTailleGrille(){
        tailleGrillePan = new JPanel();
        labelTaille = new JLabel();
        inputTaille = new JTextField();

        tailleGrillePan.setAlignmentY(0.0F);
        tailleGrillePan.setOpaque(false);
        tailleGrillePan.setLayout(new java.awt.GridBagLayout());

        labelTaille.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelTaille.setText("Taille de la grille (cm): ");
        labelTaille.setAlignmentY(0.0F);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        tailleGrillePan.add(labelTaille, gridBagConstraints);

        inputTaille.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        inputTaille.setAlignmentX(0.0F);
        inputTaille.setAlignmentY(0.0F);
        inputTaille.setMinimumSize(new java.awt.Dimension(75, (preferedHeight - (int)(((double)preferedHeight/100) * 20))));
        inputTaille.setPreferredSize(new java.awt.Dimension(75, (preferedHeight - (int)(((double)preferedHeight/100) * 20))));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        tailleGrillePan.add(inputTaille, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.1;
        this.add(tailleGrillePan, gridBagConstraints);
    }
    private void initInputTaille() {
        immv = new ImperialMetricInputValidator(fenetrePrincipale.getMetrique());
        inputTaille.setInputVerifier(immv);
        inputTaille.setToolTipText("Taille des cases de la grille");
        setInputValue(inputTaille, 30);

        //changer taille grille
        inputTaille.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                if (inputTaille.getInputVerifier().verify(inputTaille)) {
                    if (fenetrePrincipale.getMetrique()) {
                        fenetrePrincipale.getControleur().setTailleCase(Double.parseDouble(inputTaille.getText()));
                    }
                    else {
                        fenetrePrincipale.getControleur().setTailleCase(InputUtils.fractionToDouble(inputTaille.getText(), 2.54));
                    }
                    if (fenetrePrincipale.getControleur().getTailleCase() >= 5 / fenetrePrincipale.getControleur().getScale()) {
                        fenetrePrincipale.repaint();
                    }
                }
            }

            public void removeUpdate(DocumentEvent e) {
                if (inputTaille.getInputVerifier().verify(inputTaille)) {
                    if (fenetrePrincipale.getMetrique()) {
                        fenetrePrincipale.getControleur().setTailleCase(Double.parseDouble(inputTaille.getText()));
                    }
                    else {
                        fenetrePrincipale.getControleur().setTailleCase(InputUtils.fractionToDouble(inputTaille.getText(), 2.54));
                    }
                    if (fenetrePrincipale.getControleur().getTailleCase() >= 5 / fenetrePrincipale.getControleur().getScale()) {
                        fenetrePrincipale.repaint();
                    }
                }
            }

            public void insertUpdate(DocumentEvent e) {
                if (inputTaille.getInputVerifier().verify(inputTaille)) {
                    if (fenetrePrincipale.getMetrique()) {
                        fenetrePrincipale.getControleur().setTailleCase(Double.parseDouble(inputTaille.getText()));
                    }
                    else {
                        fenetrePrincipale.getControleur().setTailleCase(InputUtils.fractionToDouble(inputTaille.getText(), 2.54));
                    }
                    if (fenetrePrincipale.getControleur().getTailleCase() >= 5 / fenetrePrincipale.getControleur().getScale()) {
                        fenetrePrincipale.repaint();
                    }
                }
            }
        });
        labelTaille.setVisible(true);
        inputTaille.setEnabled(false);
    }

    private void initMsgStatus(){
        msgStatusPan = new JPanel();
        msgInfo = new JLabel();
        msgInfo.setVisible(false);
        msgStatusPan.setOpaque(false);

        msgStatusPan.setPreferredSize(new Dimension((preferedWidth * 2), preferedHeight));
        msgStatusPan.setMinimumSize(new Dimension((preferedWidth * 2), preferedHeight));

        GroupLayout msgStatusLayout = new GroupLayout(msgStatusPan);
        msgStatusPan.setLayout(msgStatusLayout);
        msgStatusLayout.setHorizontalGroup(
                msgStatusLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(msgStatusLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(msgStatusLayout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(msgInfo)
                                        .addGap(0, 0, Short.MAX_VALUE)))
        );
        msgStatusLayout.setVerticalGroup(
                msgStatusLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(msgStatusLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(msgStatusLayout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(msgInfo)
                                        .addGap(0, 0, Short.MAX_VALUE)))
        );


        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.1;
        this.add(msgStatusPan, gridBagConstraints);
    }

    private void BuildUp(){

        this.setBorder(BorderFactory.createEtchedBorder());
        this.setMinimumSize(new Dimension(1000, preferedHeight));
        this.setPreferredSize(new Dimension(DimensionDomaine.getInstance().width, preferedHeight));
        this.setRequestFocusEnabled(false);

        GridBagLayout statusBarLayout = new GridBagLayout();
        statusBarLayout.columnWidths = new int[] {0, 5, 0, 5, 0, 5, 0};
        statusBarLayout.rowHeights = new int[] {0};
        this.setLayout(statusBarLayout);

        initMouseCoord();
        initZoomLevel();
        initTailleGrille();
        initInputTaille();
        initMsgStatus();

    }


}
