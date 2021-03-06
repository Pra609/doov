/*
 * Copyright (C) by Courtanet, All Rights Reserved.
 */
package io.doov.sample.validation;

import static io.doov.core.dsl.DOOV.map;
import static io.doov.core.dsl.DOOV.mapNull;
import static io.doov.core.dsl.DOOV.mappings;
import static io.doov.core.dsl.DOOV.when;
import static io.doov.core.dsl.mapping.TypeConverters.biConverter;
import static io.doov.core.dsl.mapping.TypeConverters.converter;
import static io.doov.sample.field.SampleTag.ACCOUNT;
import static io.doov.sample.field.dsl.DslSampleModel.*;
import static io.doov.sample3.field.dsl.DslEmployee.*;
import static java.util.Arrays.asList;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Locale;

import io.doov.core.dsl.lang.MappingRule;

public class EmployeeMapping {

    public static final MappingRule FULLNAME_MAPPING = map(userFirstName, userLastName)
                    .using(biConverter((firstName, lastName) -> firstName + " " + lastName, "", "combine names"))
                    .to(employeeFullname);

    public static final MappingRule EMAIL_MAPPING = when(accountAcceptEmail.isTrue())
                    .then(map(accountEmail).to(employeeEmail));

    public static final MappingRule AGE_MAPPING = map(userBirthdate.ageAt(LocalDate.of(2019, 1, 1))).to(employeeAge);

    public static final MappingRule COUNTRY_MAPPING = map(accountCountry)
                    .using(converter(c -> c.name(), "country not available", "country name")).to(employeeCountry);

    public static final MappingRule COMPANY_MAPPING = map(accountCompany)
                    .using(converter(c -> c.name(), "company not available", "company name")).to(employeeCompany);
    
    public static final MappingRule MAPPING_ACCOUNT_NULL = mapNull(ACCOUNT);

    public static final MappingRule ALL_MAPPINGS = mappings(EMAIL_MAPPING, FULLNAME_MAPPING, AGE_MAPPING,
                    COUNTRY_MAPPING, COMPANY_MAPPING);

    public static void main(String[] args) throws IOException {
        SampleWriter.of("employee_mapping_rules.html", Locale.FRANCE)
                        .write(asList(EMAIL_MAPPING, FULLNAME_MAPPING, AGE_MAPPING, COUNTRY_MAPPING, COMPANY_MAPPING));
        System.exit(1);
    }

}
