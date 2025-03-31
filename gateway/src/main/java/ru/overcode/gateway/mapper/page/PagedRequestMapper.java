package ru.overcode.gateway.mapper.page;

import org.mapstruct.Mapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.overcode.gateway.config.mapper.MappersConfig;
import ru.overcode.gateway.dto.page.PagedRequest;

@Mapper(config = MappersConfig.class)
public interface PagedRequestMapper {

    default Pageable toPageable(PagedRequest request) {
        PageRequest pageable = PageRequest.of(request.page(), request.items());
        return request.sort() != null
                ? pageable.withSort(Sort.by(request.sort().order(), request.sort().column()))
                : pageable;
    }
}
