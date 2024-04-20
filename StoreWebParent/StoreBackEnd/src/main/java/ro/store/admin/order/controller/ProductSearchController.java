package ro.store.admin.order.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import ro.store.admin.common.paging.PagingAndSortingHelper;
import ro.store.admin.common.paging.PagingAndSortingParam;
import ro.store.admin.product.ProductService;

@Controller
public class ProductSearchController {

    private ProductService productService;

    public ProductSearchController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/orders/search_product")
    public String showSearchProductPage() {
        return "orders/search_product";
    }

    @PostMapping("/orders/search_product")
	public String searchProducts(String keyword) {
		return "redirect:/orders/search_product/page/1?sortField=name&sortDir=asc&keyword=" + keyword;
	}

    @GetMapping("/orders/search_product/page/{pageNum}")
    public String searchProductsByPage(
            @PagingAndSortingParam(listName = "products", moduleURL = "/orders/search_product") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") int pageNum) {
        productService.searchProducts(pageNum, helper);
        return "orders/search_product";
    }

}
