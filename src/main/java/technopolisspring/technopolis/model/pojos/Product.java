package technopolisspring.technopolis.model.pojos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import technopolisspring.technopolis.model.dto.CreateProductDto;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class Product implements IProduct {

    private long id;
    private String description;
    private double price;
    private String pictureUrl;
    private long brandId;
    private long subCategoryId;
    private long offerId;

    public Product(long id, String description, double price, String pictureUrl, long brandId, long subCategoryId, long offerId) {
        this.id = id;
        this.description = description;
        this.price = price;
        this.pictureUrl = pictureUrl;
        this.brandId = brandId;
        this.subCategoryId = subCategoryId;
        this.offerId = offerId;
    }

    public Product (CreateProductDto createProductDto){
        this.description = createProductDto.getDescription();
        this.brandId = createProductDto.getBrandId();
        this.subCategoryId = createProductDto.getSubCategoryId();
        this.price = createProductDto.getPrice();
        this.pictureUrl = createProductDto.getPictureUrl();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return  description;
    }

}
