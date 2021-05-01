package habla.doric
package syntax

import cats.implicits._

import org.apache.spark.sql.{DataFrame, Dataset}

trait DataFrameOps {

  implicit class DataframeSyntax[A](df: Dataset[A]) {

    /**
      * Returns a new Dataset by adding a column or replacing the existing column that has
      * the same name.
      *
      * `column`'s expression must only refer to attributes supplied by this Dataset. It is an
      * error to add a column that refers to some other Dataset.
      *
      * @note this method introduces a projection internally. Therefore, calling it multiple times,
      *       for instance, via loops in order to add multiple columns can generate big plans which
      *       can cause performance issues and even `StackOverflowException`.
      */
    def withColumn[T](colName: String, col: DoricColumn[T]): DataFrame = {
      col.elem
        .run(df.toDF())
        .fold(
          x => {
            throw DoricMultiError(x)
            df.toDF()
          },
          df.withColumn(colName, _)
        )
    }

    def filter(col: BooleanColumn): Dataset[A] = {
      col.elem
        .run(df.toDF())
        .fold(
          x => {
            throw DoricMultiError(x)
            df
          },
          df.filter
        )
    }

    def select(col: DoricColumn[_]*): DataFrame = {
      col
        .map(_.elem)
        .toList
        .sequence
        .run(df.toDF())
        .fold(
          x => {
            throw DoricMultiError(x)
            df.toDF()
          },
          df.select(_: _*)
        )
    }

    /*
    def groupBy[T: FromDf](column: DataFrame => T): RelationalGroupedDataset = {
      df.groupBy(column(df).sparkColumn)
    }

    def groupBy[T: FromDf](columns: T*): RelationalGroupedDataset = {
      df.groupBy(columns.map(_.sparkColumn): _*)
    }

    def groupBy[T1: FromDf, T2: FromDf](
        column1: T1,
        column2: T2
    ): RelationalGroupedDataset = {
      df.groupBy(column1.sparkColumn)
    }

    def groupBy[T1: FromDf, T2: FromDf, T3: FromDf](
        column1: T1,
        column2: T2,
        column3: T3
    ): RelationalGroupedDataset = {
      df.groupBy(column1.sparkColumn)
    }

    def groupBy[T1: FromDf, T2: FromDf, T3: FromDf, T4: FromDf](
        column1: T1,
        column2: T2,
        column3: T3,
        column4: T4
    ): RelationalGroupedDataset = {
      df.groupBy(column1.sparkColumn)
    }

    def groupByF[T: FromDf](f: DataFrame => T): GroupedDataFrame = {
      new GroupedDataFrame(df, f(df).sparkColumn)
    }

    def groupByF[TG: FromDf, TA: FromDf](
        f: DataFrame => TG
    )(agg: DataFrame => TA): DataFrame = {
      df.groupBy(f(df).sparkColumn).agg(agg(df).sparkColumn)
    }

    def groupByFMulty[TG: FromDf, TA: FromDf](
        f: DataFrame => Seq[TG]
    )(agg: DataFrame => Seq[TA]): DataFrame = {
      val aggColumns = agg(df).map(_.sparkColumn)
      df.groupBy(f(df).map(_.sparkColumn): _*).agg(aggColumns.head, aggColumns.tail: _*)
    }

  }

  class GroupedDataFrame private[doric] (df: DataFrame, columns: Column*) {
    def agg[T: FromDf](aggColumn: T, aggColumns: T*): DataFrame = {
      df.groupBy(columns: _*).agg(aggColumn.sparkColumn, aggColumns.map(_.sparkColumn): _*)
    }
  }

  implicit class GroupedDataFrameSyntax(gdf: RelationalGroupedDataset) {
    def agg[T: FromDf](column: T, columns: T*): DataFrame = {
      gdf.agg(column.sparkColumn, columns.map(_.sparkColumn): _*)
    }

    def agg[T1: FromDf](
        column1: T1
    ): DataFrame = {
      gdf.agg(column1.sparkColumn)
    }

    def agg[T1: FromDf, T2: FromDf](
        column1: T1,
        column2: T2
    ): DataFrame = {
      gdf.agg(column1.sparkColumn, column2.sparkColumn)
    }

    def agg[T1: FromDf, T2: FromDf, T3: FromDf](
        column1: T1,
        column2: T2,
        column3: T3
    ): DataFrame = {
      gdf.agg(column1.sparkColumn, column2.sparkColumn, column3.sparkColumn)
    }

    def agg[T1: FromDf, T2: FromDf, T3: FromDf, T4: FromDf](
        column1: T1,
        column2: T2,
        column3: T3,
        column4: T4
    ): DataFrame = {
      gdf.agg(column1.sparkColumn, column2.sparkColumn, column3.sparkColumn, column4.sparkColumn)
    }
     */
  }

}