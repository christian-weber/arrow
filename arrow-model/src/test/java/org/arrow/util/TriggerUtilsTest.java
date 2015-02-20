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

package org.arrow.util;

import junit.framework.Assert;

import org.arrow.test.runtime.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class TriggerUtilsTest {

    @Test
    public void testIsCronTime() {
        Assert.assertTrue(TriggerUtils.isCron("* * * * * *"));
        Assert.assertTrue(TriggerUtils.isCron("0 0 * * * *"));
        Assert.assertTrue(TriggerUtils.isCron("5 * * * * *"));
        Assert.assertTrue(TriggerUtils.isCron("*/5 * * * * *"));
        Assert.assertTrue(TriggerUtils.isCron("59 23 * * 1 *"));
        Assert.assertTrue(TriggerUtils.isCron("20,30 1 * * 1-5 *"));
        Assert.assertTrue(TriggerUtils.isCron("20,30 1 * * 1-5 *"));

        Assert.assertFalse(TriggerUtils.isCron("A 1 * * 1-5 *"));
    }

}
