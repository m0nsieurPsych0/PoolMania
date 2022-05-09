package ca.ulaval.glo2004.gui;
import javax.swing.*;
import java.util.regex.Pattern;

//https://stackoverflow.com/questions/2749521/how-to-validate-a-jtextfield
public class ImperialMetricInputValidator extends InputVerifier {

    private boolean metrique;
    private Pattern patternMetrique;
    private Pattern patternImperial;

    public ImperialMetricInputValidator(boolean metrique) {
        this.metrique = metrique;
        patternImperial = Pattern.compile("((-)?([0-9]+)(\\/[1-9]([0-9]+)?|([1-9][0-9]+))?)?(( [0-9]+\\/[1-9]([0-9]+)?|([1-9][0-9]+))?)");
        //https://regexland.com/regex-decimal-numbers/
        patternMetrique = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
    }

    @Override
    public boolean verify(JComponent input) {

        String text = ((JTextField) input).getText();

        if(metrique){
            return patternMetrique.matcher(text).matches();
        } else {
            return patternImperial.matcher(text).matches() && !text.isEmpty();
        }

    }

    public void setMetrique(boolean metrique) {
        this.metrique = metrique;
    }
}
