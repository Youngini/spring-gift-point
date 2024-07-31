package gift.dto;

import gift.domain.Product;

public class ProductListDto {
    public Long product_id;
    public String name;
    public Integer price;
    public String imageUrl;
    public Long category_id;

    public ProductListDto(Product product) {
        this.product_id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.imageUrl = product.getImageUrl();
        this.category_id = product.getCategory().getId();
    }
}
