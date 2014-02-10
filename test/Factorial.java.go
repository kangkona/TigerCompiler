package main
import "fmt"
func main(){

      fmt.Println(new (Fac).ComputeFac(10))
}

type Fac struct {
}

func (this *Fac) ComputeFac(num int) int {
    var num_aux int

    if num < 1 {
      num_aux = 1
    } else {
      num_aux = num * ( this.ComputeFac(num - 1) )
    }
    return num_aux
}



