package cz.zajezdy.data.bengine.test;

import cz.zajezdy.data.bengine.test.configuration.model.MultiTestDocument;
import cz.zajezdy.data.bengine.test.util.TestHelper;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class MultioutputTest {

    @Test
    public void canReturnMultipleOutput() throws Exception {
        MultiTestDocument d1 = TestHelper.getMultiTestDocument(true, false);
        assertTrue(d1.getCanContinue());
        assertThat(d1.getDocumentList(), Matchers.hasSize(2));
        assertThat(d1.getDocumentList().get(0).getTestText(), Matchers.is("no clue"));
        assertThat(d1.getDocumentList().get(1).getTestText(), Matchers.is("Changed name"));
    }

    @Test
    public void setCanContinue() throws Exception {
        MultiTestDocument d1 = TestHelper.getMultiTestDocument(true, true);
        assertFalse(d1.getCanContinue());
    }

    /**
     * This should return one output document even though addToOutput was not executed
     */
    @Test
    public void defaultOutputDocument() throws Exception {
        MultiTestDocument d1 = TestHelper.getMultiTestDocument(false, false);
        assertTrue(d1.getCanContinue());
        assertThat(d1.getDocumentList(), Matchers.hasSize(1));

        assertThat(d1.getDocumentList().get(0).getTestText(), Matchers.is("Changed name"));
    }
}
