package dbProject;

import Utils.FileWriters;
import Utils.StaticVariables;
import Utils.UndoRedoTag;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class Main {

    private List<Transaction> transactions;
    private String dataBaseFileInput;
    private String transactionsFileInput;
    private String logFileInput;
    private List<Transaction> uncommittedTransactions;

    {
        dataBaseFileInput = readFile(StaticVariables.DATABASE_FILE);
        transactionsFileInput = readFile(StaticVariables.TRANSACTIONS_FILE);
        transactions = new ArrayList<>();
        logFileInput = readFile(StaticVariables.LOG_FILE);
        uncommittedTransactions = new ArrayList<>();
    }

    public Main() {
        loadThemUp();
        if (JOptionPane.showConfirmDialog(null, "Would you like to run the transactions?", "WARNING",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
        TransactionsInitializer t = new TransactionsInitializer(transactions);
        }
    }

    /**
     * This method loads in to the memory, every value from the database and
     * every transaction that is going to have an impact to this values.
     */
    private void loadThemUp() {
        if (logFileInput != null && dataBaseFileInput != null && transactionsFileInput != null) {
            if (JOptionPane.showConfirmDialog(null, "Would you like to recover your database?", "WARNING",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                recover(loadLogFile(logFileInput));
                updateLog();
            }
            loadDataBase(dataBaseFileInput);
            loadTransactions(transactionsFileInput);
            //printLoadedDataBase();
            //printTransactionIds();
            //printTheUniverse();
        }
    }

    public static void main(String[] args) {
        new Main();
    }//main

    /**
     * This method is used to load all the transactions from the file
     * transactions.txt
     *
     * @param transaction
     */
    private void loadTransactions(String transaction) {
        String[] in = transaction.split("TRANSACTION");
        int id = 1;
        String[] operationTable;
        for (int i = 1; i < in.length; i++) {
            operationTable = in[i].split(";");
            Transaction t = new Transaction(id);
            for (int j = 0; j < operationTable.length; j++) {
                if (!operationTable[j].startsWith(" T")) {
                    if (operationTable[j].startsWith("fail")) { //read
                        Operation operation = new Operation("f", null);
                        operation.setIsFailed(true);
                        t.addOperation(operation);
                        continue;
                    }
                    if (operationTable[j].startsWith("r(")) {//read
                        String val = operationTable[j].substring(2, operationTable[j].length() - 1);
                        DbElement el = new DbElement(val);
                        Operation operation = new Operation("r", el);
                        t.addOperation(operation);
                    } else if (operationTable[j].startsWith("w(")) {//write
                        String val = operationTable[j].substring(2, operationTable[j].length() - 1);
                        DbElement el = new DbElement(val);
                        Operation operation = new Operation("w", el);
                        t.addOperation(operation);
                    } else if (operationTable[j].startsWith("u")) {//update				 
                        String var = String.valueOf(operationTable[j].charAt(1));
                        String oper = String.valueOf(operationTable[j].charAt(4));
                        String val = operationTable[j].substring(5, operationTable[j].length());
                        for (DbElement data : StaticVariables.DATABASE) {
                            if (data.getName().equalsIgnoreCase(var)) {
                                Operation operation = new Operation("u", data);
                                for (DbElement d : StaticVariables.DATABASE) {
                                    if (d.getName().equals(val)) {
                                        operation.setOperationValue(d.getValue());
                                        operation.isOperationBetweenDbElements(true, d);
                                        break;
                                    }
                                }
                                if (operation.getOperationValue() == null) {
                                    operation.setOperationValue(Integer.parseInt(val));
                                }
                                if (oper.equalsIgnoreCase("+")) {
                                    operation.isAdd(true);
                                }
                                if (oper.equalsIgnoreCase("-")) {
                                    operation.isSub(true);
                                }
                                if (oper.equalsIgnoreCase("*")) {
                                    operation.isMult(true);
                                }
                                if (oper.equalsIgnoreCase("/")) {
                                    operation.isDiv(true);
                                }
                                t.addOperation(operation);
                            }//if
                        }//for
                    }//else if
                } else {
                }
            }//for
            transactions.add(t);
            id++;
        }//for

    }

    /**
     * This method is used to load the log file to be read in case of recovery.
     *
     * @param log
     * @return
     */
    private ArrayList<UndoRedoTag> loadLogFile(String log) {
        String[] in = log.split("<START ");
        String[] tags;
        String[] values;
        UndoRedoTag<Transaction, DbElement> tag1 = new UndoRedoTag<Transaction, DbElement>();
        ArrayList<UndoRedoTag> tagList = new ArrayList<>();
        for (int i = 1; i < in.length; i++) {
            tags = in[i].split("<");
            for (int j = 0; j < tags.length; j++) {
                if (tags[j].startsWith("COMMIT")) {
                    int id = Integer.valueOf(tags[j].substring(8, 9));
                    for (UndoRedoTag t : tagList) {
                        if (t.getTransaction().getId() == id) {
                            t.getTransaction().setStatus("COMMITTED");
                        }
                    }
                } else if (String.valueOf(tags[j].charAt(2)).equalsIgnoreCase(">")) {
                } else if (tags[j].startsWith("ABORT")) {
                } else {
                    values = tags[j].split(",");
                    int tId = Integer.valueOf(values[0].substring(1, 2));
                    Transaction t = new Transaction(tId);
                    DbElement el = new DbElement(values[1].trim());
                    int oldValue = Integer.valueOf(values[2].trim());
                    String newValue = values[3].trim().substring(0, values[3].length() - 1);
                    int newValue2 = Integer.valueOf(newValue.substring(0, newValue.length() - 1));
                    UndoRedoTag<Transaction, DbElement> tag = new UndoRedoTag<>();
                    tag.add(t, el, oldValue, newValue2);
                    tag1 = tag;
                    tagList.add(tag1);
                }

            }

        }
        for (UndoRedoTag t : tagList) {
            System.out.println(t.toString());
            System.out.println("tId:" + t.getTransaction().getId() + " status:" + t.getTransaction().getStatus());
        }
        return tagList;
    }

    /**
     * This method is called at the beginning of this program execution to check
     * whether it must restore our database file to an earlier state or to
     * repeat the committed transactions.
     *
     * @param tagList
     */
    private void recover(ArrayList<UndoRedoTag> tagList) {
        for (UndoRedoTag t : tagList) {
            Transaction transaction = t.getTransaction();
            if (transaction.getStatus().equalsIgnoreCase("COMMITTED")) {//committed
                DbElement element = t.getDbElement();
                int newValue = t.getNewValue();
                element.setValue(newValue);
                FileWriters.updateDataBase(element);
            } else if (transaction.getStatus().equalsIgnoreCase("UNCOMMITTED")) {// the transaction is uncommitted
                uncommittedTransactions.add(transaction);
                DbElement element = t.getDbElement();
                int oldValue = t.getOldValue();
                element.setValue(oldValue);
                FileWriters.updateDataBase(element);
            }
        }
    }

    /**
     * This method loads the dataBase file to be used within our transactions.
     *
     * @param input
     */
    private void loadDataBase(String input) {
        String[] in = input.replaceAll(":", " ").split(";");
        for (int i = 0; i < in.length; i++) {
            String name = in[i].substring(0, 1);
            int value = Integer.valueOf(in[i].substring(2, in[i].length()));
            DbElement el = new DbElement(name, value);
            StaticVariables.DATABASE.add(el);
        }

    }

    /**
     * This method prints all operations from all transactions.
     */
    private void printTheUniverse() {
        for (Transaction t : transactions) {
            for (Operation o : t.getOperations()) {
                System.out.println("Transaction with id " + t.getId() + " has an operation of type " + o.getTypeOfOperation()
                        + " on element " + o.getElement().getName()
                        + " \nwith initial value " + o.getElement().getValue()
                        + " and the number that it will operate with is " + o.getOperationValue());
                System.out.println("\n\n");
            }
        }
    }

    /**
     * This method prints all the transaction ids.
     */
    private void printTransactionIds() {
        for (Transaction t : transactions) {
            System.out.println("Transaction id: " + t.getId());
        }
    }

    /**
     * This method prints all the elements with their values that was taken from
     * the database.
     */
    private void printLoadedDataBase() {
        for (DbElement data : StaticVariables.DATABASE) {
            System.out.println(data.getName() + " " + data.getValue());
        }
    }

    /**
     * This method takes as an argument a file name to be read and returns a
     * string that contains everything that is written to that file.
     *
     * @param filename
     * @return
     */
    private String readFile(String filename) {
        BufferedReader reader = null;
        String input = null;
        StringBuilder sBuilder = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(filename));
            while ((input = reader.readLine()) != null) {
                sBuilder.append(input);
            }
            input = sBuilder.toString();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return input;
    }

    /**
     * This method updates the log file by adding an ABORT tag for every
     * transaction that was not successful.
     */
    private void updateLog() {
        try {
            File original = new File("undo_redo.log");
            PrintWriter out = new PrintWriter(new FileWriter(original, true));
            int idTemp = -1;
            for (Transaction t : uncommittedTransactions) {
                int id = t.getId();
                if (id == idTemp) {
                    continue;
                }
                idTemp = id;
                out.append("<ABORT T" + id + ">");
                out.append("\r");

            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
