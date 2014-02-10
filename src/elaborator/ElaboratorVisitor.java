package elaborator;

public class ElaboratorVisitor implements ast.Visitor
{
  public ClassTable classTable; // symbol table for class
  public MethodTable methodTable; // symbol table for each method
  public BlockTable  blockTable; //symbol table for each block
  public String currentClass; // the class name being elaborated
  public ast.type.T type; // type of the expression being elaborated
  public static int errorCount;

  public ElaboratorVisitor()
  {
    this.classTable = new ClassTable();
    this.methodTable = new MethodTable();
    this.blockTable = new BlockTable();
    this.currentClass = null;
    this.type = null;
    errorCount = 0;
  }


  private void error(int lineNum,String e)
  {
    System.out.println("Error: at line " + lineNum + ": " + e);
    errorCount++;
    return;
  }

  // /////////////////////////////////////////////////////
  // expressions
  @Override
  public void visit(ast.exp.Add e)
  {
	  e.left.accept(this);
	    ast.type.T leftty = this.type;
	    e.right.accept(this);
	    if(!(this.type instanceof ast.type.Int) ||
	 		   !(leftty instanceof ast.type.Int))
	 		  error(e.lineNum,"both sides of + should be integer type");
	    this.type = new ast.type.Int();
	    return;
  }

  @Override
  public void visit(ast.exp.And e)
  {
	  e.left.accept(this);
	  ast.type.T leftty  = this.type;
	  e.right.accept(this);
	  if(!(this.type instanceof ast.type.Boolean) ||
	 		   !(leftty instanceof ast.type.Boolean))
	 		  error(e.lineNum,"both sides of && should be boolean type");
	  this.type = new ast.type.Boolean();
	  return;
  }

  @Override
  public void visit(ast.exp.ArraySelect e)
  {
	  e.array.accept(this);
	  ast.type.T arrayty = this.type;
	  e.index.accept(this);
	  ast.type.T indexty = this.type;
	  if(!(arrayty instanceof ast.type.IntArray))
      error(e.array.lineNum,"what you select is not an array");
		if(!(indexty instanceof ast.type.Int))
		  error(e.index.lineNum,"the index of array should be integer type");
//	  this.type = new ast.type.Int();
	  return;
  }

  @Override
  public void visit(ast.exp.Call e)
  {
    ast.type.T leftty;
    ast.type.Class ty = null;

    e.exp.accept(this);
    leftty = this.type;
    if (leftty instanceof ast.type.Class) {
      ty = (ast.type.Class) leftty;
      e.type = ty.id;
    } else
      error(e.exp.lineNum,"what is called is not an object");
    MethodType mty = this.classTable.getm(ty.id, e.id);
//    ArrayList array = this.classTable  
    java.util.LinkedList<ast.type.T> argsty = new java.util.LinkedList<ast.type.T>();
    java.util.LinkedList<ast.type.T> declaredArgTypes
                                            = new java.util.LinkedList<ast.type.T>();
    if (e.args == null && mty.argsType != null)
    	error(e.lineNum,"call function " + e.id + "' but args'type(s) mismatch");
    else if (e.args != null && mty.argsType == null)
    	error(e.lineNum,"call function " + e.id + "' but args'type(s) mismatch");
    else if (null != e.args && null != mty.argsType){
    	for (ast.exp.T a : e.args) {
    		a.accept(this);
    		argsty.addLast(this.type);
    	}
        for (ast.dec.T dec: mty.argsType){
            declaredArgTypes.add(((ast.dec.Dec)dec).type);
          }
//    	if (mty.argsType.size() != argsty.size())
        if (declaredArgTypes.size() != argsty.size())
    		error(e.lineNum,"call function " + e.id + "' but args'type(s) mismatch");
    	for (int i = 0; i < argsty.size(); i++) {
//    		ast.dec.Dec dec = (ast.dec.Dec) mty.argsType.get(i);
    		if (!(argsty.get(i) instanceof ast.type.Class) ||
    			 !(declaredArgTypes.get(i) instanceof ast.type.Class))
    		{
    			if (!declaredArgTypes.get(i).toString().equals(argsty.get(i).toString()))
    				error(e.lineNum,"call function '" + e.id + "' but args'type(s) mismatch");
    		}
    		else{
    			String type = declaredArgTypes.get(i).toString();
    			String argtype = argsty.get(i).toString();
    			while (argtype!=null&&!argtype.equals(type)){
    				argtype = this.classTable.get(argtype).extendss;
    			}
    			if (argtype == null)
    				error(e.lineNum,"call function '" + e.id + "' but args'type(s) mismatch");
    		}
    	}
    }

    this.type = mty.retType;
    e.at = declaredArgTypes;
    e.rt = this.type;
    e.at_real = argsty;// just for GenGo
    return;
  }

  @Override
  public void visit(ast.exp.False e)
  {
	  this.type = new ast.type.Boolean();
	  return;
  }

