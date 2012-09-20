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

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.mayo.bmi.uima.core.type.textsem.IdentifiedAnnotation;
import edu.mayo.bmi.uima.context.ContextHitConsumer;
import edu.mayo.bmi.uima.context.NamedEntityContextHitConsumer;
import edu.mayo.bmi.uima.context.ContextHit;

/**
 * @author Mayo Clinic
 */
public class NegativeContextHitConsumerImpl extends NamedEntityContextHitConsumer implements
	ContextHitConsumer
{
	public void consumeHit(JCas jcas, Annotation focusAnnot, int scope,
			ContextHit ctxHit)
	{
		if (focusAnnot instanceof IdentifiedAnnotation)
		{
			IdentifiedAnnotation neAnnot = (IdentifiedAnnotation) focusAnnot;
			if (neAnnot.getTypeID() != 7 /*&& neAnnot.getTypeID() != 2*/ )
				neAnnot.setPolarity(-1);
		}

		createContextAnnot(jcas, focusAnnot, scope, ctxHit).addToIndexes();
	}
}
