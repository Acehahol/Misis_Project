package misis.monad

//import scala.concurrent.ExecutionContext.Implicits.global
import java.util.concurrent.{LinkedBlockingQueue, ThreadPoolExecutor, TimeUnit}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object MonadApp extends App {
  implicit val ec = ExecutionContext.fromExecutor(new ThreadPoolExecutor(5, 5, 5, TimeUnit.SECONDS, new LinkedBlockingQueue[Runnable]))

  case class Item(name: String)

  val apple = Item("apple")

  private val maybeItem: Option[Item] = Option(apple)
  private val someItem: Option[Item]  = Some(apple)

  val store: Map[Int, Item] = Map(1 -> Item("banana"), 2 -> Item("pen"), 3 -> Item("pizza"))


  def show(i: Int): String = store.get(i).map(_.name).getOrElse(s"Такого элемента нет ${i}")
  println(show(2))
  println(show(4))

  def hardOperation(n: Int) = {
    var i = 0l
    while (i< n * 10000000000l) i += 1
    i
  }

  val f2 = Future {
    hardOperation(2)
    println("2 is done")
    2
  }
  println("after 2")
  val f1 = Future {
    hardOperation(1)
    println("1 is done")
    1
  }
  println("after 1")
  f2.map(i => println("result f2 = " + i))
  val ff12 = f1
    .flatMap(_ => f2)
    .map { res =>
      println("result after f1 and f2 = " + res)
      res
    }
  f1.flatMap(_ => f2).map(res => println("result after f1 and f2 = " + res))
  val resultF = Future.sequence(List(f1, f2)).map(_ => println("1&2 is over"))

  //    Await.ready(resultF, Duration.Inf)
}