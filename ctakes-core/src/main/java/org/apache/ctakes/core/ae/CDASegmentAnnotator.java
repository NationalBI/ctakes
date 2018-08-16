/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.ctakes.core.ae;

import org.apache.ctakes.core.pipeline.PipeBitInfo;
import org.apache.ctakes.core.resource.FileLocator;
import org.apache.ctakes.core.util.DocumentIDAnnotationUtil;
import org.apache.ctakes.typesystem.type.textspan.SectionHeading;
import org.apache.ctakes.typesystem.type.textspan.Segment;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates segment annotations based on the ccda_sections.txt file Which is
 * based on HL7/CCDA/LONIC standard headings Additional custom heading names can
 * be added to the file.
 */
@PipeBitInfo(
      name = "CCDA Sectionizer",
      description = "Annotates Document Sections by detecting Section Headers using Regular Expressions provided in a File.",
      dependencies = { PipeBitInfo.TypeProduct.DOCUMENT_ID },
      products = { PipeBitInfo.TypeProduct.SECTION }
)
public class CDASegmentAnnotator extends JCasAnnotator_ImplBase {

	Logger logger = Logger.getLogger(this.getClass());
	protected static HashMap<String, Pattern> patterns = new HashMap<>();
	protected static HashMap<String, String> section_names = new HashMap<>();
	protected static final String DEFAULT_SECTION_FILE_NAME = "org/apache/ctakes/core/sections/ccda_sections.txt";
	public static final String PARAM_FIELD_SEPERATOR = ",";
	public static final String PARAM_COMMENT = "#";
	public static final String SIMPLE_SEGMENT = "SIMPLE_SEGMENT";

  public static final String PARAM_SECTIONS_FILE = "sections_file";
	@ConfigurationParameter(name = PARAM_SECTIONS_FILE, 
	    description = "Path to File that contains the section header mappings", 
	    defaultValue=DEFAULT_SECTION_FILE_NAME,
	    mandatory=false)
	protected String sections_path;

	public static final String PARAM_SECTION_END_MARKERS = "section_end_markers";
	@ConfigurationParameter(name = PARAM_SECTION_END_MARKERS,
			description = "List of strings that can terminate a section",
			defaultValue = {},
			mandatory = false)
	protected LinkedList<String> section_end_markers = new LinkedList<>();

