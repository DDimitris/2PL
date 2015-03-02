package dbProject;

import Utils.StaticVariables;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LockManager {

    private static final Set<Lock> locks = new HashSet<>();
    private static List<Lock> locksOfACertainTransaction = new ArrayList<>();

    /**
     * This method checks if the request lock has already been assigned to some
     * transaction or not. If the requested lock is available then this method
     * returns true and adds the new lock to the Set. It checks also the type of
     * lock that is used for a certain element and the type of lock that it
     * wants to add to this element. If the element holds a lock of type READ
     * then it can add a new lock of type READ.
     *
     * @param lock
     * @return
     */
    public static synchronized boolean requestLock(Lock lock) {
        if (!containsLockOnElement(lock)) {
            locks.add(lock);
            return true;
        }
        for (Lock l : locks) {
            if (l.getTypeOfLock().equals(StaticVariables.SHARED_LOCK)
                    && lock.getTypeOfLock().equals(StaticVariables.SHARED_LOCK)
                    && l.getVariable().equals(lock.getVariable())) {
                locks.add(lock);
                return true;
            }
        }
        return false;
    }

    /**
     * This method checks whether a lock on a certain element exists or not.
     *
     * @param lock
     * @return
     */
    public static synchronized boolean containsLockOnElement(Lock lock) {
        for (Lock l : locks) {
            if (lock.getVariable().equals(l.getVariable())) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method returns the total number of locks that were given to a
     * certain element.
     *
     * @param element
     * @return
     */
    public static synchronized int getNumberOfLocksOnACertainElement(DbElement element) {
        int counter = 0;
        for (Lock l : locks) {
            if (l.getVariable().equals(element)) {
                counter++;
            }
        }
        return counter;
    }

    public static synchronized Lock getLockByElementAndTransaction(Transaction t, DbElement e) {
        for (Lock l : locks) {
            if (l.getTransaction().getId() == t.getId()
                    && l.getVariable().equals(e)) {
//                System.out.println("Match found!!!!!!");
                return l;
            }
        }
        System.out.println("Returning null.....");
        return null;
    }

    /**
     * This method releases a previously requested lock.
     *
     * @param lock
     */
    public static synchronized void releaseLock(Lock lock) {
        System.out.println("Number of locks before removal " + locks.size());
        System.out.println("Releasing lock on element " + lock.getVariable().getName() + " with type of lock " + lock.getTypeOfLock());
        locks.remove(lock);
        System.out.println("Number of locks After removal " + locks.size());
    }

    /**
     * This method returns a full list of all locks that they were assigned to
     * some dataBase Elements.
     *
     * @return
     */
    public static Set<Lock> getListOfLocks() {
        return locks;
    }

    /**
     * Prints all type of locks that the lock manager holds.
     */
    public static void printAllLocks() {
        System.out.println("Type of locks we have:");
        int i = 1;
        for (Lock l : locks) {
            System.out.println(i + ") " + l.getTypeOfLock());
            i++;
        }
    }

    /**
     * This method removes all the locks that a transaction may hold.
     *
     * @param t
     */
    public static synchronized void removeAllLocksFromATransaction(List<Lock> list) {
        locks.removeAll(list);
    }

    public static synchronized boolean updateLockToExclusive(Lock lock) {
        if (lock.getTypeOfLock().equals(StaticVariables.EXCLUSIVE_LOCK)) {
            return true;
        }
        for (Lock l : locks) {
            System.out.println("Lock in set: (Transaction ID) " + l.getTransaction().getId());
            System.out.println("Given lock: (Transaction ID) " + lock.getTransaction().getId());
            if (l.getTransaction().getId() == lock.getTransaction().getId()
                    && l.getTypeOfLock().equals(StaticVariables.SHARED_LOCK)
                    && l.getVariable().equals(lock.getVariable())
                    && getNumberOfLocksOnACertainElement(lock.getVariable()) == 1) {
                locks.remove(l);
                l.setTypeOfLock(StaticVariables.EXCLUSIVE_LOCK);
                locks.add(l);
                printAllLocks();
                return true;
            }
        }
        return false;
    }

    public static List<Lock> getLocksForATransaction(Transaction t) {
        locksOfACertainTransaction.clear();
        for (Lock l : locks) {
            if (l.getTransaction().getId() == t.getId()) {
                locksOfACertainTransaction.add(l);
            }
        }
        return locksOfACertainTransaction;
    }
}
