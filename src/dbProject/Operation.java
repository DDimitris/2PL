package dbProject;

import Utils.StaticVariables;

public class Operation {

    private boolean write;
    private boolean read;
    private Integer newValue = null;
    private DbElement variable;
    private boolean isAdd;
    private boolean isSub;
    private boolean isMult;
    private boolean isDiv;
    private boolean isFunction;
    private boolean isElement = false;
    private boolean isFailed = false;
    private DbElement elementToOperateWith;

    public Operation(String operation, DbElement variable) {
        if (operation.equalsIgnoreCase("r")) {
            read = true;
        } else if (operation.equalsIgnoreCase("w")) {
            write = true;
        } else if (operation.equalsIgnoreCase("u")) {
            isFunction = true;
        }
        this.variable = variable;
    }

    public String getTypeOfOperation() {
        if (read) {
            return StaticVariables.READ;
        } else if (write) {
            return StaticVariables.WRITE;
        } else if (isAdd || isMult || isSub || isDiv) {
            return StaticVariables.FUNCTION;
        } else {
            return null;
        }
    }

    public String getTypeOfLockNeeded() {
        if (read) {
            return StaticVariables.SHARED_LOCK;
        } else if (write || isFunction) {
            return StaticVariables.EXCLUSIVE_LOCK;
        } else {
            return null;
        }
    }

    public void isAdd(boolean isAdd) {
        this.isAdd = isAdd;
    }

    public void isSub(boolean isSub) {
        this.isSub = isSub;
    }

    public void isMult(boolean isMult) {
        this.isMult = isMult;
    }

    public void isDiv(boolean isDiv) {
        this.isDiv = isDiv;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public boolean isSub() {
        return isSub;
    }

    public boolean isMult() {
        return isMult;
    }

    public boolean isDiv() {
        return isDiv;
    }

    public void setOperationValue(int value) {
        newValue = value;
    }

    public Integer getOperationValue() {
        return newValue;
    }

    public DbElement getElement() {
        return variable;
    }

    public void isOperationBetweenDbElements(boolean isElement, DbElement elementToOperateWith) {
        this.isElement = isElement;
        this.elementToOperateWith = elementToOperateWith;
    }

    public DbElement getElementsToOperateWith() {
        return elementToOperateWith;
    }

    public boolean isOperationBetweenDbElements() {
        return isElement;
    }

    public boolean isIsFailed() {
        return isFailed;
    }

    public void setIsFailed(boolean isFailed) {
        this.isFailed = isFailed;
    }
}
