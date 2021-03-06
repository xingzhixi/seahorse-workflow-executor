/**
 * Copyright 2016, deepsense.io
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

package io.deepsense.deeplang

import org.scalatest.mockito.MockitoSugar

import io.deepsense.commons.rest.client.datasources.{DatasourceClient, DatasourceInMemoryClientFactory}
import io.deepsense.deeplang.catalogs.doperable.DOperableCatalog
import io.deepsense.deeplang.doperables.dataframe.DataFrameBuilder
import io.deepsense.deeplang.inference.InferContext

object MockedInferContext extends MockitoSugar {

  def apply(dOperableCatalog: DOperableCatalog = CatalogRecorder.catalogs.dOperableCatalog,
            dataFrameBuilder: DataFrameBuilder = mock[DataFrameBuilder],
            innerWorkflowParser: InnerWorkflowParser = mock[InnerWorkflowParser],
            datasourceClient: DatasourceClient = new DatasourceInMemoryClientFactory(List.empty).createClient
           ): InferContext = InferContext(
    dataFrameBuilder,
    dOperableCatalog,
    innerWorkflowParser,
    datasourceClient
  )

}
