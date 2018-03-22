package it.nexsoft.parser;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;

public class PdfParser extends Parser {

	public PdfParser(File file) {
		super(file);
	}

	@Override
	public String parseAttachment() throws InvalidPasswordException, IOException {

		String sRet = null;
		
		PDDocument document = PDDocument.load(file);
		
		PDFTextStripper stripper = new PDFTextStripper();
		
		String documentText = stripper.getText(document);
		
		//Pattern pattern = Pattern.compile("[\\w.]+@[\\w.]+");
		Pattern pattern = Pattern.compile(emailRegex);
		Matcher matcher = pattern.matcher(documentText);
		
		if(matcher.find()) {
			sRet = matcher.group();
		}
		
		document.close();
		
		return sRet;
	}

}
