/*
 * Copyright 2014 Christian Weber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.arrow.test.spock.spring

import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.FieldInfo
import org.spockframework.runtime.model.MethodInfo
import org.spockframework.runtime.model.SpecInfo
import org.arrow.test.WorkflowTest

/**
 * Created by christian.weber on 01.10.2014.
 */
class CustomAnnotationDrivenExtension extends AbstractAnnotationDrivenExtension<WorkflowTest> {

    @Override
    void visitSpec(SpecInfo spec) {
        spec.reflection.mixin(WorkflowDsl)
    }

    @Override
    void visitSpecAnnotation(WorkflowTest annotation, SpecInfo spec) {

    }

    @Override
    void visitFeatureAnnotation(WorkflowTest annotation, FeatureInfo feature) {

    }

    @Override
    void visitFixtureAnnotation(WorkflowTest annotation, MethodInfo fixtureMethod) {

    }

    @Override
    void visitFieldAnnotation(WorkflowTest annotation, FieldInfo field) {

    }
}
