package main
import "fmt"
func main(){

      fmt.Println(new (BT).Start())
}

type BTI interface {
      Start() int
}

type BT struct {
}

func (this *BT) Start() int {
    var root *Tree
    var ntb bool

    root = new (Tree)
    ntb = root.Init(16)
    ntb = root.Print()
    fmt.Println(100000000)
    ntb = root.Insert(8)
    ntb = root.Print()
    ntb = root.Insert(24)
    ntb = root.Insert(4)
    ntb = root.Insert(12)
    ntb = root.Insert(20)
    ntb = root.Insert(28)
    ntb = root.Insert(14)
    ntb = root.Print()
    fmt.Println(root.Search(24))
    fmt.Println(root.Search(12))
    fmt.Println(root.Search(16))
    fmt.Println(root.Search(50))
    fmt.Println(root.Search(12))
    ntb = root.Delete(12)
    ntb = root.Print()
    fmt.Println(root.Search(12))
    ntb = ntb
    return 0
}
type TreeI interface {
      Init(v_key int) bool
      SetRight(rn *Tree) bool
      SetLeft(ln *Tree) bool
      GetRight() *Tree
      GetLeft() *Tree
      GetKey() int
      SetKey(v_key int) bool
      GetHas_Right() bool
      GetHas_Left() bool
      SetHas_Left(val bool) bool
      SetHas_Right(val bool) bool
      Compare(num1 int, num2 int) bool
      Insert(v_key int) bool
      Delete(v_key int) bool
      Remove(p_node *Tree, c_node *Tree) bool
      RemoveRight(p_node *Tree, c_node *Tree) bool
      RemoveLeft(p_node *Tree, c_node *Tree) bool
      Search(v_key int) int
      Print() bool
      RecPrint(node *Tree) bool
}

type Tree struct {
      left *Tree
      right *Tree
      key int
      has_left bool
      has_right bool
      my_null *Tree
}

