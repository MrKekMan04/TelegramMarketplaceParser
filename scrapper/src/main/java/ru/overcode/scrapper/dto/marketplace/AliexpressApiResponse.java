package ru.overcode.scrapper.dto.marketplace;

import java.util.List;

public record AliexpressApiResponse(
        ProductData data
) {

    public record ProductData(
            String id,
            SkuInfo skuInfo
    ) {

        public record SkuInfo(
                List<Price> priceList
        ) {

            public record Price(
                    ActivityAmount activityAmount
            ) {

                public record ActivityAmount(
                        Long value
                ) {

                }
            }
        }
    }
}
