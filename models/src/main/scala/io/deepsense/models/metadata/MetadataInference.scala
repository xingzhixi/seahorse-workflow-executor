/**
 * Copyright (c) 2015, CodiLime Inc.
 */

package io.deepsense.models.metadata

import io.deepsense.deeplang.DOperable
import io.deepsense.deeplang.DOperable.AbstractMetadata
import io.deepsense.deeplang.inference.{InferContext, InferenceWarnings}
import io.deepsense.graph.GraphKnowledge.InferenceErrors
import io.deepsense.graph.Node
import io.deepsense.models.workflows.Workflow

case class MetadataInferenceResult(
  metadata: Seq[Option[AbstractMetadata]],
  warnings: InferenceWarnings,
  errors: InferenceErrors)

object MetadataInference {
  def run(
      experiment: Workflow,
      nodeId: Node.Id,
      portIndex: Int,
      baseContext: InferContext): MetadataInferenceResult = {

    val inferContext = InferContext(baseContext, true)
    val singlePortInferenceResult = experiment.graph.inferKnowledge(nodeId, portIndex, inferContext)

    MetadataInferenceResult(
      singlePortInferenceResult.knowledge.types.toList.map(
        (operable: DOperable) => operable.inferredMetadata),
      singlePortInferenceResult.warnings,
      singlePortInferenceResult.errors)
  }
}
