package ch.wisteca.anarchy.events;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation que doivent port�es toutes les m�thodes des objets qui �coutent des �venements.
 * @author Wisteca
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface EventListener {}
