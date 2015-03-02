package dbProject;

import Utils.FileWriters;
import Utils.StaticVariables;
import Utils.UndoRedoTag;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionExecution implements Runnable {

    private Transaction transaction;
    private Map<DbElement, Integer> importantMap;//Db element with new Value
    private int timeout;
    private boolean isDead = false;

    {
        importantMap = new HashMap<>();
    }

    public TransactionExecution(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void run() {
        execTransaction();
    }

    /**
     * WE ASSUME THAT IF A VARIABLE MUST BE UPDATED THEN IT MUST BE READ
     * FIRST!!! An upothetoume oti mia metavliti panta prwta prepei na
     * diavazetai tote kathe fora pou kanoume new Lock tote tha einai panta
     * tupou SHARED kai an theloume na kanoume write tote tha kanoume update
     * auto to lock.
     */
    private void execTransaction() {
        System.out.println("EXECUTING TRANSACTION WITH ID " + transaction.getId());
        FileWriters.writeToLog("<START T" + transaction.getId() + ">");
        for (int i = 0; i < transaction.getOperations().size(); i++) {
            System.out.println("Number of iteration " + i);
            Operation operation = transaction.getOperations().get(i);
            Lock l = null;
            //Create a new lock
            if (operation.isIsFailed()) {
                System.out.println("Exiting because we had a failure!!!");
                System.exit(1);
            }
            if (operation.getTypeOfOperation().equals(StaticVariables.READ)) {
                System.out.println("Creating new shared lock");
                l = new Lock(operation.getElement(), transaction, operation.getTypeOfLockNeeded());
            }
            //Execute function
            if (operation.getTypeOfOperation().equals(StaticVariables.FUNCTION)) {
                System.out.println("Executing function and store temporalily the value...");
                int result = executeFunction(operation);
                importantMap.put(operation.getElement(), result);
//                System.out.println("result " + result);
                continue;
            }
            //Request a new lock
            if (l != null && LockManager.requestLock(l)) {   //do the magic!
                System.out.println("Request lock on element " + operation.getElement().getName() + " achived!!");
            } else if (l == null) {
                Lock lockByElementAndTransaction = LockManager.getLockByElementAndTransaction(transaction, operation.getElement());
                //Update lock to exclusive and write    
                if (LockManager.updateLockToExclusive(lockByElementAndTransaction)) {
                    DbElement el = null;
                    for (DbElement e : StaticVariables.DATABASE) {
                        if (e.equals(operation.getElement())) {
                            el = e;
                            break;
                        }
                    }
                    System.out.println("Lock updated!!!");
                    Integer newValue = importantMap.get(el);
//                    System.out.println("newValue " + newValue);
                    System.out.print("-----Writing value on element " + el.getName()
                            + " with old value " + el.getValue());
                    UndoRedoTag<Transaction, DbElement> tag = new UndoRedoTag<>();
                    tag.add(transaction, el, el.getValue(), newValue);
                    writeValueToDataBase(el, newValue);
                    FileWriters.writeToLog(tag.toString());
                    el.setValue(newValue);
                    System.out.println(" and new value " + el.getValue());

                    //write
                }
            } else {
                timeout += 500;
                if (timeout == 1000) {
                    isDead = true;
                    break;
                }
                System.err.println("Repeat the same iteration " + i);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(TransactionExecution.class.getName()).log(Level.SEVERE, null, ex);
                }
                i = i - 1; //because we want to repeat the same iteration.
                continue;
            }
        }
            LockManager.removeAllLocksFromATransaction(LockManager.getLocksForATransaction(transaction));
        if (!isDead) {
            for (Map.Entry<DbElement, Integer> map : importantMap.entrySet()) {
                FileWriters.updateDataBase(map.getKey());
            }
            FileWriters.writeToLog("<COMMIT T" + transaction.getId() + ">");
            System.out.println("############################################");
        }else{
            run();
        }
    }

    private void writeValueToDataBase(DbElement element, Integer newValue) {
        for (DbElement e : StaticVariables.DATABASE) {
            if (e.equals(element)) {
                StaticVariables.DATABASE.remove(e);
                e.setValue(newValue);
                StaticVariables.DATABASE.add(e);
                break;
            }
        }
    }

    /**
     * Basiki paratirisi!!! sto arxeio transaction.txt otan kanoume mia praksi
     * prepei oposdipote na einai tou tupoy a=a+x , a=a+1 ktl diladi sto deksi
     * meros tis isotitas to prwto stoixeio tis praksis na einai to IDIO me to
     * aristero meros tis anisotitas!!! (a=a(oti theloume))
     *
     * @param op
     * @return
     */
    private int executeFunction(Operation op) {
        int initialValue = 0;
        int value = 0;
        for (DbElement data : StaticVariables.DATABASE) {
            if (op.getElement().getName().equalsIgnoreCase(data.getName())) {
                initialValue = data.getValue();
                break;
            }
        }
        if (op.isOperationBetweenDbElements()) {
            for (DbElement data : StaticVariables.DATABASE) {
                if (data.getName().equals(op.getElementsToOperateWith().getName())) {
                    System.out.println("isOperationBetweenDbElements");
                    value = data.getValue();
                }
            }
        } else {
            value = op.getOperationValue();
        }
        int updatedValue = 0;
        if (op.isAdd()) {
            updatedValue = initialValue + value;
        } else if (op.isSub()) {
            updatedValue = initialValue - value;
        } else if (op.isMult()) {
            updatedValue = initialValue * value;
        } else if (op.isDiv()) {
            updatedValue = initialValue / value;
        } else {
            System.out.println("Error in executing function");
        }
        return updatedValue;
    }
}
