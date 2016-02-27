
import android.util.Log;

/**
 * Helper methods that make logging more consistent throughout the app.
 */
public class L {
    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------
    private static final String LOG_PREFIX = "";
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    private static final int MAX_LOG_TAG_LENGTH = 23;

    // ---------------------------------------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------------------------------------
    private static int sLineNumber;
    private static String sClassName;
    private static String sMethodName;

    // ---------------------------------------------------------------------------------------------
    // Public methods
    // ---------------------------------------------------------------------------------------------

    /**
     * WARNING: Don't use this when obfuscating class names with Proguard!
     */
    public static String makeLogTag(Class<? extends Object> cls) {
        return makeLogTag(cls.getSimpleName());
    }

    /**
     * Send a DEBUG log message.
     *
     * @param msg The message you would like logged.
     */
    public static void d(String msg) {
        getMethodNames(new Throwable().getStackTrace());
        Log.d(sClassName, createLog(msg));
    }

    /**
     * Send a DEBUG log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void d(String msg, Throwable tr) {
        getMethodNames(new Throwable().getStackTrace());
        Log.d(sClassName, createLog(msg), tr);
    }

    /**
     * Send a VERBOSE log message.
     *
     * @param msg The message you would like logged.
     */
    public static void v(String msg) {
        getMethodNames(new Throwable().getStackTrace());
        Log.v(sClassName, createLog(msg));
    }

    /**
     * Send a VERBOSE log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void v(String msg, Throwable tr) {
        getMethodNames(new Throwable().getStackTrace());
        Log.v(sClassName, createLog(msg), tr);

    }

    /**
     * Send an INFO log message.
     *
     * @param msg The message you would like logged.
     */
    public static void i(String tag, String msg) {
        Log.i(tag, createLog(msg));
    }

    /**
     * Send a INFO log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void i(String msg, Throwable tr) {
        getMethodNames(new Throwable().getStackTrace());
        Log.i(sClassName, createLog(msg), tr);
    }

    /**
     * Send a WARNING log message.
     *
     * @param msg The message you would like logged.
     */
    public static void w(String msg) {
        getMethodNames(new Throwable().getStackTrace());
        Log.w(sClassName, createLog(msg));
    }

    /**
     * Send a WARNING log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void w(String msg, Throwable tr) {
        getMethodNames(new Throwable().getStackTrace());
        Log.w(sClassName, createLog(msg), tr);
    }

    /**
     * Send an ERROR log message.
     *
     * @param msg The message you would like logged.
     */
    public static void e(String msg) {
        getMethodNames(new Throwable().getStackTrace());
        Log.e(sClassName, createLog(msg));
    }

    /**
     * Send a ERROR log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void e(String msg, Throwable tr) {
        getMethodNames(new Throwable().getStackTrace());
        Log.e(sClassName, createLog(msg), tr);
    }

    // ---------------------------------------------------------------------------------------------
    // Private methods
    // ---------------------------------------------------------------------------------------------
    private static String makeLogTag(String str) {
        if (str.length() > (MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH)) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
        }
        return LOG_PREFIX + str;
    }

    private static void getMethodNames(StackTraceElement[] sElements) {
        sClassName = makeLogTag(sElements[1].getFileName());
        sMethodName = sElements[1].getMethodName();
        sLineNumber = sElements[1].getLineNumber();
    }

    private static String createLog(String log) {
        return "[" + sMethodName + ":" + sLineNumber + "] " + log;
    }

}
