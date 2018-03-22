package it.nexsoft.parser;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

public class CustomParserFactory {
	
	public static Parser getParser(File file) {
		
		Parser parser = null;
		String attachmentExtension = FilenameUtils.getExtension(file.getName());
		
		switch (attachmentExtension) {
		case "pdf":
			parser = new PdfParser(file);
			break;
		case "doc":
		case "docx":
			parser = new DocParser(file);
			break;
		case "odt":
		default:
			break;
		}
		
		return parser;
	}
}
