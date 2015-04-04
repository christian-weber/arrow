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

package org.arrow.parser.xml.bpmn

import org.arrow.model.process.Definitions

/**
 * XStream {@link BpmnParser} implementation.
 *
 * @author christian.weber
 * @since 1.0.0
 */
public class XStreamBpmnParser implements BpmnParser {

    /**
     * {@inheritDoc}
     */
    @Override
    public Definitions parse(InputStream stream) {
        assert stream != null, "stream must not be null"
        BpmnXStream.instance.fromXML(stream) as Definitions
    }

}