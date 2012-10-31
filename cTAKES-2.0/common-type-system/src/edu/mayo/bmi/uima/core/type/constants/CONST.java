/*
 * Copyright: (c) 2009   Mayo Foundation for Medical Education and 
 * Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
 * triple-shield Mayo logo are trademarks and service marks of MFMER.
 *
 * Except as contained in the copyright notice above, or as used to identify 
 * MFMER as the author of this software, the trade names, trademarks, service
 * marks, or product names of the copyright holder shall not be used in
 * advertising, promotion or otherwise in connection with this software without
 * prior written authorization of the copyright holder.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package edu.mayo.bmi.uima.core.type.constants;
/**
 * Contains a set of constants for feature values.
 * 
 * @author Mayo
 */
public class CONST {

	public static final int NE_TYPE_ID_UNKNOWN = 0;
	public static final int NE_TYPE_ID_DRUG = 1;
	public static final int NE_TYPE_ID_DISORDER = 2;
	public static final int NE_TYPE_ID_FINDING = 3;

	public static final int NE_TYPE_ID_PROCEDURE = 5;
	public static final int NE_TYPE_ID_ANATOMICAL_SITE = 6;

	// entity types used for mipacq
	public static final int NE_TYPE_ID_DISEASE_DISORDER = 101;
	public static final int NE_TYPE_ID_ACTIVITIES_AND_BEHAVIORS = 102;
	public static final int NE_TYPE_ID_ORGANIZATIONS = 103;
	public static final int NE_TYPE_ID_OBJECTS = 104;
	public static final int NE_TYPE_ID_DEVICES = 105;
	public static final int NE_TYPE_ID_PHYSIOLOGY = 106;
	public static final int NE_TYPE_ID_GEOGRAPHIC_AREAS = 107;
	public static final int NE_TYPE_ID_PHENOMENA = 108;

	public static final int NE_TYPE_ID_MEDICATIONS_DRUGS = 110;
	public static final int NE_TYPE_ID_EVENT_UMLS = 111;
	public static final int NE_TYPE_ID_CONCEPTS_AND_IDEAS = 112;
	public static final int NE_TYPE_ID_LIVING_BEINGS = 113;
	public static final int NE_TYPE_ID_CLASSIFICATION = 114;
	public static final int NE_TYPE_ID_SIGN_SYMPTOM = 115;
	public static final int NE_TYPE_ID_TIMEX3 = 116;
	public static final int NE_TYPE_ID_LANGUAGE = 117;
	public static final int NE_TYPE_ID_OCCUPATIONS = 118;
	public static final int NE_TYPE_ID_LABORATORY_OR_TEST_PROCEDURE = 119;

	public static final int NE_TYPE_ID_GENES_AND_MOLECULAR_SEQUENCES = 121;
	public static final int NE_TYPE_ID_PERSON = 122;
	
	
	public static final int NE_DISCOVERY_TECH_DICT_LOOKUP = 1;
	public static final int NE_DISCOVERY_TECH_GOLD_ANNOTATION = 2;
	
	public static final int NE_CERTAINTY_POSITIVE = 1;
	public static final int NE_CERTAINTY_NEGATED = -1;	
	public static final int NE_DIAG_STATUS_CONFIRMED = 0;
	public static final int NE_DIAG_STATUS_HISTORY_OF = 1;
	public static final int NE_DIAG_STATUS_FAM_HISTORY_OF = 2;
	public static final int NE_DIAG_STATUS_PROBABLE = 3;

	public static final int REL_DISCOVERY_TECH_GOLD_ANNOTATION = 1;

	public static final String MED_STATUS_CHANGE_START = "start";
	public static final String MED_STATUS_CHANGE_STOP = "stop";
	public static final String MED_STATUS_CHANGE_INCREASEFROM = "increasefrom";
	public static final String MED_STATUS_CHANGE_DECREASEFROM = "decreasefrom";
	public static final String MED_STATUS_CHANGE_INCREASE = "increase";
	public static final String MED_STATUS_CHANGE_DECREASE = "decrease";
	public static final String MED_STATUS_CHANGE_NOCHANGE = "noChange";
	public static final String MED_STATUS_CHANGE_SUM = "add";
	public static final String MED_STATUS_CHANGE_MAX = "maximum";
	public static final String MED_STATUS_CHANGE_OTHER = "change";
	
}