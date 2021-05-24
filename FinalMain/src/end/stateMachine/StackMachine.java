package end.stateMachine;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Stack;

import end.codeGen.ClassFile;
import end.codeGen.ICodes;
import end.codeGen.Instruction;
import end.codeGen.Method;

public class StackMachine implements ICodes {
	Stack<Integer> data;
	Stack<Method> activations;
	int next = 0;
	ClassFile classFile;

	public StackMachine(String name) throws Exception {
		data = new Stack<Integer>();
		activations = new Stack<Method>();
		classFile = readFromFile(name);
	}

	public void execute() {
		int n;
		int code = 0;
		Method method = classFile.getMainMethod();
		while (code != STOP) {
			Instruction instruction = method.getInstruction(next);
			code = instruction.getCode();
			Object arg = instruction.getArgument();
			switch (code) {
			case ILOAD:
				n = Integer.parseInt(arg.toString());
				data.push(method.getVarValues().get(n));
				break;
			case ICONST:
				data.push(Integer.parseInt(arg.toString()));
				break;
			case ISTORE:
				n = Integer.parseInt(arg.toString());
				method.getVarValues().set(n, data.pop());
				break;
			case IADD:
				data.push(data.pop() + data.pop());
				break;
			case IMUL:
				data.push(data.pop() * data.pop());
				break;
			case ISUB:
				Integer n1 = data.pop();
				Integer n2 = data.pop();
				data.push(n2 - n1);
				break;
			case ILT:// v1 = pop() v2= pop() if(v2<v1) push(1) else push(0)
				n1 = data.pop();
				n2 = data.pop();
				if (n2 < n1)
					data.push(1);
				else
					data.push(0);
				break;
			case IAND:
				if (data.pop() * data.pop() == 0)
					data.push(0);
				else
					data.push(1);
				break;
			case IOR:
				if (data.pop() + data.pop() == 0)
					data.push(0);
				else
					data.push(1);
				break;
			case INOT:
				if (data.pop() == 0)
					data.push(1);
				else
					data.push(0);
				break;
			case GOTO:
				n = Integer.parseInt(arg.toString());
				next = n - 1;
				break;
			case IF_FALSE:
				n = Integer.parseInt(arg.toString());
				if (data.pop() == 0)
					next = n - 1;
				break;
			case INVOKEVIRTUAL:
				method.setPC(next);
				activations.push(method);
				method = classFile.getMethods().get(arg.toString());
				next = -1;
				break;
			case IRETURN:
				method = activations.pop();
				next = method.getPC();
				break;
			case PRINT:
				System.out.println(data.pop());
				break;
			}
			next++;
		}
	}

	public ClassFile readFromFile(String name) {
		ObjectInputStream objectIn = null;
		ClassFile clsfile = null;
		try {
			objectIn = new ObjectInputStream(new BufferedInputStream(
					new FileInputStream(name)));
			clsfile = (ClassFile) objectIn.readObject();
			objectIn.close();
		} catch (ClassNotFoundException | IOException e) {

			System.err.println("THE GIVEN FILE PATH/NAME IS WRONG!!");
		}
		return clsfile;
	}

}
