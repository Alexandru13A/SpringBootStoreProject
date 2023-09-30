package ro.store.admin.common.paging;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PagingAndSortingArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterAnnotation(PagingAndSortingParam.class) != null;
  }

  @Override
  @Nullable
  public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer model,
      NativeWebRequest request, @Nullable WebDataBinderFactory binderFactory) throws Exception {

    String sortOrder = request.getParameter("sortOrder");
    String sortField = request.getParameter("sortField");
    String keyword = request.getParameter("keyword");

    String reverseSortOrder = sortOrder.equals("asc") ? "desc" : "asc";

    model.addAttribute("sortField", sortField);
    model.addAttribute("sortOrder", sortOrder);
    model.addAttribute("keyword", keyword);
    model.addAttribute("reverseSortOrder", reverseSortOrder);

    PagingAndSortingParam annotation = parameter.getParameterAnnotation(PagingAndSortingParam.class);

    return new PagingAndSortingHelper(model, annotation.listName(),
        sortField, sortOrder, keyword);

  }

}