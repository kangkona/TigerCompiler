package codegen.bytecode;

public class PrettyPrintVisitor implements Visitor
{
  private java.io.BufferedWriter writer;

  public PrettyPrintVisitor()
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

  private void isayln(String s)
  {
    say("    ");
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
  // statements
  @Override
  public void visit(codegen.bytecode.stm.Aload s)
  {
    this.isayln("aload " + s.index);
    return;
  }

  @Override
  public void visit(codegen.bytecode.stm.Areturn s)
  {
    this.isayln("areturn");
    return;
  }
  
  @Override
  public void visit(codegen.bytecode.stm.Arraylength s)
  {
    this.isayln("arraylength");
    return;
  }

  @Override
  public void visit(codegen.bytecode.stm.Astore s)
  {
    this.isayln("astore " + s.index);
    return;
  }

  @Override
  public void visit(codegen.bytecode.stm.Goto s)
  {
    this.isayln("goto " + s.l.toString());
    return;
  }
  
  @Override
  public void visit(codegen.bytecode.stm.Getfield s)
  {
	this.isayln("getfield " + s.field_spec + " " + s.descriptor);  
  }
  
  @Override
  public void visit(codegen.bytecode.stm.Iastore s)
  {
	this.isayln("iastore");  
  }
  
  @Override
  public void visit(codegen.bytecode.stm.Iaload s)
  {
	this.isayln("iaload");  
  }

  @Override
  public void visit(codegen.bytecode.stm.Ificmplt s)
  {
    this.isayln("if_icmplt " + s.l.toString());
    return;
  }

  @Override
  public void visit(codegen.bytecode.stm.Ifne s)
  {
    this.isayln("ifne " + s.l.toString());
    return;
  }
  
  @Override
  public void visit(codegen.bytecode.stm.Iadd s)
  {
	 this.isayln("iadd");
	 return;
  }
  
  @Override
  public void visit(codegen.bytecode.stm.Iand s)
  {
	 this.isayln("iand");
	 return;
  }

  @Override
  public void visit(codegen.bytecode.stm.Iload s)
  {
    this.isayln("iload " + s.index);
    return;
  }

  @Override
  public void visit(codegen.bytecode.stm.Imul s)
  {
    this.isayln("imul");
    return;
  }

  @Override
  public void visit(codegen.bytecode.stm.Invokevirtual s)
  {
    this.say("    invokevirtual " + s.c + "/" + s.f + "(");
    for (codegen.bytecode.type.T t : s.at) {
      t.accept(this);
//      this.say(";");
    }
    this.say(")");
    s.rt.accept(this);
    this.sayln("");
    return;
  }

  @Override
  public void visit(codegen.bytecode.stm.Ireturn s)
  {
    this.isayln("ireturn");
    return;
  }

  @Override
  public void visit(codegen.bytecode.stm.Istore s)
  {
    this.isayln("istore " + s.index);
    return;
  }

  @Override
  public void visit(codegen.bytecode.stm.Isub s)
  {
    this.isayln("isub");
    return;
  }

  @Override
  public void visit(codegen.bytecode.stm.Label s)
  {
    this.sayln(s.l.toString() + ":");
    return;
  }

  @Override
  public void visit(codegen.bytecode.stm.Ldc s)
  {
    this.isayln("ldc " + s.i);
    return;
  }

  @Override
  public void visit(codegen.bytecode.stm.New s)
  {
    this.isayln("new " + s.c);
    this.isayln("dup");
    this.isayln("invokespecial " + s.c + "/<init>()V");
    return;
  }
  
  @Override
  public void visit(codegen.bytecode.stm.Newarray s)
  {
    this.isayln("newarray int");
    return;
  }
  

  @Override
  public void visit(codegen.bytecode.stm.Print s)
  {
    this.isayln("getstatic java/lang/System/out Ljava/io/PrintStream;");
    this.isayln("swap");
    this.isayln("invokevirtual java/io/PrintStream/println(I)V");
    return;
  }
  
  @Override
  public void visit(codegen.bytecode.stm.Putfield s)
  {
	this.isayln("putfield " + s.field_spec + " " + s.descriptor);  
  }

  // type
  @Override
  public void visit(codegen.bytecode.type.Class t)
  {
    this.say("L" + t.id + ";");
  }

  @Override
  public void visit(codegen.bytecode.type.Int t)
  {
    this.say("I");
  }

  @Override
  public void visit(codegen.bytecode.type.IntArray t)
  {
    this.say("[I");
  }

  // dec
  @Override
  public void visit(codegen.bytecode.dec.Dec d)
  {
  }

  // method
  @Override
  public void visit(codegen.bytecode.method.Method m)
  {
    this.say(".method public " + m.id + "(");
    for (codegen.bytecode.dec.T d : m.formals) {
      codegen.bytecode.dec.Dec dd = (codegen.bytecode.dec.Dec) d;
      dd.type.accept(this);
//      this.say(";");
    }
    this.say(")");
    m.retType.accept(this);
    this.sayln("");
    this.sayln(".limit stack 4096");
    this.sayln(".limit locals " + (m.index + 1));

    for (codegen.bytecode.stm.T s : m.stms)
      s.accept(this);

    this.sayln(".end method");
    return;
  }

  // class
  @Override
  public void visit(codegen.bytecode.classs.Class c)
  {
    // Every class must go into its own class file.
    try {
      this.writer = new java.io.BufferedWriter(new java.io.OutputStreamWriter(
          new java.io.FileOutputStream(c.id + ".j")));
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }

    // header
    this.sayln("; This is automatically generated by the Tiger compiler.");
    this.sayln("; Do NOT modify!\n");

    this.sayln(".class public " + c.id);
    if (c.extendss == null)
      this.sayln(".super java/lang/Object\n");
    else
      this.sayln(".super " + c.extendss);

    // fields
    for (codegen.bytecode.dec.T d : c.decs) {
      codegen.bytecode.dec.Dec dd = (codegen.bytecode.dec.Dec) d;
      this.say(".field public " + dd.id + " ");
      dd.type.accept(this);
      this.sayln("");
    }

    // methods
    this.sayln(".method public <init>()V");
    this.isayln("aload 0");
    if (c.extendss == null)
      this.isayln("invokespecial java/lang/Object/<init>()V");
    else
      this.isayln("invokespecial " + c.extendss + "/<init>()V");
    this.isayln("return");
    this.sayln(".end method\n\n");

    for (codegen.bytecode.method.T m : c.methods) {
      m.accept(this);
    }

    try {
      this.writer.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    return;
  }

  // main class
  @Override
  public void visit(codegen.bytecode.mainClass.MainClass c)
  {
    // Every class must go into its own class file.
    try {
      this.writer = new java.io.BufferedWriter(new java.io.OutputStreamWriter(
          new java.io.FileOutputStream(c.id + ".j")));
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }

    this.sayln("; This is automatically generated by the Tiger compiler.");
    this.sayln("; Do NOT modify!\n");

    this.sayln(".class public " + c.id);
    this.sayln(".super java/lang/Object\n");
    this.sayln(".method public static main([Ljava/lang/String;)V");
    this.isayln(".limit stack 4096");
    this.isayln(".limit locals 2");
    for (codegen.bytecode.stm.T s : c.stms)
      s.accept(this);
    this.isayln("return");
    this.sayln(".end method");

    try {
      this.writer.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    return;
  }

  // program
  @Override
  public void visit(codegen.bytecode.program.Program p)
  {

    p.mainClass.accept(this);

    for (codegen.bytecode.classs.T c : p.classes) {
      c.accept(this);
    }

  }

}
