package org.apache.ctakes.core.util;

import org.apache.ctakes.typesystem.type.refsem.OntologyConcept;
import org.apache.ctakes.typesystem.type.refsem.UmlsConcept;
import org.apache.ctakes.typesystem.type.textsem.IdentifiedAnnotation;
import org.apache.log4j.Logger;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author SPF , chip-nlp
 * @version %I%
 * @since 1/8/2015
 */
final public class OntologyConceptUtil {

   static private final Logger LOGGER = Logger.getLogger( "IdentifiedAnnotationUtil" );

   static private final FeatureStructure[] EMPTY_FEATURE_ARRAY = new FeatureStructure[ 0 ];

   private OntologyConceptUtil() {
   }

   // placed in a predicate so that we can use negate()
//   static private final Predicate<FeatureStructure> isUmlsConcept = UmlsConcept.class::isInstance;

   static private final Predicate<OntologyConcept> isSchemeOk
         = concept -> concept.getCodingScheme() != null && !concept.getCodingScheme().isEmpty();

   static private final Predicate<OntologyConcept> isCodeOk
         = concept -> concept.getCode() != null && !concept.getCode().isEmpty();

   static private final Function<OntologyConcept, Collection<String>> getCodeAsSet
         = concept -> new HashSet<>( Collections.singletonList( concept.getCode() ) );

   static private final BinaryOperator<Collection<String>> mergeSets
         = ( set1, set2 ) -> {
      set1.addAll( set2 );
      return set1;
   };

   static private final Function<IdentifiedAnnotation, Stream<String>> flattenCuis
         = annotation -> getCuis( annotation ).stream();

   static private final Function<IdentifiedAnnotation, Stream<String>> flattenTuis
         = annotation -> getTuis( annotation ).stream();

   static private final Function<Map<String, Collection<String>>, Stream<Map.Entry<String, Collection<String>>>>
         flattenSchemeCodes
         = map -> map.entrySet().stream();

   /**
    * @param annotation -
    * @return array of FeatureStructure castable to array of OntologyConcept
    */
   static private FeatureStructure[] getOntologyConcepts( final IdentifiedAnnotation annotation ) {
      if ( annotation == null ) {
         return EMPTY_FEATURE_ARRAY;
      }
      final FSArray ontologyConcepts = annotation.getOntologyConceptArr();
      if ( ontologyConcepts == null ) {
         return EMPTY_FEATURE_ARRAY;
      }
      return ontologyConcepts.toArray();
   }

   /**
    * @param annotation -
    * @return set of all Umls Concepts associated with the annotation
    */
   static public Collection<UmlsConcept> getConcepts( final IdentifiedAnnotation annotation ) {
      return Arrays.stream( getOntologyConcepts( annotation ) )
            .filter( UmlsConcept.class::isInstance )
            .map( fs -> (UmlsConcept)fs )
            .collect( Collectors.toSet() );
   }


   //
   //   Get cuis, tuis, or codes for a single IdentifiedAnnotation
   //

   /**
    * @param annotation -
    * @return set of all Umls cuis associated with the annotation
    */
   static public Collection<String> getCuis( final IdentifiedAnnotation annotation ) {
      return getConcepts( annotation )
            .stream()
            .map( UmlsConcept::getCui )
            .collect( Collectors.toSet() );
   }

   /**
    * @param annotation -
    * @return set of all Umls tuis associated with the annotation
    */
   static public Collection<String> getTuis( final IdentifiedAnnotation annotation ) {
      return getConcepts( annotation )
            .stream()
            .map( UmlsConcept::getTui )
            .collect( Collectors.toSet() );
   }

   /**
    * @param annotation -
    * @return map of ontology scheme names to a set of ontology codes associated each scheme
    */
   static public Map<String, Collection<String>> getSchemeCodes( final IdentifiedAnnotation annotation ) {
      return Arrays.stream( getOntologyConcepts( annotation ) )
            .filter( OntologyConcept.class::isInstance )
//            .filter( isUmlsConcept.negate() )
            .map( fs -> (OntologyConcept)fs )
            .filter( isSchemeOk )
            .filter( isCodeOk )
            .collect( Collectors.toMap( OntologyConcept::getCodingScheme, getCodeAsSet, mergeSets ) );
   }

