/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import static Utils.StaticVariables.DATABASE_FILE;
import static Utils.StaticVariables.TEMP_FILE;
import static Utils.StaticVariables.LOG_FILE;
import dbProject.DbElement;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Dimitris Dedousis <dimitris.dedousis@gmail.com>
 */
public class FileWriters {

    public static synchronized void updateDataBase(DbElement element) {
        try {
            File tmp = new File(TEMP_FILE);
            File original = new File(DATABASE_FILE);
            BufferedReader in = new BufferedReader(new FileReader(original));
            PrintWriter out = new PrintWriter(new FileWriter(tmp));
            String line = null;
            while ((line = in.readLine()) != null) {
                if (line.startsWith(element.getName())) {//if it's the element to update
                    out.println(element.getName() + ":" + element.getValue() + ";");//write to tmp file the new value
                } else {//else, copy the line to tmp file
                    out.println(line);
                    out.flush();
                }
            }
            in.close();
            out.close();
            if (!original.delete()) {//delete the original file
                System.out.println("Error in file deletion");
            }
            if (!tmp.renameTo(original)) {//rename the tmp file
                System.out.println("Error in file rename");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void writeToLog(String data) {
        try {
            File file = new File(LOG_FILE);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(data);
            bw.write("\r");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
