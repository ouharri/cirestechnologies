package com.ouharri.cirestechnologies.model.dto.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * DTO (Data Transfer Object) representing a summary of JSON file import.
 * Contains information about the total number of records, the number of records successfully imported
 * and the number of records failed to import.
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadSummaryResponseDTO {
    /**
     * The total number of records in the JSON file.
     */
    private int totalRecords;

    /**
     * The number of records successfully imported into the database.
     */
    private int successfullyImported;

    /**
     * The number of records that failed to be imported into the database.
     */
    private int failedToImport;
}