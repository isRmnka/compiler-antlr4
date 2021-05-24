package end.analysis;

import java.util.ArrayList;
import java.util.List;

public class isMethod extends Record {

	private static final long serialVersionUID = 1L;
	private List<isVariable> paramList;
	private List<isVariable> varList;
	private List<isVariable> list;

	public isMethod(String id, String type) {
		super(id, type);
		paramList=new ArrayList<isVariable>();
		varList=new ArrayList<isVariable>();
		list= new ArrayList<isVariable>();
	}
	
	public void putParam(isVariable param){
		paramList.add(param);
	}
	
	public void putVar(isVariable var){
		varList.add(var);
	}

	public List<isVariable> getParamList() {
		return paramList;
	}

	public List<isVariable> getVarList() {
		return varList;
	}

	//public void setVarList(List<VariableRecord> varList) {this.varList = varList;}

	public List<isVariable> getList() {
		return list;
	}

	public void setList(List<isVariable> list) {
		this.list.addAll(list);
	}

}
