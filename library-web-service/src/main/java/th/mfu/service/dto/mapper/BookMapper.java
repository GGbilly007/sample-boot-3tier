package th.mfu.service.dto.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import th.mfu.domain.Book;
import th.mfu.service.dto.BookDTO;

/**
 * The ASSEMBLER for Book.
 *
 * A DTO must be independent of the domain object, so something has to copy
 * between them. That something is the Assembler - and MapStruct writes it for
 * us.
 *
 * You write the interface; MapStruct writes the class AT COMPILE TIME. After a
 * build, go and read what it produced:
 *
 *   library-web-service/target/generated-sources/annotations/
 *       th/mfu/service/dto/mapper/BookMapperImpl.java
 *
 * No reflection, no runtime magic - just generated getters and setters.
 *
 * componentModel = "spring" makes the generated class a @Component, so it can
 * be @Autowired into a controller.
 */
@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    void updateBookFromEntity(Book entity, @MappingTarget BookDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateBookFromDto(BookDTO dto, @MappingTarget Book entity);
}
