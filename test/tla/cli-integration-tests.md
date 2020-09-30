# Apalache CLI Integration Tests

The code blocks in this file use [mdx](https://github.com/realworldocaml/mdx) to
run integration tests of the Apalache CLI interface.

To run these tests, execute the [../mdx-test.py](../mdx-test.py) script with no 
arguments.

## How to write a test

Any `sh` code block will be run as a test. 

The test executes the command following a `$` which it expects to produce the
output on the subsequent lines.

Use `...` to omit lines in the match. This is useful for  nondeterministic
output.

Specify a non-zero return code `n` by adding `[n]` on a line by itself after
all output.

Pipe through filters to get arbitrary control of the expected output.

The usual flow is:

1. Write a failing test that executes the command to be run.
2. Run the test (see below).
3. Check that the corrected output is what you expect, then run `make promote`, 
   to copy the output back into this file.
4. Replace any non-essential lines with `...`.

See the documentation on `mdx` for more.

## How to run tests

### Run all the tests in this file

<!-- $MDX skip -->
```sh
test/mdx-test.py "Can print version"
```

### Run a single test

Each section, demarcated by headings, can be run selectively by supplying an 
argument that matches the heading.

E.g., to run just the test for the `version` command, run

<!-- $MDX skip -->
```sh
test/mdx-test.py "Can print version"
```

### Run all tests in sections matching a pattern

The matching is based on (perl) pattern matching, so you can run all the tests
in sections that include, e.g., `y2k` in their section headings with

<!-- $MDX skip -->
```sh
test/mdx-test.py y2k
```

## test environment

### working directory

```sh
$ pwd | grep -o test/tla
test/tla
```

## executing the binary

### executable prints version

```sh
$ apalache-mc version
...
EXITCODE: OK
```

### executable prints help

```sh
$ apalache-mc help
...
EXITCODE: OK
```

## running the check command

### check Bug20190118 succeeds

```sh
$ apalache-mc check --length=1 --init=Init --next=Next --inv=Inv Bug20190118.tla | sed 's/I@.*//'
...
The outcome is: NoError
...
```

### check mis.tla succeeds

```sh
$ apalache-mc check --length=5 --inv=IsIndependent mis.tla | sed 's/I@.*//'
...
The outcome is: NoError
...
```

### check mis_bug.tla errors

```sh
$ apalache-mc check --length=5 --inv=IsIndependent mis_bug.tla | sed 's/I@.*//'
...
The outcome is: Error
Checker has found an error
...
```


### check ast.tla succeeds

```sh
$ apalache-mc check --length=5 ast.tla | sed 's/I@.*//'
...
The outcome is: NoError
...
```

### check pr.tla suceeds

```sh
$ apalache-mc check --length=2 pr.tla | sed 's/I@.*//'
...
The outcome is: NoError
...
```

### check EWD840.tla succeeds

```sh
$ apalache-mc check --length=5 --inv=Inv EWD840.tla | sed 's/I@.*//'
...
The outcome is: NoError
...
```

### check Paxos.tla succeeds

```sh
$ apalache-mc check --length=5 --inv=Inv Paxos.tla | sed 's/I@.*//'
...
The outcome is: NoError
...
```

### check Bug20190118 succeeds

```sh
$ apalache-mc check --length=1 Bug20190118.tla | sed 's/I@.*//'
...
The outcome is: NoError
...
```

### check Bug20190921 succeeds

```sh
$ apalache-mc check --length=5 --cinit=CInit Bug20190921.tla | sed 's/I@.*//'
...
The outcome is: NoError
...
```

### check Counter.tla errors

```sh
$ apalache-mc check --length=10 --inv=Inv Counter.tla | sed 's/I@.*//'
...
The outcome is: Error
Checker has found an error
...
```

### y2k.tla

#### check y2k with length 20 and ConstInit errors

```sh
$ apalache-mc check --length=20 --inv=Safety --cinit=ConstInit y2k_cinit.tla  | sed 's/I@.*//'
...
The outcome is: Error
Checker has found an error
...
```

#### check y2k with length 20 succeeds

```sh
$ apalache-mc check --length=20 --inv=Safety y2k_instance.tla | sed 's/I@.*//'
...
The outcome is: NoError
...
```

#### check y2k with length 30 errors

```sh
$ apalache-mc check --length=30 --inv=Safety y2k_instance.tla | sed 's/I@.*//'
...
The outcome is: Error
Checker has found an error
...
```

### check Counter.tla errors

```sh
$ apalache-mc check --length=10 --inv=Inv Counter.tla | sed 's/I@.*//'
...
The outcome is: Error
Checker has found an error
...
```

### check NatCounter.tla errors

```sh
$ apalache-mc check --length=10 --inv=Inv NatCounter.tla  | sed 's/I@.*//'
...
The outcome is: Error
...
```

### check NeedForTypesWithTypes.tla succeeds

```sh
$ apalache-mc check --length=10 --cinit=ConstInit --inv=Inv NeedForTypesWithTypes.tla | sed 's/I@.*//'
...
The outcome is: NoError
...
```

### check HandshakeWithTypes.tla with length 4 succeeds

```sh
$ apalache-mc check --length=4 --inv=Inv HandshakeWithTypes.tla | sed 's/I@.*//'
...
The outcome is: NoError
...
```

### check HandshakeWithTypes.tla with lengh 5 deadlocks

```sh
$ apalache-mc check --length=5 --inv=Inv HandshakeWithTypes.tla | sed 's/I@.*//'
...
The outcome is: Deadlock
...
```

### check trivial violation of FALSE invariant

```sh
$ apalache-mc check --length=2 --inv=Inv Bug20200306.tla | sed 's/I@.*//'
...
The outcome is: Error
...
```

### check Init without an assignment fails

```sh
$ apalache-mc check --length=1 --inv=Inv Assignments20200309.tla
...
EXITCODE: ERROR (99)
[99]
```

### check Inline.tla suceeds

```sh
$ apalache-mc check --length=5 Inline.tla | sed 's/I@.*//'
...
The outcome is: NoError
...
```

### check Rec1.tla succeeds

```sh
$ apalache-mc check --length=5 --inv=Inv Rec1.tla | sed 's/I@.*//'
...
The outcome is: NoError
...
```

### check Rec3.tla succeeds
```sh
$ apalache-mc check --length=10 --inv=Inv Rec3.tla | sed 's/I@.*//'
...
The outcome is: NoError
...
```

### check Rec8.tla succeeds

```sh
$ apalache-mc check --length=10 --inv=Inv Rec8.tla | sed 's/I@.*//'
...
The outcome is: NoError
...
```

### check Rec9.tla succeeds

```sh
$ apalache-mc check --length=5 --inv=Inv Rec9.tla | sed 's/I@.*//'
...
The outcome is: NoError
...
```

### check ExistsAsValue.tla succeeds

```sh
$ apalache-mc check --inv=Inv ExistsAsValue.tla | sed 's/I@.*//'
...
The outcome is: NoError
...
```

### check Empty.tla fails

```sh
$ apalache-mc check Empty.tla
...
EXITCODE: ERROR (99)
[99]
```

### check HourClock.tla without Init fails

```sh
$ apalache-mc check --init=NonExistantInit HourClock.tla
...
EXITCODE: ERROR (99)
[99]
```

### check HourClock.tla without Next fails

```sh
$ apalache-mc check --next=NonExistantNext HourClock.tla
...
EXITCODE: ERROR (99)
[99]
```

### check HourClock.tla without Inv fails

```sh
$ apalache-mc check --inv=NonExistantInv HourClock.tla
...
EXITCODE: ERROR (99)
[99]
```

### check use of TLA_PATH for modules in child directory succeeds

```sh
$ TLA_PATH=./tla-path-tests apalache-mc check ./tla-path-tests/ImportingModule.tla | sed 's/I@.*//'
...
The outcome is: NoError
...
```
