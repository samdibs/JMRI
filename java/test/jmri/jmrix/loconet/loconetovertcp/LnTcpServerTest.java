package jmri.jmrix.loconet.loconetovertcp;

import jmri.jmrix.loconet.LocoNetInterfaceScaffold;
import jmri.jmrix.loconet.LocoNetSystemConnectionMemo;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for LnTcpServer class.
 *
 * @author Paul Bender Copyright (C) 2016
 */
public class LnTcpServerTest {

    private LocoNetInterfaceScaffold lnis;

    @Test
    public void getInstanceTest() {
        Assert.assertNotNull("Server getInstance", LnTcpServer.getDefault());
        LnTcpServer.getDefault().disable();  // turn the server off after enabled during creation.
    }

    @Before
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetProfileManager();
        LocoNetSystemConnectionMemo memo = new LocoNetSystemConnectionMemo();
        // provide a memo in order to later use InstanceManager.getDefault()
        lnis = new LocoNetInterfaceScaffold(memo);
        // memo.setLnTrafficController(lnis);
    }

    @After
    public void tearDown() {
        lnis = null;
        JUnitUtil.tearDown();
        JUnitUtil.resetInstanceManager();
    }

}
