
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * @author Kevin Kowalewski
 */
public class RootUtil {
    // ---------------------------------------------------------------------------------------------
    // Public methods
    // ---------------------------------------------------------------------------------------------

    /**
     * Method that check root access of the device.
     *
     * @return true or false.
     */
    public static boolean isDeviceRooted() {
        return checkFromBuildInfo() || checkForSuperUserApk() || canExecuteCommand();
    }

    // ---------------------------------------------------------------------------------------------
    // Private methods
    // ---------------------------------------------------------------------------------------------
    private static boolean checkFromBuildInfo() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean checkForSuperUserApk() {
        String[] paths =
            {"/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su",
                "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su"};
        for (String path : paths) {
            if (new File(path).exists()) {
                return true;
            }
        }
        return false;
    }

    private static boolean canExecuteCommand() {
        Process process = null;
        try {
            process = Runtime.getRuntime()
                .exec(new String[]{"/system/xbin/which", "su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return in.readLine() != null;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
}
