/**
 * Copyright 2015, deepsense.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.deepsense.deeplang.doperables

import scala.reflect.runtime.universe.TypeTag

import org.apache.spark.ml
import org.apache.spark.sql.types.StructType

import io.deepsense.deeplang.ExecutionContext
import io.deepsense.deeplang.doperables.dataframe.{DataFrame, DataFrameColumnsGetter}
import io.deepsense.deeplang.doperables.multicolumn.SingleColumnParams.SingleColumnInPlaceChoice
import io.deepsense.deeplang.doperables.multicolumn.SingleColumnParams.SingleTransformInPlaceChoices.NoInPlaceChoice
import io.deepsense.deeplang.doperables.multicolumn.{HasSingleInPlaceParam, HasSpecificParams}
import io.deepsense.deeplang.doperables.spark.wrappers.params.common.HasInputColumn
import io.deepsense.deeplang.params.Param
import io.deepsense.deeplang.params.wrappers.spark.ParamsWithSparkWrappers

abstract class SparkSingleColumnEstimatorWrapper[
    MD <: ml.Model[MD] { val outputCol: ml.param.Param[String] },
    E <: ml.Estimator[MD] { val outputCol: ml.param.Param[String] },
    MW <: SparkSingleColumnModelWrapper[MD, E]]
(override implicit val modelWrapperTag: TypeTag[MW], override implicit val estimatorTag: TypeTag[E])
  extends SparkEstimatorWrapper[MD, E, MW]
  with ParamsWithSparkWrappers
  with HasInputColumn
  with HasSingleInPlaceParam
  with HasSpecificParams {

  override lazy val params: Array[Param[_]] =
    Array(inputColumn, singleInPlaceChoice) ++ getSpecificParams

  def setSingleInPlaceParam(value: SingleColumnInPlaceChoice): this.type = {
    set(singleInPlaceChoice -> value)
  }

  def setNoInPlace(outputColumn: String): this.type = {
    setSingleInPlaceParam(NoInPlaceChoice().setOutputColumn(outputColumn))
  }

  override private[deeplang] def _fit(ctx: ExecutionContext, df: DataFrame): MW = {
    val schema = df.schema.get
    val inputColumnName = DataFrameColumnsGetter.getColumnName(schema, $(inputColumn))
    val convertedDataFrame = if (convertInputNumericToVector
      && NumericToVectorUtils.isColumnNumeric(schema, inputColumnName)) {
      // Automatically convert numeric input column to one-element vector column
      DataFrame.fromSparkDataFrame(NumericToVectorUtils.convertDataFrame(df, inputColumnName, ctx))
    } else {
      df
    }
    super._fit(ctx, convertedDataFrame)
  }


  override private[deeplang] def _fit_infer(maybeSchema: Option[StructType]): MW = {
    maybeSchema match {
      case Some(schema) =>
        val inputColumnName = DataFrameColumnsGetter.getColumnName(schema, $(inputColumn))
        val convertedSchema = if (convertInputNumericToVector
          && NumericToVectorUtils.isColumnNumeric(schema, inputColumnName)) {
          // Automatically convert numeric input column to one-element vector column
          NumericToVectorUtils.convertSchema(schema, inputColumnName)
        } else {
          schema
        }
        super._fit_infer(Some(convertedSchema))
      case None => super._fit_infer(None)
    }
  }
}
