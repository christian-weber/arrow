/*
 * Copyright 2015 Christian Weber
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

package org.arrow.extension

import com.thoughtworks.xstream.io.HierarchicalStreamReader
import org.springframework.util.CollectionUtils

/**
 * Arror groovy meta class extension module.
 *
 * @since 1.0.0
 * @author christian.weber
 */
class ArrowGroovyExtension {

    /**
     * HierarchicalStreamReader extension method.
     * @param self
     * @param closure
     */
    static void nextElement(final HierarchicalStreamReader self, Closure closure) {
        self.moveDown()
        closure()
        self.moveUp()
    }

    /**
     * Indicates if the given reference objects is containing.
     *
     * @param self
     * @param reference
     */
    static void containsObject(final Set self, Object reference) {
        !CollectionUtils.contains(self.iterator(), reference)
    }

    /**
     * Indicates if the element which matches the closure exists within the collection.
     *
     * @param self
     * @param closure
     * @return boolean
     */
    static boolean matchAny(final Collection self, Closure closure) {
        self.find(closure) != null
    }

}
