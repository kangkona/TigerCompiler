package codegen.bytecode;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Stack;

import util.Label;

// Given a Java ast, translate it into Java bytecode.

public class TranslateVisitor implements ast.Visitor
{
  private String classId;
  private int index;
  private Hashtable<String,Stack<Integer>> indexTable;
  private codegen.bytecode.type.T type; // type after translation
  private codegen.bytecode.dec.T dec;
  private LinkedList<codegen.bytecode.stm.T> stms;
  private codegen.bytecode.method.T method;
  private codegen.bytecode.classs.T classs;
  private codegen.bytecode.mainClass.T mainClass;
  public codegen.bytecode.program.T program;

  public TranslateVisitor()
  {
    this.classId = null;
    this.indexTable = null;
    this.type = null;
    this.dec = null;
    this.stms = new java.util.LinkedList<codegen.bytecode.stm.T>();
    this.method = null;
    this.classs = null;
    this.mainClass = null;
    this.program = null;
  }

  private void emit(codegen.bytecode.stm.T s)
  {
    this.stms.add(s);
  }

  // /////////////////////////////////////////////////////
  // expressions
  @Override
  public void visit(ast.exp.Add e)
  {
	  e.left.accept(this);
	  e.right.accept(this);
	  emit(new codegen.bytecode.stm.Iadd());
	  return;
  }

  @Override
  public void visit(ast.exp.And e)
  {
	  e.left.accept(this);
	  e.right.accept(this);
	  emit(new codegen.bytecode.stm.Iand());
	  return;
  }

  @Override
  public void visit(ast.exp.ArraySelect e)
  {  
	 e.array.accept(this);
	 e.index.accept(this);
	 emit(new codegen.bytecode.stm.Iaload());
	 return;
  }

  @Override
  public void visit(ast.exp.Call e)
  {
    e.exp.accept(this);
    if (null != e.args){
    	for (ast.exp.T x : e.args) {
    		x.accept(this);
    	}
    }
    e.rt.accept(this);
    codegen.bytecode.type.T rt = this.type;
    java.util.LinkedList<codegen.bytecode.type.T> at = new java.util.LinkedList<codegen.bytecode.type.T>();
    for (ast.type.T t : e.at) {
      t.accept(this);
      at.add(this.type);
    }
    emit(new codegen.bytecode.stm.Invokevirtual(e.id, e.type, at, rt));
    return;
  }

  @Override
  public void visit(ast.exp.False e)
  {
	 emit(new codegen.bytecode.stm.Ldc(0));
	 return;
  }

  @Override
  public void visit(ast.exp.Id e)
  {
    if ( e.isField == false){
    	int index = this.indexTable.get(e.id).peek();
    	ast.type.T type = e.type;
    	if (type.getNum() > 0)// a reference
    		emit(new codegen.bytecode.stm.Aload(index));
    	else
    		emit(new codegen.bytecode.stm.Iload(index));
    	// but what about this is a field?
    	return;
    }else{
		emit(new codegen.bytecode.stm.Aload(0));
		String field_spec = e.idBelong + "/" + e.id;
		String descriptor = e.type.toString();
		if (descriptor.equals("@boolean")
				||  descriptor.equals("@int")){
			descriptor = "I";
		}else if (descriptor.equals("@int[]")){
			descriptor ="[I";
		}else{
			descriptor = "L" + descriptor + ";";
		}
		emit(new codegen.bytecode.stm.Getfield(field_spec,descriptor));
    }
  }

  @Override
  public void visit(ast.exp.Length e)
  {   
	  e.array.accept(this);
	  emit(new codegen.bytecode.stm.Arraylength());
	  return;
  }

  @Override
  public void visit(ast.exp.Lt e)
  {
//    Label tl = new Label(), fl = new Label(), el = new Label();
	Label tl = new Label(), el = new Label();  //add by k  
    e.left.accept(this);
    e.right.accept(this);
    emit(new codegen.bytecode.stm.Ificmplt(tl));
//    emit(new codegen.bytecode.stm.Label(fl));
    emit(new codegen.bytecode.stm.Ldc(0));
    emit(new codegen.bytecode.stm.Goto(el));
    emit(new codegen.bytecode.stm.Label(tl));
    emit(new codegen.bytecode.stm.Ldc(1));
//    emit(new codegen.bytecode.stm.Goto(el));
    emit(new codegen.bytecode.stm.Label(el));
    return;
  }

  @Override
  public void visit(ast.exp.NewIntArray e)
  {
	e.exp.accept(this);
    emit(new codegen.bytecode.stm.Newarray());
    return;
  }

