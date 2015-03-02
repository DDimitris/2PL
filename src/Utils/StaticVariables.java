/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import dbProject.DbElement;
import java.util.HashSet;
import java.util.Set;

public class StaticVariables {

    public static Set<DbElement> DATABASE = new HashSet<>(); //its type of Set because in the file db.txt we dont accept duplicate values.
    private static String OS = System.getProperty("os.name").toLowerCase();
    public static final String FILE_SEPERATOR = System.getProperty("file.separator");
    public static final String DATABASE_FILE = "db.txt";
    public static final String TRANSACTIONS_FILE = "transactions.txt";
    public static final String USER_DIR = System.getProperty("user.dir");
    public static final String READ = "READ";
    public static final String WRITE = "WRITE";
    public static final String FUNCTION = "FUNCTION";
    public static final String EXCLUSIVE_LOCK = "exclusiveLock";
    public static final String SHARED_LOCK = "sharedLock";
    public static final String LOG_FILE = "undo_redo.log";
    public static final String TEMP_FILE = "tmp.txt";

    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
    }

    public static boolean isSolaris() {
        return (OS.indexOf("sunos") >= 0);
    }
}
