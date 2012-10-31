/*
 * Copyright: (c) 2012  Children's Hospital Boston, Regents of the University of Colorado 
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
 * 
 * @author Dmitriy Dligach
 */

package org.chboston.cnlp.ctakes.relationextractor.ae.features;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.cleartk.classifier.Feature;
import org.uimafit.util.JCasUtil;

import edu.mayo.bmi.uima.core.type.syntax.BaseToken;
import edu.mayo.bmi.uima.core.type.textsem.IdentifiedAnnotation;

/**
 * Features that indicate whether there is a preposition between the two arguments. 
 * Uses a predifined list of prepositions rather than POS tags.
 * 
 */
public class PrepositionFeatureExtractor implements RelationFeaturesExtractor {

  @Override
  public List<Feature> extract(JCas jCas, IdentifiedAnnotation arg1, IdentifiedAnnotation arg2) throws AnalysisEngineProcessException {

  	HashSet<String> prepositions = 
  			new HashSet<String>(Arrays.asList("about", "above", "across", "against", "amid", "around", "at", "atop", 
  					"behind", "below", "beneath", "beside", "between", "beyond", "by", "for", "from",
  					"down", "in", "including", "inside", "into", "mid", "near", "of", "off", "on", "onto", "opposite", "out",
  					"outside", "over", "round", "through", "throughout", "to", "under", "underneath", "with", "within", "without"));
  					
  	List<Feature> features = new ArrayList<Feature>();
  	
  	// entity1 ... entity2 scenario
  	if(arg1.getEnd() < arg2.getBegin()) {
  		for(BaseToken token : JCasUtil.selectCovered(jCas, BaseToken.class, arg1.getEnd(), arg2.getBegin())) {
  			if(prepositions.contains(token.getCoveredText())) {
  				features.add(new Feature("arg1_preposition_arg2", token.getCoveredText()));
  			}
  		}
  	}
  	
  	// entity2 ... entity1 scenario
  	if(arg2.getEnd() < arg1.getBegin()) {
  		for(BaseToken token : JCasUtil.selectCovered(jCas, BaseToken.class, arg2.getEnd(), arg1.getBegin())) {
  			if(prepositions.contains(token.getCoveredText())) {
  				features.add(new Feature("arg2_preposition_arg1", token.getCoveredText()));
  			}
  		}
  	}
  	
    return features;
  }

}