  @Override
  public void visit(ast.exp.NewObject e)
  {
    emit(new codegen.bytecode.stm.New(e.id));
    return;
  }

  @Override
  public void visit(ast.exp.Not e)
  {
	Label f1 = new Label(), f2 = new Label(); 
	e.exp.accept(this);
	emit(new codegen.bytecode.stm.Ifne(f1));
	emit(new codegen.bytecode.stm.Ldc(1));
	emit(new codegen.bytecode.stm.Goto(f2));
	emit(new codegen.bytecode.stm.Label(f1));
	emit(new codegen.bytecode.stm.Ldc(0));
	emit(new codegen.bytecode.stm.Label(f2));
	return;
  }

  @Override
  public void visit(ast.exp.Num e)
  {
    emit(new codegen.bytecode.stm.Ldc(e.num));
    return;
  }

  @Override
  public void visit(ast.exp.Paren e)
  {
	  e.exp.accept(this);
	  return;
  }
  
  @Override
  public void visit(ast.exp.Sub e)
  {
    e.left.accept(this);
    e.right.accept(this);
    emit(new codegen.bytecode.stm.Isub());
    return;
  }

  @Override
  public void visit(ast.exp.This e)
  {
    emit(new codegen.bytecode.stm.Aload(0));
    return;
  }

  @Override
  public void visit(ast.exp.Times e)
  {
    e.left.accept(this);
    e.right.accept(this);
    emit(new codegen.bytecode.stm.Imul());
    return;
  }

  @Override
  public void visit(ast.exp.True e)
  {
	  emit(new codegen.bytecode.stm.Ldc(1));
	  return;
  }

  // statements
  @Override
  public void visit(ast.stm.Assign s)
  {
    
//    System.out.println(s.exp.lineNum+s.id);
    if (s.idisField == false){
    	s.exp.accept(this);
    	int index = this.indexTable.get(s.id).peek();
    	ast.type.T type = s.type;
    	if (type.getNum() > 0)
    		emit(new codegen.bytecode.stm.Astore(index));
    	else
    		emit(new codegen.bytecode.stm.Istore(index));
    }else{
    	emit(new codegen.bytecode.stm.Aload(0));
    	s.exp.accept(this);
//		String field_spec = this.classId + "/" + s.id;
    	String field_spec = s.idBelong + "/" + s.id;
		String descriptor = s.type.toString();
		if (descriptor.equals("@boolean")
				||  descriptor.equals("@int")){
			descriptor = "I";
		}else if (descriptor.equals("@int[]")){
			descriptor ="[I";
		}else{
			descriptor = "L" + descriptor + ";";
		}
    	emit(new codegen.bytecode.stm.Putfield(field_spec,descriptor));
    }
    return;
  }

  @Override
  public void visit(ast.stm.AssignArray s)
  {
	if (s.idisField ==false){
		int index = this.indexTable.get(s.id).peek();
		emit(new codegen.bytecode.stm.Aload(index));
	}else{
		emit(new codegen.bytecode.stm.Aload(0));
//		String field_spec = this.classId + "/" + s.id;
		String field_spec = s.idBelong + "/" + s.id;
		emit(new codegen.bytecode.stm.Getfield(field_spec,"[I"));
	}
	s.index.accept(this);
	s.exp.accept(this);
	emit(new codegen.bytecode.stm.Iastore());
  }

  @Override
  public void visit(ast.stm.Block s)
  {
	for (ast.dec.T local : s.locals){
		ast.dec.Dec dec =(ast.dec.Dec)local;
		dec.accept(this);
	}
	for (ast.stm.T stm : s.stms){
		stm.accept(this);
	}
	for (ast.dec.T local : s.locals){
		ast.dec.Dec dec =(ast.dec.Dec)local;
		Stack<Integer> stack = this.indexTable.get(dec.id);
		if (stack != null){
			stack.pop();
			this.index--;
		}
		this.indexTable.put(dec.id, stack);
	}
  }

  @Override
  public void visit(ast.stm.If s)
  {
    Label tl = new Label(), fl = new Label(), el = new Label();
    s.condition.accept(this);
    emit(new codegen.bytecode.stm.Ifne(tl));
    emit(new codegen.bytecode.stm.Label(fl));
    s.elsee.accept(this);
    emit(new codegen.bytecode.stm.Goto(el));
    emit(new codegen.bytecode.stm.Label(tl));
    s.thenn.accept(this);
    emit(new codegen.bytecode.stm.Goto(el));
    emit(new codegen.bytecode.stm.Label(el));
    return;
  }

  @Override
  public void visit(ast.stm.Print s)
  {
    s.exp.accept(this);
    emit(new codegen.bytecode.stm.Print());
    return;
  }

