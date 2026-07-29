package th.mfu.service.dto.mapper;

import org.mapstruct.Mapper;

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

    // TODO: (step 3) Declare the two directions.
    //
    // (a) entity -> DTO, for sending a book out. The two @Mapping lines flatten
    //     the relationship: the entity's category.id and category.name become
    //     two plain fields on the DTO.
    //
    //     @Mapping(source = "category.id",   target = "categoryId")
    //     @Mapping(source = "category.name", target = "categoryName")
    //     void updateBookFromEntity(Book entity, @MappingTarget BookDTO dto);
    //
    // (b) DTO -> entity, for a create and for the PARTIAL UPDATE in step 5.
    //     This is the important one:
    //
    //     @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    //     @Mapping(target = "id",       ignore = true)
    //     @Mapping(target = "category", ignore = true)
    //     void updateBookFromDto(BookDTO dto, @MappingTarget Book entity);
    //
    //     IGNORE means: if a field of the DTO is null, DO NOT TOUCH the entity.
    //     That single setting is the whole partial-update feature.
    //
    //     id and category are ignored because the controller decides those -
    //     the id comes from the URL, and the category must be looked up as a
    //     real row before it can be attached.
    //
    // Imports you will need:
    //   org.mapstruct.BeanMapping, Mapping, MappingTarget,
    //   org.mapstruct.NullValuePropertyMappingStrategy
    //   th.mfu.domain.Book, th.mfu.service.dto.BookDTO
}
