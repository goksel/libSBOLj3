package org.sbolstandard.core3.measure.test;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Optional;

import org.apache.jena.rdf.model.Resource;
import org.sbolstandard.core3.entity.*;
import org.sbolstandard.core3.entity.measure.SingularUnit;
import org.sbolstandard.core3.test.TestUtil;
import org.sbolstandard.core3.util.RDFUtil;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.vocabulary.*;
import junit.framework.TestCase;

public class UnitTest_13501 extends TestCase {
	
	public void testInterface() throws SBOLGraphException, IOException, Exception
    {
		String namespace="https://sbolstandard.org";
		String baseUri=namespace + "/examples/";

		SBOLDocument doc=new SBOLDocument(URI.create(baseUri));

		SingularUnit liter=doc.createSingularUnit("litre", "l", "liter");
		liter.setAlternativeLabels(Arrays.asList("liter","litre2"));
		liter.setComment("The litre is a unit of volume defined as 1.0e-3 cubic metre.");
		liter.setAlternativeSymbols(Arrays.asList("L", "L2"));
		liter.setLongComment("This is an example long comment.");
		liter.setFactor(Optional.of(0.001f));
		
		TestUtil.validateDocument(doc,0);
		
		liter.setLabel("literA");
		TestUtil.validateDocument(doc,0);
		
		Resource res=TestUtil.getResource(liter);
		RDFUtil.setProperty(res, DataModel.Identified.name, "literB");
		TestUtil.validateDocument(doc,1, "sbol3-13501");
		liter.setLabel("literA");
		TestUtil.validateDocument(doc,0);
    }

}
