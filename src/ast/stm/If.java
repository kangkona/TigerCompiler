package ast.stm;

public class If extends T
{
  public ast.exp.T condition;
  public ast.DecOrStm thenn;
  public ast.DecOrStm elsee;

  public If(ast.exp.T condition, ast.DecOrStm thenn, ast.DecOrStm elsee)
  {
    this.condition = condition;
    this.thenn = thenn;
    this.elsee = elsee;
  }

  @Override
  public void accept(ast.Visitor v)
  {
    v.visit(this);
  }
}
