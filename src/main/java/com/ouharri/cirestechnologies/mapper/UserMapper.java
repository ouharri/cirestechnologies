package com.ouharri.cirestechnologies.mapper;

import com.ouharri.cirestechnologies.model.dto.requests.UserRequest;
import com.ouharri.cirestechnologies.model.dto.responses.UserResponses;
import com.ouharri.cirestechnologies.model.entities.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

/**
 * Mapper interface for converting between {@link UserRequest}, {@link UserResponses}, and {@link User} entities.
 * Extends the generic {@link _Mapper} interface with UUID as the identifier type.
 */
@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface UserMapper extends _Mapper<UUID, UserRequest, UserResponses, User> {
}