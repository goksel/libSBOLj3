package org.sbolstandard.core3.validation;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.lang3.StringUtils;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.io.SBOLIO;
import org.sbolstandard.core3.util.SBOLGraphException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class SBOLValidator {

	//private static SBOLValidator sbolValidator = null;
	protected Validator validator;
	
	private SBOLValidator() throws SBOLGraphException
	{	
		ValidatorFactory factory = Validation.byDefaultProvider()
 	            .configure()
 	            //.addValueExtractor(new ...ValueExtractor())
 	            .buildValidatorFactory();
		this.setValidator(factory.getValidator());	
	}
	
	private void setValidator(Validator validator) throws SBOLGraphException	
	{
		if (validator==null)
		{
			throw new SBOLGraphException("Unable to create a Validator");
		}
			
		this.validator=validator;
	}
	
	/*public static SBOLValidator getValidator() throws SBOLGraphException
	{
		if (sbolValidator == null)
		{
			try
			{
				sbolValidator=new SBOLValidator();
				ValidatorFactory factory = Validation.byDefaultProvider()
		 	            .configure()
		 	            //.addValueExtractor(new ...ValueExtractor())
		 	            .buildValidatorFactory();
				sbolValidator.setValidator(factory.getValidator());	
			}
			catch (Exception exception)
			{
				throw new SBOLGraphException("Could not initialize the validator", exception);
			}
		}
		return sbolValidator;
	}*/
	
	public static SBOLValidator getValidator() throws SBOLGraphException {
		return SingletonHelper.INSTANCE;
	}

	private static class SingletonHelper {
        private static final SBOLValidator INSTANCE ;
        static {
            try {
                INSTANCE = new SBOLValidator();
            } catch (SBOLGraphException e) {
                throw new ExceptionInInitializerError(e);
            }
        }
    }

	
	public List<String> validate(SBOLDocument document)
	{
	    Set<ConstraintViolation<SBOLDocument>> violations = validator.validate(document);
	    List<String> messages=new ArrayList<String>();
	    for (ConstraintViolation<SBOLDocument> violation : violations) {
	    	/*List<String> fragments=new ArrayList<String>();
	    	fragments.add(violation.getMessage());
	    	fragments.add(String.format("Property: %s",violation.getPropertyPath().toString()));
	    	if (violation.getLeafBean()!=null && violation.getLeafBean() instanceof Identified ){
	    	    Identified identified= (Identified) violation.getLeafBean();
	    	    fragments.add(String.format("Entity URI: %s",identified.getUri().toString()));
	    	    fragments.add(String.format("Entity type: %s",identified.getClass()));    
	    	}
	    	if (violation.getInvalidValue()!=null && !(violation.getInvalidValue() instanceof Identified)){
	    		fragments.add("Value:" + violation.getInvalidValue().toString());
	    	}
	    	String message=StringUtils.join(fragments, String.format(",%s\t", System.lineSeparator()));*/
	    	messages.add(PropertyValidator.getViolotionMessage(violation));
	    }
	    return messages;
	}
	
	public boolean isValid(SBOLDocument document) throws SBOLGraphException
	{
		String CRLF=System.lineSeparator(); 	
		List<String> messages=validate(document);
		if (messages!=null && messages.size()>0)
		{
			String message=StringUtils.join(messages, CRLF + CRLF);
			throw new SBOLGraphException("Could not validate the SBOL document:" + CRLF + message);
		}
		return true; 
	}
	
	public static List<String> validateSBOLDocument2(SBOLDocument document)
	{
		Validator validator=null; 
		ValidatorFactory factory = Validation.byDefaultProvider()
 	            .configure()
 	            //.addValueExtractor(new ProfileValueExtractor())
 	            .buildValidatorFactory();
 	        validator = factory.getValidator();
    
 	       Set<ConstraintViolation<SBOLDocument>> violations = validator.validate(document);
 	      List<String> messages=new ArrayList<String>();
 	      for (ConstraintViolation<SBOLDocument> violation : violations) {
 	    	    String propertyMessage=String.format("Property:%s",violation.getPropertyPath().toString());
 	    	    //String entityMessage =String.format("Entity Type:%s",violation.getRootBeanClass().getName());
 	    	    String uriMessage="";
 	    	    if (violation.getLeafBean()!=null)
 	    	    {
 	    	    	URI uri=((Identified) violation.getLeafBean()).getUri();
 	    	    	uriMessage =String.format("Entity:%s",uri.toString());
 	    	    }
 	    	 	
 	    	    String message=String.format("%s, %s, %s", violation.getMessage(), propertyMessage, uriMessage);
 	    	    messages.add(message);
 	    	}
 	       return messages;
	}
	
	public static String validateFolder(String folder, String extension, String keywordsToExclude) throws IOException, SBOLGraphException
	{
		StringBuilder output=new StringBuilder();
		validateFolderRecursive(output, new File(folder), extension, keywordsToExclude);
		String result=output.toString();
		return result;
	}
	
	public static String validateFolder(String folder) throws IOException, SBOLGraphException
	{
		return validateFolder(folder,null,null);
	}

	private static void validateFolderRecursive(StringBuilder output, File folder, String extension, String keywordsToExclude) throws IOException, SBOLGraphException
	{
		File[] files=folder.listFiles();
		for (int i=0;i<files.length;i++)
		{
			File file=files[i];
			if (file.isDirectory())
			{
				validateFolderRecursive(output, file, extension, keywordsToExclude);
			}
			else
			{
				boolean validate=true;
				if (!file.getPath().toLowerCase().contains(keywordsToExclude))
				{
					if (extension!=null)
					{
						String ext=FileNameUtils.getExtension(file.getName());
						if (!ext.equalsIgnoreCase(extension))
						{
							validate=false;
						}
					}
					if (validate)
					{
						validateFile(output, file);
					}
				}
			}
		}
	}
	
	
	public static void validateFile(StringBuilder output, File file) throws IOException, SBOLGraphException
	{ 
		try {
			SBOLIO.read(file);
			/*String folder=SBOLAPI.class.getClassLoader().getResource("validation").getFile();
			String[] files=new File(folder).list();
			
			//while (validationFiles.hasMoreElements())
			for (int i=0;i<files.length;i++)
			{
				//String resourceFile=validationFiles.nextElement().getFile();
				String resourceFile="validation/" + files[i];
				
				String sparqlQuery=loadQuery(resourceFile);
				String queryName= new File(resourceFile).getName();
				
				validateFileWithQuery(output, doc, sparqlQuery, file, queryName);
			}*/
		}
		catch (SBOLGraphException e)
		{
			throw new SBOLGraphException(e.getMessage() +  System.lineSeparator()  + "File:" + file.getPath(), null);
		}
		
		//String sparqlDisplayId=loadQuery("validation/displayid.sparql");
		//validateFileWithQuery(output, doc, sparqlDisplayId, file, "displayid");
	}
	
	/*private static void validateFileWithQuery(StringBuilder output, SBOLDocument doc, String query, File file, String queryName)
	{
		ResultSet rs=RDFUtil.executeSPARQLSelectQuery(doc.getRDFModel(), query, Syntax.syntaxSPARQL_11);
		while (rs.hasNext())
		{
			QuerySolution qs=rs.next();
			RDFNode identified=qs.get(rs.getResultVars().get(0));
			RDFNode value=qs.get(rs.getResultVars().get(1));
			
			String identifiedLiteral=RDFUtil.toLiteralString(identified);
			String valueLiteral=RDFUtil.toLiteralString(value);
			
			String message=String.format("Validation failed for %s - %s. Identified: %s, value: '%s'", file.getName(), queryName, identifiedLiteral, valueLiteral);
			
			output.append(message);
			output.append(System.lineSeparator());
		}
	
	}*/
	//   FILTER (!regex(?displayId, "^[a-zA-Z_]+[a-zA-Z0-9_]*$")) .
	   
	//   FILTER (!regex(?displayId, "^[a-zA-Z0-9_]*$")) .
	/*public static String toLiteralString(RDFNode node)
	{
		if (node.isLiteral())
		{
			return node.asLiteral().getValue().toString();
		}
		else
		{
			return node.asResource().getURI();
		}
	}	*/
	
	/*private static String loadQuery(String queryFile) throws IOException
	{
		try
		{
			File file = new File(SBOLAPI.class.getClassLoader().getResource(queryFile).getFile());
			return IOUtils.toString(new FileInputStream(file), Charset.defaultCharset());
		}
		catch (Exception e)
		{
			throw new IOException("Could not load the sparql file " + queryFile,e);
		}
	}*/
	
	

}
