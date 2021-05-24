package end.analysis;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scope {

	private int next = 0;
	private Scope parent;
	private List<Scope> children = new ArrayList<Scope>();
	private Map<String, Record> records = new HashMap<String, Record>();

	private Record containingClass;

	public Scope(Object object) {
		children = new ArrayList<Scope>();
		records = new HashMap<String, Record>();
		if (object != null)
			parent = (Scope) object;
	}

	public Scope nextChild() {
		Scope nextC;
		if (next >= children.size()) {
			nextC = new Scope(this);
			children.add(nextC);
		} else {
			nextC = (Scope) children.get(next);
		}
		next++;
		return nextC;
	}

	public Scope getParent() {
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
			String leftAlignFormat = " %-29s  %-19s  %-27s%n";
			String key = entry.getKey();
			Record value = entry.getValue();
			String recordClass = value.getClass().getSimpleName();
			try(FileWriter writer = new FileWriter("Table.txt", true)) {
				String text = "".format(leftAlignFormat, key, value.getType(),recordClass);
				writer.write(text);
				writer.flush();
			}
			catch(IOException ex){
				System.out.println(ex.getMessage());
			}
			//System.out.format(leftAlignFormat, key,value.getType(),recordClass);
		}
		for (Scope scope : children) {
			scope.printScope();
		}

	}

	public void printHeadTable() {
		try(FileWriter writer = new FileWriter("Table.txt", true)) {
			writer.write("Printing the Symbol Table:\n "
					+ "             ID                         TYPE                VISIBILITY        \n"
					+ "+-------------------------------------------------------------------------------------------+\n");
		}
		catch(IOException ex){

			System.out.println(ex.getMessage());
		}
	}

	public void resetScope() {
		next = 0;
		for (int i = 0; i < children.size(); i++)
			((Scope) children.get(i)).resetScope();

	}

	public void setContainingClass(Record record) {
		containingClass = record;
	}

	public Record getContainingClass() {
		return containingClass;
	}
}
