---------------------------- MODULE Typing ----------------------------------
(****************************************************************************)
(* An Apalache module that defines the operators for type annotations.      *)
(* The standard language of TLA+ is untyped. However, if you like to        *)
(* refine your specifications with types, feel free to use this module.     *)
(*                                                                          *)
(* See ADR-002 on type annotations:                                         *)
(* https://github.com/informalsystems/apalache/pull/179                     *)
(*                                                                          *)
(* Contributors:                                                            *)
(* Shon Feder, Igor Konnov, Jure Kukovec, Andrey Kuprianov, 2020            *)
(****************************************************************************)

(****************************************************************************)
(* Assume that a constant or a state variable has the type type_str.        *)
(* This operator should be used in the top-level operator TypeAssumptions.  *)
(* @see ADR-002.                                                            *)
(*                                                                          *)
(* Example:                                                                 *)
(* VARIABLE x, S                                                            *)
(* TypeAssumptions ==                                                       *)
(*   /\ AssumeType(x, "Int")                                                *)
(*   /\ AssumeType(S, "Set(Str)")                                           *)
(*                                                                          *)
(* @param name is the name of a constant or a state varible                 *)
(* @param type_str is a string that represents a type in Type System 1      *)
(*                               (see ADR-002 for the concrete syntax)      *)
(* @return TRUE, though the type checkers and the model checkers uses this  *)
(*          may use this type annotation                                    *)
(****************************************************************************)
AssumeType(name, tp) == TRUE

(****************************************************************************)
(* Annotate an operator body (or the body of a recursive function) with     *)
(* a type.                                                                  *)
(* @see ADR-002.                                                            *)
(*                                                                          *)
(* Example:                                                                 *)
(* Plus(x, y) == "(Int, Int) => Int" ##                                     *)
(*               x + y                                                      *)
(*                                                                          *)
(* @param type_str is a string that represents a type in Type System 1      *)
(*                               (see ADR-002 for the concrete syntax)      *)
(* @param ex the operator body to be wrapped with a type                    *)
(* @return ex, that is, erase type information                              *)
(****************************************************************************)
type_str ## ex == ex

(****************************************************************************)
(* Produce an empty set, whose elements have the type tp.                   *)
(* Use this operator, if the type checker cannot infer the type of { }.     *)
(****************************************************************************)
EmptySet(tp) == { }

(****************************************************************************)
(* Produce an empty sequence, whose elements have the type tp.              *)
(* Use this operator, if the type checker cannot infer the type of << >>.   *)
(****************************************************************************)
EmptySeq(tp) == << >>

=============================================================================
