package main
import "fmt"
func main(){

      fmt.Println(new (LL).Start())
}

type Element struct {
      Age int
      Salary int
      Married bool
}

func (this *Element) Init(v_Age int, v_Salary int, v_Married bool) bool {

    this.Age = v_Age
    this.Salary = v_Salary
    this.Married = v_Married
    return true
}
func (this *Element) GetAge() int {

    return this.Age
}
func (this *Element) GetSalary() int {

    return this.Salary
}
func (this *Element) GetMarried() bool {

    return this.Married
}
func (this *Element) Equal(other *Element) bool {
    var ret_val bool
    var aux01 int
    var aux02 int
    var nt int

    ret_val = true
    aux01 = other.GetAge()
    if !this.Compare(aux01, this.Age) {
      ret_val = false
    } else {

      aux02 = other.GetSalary()
      if !this.Compare(aux02, this.Salary) {
        ret_val = false
      } else {
        if this.Married {
          if !other.GetMarried() {
            ret_val = false
          } else {
            nt = 0
          }
        } else {
          if other.GetMarried() {
            ret_val = false
          } else {
            nt = 0
          }
        }
      }
    }
    ret_val = ret_val
    nt = nt
    return ret_val
}
func (this *Element) Compare(num1 int, num2 int) bool {
    var retval bool
    var aux02 int

    retval = false
    aux02 = num2 + 1
    if num1 < num2 {
      retval = false
    } else {
      if !( num1 < aux02 ) {
        retval = false
      } else {
        retval = true
      }
    }
    retval = retval
    return retval
}
type List struct {
      elem *Element
      next *List
      end bool
}

func (this *List) Init() bool {

    this.end = true
    return true
}
func (this *List) InitNew(v_elem *Element, v_next *List, v_end bool) bool {

    this.end = v_end
    this.elem = v_elem
    this.next = v_next
    return true
}
func (this *List) Insert(new_elem *Element) *List {
    var ret_val bool
    var aux03 *List
    var aux02 *List

    aux03 = this
    aux02 = new (List)
    ret_val = aux02.InitNew(new_elem, aux03, false)
    ret_val = ret_val
    return aux02
}
func (this *List) SetNext(v_next *List) bool {

    this.next = v_next
    return true
}
func (this *List) Delete(e *Element) *List {
    var my_head *List
    var ret_val bool
    var aux05 bool
    var aux01 *List
    var prev *List
    var var_end bool
    var var_elem *Element
    var aux04 int
    var nt int

    my_head = this
    ret_val = false
    aux04 = 0 - 1
    aux01 = this
    prev = this
    var_end = this.end
    var_elem = this.elem
    for ( !var_end ) && ( !ret_val ) {

      if e.Equal(var_elem) {

        ret_val = true
        if aux04 < 0 {

          my_head = aux01.GetNext()
        } else {

          fmt.Println(0 - 555)
          aux05 = prev.SetNext(aux01.GetNext())
          fmt.Println(0 - 555)
        }
      } else {
        nt = 0
      }
      if !ret_val {

        prev = aux01
        aux01 = aux01.GetNext()
        var_end = aux01.GetEnd()
        var_elem = aux01.GetElem()
        aux04 = 1
      } else {
        nt = 0
      }
    }
    my_head = my_head
    aux05 = aux05
    nt = nt
    return my_head
}
func (this *List) Search(e *Element) int {
    var int_ret_val int
    var aux01 *List
    var var_elem *Element
    var var_end bool
    var nt int

    int_ret_val = 0
    aux01 = this
    var_end = this.end
    var_elem = this.elem
    for !var_end {

      if e.Equal(var_elem) {

        int_ret_val = 1
      } else {
        nt = 0
      }
      aux01 = aux01.GetNext()
      var_end = aux01.GetEnd()
      var_elem = aux01.GetElem()
    }
    int_ret_val = int_ret_val
    nt = nt
    return int_ret_val
}
func (this *List) GetEnd() bool {

    return this.end
}
func (this *List) GetElem() *Element {

    return this.elem
}
func (this *List) GetNext() *List {

    return this.next
}
func (this *List) Print() bool {
    var aux01 *List
    var var_end bool
    var var_elem *Element

    aux01 = this
    var_end = this.end
    var_elem = this.elem
    for !var_end {

      fmt.Println(var_elem.GetAge())
      aux01 = aux01.GetNext()
      var_end = aux01.GetEnd()
      var_elem = aux01.GetElem()
    }
    return true
}
type LL struct {
}

func (this *LL) Start() int {
    var head *List
    var last_elem *List
    var aux01 bool
    var el01 *Element
    var el02 *Element
    var el03 *Element

    last_elem = new (List)
    aux01 = last_elem.Init()
    head = last_elem
    aux01 = head.Init()
    aux01 = head.Print()
    el01 = new (Element)
    aux01 = el01.Init(25, 37000, false)
    head = head.Insert(el01)
    aux01 = head.Print()
    fmt.Println(10000000)
    el01 = new (Element)
    aux01 = el01.Init(39, 42000, true)
    el02 = el01
    head = head.Insert(el01)
    aux01 = head.Print()
    fmt.Println(10000000)
    el01 = new (Element)
    aux01 = el01.Init(22, 34000, false)
    head = head.Insert(el01)
    aux01 = head.Print()
    el03 = new (Element)
    aux01 = el03.Init(27, 34000, false)
    fmt.Println(head.Search(el02))
    fmt.Println(head.Search(el03))
    fmt.Println(10000000)
    el01 = new (Element)
    aux01 = el01.Init(28, 35000, false)
    head = head.Insert(el01)
    aux01 = head.Print()
    fmt.Println(2220000)
    head = head.Delete(el02)
    aux01 = head.Print()
    fmt.Println(33300000)
    head = head.Delete(el01)
    aux01 = head.Print()
    fmt.Println(44440000)
    aux01 = aux01
    return 0
}



