package end.stateMachine;

public class Interpreter {

	public static void main(String[] args) throws Exception {

		String file = "./FinalMain/samples/OhGodPleaseNo.pepe";
		System.out.println("\nFinal Results From StackMachine:");
		StackMachine SM = new StackMachine(file);
		SM.execute();
	}
}
