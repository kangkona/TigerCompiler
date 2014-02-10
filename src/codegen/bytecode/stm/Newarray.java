package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class Newarray extends T
{
  public Newarray()
  {
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
