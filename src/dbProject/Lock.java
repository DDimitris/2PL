package dbProject;

import java.util.Objects;

public class Lock {

    private DbElement variable;
    private Transaction transaction;
    private String typeOfLock;

    public Lock(DbElement variable, Transaction transaction, String typeOfLock) {
        this.variable = variable;
        this.transaction = transaction;
        this.typeOfLock = typeOfLock;
    }

    public DbElement getVariable() {
        return variable;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public String getTypeOfLock() {
        return typeOfLock;
    }

    public void setTypeOfLock(String typeOfLock) {
        this.typeOfLock = typeOfLock;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.variable);
        hash = 97 * hash + Objects.hashCode(this.transaction);
        hash = 97 * hash + Objects.hashCode(this.typeOfLock);
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
        final Lock other = (Lock) obj;
        if (!Objects.equals(this.variable, other.variable)) {
            return false;
        }
        if (!Objects.equals(this.transaction, other.transaction)) {
            return false;
        }
        if (!Objects.equals(this.typeOfLock, other.typeOfLock)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Lock{" + "variable=" + variable + ", transaction=" + transaction + ", typeOfLock=" + typeOfLock + '}';
    }
}