	/**
	 * Init and load the sections mapping file and precompile the regex matches
	 * into a hashmap
	 */
	@Override
  public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);

		try {
		  BufferedReader br = new BufferedReader(new InputStreamReader(
		      FileLocator.getAsStream(sections_path)));

		  // Read in the Section Mappings File
		  // And load the RegEx Patterns into a Map
		  logger.info("Reading Section File " + sections_path);
		  String line = null;
		  while ((line = br.readLine()) != null) {
		    if (!line.trim().startsWith(PARAM_COMMENT)) {
		      String[] l = line.split(PARAM_FIELD_SEPERATOR);
		      // First column is the HL7 section template id
		      if (l != null && l.length > 0 && l[0] != null
		          && l[0].length() > 0
		          && !line.endsWith(PARAM_FIELD_SEPERATOR)) {
		        String id = l[0].trim();
		        // Make a giant alternator (|) regex group for each HL7
		        Pattern p = buildPattern(l);
		        patterns.put(id, p);
		        if (l.length > 2 && l[2] != null) {
		          String temp = l[2].trim();
		          section_names.put(id, temp);
		        }						

		      } else {
		        logger.info("Warning: Skipped reading sections config row: "
		            + Arrays.toString(l));
		      }
		    }
		  }      
		} catch (IOException e) {
		  e.printStackTrace();
		  throw new ResourceInitializationException(e);
		}
	}

	/**
	 * Build a regex pattern from a list of section names. used only during init
	 * time
	 */
	private static Pattern buildPattern(String[] line) {
		StringBuffer sb = new StringBuffer();
		// Column 0 is the section ID; column 1 is the corresponding CDA code.
		// This annotator used to allow "<CDA code>:" or similar as a section
		// header, but we're not making use of that, so we skip directly to
		// column 2.
		for (int i = 2; i < line.length; i++) {
			// Build the RegEx pattern for each comma delimited header name
			// Suffixed with a aggregator pipe
			sb.append("[\\s\u2003]*" + line[i].trim() + "[ \t\u2003]*(?::[\\s\u2003]*|\r*\n)");
			if (i != line.length - 1) {
				sb.append("|");
			}
		}
		int patternFlags = 0;
		patternFlags |= Pattern.CASE_INSENSITIVE;
		patternFlags |= Pattern.DOTALL;
		patternFlags |= Pattern.MULTILINE;
		Pattern p = Pattern.compile("^(" + sb + ")", patternFlags);
		return p;
	}

	private final Segment createSegment(JCas jCas, int begin, int end, String id) {
		Segment segment = new Segment(jCas);
		segment.setBegin(begin);
		segment.setEnd(end);
		segment.setId(id);
		return segment;
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		String text = jCas.getDocumentText();
		int textLength = text.length();
		if (text == null) {
			String docId = DocumentIDAnnotationUtil.getDocumentID(jCas);
			logger.info("text is null for docId=" + docId);
		} else {
			ArrayList<Segment> sorted_segments = new ArrayList<>();
			for (String id : patterns.keySet()) {
				Pattern p = patterns.get(id);
				// System.out.println("Pattern" + p);
				Matcher m = p.matcher(text);
				while (m.find()) {
					Segment segment = createSegment(jCas, m.start(), m.end(), id);
					sorted_segments.add(segment);
				}
			}

			// If there are no segments, create a simple one that spans the
			// entire doc, and return.
			if (sorted_segments.size() <= 0) {
				Segment header = createSegment(jCas, 0, textLength, SIMPLE_SEGMENT);
				header.addToIndexes();
				return;
			}

			// The sections must be sorted because the end of each section is implied by the
			// beginning of the following section (or the end of the document).
			Collections.sort(sorted_segments, new Comparator<Segment>() {
				public int compare(Segment s1, Segment s2) {
					return s1.getBegin() - (s2.getBegin());
				}
			});
			int index = 0;
			int sorted_segments_size = sorted_segments.size();
			for (Segment s : sorted_segments) {
				int sectionHeadingBegin = s.getBegin();
				int sectionHeadingEnd = s.getEnd();
				int sectionBodyBegin = sectionHeadingEnd;
				int sectionBodyEnd;
				if (index < sorted_segments_size - 1) {
					sectionBodyEnd = sorted_segments.get(index + 1).getBegin();
					if (sectionBodyBegin > sectionBodyEnd) {
						// This can happen if the two section headings
						// overlap, e.g. if the regexes include whitespace and
						// we've got two headings next to each other with some
						// whitespace between.  Cap the sectionBodyBegin so
						// that the code below doesn't barf, but this is going
						// to all fall through and result in an empty section.
						sectionBodyBegin = sectionBodyEnd;
					}
				}
				else {
					sectionBodyEnd = textLength;
				}

				// Pull the section ends inwards to avoid any whitespace at either end.
				// This means that we'll just be tagging the actual text that we care about
				// and not the separating whitespace.
				// For the end of the body, also pull that in to avoid any of the section_end_markers.
				sectionHeadingBegin = skipWhitespaceAtBeginning(text, sectionHeadingBegin, sectionHeadingEnd);
				sectionHeadingEnd = skipWhitespaceAtEnd(text, sectionHeadingBegin, sectionHeadingEnd);
				sectionBodyBegin = skipWhitespaceAtBeginning(text, sectionBodyBegin, sectionBodyEnd);
				sectionBodyEnd = findTrueSectionEnd(text, sectionBodyBegin, sectionBodyEnd);

				// Sanity-check the body end.
				//
				// We used to skip sections with empty bodies entirely (i.e.
				// not even add the SectionHeading) but if we do that and
				// the document has multilevel headings (e.g. the equivalent
				// of <h1>Heading</h1><h2>Subheading</h2>) then we would skip
				// the first heading because there is no text between the
				// </h1> and the <h2>.  This annotator cannot detect
				// multilevel headings like this, since it has no
				// font/position info, so the best we can do is include the
				// <h1> as a blank section, and maybe the caller can infer
				// the structure later on.
				//
				// For the special case where the document text ends at a
				// heading, the blank body section needs to be placed at the
				// final character so that we don't create a segment that's
				// off the end of the document text.
				if (sectionBodyBegin >= textLength) {
					sectionBodyBegin = textLength - 1;
					sectionBodyEnd = textLength;
				}
				else if (sectionBodyEnd < sectionBodyBegin + 1) {
					sectionBodyEnd = sectionBodyBegin + 1;
				}

				String sId = s.getId();
				String preferredText = section_names.get(sId);

				Segment segment = createSegment(jCas, sectionBodyBegin, sectionBodyEnd, sId);
				segment.setPreferredText(preferredText);
				segment.addToIndexes();

				SectionHeading heading = new SectionHeading(jCas);
				heading.setBegin(sectionHeadingBegin);
				heading.setEnd(sectionHeadingEnd);
				heading.setId(sId);
				heading.setPreferredText(preferredText);
				heading.addToIndexes();

				index++;
			}
		}
	}

	private static int skipWhitespaceAtBeginning(String text, int begin, int end) {
		while (begin < end && Character.isWhitespace(text.charAt(begin))) {
			begin++;
		}
		return begin;
	}

	private static int skipWhitespaceAtEnd(String text, int begin, int end) {
		while (begin < end && Character.isWhitespace(text.charAt(end - 1))) {
			end--;
		}
		return end;
	}

	private int findTrueSectionEnd(String text, int begin, int end) {
		while (true) {
			int newEnd = findSectionEndMarker(text, begin, end);
			if (newEnd == -1) {
				return skipWhitespaceAtEnd(text, begin, end);
			}
			end = newEnd;
		}
	}

	private int findSectionEndMarker(String text, int begin, int end) {
		String sectionText = text.substring(begin, end);
		for (String indicator : section_end_markers) {
			int idx = sectionText.indexOf(indicator);
			if (idx != -1) {
				return begin + idx;
			}
		}
		return -1;
	}
}
