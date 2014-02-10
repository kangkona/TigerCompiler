package main
import "fmt"
func main(){

      fmt.Println(new (BS).Start(20))
}

type BSI interface {
      Start(sz int) int
      Search(num int) bool
      Div(num int) int
      Compare(num1 int, num2 int) bool
      Print() int
      Init(sz int) int
}

type BS struct {
      number []int
      size int
}

func (this *BS) Start(sz int) int {
    var aux01 int
    var aux02 int

    aux01 = this.Init(sz)
    aux02 = this.Print()
    if this.Search(8) {
      fmt.Println(1)
    } else {
      fmt.Println(0)
    }
    if this.Search(19) {
      fmt.Println(1)
    } else {
      fmt.Println(0)
    }
    if this.Search(20) {
      fmt.Println(1)
    } else {
      fmt.Println(0)
    }
    if this.Search(21) {
      fmt.Println(1)
    } else {
      fmt.Println(0)
    }
    if this.Search(37) {
      fmt.Println(1)
    } else {
      fmt.Println(0)
    }
    if this.Search(38) {
      fmt.Println(1)
    } else {
      fmt.Println(0)
    }
    if this.Search(39) {
      fmt.Println(1)
    } else {
      fmt.Println(0)
    }
    if this.Search(50) {
      fmt.Println(1)
    } else {
      fmt.Println(0)
    }
    aux01 = aux01
    aux02 = aux02
    return 999
}
func (this *BS) Search(num int) bool {
    var bs01 bool
    var right int
    var left int
    var var_cont bool
    var medium int
    var aux01 int
    var nt int

    aux01 = 0
    bs01 = false
    right = len(this.number)
    right = right - 1
    left = 0
    var_cont = true
    for var_cont {

      medium = left + right
      medium = this.Div(medium)
      aux01 = this.number[medium]
      if num < aux01 {
        right = medium - 1
      } else {
        left = medium + 1
      }
      if this.Compare(aux01, num) {
        var_cont = false
      } else {
        var_cont = true
      }
      if right < left {
        var_cont = false
      } else {
        nt = 0
      }
    }
    if this.Compare(aux01, num) {
      bs01 = true
    } else {
      bs01 = false
    }
    bs01 = bs01
    nt = nt
    return bs01
}
func (this *BS) Div(num int) int {
    var count01 int
    var count02 int
    var aux03 int

    count01 = 0
    count02 = 0
    aux03 = num - 1
    for count02 < aux03 {

      count01 = count01 + 1
      count02 = count02 + 2
    }
    return count01
}
func (this *BS) Compare(num1 int, num2 int) bool {
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
func (this *BS) Print() int {
    var j int

    j = 1
    for j < ( this.size ) {

      fmt.Println(this.number[j])
      j = j + 1
    }
    fmt.Println(99999)
    return 0
}
func (this *BS) Init(sz int) int {
    var j int
    var k int
    var aux02 int
    var aux01 int

    this.size = sz
    this.number = make([]int, sz)
    j = 1
    k = this.size + 1
    for j < ( this.size ) {

      aux01 = 2 * j
      aux02 = k - 3
      this.number[j] = aux01 + aux02
      j = j + 1
      k = k - 1
    }
    return 0
}



