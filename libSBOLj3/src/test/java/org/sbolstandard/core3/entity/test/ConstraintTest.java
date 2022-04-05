package org.sbolstandard.core3.entity.test;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.sbolstandard.core3.api.SBOLAPI;
import org.sbolstandard.core3.entity.Collection;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.ComponentReference;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.io.SBOLFormat;
import org.sbolstandard.core3.io.SBOLIO;
import org.sbolstandard.core3.test.TestUtil;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.vocabulary.ComponentType;
import org.sbolstandard.core3.vocabulary.Encoding;
import org.sbolstandard.core3.vocabulary.RestrictionType;
import org.sbolstandard.core3.vocabulary.Role;

import jakarta.validation.Constraint;
import junit.framework.TestCase;

public class ConstraintTest extends TestCase {
	
	public void testConstraintReference() throws SBOLGraphException, IOException
    {
		URI base=URI.create("https://synbiohub.org/public/igem/");
		SBOLDocument doc=new SBOLDocument(base);
		
		Component device= SBOLAPI.createDnaComponent(doc, "i13504", "i13504", "Screening plasmid intermediate", ComponentType.DNA.getUrl(), null);
		
		Component i13504_system=SBOLAPI.createComponent(doc,"i13504_system", ComponentType.FunctionalEntity.getUrl(), "i13504 system", null, Role.FunctionalCompartment);
		 
		Component ilab16_dev1=doc.createComponent("interlab16device1", Arrays.asList(ComponentType.DNA.getUrl())); 
		SubComponent i13504SubComponent=SBOLAPI.addSubComponent(i13504_system, device);
		SubComponent sc_i13504_system=SBOLAPI.addSubComponent(ilab16_dev1, i13504_system);	 	
		ComponentReference compRef_i13504_dev1=ilab16_dev1.createComponentReference(i13504SubComponent, sc_i13504_system);
		Component j23101=doc.createComponent("j23101", Arrays.asList(ComponentType.DNA.getUrl())); 
		SubComponent sc_j23101=SBOLAPI.addSubComponent(ilab16_dev1, j23101);	
			
		org.sbolstandard.core3.entity.Constraint constraint=ilab16_dev1.createConstraint(RestrictionType.Topology.meets, sc_j23101.getUri(), compRef_i13504_dev1.getUri());
		
	    TestUtil.serialise(doc, "entity_additional/constraint", "constraint");
      
        System.out.println(SBOLIO.write(doc, SBOLFormat.TURTLE));
        TestUtil.assertReadWrite(doc);
        
        TestUtil.validateIdentified(constraint,doc,0);
		constraint.setRestriction(null);
		TestUtil.validateIdentified(constraint,doc,1);
		constraint.setObject(null);
		TestUtil.validateIdentified(constraint,doc,2);
		constraint.setSubject(null);
		TestUtil.validateIdentified(constraint,doc,3);
    }

}
