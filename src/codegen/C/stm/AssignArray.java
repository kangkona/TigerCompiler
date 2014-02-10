package codegen.C.stm;

import codegen.C.Visitor;

public class AssignArray extends T
{
  public String id;
  public boolean idisField;
  public codegen.C.exp.T index;
  public codegen.C.exp.T exp;

  public AssignArray(String id, codegen.C.exp.T index, codegen.C.exp.T exp,boolean idisField)
  {
    this.id = id;
    this.index = index;
    this.exp = exp;
    this.idisField = idisField;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
