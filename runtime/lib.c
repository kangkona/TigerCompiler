#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int len(char* da)
{
    return *((int*)da + 2);
}
int System_out_println (int i)
{
  printf ("%d\n", i);
  return 0;
}
