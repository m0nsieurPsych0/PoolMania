package ca.ulaval.glo2004.utils;

public class InputUtils {

    public static double fractionToDouble(String input, double ratio) {
        input = input.trim();
        if(input.contains("/")) {
            if(input.contains(" ")) {
                String[] strings = input.split("\\s");
                String[] stringsFraction = strings[1].split("/");
                if(Double.parseDouble(strings[0]) < 0){
                    return (Double.parseDouble(strings[0]) - Double.parseDouble(stringsFraction[0])/Double.parseDouble(stringsFraction[1]))*ratio;
                } else {
                    return (Double.parseDouble(strings[0]) + Double.parseDouble(stringsFraction[0])/Double.parseDouble(stringsFraction[1]))*ratio;
                }
            }else {
                String[] stringsFraction = input.split("/");
                return (Double.parseDouble(stringsFraction[0])/Double.parseDouble(stringsFraction[1]))*ratio;
            }
        } else {
            return Double.parseDouble(input)*ratio;
        }
    }

    public static String doubleToFraction(double fraction, double ratio) {
        double enPouce = fraction/ratio;
        int partieInt = (int)(enPouce);
        //https://stackoverflow.com/questions/34367362/java-get-first-2-decimal-digits-of-a-double
        int partieFractionelle = (int) Math.floor((enPouce - partieInt) * 1000000);

        if(partieFractionelle == 0){
            return Integer.toString(partieInt);
        } else {
            //TODO Prendre la plus petite fraction
            String fractionHaut = asFraction(Math.abs(partieFractionelle), 1000000);

            if(fractionHaut.split("/")[0].length() > 3){
                partieFractionelle = (int) Math.floor((enPouce - partieInt) * 100);
                fractionHaut = asFraction(Math.abs(partieFractionelle), 100);
            }

            return (partieInt) + " " + fractionHaut;
        }
    }

    //https://stackoverflow.com/questions/8391979/does-java-have-a-int-tryparse-that-doesnt-throw-an-exception-for-bad-data
    public static double tryParseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    //https://stackoverflow.com/questions/6618994/simplifying-fractions-in-java
    public static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    //https://stackoverflow.com/questions/6618994/simplifying-fractions-in-java
    public static String asFraction(int a, int b) {
        long gcd = gcd(a, b);
        return (a / gcd) + "/" + (b / gcd);
    }


}
