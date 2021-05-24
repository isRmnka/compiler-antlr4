import org.antlr.v4.gui.Trees;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.tree.ParseTree;
import rmn.grammar.JavaLexer;
import rmn.grammar.JavaParser;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class TestAST {

	public static void main(String[] args) throws Exception {

		ArrayList<String> files = new ArrayList<String>();
		getFiles("./MAIN/testFiles", files);
			
			CharStream input =null;
			try {
				input = CharStreams.fromFileName("./MAIN/testFiles/factorial.java");
			} catch (IOException e) {
				e.printStackTrace();
			}
			JavaLexer lex=new JavaLexer(input);
			JavaParser pars = new JavaParser(new BufferedTokenStream(lex));
			ParseTree tree=pars.program();
			Trees.inspect(tree, pars);
			System.out.println("We are alive...");
	}

	public static void getFiles(String directoryName, ArrayList<String> files) {
		File directory = new File(directoryName);

		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile()) {
				files.add(file.getPath());
			} else if (file.isDirectory()) {
				getFiles(file.getAbsolutePath(), files);
			}
		}
	}

}