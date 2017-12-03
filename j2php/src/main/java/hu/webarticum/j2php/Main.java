package hu.webarticum.j2php;

import java.io.InputStream;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class Main {

	public static void main(String[] args) {
		InputStream inputStream = Main.class.getResourceAsStream("/sample/SampleClass.java.src");
		
		CompilationUnit compilationUnit = JavaParser.parse(inputStream);
		
		System.out.println(new VerySimplePhpTranslator(compilationUnit));
	}

}
