#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
// define-macro some helper  tools
#define  Header sizeof(int)*4
#define  FreeSpace heap.from + heap.size - heap.fromFree
#define  needBytes(length)  Header + sizeof(int)*length
#define  durTime(start,end)  (float)(1000000*(end.tv_sec-start.tv_sec)+(end.tv_usec-start.tv_usec))/1000
#define notEnough(need)   need > logSize ? 1:0
// The Gimple Garbage Collector.
static void Tiger_gc();
void Tiger_logGc();
void  addToLog(char * buffer);
static int round=0;
static int logSize = 1024;
char *log_gc;
//===============================================================//
// The Java Heap data structure.

/*   
      ----------------------------------------------------
      |                        |                         |
      ----------------------------------------------------
      ^\                      /^
      | \<~~~~~~~ size ~~~~~>/ |
    from                       to
 */
struct JavaHeap
{
  int size;         // in bytes, note that this if for semi-heap size
  char *from;       // the "from" space pointer
  char *fromFree;   // the next "free" space in the from space
  char *to;         // the "to" space pointer
  char *toStart;    // "start" address in the "to" space
  char *toNext;     // "next" free space pointer in the to space
};

// The Java heap, which is initialized by the following
// "heap_init" function.
struct JavaHeap heap;

// Lab 4, exercise 10:
// Given the heap size (in bytes), allocate a Java heap
// in the C heap, initialize the relevant fields.
void Tiger_heap_init (int heapSize)
{
  // You should write 7 statement here:
  // #1: allocate a chunk of memory of size "heapSize" using "malloc"
  char *whole = (char *)malloc (heapSize);
  // #2: initialize the "size" field, note that "size" field
  // is for semi-heap, but "heapSize" is for the whole heap.
  heap.size  = heapSize/2;
  memset(whole,0,heapSize);
  // #3: initialize the "from" field (with what value?)
  heap.from = whole;
  // #4: initialize the "fromFree" field (with what value?)
  heap.fromFree = heap.from;
  // #5: initialize the "to" field (with what value?)
  heap.to = heap.from + heap.size;
  // #6: initizlize the "toStart" field with NULL;
  heap.toStart = NULL;
  // #7: initialize the "toNext" field with NULL;
  heap.toNext = NULL;
  return;
}

// The "prev" pointer, pointing to the top frame on the GC stack. 
// (see part A of Lab 4)
void *prev = 0;



//===============================================================//
// Object Model And allocation


// Lab 4: exercise 11:
// "new" a new object, do necessary initializations, and
// return the pointer (reference).
/*    ----------------
      | vptr      ---|----> (points to the virtual method table)
      |--------------|
      | isObjOrArray | (0: for normal objects)
      |--------------|
      | length       | (this field should be empty for normal objects)
      |--------------|
      | forwarding   | 
      |--------------|\
p---->| v_0          | \      
      |--------------|  s
      | ...          |  i
      |--------------|  z
      | v_{size-1}   | /e
      ----------------/
*/
// Try to allocate an object in the "from" space of the Java
// heap. Read Tiger book chapter 13.3 for details on the
// allocation.
// There are two cases to consider:
//   1. If the "from" space has enough space to hold this object, then
//      allocation succeeds, return the apropriate address (look at
//      the above figure, be careful);
//   2. if there is no enough space left in the "from" space, then
//      you should call the function "Tiger_gc()" to collect garbages.
//      and after the collection, there are still two sub-cases:
//        a: if there is enough space, you can do allocations just as case 1; 
//        b: if there is still no enough space, you can just issue
//           an error message ("OutOfMemory") and exit.
//           (However, a production compiler will try to expand
//           the Java heap.)
//# define  FreeSpace heap.from + heap.size - heap.fromFree
//  return (heap.to - heap.fromFree + 2*heap.size)%(2*heap.size);
void *Tiger_new (void *vtable, int size)
{
  int needSpace = size;
  if ( FreeSpace < needSpace ){
//	  printf("before gc\n");
       Tiger_gc();
       if ( FreeSpace < needSpace ){
            printf("OutOfMemory\n");
            exit(-1);
       }
  }
//  printf("new\n");
  memset(heap.fromFree,0,needSpace);
  int *p = (int*)heap.fromFree;
  heap.fromFree += needSpace;
  p[0] = (int)vtable;
  p[1] = 0;
  p[2] = 0;
  p[3] = 0;
//  p = p + 4;
  return (void*)p;
}

// "new" an array of size "length", do necessary
// initializations. And each array comes with an
// extra "header" storing the array length and other information.
/*    ----------------
      | vptr         | (this field should be empty for an array)
      |--------------|
      | isObjOrArray | (1: for array)
      |--------------|
      | length       |
      |--------------|
      | forwarding   | 
      |--------------|\
p---->| e_0          | \      
      |--------------|  s
      | ...          |  i
      |--------------|  z
      | e_{length-1} | /e
      ----------------/
*/
// Try to allocate an array object in the "from" space of the Java
// heap. Read Tiger book chapter 13.3 for details on the
// allocation.
// There are two cases to consider:
//   1. If the "from" space has enough space to hold this array object, then
//      allocation succeeds, return the apropriate address (look at
//      the above figure, be careful);
//   2. if there is no enough space left in the "from" space, then
//      you should call the function "Tiger_gc()" to collect garbages.
//      and after the collection, there are still two sub-cases:
//        a: if there is enough space, you can do allocations just as case 1; 
//        b: if there is still no enough space, you can just issue
//           an error message ("OutOfMemory") and exit.
//           (However, a production compiler will try to expand
//           the Java heap.)
//#define  needBytes(length)  Header + sizeof(int)*length
void *Tiger_new_array (int length){
	 int needSpace = needBytes(length);
  if ( FreeSpace < needSpace ){
      Tiger_gc();
      if ( FreeSpace < needSpace ){
          printf("OutOfMemory\n");
          exit(-1);
      }
    }
      memset(heap.fromFree,0,needSpace);
      int *p =(int*) heap.fromFree;
      heap.fromFree += needSpace;
      p[0] = 0;
      p[1] = 1;
      p[2] = length;
      p[3] = 0;
      return (void*)p;
}

