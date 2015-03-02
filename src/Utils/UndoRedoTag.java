/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import dbProject.DbElement;
import dbProject.Transaction;
import java.util.Objects;

/**
 *
 * @author Dimitris Dedousis <dimitris.dedousis@gmail.com>
 */
public class UndoRedoTag<T extends Transaction, E extends DbElement> {

    private T transaction;
    private E dbElement;
    private Integer oldValue;
    private Integer newValue;

    public void add(T transaction, E dbElement, Integer oldValue, Integer newValue) {
        this.transaction = transaction;
        this.dbElement = dbElement;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public T getTransaction() {
        return transaction;
    }

    public E getDbElement() {
        return dbElement;
    }

    public Integer getOldValue() {
        return oldValue;
    }

    public Integer getNewValue() {
        return newValue;
    }

    public void setOldValue(Integer oldValue) {
        this.oldValue = oldValue;
    }

    public void setNewValue(Integer newValue) {
        this.newValue = newValue;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.transaction);
        hash = 89 * hash + Objects.hashCode(this.dbElement);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UndoRedoTag<T, E> other = (UndoRedoTag<T, E>) obj;
        if (!Objects.equals(this.transaction, other.transaction)) {
            return false;
        }
        if (!Objects.equals(this.dbElement, other.dbElement)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "<T" + transaction.getId() + " , " + dbElement.getName() + " , " + oldValue + " , " + newValue + ">";
    }
}
