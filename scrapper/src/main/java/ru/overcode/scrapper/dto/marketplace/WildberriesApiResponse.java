package ru.overcode.scrapper.dto.marketplace;

import java.util.List;

public record WildberriesApiResponse(
        DataPayload data
) {

    public record DataPayload(
            List<Product> products
    ) {

        public record Product(
                Long id,
                List<Size> sizes
        ) {

            public record Size(
                    Price price
            ) {

                public record Price(
                        Long total
                ) {

                }
            }
        }
    }
}
