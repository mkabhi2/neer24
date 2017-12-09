package in.neer24.neer24.Utilities;

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
}
