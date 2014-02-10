package ast;
import control.Control;
public class GenXML implements Visitor
{

  private java.io.BufferedWriter writer;

  public GenXML()
  {
  }

 
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
	this.sayln("<add>");
	this.sayln("<left>");
	e.left.accept(this);
	this.sayln("</left>\n<right>");
	e.right.accept(this);
	this.sayln("</right>\n</add>");
	return;
  }

  @Override
  public void visit(ast.exp.And e)
  {
	this.sayln("<and>");
	this.sayln("<left>");
    e.left.accept(this);
    this.sayln("</left>\n<right>");
    e.right.accept(this);
    this.sayln("</right>\n</and>");
    return;
  }

  @Override
  public void visit(ast.exp.ArraySelect e)
  {
   this.sayln("<arrayselect>\n<array>");
   e.array.accept(this);
   this.sayln("</array>\n<index>"); 
   e.index.accept(this);
   this.sayln("</index>\n</arrayselect>");
   return;
  }

  @Override
  public void visit(ast.exp.Call e)
  {
	this.sayln("<call>\n<caller>");
    e.exp.accept(this);
    this.say("</caller>\n<method>");
    this.sayln(e.id + "</method>");
    this.sayln("<realArgs>");
    if ( null != e.args ){
    	for (ast.exp.T x : e.args) {
    		x.accept(this);
      	}
    }
    this.sayln("</realArgs>");
    this.sayln("</call>");
    
    return;
  }

  @Override
  public void visit(ast.exp.False e)
  {
    this.say("<false />");
    return;
  }

  @Override
  public void visit(ast.exp.Id e)
  {
	this.say("<id");
	if(e.isField)
		this.say(" isField=\"yes\"");
	this.say(">");
    this.say(e.id);
    this.sayln("</id>");
    return;
  }

  @Override
  public void visit(ast.exp.Length e)
  {
	this.say("<length>");
    e.array.accept(this);
    this.sayln("</length>");
    return;
  }

  @Override
  public void visit(ast.exp.Lt e)
  {	
	this.sayln("<lt>");
	this.sayln("<left>");
    e.left.accept(this);
    this.sayln("</left>\n<right>");
    e.right.accept(this);
    this.sayln("</right>\n</lt>");
    return;
  }

  @Override
  public void visit(ast.exp.NewIntArray e)
  {
    this.say("<newarray>");
    e.exp.accept(this);
    this.sayln("</newarray>");
    return;
  }

  @Override
  public void visit(ast.exp.NewObject e)
  {
    this.sayln("<newobject>" + e.id +"</newobject>");
    return;
  }

  @Override
  public void visit(ast.exp.Not e)
  {
    this.say("<not>");
    e.exp.accept(this);
    this.sayln("</not>");
    return;
  }

  @Override
  public void visit(ast.exp.Num e)
  {
	this.say("<num>");
	this.say(Integer.toString(e.num));
	this.sayln("</num>");
    return;
  }
  
  @Override
  public void visit(ast.exp.Paren e)
  {
    this.sayln("<paren>");
    e.exp.accept(this);
    this.sayln("</paren>");
    return;
  }

  @Override
  public void visit(ast.exp.Sub e)
  {	
	this.sayln("<sub>");
	this.sayln("<left>");
    e.left.accept(this);
    this.sayln("</left>\n<right>");
    e.right.accept(this);
    this.sayln("</right>\n</sub>");
    return;
  }

  @Override
  public void visit(ast.exp.This e)
  {
    this.sayln("<this />");
  }

  @Override
  public void visit(ast.exp.Times e)
  {	
	this.sayln("<times>");
	this.sayln("<left>");
    e.left.accept(this);
    this.sayln("</left>\n<right>");
    e.right.accept(this);
    this.sayln("</right>\n</times>");
    return;
  }

  @Override
  public void visit(ast.exp.True e)
  {
    this.sayln("<true />");
    return;
  }

  // statements
  @Override
  public void visit(ast.stm.Assign s)
  {
	this.sayln("<assign>\n<left>");
	this.say("<id");
	if(s.idisField)
		this.say(" isField=\"yes\"");
	this.say(">");
    this.say(s.id);
    this.sayln("</id>\n</left>");
    this.sayln("<right>");
    s.exp.accept(this);
    this.sayln("</right>\n</assign>");
    return;
  }

  @Override
  public void visit(ast.stm.AssignArray s)
  {
	this.sayln("<assign>\n<left>");
	this.say("<array");
	if(s.idisField)
		this.say(" isField=\"yes\"");
	this.say(">");
	this.say(s.id);
	this.sayln("</array>\n<index>");
	s.index.accept(this);
	this.sayln("</index>\n</left>\n<right>");
	s.exp.accept(this);
	this.sayln("</right>\n</assign>");
  }

  @Override
  public void visit(ast.stm.Block s)
  {
   this.sayln("<block>\n<locals>");
   for(ast.dec.T dec : s.locals){
      dec.accept(this);
    }
   this.sayln("</locals>\n<statements>");
   for(ast.stm.T stm : s.stms){
	   stm.accept(this);
   }
   this.sayln("</statements>\n</block>");
   return;
  }

  @Override
  public void visit(ast.stm.If s)
  {
    this.sayln("<if>\n<condition>");
    s.condition.accept(this);
    this.sayln("</condition>\n<then>");
    s.thenn.accept(this);
    this.sayln("</then>\n<else>");
    s.elsee.accept(this);
    this.sayln("</else>\n</if>");
    return;
  }

  @Override
  public void visit(ast.stm.Print s)
  {
    this.sayln("<print>");
    s.exp.accept(this);
    this.sayln("</print>");
    return;
  }

  @Override
  public void visit(ast.stm.While s)
  {
    this.sayln("<while>\n<condition>");
    s.condition.accept(this);
    this.sayln("</condition>");
    this.sayln("<body>");
    s.body.accept(this);
    this.sayln("</body>\n</while>");
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
    this.say(t.id);
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
	this.sayln("array");
    return;
  }

  // dec
  @Override
  public void visit(ast.dec.Dec d)
  { 
	this.say("<dec type=\"");
    d.type.accept(this);
    this.sayln("\">" + d.id + "</dec>");
    return;
  }

  // method
  @Override
  public void visit(ast.method.Method m)
  { 
	this.say("<method name=\"" + m.id +"\" rtype=\"");
	m.retType.accept(this);
	this.sayln("\">");
	this.sayln("<formals>");
	if (null != m.formals){
		 for (ast.dec.T dec : m.formals){
    		 dec.accept(this);
    	 }
	}
	this.sayln("</formals>");
	this.sayln("<locals>");
    for (ast.dec.T dec : m.locals) {
    	dec.accept(this);
   	}
	this.sayln("</locals>");
	this.sayln("<statements>");
    for (ast.stm.T s : m.stms){
        s.accept(this);
    }
    this.sayln("<return>");
    m.retExp.accept(this);
    this.sayln("</return>");
	this.sayln("</statements>");
	this.sayln("</method>");
  }

  // class
  @Override
  public void visit(ast.classs.Class c)
  {
//	this.classId = c.id;
    this.say("<class name=\"" + c.id +"\"");
    if (c.extendss != null){
      this.sayln(" extends=\"" + c.extendss + "\"");
    }
    this.sayln(">");
    this.sayln("<fields>");
    for (ast.dec.T d : c.decs) {
      ast.dec.Dec dec = (ast.dec.Dec) d;
      this.say("<field type=\"");
      dec.type.accept(this);
      this.say("\">");
      this.say(dec.id);
      this.sayln("</field>");
    }
    this.sayln("</fields>");
    this.sayln("<methods>");
    for (ast.method.T mthd : c.methods)
      mthd.accept(this);
    this.sayln("</methods>");
    this.sayln("</class>");
    return;
  }

  // main class
  @Override
  public void visit(ast.mainClass.MainClass c)
  {
	  this.sayln("<main name=\"" + c.id + "\">");
	  c.block.accept(this);
	  this.sayln("</main>");
      return;
  }
  // program
  @Override
  public void visit(ast.program.Program p)
  {
	String outputName = null;
    try {
	   
	    if (Control.outputName != null)
	       outputName = Control.outputName;
	    else if (Control.fileName != null)
	       outputName = Control.fileName + ".xml";
	    else
	       outputName = "a.xml";

	    this.writer = new java.io.BufferedWriter(new java.io.OutputStreamWriter(
	                  new java.io.FileOutputStream(outputName)));
	    } catch (Exception e) {
	      e.printStackTrace();
	      System.exit(1);
	      }
    this.sayln("<program src=\""+outputName+"\">");
    p.mainClass.accept(this);
    for (ast.classs.T classs : p.classes) {
      classs.accept(this);
    }
    this.sayln("</program>");
    try {
        this.writer.close();
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    System.out.println("Ok");
  }
}