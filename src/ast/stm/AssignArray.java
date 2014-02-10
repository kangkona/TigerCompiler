package ast.stm;

public class AssignArray extends T
{
  public String id;
  public boolean idisField;
  public ast.exp.T index;
  public ast.exp.T exp;
  public ast.type.T type;
  public String idBelong; // id belong which class?

  public AssignArray(String id, ast.exp.T index, ast.exp.T exp)
  {
    this.id = id;
    this.index = index;
    this.exp = exp;
    this.type = null;
    this.idisField = false;
    this.idBelong = null;
  }

  public AssignArray(String id, ast.exp.T index, ast.exp.T exp,boolean idisField)
  {
    this.id = id;
    this.index = index;
    this.exp = exp;
    this.type = null;
    this.idisField = idisField;
    this.idBelong = null;
  }

  @Override
  public void accept(ast.Visitor v)
  {
    v.visit(this);
  }
}
