package ast.stm;

public class Assign extends T
{
  public String id;
  public boolean idisField;
  public ast.exp.T exp;
  public ast.type.T type; // type of the id
  public String idBelong; // id belong which class?

  public Assign(String id, ast.exp.T exp)
  {
    this.id = id;
    this.exp = exp;
    this.type = null;
    this.idisField = false;
    this.idBelong = null;
  }

  public Assign(String id, ast.exp.T exp,boolean idisField)
  {
    this.id = id;
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
