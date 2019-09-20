package at.forsyte.apalache.tla.assignments

import at.forsyte.apalache.tla.lir.Builder.primeInSingleton
import at.forsyte.apalache.tla.lir.oper.TlaActionOper
import at.forsyte.apalache.tla.lir.transformations.impl.TrackerWithListeners
import at.forsyte.apalache.tla.lir._
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith( classOf[JUnitRunner] )
class TestSymbTransGenerator extends FunSuite with TestingPredefs {

  val stg = new SymbTransGenerator( TrackerWithListeners() )

  import stg.helperFunctions._
  import at.forsyte.apalache.tla.lir.Builder._

  test( "Test allCombinations" ) {

    assert( allCombinations[Int](Seq.empty[Set[Set[Int]]]).isEmpty )

    val empty = Set.empty[Set[Int]]
    assert( allCombinations( Seq( empty ) ) == empty )


    val s11 = Set( Set( 1 ) )
    val s12 = Set( Set( 2 ) )
    val expected1 = Set( Set( 1, 2 ) )
    val actual1 = allCombinations[Int]( Seq( s11, s12 ) )

    assert( expected1 == actual1 )

    val N = 10
    val oneToN = Range( 1, N ).toSet
    val ss = oneToN.toSeq map { x => Set( Set( x ) ) }
    val expected2 = Set( oneToN )
    val actual2 = allCombinations[Int]( ss )

    assert( expected2 == actual2 )

    val s31 = Set( Set( 1 ), Set( 2 ) )
    val s32 = Set( Set( 3 ), Set( 4 ) )
    val s33 = Set( Set( 5 ), Set( 6 ) )
    val expected3 = Set(
      Set( 1, 3, 5 ),
      Set( 1, 3, 6 ),
      Set( 1, 4, 5 ),
      Set( 1, 4, 6 ),
      Set( 2, 3, 5 ),
      Set( 2, 3, 6 ),
      Set( 2, 4, 5 ),
      Set( 2, 4, 6 )
    )
    val actual3 = allCombinations( Seq( s31, s32, s33 ) )

    assert( actual3 == expected3 )
  }

  test( "Test labelsAt" ) {
    val ex11 = n_x
    val ex12 = n_y
    val ex13 = or( ex11, ex12 )

    val sel1 : SelMapType = Map(
      ex13.ID -> Set( Set( ex11.ID ), Set( ex12.ID ) )
    )

    val expected1 = Set( ex11.ID, ex12.ID )
    val actual1 = labelsAt( ex13, sel1 )

    assert( expected1 == actual1 )
  }

  test( "Test makeAllSelections" ) {
    val xasgn11 = in( prime( n_x ), n_S )
    val xasgn12 = in( prime( n_x ), enumSet( int( 1 ) ) )
    val yasgn11 = in( prime( n_y ), enumSet( n_T ) )
    val yasgn12 = in( prime( n_y ), dotdot( int( 2 ), n_a ) )

    val ex1 =
      ite(
        ge( int( 0 ), int( 1 ) ),
        xasgn11,
        xasgn12
      )

    val ex2 = or( yasgn11, yasgn12 )

    val ex3 = and(
      ex1,
      ex2
    )

    val possibleAssgnsX = Seq(
      Set( xasgn11.ID ),
      Set( xasgn12.ID )
    )

    val possibleAssgnsY = Seq(
      Set( yasgn11.ID ),
      Set( yasgn12.ID )
    )

    val possibleAssgnsXY = Seq(
      Set( xasgn11.ID, yasgn11.ID ),
      Set( xasgn11.ID, yasgn12.ID ),
      Set( xasgn12.ID, yasgn11.ID ),
      Set( xasgn12.ID, yasgn12.ID )
    )

    val selections1 = possibleAssgnsX map { asgn =>
      allSelections( ex1, asgn )
    }

    val selections2 = possibleAssgnsY map { asgn =>
      allSelections( ex2, asgn )
    }

    val selections3 = possibleAssgnsXY map { asgn =>
      allSelections( ex3, asgn )
    }

    selections1 zip possibleAssgnsX foreach { case (s,e) =>
        assert( s(ex1.ID) == Set(e) )
    }

    selections2 zip possibleAssgnsY foreach { case (s,e) =>
      assert( s(ex2.ID) == Set(e) )
    }

    selections3 zip possibleAssgnsXY foreach { case (s,e) =>
      assert( s(ex3.ID) == Set(e) )
    }
  }


  test( "Test ITE with multibranching" ){

    val asgn1 = primeInSingleton( n_x, int(1) )
    val asgn2 = primeInSingleton( n_x, int(2) )
    val asgn3 = primeInSingleton( n_x, int(3) )

    val next = ite(
      trueEx,
      asgn1,
      ite(
        trueEx,
        asgn2,
        asgn3
      )
    )

    val sel = Seq( asgn1.ID, asgn2.ID, asgn3.ID )

    val transitions = stg( next, sel )

    // Only expected to work on the above
    def countXprime(ex: TlaEx): Int = ex match {
      case OperEx( TlaActionOper.prime, NameEx( "x" ) ) => 1
      case OperEx( _, args@_* ) =>
        (args map countXprime).sum
      case _ => 0
    }

    transitions foreach { t =>
      assert(countXprime( t._2 ) == 1)
    }

  }


}