   /**
    * @param annotation -
    * @return set of ontology codes associated with all schemes
    */
   static public Collection<String> getCodes( final IdentifiedAnnotation annotation ) {
      return Arrays.stream( getOntologyConcepts( annotation ) )
            .filter( OntologyConcept.class::isInstance )
//            .filter( isUmlsConcept.negate() )
            .map( fs -> (OntologyConcept)fs )
            .filter( isSchemeOk )
            .filter( isCodeOk )
            .map( OntologyConcept::getCode )
            .collect( Collectors.toSet() );
   }

   /**
    * @param annotation -
    * @param schemeName name of the scheme of interest
    * @return set of ontology codes associated the named scheme
    */
   static public Collection<String> getCodes( final IdentifiedAnnotation annotation,
                                              final String schemeName ) {
      return Arrays.stream( getOntologyConcepts( annotation ) )
            .filter( OntologyConcept.class::isInstance )
//            .filter( isUmlsConcept.negate() )
            .map( fs -> (OntologyConcept)fs )
            .filter( concept -> schemeName.equalsIgnoreCase( concept.getCodingScheme() ) )
            .filter( isCodeOk )
            .map( OntologyConcept::getCode )
            .collect( Collectors.toSet() );
   }


   //
   //   Get cuis, tuis, or codes for all IdentifiedAnnotations in a jcas
   //

   /**
    * @param jcas -
    * @return set of all cuis in jcas
    */
   static public Collection<String> getCuis( final JCas jcas ) {
      return JCasUtil.select( jcas, IdentifiedAnnotation.class )
            .stream()
            .flatMap( flattenCuis )
            .collect( Collectors.toSet() );
   }

   /**
    * @param jcas -
    * @return set of all tuis in jcas
    */
   static public Collection<String> getTuis( final JCas jcas ) {
      return JCasUtil.select( jcas, IdentifiedAnnotation.class )
            .stream()
            .flatMap( flattenTuis )
            .collect( Collectors.toSet() );
   }

   /**
    * @param jcas -
    * @return set of all tuis in jcas
    */
   static public Map<String, Collection<String>> getSchemeCodes( final JCas jcas ) {
      return JCasUtil.select( jcas, IdentifiedAnnotation.class )
            .stream()
            .map( OntologyConceptUtil::getSchemeCodes )
            .flatMap( flattenSchemeCodes )
            .collect( Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue, mergeSets ) );
   }

   /**
    * @param jcas -
    * @return set of all tuis in jcas
    */
   static public Collection<String> getCodes( final JCas jcas ) {
      return JCasUtil.select( jcas, IdentifiedAnnotation.class )
            .stream()
            .map( OntologyConceptUtil::getCodes )
            .flatMap( Collection::stream )
            .collect( Collectors.toSet() );
   }

   /**
    * @param jcas       -
    * @param schemeName name of the scheme of interest
    * @return set of ontology codes associated the named scheme
    */
   static public Collection<String> getCodes( final JCas jcas,
                                              final String schemeName ) {
      return JCasUtil.select( jcas, IdentifiedAnnotation.class )
            .stream()
            .map( annotation -> getCodes( annotation, schemeName ) )
            .flatMap( Collection::stream )
            .collect( Collectors.toSet() );
   }


   //
   //   Get all IdentifiedAnnotations in jcas with given cui, tui, or code
   //

   /**
    * @param jcas -
    * @param cui  cui of interest
    * @return all IdentifiedAnnotations that have the given cui
    */
   static public Collection<IdentifiedAnnotation> getAnnotationsByCui( final JCas jcas,
                                                                       final String cui ) {
      return JCasUtil.select( jcas, IdentifiedAnnotation.class )
            .stream()
            .filter( annotation -> getCuis( annotation ).contains( cui ) )
            .collect( Collectors.toSet() );
   }

   /**
    * @param jcas -
    * @param tui  tui of interest
    * @return all IdentifiedAnnotations that have the given tui
    */
   static public Collection<IdentifiedAnnotation> getAnnotationsByTui( final JCas jcas,
                                                                       final String tui ) {
      return JCasUtil.select( jcas, IdentifiedAnnotation.class )
            .stream()
            .filter( annotation -> getTuis( annotation ).contains( tui ) )
            .collect( Collectors.toSet() );
   }

   /**
    * @param jcas -
    * @param code code of interest
    * @return all IdentifiedAnnotations that have the given code
    */
   static public Collection<IdentifiedAnnotation> getAnnotationsByCode( final JCas jcas,
                                                                        final String code ) {
      return JCasUtil.select( jcas, IdentifiedAnnotation.class )
            .stream()
            .filter( annotation -> getCodes( annotation ).contains( code ) )
            .collect( Collectors.toSet() );
   }


}