  @Override
  public void visit(ast.stm.While s)
  {
	    Label in = new Label(), out = new Label(),pre = new Label();
	    emit(new codegen.bytecode.stm.Label(pre));
	    s.condition.accept(this);
	    emit(new codegen.bytecode.stm.Ifne(in));
	    emit(new codegen.bytecode.stm.Goto(out));
	    emit(new codegen.bytecode.stm.Label(in));
	    s.body.accept(this);
	    emit(new codegen.bytecode.stm.Goto(pre));
	    emit(new codegen.bytecode.stm.Label(out));
	    return;
  }

  // type
  @Override
  public void visit(ast.type.Boolean t)
  {
	this.type = new codegen.bytecode.type.Int();
  }

  @Override
  public void visit(ast.type.Class t)
  {
	this.type = new codegen.bytecode.type.Class(t.id);
  }

  @Override
  public void visit(ast.type.Int t)
  {
    this.type = new codegen.bytecode.type.Int();
  }

  @Override
  public void visit(ast.type.IntArray t)
  {
    this.type = new codegen.bytecode.type.IntArray();
  }

  // dec
  @Override
  public void visit(ast.dec.Dec d)
  {
    d.type.accept(this);
    this.dec = new codegen.bytecode.dec.Dec(this.type, d.id);
    Stack<Integer> stack = this.indexTable.get(d.id);
    if (stack == null)
    	stack = new Stack<Integer>();
    stack.push(index++);
    this.indexTable.put(d.id, stack);
    return;
  }

  // method
  @Override
  public void visit(ast.method.Method m)
  {
    // record, in a hash table, each var's index
    // this index will be used in the load store operation
    this.index = 1;
    this.indexTable = new java.util.Hashtable<String, Stack<Integer>>();

    m.retType.accept(this);
    codegen.bytecode.type.T newRetType = this.type;
    java.util.LinkedList<codegen.bytecode.dec.T> newFormals = new java.util.LinkedList<codegen.bytecode.dec.T>();
    if (null != m.formals){
    	for (ast.dec.T d : m.formals) {
    		d.accept(this);
    		newFormals.add(this.dec);
    	}
    }
    java.util.LinkedList<codegen.bytecode.dec.T> locals = new java.util.LinkedList<codegen.bytecode.dec.T>();
    for (ast.dec.T d : m.locals) {
      d.accept(this);
      locals.add(this.dec);
    }
    this.stms = new java.util.LinkedList<codegen.bytecode.stm.T>();
    for (ast.stm.T s : m.stms) {
      s.accept(this);
    }

    // return statement is specially treated
    m.retExp.accept(this);

    if (m.retType.getNum() > 0)
      emit(new codegen.bytecode.stm.Areturn());
    else
      emit(new codegen.bytecode.stm.Ireturn());

    this.method = new codegen.bytecode.method.Method(newRetType, m.id,
        this.classId, newFormals, locals, this.stms, 0, this.index);

    return;
  }

  // class
  @Override
  public void visit(ast.classs.Class c)
  {
    this.classId = c.id;
    java.util.LinkedList<codegen.bytecode.dec.T> newDecs = new java.util.LinkedList<codegen.bytecode.dec.T>();
    for (ast.dec.T dec : c.decs) {
      dec.accept(this);
      newDecs.add(this.dec);
    }
    java.util.LinkedList<codegen.bytecode.method.T> newMethods = new java.util.LinkedList<codegen.bytecode.method.T>();
    for (ast.method.T m : c.methods) {
      m.accept(this);
      newMethods.add(this.method);
    }
    this.classs = new codegen.bytecode.classs.Class(c.id, c.extendss, newDecs,
        newMethods);
    return;
  }

  // main class
  @Override
  public void visit(ast.mainClass.MainClass c)
  {
	this.index = 1;
	this.indexTable = new java.util.Hashtable<String, Stack<Integer>>();
    c.block.accept(this);
    this.mainClass = new codegen.bytecode.mainClass.MainClass(c.id, c.arg,
        this.stms);
    this.stms = new java.util.LinkedList<codegen.bytecode.stm.T>();
    return;
  }

  // program
  @Override
  public void visit(ast.program.Program p)
  {
    // do translations
    p.mainClass.accept(this);

    java.util.LinkedList<codegen.bytecode.classs.T> newClasses = new java.util.LinkedList<codegen.bytecode.classs.T>();
    for (ast.classs.T classs : p.classes) {
      classs.accept(this);
      newClasses.add(this.classs);
    }
    this.program = new codegen.bytecode.program.Program(this.mainClass,
        newClasses);
    return;
  }
}
