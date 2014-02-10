package ast.mainClass;

import ast.Visitor;

public class MainClass extends T
{
  public String id;
  public String arg;
  //public ast.stm.T stm;
//  public java.util.LinkedList<ast.DecOrStm> doss;
  public ast.stm.Block block;

  public MainClass(String id, String arg, ast.stm.Block block)
  {
    this.id = id;
    this.arg = arg;
    this.block = block;
  }
  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
    return;
  }

}