func (this *Tree) Init(v_key int) bool {

    this.key = v_key
    this.has_left = false
    this.has_right = false
    return true
}
func (this *Tree) SetRight(rn *Tree) bool {

    this.right = rn
    return true
}
func (this *Tree) SetLeft(ln *Tree) bool {

    this.left = ln
    return true
}
func (this *Tree) GetRight() *Tree {

    return this.right
}
func (this *Tree) GetLeft() *Tree {

    return this.left
}
func (this *Tree) GetKey() int {

    return this.key
}
func (this *Tree) SetKey(v_key int) bool {

    this.key = v_key
    return true
}
func (this *Tree) GetHas_Right() bool {

    return this.has_right
}
func (this *Tree) GetHas_Left() bool {

    return this.has_left
}
func (this *Tree) SetHas_Left(val bool) bool {

    this.has_left = val
    return true
}
func (this *Tree) SetHas_Right(val bool) bool {

    this.has_right = val
    return true
}
func (this *Tree) Compare(num1 int, num2 int) bool {
    var ntb bool
    var nti int

    ntb = false
    nti = num2 + 1
    if num1 < num2 {
      ntb = false
    } else {
      if !( num1 < nti ) {
        ntb = false
      } else {
        ntb = true
      }
    }
    ntb = ntb
    return ntb
}
func (this *Tree) Insert(v_key int) bool {
    var new_node *Tree
    var ntb bool
    var cont bool
    var key_aux int
    var current_node *Tree

    new_node = new (Tree)
    ntb = new_node.Init(v_key)
    current_node = this
    cont = true
    for cont {

      key_aux = current_node.GetKey()
      if v_key < key_aux {

        if current_node.GetHas_Left() {
          current_node = current_node.GetLeft()
        } else {

          cont = false
          ntb = current_node.SetHas_Left(true)
          ntb = current_node.SetLeft(new_node)
        }
      } else {

        if current_node.GetHas_Right() {
          current_node = current_node.GetRight()
        } else {

          cont = false
          ntb = current_node.SetHas_Right(true)
          ntb = current_node.SetRight(new_node)
        }
      }
    }
    ntb = ntb
    return true
}
func (this *Tree) Delete(v_key int) bool {
    var current_node *Tree
    var parent_node *Tree
    var cont bool
    var found bool
    var is_root bool
    var key_aux int
    var ntb bool

    current_node = this
    parent_node = this
    cont = true
    found = false
    is_root = true
    for cont {

      key_aux = current_node.GetKey()
      if v_key < key_aux {
        if current_node.GetHas_Left() {

          parent_node = current_node
          current_node = current_node.GetLeft()
        } else {
          cont = false
        }
      } else {
        if key_aux < v_key {
          if current_node.GetHas_Right() {

            parent_node = current_node
            current_node = current_node.GetRight()
          } else {
            cont = false
          }
        } else {

          if is_root {
            if ( !current_node.GetHas_Right() ) && ( !current_node.GetHas_Left() ) {
              ntb = true
            } else {
              ntb = this.Remove(parent_node, current_node)
            }
          } else {
            ntb = this.Remove(parent_node, current_node)
          }
          found = true
          cont = false
        }
      }
      is_root = false
    }
    found = found
    ntb = ntb
    return found
}
func (this *Tree) Remove(p_node *Tree, c_node *Tree) bool {
    var ntb bool
    var auxkey1 int
    var auxkey2 int

    if c_node.GetHas_Left() {
      ntb = this.RemoveLeft(p_node, c_node)
    } else {
      if c_node.GetHas_Right() {
        ntb = this.RemoveRight(p_node, c_node)
      } else {

        auxkey1 = c_node.GetKey()
        auxkey2 = ( p_node.GetLeft() ).GetKey()
        if this.Compare(auxkey1, auxkey2) {

          ntb = p_node.SetLeft(this.my_null)
          ntb = p_node.SetHas_Left(false)
        } else {

          ntb = p_node.SetRight(this.my_null)
          ntb = p_node.SetHas_Right(false)
        }
      }
    }
    ntb = ntb
    return true
}
func (this *Tree) RemoveRight(p_node *Tree, c_node *Tree) bool {
    var ntb bool

    for c_node.GetHas_Right() {

      ntb = c_node.SetKey(( c_node.GetRight() ).GetKey())
      p_node = c_node
      c_node = c_node.GetRight()
    }
    ntb = p_node.SetRight(this.my_null)
    ntb = p_node.SetHas_Right(false)
    ntb = ntb
    return true
}
func (this *Tree) RemoveLeft(p_node *Tree, c_node *Tree) bool {
    var ntb bool

    for c_node.GetHas_Left() {

      ntb = c_node.SetKey(( c_node.GetLeft() ).GetKey())
      p_node = c_node
      c_node = c_node.GetLeft()
    }
    ntb = p_node.SetLeft(this.my_null)
    ntb = p_node.SetHas_Left(false)
    ntb = ntb
    return true
}
func (this *Tree) Search(v_key int) int {
    var cont bool
    var ifound int
    var current_node *Tree
    var key_aux int

    current_node = this
    cont = true
    ifound = 0
    for cont {

      key_aux = current_node.GetKey()
      if v_key < key_aux {
        if current_node.GetHas_Left() {
          current_node = current_node.GetLeft()
        } else {
          cont = false
        }
      } else {
        if key_aux < v_key {
          if current_node.GetHas_Right() {
            current_node = current_node.GetRight()
          } else {
            cont = false
          }
        } else {

          ifound = 1
          cont = false
        }
      }
    }
    ifound = ifound
    return ifound
}
func (this *Tree) Print() bool {
    var current_node *Tree
    var ntb bool

    current_node = this
    ntb = this.RecPrint(current_node)
    ntb = ntb
    return true
}
func (this *Tree) RecPrint(node *Tree) bool {
    var ntb bool

    if node.GetHas_Left() {

      ntb = this.RecPrint(node.GetLeft())
    } else {
      ntb = true
    }
    fmt.Println(node.GetKey())
    if node.GetHas_Right() {

      ntb = this.RecPrint(node.GetRight())
    } else {
      ntb = true
    }
    ntb = ntb
    return true
}



