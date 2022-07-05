package org.sbolstandard.core3.entity.test;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.sbolstandard.core3.api.SBOLAPI;
import org.sbolstandard.core3.entity.*;
import org.sbolstandard.core3.io.SBOLFormat;
import org.sbolstandard.core3.io.SBOLIO;
import org.sbolstandard.core3.test.TestUtil;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.util.URINameSpace;
import org.sbolstandard.core3.vocabulary.*;
import junit.framework.TestCase;

public class AnnotationTest extends TestCase {
	
	public void testImplementation() throws SBOLGraphException, IOException
    {
		String baseUri="https://sbolstandard.org/examples/";
        SBOLDocument doc=new SBOLDocument(URI.create(baseUri));
        URINameSpace igem=new URINameSpace(URI.create("http://parts.igem.org/"), "igem");
        doc.addNameSpacePrefixes(igem);
        
        Component part=SBOLAPI.createComponent(doc, "BBa_J23119", ComponentType.DNA.getUri(), "BBa_J23119 part", "Parts J23100 through J23119 are a family of constitutive promoter parts isolated from a small combinatorial library.", Role.Promoter);
        part.addAnnotion(igem.local("group"), "iGEM2006_Berkeley");
        part.addAnnotion(igem.local("experienceURL"), URI.create("http://parts.igem.org/Part:BBa_J23119:Experience"));
        
        //Internal metadata
        Identified igemInf=part.createMetadata("information1", igem.local("Information"), igem.local("hasInformation"));
        igemInf.setName("BBa_J23119_experience");
        igemInf.setDescription("The experience page captures users' experience using the BBa_J23119 part");
        igemInf.addAnnotion(igem.local("sigmaFactor"), "//rnap/prokaryote/ecoli/sigma70");
        igemInf.addAnnotion(igem.local("regulation"), "//regulation/constitutive");
        igemInf.addAnnotion(igem.local("regulation"), "//regulation/second_regulation");
        
        Identified igemInf2=part.createMetadata("usage1", igem.local("IGEMUsage"), igem.local("hasUsage"));
        igemInf2.setName("BBa_J23119_usage");
        igemInf2.setDescription("BBa_J23119 usage statistics");
        igemInf2.addAnnotion(igem.local("inStock"), "true");
        igemInf2.addAnnotion(igem.local("registryStar"), "1");
        igemInf2.addAnnotion(igem.local("uses"), "442");
        igemInf2.addAnnotion(igem.local("twins"), "7");
     
        //Metadata within metadata
        Identified igemInfInternal=igemInf2.createMetadata("twinParts", igem.local("TwinPartUsage"), igem.local("twinURLs"));
        igemInfInternal.setName("twin parts");
        igemInfInternal.addAnnotion(igem.local("twinURL"), URI.create("http://parts.igem.org/wiki/index.php?title=Part:BBa_J72073"));
        igemInfInternal.addAnnotion(igem.local("twinURL"), URI.create("http://parts.igem.org/wiki/index.php?title=Part:BBa_M1638"));
        igemInfInternal.addAnnotion(igem.local("twinURL"), URI.create("http://parts.igem.org/wiki/index.php?title=Part:BBa_M36800"));

        //TopLevel metadata
        TopLevel igemInf4=doc.createMetadata("iGEMRepository", igem.local("Repository"));
        igemInf4.setName("iGEM Registry");
        igemInf4.setDescription("Registry of Standard Biological Parts");
        igemInf4.addAnnotion(igem.local("website"), URI.create("http://parts.igem.org/Main_Page"));
        
        TopLevel igemInf5=doc.createMetadata("SynBioHubRepository", igem.local("Repository"));
        igemInf5.setName("SynBioHub");
        
        part.addAnnotion(igem.local("belongsTo"), igemInf5);
        part.addAnnotion(igem.local("belongsTo"), igemInf4);
        
        TestUtil.serialise(doc, "entity/annotation", "annotation");
   
        String output=SBOLIO.write(doc, SBOLFormat.TURTLE);
        System.out.println(output);
        SBOLDocument doc2=SBOLIO.read(output, SBOLFormat.TURTLE); 
            
        output=SBOLIO.write(doc2, SBOLFormat.TURTLE);
        System.out.println(output);
         
        printMetadata(doc2.getComponents().get(0));
        
        TestUtil.assertReadWrite(doc);
    }
	
	public void printMetadata(Identified identified) throws SBOLGraphException
	{
		 URINameSpace igem=new URINameSpace(URI.create("http://parts.igem.org/"), "igem");  
		 System.out.println("group:" + identified.getAnnotion(igem.local("group")));
		 System.out.println("experienceURL:" + identified.getAnnotion(igem.local("experienceURL")));
		 List<Object> informationMetadata=identified.getAnnotion(igem.local("hasInformation"));
		 if (informationMetadata!=null)
		 {
			 Metadata metadata=(Metadata) informationMetadata.get(0);
			 System.out.println("hasInformation");
			 printMetadata(metadata,3);
			 System.out.println("   SigmaFactor:" + metadata.getAnnotion(igem.local("sigmaFactor")));
			 System.out.println("   Regulation:" + metadata.getAnnotion(igem.local("regulation")));
		 }
		 List<Object> usage=identified.getAnnotion(igem.local("hasUsage"));
		 if (usage!=null)
		 {
			 Metadata metadata=(Metadata) usage.get(0);
			 System.out.println("hasUsage");
			 printMetadata(metadata,3);
			 System.out.println("   Uses:" + metadata.getAnnotion(igem.local("uses")));
			 System.out.println("   Twins:" + metadata.getAnnotion(igem.local("twins")));
			 List<Object> twinsURLs=metadata.getAnnotion(igem.local("twinURLs"));
			 Metadata twinPartUsageMetadata=(Metadata) twinsURLs.get(0);
			 System.out.println("   TwinPartUsage:");
			 printMetadata(metadata,6);
			 System.out.println("      twinURL:" + twinPartUsageMetadata.getAnnotion(igem.local("twinURL")));
		 }
		 
		 List<Object> repositoryMetadataList=identified.getAnnotion(igem.local("belongsTo"));
		 System.out.println("belongsTo:");
		 if (repositoryMetadataList!=null)
		 {
			 for (Object o: repositoryMetadataList)
			 {
				 Identified repositoryMetadata=(Identified) o;
				 System.out.println("  " + repositoryMetadata.getName());
				 
			 }
		 }
	}

	 private void printMetadata(Metadata metadata, int count) throws SBOLGraphException
	 {
		 String space=String.format("%"+count+"s", "") ;
		 System.out.println(space + "uri:" + metadata.getUri());
		 System.out.println(space + "name:" + metadata.getName());
		 System.out.println(space + "desc:" + metadata.getDescription());
		 System.out.println(space + "displayId:" + metadata.getDisplayId());
		 
	 }
	/*
	public class IgemInformation extends Metadata
	{
		public IgemInformation (SBOLDocument doc, URI uri) throws SBOLGraphException
		{
			super(doc, uri);
		}
		
		@Override
		public URI getResourceType() {
			return URI.create("http://parts.igem.org/IgemInformation");
		}
	}*/

}
