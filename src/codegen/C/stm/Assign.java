package codegen.C.stm;

import codegen.C.Visitor;

public class Assign extends T
{
  public String id;
  public boolean idisField;
  public codegen.C.exp.T exp;

  public Assign(String id, codegen.C.exp.T exp,boolean idisField)
  {
    this.id = id;
    this.exp = exp;
    this.idisField = idisField;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
