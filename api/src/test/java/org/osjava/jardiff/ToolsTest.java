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

    static class lala {
        int lala;
    }

	@Test
	public void isClassAccessChange() {
		// A class can't become final.
		assertTrue(Tools.isClassAccessChange(0, Opcodes.ACC_FINAL));
		assertTrue(Tools.isClassAccessChange(0, Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL));
		// ... but can become non-final.
		assertFalse(Tools.isClassAccessChange(Opcodes.ACC_FINAL, 0));
		assertFalse(Tools.isClassAccessChange(Opcodes.ACC_FINAL + Opcodes.ACC_PUBLIC, Opcodes.ACC_PUBLIC));

		// No matter the final access, can't become protected or private or
		// package if it was public.
		assertTrue(Tools.isClassAccessChange(Opcodes.ACC_FINAL + Opcodes.ACC_PUBLIC, 0));
		assertTrue(Tools.isClassAccessChange(Opcodes.ACC_PUBLIC, Opcodes.ACC_PROTECTED));
		// A class can become concrete.
		assertFalse(Tools.isClassAccessChange(Opcodes.ACC_ABSTRACT, 0));
		assertFalse(Tools.isClassAccessChange(Opcodes.ACC_ABSTRACT + Opcodes.ACC_PUBLIC, Opcodes.ACC_PUBLIC));
		assertFalse(Tools.isClassAccessChange(Opcodes.ACC_ABSTRACT + Opcodes.ACC_PROTECTED, Opcodes.ACC_PROTECTED));
		// ...but can't become abstract
		assertTrue(Tools.isClassAccessChange(0, Opcodes.ACC_ABSTRACT));
		assertTrue(Tools.isClassAccessChange(Opcodes.ACC_PUBLIC, Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT));
		assertTrue(Tools.isClassAccessChange(Opcodes.ACC_PROTECTED, Opcodes.ACC_PROTECTED + Opcodes.ACC_ABSTRACT));
	}

    @Test
    public void isFieldAccessChange() {
        // A field can't become final.
        assertTrue(Tools.isFieldAccessChange(0, Opcodes.ACC_FINAL));
        assertTrue(Tools.isFieldAccessChange(0, Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL));
        // ... but can become non-final.
        assertFalse(Tools.isFieldAccessChange(Opcodes.ACC_FINAL, 0));
        assertFalse(Tools.isFieldAccessChange(Opcodes.ACC_FINAL + Opcodes.ACC_PUBLIC, Opcodes.ACC_PUBLIC));

        // No matter the final access, can't become protected or private or
        // package if it was public.
        assertTrue(Tools.isFieldAccessChange(Opcodes.ACC_FINAL + Opcodes.ACC_PUBLIC, 0));
        assertTrue(Tools.isFieldAccessChange(Opcodes.ACC_PUBLIC, Opcodes.ACC_PROTECTED));

        // A field can't change static
        assertTrue(Tools.isFieldAccessChange(Opcodes.ACC_STATIC, 0));
        assertTrue(Tools.isFieldAccessChange(Opcodes.ACC_STATIC + Opcodes.ACC_PUBLIC, Opcodes.ACC_PUBLIC));

        // A field can't change volatile
        assertTrue(Tools.isFieldAccessChange(Opcodes.ACC_VOLATILE, 0));
        assertTrue(Tools.isFieldAccessChange(Opcodes.ACC_VOLATILE + Opcodes.ACC_PUBLIC, Opcodes.ACC_PUBLIC));

        // A field can change transient
        assertFalse(Tools.isFieldAccessChange(0,
                                              Opcodes.ACC_TRANSIENT));
        assertFalse(Tools.isFieldAccessChange(Opcodes.ACC_PUBLIC,
                                              Opcodes.ACC_PUBLIC + Opcodes.ACC_TRANSIENT));
        assertFalse(Tools.isFieldAccessChange(Opcodes.ACC_TRANSIENT,
                                              0));
        assertFalse(Tools.isFieldAccessChange(Opcodes.ACC_PUBLIC + Opcodes.ACC_TRANSIENT,
                                              Opcodes.ACC_PUBLIC));
    }

    @Test
    public void isMethodAccessChange() {
        // A non-static method can't become final.
        assertTrue(Tools.isMethodAccessChange(0, Opcodes.ACC_FINAL));
        assertTrue(Tools.isMethodAccessChange(0, Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL));
        // ... but can become non-final.
        assertFalse(Tools.isMethodAccessChange(Opcodes.ACC_FINAL, 0));
        assertFalse(Tools.isMethodAccessChange(Opcodes.ACC_FINAL + Opcodes.ACC_PUBLIC, Opcodes.ACC_PUBLIC));
        // ... but a static method can become final!
        assertFalse(Tools.isMethodAccessChange(Opcodes.ACC_STATIC,
                                               Opcodes.ACC_STATIC + Opcodes.ACC_FINAL));
        assertFalse(Tools.isMethodAccessChange(Opcodes.ACC_STATIC + Opcodes.ACC_PUBLIC,
                                               Opcodes.ACC_STATIC + Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL));

        // No matter the final access, can't become protected or private or
        // package if it was public.
        assertTrue(Tools.isMethodAccessChange(Opcodes.ACC_FINAL + Opcodes.ACC_PUBLIC, 0));
        assertTrue(Tools.isMethodAccessChange(Opcodes.ACC_PUBLIC, Opcodes.ACC_PROTECTED));
        // A class or method can become concrete.
        assertFalse(Tools.isMethodAccessChange(Opcodes.ACC_ABSTRACT, 0));
        assertFalse(Tools.isMethodAccessChange(Opcodes.ACC_ABSTRACT + Opcodes.ACC_PUBLIC, Opcodes.ACC_PUBLIC));
        assertFalse(Tools.isMethodAccessChange(Opcodes.ACC_ABSTRACT + Opcodes.ACC_PROTECTED, Opcodes.ACC_PROTECTED));
        // ...but can't become abstract
        assertTrue(Tools.isMethodAccessChange(0, Opcodes.ACC_ABSTRACT));
        assertTrue(Tools.isMethodAccessChange(Opcodes.ACC_PUBLIC, Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT));
        assertTrue(Tools.isMethodAccessChange(Opcodes.ACC_PROTECTED, Opcodes.ACC_PROTECTED + Opcodes.ACC_ABSTRACT));

        // A method can't change static
        assertTrue(Tools.isMethodAccessChange(Opcodes.ACC_STATIC, 0));
        assertTrue(Tools.isMethodAccessChange(Opcodes.ACC_STATIC + Opcodes.ACC_PUBLIC, Opcodes.ACC_PUBLIC));

        // A method can change synchronized
        assertFalse(Tools.isMethodAccessChange(0,
                                               Opcodes.ACC_SYNCHRONIZED));
        assertFalse(Tools.isMethodAccessChange(Opcodes.ACC_PUBLIC,
                                               Opcodes.ACC_PUBLIC + Opcodes.ACC_SYNCHRONIZED));
        assertFalse(Tools.isMethodAccessChange(Opcodes.ACC_SYNCHRONIZED,
                                               0));
        assertFalse(Tools.isMethodAccessChange(Opcodes.ACC_PUBLIC + Opcodes.ACC_SYNCHRONIZED,
                                               Opcodes.ACC_PUBLIC));

        // A method can change native
        assertFalse(Tools.isMethodAccessChange(0,
                                               Opcodes.ACC_NATIVE));
        assertFalse(Tools.isMethodAccessChange(Opcodes.ACC_PUBLIC,
                                               Opcodes.ACC_PUBLIC + Opcodes.ACC_NATIVE));
        assertFalse(Tools.isMethodAccessChange(Opcodes.ACC_NATIVE,
                                               0));
        assertFalse(Tools.isMethodAccessChange(Opcodes.ACC_PUBLIC + Opcodes.ACC_NATIVE,
                                               Opcodes.ACC_PUBLIC));
    }

}
