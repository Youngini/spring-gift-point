package gift.service;

import gift.domain.Category;
import gift.domain.Option;
import gift.domain.Product;
import gift.dto.CreateProductDto;
import gift.dto.ProductListDto;
import gift.dto.ProductOptionDto;
import gift.dto.UpdateProductDto;
import gift.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductService(ProductRepository productRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    public Product createProduct(CreateProductDto productDto) {
        Product product = productDto.toProduct();
        productRepository.save(product);
        return product;
    }

    public Page<ProductListDto> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(ProductListDto::new);
    }

    public List<ProductOptionDto> getProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow();
        List<Option> options = product.getOptions();
        return options.stream().map(ProductOptionDto::new).collect(Collectors.toList());
    }

    public Product updateProduct(Long productId, UpdateProductDto productDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("해당 product가 존재하지 않습니다."));
        productDto.update(product);
        updateProductOptions(product,productDto.getOptions());
        productRepository.save(product);
        return product;
    }

    private void updateProductOptions(Product product, List<Option> updateOptions) {
        List<Option> originOptions = new ArrayList<>(product.getOptions());
        Map<Long, Option> originOptionsMap = originOptions.stream()
                .collect(Collectors.toMap(Option::getId, Function.identity()));

        for (Option optionDto : updateOptions) {
            Option originOption = originOptionsMap.get(optionDto.getId());
            if (originOption != null) {
                originOption.setName(optionDto.getName());
            } else {
                originOptions.add(new Option(optionDto.getName(), product));
            }
        }

        product.setOptions(originOptions);
    }


    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    public Page<ProductListDto> getProductsByCategory(Long categoryId, Pageable pageable) {
        Category category = categoryService.findById(categoryId);
        Page<Product> products = productRepository.findByCategory(category);
        return products.map(ProductListDto::new);
    }
}
