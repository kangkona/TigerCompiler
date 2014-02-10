package ast;
import control.Control;
public class GenGo implements Visitor
{
  private int indentLevel;
  private String classId;
  private java.io.BufferedWriter writer;
  private java.util.LinkedList<String> badLocals;

  public GenGo()
  {
    this.indentLevel = 4;
    this.classId = null;
    this.badLocals = null;
  }

  private void indent()
  {
    this.indentLevel += 2;
  }

  private void unIndent()
  {
    this.indentLevel -= 2;
  }

  private void printSpaces()
  {
    int i = this.indentLevel;
    while (i-- != 0)
      this.say(" ");
  }
/*
  private void sayln(String s)
  {
    System.out.println(s);
  }

  private void say(String s)
  {
    System.out.print(s);
  }
  */
  private void sayln(String s)
  {
    say(s);
    try {
      this.writer.write("\n");
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  private void say(String s)
  {
    try {
      this.writer.write(s);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  // /////////////////////////////////////////////////////
  // expressions
  @Override
  public void visit(ast.exp.Add e)
  {
    // Lab2, exercise4: filling in missing code.
    // Similar for other methods with empty bodies.
    // Your code here:
    e.left.accept(this);
    this.say(" + ");
    e.right.accept(this);
    return;
  }

  @Override
  public void visit(ast.exp.And e)
  {
    e.left.accept(this);
    this.say(" && ");
    e.right.accept(this);
    return;
  }

  @Override
  public void visit(ast.exp.ArraySelect e)
  {
   e.array.accept(this);
   this.say("["); 
   e.index.accept(this);
   this.say("]");
   return;
  }

  @Override
  public void visit(ast.exp.Call e)
  {
    e.exp.accept(this);
    this.say("." + e.id + "(");
    /*
    if ( null != e.args ){
    	ast.exp.T firstAgr = e.args.pop();
    	firstAgr.accept(this);
    	for (ast.exp.T x : e.args) {
    		this.say(", ");
    		x.accept(this);
      	}
    	e.args.push(firstAgr);
    }
    */
    if (null != e.args){
    	int i = 0;
    	ast.exp.T firstAgr = e.args.get(i);
    	if (e.at.get(i) instanceof ast.type.Class
    		 && (!((ast.type.Class)e.at_real.get(i)).id
    			.equals(((ast.type.Class)e.at.get(i)).id))){
    			this.say("&(");
    			firstAgr.accept(this);
    			this.say(")." + ((ast.type.Class)e.at.get(i)).id);
    		}
    	else{
    		firstAgr.accept(this);
    	}
    	for (i=1;i<e.args.size();i++){
    		this.say(", ");
    		ast.exp.T x = e.args.get(i);
        	if (e.at.get(i) instanceof ast.type.Class
        		 && (!((ast.type.Class)e.at_real.get(i)).id
        			.equals(((ast.type.Class)e.at.get(i)).id))){
        			this.say("&(");
        			x.accept(this);
        			this.say(")." + ((ast.type.Class)e.at.get(i)).id);
        		}
        	else{
        		x.accept(this);
        	}
    	}
    }
    this.say(")");
    return;
  }

  @Override
  public void visit(ast.exp.False e)
  {
    this.say("false");
    return;
  }

  @Override
  public void visit(ast.exp.Id e)
  {
	if(e.isField)
		this.say("this.");
	else if (this.badLocals.contains(e.id))
	    	this.badLocals.remove(e.id);
    this.say(e.id);
    return;
  }

  @Override
  public void visit(ast.exp.Length e)
  {
	this.say("len(");
    e.array.accept(this);
    this.say(")");
    return;
  }

  @Override
  public void visit(ast.exp.Lt e)
  {
    e.left.accept(this);
    this.say(" < ");
    e.right.accept(this);
    return;
  }

  @Override
  public void visit(ast.exp.NewIntArray e)
  {
    this.say("make([]int, ");
    e.exp.accept(this);
    this.say(")");
    return;
  }

  @Override
  public void visit(ast.exp.NewObject e)
  {
    this.say("new (" + e.id + ")");
    return;
  }

  @Override
  public void visit(ast.exp.Not e)
  {
    this.say("!");
    e.exp.accept(this);
    return;
  }

  @Override
  public void visit(ast.exp.Num e)
  {
	this.say(Integer.toString(e.num));
    return;
  }
  
  @Override
  public void visit(ast.exp.Paren e)
  {
    this.say("( ");
    e.exp.accept(this);
    this.say(" )");
    return;
  }

  @Override
  public void visit(ast.exp.Sub e)
  {
    e.left.accept(this);
    this.say(" - ");
    e.right.accept(this);
    return;
  }

  @Override
  public void visit(ast.exp.This e)
  {
    this.say("this");
  }

  @Override
  public void visit(ast.exp.Times e)
  {
    e.left.accept(this);
    this.say(" * ");
    e.right.accept(this);
    return;
  }

  @Override
  public void visit(ast.exp.True e)
  {
    this.say("true");
    return;
  }

  // statements
  @Override
  public void visit(ast.stm.Assign s)
  {
    this.printSpaces();
    if (s.idisField)
    	this.say("this.");
    this.say(s.id + " = ");
    s.exp.accept(this);
    this.sayln("");
    return;
  }

  @Override
  public void visit(ast.stm.AssignArray s)
  {
    this.printSpaces();
    if (s.idisField)
    	this.say("this.");
    
    this.say(s.id + "[");
    s.index.accept(this);
    this.say("] = ");
    s.exp.accept(this);
    this.sayln("");
    return;
  }

  @Override
  public void visit(ast.stm.Block s)
  {
//    this.printSpaces();
//    this.sayln(" {");
//    this.indent();
    for(ast.dec.T dec : s.locals){
      dec.accept(this);
    }
   this.sayln("");
   for(ast.stm.T stm : s.stms){
	   stm.accept(this);
   }
//    this.unIndent();
//    this.printSpaces();
//    this.sayln("}");
    return;
  }

  @Override
  public void visit(ast.stm.If s)
  {
    this.printSpaces();
    this.say("if ");
    s.condition.accept(this);
    this.sayln(" {");
    this.indent();
    s.thenn.accept(this);
    this.unIndent();
    this.printSpaces();
    this.sayln("} else {");
//    this.printSpaces();
//    this.sayln("else {");
    this.indent();
    s.elsee.accept(this);
    this.unIndent();
    this.printSpaces();
    this.sayln("}");
    return;
  }

  @Override
  public void visit(ast.stm.Print s)
  {
    this.printSpaces();
    this.say("fmt.Println(");
    s.exp.accept(this);
    this.sayln(")");
    return;
  }

  @Override
  public void visit(ast.stm.While s)
  {
    this.printSpaces();
    this.say("for ");
    s.condition.accept(this);
    this.sayln(" {");
    this.indent();
    s.body.accept(this);
    this.unIndent();
    this.printSpaces();
    this.sayln("}");
    return;
  }

  // type
  @Override
  public void visit(ast.type.Boolean t)
  {
    this.say("bool");
    return;
  }

  @Override
  public void visit(ast.type.Class t)
  {
    this.say("*" + t.id);
//	  this.say(t.id);
    return;
  }

  @Override
  public void visit(ast.type.Int t)
  {
    this.say("int");
    return;
  }

  @Override
  public void visit(ast.type.IntArray t)
  {
    this.say("[]int");
    return;
  }

  // dec
  @Override
  public void visit(ast.dec.Dec d)
  { 
	this.printSpaces();
    this.say("var " + d.id);
    d.type.accept(this);
    this.sayln("");
    return;
  }

  // method
  @Override
  public void visit(ast.method.Method m)
  {
    this.badLocals = new java.util.LinkedList<String>();  
	this.say("func (this *" +this.classId +")");
    this.say(" " + m.id + "(");
     if ( null != m.formals ){
    	 ast.dec.T firstFormal = m.formals.pop();
    	 ast.dec.Dec first = (ast.dec.Dec) firstFormal;
    	 this.say(first.id + " ");
	     first.type.accept(this);
    	 for (ast.dec.T d : m.formals) {
    		 this.say(", ");
    		 ast.dec.Dec dec = (ast.dec.Dec) d;
    		 // this.say(" " + dec.id + ", ");
    		 this.say(dec.id + " ");
    		 dec.type.accept(this);
    	 }
     }
    this.say(") ");
    m.retType.accept(this);
    this.sayln(" {");
    for (ast.dec.T d : m.locals) {
      ast.dec.Dec dec = (ast.dec.Dec) d;
      this.say("    ");
      this.say("var " + dec.id + " ");
      this.badLocals.addLast(dec.id);
      dec.type.accept(this);
      this.sayln("");
    }
    this.sayln("");
    for (ast.stm.T s : m.stms)
      s.accept(this);
    for (String s : this.badLocals){
    	this.printSpaces();
    	this.sayln(s + " = " + s);
    }
    this.say("    return ");
    m.retExp.accept(this);
//    this.sayln(";");
    this.sayln("\n}");
    return;
  }

  // class
  @Override
  public void visit(ast.classs.Class c)
  {
	
	// gen  interface
	if (!c.methods.isEmpty()){
		this.sayln("type " + c.id + "I interface {");
		this.indent();
		 for (ast.method.T mthd : c.methods){
			  ast.method.Method mthdI = (ast.method.Method)mthd;
		      this.printSpaces();
		      this.say(mthdI.id + "(");
		      if ( null != mthdI.formals ){
		     	 ast.dec.T firstFormal = mthdI.formals.pop();
		     	 ast.dec.Dec first = (ast.dec.Dec) firstFormal;
		     	 this.say(first.id + " ");
	    		 first.type.accept(this);
		     	 for (ast.dec.T d : mthdI.formals) {
		     		 this.say(", ");
		     		 ast.dec.Dec dec = (ast.dec.Dec) d;
		     		 this.say(dec.id + " ");
		    	     dec.type.accept(this);
		     	 }
		     	 mthdI.formals.push(firstFormal);
		      }
		      this.say(") ");
		      mthdI.retType.accept(this);
		      this.sayln("");
		 }
		 this.unIndent();
		 this.sayln("}\n");
		
	}
	
	this.classId = c.id;
    this.sayln("type " + c.id + " struct {");
    this.indent();
    if (c.extendss != null){
      this.printSpaces();
      this.sayln(c.extendss);
    }
    
    for (ast.dec.T d : c.decs) {
      ast.dec.Dec dec = (ast.dec.Dec) d;
      this.printSpaces();
      this.say(dec.id + " ");
      dec.type.accept(this);
      this.sayln("");
    }
    this.unIndent();
//    this.printSpaces();
    this.sayln("}\n");
   
    for (ast.method.T mthd : c.methods)
      mthd.accept(this);
    return;
  }

  // main class
  @Override
  public void visit(ast.mainClass.MainClass c)
  {
	  this.sayln("func main(){");
	  this.indent();
	  c.block.accept(this);
	  this.unIndent();
	  this.sayln("}");
      return;
  }
  // program
  @Override
  public void visit(ast.program.Program p)
  {
    try {
	    String outputName = null;
	    if (Control.outputName != null)
	       outputName = Control.outputName;
	    else if (Control.fileName != null)
	       outputName = Control.fileName + ".go";
	    else
	       outputName = "a.go";

	    this.writer = new java.io.BufferedWriter(new java.io.OutputStreamWriter(
	                  new java.io.FileOutputStream(outputName)));
	    } catch (Exception e) {
	      e.printStackTrace();
	      System.exit(1);
	      }
    
    this.sayln("package main\nimport \"fmt\"");
    p.mainClass.accept(this);
    this.sayln("");
    for (ast.classs.T classs : p.classes) {
      classs.accept(this);
    }
    this.sayln("\n\n");
    try {
        this.writer.close();
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    System.out.println("Ok");
  }
}