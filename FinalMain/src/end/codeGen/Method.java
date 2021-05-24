package end.codeGen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import end.analysis.isVariable;

public class Method implements Serializable, ICodes {

	private static final long serialVersionUID = 1L;
	private List<isVariable> paramList;
	private List<isVariable> list;// // List of parameters and variables together
	private List<Instruction> instList;// Instructions List
	private List<Integer> varValues;
	private int PC = 0;// Program Counter

	/* Constructor */
	public Method() {
		paramList = new ArrayList<isVariable>();
		list = new ArrayList<isVariable>();
		instList = new ArrayList<Instruction>();
		varValues = new ArrayList<Integer>();
	}

	public int getPC() {
		return PC;
	}

	public void setPC(int pc) {
		PC = pc;
	}

	public List<Instruction> getInstList() {
		return instList;
	}

	public void setInstList(Instruction inst) {
		instList.add(inst);
	}

	public List<isVariable> getList() {
		return list;
	}

	public void setList(List<isVariable> list) {
		this.list.addAll(list);
	}

	public void setParamList(List<isVariable> paramList) {
		this.paramList = paramList;
	}

	public List<Integer> getVarValues() {
		return varValues;
	}

	public Instruction getInstruction(int n) {
		return instList.get(n);
	}


}
