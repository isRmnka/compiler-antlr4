package symbolTable;

public class SymbolTable {

	private Scope root;
	private Scope current;

	public SymbolTable() {
		root = new Scope(null);
		current = root;
	}

	public void enterScope() {
		current = current.nextChild();

	}

	public void exitScope() {
		current = current.getParent();
	}

	public void put(String key, Record item) {
		current.put(key, item);
	}

	public Record lookup(String key) {
		return current.lookup(key);
	}

	public void printTable() {
		System.out.printf("%" + 18 + "s %" + 20 + "s %" + 32 + "s %n", "ID",
				"TYPE", "VISIBILITY");
		System.out
				.printf("%s %n",
						"+-------------------------------------------------------------------------------------------+");
		root.printScope();
	}

	public void resetTable() {
		current = root;
		root.resetScope();
	}

	public void setCurrentClass(Record cRec) {
		current.setContainingClass(cRec);
	}
}
