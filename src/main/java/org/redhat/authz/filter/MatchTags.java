package org.redhat.authz.filter;

import javax.enterprise.util.Nonbinding;
import javax.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface MatchTags {
    @Nonbinding String value() default "";
}
