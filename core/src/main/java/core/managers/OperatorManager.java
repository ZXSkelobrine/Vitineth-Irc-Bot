package core.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class OperatorManager {

    /**
     * This is a list off all the current operators
     */
    private List<String> operatorUUIDs = new ArrayList<String>();

    /**
     * This will add an operator to the list
     * 
     * @param operator
     *            - The uuid of the operator to add.
     */
    public void addOperator(String operator) {
	synchronized (operatorUUIDs) {
	    operatorUUIDs.add(operator);
	}
    }

    /**
     * This will remove an operator from the list.
     * 
     * @param opperator
     *            - The uuid of the operator to remove.
     */
    public void removeOperator(String opperator) {
	synchronized (operatorUUIDs) {
	    operatorUUIDs.remove(opperator);
	}
    }

    /**
     * This will return if the given users uuid has op privileges.
     * 
     * @param uuid
     *            - The uuid of the user to check.
     * @return Boolean - if they have op privileges.
     */
    public boolean hasOpAccount(String uuid) {
	return new CopyOnWriteArrayList<>(operatorUUIDs).indexOf(uuid) != -1;
    }

}
