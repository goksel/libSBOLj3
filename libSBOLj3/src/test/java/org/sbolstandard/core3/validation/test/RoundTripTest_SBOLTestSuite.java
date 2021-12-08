package org.sbolstandard.core3.validation.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;

import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.jena.riot.RDFFormat;
import org.junit.Test;
import org.sbolstandard.core3.api.SBOLAPI;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.io.SBOLFormat;
import org.sbolstandard.core3.io.SBOLIO;
import org.sbolstandard.core3.test.TestUtil;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.validation.SBOLComparator;
import org.sbolstandard.core3.validation.SBOLValidator;

public class RoundTripTest_SBOLTestSuite {

	private static final String sboltestsuite="SBOLTestSuite" + File.separator + "SBOL3"; 
	@Test
	public void validate() throws IOException, SBOLGraphException {
		
		 //SBOLDocument doc2 = SBOLIO.read(new File("SBOLTestSuite/SBOL3/combine2020/combine2020.rdf"),RDFFormat.RDFXML_ABBREV);
		 //SBOLDocument doc2 = SBOLIO.read("SBOLTestSuite/SBOL3/combine2020/combine2020.rdf",SBOLFormat.RDFXML);
		 //SBOLDocument docttl = SBOLIO.read("output/combine2020/combine2020.ttl",SBOLFormat.TURTLE);
		 //SBOLDocument doc2 = SBOLIO.read("output/combine2020/combine2020.rdf",SBOLFormat.RDFXML);
		 
		 
		 
		
		String message=RoundTripTest.validateFolder(sboltestsuite);
		
		if (!message.isEmpty())
		{
			System.out.println(message);
			if (message.contains("Could'not"))
			{
				fail ("Validation found errors!");		
			}
		}
		

	}
	
	
}