/*
void outputlog()
{
	FILE *log=fopen("gcLog.txt","wb+");
	while(fgets(buffer , 100 , log))
	{
		printf("%s\n",buffer);
	}
	fclose(log);
}
*/
//===============================================================//
// The Gimple Garbage Collector

// Lab 4, exercise 12:
// A copying collector based-on Cheney's algorithm.

int Forward(void *p){
  int *forwarding;
  int realSize = 0;
  char *class_gc_map;
  if (p>=(void*)heap.from && p<(void*)heap.from+heap.size ){
    forwarding= (int*)p+3;
    if ( *forwarding>=(int)heap.to && *forwarding<(int)heap.to+heap.size ){
      return *forwarding;
    }else{
      *forwarding=(int)heap.toNext;
      if( 0 == *((int*)p+1 )){            //object
        class_gc_map=(char*)(*(int*)(*((int*)p)));   // the fields map
        realSize = needBytes(strlen(class_gc_map));
        memcpy(heap.toNext,p,realSize);
      }else{ //array
    	realSize = needBytes(*((int*)p +2));
        memcpy(heap.toNext,p,realSize);
      }
      heap.toNext = heap.toNext + realSize;
    }
    return *forwarding;
  }
  return (int)p;
}


static void Tiger_gc (){
	FILE *logfile=fopen("gc_log.txt","a");
    round++;
    struct timeval gc_start,gc_end;
    float gc_time;
    gettimeofday(&gc_start,NULL);
    int preGC = (int)heap.fromFree - (int)heap.from;
//    printf("preGC:%d\n",preGC);
   heap.toNext = heap.to;   //point to vptr
   char *scan = heap.toNext;
   void *prePrev=prev;       // mark the head frame chain
   while(NULL != prev){
   //args forward
    char *args_gc_map = (char*)(*((int*)prev+1));
    int *args=(int*)(*((int*)prev+2));
    int locals_gc_map = *((int*)prev+3);
    int *locals=(int*)prev+4;
//    printf("args_gc_map:%s\n",args_gc_map);
    while('\0' != *args_gc_map){
      if('1' == *args_gc_map){
        *args = Forward((int*)(*args));
      }
      args++;
      args_gc_map++;
    }
  //locals forward
    while(locals_gc_map > 0){
        *locals=Forward((int*)(*locals));
        locals++;
        locals_gc_map--;
    }
    prev=(void*)(*(int*)prev);
  }
  prev=prePrev; //
  while(scan<heap.toNext){
    int scan_size=0;
    if(0==*((int*)scan + 1)){  //object
      int *scan_fields = (int*)scan + 4;
      char *class_gc_map=(char*)(*(int*)(*(int*)scan));
      while( '\0'  !=  *class_gc_map){
        if( '1' == *class_gc_map){
          *scan_fields = Forward((int*)(*scan_fields));
        }
        scan_size++;
        scan_fields++;
        class_gc_map++;
      }
    }else{  //array
      scan_size+=*((int*)scan+2);
    }
    scan = scan + needBytes(scan_size);
  }

  int afterGC = (int)heap.toNext - (int)heap.to;
//  printf("afterGC:%d\n",afterGC);
  //swap heap pointer
  char *swap = heap.from;
  heap.from = heap.to;
  heap.to = swap;
  heap.fromFree = heap.toNext;
  gettimeofday(&gc_end,NULL);
  gc_time = durTime(gc_start,gc_end);
  printf("%d round of GC: %.5f ms, collected %d bytes garbage.\n",round,gc_time ,preGC - afterGC);
 if (Control_logGc){
      char buffer[100];
      sprintf(buffer,"%d round of GC: %.5f ms, collected %d bytes garbage.\n",round,gc_time ,preGC - afterGC);
      //addToLog(buffer);
      fprintf(logfile, "%s\n", buffer);
      fclose(logfile);
 }
}

void addToLog(char *buffer){
         if(round == 1){
        	 log_gc = (char*)malloc(logSize);
        	 memset(log_gc,0,logSize);
         }
         if (notEnough(strlen(buffer)+strlen(log_gc))){
        	 logSize*=2;
        	 log_gc = (char*)realloc(log_gc,logSize);
         }
         strcat(log_gc,buffer);
         return;
}
void Tiger_logGc(){
	FILE *logf=fopen("gc_log.txt","wb+");
    if(!logf){
        printf("Log GC Failed !!\n");
        exit(-1);
    }
    /*
    if(0 == strlen(log_gc)){
    	log_gc =(char*)malloc(logSize);
    	memset(log_gc,0,logSize);
    	char *buffer = "Your Tiger_heap is too large to call GC, just smaller it to see this log again\n";
    	strcat(log_gc,buffer);
    }
    */
//    fputs(log_gc,logf);
	fclose(logf);
}