  @Override
  public void visit(ast.exp.Id e)
  {
    // first look up the id in block table
	  ast.type.T type = this.blockTable.get(e.id);
	// second look up the id in method table
	  if ( type == null )
		  type = this.methodTable.get(e.id);
    // if search failed, then s.id must be a class field.
    if (type == null) {
//      type = this.classTable.get(this.currentClass, e.id);
      Object [] array = this.classTable.get(this.currentClass, e.id);
      type =(ast.type.T)array[0];
      e.idBelong = (String)array[1];
      // mark this id as a field id, this fact will be
      // useful in later phase.
      e.isField = true;
    }
    if (type == null)
      error(e.lineNum, "'" + e.id + "' is not defined");
    this.type = type;
    // record this type on this node for future use.
    e.type = type;
    return;
  }

  @Override
  public void visit(ast.exp.Length e)
  {
	  ast.type.T leftty;
	  e.array.accept(this);
	  leftty = this.type;
	  if(!(leftty instanceof ast.type.IntArray))
		  error(e.lineNum,"you can only get length from array");
	  this.type = new ast.type.Int();
	  return;
  }

  @Override
  public void visit(ast.exp.Lt e)
  {
    e.left.accept(this);
    ast.type.T ty = this.type;
    e.right.accept(this);
    if (this.type == null || ty == null ||
    	 !this.type.toString().equals(ty.toString()))
      error(e.lineNum ,"both sides of < should be same type");
    this.type = new ast.type.Boolean();
    return;
  }

  @Override
  public void visit(ast.exp.NewIntArray e)
  {
	  e.exp.accept(this);
	  if (!(this.type instanceof ast.type.Int))
		  error(e.lineNum,"index of array should be integer type");
	  this.type = new ast.type.IntArray();
	  return;
  }

  @Override
  public void visit(ast.exp.NewObject e)
  {
    this.type = new ast.type.Class(e.id);
    return;
  }

  @Override
  public void visit(ast.exp.Not e)
  {
	  e.exp.accept(this);
	  if (!(this.type instanceof ast.type.Boolean))
		  error(e.lineNum,"operation ! can only be applied to boolean type");
	  this.type = new ast.type.Boolean();
  }

  @Override
  public void visit(ast.exp.Num e)
  {
    this.type = new ast.type.Int();
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
    ast.type.T leftty = this.type;
    e.right.accept(this);
    if(!(this.type instanceof ast.type.Int) ||
	 		   !(leftty instanceof ast.type.Int))
	 		  error(e.lineNum,"both sides of - should be integer type");
    this.type = new ast.type.Int();
    return;
  }

  @Override
  public void visit(ast.exp.This e)
  {
    this.type = new ast.type.Class(this.currentClass);
    return;
  }

  @Override
  public void visit(ast.exp.Times e)
  {
    e.left.accept(this);
    ast.type.T leftty = this.type;
    e.right.accept(this);
    if(!(this.type instanceof ast.type.Int) ||
	 		      !(leftty instanceof ast.type.Int))
    	error(e.lineNum,"both sides of * should be integer type");
    this.type = new ast.type.Int();
    return;
  }

  @Override
  public void visit(ast.exp.True e)
  {
	  this.type = new ast.type.Boolean();
	  return;
  }

  // statements
  @Override
  public void visit(ast.stm.Assign s)
  {
    // first look up the id in block table
    ast.type.T type = this.blockTable.get(s.id);
    // second look up the id in block table
    if (type == null)
      type = this.methodTable.get(s.id);
    // if search failed, then s.id must
    if (type == null){
        Object [] array = this.classTable.get(this.currentClass, s.id);
        type =(ast.type.T)array[0];
        s.idBelong = (String)array[1];
//      type = this.classTable.get(this.currentClass, s.id);
        s.idisField = true;
    }
    if (type == null)
      error(s.exp.lineNum,"'" + s.id + "' is not defined");
    s.exp.accept(this);
    s.type = type;
    if( this.type != null && type != null &&
    		!this.type.toString().equals(type.toString()))
    	error(s.exp.lineNum,"both sides of = is not same type");
    return;
  }

  @Override
  public void visit(ast.stm.AssignArray s)
  {
	  ast.type.T type = this.blockTable.get(s.id);
	  if (type == null)
		  type = this.blockTable.get(s.id);
	  if (type == null){
//		  type = this.classTable.get(this.currentClass, s.id);
	      Object [] array = this.classTable.get(this.currentClass, s.id);
	      type =(ast.type.T)array[0];
	      s.idBelong = (String)array[1];
		  s.idisField = true;
	  }
     if (type == null)
       error(s.exp.lineNum,"'" + s.id + "' is not defined");
	  if (!(type instanceof ast.type.IntArray))
		  error(s.exp.lineNum,"'" + s.id + "' is not an array");
	  s.index.accept(this);
	  if (!(this.type instanceof ast.type.Int))
		  error(s.exp.lineNum,"index of array should be integer type");
	  s.exp.accept(this);
	  s.type = type;
	  return;
  }

