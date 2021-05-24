package end.analysis;

import java.util.LinkedHashMap;
import java.util.Map;

public class isClass extends Record {
	private static final long serialVersionUID = 1L;
	private Map<String, isMethod> methodList;
	private Map<String, isVariable> fieldList;

	/* Constructor */
	public isClass(String name, String type) {
		super(name, type);
		methodList = new LinkedHashMap<String, isMethod>();
		fieldList = new LinkedHashMap<String, isVariable>();
	}

	public void putField(String fName, isVariable field) {
		fieldList.put(fName, field);
	}

	public void putMethod(String mName, isMethod currentMethod) {
		methodList.put(mName, currentMethod);
	}

	public Map<String, isMethod> getMethodList() {
		return methodList;
	}


}
