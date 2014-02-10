package elaborator;


public class ClassTable
{
  // map each class name (a string), to the class bindings.
  private java.util.Hashtable<String, ClassBinding> table;

  public ClassTable()
  {
    this.table = new java.util.Hashtable<String, ClassBinding>();
  }

  // Duplication is not allowed
  public void put(String c, ClassBinding cb)
  {
    if (this.table.get(c) != null) {
      System.out.println("Error: duplicated class: " + c);
      ElaboratorVisitor.errorCount++;  
    }
    else
    	this.table.put(c, cb);
  }

  // put a field into this table
  // Duplication is not allowed
  public void put(String c, String id, ast.type.T type)
  {
    ClassBinding cb = this.table.get(c);
    cb.put(id, type);
    return;
  }

  // put a method into this table
  // Duplication is not allowed.
  // Also note that MiniJava does NOT allow overloading.
  public void put(String c, String id, MethodType type)
  {
    ClassBinding cb = this.table.get(c);
    cb.put(id, type);
    return;
  }

  // return null for non-existing class
  public ClassBinding get(String className)
  {
    return this.table.get(className);
  }

  // get type of some field
  // return null for non-existing field.
  /*
  public ast.type.T get(String className, String xid)
  {
    ClassBinding cb = this.table.get(className);
    ast.type.T type = cb.get(xid);
    while (type == null) { // search all parent classes until found or fail
      if (cb.extendss == null)
        return type;

      cb = this.table.get(cb.extendss);
      type = cb.get(xid);
    }
    return type;
  }
  */
  public Object[] get(String className, String xid)
  {
	Object [] array = new Object[2];
    ClassBinding cb = this.table.get(className);
    ast.type.T type = cb.get(xid);
    array[0] = type;
    array[1] = className;
    while (type == null) { // search all parent classes until found or fail
      if (cb.extendss == null){
          array[0] = null;
          array[1] = null;
    	  return array;
      }
      array[1] = cb.extendss;
      cb = this.table.get(cb.extendss);
      type = cb.get(xid);
      array[0] = type;
    }
    return array;
  }

  // get type of some method
  // return null for non-existing method
  public MethodType getm(String className, String mid)
  {
    ClassBinding cb = this.table.get(className);
    MethodType type = cb.methods.get(mid);
    while (type == null) { // search all parent classes until found or fail
      if (cb.extendss == null)
        return type;

      cb = this.table.get(cb.extendss);
      type = cb.methods.get(mid);
    }
    return type;
  }

  public void dump()
  {
    //new Todo();
	  java.util.Set<String> classNames = this.table.keySet();
	  for (String className : classNames){
       System.out.println("_______________________________");
		  System.out.println("            " + className);
		  ClassBinding cb = this.table.get(className);
		  cb.toString();
       System.out.println();
	  }
  }

  @Override
  public String toString()
  {
    return this.table.toString();
  }
}

