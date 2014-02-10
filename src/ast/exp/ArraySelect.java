package ast.exp;

public class ArraySelect extends T
{
  public T array;
  public T index;

  public ArraySelect(T array, T index)
  {
    this.array = array;
    this.index = index;
    this.lineNum = array.lineNum;
  }

  @Override
  public void accept(ast.Visitor v)
  {
    v.visit(this);
    return;
  }
}
