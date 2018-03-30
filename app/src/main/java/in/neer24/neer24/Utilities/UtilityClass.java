package in.neer24.neer24.Utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by abhmishr on 12/7/17.
 */

public class UtilityClass {
    public final static boolean validate(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    public static boolean checkIfOrderIsNightDeliveryOrNot(String deliveryTime) {
        try {
            Date deliveryTimeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(deliveryTime);
            String newDeliveryTimeString = new SimpleDateFormat("HH:mm:ss").format(deliveryTimeDate);
            Date deliveryTimeDateAgain = new SimpleDateFormat("HH:mm:ss").parse(newDeliveryTimeString);


            String startTime = "2010-07-14 21:00:00";
            Date startTimeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime);
            String newStartTimeString = new SimpleDateFormat("HH:mm:ss").format(startTimeDate);
            Date startTimeDateAgain = new SimpleDateFormat("HH:mm:ss").parse(newStartTimeString);


            String closeTime = "2010-07-14 23:59:00";
            Date closeTimeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(closeTime);
            String newCloseTimeString = new SimpleDateFormat("HH:mm:ss").format(closeTimeDate);
            Date closeTimeDateAgain = new SimpleDateFormat("HH:mm:ss").parse(newCloseTimeString);


            // delivery time start time se jayda and close time se kam

            if (startTimeDateAgain.compareTo(deliveryTimeDateAgain) <= 0 && closeTimeDateAgain.compareTo(deliveryTimeDateAgain) >= 0) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {

        }

        return false;
    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    public static boolean validateFirstName(String name){
        return (name!=null && name.length()>3);
    }

    public static boolean validateMobile(String mobile) {
        return mobile.matches("\\d{10}");
    }


    public static boolean validatePassword(String password) {
        return password.length()>3;
    }

    public static boolean validateReferalCdde(String referralCode) {
        return referralCode.length()==6;
    }
}
