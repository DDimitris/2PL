package dbProject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Transaction {

    private int id;
    private String status;
    private Set<Lock> waitLocks = new HashSet<>();
    private Set<Lock> locks = new HashSet<>();
    private List<Operation> operations = new ArrayList<>();

    public Transaction(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        if (status == null) {
            status = "UNCOMMITTED";
        }
        return status;
    }

    public void addWaitLock(Lock l) {
        waitLocks.add(l);
    }

    public Set<Lock> getWaitLocks() {
        return waitLocks;
    }

    public void addLocks(Lock l) {
        locks.add(l);
    }

    public void removeLock(Lock l) {
        locks.remove(l);
    }

    public Set<Lock> getLocks() {
        return locks;
    }

    public void addOperation(Operation op) {
        operations.add(op);
    }

    public List<Operation> getOperations() {
        return operations;
    }
}
