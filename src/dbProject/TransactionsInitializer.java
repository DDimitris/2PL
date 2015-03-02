/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dbProject;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author Dimitris Dedousis <dimitris.dedousis@gmail.com>
 */
public class TransactionsInitializer {

    private List<Transaction> transactions;

    {
        transactions = Collections.EMPTY_LIST;
    }

    public TransactionsInitializer(List<Transaction> transactions) {
        this.transactions = transactions;
        System.out.println("Initializing " + transactions.size() + " transactions");
        initializeTransactions();
    }
    int i = 0;

    private void initializeTransactions() {
        for (Transaction tr : transactions) {
            TransactionExecution t = new TransactionExecution(tr);
            Thread th = new Thread(t);
            th.start();
        }
    }
}
