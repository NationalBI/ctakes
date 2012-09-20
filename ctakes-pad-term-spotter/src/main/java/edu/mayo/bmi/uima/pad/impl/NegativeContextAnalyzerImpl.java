/**
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
package edu.mayo.bmi.uima.pad.impl;

import java.util.List;
import java.util.Set;

import org.apache.uima.jcas.tcas.Annotation;

import edu.mayo.bmi.fsm.output.NegationIndicator;
import edu.mayo.bmi.fsm.pad.machine.NegDxIndicatorFSM;
import edu.mayo.bmi.fsm.token.TextToken;
import edu.mayo.bmi.uima.context.ContextAnalyzer;
import edu.mayo.bmi.uima.context.NamedEntityContextAnalyzer;
import edu.mayo.bmi.uima.context.ContextHit;

/**
 * @author Mayo Clinic
 */
public class NegativeContextAnalyzerImpl extends NamedEntityContextAnalyzer
        implements ContextAnalyzer
{
    private NegDxIndicatorFSM iv_negIndicatorFSM = new NegDxIndicatorFSM();

    public ContextHit analyzeContext(List<? extends Annotation> tokenList, int scope)
    {
        List<TextToken> fsmTokenList = wrapAsFsmTokens(tokenList);

        try
        {
            Set<NegationIndicator> s = iv_negIndicatorFSM.execute(fsmTokenList);

            if (s.size() > 0)
            {
                NegationIndicator neg = (NegationIndicator) s.iterator().next();
                return new ContextHit(neg.getStartOffset(), neg.getEndOffset());
            }
            else
            {
                return null;
            }
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
		return null;
    }
}