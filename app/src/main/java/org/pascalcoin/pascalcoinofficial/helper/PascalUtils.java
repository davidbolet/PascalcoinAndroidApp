package org.pascalcoin.pascalcoinofficial.helper;

public class PascalUtils {

    public static String validChars ="abcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-+{}[]_:\"|<>,.?/~";

    public static boolean checkValidAccountName(String text) {
        boolean valid;
        valid=text!=null && text.length()>2 && text.length()<65;
        valid = valid && (text.charAt(0)<'0' || text.charAt(0)>'9') && isValidText(text);
        return valid;
    }


    public static Integer calculateChecksum(Integer account) {
        return (((account) * 101) % 89) + 10;
    }


    public static boolean checkValidPayload(String text) {
        boolean valid;
        valid=text!=null && text.length()>2 && text.length()<256 && isValidText(text);
        return valid;
    }

    public static boolean isValidText(String text) {
        boolean valid=true;
        for (int i=0; valid && i<text.length();i++) {
            valid = validChars.indexOf(text.charAt(i)) >= 0;
        }
        return valid;
    }

}
