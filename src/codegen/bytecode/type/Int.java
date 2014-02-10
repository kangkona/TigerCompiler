package codegen.bytecode.type;

import codegen.bytecode.Visitor;

public class Int extends T
{
  public Int()
  {
  }

  @Override
  public String toString()
  {
    return "I";
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
