package ast.exp;

public class Id extends T
{
  public String id; // name of the id
  public ast.type.T type; // type of the id
  public boolean isField; // whether or not this is a class field
  public String idBelong; // id belong which class?

  public Id(String id)
  {
    this.id = id;
    this.type = null;
    this.isField = false;
    this.idBelong = null;
  }
  public Id(String id,int lineNum)
  {
    this.id = id;
    this.type = null;
    this.isField = false;
    this.idBelong = null;
    this.lineNum = lineNum;
  }

  public Id(String id, ast.type.T type, boolean isField)
  {
    this.id = id;
    this.type = type;
    this.isField = isField;
  }

  @Override
  public void accept(ast.Visitor v)
  {
    v.visit(this);
    return;
  }
}
