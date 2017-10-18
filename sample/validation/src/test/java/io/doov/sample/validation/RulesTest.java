package io.doov.sample.validation;

import static io.doov.sample.validation.AccountRulesId.VALID_COUNTRY;
import static io.doov.sample.validation.AccountRulesId.VALID_EMAIL;
import static io.doov.sample.validation.AccountRulesId.VALID_EMAIL_SIZE;
import static io.doov.sample.validation.Rules.REGISTRY_ACCOUNT;
import static io.doov.sample.validation.Rules.REGISTRY_USER;
import static io.doov.sample.validation.UserRulesId.VALID_ADULT;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.junit.Before;
import org.junit.Test;

import io.doov.core.FieldModel;
import io.doov.core.dsl.lang.*;
import io.doov.core.dsl.meta.Readable;
import io.doov.sample.model.*;

public class RulesTest {

    private FieldModel wrapper;
    private Account account;
    private User user;

    @Before
    public void before() {
        SampleModel sample = SampleModels.sample();
        wrapper = new SampleModelWrapper(sample);
        account = sample.getAccount();
        user = sample.getUser();
    }

    @Test
    public void test_valid_email() {
        assertThat(executeOn(REGISTRY_ACCOUNT, VALID_EMAIL).isValid()).isTrue();

        account.setEmail("test@test.gh");
        Result actual = executeOn(REGISTRY_ACCOUNT, VALID_EMAIL);
        assertThat(actual.isValid()).isFalse();
        assertThat(actual.getFailedNodes()).hasSize(3);
        assertThat(actual.getFailedNodes().stream().map(Readable::readable).collect(toList()))
                        .contains("account email matches '\\w+[@]\\w+\\.com'",
                                        "account email matches '\\w+[@]\\w+\\.fr'",
                                        "(account email matches '\\w+[@]\\w+\\.com' or " +
                                                        "account email matches '\\w+[@]\\w+\\.fr')");
    }

    @Test
    public void test_valid_email_size() {
        assertThat(executeOn(REGISTRY_ACCOUNT, VALID_EMAIL_SIZE).isValid()).isTrue();

        account.setMaxEmailSize(5);
        assertThat(executeOn(REGISTRY_ACCOUNT, VALID_EMAIL_SIZE).isValid()).isFalse();
    }

    @Test
    public void test_valid_country() {
        assertThat(executeOn(REGISTRY_ACCOUNT, VALID_COUNTRY).isValid()).isTrue();

        account.setPhoneNumber("+336123456789");
        assertThat(executeOn(REGISTRY_ACCOUNT, VALID_COUNTRY).isValid()).isFalse();
    }

    @Test
    public void test_valid_adult() {
        assertThat(executeOn(REGISTRY_USER, VALID_ADULT).isValid()).isTrue();

        user.setBirthDate(LocalDate.now());
        assertThat(executeOn(REGISTRY_USER, VALID_ADULT).isValid()).isFalse();
    }

    @Test
    public void test_all_account_rules_invalid_messages() {
        List<Result> messages = REGISTRY_ACCOUNT.stream()
                        .map(rule -> rule.executeOn(wrapper))
                        .collect(toList());
        assertThat(messages).isNotEmpty();
        assertThat(messages).extracting(Result::isValid).allMatch(Boolean::booleanValue);
        assertThat(messages).extracting(Result::getMessage).allMatch(Objects::isNull);
    }

    @Test
    public void print_all_account_rules() {
        concat(REGISTRY_USER.stream(), REGISTRY_ACCOUNT.stream())
                        .map(ValidationRule::readable)
                        .forEach(System.out::println);
    }

    private Result executeOn(RuleRegistry registry, RuleId id) {
        return registry.get(id).map(rule -> rule.executeOn(wrapper)).orElseThrow(AssertionError::new);
    }

}