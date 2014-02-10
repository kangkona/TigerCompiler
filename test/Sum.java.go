package main
import "fmt"
func main(){

      fmt.Println(new (Doit).doit(101))
}

type DoitI interface {
      doit(n int) int
}

type Doit struct {
}

func (this *Doit) doit(n int) int {
    var sum int
    var i int

    i = 0
    sum = 0
    for i < n {

      sum = sum + i
      i = i + 1
    }
    return sum
}



