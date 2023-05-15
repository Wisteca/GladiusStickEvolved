package ch.wisteca.anarchy.events;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation que doivent portées toutes les méthodes des objets qui écoutent des évenements.
 * @author Wisteca
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface EventListener {}
