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
