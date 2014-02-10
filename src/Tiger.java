import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import lexer.Lexer;
import lexer.Token;
import lexer.Token.Kind;

import control.CommandLine;
import control.Control;

import parser.Parser;

// get information of OS
class OSinfo {
	
	private static String OS = System.getProperty("os.name").toLowerCase();
	private OSinfo(){}	
	public static boolean isLinux(){
		return OS.indexOf("linux")>=0;
	}
	public static boolean isMacOS(){
		return OS.indexOf("mac")>=0&&OS.indexOf("os")>0&&OS.indexOf("x")<0;
	}
	public static boolean isMacOSX(){
		return OS.indexOf("mac")>=0&&OS.indexOf("os")>0&&OS.indexOf("x")>0;
	}
	public static boolean isWindows(){
		return OS.indexOf("windows")>=0;
	}
}

public class Tiger
{
  public static void main(String[] args)
  {
    InputStream fstream;
    Parser parser;

    // ///////////////////////////////////////////////////////
    // handle command line arguments
    CommandLine cmd = new CommandLine();
    String fname = cmd.scan(args);

    // /////////////////////////////////////////////////////
    // to test the pretty printer on the "test/Fac.java" program
    if (control.Control.testFac) {
      System.out.println("Testing the Tiger compiler on Fac.java starting:");
      ast.PrettyPrintVisitor pp = new ast.PrettyPrintVisitor();
      ast.Fac.prog.accept(pp);

      // elaborate the given program, this step is necessary
      // for that it will annotate the AST with some
      // informations used by later phase.
      elaborator.ElaboratorVisitor elab = new elaborator.ElaboratorVisitor();
      ast.Fac.prog.accept(elab);

      // Compile this program to C.
      System.out.println("Translate the program to C");
      codegen.C.TranslateVisitor trans2C = new codegen.C.TranslateVisitor();
      // pass this visitor to the "Fac.java" program.
      ast.Fac.prog.accept(trans2C);
      // this visitor will return an AST for C.
      codegen.C.program.T cast = trans2C.program;
      // output the AST for C.
      codegen.C.PrettyPrintVisitor ppc = new codegen.C.PrettyPrintVisitor();
      cast.accept(ppc);
      System.out.println("Testing the Tiger compiler on Fac.java finished.\n");
      System.exit(1);
    }

    if (control.Control.testSum) {
      System.out.println("Testing the Tiger compiler on Sum.java starting:");
      ast.PrettyPrintVisitor pp = new ast.PrettyPrintVisitor();
      ast.Fac.prog_sum.accept(pp);
      
      elaborator.ElaboratorVisitor elab = new elaborator.ElaboratorVisitor();
      ast.Fac.prog_sum.accept(elab);
   // Compile this program to C.
      System.out.println("Translate the program to C");
      codegen.C.TranslateVisitor trans2C = new codegen.C.TranslateVisitor();
      // pass this visitor to the "Fac.java" program.
      ast.Fac.prog_sum.accept(trans2C);
      // this visitor will return an AST for C.
      codegen.C.program.T cast = trans2C.program;
      // output the AST for C.
      codegen.C.PrettyPrintVisitor ppc = new codegen.C.PrettyPrintVisitor();
      cast.accept(ppc);
      System.out.println("Testing the Tiger compiler on Sum.java finished.\n");
      System.out.println("code generation starting");
      System.exit(1);
    }

    if (fname == null) {
      cmd.usage();
      return;
    }
    Control.fileName = fname;

    // /////////////////////////////////////////////////////
    // it would be helpful to be able to test the lexer
    // independently.
    if (control.Control.testlexer) {
      System.out.println("Testing the lexer. All tokens:");
      try {
        fstream = new BufferedInputStream(new FileInputStream(fname));
        Lexer lexer = new Lexer(fname, fstream);
        Token token = lexer.nextToken();
        while (token.kind != Kind.TOKEN_EOF) {
          System.out.println(token.toString());
          token = lexer.nextToken();
        }
        fstream.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
      System.exit(1);
    }

    // /////////////////////////////////////////////////////////
    // normal compilation phases.
    ast.program.T theAst = null;

    // parsing the file, get an AST.
    try {
      fstream = new BufferedInputStream(new FileInputStream(fname));
      parser = new Parser(fname, fstream);

      theAst = parser.parse();

      fstream.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }

    // pretty printing the AST, if necessary
    if (control.Control.dumpAst) {
      ast.PrettyPrintVisitor pp = new ast.PrettyPrintVisitor();
      theAst.accept(pp);
      System.exit(1);
    }
    
    // elaborate the AST, report all possible errors.
    elaborator.ElaboratorVisitor elab = new elaborator.ElaboratorVisitor();
    theAst.accept(elab);
    // code generation
    switch (control.Control.codegen) {
    case Bytecode:
      codegen.bytecode.TranslateVisitor trans = new codegen.bytecode.TranslateVisitor();
      theAst.accept(trans);
      codegen.bytecode.program.T bytecodeAst = trans.program;
      codegen.bytecode.PrettyPrintVisitor ppbc = new codegen.bytecode.PrettyPrintVisitor();
      bytecodeAst.accept(ppbc);
      break;
    case C:
      codegen.C.TranslateVisitor transC = new codegen.C.TranslateVisitor();
      theAst.accept(transC);
      codegen.C.program.T cAst = transC.program;
      codegen.C.PrettyPrintVisitor ppc = new codegen.C.PrettyPrintVisitor();
      cAst.accept(ppc);
      break;
    case Dalvik:
      codegen.dalvik.TranslateVisitor transDalvik = new codegen.dalvik.TranslateVisitor();
      theAst.accept(transDalvik);
      codegen.dalvik.program.T dalvikAst = transDalvik.program;
      codegen.dalvik.PrettyPrintVisitor ppDalvik = new codegen.dalvik.PrettyPrintVisitor();
      dalvikAst.accept(ppDalvik);
      break;
    case X86:
      // similar
      break;
    case Go:
      ast.GenGo gg = new ast.GenGo();
      theAst.accept(gg);
      break;
    case XML:
        ast.GenXML gl = new ast.GenXML();
        theAst.accept(gl);
        break;
    default:
      break;
    }
    
    // Lab3, exercise 6: add some glue code to
    // call gcc to compile the generated C or x86
//<<<<<<< HEAD
    // file, or call java to run the bytecode file.
    // Your code:
    /*
     * glue code:If your OS is not Linux,Please comment the 
     * below code,link and run manually,we are sorry about that.
     */
    // /*
    if(OSinfo.isLinux()){
    Runtime rt = Runtime.getRuntime();
    Process proc; 
    String[] pwd = fname.split("test/");
    String env = pwd[0];

    switch (control.Control.codegen) {
    case C:
//    	link = "gcc "+ fname + ".c "+ env + "runtime/runtime.c";
//    	String[] clcmds = {"/bin/sh","-c",link}; //link
    	String[] clcmds = {"gcc",fname+".c",env+"runtime/runtime.c"};
    	String[] crcmds = {"./a.out"}; //run
    	String link;
    	try {   
            proc = rt.exec(clcmds);
            BufferedReader br = new BufferedReader(new InputStreamReader(proc
                                    .getInputStream()));
            String line;
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                System.out.println(line);
                System.out.flush();
            }
            line = null;
            br = new BufferedReader(new InputStreamReader(proc
                                            .getErrorStream()));
            while ((line = br.readLine()) != null) {  
                System.out.println(line);  
            }
            	proc = rt.exec(crcmds);
            	br = new BufferedReader(new InputStreamReader(proc
                                       		.getInputStream()));
            line = null;
            while ((line = br.readLine()) != null && !line.isEmpty()) {
            	System.out.println(line);
            	System.out.flush();
            }
            line = null;
            br = new BufferedReader(new InputStreamReader(proc
                                             .getErrorStream()));
            while ((line = br.readLine()) != null) {  
            	System.out.println(line); 
            }
            br.close();  
        } catch (IOException e) {  
        	e.printStackTrace(); 
        }
        break;
    case Bytecode:
    	String objectfile = pwd[pwd.length-1].split(".java")[0];
        link = "java -jar "+ env + "jasmin.jar " + "*.j";
        String run = "java "+objectfile;
    	String[] blcmds = {"/bin/sh","-c",link};//link
    	String[] brcmds = {"/bin/sh","-c",run};//run
     	try {   
            proc = rt.exec(blcmds);
            BufferedReader br = new BufferedReader(new InputStreamReader(proc
                                    .getInputStream()));
            String line;
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                System.out.println(line);
                System.out.flush();
            }
            line = null;
            br = new BufferedReader(new InputStreamReader(proc
                     .getErrorStream()));
            while ((line = br.readLine()) != null) {  
                System.out.println(line);  
            }
            proc = rt.exec(brcmds);
            br = new BufferedReader(new InputStreamReader(proc
                                    .getInputStream()));
            line = null;
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                System.out.println(line);
                System.out.flush();
            }
            line = null;
            br = new BufferedReader(new InputStreamReader(proc
                     .getErrorStream()));
            while ((line = br.readLine()) != null) {  
                System.out.println(line);  
            }
            br.close();  
        } catch (IOException e) {  
        	e.printStackTrace(); 
        }
        break;
    case X86:
      // similar
      break;
    default:
      break;
     }
    }else{
    	System.out.println("If your OS is not Linux,Please comment the"
				+ " glue code,link and run manually,we are sorry about that.");
		System.exit(0);
    }
//*/
    return;
  }
}
