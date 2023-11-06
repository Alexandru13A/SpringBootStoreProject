package ro.store.admin.common.paging;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(PARAMETER)
public @interface PagingAndSortingParam {
    public String listName();
}