  // further extends are in consideration
  @Override
  public void visit(ast.stm.Block s)
  {
	    this.blockTable.put(s.locals);
       
	    for (ast.stm.T stm : s.stms)
	      stm.accept(this);
	    this.blockTable.unUsed();
	    this.blockTable.empty(); // out the scope,locals poped.
	    return;
		 
  }

  @Override
  public void visit(ast.stm.If s)
  {
    s.condition.accept(this);
    if (!this.type.toString().equals("@boolean"))
      error(s.condition.lineNum,"the condition of If should be boolean type");
    s.thenn.accept(this);
    s.elsee.accept(this);
    return;
  }

  @Override
  public void visit(ast.stm.Print s)
  {
    s.exp.accept(this);
    if (!this.type.toString().equals("@int"))
      error(s.exp.lineNum, "print only supports integer type");
    return;
  }

  @Override
  public void visit(ast.stm.While s)
  {
	  s.condition.accept(this);
	  if (!this.type.toString().equals("@boolean"))
		  error(s.condition.lineNum,"the condition of While should be boolean type");
	  s.body.accept(this);
	  return;
  }

  // type
  @Override
  public void visit(ast.type.Boolean t)
  {
	  //this.type = new ast.type.Boolean();
	  return;
  }

  @Override
  public void visit(ast.type.Class t)
  {
	  if (null == this.classTable.get(t.id))
		  error(t.lineNum, "Class '" + t.id + "' is not defined");
	  //this.type = new ast.type.Class(t.id);
	  return;
  }

  @Override
  public void visit(ast.type.Int t)
  {
	 //this.type = new ast.type.Int();
    return;
  }

  @Override
  public void visit(ast.type.IntArray t)
  {
	  //this.type = new ast.type.IntArray();
	  return;
  }

  // dec
  @Override
  public void visit(ast.dec.Dec d)
  {
     d.type.accept(this);
	  return;
  }

  // method
  @Override
  public void visit(ast.method.Method m)
  {
    // construct the method table
    this.methodTable.put(m.formals, m.locals);
    if (control.Control.elabMethodTable){
    	System.out.println("_______________________________");
    	System.out.println("        "+currentClass+"."+m.id);
      this.methodTable.dump();
    }
    for (ast.stm.T s : m.stms)
      s.accept(this);
    m.retExp.accept(this);
    this.methodTable.unUsed();
    this.methodTable.empty(); // out the scope,locals and formals poped.
    return;
  }

  // class
  @Override
  public void visit(ast.classs.Class c)
  {
    this.currentClass = c.id;

    for (ast.method.T m : c.methods) {
      m.accept(this);
    }
    return;
  }

  // main class
  @Override
  public void visit(ast.mainClass.MainClass mc)
  {
    this.currentClass = mc.id;
    // "main" has an argument "arg" of type "String[]", but
    // one has no chance to use it. So it's safe to skip it...
    mc.block.accept(this);
    return;
  }

  // ////////////////////////////////////////////////////////
  // step 1: build class table
  // class table for Main class
  private void buildMainClass(ast.mainClass.MainClass main)
  {
    this.classTable.put(main.id, new ClassBinding(null));
  }

  // class table for normal classes
  private void buildClass(ast.classs.Class c)
  {
    this.classTable.put(c.id, new ClassBinding(c.extendss));
    for (ast.dec.T dec : c.decs) {
      ast.dec.Dec d = (ast.dec.Dec) dec;
      this.classTable.put(c.id, d.id, d.type);
    }
    for (ast.method.T method : c.methods) {
      ast.method.Method m = (ast.method.Method) method;
      this.classTable.put(c.id, m.id, new MethodType(m.retType, m.formals));
    }
  }

  // step 1: end
  // ///////////////////////////////////////////////////

  // program
  @Override
  public void visit(ast.program.Program p)
  {
    // ////////////////////////////////////////////////
    // step 1: build a symbol table for class (the class table)
    // a class table is a mapping from class names to class bindings
    // classTable: className -> ClassBinding{extends, fields, methods}
    buildMainClass((ast.mainClass.MainClass) p.mainClass);
    for (ast.classs.T c : p.classes) {
      buildClass((ast.classs.Class) c);
    }

    // we can double check that the class table is OK!
    if (control.Control.elabClassTable) {
      this.classTable.dump();
    }

    // ////////////////////////////////////////////////
    // step 2: elaborate each class in turn, under the class table
    // built above.
    p.mainClass.accept(this);
    for (ast.classs.T c : p.classes) {
      c.accept(this);
       ast.classs.Class cc = (ast.classs.Class)c;
       this.classTable.get(cc.id).unUsed();
    }
   if(errorCount > 0){
	   System.out.println("There are " + errorCount + " error(s) to fix before codegen phase.");
	   System.exit(0);
   }

  }
}
