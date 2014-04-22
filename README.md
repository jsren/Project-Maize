Project-Maize
=============

Project Maize - A multi-pass stage compiler written entirely in Java.

Features
--------

<h4><em>Modules, not headers</em></h4>

    using std;


<h4><em>Inline assembly</em></h4>

    int add(a, b)
    {
       #add a, b
       return a;
    }
    
    asm int add(a, b)
    {
      #add a, b
      #mov eax, a
      #ret
    }
    
<h4><em>Better namespaces and class attributes</em></h4>

    namespace system::math
    {
        [type:pod, template:T, align:4]
        public class Widget
        {
            private T obj;
            
            public void doSomething() {
                obj.function();
            }
        }
    }

<h3>Current Status: PRE-ALPHA</h3>

  - Complete operator parser
  - Fix code generator bugs
  - Tidy & complete second-pass parser
  - ELF symbol table format




