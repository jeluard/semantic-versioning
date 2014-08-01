/**
 * Copyright 2012-2014 Julien Eluard and contributors
 * This project includes software developed by Julien Eluard: https://github.com/jeluard/
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
package org.osjava.jardiff;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import static org.junit.Assert.*;

public class ToolsTest {

    @Test
    public void isAccessChange() {
        assertTrue(Tools.isAccessChange(0, Opcodes.ACC_FINAL));
        assertTrue(Tools.isAccessChange(0, Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL));
        assertTrue(Tools.isAccessChange(Opcodes.ACC_FINAL + Opcodes.ACC_PUBLIC, 0));
        assertTrue(Tools.isAccessChange(Opcodes.ACC_PUBLIC, Opcodes.ACC_PROTECTED));
        assertFalse(Tools.isAccessChange(Opcodes.ACC_FINAL, 0));
        assertFalse(Tools.isAccessChange(Opcodes.ACC_FINAL + Opcodes.ACC_PUBLIC, Opcodes.ACC_PUBLIC));
    }

}
