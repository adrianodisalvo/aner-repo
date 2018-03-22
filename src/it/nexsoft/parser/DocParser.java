package it.nexsoft.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

public class DocParser extends Parser {

	public DocParser(File file) {
		super(file);
	}

	@Override
	public String parseAttachment() throws IOException {
		
		String sRet = null;

		FileInputStream fis = new FileInputStream(file.getAbsolutePath());
		HWPFDocument document = new HWPFDocument(fis);
		WordExtractor extractor = new WordExtractor(document);
		
		String[] fileData = extractor.getParagraphText();
		
		for (int i = 0; i < fileData.length; i++) {
			
			if (fileData[i] != null) {
				
				String currentParagraph = WordExtractor.stripFields(fileData[i]);
				//Pattern pattern = Pattern.compile("[\\w.]+@[\\w.]+");
				Pattern pattern = Pattern.compile(emailRegex);
				Matcher matcher = pattern.matcher(currentParagraph);
				
				if(matcher.find()) {
					sRet = matcher.group();
					break;
				}
			}
		}
		
		extractor.close();
		return sRet;
	}

}
