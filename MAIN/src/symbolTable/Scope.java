package symbolTable;

import symbolTable.Record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scope {

	private int next = 0; // Next child to visit
	private symbolTable.Scope parent; // Parent scope
	private List<symbolTable.Scope> children = new ArrayList<symbolTable.Scope>(); // Children scopes
	private Map<String, Record> records = new HashMap<String, Record>(); // ID
																			// to
																			// Record
																			// map
	private String name;
	private String type;
	private Record containingClass;

	public Scope(Object object) {
		children = new ArrayList<symbolTable.Scope>();
		records = new HashMap<String, Record>();
		if (object != null)
			parent = (symbolTable.Scope) object;
	}

	public symbolTable.Scope nextChild() {
		symbolTable.Scope nextC;
		if (next >= children.size()) {
			nextC = new symbolTable.Scope(this);
			children.add(nextC);
		} else {
			nextC = (symbolTable.Scope) children.get(next); // ==> visit child
		}
		next++;
		return nextC;
	}

	public symbolTable.Scope getParent() {
		return parent;
	}

	public void put(String key, Record item) {
		records.put(key, item);
	}

	public Record lookup(String key) {
		if (records.containsKey(key))
			return records.get(key);
		else {
			if (parent == null)
				return null;
			else
				return parent.lookup(key);
		}
	}

	public void printScope() {
		for (Map.Entry<String, Record> entry : records.entrySet()) {
			String key = entry.getKey();
			Record value = entry.getValue();
			String recordClss = value.getClass().getSimpleName();
			System.out.printf("%" + 20 + "s %" + 20 + "s %" + 30 + "s %n", key,
					value.getType(), recordClss);
		}
		for (symbolTable.Scope scope : children) {
			scope.printScope();
		}

	}

	public void resetScope() {
		next = 0;
		for (int i = 0; i < children.size(); i++)
			((symbolTable.Scope) children.get(i)).resetScope();
	}

	public void setContainingClass(Record record) {
		containingClass = record;
	}

	public Record getContainingClass() {
		return containingClass;
	}
}
