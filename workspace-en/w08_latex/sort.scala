object SortComparison:
  def quickSort(arr: Array[Int]): Array[Int] =
    if arr.length <= 1 then arr
    else
      val pivot = arr(arr.length / 2)
      Array.concat(
        quickSort(arr.filter(_ < pivot)),
        arr.filter(_ == pivot),
        quickSort(arr.filter(_ > pivot))
      )

  def mergeSort(arr: Array[Int]): Array[Int] =
    if arr.length <= 1 then arr
    else
      val (left, right) = arr.splitAt(arr.length / 2)
      val sortedLeft = mergeSort(left)
      val sortedRight = mergeSort(right)
      merge(sortedLeft, sortedRight)

  private def merge(left: Array[Int], right: Array[Int]): Array[Int] =
    (left, right) match
      case (Array(), _) => right
      case (_, Array()) => left
      case (l, r) =>
        if l.head < r.head then l.head +: merge(l.tail, r)
        else r.head +: merge(l, r.tail)
