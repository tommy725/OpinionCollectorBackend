package pl.opinion_collector.backend.logic.product.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * Product Wrapper dto class
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductWrapperDto {
    @ApiModelProperty(notes = "list of products", dataType = "pl.opinion_collector.backend.logic.product.controller.dto.ProductWideDto", required = true)
    private List<ProductExtendDto> products;
    @ApiModelProperty(notes = "actualPage", example = "0", required = true)
    private int actualPage;
    @ApiModelProperty(notes = "number Of Pages", example = "2", required = true)
    private int numberOfPages;
}
