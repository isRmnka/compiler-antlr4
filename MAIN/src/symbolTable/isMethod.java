package symbolTable;

import java.util.ArrayList;
import java.util.List;

public class isMethod extends Record {

	private List<isVariable> paramList;
	private List<isVariable> varList;

	public isMethod(String id, String type) {
		super(id, type);
		paramList=new ArrayList<isVariable>();
		varList=new ArrayList<isVariable>();
	}

	public void putParam(isVariable param){
		paramList.add(param);
	}
	
	public void putVar(isVariable var){ varList.add(var); }
}
