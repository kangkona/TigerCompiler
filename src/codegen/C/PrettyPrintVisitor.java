package codegen.C;

import control.Control;

public class PrettyPrintVisitor implements Visitor
{
  private int indentLevel;
  private java.io.BufferedWriter writer;
  private java.util.LinkedList<String> locals;
  private String fieldsMap;

  public PrettyPrintVisitor()
  {
    this.indentLevel = 2;
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
  public void visit(codegen.C.exp.Add e)
  {
   e.left.accept(this);
   this.say(" + ");
   e.right.accept(this);
   return;
  }

  @Override
  public void visit(codegen.C.exp.And e)
  {
   e.left.accept(this);
   this.say(" * ");
   e.right.accept(this);
   return;
  }

  @Override
  public void visit(codegen.C.exp.ArraySelect e)
  {
   e.array.accept(this);
   this.say("["); 
   e.index.accept(this);
   this.say("+4]");
   return;
  }

  @Override
  public void visit(codegen.C.exp.Call e)
  { 
	String tmp = "frame." + e.assign;
    this.say("(" + tmp + "=");
    e.exp.accept(this);
    this.say(", ");
    this.say(tmp + "->vptr->" + e.id + "(" + tmp);
    int size = e.args.size();
    if (size == 0) {
      this.say("))");
      return;
    }
    for (codegen.C.exp.T x : e.args) {
      this.say(", ");
      x.accept(this);
    }
    this.say("))");
    return;
  }

  @Override
  public void visit(codegen.C.exp.Id e)
  {
	if (e.isField)
		this.say("this->");
	if(this.locals.contains(e.id))
		this.say("frame.");
	this.say(e.id);
  }

  @Override
  public void visit(codegen.C.exp.Length e)
  {
	this.say("len(");
	e.array.accept(this);
	this.say(")");
	return;
  }

  @Override
  public void visit(codegen.C.exp.Lt e)
  {
    e.left.accept(this);
    this.say(" < ");
    e.right.accept(this);
    return;
  }

  @Override  
  public void visit(codegen.C.exp.NewIntArray e)
  {
	 this.say("(int*)Tiger_new_array(");
	 e.exp.accept(this);
	 this.say(")");
  }

  @Override
  public void visit(codegen.C.exp.NewObject e)
  {
    this.say("((struct " + e.id + "*)(Tiger_new (&" + e.id
        + "_vtable_, sizeof(struct " + e.id + "))))");
    return;
  }

  @Override
  public void visit(codegen.C.exp.Not e)
  {
	this.say("!");
    e.exp.accept(this);
    return;
  }

  @Override
  public void visit(codegen.C.exp.Num e)
  {
    this.say(Integer.toString(e.num));
    return;
  }

  @Override
  public void visit(codegen.C.exp.Paren e)
  {
	this.say("(");
	e.exp.accept(this);
	this.say(")");
	return;
  }
  
  @Override
  public void visit(codegen.C.exp.Sub e)
  {
    e.left.accept(this);
    this.say(" - ");
    e.right.accept(this);
    return;
  }

  @Override
  public void visit(codegen.C.exp.This e)
  {
    this.say("this");
  }

  @Override
  public void visit(codegen.C.exp.Times e)
  {
    e.left.accept(this);
    this.say(" * ");
    e.right.accept(this);
    return;
  }

  // statements
  @Override
  public void visit(codegen.C.stm.Assign s)
  {
    this.printSpaces();
    if(s.idisField)
    	this.say("this->");
	if(this.locals.contains(s.id))
		this.say("frame.");
    this.say(s.id + " = ");
    s.exp.accept(this);
    this.sayln(";");
    return;
  }

  @Override
  public void visit(codegen.C.stm.AssignArray s)
  {
	this.printSpaces();
	if(s.idisField)
    	this.say("this->");
	if(this.locals.contains(s.id))
		this.say("frame.");
	this.say(s.id + "[");
	s.index.accept(this);
	this.say("+4] = ");
	s.exp.accept(this);
	this.sayln(";");
	return;
  }

  @Override
  public void visit(codegen.C.stm.Block s)
  {
	this.printSpaces();
	this.sayln("{");
	this.indent();
	/*
	for (codegen.C.dec.T d : s.locals) {
		codegen.C.dec.Dec dec = (codegen.C.dec.Dec) d;
	    this.say("    ");
	    dec.type.accept(this);
	    this.say(" " + dec.id + ";\n");
	    }
	*/
	this.sayln("");
    for (codegen.C.stm.T ss : s.stms)
    	ss.accept(this);
    this.unIndent();
    this.printSpaces();
    this.sayln("}");
    return;
  }

  @Override
  public void visit(codegen.C.stm.If s)
  {
    this.printSpaces();
    this.say("if (");
    s.condition.accept(this);
    this.sayln(")");
    this.indent();
    s.thenn.accept(this);
    this.unIndent();
    this.sayln("");
    this.printSpaces();
    this.sayln("else");
    this.indent();
    s.elsee.accept(this);
    this.sayln("");
    this.unIndent();
    return;
  }

  @Override
  public void visit(codegen.C.stm.Print s)
  {
    this.printSpaces();
	this.say("System_out_println (");
	s.exp.accept(this);
	this.sayln(");");
	return;
  }

  @Override
  public void visit(codegen.C.stm.While s)
  {
	  this.printSpaces();
	  this.say("while (");
	  s.condition.accept(this);
	  this.sayln(")");
	  this.indent();
	  s.body.accept(this);
	  this.unIndent();
	  this.sayln("");
	  return;
  }

  // type
  @Override
  public void visit(codegen.C.type.Class t)
  {
    this.say("struct " + t.id + " *");
  }

  @Override
  public void visit(codegen.C.type.Int t)
  {
    this.say("int");
  }

  @Override
  public void visit(codegen.C.type.IntArray t)
  {
	this.say("int*");
  }

  // dec
  @Override
  public void visit(codegen.C.dec.Dec d)
  {
	this.printSpaces();
		d.type.accept(this);
		this.say(" " + d.id);
		this.sayln(";");
	return;
  }

  // method
  @Override
  public void visit(codegen.C.method.Method m)
  {
   //code to gen bit-vector
	String args_bitvector="\"";
	int locals_bitvector=0;
	for (codegen.C.dec.T d:m.formals){
		codegen.C.dec.Dec dd = (codegen.C.dec.Dec)d;
		if (dd.type instanceof codegen.C.type.Int)
			args_bitvector+="0";
		else
			args_bitvector+="1";
	}
	for (codegen.C.dec.T d:m.locals){
		codegen.C.dec.Dec dd = (codegen.C.dec.Dec)d;
		if (!(dd.type instanceof codegen.C.type.Int))
			locals_bitvector+=1;
	}
	args_bitvector+="\"";
	this.sayln("char *"+m.classId + "_" +m.id+"_arguments_gc_map = " + args_bitvector +";");
	this.sayln("int "+m.classId + "_" +m.id+"_locals_gc_map = " + locals_bitvector+";");

	this.locals = new java.util.LinkedList<String>();
	//code to gen gc_frame
	this.sayln("struct "+m.classId + "_" +m.id+"_gc_frame{");
	this.sayln("void *prev;");
	this.sayln("char *arguments_gc_map;");
	this.sayln("int *arguments_base_address;");
	this.sayln("int locals_gc_map;");
	for (codegen.C.dec.T d : m.locals) {
		codegen.C.dec.Dec dec = (codegen.C.dec.Dec) d;
		if(!(dec.type instanceof codegen.C.type.Int)){
			this.say("  ");
			dec.type.accept(this);
			this.say(" " + dec.id + ";\n");
			this.locals.addLast(dec.id);     //associate with frame
		}
	}
	this.sayln("};");
	
	
	//code to gen method
    m.retType.accept(this);
    this.say(" " + m.classId + "_" + m.id + "(");
    int size = m.formals.size();
    for (codegen.C.dec.T d : m.formals) {
      codegen.C.dec.Dec dec = (codegen.C.dec.Dec) d;
      size--;
      dec.type.accept(this);
      this.say(" " + dec.id);
      if (size > 0)
        this.say(", ");
    }
    this.sayln(")");
    this.sayln("{");
    
    //code to put these GC frames onto the call stack
    this.sayln("  struct "+m.classId + "_" +m.id+"_gc_frame frame;");
    this.sayln("  frame.prev = prev;");
    this.sayln("  prev = &frame;");
    this.sayln("  frame.arguments_gc_map = "+m.classId + "_" +m.id+"_arguments_gc_map;");
    this.sayln("  frame.arguments_base_address = (int*)&this;");
    this.sayln("  frame.locals_gc_map = "+m.classId + "_" +m.id+"_locals_gc_map;");
    
    
    for (codegen.C.dec.T d : m.locals) {
      codegen.C.dec.Dec dec = (codegen.C.dec.Dec) d;
      this.say("  ");
      if(dec.type instanceof codegen.C.type.Int){
    	  dec.type.accept(this);
    	  this.say(" " + dec.id + ";\n");
      }
    }
    
    this.sayln("");
    for (codegen.C.stm.T s : m.stms)
      s.accept(this);
    this.sayln("prev = frame.prev;");  //pop gc frame
    this.say("  return ");
    m.retExp.accept(this);
    this.sayln(";");
    this.sayln("}");
    return;
  }
 
  public void methodDeclear(codegen.C.method.Method m)
  {
    m.retType.accept(this);
    this.say(" " + m.classId + "_" + m.id + "(");
    int size = m.formals.size();
    for (codegen.C.dec.T d : m.formals) {
      codegen.C.dec.Dec dec = (codegen.C.dec.Dec) d;
      size--;
      dec.type.accept(this);
      this.say(" " + dec.id);
      if (size > 0)
        this.say(", ");
    }
    this.sayln(");");
  }
  
  @Override
  public void visit(codegen.C.mainMethod.MainMethod m)
  {
    this.locals = new java.util.LinkedList<String>();
    int locals_bitvector= 0;
	//code to gen gc_frame
	this.sayln("struct Tiger_main_gc_frame{");
	this.sayln("void *prev;");
	this.sayln("char *arguments_gc_map;");
	this.sayln("int *arguments_base_address;");
	this.sayln("int locals_gc_map;");
	for (codegen.C.dec.T d : m.block.locals) {
		codegen.C.dec.Dec dec = (codegen.C.dec.Dec) d;
            if (!(dec.type instanceof  codegen.C.type.Int)){
			locals_bitvector++;
                  this.say("  ");
			dec.type.accept(this);
			this.say(" " + dec.id + ";\n");
			this.locals.addLast(dec.id);     //associate with frame
            }
	}
	this.sayln("};");
    this.sayln("char *Tiger_main_arguments_gc_map = \"\";");
    this.sayln("int Tiger_main_locals_gc_map = " + locals_bitvector +";");
    this.sayln("int Tiger_main ()");
    this.sayln("{");
	this.sayln("  struct Tiger_main_gc_frame frame;");
	this.sayln("  frame.prev = prev;");
	this.sayln("  prev = &frame;");
	this.sayln("  frame.arguments_gc_map = Tiger_main_arguments_gc_map;");
//	this.sayln("frame.arguments_base_address = &this;");
	this.sayln("  frame.locals_gc_map = Tiger_main_locals_gc_map;");
    m.block.accept(this);
    this.sayln("  prev = frame.prev;");
    this.sayln("}");
    return;
  }

  // vtables
  @Override
  public void visit(codegen.C.vtable.Vtable v)
  {
    this.sayln("struct " + v.id + "_vtable");
    this.sayln("{");
    this.say("  ");
    this.sayln("char *"+v.id+"_gc_map;"); 
    for (codegen.C.Ftuple t : v.ms) {
      this.say("  ");
      t.ret.accept(this);
      this.sayln(" (*" + t.id + ")();");
    }
    this.sayln("};\n");
    return;
  }

  private void outputVtable(codegen.C.vtable.Vtable v)
  {
    this.sayln("struct " + v.id + "_vtable " + v.id + "_vtable_ = ");
    this.sayln("{");
    this.say("  ");
    this.sayln(v.id +"_gc_map,");
    for (codegen.C.Ftuple t : v.ms) {
      this.say("  ");
      this.sayln(t.classs + "_" + t.id + ",");
    }
    this.sayln("};\n");
    return;
  }

  // class
  @Override
  public void visit(codegen.C.classs.Class c)
  {
    this.sayln("struct " + c.id);
    this.sayln("{");
    this.sayln("  struct " + c.id + "_vtable *vptr;");
    this.sayln("  int isObjOrArray;");
    this.sayln("  int length;");
    this.sayln("  void* forwarding;");
    this.fieldsMap ="\"";
    for (codegen.C.Tuple t : c.decs) {
      this.say("  ");
      t.type.accept(this);
      this.say(" ");
      this.sayln(t.id + ";");
      if (t.type instanceof codegen.C.type.Int)
    	  this.fieldsMap+="0";
      else
    	  this.fieldsMap+="1";
    }
    this.fieldsMap+="\"";
    this.sayln("};");
    this.sayln("char " + c.id +"_gc_map[]=" + this.fieldsMap +";");
    return;
  }

  // program
  @Override
  public void visit(codegen.C.program.Program p)
  {
    // we'd like to output to a file, rather than the "stdout".
    try {
      String outputName = null;
      if (Control.outputName != null)
        outputName = Control.outputName;
      else if (Control.fileName != null)
        outputName = Control.fileName + ".c";
      else
        outputName = "a.c";

      this.writer = new java.io.BufferedWriter(new java.io.OutputStreamWriter(
          new java.io.FileOutputStream(outputName)));
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }

    this.sayln("// This is automatically generated by the Tiger compiler.");
    this.sayln("// Do NOT modify!\n");
    this.sayln("#include<memory.h>");
    this.sayln("// structures");
    for (codegen.C.classs.T c : p.classes) {
      c.accept(this);
    }

    this.sayln("// vtables structures");
    for (codegen.C.vtable.T v : p.vtables) {
      v.accept(this);
    }
    this.sayln("");
    this.sayln("extern void *prev;");
    this.sayln("// methods declear");
    for (codegen.C.method.T m : p.methods) {
        methodDeclear((codegen.C.method.Method)m);
      }

    this.sayln("// vtables");
    for (codegen.C.vtable.T v : p.vtables) {
      outputVtable((codegen.C.vtable.Vtable) v);
    }
    this.sayln("");
    
    this.sayln("// methods");
    for (codegen.C.method.T m : p.methods) {
      m.accept(this);
    }
    this.sayln("");
    this.sayln("// main method");
    p.mainMethod.accept(this);
    this.sayln("");

    this.say("\n\n");

    try {
      this.writer.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

}
