package org.sbolstandard.core3.entity.test;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import org.sbolstandard.core3.api.SBOLAPI;
import org.sbolstandard.core3.entity.Attachment;
import org.sbolstandard.core3.entity.Collection;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Experiment;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.io.SBOLFormat;
import org.sbolstandard.core3.io.SBOLIO;
import org.sbolstandard.core3.test.TestUtil;
import org.sbolstandard.core3.util.Configuration;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.util.Configuration.PropertyValidationType;
import org.sbolstandard.core3.vocabulary.ComponentType;
import org.sbolstandard.core3.vocabulary.Role;

import junit.framework.TestCase;

public class ExperimentTest extends TestCase {
	
	public void testExperiment() throws SBOLGraphException, IOException
    {
		String baseUri="https://sbolstandard.org/examples/";
        SBOLDocument doc=new SBOLDocument(URI.create(baseUri));
        
       
        Attachment attachment1=doc.createAttachment("attachment1", URI.create("https://sbolstandard.org/attachment1"));
        Attachment attachment2=doc.createAttachment("attachment2", URI.create("https://sbolstandard.org/attachment2"));
        
        Experiment exp=doc.createExperiment("exp1");
        
    	Configuration.getConfiguration().setPropertyValidationType(PropertyValidationType.ValidateBeforeSavingSBOLDocuments);
        
        //Collections can be empty
        TestUtil.validateIdentified(exp,doc,0);
                
        exp.setMembers(Arrays.asList(attachment1.getUri(), attachment2.getUri()));
        //Collections can have members
        TestUtil.validateIdentified(exp,doc,0); 
        
        TestUtil.serialise(doc, "entity_additional/experiment", "experiment");
        System.out.println(SBOLIO.write(doc, SBOLFormat.TURTLE));
        TestUtil.assertReadWrite(doc);
    }

}