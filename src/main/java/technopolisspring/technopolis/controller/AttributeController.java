package technopolisspring.technopolis.controller;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import technopolisspring.technopolis.exception.BadRequestException;
import technopolisspring.technopolis.exception.InvalidArgumentsException;
import technopolisspring.technopolis.model.daos.AttributeDao;
import technopolisspring.technopolis.model.daos.ProductDao;
import technopolisspring.technopolis.model.dto.AddAttributeToProductDto;
import technopolisspring.technopolis.model.dto.AttributeWithoutValueDto;
import technopolisspring.technopolis.model.pojos.Attribute;
import technopolisspring.technopolis.model.pojos.IProduct;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class AttributeController extends AbstractController {

    private static final String INVALID_ATTRIBUTE_OR_PRODUCT = "Invalid attribute or product";
    private static final String SUB_CATEGORIES_MISMATCH = "Sub-category of product has to match the one of the attribute";
    private static final String ALREADY_EXISTS = "Such attribute already exists";
    @Autowired
    AttributeDao attributeDao;
    @Autowired
    ProductDao productDao;

    @SneakyThrows
    @PostMapping("products/{productId}/attributes")
     public Attribute addAttributeToProduct(@RequestBody AddAttributeToProductDto attributeToAdd,
                                                           HttpSession session,
                                                           @PathVariable long productId) {
        checkIfUserIsAdmin(session);
        IProduct product = productDao.getProductById(productId);
        if(product == null){
            throw new BadRequestException(ProductController.INVALID_PRODUCT);
        }
        // check if it exists as an attribute or in the many to many if any is false return null
        AttributeWithoutValueDto attributeWithoutValueDto = attributeDao.getAttributeById(attributeToAdd.getId());
        if (attributeWithoutValueDto == null){
            throw new BadRequestException(INVALID_ATTRIBUTE_OR_PRODUCT);
        }
        if (product.getSubCategoryId() != attributeWithoutValueDto.getSubCategoryId()){
            throw new InvalidArgumentsException(SUB_CATEGORIES_MISMATCH);
        }
        attributeDao.addAttributeToProduct(attributeToAdd, productId);
        return new Attribute(
                attributeWithoutValueDto.getId(),
                attributeWithoutValueDto.getName(),
                attributeWithoutValueDto.getSubCategoryId(),
                attributeToAdd.getValue()
        );
     }

    @SneakyThrows
    @GetMapping("attributes/page/{pageNumber}")
    public List<AttributeWithoutValueDto> getAllAttributes(HttpSession session, @PathVariable int pageNumber) {
        checkIfUserIsAdmin(session);
        return attributeDao.getAllAttributes(validatePageNumber(pageNumber));
    }

    @SneakyThrows
    @DeleteMapping("attributes/{attributeId}")
    public String deleteAttribute(@PathVariable long attributeId, HttpSession session) {
        checkIfUserIsAdmin(session);
        if(!attributeDao.deleteAttribute(attributeId)){
            throw new BadRequestException(INVALID_ATTRIBUTE_OR_PRODUCT);
        }
        return ProductController.SUCCESS;
    }

    @SneakyThrows
    @PostMapping("attributes")
    public AttributeWithoutValueDto addAttribute(@RequestBody AttributeWithoutValueDto attribute,
                                                 HttpSession session){
        checkIfUserIsAdmin(session);
        AttributeWithoutValueDto checkAttribute = attributeDao.getAttributeByName(attribute.getName());
        if (checkAttribute != null){
            throw new BadRequestException(ALREADY_EXISTS);
        }
        if (checkAttribute.getName().equals(attribute.getName())){
            throw new BadRequestException(ALREADY_EXISTS);
        }
        attributeDao.addAttribute(attribute);
        return attribute;
    }

    @SneakyThrows
    @DeleteMapping("products/{productId}/attributes/{attributeId}")
    public String removeAttributeFromProduct(@PathVariable long attributeId,
                                             HttpSession session,
                                             @PathVariable long productId) {
        checkIfUserIsAdmin(session);
        if (!validationUtil.validId(attributeId)){
            throw new BadRequestException(INVALID_ATTRIBUTE_OR_PRODUCT);
        }
        if (!validationUtil.validId(productId)){
            throw new BadRequestException(INVALID_ATTRIBUTE_OR_PRODUCT);
        }
        if(!attributeDao.removeAttributeFromProduct(attributeId, productId)){
            throw new BadRequestException(INVALID_ATTRIBUTE_OR_PRODUCT);
        }
        return ProductController.SUCCESS;
    }

}
